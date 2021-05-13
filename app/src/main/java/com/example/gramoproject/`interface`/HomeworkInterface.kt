package com.example.gramoproject.`interface`

import com.example.gramoproject.DataClass.HomeworkBodyData
import com.example.gramoproject.DataClass.HomeworkContentResponseData
import com.example.gramoproject.DataClass.HomeworkResponse
import com.example.gramoproject.DataClass.HomeworkedUserData
import retrofit2.Call
import retrofit2.http.*

interface HomeworkInterface {
    @GET("/homework/assign")
    fun getAssignedHomeworkList(): Call<List<HomeworkResponse>>

    @GET("/homework/submit")
    fun getSubmittedHomeworkList(): Call<List<HomeworkResponse>>

    @GET("/homework/order")
    fun getOrderedHomeworkList(): Call<List<HomeworkResponse>>

    @GET("/homework/{homeworkID}")
    fun getHomeworkContent(@Path("homeworkID") homeworkID: Int): Call<HomeworkContentResponseData>

    @POST("/homework")
    fun createHomework(@Body body: HomeworkBodyData): Call<Unit>

    @DELETE("/homework/{detailId}")
    fun deleteHomework(@Path("detailId") detailId: Int): Call<Unit>

    @PATCH("/homework/{homeworkID}")
    fun submitHomework(@Path("homeworkID") homeworkID: Int): Call<Unit>

    @PATCH("/homework/reject/{homeworkID}")
    fun rejectHomework(@Path("homeworkID") homeworkID: Int): Call<Unit>

    @GET("/user/list")
    fun getUserList(): Call<HomeworkedUserData>

}

