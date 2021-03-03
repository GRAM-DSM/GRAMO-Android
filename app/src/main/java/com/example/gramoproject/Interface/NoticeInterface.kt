package com.example.gramoproject.Interface

import com.example.gramoproject.DataClass.NoticeModel
import retrofit2.Call
import retrofit2.http.*

interface NoticeInterface {
    @Headers("Authorization: your auth token")
    @GET("")
    fun getNoticeList(@Query("size") size: Int) : Call<Int>

    @Headers("Authorization: your auth token")
    @GET("")
    fun getNoticeDetail(@Path("id") id: Int) : Call<Int>

    @FormUrlEncoded
    @Headers("Authorization: your auth token")
    @POST("")
    fun getNotice(
        @Field("title") title:String,
        @Field("content") content: String
    ): Call<NoticeModel>

    @Headers("Authorization: your auth token")
    @DELETE("")
    fun deleteNotice(@Path("id") id: Int) : Call<Int>
}