package com.leon.baseapp.base.mvp

import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileNotFoundException

class ParamsFactory {
    private var anyMap: MutableMap<String, Any> = mutableMapOf()
    var mMultipartBodyBuilder: MultipartBody.Builder? = null

    init {
        init()
    }

    fun init() {
        anyMap.clear()
        //todo 这里可以添加公共参数
    }

    fun add(key: String, value: Any): ParamsFactory {
        anyMap[key] = value
        return this
    }

    fun buildStrMap(): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        anyMap.mapValues {
            map[it.key] = it.value.toString()
        }
        init()
        return map
    }

    fun buildAnyMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        map.putAll(anyMap)
        init()
        return map
    }

    fun buildRequestBody(): RequestBody {
        val json = Gson().toJson(anyMap)
        init()
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
    }

    fun addFile(partName: String, path: String): ParamsFactory {
        if (mMultipartBodyBuilder == null) mMultipartBodyBuilder = MultipartBody.Builder()
        val file = File(path)
        if (!file.exists()) throw FileNotFoundException("文件不存在")
        val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        mMultipartBodyBuilder?.addFormDataPart(partName, file.name, requestBody)
        return this
    }

    fun buildMultipartBody(): MultipartBody {
        if (mMultipartBodyBuilder == null) throw KotlinNullPointerException("mMultipartBodyBuilder is Null")
        mMultipartBodyBuilder?.setType(MultipartBody.FORM)
        return mMultipartBodyBuilder?.build()!!
    }
}