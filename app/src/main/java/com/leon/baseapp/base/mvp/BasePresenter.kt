package com.leon.baseapp.base.mvp

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.cxz.wanandroid.http.RetrofitHelper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus


/**
 * Created by leonhwang on 2017/9/4.
 */
abstract class BasePresenter<V : IBaseView> : LifecycleObserver {
    val mDisposables: CompositeDisposable by lazy { CompositeDisposable() }

    protected var mvpView: V? = null
    var totalPage = 1
    var model: RequestMode? = RequestMode.REQUEST
    val request = RetrofitHelper.service

    /**
     * 是否使用 EventBus
     */
    open fun useEventBus(): Boolean = false


    fun attachView(view: V) {
        this.mvpView = view
        if (mvpView is LifecycleOwner) {
            (mvpView as LifecycleOwner).lifecycle.addObserver(this)
        }
    }

    /**
     * 取消订阅
     * 如果不能及时被释放，将有内存泄露的风险
     */
    fun detachView() {
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
        this.mvpView = null
        if (!mDisposables.isDisposed) {
            mDisposables.clear()
        }
    }

    /**
     * 添加订阅事件
     */
    fun addSubscription(disposable: Disposable) {
        if (mvpView == null) throw Throwable("IBaseView未绑定")//检查view是否绑定
        mDisposables.add(disposable)
    }

    open fun start() {}


    /**
     *@param REQUEST 初次请求
     * @param MORE 请求更多
     */
    enum class RequestMode {
        REQUEST, MORE
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(owner: LifecycleOwner) {
        detachView()
        owner.lifecycle.removeObserver(this)
    }

    companion object {
        fun getParamsFactory() = ParamsFactory()
    }
}