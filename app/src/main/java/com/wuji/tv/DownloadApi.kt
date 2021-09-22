package com.wuji.tv

import com.wuji.tv.model.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface DownloadApi {

    @POST("/version")
    @Headers("Content-Type:text/plain; charset=utf-8")
    suspend fun version(): ResponseBody

    @POST("/getList")
    @Headers("Content-Type:text/plain; charset=utf-8")
    suspend fun getList(@Body params: Any): BaseDownloadResult<SharedListModel>

    @POST("/download")
    @Headers("Content-Type:text/plain; charset=utf-8")
    suspend fun download(@Body params: Any): BaseDownloadResult<DownloadResultModel>

    @POST("/progress")
    @Headers("Content-Type:text/plain; charset=utf-8")
    suspend fun progress(@Body params: Any): BaseDownloadResult<ProgressModel>

    @POST("/getCompleteFileList")
    @Headers("Content-Type:text/plain; charset=utf-8")
    suspend fun getCompleteFileList(@Body params: Any): BaseDownloadResult<CompelateDownloadModel>


    @POST("/getDownloadInfo")
    @Headers("Content-Type:text/plain; charset=utf-8")
    suspend fun getDownloadInfo(@Body params: Any): ResponseBody

    @POST("/cancelDownload")
    @Headers("Content-Type:text/plain; charset=utf-8")
    suspend fun cancelDownload(@Body params: Any): ResponseBody

    @POST("/stopDownload")
    @Headers("Content-Type:text/plain; charset=utf-8")
    suspend fun stopDownload (@Body params: Any): ResponseBody

    @POST("/resumeDownload")
    @Headers("Content-Type:text/plain; charset=utf-8")
    suspend fun resumeDownload  (@Body params: Any): ResponseBody

    @POST("/auth")
    @Headers("Content-Type:text/plain; charset=utf-8")
    suspend fun auth(@Body params: Any): BaseDownloadDbResult<AuthResult>

    @POST("/list")
    @Headers("Content-Type:text/plain; charset=utf-8")
    suspend fun listDb(): BaseDownloadDbResult<ListResult>

    @POST("/download")
    @Headers("Content-Type:text/plain; charset=utf-8")
    suspend fun downloadDb(@Body params: Any): BaseDownloadDbResult<DownloadDbResultModel>

    @POST("/progress")
    @Headers("Content-Type:text/plain; charset=utf-8")
    suspend fun dbProgress(@Body params: Any): BaseDownloadDbResult<ProgressDbModel>

    @POST("/cancel")
    @Headers("Content-Type:text/plain; charset=utf-8")
    suspend fun cancelDb(@Body params: Any): BaseCancelDbResult

    @POST("/stop")
    @Headers("Content-Type:text/plain; charset=utf-8")
    suspend fun stopDb(@Body params: Any): BaseCancelDbResult

    @POST("/resume")
    @Headers("Content-Type:text/plain; charset=utf-8")
    suspend fun resumeDb(@Body params: Any): BaseCancelDbResult

    @POST("/version")
    @Headers("Content-Type:text/plain; charset=utf-8")
    fun versionDb(): Call<ResponseBody>

    @POST("/auth")
    @Headers("Content-Type:text/plain; charset=utf-8")
    fun authTest(@Body params: Any): Call<ResponseBody>

    @POST("/progress")
    @Headers("Content-Type:text/plain; charset=utf-8")
    fun progressDbTest(@Body params: Any): Call<ResponseBody>

}