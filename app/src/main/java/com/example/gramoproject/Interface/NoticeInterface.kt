package com.example.gramoproject.`interface`

import com.example.gramoproject.DataClass.NoticeDetail
import com.example.gramoproject.DataClass.Notice
import com.example.gramoproject.dataclass.NoticeItem

import retrofit2.Call
import retrofit2.http.*

interface NoticeInterface {
    @GET("/notice/{off_set}/{limit_num}")
    fun getNoticeList(
            @Header("Authorization") header: String,
            @Path("off_set") off_set: Int,
            @Path("limit_num") limit_num: Int) : Call<Notice>

    @GET("/notice/{notice_id}")
    fun getNoticeDetail(
            @Header("Authorization") header: String,
            @Path("notice_id") notice_id: Int) : Call<NoticeDetail>

    @POST("/notice")
    fun createNotice(
            @Header("Authorization") header: String,
            @Body notice : NoticeItem
    ): Call<Unit>

    @DELETE("/notice/{notice_id}")
    fun deleteNotice(
            @Header("Authorization") header: String,
            @Path("notice_id") notice_id: Int) : Call<Unit>
}