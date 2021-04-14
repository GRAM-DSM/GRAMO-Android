package com.example.gramoproject.`interface`

import com.example.gramoproject.dataclass.NoticeItem
import com.example.gramoproject.dataclass.NoticeModel
import retrofit2.Call
import retrofit2.http.*

interface NoticeInterface {
    @GET("/notice")
    fun getNoticeList(
            @Header("Authorization") header: String,
            @Path("off_set") off_set: Int,
            @Path("limit_num") limit_num: Int) : Call<NoticeModel>

    @Headers("Authorization: your auth token")
    @GET("/notice/\$id")
    fun getNoticeDetail(
            @Header("Authorization") header: String,
            @Path("id") id: Int) : Call<NoticeModel>

    @POST("/notice")
    fun createNotice(
            @Header("Authorization") header: String,
            @Body notice : NoticeItem
    ): Call<Unit>

    @DELETE("/notice/\$id")
    fun deleteNotice(
            @Header("Authorization") header: String,
            @Path("id") id: Int) : Call<NoticeModel>
}