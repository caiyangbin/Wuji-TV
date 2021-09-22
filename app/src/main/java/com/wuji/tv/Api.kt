package com.wuji.tv

import com.wuji.tv.model.*
import okhttp3.ResponseBody
import retrofit2.http.*


interface Api {

    @POST("/user")
    suspend fun access(@Body params: Any): BaseResult<AccessResult>

    @POST("/file")
    suspend fun fileList(@Body params: Any): BaseResult<FileListResult>


    @POST("/file")
    suspend fun deleteFile(@Body params: Any): BaseResult<*>

    @POST("/file")
    suspend fun mkDir(@Body params: Any): BaseResult<*>

    @POST("/file")
    suspend fun copyFile(@Body params: Any): BaseResult<*>
    @POST("/file")
    suspend fun fileInfo(@Body params: Any): BaseResult<FileInfoResult>

    @POST("/file")
    suspend fun fileThumbnail(@Body params: Any): BaseResult<*>

    @POST("/file")
    suspend fun readTxt(@Body params: Any): BaseResult<TxtResult>

    @POST("/file")
    suspend fun updateFile(@Body params: Any): BaseResult<Any>

    @POST("/file")
    suspend fun readVersion(@Body params: Any): BaseResult<VersionResult>


    @Streaming
    @GET
    suspend fun downloadFile(@Url fileUrl: String): ResponseBody


}