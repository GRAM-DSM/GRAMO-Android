package com.example.gramoproject.`interface`

import com.example.gramoproject.dataclass.Login
import com.example.gramoproject.dataclass.TokenModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface LoginInterface {
    @POST("/auth")
    fun signIn(
            @Body login: Login
    ): Call<TokenModel>

    @DELETE("/auth")
    fun logout(
            @Header("Authorization")header: String) : Call<Unit>

    @GET("/auth")
    fun tokenRefresh(
            @Header("Authorization")header: String) : Response<TokenModel>
}