package com.example.gramoproject.`interface`

import com.example.gramoproject.DataClass.Login
import com.example.gramoproject.DataClass.LoginUser
import com.example.gramoproject.DataClass.TokenRefresh
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

    @GET("/auth")
    fun tokenRefresh(
            @Header("Authorization")header: String) : Call<TokenRefresh>
}