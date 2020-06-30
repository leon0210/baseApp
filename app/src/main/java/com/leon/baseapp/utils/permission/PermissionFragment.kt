package com.leon.baseapp.utils.permission

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.SparseArray
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.leon.baseapp.utils.permission.PermissionUtils.checkMorePermissionPermanentDenied
import com.leon.baseapp.utils.permission.PermissionUtils.getFailPermissions
import com.leon.baseapp.utils.permission.PermissionUtils.getSucceedPermissions
import com.leon.baseapp.utils.permission.PermissionUtils.isHasInstallPermission
import com.leon.baseapp.utils.permission.PermissionUtils.isHasOverlaysPermission
import com.leon.baseapp.utils.permission.PermissionUtils.isOverMarshmallow
import com.leon.baseapp.utils.permission.PermissionUtils.isOverOreo
import com.leon.baseapp.utils.permission.PermissionUtils.isRequestDeniedPermission
import java.lang.ref.SoftReference
import java.util.*

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2018/06/15
 * desc   : 权限请求处理类
 */
class PermissionFragment : Fragment(), Runnable {
    /**
     * 准备请求
     */
    fun prepareRequest(activity: AppCompatActivity, callback: OnPermission) {
        // 将当前的请求码和对象添加到集合中
        arguments?.getInt(REQUEST_CODE)?.let {
            CALLBACKS.put(it, SoftReference(callback))
        }
        activity.supportFragmentManager.beginTransaction().add(this, activity.javaClass.name).commitAllowingStateLoss()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val permissions =
            arguments?.getStringArrayList(PERMISSION_GROUP) ?: return
        @RequiresApi(Build.VERSION_CODES.O)
        if (permissions.contains(Permission.REQUEST_INSTALL_PACKAGES) && !isHasInstallPermission(activity?.applicationContext!!)) {
            // 跳转到允许安装未知来源设置页面
            val intent = Intent(
                Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                Uri.parse("package:" + context?.packageName)
            )
            startActivityForResult(intent, arguments?.getInt(REQUEST_CODE)!!)
            return
        }
        @RequiresApi(Build.VERSION_CODES.M)
        if (permissions.contains(Permission.SYSTEM_ALERT_WINDOW) && !isHasOverlaysPermission(activity)) {
            // 跳转到悬浮窗设置页面
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context?.packageName)
            )
            startActivityForResult(intent, arguments?.getInt(REQUEST_CODE)!!)
            return
        }
        // 当前必须没有跳转到悬浮窗或者安装权限界面
        requestPermission()
    }

    /**
     * 请求权限
     */
    private fun requestPermission() {
        if (isOverMarshmallow) {
            val permissions =
                arguments?.getStringArrayList(PERMISSION_GROUP)
            if (permissions != null && permissions.size > 0) {
                arguments?.getInt(REQUEST_CODE)?.let {
                    requestPermissions(
                        permissions.toTypedArray(),
                        it
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        val reference = CALLBACKS[requestCode] ?: return
        val callback = reference.get() ?: return
        // 根据请求码取出的对象为空，就直接返回不处理
        for (i in permissions.indices) {
            // 重新检查安装权限
            @RequiresApi(Build.VERSION_CODES.O)
            if (Permission.REQUEST_INSTALL_PACKAGES == permissions[i]) {
                if (isHasInstallPermission(activity?.applicationContext!!)) {
                    grantResults[i] = PackageManager.PERMISSION_GRANTED
                } else {
                    grantResults[i] = PackageManager.PERMISSION_DENIED
                }
            }

            // 重新检查悬浮窗权限
            @RequiresApi(Build.VERSION_CODES.M)
            if (Permission.SYSTEM_ALERT_WINDOW == permissions[i]) {
                if (isHasOverlaysPermission(activity)) {
                    grantResults[i] = PackageManager.PERMISSION_GRANTED
                } else {
                    grantResults[i] = PackageManager.PERMISSION_DENIED
                }
            }

            // 重新检查8.0的两个新权限
            @RequiresApi(Build.VERSION_CODES.O)
            if (Permission.ANSWER_PHONE_CALLS == permissions[i] || Permission.READ_PHONE_NUMBERS == permissions[i]) {
                // 检查当前的安卓版本是否符合要求
                if (!isOverOreo) {
                    grantResults[i] = PackageManager.PERMISSION_GRANTED
                }
            }
        }

        // 获取授予权限
        val succeedPermissions: List<String?> = getSucceedPermissions(permissions, grantResults)
        // 如果请求成功的权限集合大小和请求的数组一样大时证明权限已经全部授予
        if (succeedPermissions.size == permissions.size) {
            // 代表申请的所有的权限都授予了
            callback.hasPermission(succeedPermissions, true)
        } else {
            // 获取拒绝权限
            val failPermissions: List<String> = getFailPermissions(permissions, grantResults)
            // 检查是否开启了继续申请模式，如果是则检查没有授予的权限是否还能继续申请
            if (arguments?.getBoolean(REQUEST_CONSTANT)!!
                && isRequestDeniedPermission(activity!!, failPermissions)
            ) {
                // 如果有的话就继续申请权限，直到用户授权或者永久拒绝
                requestPermission()
                return
            }

            // 代表申请的权限中有不同意授予的，如果有某个权限被永久拒绝就返回true给开发人员，让开发者引导用户去设置界面开启权限
            callback.noPermission(failPermissions, checkMorePermissionPermanentDenied(activity!!, failPermissions))
            // 证明还有一部分权限被成功授予，回调成功接口
            if (succeedPermissions.isNotEmpty()) {
                callback.hasPermission(succeedPermissions, false)
            }
        }

        // 权限回调结束后要删除集合中的对象，避免重复请求
        CALLBACKS.remove(requestCode)
        fragmentManager!!.beginTransaction().remove(this).commit()
    }

    /** 是否已经回调了，避免安装权限和悬浮窗同时请求导致的重复回调  */
    private var mCallback = false

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!mCallback && requestCode == arguments?.getInt(REQUEST_CODE)) {
            mCallback = true
            // 需要延迟执行，不然有些华为机型授权了但是获取不到权限
            HANDLER.postDelayed(this, 500)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun run() {
        // 如果用户离开太久，会导致 Activity 被回收掉，所以这里要判断当前 Fragment 是否有被添加到 Activity（可在开发者模式中开启不保留活动复现崩溃的 Bug）
        if (isAdded) {
            // 请求其他危险权限
            requestPermission()
        }
    }

    companion object {
        /** 全局的 Handler 对象  */
        private val HANDLER = Handler(Looper.getMainLooper())

        /** 请求的权限  */
        private const val PERMISSION_GROUP = "permission_group"

        /** 请求码（自动生成）  */
        private const val REQUEST_CODE = "request_code"

        /** 是否不断请求  */
        private const val REQUEST_CONSTANT = "request_constant"

        /** 回调对象存放  */
        private val CALLBACKS = SparseArray<SoftReference<OnPermission>?>()

        fun newInstance(permissions: ArrayList<String?>?, constant: Boolean): PermissionFragment {
            val fragment = PermissionFragment()
            val bundle = Bundle()
            var requestCode: Int
            // 请求码随机生成，避免随机产生之前的请求码，必须进行循环判断
            do {
                // Studio编译的APK请求码必须小于 65536
                // Eclipse编译的APK请求码必须小于 256
                requestCode = Random().nextInt(255)
            } while (CALLBACKS[requestCode] != null)
            with(bundle) {
                putInt(REQUEST_CODE, requestCode)
                putStringArrayList(PERMISSION_GROUP, permissions)
                putBoolean(REQUEST_CONSTANT, constant)
                fragment.arguments = this
                return fragment
            }
        }
    }
}