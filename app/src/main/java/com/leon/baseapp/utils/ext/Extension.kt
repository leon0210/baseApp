package com.leon.baseapp.utils.ext

import android.app.Activity
import android.view.ViewGroup
import android.webkit.WebView
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import com.just.agentweb.WebChromeClient
import com.just.agentweb.WebViewClient
import com.leon.baseapp.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * Created by leonhwang on 2017/8/31.
 */

fun <T> Observable<T>.applySchedulers(): Observable<T> {
    return subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

inline fun tryCatch(tryBlock: () -> Unit) {
    try {
        tryBlock()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

inline fun tryCatch(tryBlock: () -> Unit, catchBlock: (Throwable) -> Unit) {
    try {
        tryBlock()
    } catch (e: Exception) {
        e.printStackTrace()
        catchBlock(e)
    }
}

/**
 * 复制文字到剪切板
 */
/*fun String.copy(context: Context) {
    var mClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    mClipboardManager.primaryClip= ClipData.newPlainText(null, this)
}*/

/**
 * getAgentWeb
 */
fun String.getAgentWeb(
    activity: Activity,
    webContent: ViewGroup,
    layoutParams: ViewGroup.LayoutParams,
    webView: WebView,
    webViewClient: WebViewClient?,
    webChromeClient: WebChromeClient?,
    indicatorColor: Int
): AgentWeb = AgentWeb.with(activity)//传入Activity or Fragment
    .setAgentWebParent(webContent, 1, layoutParams)//传入AgentWeb 的父控件
    .useDefaultIndicator(indicatorColor, 2)// 使用默认进度条
    .setWebView(webView)
    .setWebViewClient(webViewClient)
    .setWebChromeClient(webChromeClient)
    .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
    .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
    .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)//打开其他应用时，弹窗咨询用户是否前往其他应用
    .interceptUnkownUrl()
    .createAgentWeb()//
    .ready()
    .go(this)
