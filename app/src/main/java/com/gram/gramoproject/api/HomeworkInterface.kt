package com.gram.gramoproject.api

import com.gram.gramoproject.model.HomeworkBodyData
import com.gram.gramoproject.model.HomeworkContentResponseData
import com.gram.gramoproject.model.HomeworkResponse
import com.gram.gramoproject.model.HomeworkedUserData
import retrofit2.Call
import retrofit2.http.*

interface HomeworkInterface {
    @GET("/homework/assign")
    fun getAssignedHomeworkList(@Header("Authorization") header: String): Call<List<HomeworkResponse>>

    @GET("/homework/submit")
    fun getSubmittedHomeworkList(@Header("Authorization") header: String): Call<List<HomeworkResponse>>

    @GET("/homework/order")
    fun getOrderedHomeworkList(@Header("Authorization")header: String): Call<List<HomeworkResponse>>

    @GET("/homework/{homeworkID}")
    fun getHomeworkContent(
            @Path("homeworkID") homeworkID: Int,
            @Header("Authorization") header: String
    ): Call<HomeworkContentResponseData>

    @POST("/homework")
    fun createHomework(
            @Header("Authorization") header: String,
            @Body body: HomeworkBodyData): Call<Unit>

    @DELETE("/homework/{detailId}")
    fun deleteHomework(
            @Header("Authorization") header: String,
            @Path("detailId") detailId: Int): Call<Unit>

    @PATCH("/homework/{homeworkID}")
    fun submitHomework(
            @Header("Authorization") header: String,
            @Path("homeworkID") homeworkID: Int): Call<Unit>

    @PATCH("/homework/reject/{homeworkID}")
    fun rejectHomework(
            @Header("Authorization") header: String,
            @Path("homeworkID") homeworkID: Int): Call<Unit>

    @GET("/user/list")
    fun getUserList(
            @Header("Authorization") header: String
    ): Call<HomeworkedUserData>

}

