package com.wuji.tv.widget

import com.wuji.tv.model.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface CreateApi {

    @POST("/list")
    @Headers("Content-Type:text/plain; charset=utf-8")
    fun listDb(@Body params: Any): Call<ResponseBody>

    @POST("/create")
    @Headers("Content-Type:text/plain; charset=utf-8")
    suspend fun create(@Body params: Any): BaseDownloadResult<CreateResultModel>

    @POST("/cancel")
    @Headers("Content-Type:text/plain; charset=utf-8")
    suspend fun cancel(@Body params: Any): CancelResultModel


}