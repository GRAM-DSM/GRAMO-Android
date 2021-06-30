package com.example.gramoproject.api

import com.example.gramoproject.model.EmailAuth
import com.example.gramoproject.model.RegisterUser
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface RegisterInterface {
    @POST("/signup")
    fun signUp(
            @Body user : RegisterUser
    ): Call<Unit>

    @POST("/sendemail")
    fun sendEmail(
            @Body email: JsonObject
    ) : Call<Unit>

    @POST("/checkcode")
    fun checkEmailAuthenticationCode(
            @Body authInfo : EmailAuth
    ) : Call<Unit>
}