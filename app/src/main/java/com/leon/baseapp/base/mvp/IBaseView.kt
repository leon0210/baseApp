package com.leon.baseapp.base.mvp


/**
 * Created by leonhwang on 2017/9/1.
 */
interface IBaseView {
    /**
     * 显示加载动画
     */
    fun showLoading()

    /**
     * 隐藏加载动画
     */
    fun hideLoading()

    /**
     * 请求成功
     * @param any 返回的数据
     */
    fun onSuccess(any: Any?)

    /**
     * 请求失败
     * @param errorCode 错误码
     * @param errorMsg 错误信息
     */
    fun onFailed(errorMsg: String? = "", errorCode: String? = "")

    /**
     * 请求异常
     * @param e 异常
     */
    fun onError(e: Throwable)
}