package com.example.gramoproject.api

import com.example.gramoproject.model.PicuList
import com.example.gramoproject.model.PlanList
import retrofit2.Call
import retrofit2.http.*

interface CalendarInterface {
    @GET("/calendar/plan/{date}")
    fun getPlan(
        @Header("Authorization") header: String,
        @Path("date") date: String
    ): Call<PlanList>

    @GET("/calendar/picu/{date}")
    fun getPicu(
        @Header("Authorization") header: String,
        @Path("date") date: String
    ): Call<PicuList>

    @POST("/calendar/plan")
    fun createPlan(
        @Header("Authorization") header: String,
        @Body content: String,
        @Body title: String,
        @Body date: String
    ) : Call<Unit>

    @POST("/calendar/picu")
    fun createPicu(
        @Header("Authorization") header: String,
        @Body description : String,
        @Body date : String
    ) : Call<Unit>

    @DELETE("/calendar/plan/{planId}")
    fun deletePlan(
        @Header("Authorization") header: String,
        @Path("planId") planId : Int
    ) : Call<Unit>

    @DELETE("/calendar/picu/{picuId}")
    fun deletePicu(
        @Header("Authorization") header: String,
        @Path("picuId") picuId : Int
    ) : Call<Unit>
}