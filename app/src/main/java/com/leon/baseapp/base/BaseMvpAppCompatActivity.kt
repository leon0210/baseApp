package com.leon.baseapp.base


import android.os.Bundle
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
abstract class BaseMvpAppCompatActivity<V : IBaseView> :
    BaseAppCompatActivity(), IBaseView, LifecycleOwner {

    protected var mPresenter: QuickPresenter? = QuickPresenter()

    /**
     * 初始化UI
     */
    override fun initView() {
        mPresenter?.attachView(this as V)
    }

    override fun onPause() {
        super.onPause()
//        dismissLoadingDialog() //关闭对话框,防止窗口泄露
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.detachView()
        mPresenter = null
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
}
