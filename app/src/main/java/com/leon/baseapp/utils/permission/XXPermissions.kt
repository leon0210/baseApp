package com.leon.baseapp.utils.permission

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.util.*

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2018/06/15
 * desc   : Android 危险权限请求类
 */
class XXPermissions private constructor(private val mActivity: AppCompatActivity) {
    private var mPermissions: MutableList<String> = mutableListOf()
    private var mIsContinue = false

    /**
     * 设置权限组
     */
    fun permission(vararg permissions: String): XXPermissions {
        mPermissions.addAll(listOf(*permissions))
        return this
    }

    /**
     * 设置权限组
     */
    fun permission(vararg permissions: Array<String>): XXPermissions {
        for (group in permissions) {
            mPermissions.addAll(listOf(*group))
        }
        return this
    }

    /**
     * 被拒绝后继续申请，直到授权或者永久拒绝
     */
    fun constantRequest(): XXPermissions {
        mIsContinue = true
        return this
    }

    /**
     * 请求权限
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun request(callback: OnPermission?) {
        // 如果没有指定请求的权限，就使用清单注册的权限进行请求
        if (mPermissions.isEmpty()) {
            PermissionUtils.getManifestPermissions(mActivity).let { mPermissions.addAll(it!!) }
        }
        require(mPermissions.isNotEmpty()) { "The requested permission cannot be empty" }
//        requireNotNull(mActivity) { "The activity is empty" }
        check(!mActivity.isDestroyed) { "The event has been destroyed" }
        check(!mActivity.isFinishing) { "The event has been finish" }
        requireNotNull(callback) { "The permission request callback interface must be implemented" }
        //检查targetSdkVersion是否符合要求
//        PermissionUtils.checkTargetSdkVersion(mActivity, mPermissions)
        val failPermissions = PermissionUtils.getFailPermissions(mActivity, mPermissions)
        if (failPermissions.isEmpty()) {
            // 为空证明权限已经全部授予过
            callback.hasPermission(mPermissions, true)
        } else {
            // 检测权限有没有在清单文件中注册
            PermissionUtils.checkPermissions(mActivity, mPermissions)
            // 申请没有授予过的权限
            PermissionFragment.newInstance(ArrayList(mPermissions), mIsContinue)
                .prepareRequest(mActivity, callback)
        }
    }

    companion object {
        /**
         * 设置请求的对象
         */
        fun with(activity: AppCompatActivity): XXPermissions {
            return XXPermissions(activity)
        }

        /**
         * 检查某些权限是否全部授予了
         *
         * @param permissions 需要请求的权限组
         */
        fun hasPermission(context: Context?, vararg permissions: String): Boolean {
            return if (permissions.isEmpty()) {
                hasPermission(context, PermissionUtils.getManifestPermissions(context!!))
            } else {
                hasPermission(context, listOf(*permissions) as? MutableList<String>)
            }
        }

        private fun hasPermission(context: Context?, permissions: MutableList<String>?): Boolean {
            val failPermissions = PermissionUtils.getFailPermissions(context!!, permissions)
            return failPermissions.isEmpty()
        }

        /**
         * 检查某些权限是否全部授予了
         *
         * @param permissions 需要请求的权限组
         */
        @Suppress("UNCHECKED_CAST")
        fun hasPermission(context: Context?, vararg permissions: Array<String>): Boolean {
            val permissionList: MutableList<String>? = mutableListOf()
            for (group in permissions) {
                permissionList?.addAll(group.toList())
            }
            val failPermissions = PermissionUtils.getFailPermissions(context!!, permissionList)
            return failPermissions.isEmpty()
        }

        /**
         * 跳转到应用权限设置页面
         */
        fun startPermissionActivity(context: Context?) {
            context?.let { PermissionSettingPage.start(it, false) }
        }

        /**
         * 跳转到应用权限设置页面
         *
         * @param newTask       是否使用新的任务栈启动
         */
        fun startPermissionActivity(context: Context?, newTask: Boolean) {
            context?.let { PermissionSettingPage.start(it, newTask) }
        }
    }
}