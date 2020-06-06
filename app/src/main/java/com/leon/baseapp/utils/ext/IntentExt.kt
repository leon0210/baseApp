package com.leon.baseapp.utils.ext

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment

/**
 * Author: 千里
 * Date: 2020/6/2 11:02
 * Description:
 */

/**
 * @param requestCode 请求码
 */
inline fun <reified T : Activity> Activity.newIntent(
    bundle: Bundle? = null,
    requestCode: Int = -100
) {
    val intent = Intent(this, T::class.java)
    if (bundle != null) {
        intent.putExtras(bundle)
    }
    if (requestCode != -100) startActivityForResult(intent, requestCode)
    else startActivity(intent)
}

inline fun <reified T : Activity> Fragment.newIntent(
    bundle: Bundle? = null,
    requestCode: Int = -100
) {
    val intent = Intent(activity, T::class.java)
    if (bundle != null) {
        intent.putExtras(bundle)
    }
    if (requestCode != -100) startActivityForResult(intent, requestCode)
    else startActivity(intent)
}


/**
 * 动画共享 使用于5.0版本以上
 */
inline fun <reified T : Activity> Activity.newTransitionIntent(
    view: View?,
    bundle: Bundle? = null,
    requestCode: Int = -100
) {
    val intent = Intent(this, T::class.java)
    if (bundle != null) {
        intent.putExtras(bundle)
    }
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        if (requestCode != -100) startActivityForResult(intent, requestCode)
        else startActivity(intent)
    } else {
        val transitionAnimation =
            ActivityOptionsCompat.makeSceneTransitionAnimation(this, view!!, "secondSharedView")
        if (requestCode != -100) ActivityCompat.startActivityForResult(
            this,
            intent,
            requestCode,
            transitionAnimation.toBundle()
        )
        else ActivityCompat.startActivity(this, intent, transitionAnimation.toBundle())
    }
}

inline fun <reified T : Activity> Fragment.newTransitionIntent(
    view: View?,
    bundle: Bundle? = null,
    requestCode: Int = -100
) {
    val intent = Intent(activity, T::class.java)
    if (bundle != null) {
        intent.putExtras(bundle)
    }
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        if (requestCode != -100) startActivityForResult(intent, requestCode)
        else startActivity(intent)
    } else {
        val transitionAnimation = ActivityOptionsCompat.makeSceneTransitionAnimation(
            activity!!,
            view!!,
            "secondSharedView"
        )
        if (requestCode != -100) ActivityCompat.startActivityForResult(
            activity!!,
            intent,
            requestCode,
            transitionAnimation.toBundle()
        )
        else ActivityCompat.startActivity(activity!!, intent, transitionAnimation.toBundle())
    }
}

/**
 * 使用浏览器打开指定网址
 */
fun Activity.openBrowser(targetUrl: String) {
    if (TextUtils.isEmpty(targetUrl) || targetUrl.startsWith("file://")) {
        ToastUtil.showToast("$targetUrl 该链接无法使用浏览器打开。")
        return
    }
    Intent().run {
        action = "android.intent.action.VIEW"
        data = Uri.parse(targetUrl)
        startActivity(this)
    }
}