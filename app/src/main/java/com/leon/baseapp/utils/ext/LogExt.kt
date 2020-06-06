package com.leon.baseapp.utils.ext

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import com.leon.baseapp.BuildConfig
import com.leon.baseapp.base.BaseApplication

/**
 * Log 打印
 */

fun Any.logi(msg: String) {
    if (BuildConfig.DEBUG)
        Log.i(this.javaClass.simpleName, msg)
}

fun Any.logd(msg: String) {
    if (BuildConfig.DEBUG)
        Log.d(this.javaClass.simpleName, msg)
}

fun Any.loge(msg: String) {
    if (BuildConfig.DEBUG)
        Log.e(this.javaClass.simpleName, msg)
}

// 下面是传入自定义tag的函数
fun logi(tag: String, msg: String) {
    if (BuildConfig.DEBUG)
        Log.i(tag, msg)
}

fun logd(tag: String, msg: String) {
    if (BuildConfig.DEBUG)
        Log.d(tag, msg)
}

fun loge(tag: String, msg: String) {
    if (BuildConfig.DEBUG)
        Log.e(tag, msg)
}

/**
 * 吐司
 */
object ToastUtil {
    private var toast: Toast? = null

    @SuppressLint("ToastUtil.showToast", "ShowToast")
    fun showToast(string: String?, duration: Int = Toast.LENGTH_SHORT) {
        if (string == null) return
        if (toast == null) {
            toast = Toast.makeText(BaseApplication.instance.applicationContext, string, duration)
        } else {
            toast?.setText(string)
            toast?.duration = duration
        }
        Handler(Looper.getMainLooper()).post { toast?.show() }
    }

    @SuppressLint("ToastUtil.showToast", "ShowToast")
    fun showToast(@StringRes strId: Int, duration: Int = Toast.LENGTH_SHORT) {
        if (toast == null) {
            toast = Toast.makeText(BaseApplication.instance.applicationContext, strId, duration)
        } else {
            toast?.setText(strId)
            toast?.duration = duration
        }
        Handler(Looper.getMainLooper()).post { toast?.show() }
    }

    fun dismiss() {
        if (toast != null) toast?.cancel()
    }
}

fun Any.showToast(string: String?, duration: Int = Toast.LENGTH_SHORT) {
    ToastUtil.showToast(string, duration)
}

fun Any.showToast(@StringRes strId: Int, duration: Int = Toast.LENGTH_SHORT) {
    ToastUtil.showToast(strId, duration)
}