package com.gram.gramoproject.api

import com.gram.gramoproject.model.Login
import com.gram.gramoproject.model.LoginUser
import com.gram.gramoproject.model.TokenRefresh
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