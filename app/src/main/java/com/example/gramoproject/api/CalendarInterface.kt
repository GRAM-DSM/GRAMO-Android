package com.example.gramoproject.api

import com.example.gramoproject.model.PicuList
import com.example.gramoproject.model.PlanList
import retrofit2.Call
import retrofit2.http.*

interface CalendarInterface {
    @GET("/calendar/plan")
    fun getPlan(
        @Header("Authorization") header: Header,
        @Path("date") date: String
    ): Call<PlanList>

    @GET("/calendar/picu")
    fun getPicu(
        @Header("Authorization") header: Header,
        @Path("date") date: String
    ): Call<PicuList>

    @POST("/calendar/plan")
    fun createPlan(
        @Header("Authorization") header: Header,
        @Body content: String,
        @Body title: String,
        @Body date: String
    ) : Call<Unit>

    @POST("/calendar/picu")
    fun createPicu(
        @Header("Authorization") header: Header,
        @Body description : String,
        @Body date : String
    ) : Call<Unit>

    @DELETE("/calendar/plan")
    fun deletePlan(
        @Header("Authorization") header: Header,
        @Path("planId") planId : Int
    ) : Call<Unit>

    @DELETE("/calendar/picu")
    fun deletePicu(
        @Header("Authorization") header: Header,
        @Path("picuId") picuId : Int
    ) : Call<Unit>
}