package com.example.gramoproject.Interface

import com.example.gramoproject.DataClass.NoticeModel
import retrofit2.Call
import retrofit2.http.*

interface NoticeInterface {
    @GET("/notice")
    fun getNoticeList(
            @Header("Authorization") header: String,
            @Query("off_set") off_set: Int,
            @Query("limit_num") limit_num: Int) : Call<NoticeModel>

    @Headers("Authorization: your auth token")
    @GET("/notice/\$id")
    fun getNoticeDetail(
            @Header("Authorization") header: String,
            @Path("id") id: Int) : Call<NoticeModel>

    @FormUrlEncoded
    @POST("/notice")
    fun createNotice(
            @Header("Authorization") header: String,
            @Field("title") title:String,
            @Field("content") content: String
    ): Call<Unit>

    @DELETE("/notice/\$id")
    fun deleteNotice(
            @Header("Authorization") header: String,
            @Path("id") id: Int) : Call<NoticeModel>
}