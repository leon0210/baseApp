package com.leon.baseapp.base.mvp

import com.cxz.wanandroid.mvp.model.bean.BaseBean
import com.leon.baseapp.R
import com.leon.baseapp.http.ErrorStatus
import com.leon.baseapp.utils.ext.getQuickString
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.net.UnknownHostException

class MyObserver<T> : Observer<T> {
    private var mvpView: IBaseView?
    private var mDisposables: CompositeDisposable
    private var mCall: ((T) -> Unit)? = null

    constructor(view: IBaseView?, disposable: CompositeDisposable) {
        mvpView = view
        mDisposables = disposable
    }

    constructor(view: IBaseView?, disposable: CompositeDisposable, call: ((T) -> Unit)? = null) {
        mvpView = view
        mDisposables = disposable
        mCall = call
    }


    override fun onComplete() {
    }

    override fun onSubscribe(d: Disposable) {
        mDisposables.add(d)
        mvpView?.showLoading()
    }

    override fun onNext(t: T) {
        mvpView?.hideLoading()
        if (t is BaseBean) {
            if (t.errorCode == ErrorStatus.SUCCESS) {
                if (mCall == null) mvpView?.onSuccess(t)
                else mCall?.invoke(t)
            } else mvpView?.onFailed(t.errorMsg)
        } else {
            if (mCall == null) mvpView?.onSuccess(t)
            else mCall?.invoke(t)
        }
    }

    override fun onError(e: Throwable) {
        mvpView?.hideLoading()
        if (e is UnknownHostException) mvpView?.onFailed(getQuickString(R.string.network_unavailable_tip))
        else mvpView?.onError(e)
    }
}