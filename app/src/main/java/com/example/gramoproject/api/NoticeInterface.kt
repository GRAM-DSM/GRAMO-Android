package com.example.gramoproject.`interface`

import com.example.gramoproject.model.NoticeItem
import com.example.gramoproject.model.NoticeList
import com.example.gramoproject.model.GetDetailNotice

import retrofit2.Call
import retrofit2.http.*

interface NoticeInterface {
    @GET("/notice/{off_set}/{limit_num}")
    fun getNoticeList(
            @Header("Authorization") header: String,
            @Path("off_set") off_set: Int,
            @Path("limit_num") limit_num: Int) : Call<NoticeList>

    @GET("/notice/{notice_id}")
    fun getNoticeDetail(
            @Header("Authorization") header: String,
            @Path("notice_id") notice_id: Int) : Call<GetDetailNotice>

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