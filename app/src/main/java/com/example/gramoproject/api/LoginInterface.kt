package com.example.gramoproject.api

import com.example.gramoproject.model.Login
import com.example.gramoproject.model.LoginUser
import com.example.gramoproject.model.TokenRefresh
import retrofit2.Call
import retrofit2.http.*

interface LoginInterface {
    @POST("/auth")
    fun signIn(
            @Body login: Login
    ): Call<LoginUser>

    @DELETE("/auth")
    fun logout(
            @Header("Authorization")header: String) : Call<Unit>

    @DELETE("/withdrawal")
    fun withDrawal(
            @Header("Authorization")header: String) : Call<Unit>

    @GET("/auth")
    fun tokenRefresh(
            @Header("Authorization")header: String) : Call<TokenRefresh>
}