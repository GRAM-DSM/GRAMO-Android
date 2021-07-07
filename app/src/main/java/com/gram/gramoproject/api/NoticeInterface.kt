package com.gram.gramoproject.api

import com.gram.gramoproject.model.NoticeItem
import com.gram.gramoproject.model.NoticeList
import com.gram.gramoproject.model.GetDetailNotice

import retrofit2.Call
import retrofit2.http.*

interface NoticeInterface {
    @GET("/notice/list/{page}")
    fun getNoticeList(
            @Header("Authorization") header: String,
            @Path("page") page : Int) : Call<NoticeList>

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