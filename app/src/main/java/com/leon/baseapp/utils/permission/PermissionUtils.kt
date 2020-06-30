package com.leon.baseapp.utils.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import java.util.*

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2018/06/15
 * desc   : 权限请求工具类
 */
internal object PermissionUtils {
    /**
     * 是否是 6.0 以上版本
     */
    val isOverMarshmallow: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    /**
     * 是否是 8.0 以上版本
     */
    val isOverOreo: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    /**
     * 返回应用程序在清单文件中注册的权限
     */
    fun getManifestPermissions(context: Context): MutableList<String>? {
        return try {
            Arrays.asList(
                *context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_PERMISSIONS
                ).requestedPermissions
            )
        } catch (ignored: PackageManager.NameNotFoundException) {
            mutableListOf()
        }
    }

    /**
     * 是否有安装权限
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun isHasInstallPermission(context: Context): Boolean {
        return if (isOverOreo) {
            context.packageManager.canRequestPackageInstalls()
        } else true
    }

    /**
     * 是否有悬浮窗权限
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun isHasOverlaysPermission(context: Context?): Boolean {
        return if (isOverMarshmallow) {
            Settings.canDrawOverlays(context)
        } else true
    }

    /**
     * 获取没有授予的权限
     *
     * @param context               上下文对象
     * @param permissions           需要请求的权限组
     */
    fun getFailPermissions(
        context: Context,
        permissions: MutableList<String>?
    ): MutableList<String> {
        // 如果是安卓6.0以下版本就返回空
        if (!isOverMarshmallow || permissions == null) {
            return mutableListOf()
        }
        val failPermissions: MutableList<String> = mutableListOf()
        for (permission in permissions) {
            // 检测安装权限
            @RequiresApi(Build.VERSION_CODES.O)
            if (Permission.REQUEST_INSTALL_PACKAGES == permission) {
                if (!isHasInstallPermission(context)) {
                    failPermissions.add(permission)
                }
                continue
            }

            // 检测悬浮窗权限
            @RequiresApi(Build.VERSION_CODES.M)
            if (Permission.SYSTEM_ALERT_WINDOW == permission) {
                if (!isHasOverlaysPermission(context)) {
                    failPermissions.add(permission)
                }
                continue
            }

            // 检测8.0的两个新权限
            @RequiresApi(Build.VERSION_CODES.O)
            if (Permission.ANSWER_PHONE_CALLS == permission || Permission.READ_PHONE_NUMBERS == permission) {
                // 检查当前的安卓版本是否符合要求
                if (!isOverOreo) {
                    continue
                }
            }

            // 把没有授予过的权限加入到集合中
            @RequiresApi(Build.VERSION_CODES.M)
            if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                failPermissions.add(permission)
            }
        }
        return failPermissions
    }

    /**
     * 是否还能继续申请没有授予的权限
     *
     * @param activity              Activity对象
     * @param failPermissions       失败的权限
     */
    fun isRequestDeniedPermission(activity: Activity, failPermissions: List<String>): Boolean {
        for (permission in failPermissions) {
            // 安装权限和浮窗权限不算，本身申请方式和危险权限申请方式不同，因为没有永久拒绝的选项，所以这里返回false
            @RequiresApi(Build.VERSION_CODES.O)
            if (Permission.REQUEST_INSTALL_PACKAGES == permission || Permission.SYSTEM_ALERT_WINDOW == permission) {
                continue
            }

            // 检查是否还有权限还能继续申请的（这里指没有被授予的权限但是也没有被永久拒绝的）
            @RequiresApi(Build.VERSION_CODES.O)
            if (!checkSinglePermissionPermanentDenied(activity, permission)) {
                return true
            }
        }
        return false
    }

    /**
     * 在权限组中检查是否有某个权限是否被永久拒绝
     *
     * @param activity              Activity对象
     * @param permissions            请求的权限
     */

    fun checkMorePermissionPermanentDenied(activity: Activity, permissions: List<String>): Boolean {
        @RequiresApi(Build.VERSION_CODES.O)
        for (permission in permissions) {
            // 安装权限和浮窗权限不算，本身申请方式和危险权限申请方式不同，因为没有永久拒绝的选项，所以这里返回false
            if (Permission.REQUEST_INSTALL_PACKAGES == permission || Permission.SYSTEM_ALERT_WINDOW == permission) {
                continue
            }
            if (checkSinglePermissionPermanentDenied(activity, permission)) {
                return true
            }
        }
        return false
    }

    /**
     * 检查某个权限是否被永久拒绝
     *
     * @param activity              Activity对象
     * @param permission            请求的权限
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkSinglePermissionPermanentDenied(activity: Activity, permission: String): Boolean {

//        // 安装权限和浮窗权限不算，本身申请方式和危险权限申请方式不同，因为没有永久拒绝的选项，所以这里返回false
//        if (Permission.REQUEST_INSTALL_PACKAGES.equals(permission) || Permission.SYSTEM_ALERT_WINDOW.equals(permission)) {
//            return false;
//        }

        // 检测8.0的两个新权限
        if (Permission.ANSWER_PHONE_CALLS == permission || Permission.READ_PHONE_NUMBERS == permission) {

            // 检查当前的安卓版本是否符合要求
            if (!isOverOreo) {
                return false
            }
        }
        if (isOverMarshmallow) {
            if (activity.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED &&
                !activity.shouldShowRequestPermissionRationale(permission)
            ) {
                return true
            }
        }
        return false
    }

    /**
     * 获取没有授予的权限
     *
     * @param permissions           需要请求的权限组
     * @param grantResults          允许结果组
     */
    fun getFailPermissions(
        permissions: Array<String>,
        grantResults: IntArray
    ): List<String> {
        val failPermissions: MutableList<String> = ArrayList()
        for (i in grantResults.indices) {

            // 把没有授予过的权限加入到集合中，-1表示没有授予，0表示已经授予
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                failPermissions.add(permissions[i])
            }
        }
        return failPermissions
    }

    /**
     * 获取已授予的权限
     *
     * @param permissions       需要请求的权限组
     * @param grantResults      允许结果组
     */
    fun getSucceedPermissions(
        permissions: Array<String>,
        grantResults: IntArray
    ): List<String> {
        val succeedPermissions: MutableList<String> = ArrayList()
        for (i in grantResults.indices) {
            // 把授予过的权限加入到集合中，-1表示没有授予，0表示已经授予
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                succeedPermissions.add(permissions[i])
            }
        }
        return succeedPermissions
    }

    /**
     * 检测权限有没有在清单文件中注册
     *
     * @param activity              Activity对象
     * @param requestPermissions    请求的权限组
     */
    fun checkPermissions(activity: Activity, requestPermissions: MutableList<String>?) {
        val manifestPermissions = getManifestPermissions(activity)
        if (manifestPermissions != null && manifestPermissions.isNotEmpty()) {
            if (requestPermissions != null) {
                for (permission in requestPermissions) {
                    if (!manifestPermissions.contains(permission)) {
                        throw ManifestException(permission)
                    }
                }
            }
        } else {
            throw ManifestException()
        }
    }

    /**
     * 检查targetSdkVersion 是否符合要求
     *
     * @param context                   上下文对象
     * @param requestPermissions        请求的权限组
     */
    fun checkTargetSdkVersion(context: Context, requestPermissions: List<String?>) {
        // 检查是否包含了8.0的权限
        @RequiresApi(Build.VERSION_CODES.O)
        if (requestPermissions.contains(Permission.REQUEST_INSTALL_PACKAGES)
            || requestPermissions.contains(Permission.ANSWER_PHONE_CALLS)
            || requestPermissions.contains(Permission.READ_PHONE_NUMBERS)
        ) {
            // 必须设置 targetSdkVersion >= 26 才能正常检测权限
            if (context.applicationInfo.targetSdkVersion < Build.VERSION_CODES.O) {
                throw RuntimeException("The targetSdkVersion SDK must be 26 or more")
            }
        } else {
            // 必须设置 targetSdkVersion >= 23 才能正常检测权限
            if (context.applicationInfo.targetSdkVersion < Build.VERSION_CODES.M) {
                throw RuntimeException("The targetSdkVersion SDK must be 23 or more")
            }
        }
    }
}