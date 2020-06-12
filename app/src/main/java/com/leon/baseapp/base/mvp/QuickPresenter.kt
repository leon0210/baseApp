package com.leon.baseapp.base.mvp

import com.cxz.wanandroid.mvp.model.bean.BaseBean
import com.leon.baseapp.R
import com.leon.baseapp.http.ErrorStatus
import com.leon.baseapp.utils.ext.applySchedulers
import com.leon.baseapp.utils.ext.getQuickString
import com.leon.baseapp.utils.ext.tryCatch
import io.reactivex.Observable
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.net.UnknownHostException

class QuickPresenter : BasePresenter<IBaseView>() {

    private val paramsFactory = ParamsFactory()

    /**
     * author: 千里
     * date: 2020/6/2 15:04
     * describe:固定参数
     * get
     *  @Path  url中带有参数一般配合{}一起 例如：/xxx/{param}/xxx
     *  @Query 是把key-value 拼接到url 后面 例如：/xxx/xx?param=yy
     * post 一般配合@FormUrlEncoded使用
     *      @FormUrlEncoded主要是做表单提交，与@POST结合使用
     *      @Multipart主要是与@POST结合使用做文件的上传
     *      @Streaming主要做大文件下载
     *  @Field
     */
//    fun <T> doRequest(observable: Observable<T>, success: ((T) -> Unit)? = null) {
//        observable.applySchedulers().retryWhen(RetryWithDelay())
//            .subscribe(MyObserver(mvpView, mDisposables, success))
//    }
    /**
     * author: 千里
     * date: 2020/6/12 11:03
     * describe:协程
     */
    fun <T> doRequest(
        request: () -> Deferred<T>,
        success: ((T) -> Unit)? = null,
        failed: ((Pair<String?, Int?>) -> Unit)? = null,
        showLoading: Boolean = true
    ) {
        if (showLoading) mvpView?.showLoading()
        GlobalScope.launch(Dispatchers.IO) {
            tryCatch({
                val response = request().await()
                withContext(Dispatchers.Main) {
                    mvpView?.hideLoading()
                    if (response is BaseBean) {
                        if (response.errorCode == ErrorStatus.SUCCESS) {
                            success?.invoke(response) ?: mvpView?.onSuccess(response)
                        } else failed?.invoke(
                            Pair(response.errorMsg, response.errorCode)
                        ) ?: mvpView?.onFailed(response.errorMsg, response.errorCode)
                    } else {
                        success?.invoke(response) ?: mvpView?.onSuccess(response)
                    }
                }
            }, {
                withContext(Dispatchers.Main) {
                    if (showLoading) mvpView?.hideLoading()
                    if (failed != null) {
                        if (it is UnknownHostException) mvpView?.onFailed(getQuickString(R.string.network_unavailable_tip))
                        else failed(Pair(it.message, -1))
                    } else {
                        if (it is UnknownHostException) mvpView?.onFailed(getQuickString(R.string.network_unavailable_tip))
                        else mvpView?.onError(it)
                    }
                }
            })
        }
    }

    /**
     * author: 千里
     * date: 2020/6/2 15:04
     * describe:
     * @Body 实体类 如：body: RequestBody
     *      RequestBody可以动态增加参数 这里参数为json
     */
    fun doRequestBody(
        call: (requestBody: RequestBody) -> Observable<*>,
        success: ((Any) -> Unit)? = null
    ) {
        call(paramsFactory.buildRequestBody()).applySchedulers()
            .subscribe(MyObserver(mvpView, mDisposables, success))
        paramsFactory.init()
    }

    /**
     * author: 千里
     * date: 2020/6/2 17:13
     * describe:动态参数 map
     * @QueryMap  多个@Query
     * @FieldMap  多个@Field
     */
    fun doRequestMap(
        call: (map: MutableMap<String, String>) -> Observable<*>,
        success: ((Any) -> Unit)? = null
    ) {
        call(paramsFactory.buildStrMap()).applySchedulers()
            .subscribe(MyObserver(mvpView, mDisposables, success))
        paramsFactory.init()
    }

    /**
     * author: 千里
     * date: 2020/6/2 17:13
     * describe:文件带参数
     * post
     * @QueryMap map: MutableMap<String, String>, @Body body: MultipartBody
     */
    fun doRequestMapAndMultipartBody(
        call: (map: MutableMap<String, String>, body: MultipartBody) -> Observable<*>,
        success: ((Any) -> Unit)? = null
    ) {
        call(paramsFactory.buildStrMap(), paramsFactory.buildMultipartBody()).applySchedulers()
            .subscribe(MyObserver(mvpView, mDisposables, success))
        paramsFactory.init()
    }

    fun add(key: String, value: Any): QuickPresenter {
        paramsFactory.add(key, value)
        return this
    }

    fun addFile(partName: String, path: String): QuickPresenter {
        paramsFactory.addFile(partName, path)
        return this
    }
}
