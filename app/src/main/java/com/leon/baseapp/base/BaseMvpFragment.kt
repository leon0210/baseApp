package com.leon.baseapp.base


import android.os.Bundle
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.leon.baseapp.base.mvp.BasePresenter
import com.leon.baseapp.base.mvp.IBaseView
import com.leon.baseapp.base.mvp.QuickPresenter
import com.leon.baseapp.utils.ext.ToastUtil
import com.leon.baseapp.utils.ext.ToastUtil.showToast


/**
 * Created by leonhwang on 2017/8/31.
 */

@Suppress("UNCHECKED_CAST")
abstract class BaseMvpFragment<V : IBaseView> :
    BaseFragment(), IBaseView, LifecycleOwner {

    protected var mPresenter: QuickPresenter? = QuickPresenter()

    /**
     * 是否是刷新
     */
    protected var isRefresh = true

    /**
     * 初始化UI
     */
    override fun initView(view: View) {
        this.mPresenter?.attachView(this as V)
    }

    override fun onPause() {
        super.onPause()
//        dismissLoadingDialog() //关闭对话框,防止窗口泄露
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.mPresenter?.detachView()
        this.mPresenter = null
//        dismissLoadingDialog() //关闭对话框,防止窗口泄露
    }

    override fun showLoading() {
//        showLoadingDialog()
    }

    override fun hideLoading() {
//        dismissLoadingDialog()
    }

    override fun onFailed(errorMsg: String?, errorCode: String?) {
        showToast(errorMsg)
    }

    override fun onError(e: Throwable) {
        showToast(e.message)
    }

    override fun onSuccess(any: Any?) {

    }
}
