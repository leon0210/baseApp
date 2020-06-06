package com.leon.baseapp.utils.ext

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.leon.baseapp.R
import com.leon.baseapp.base.BaseApplication
import com.leon.baseapp.widget.GlideRoundTransform


/**
 * Author: 千里
 * Date: 2020/6/2 11:01
 * Description:
 */
/**
 * 导入view
 */
fun Context.getQuickLayoutInflater(@LayoutRes resId: Int): View? =
    LayoutInflater.from(this).inflate(resId, null)

/**
 * 获取图片
 */
fun getQuickDrawable(@DrawableRes resId: Int): Drawable? =
    ContextCompat.getDrawable(BaseApplication.instance, resId)

/**
 * 获取字符串
 */
fun getQuickString(@StringRes resId: Int): String? = BaseApplication.instance.getString(resId)

/**
 * 获取颜色
 */
fun getQuickColor(@ColorRes resId: Int): Int =
    ContextCompat.getColor(BaseApplication.instance, resId)

/**
 * view是否显示 true 显示 false 隐藏
 */
fun View.setVisible(visible: Boolean, inVisible: Boolean = false) {
    if (inVisible) {
        this.visibility = View.INVISIBLE
        return
    }
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.getVisible() = this.visibility == View.VISIBLE

/**
 * 首行缩进两个字符
 */
fun TextView.textIndent2Char(content: String?) {
    if (content == null) {
        this.text = ""
        return
    }
    val span = SpannableStringBuilder("缩进$content")
    span.setSpan(
        ForegroundColorSpan(Color.TRANSPARENT), 0, 2,
        Spanned.SPAN_INCLUSIVE_EXCLUSIVE
    )
    this.text = span
}

/**
 * 图片加载 Glide
 */
fun ImageView.loadImg(
    path: Any?,
    error: Int = R.mipmap.ic_launcher,
    transformSize: Int = 0
) {
    val sharedOptions: RequestOptions = RequestOptions()
        .placeholder(error)
        .error(error)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
    if (transformSize > 0) sharedOptions.centerCrop().transform(GlideRoundTransform(transformSize))
    Glide.with(BaseApplication.instance)
        .load(path)
        .apply(sharedOptions)
        .into(this)
}