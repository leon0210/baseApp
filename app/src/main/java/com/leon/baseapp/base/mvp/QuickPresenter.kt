package com.leon.baseapp.base.mvp

import com.cxz.wanandroid.http.function.RetryWithDelay
import com.leon.baseapp.utils.ext.applySchedulers
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody

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
    fun <T> doRequest(observable: Observable<T>, success: ((T) -> Unit)? = null) {
        observable.applySchedulers().retryWhen(RetryWithDelay())
            .subscribe(MyObserver(mvpView, mDisposables, success))
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
