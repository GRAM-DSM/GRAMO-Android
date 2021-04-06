package com.example.gramoproject.DataClass

import com.google.gson.annotations.SerializedName

data class UserModel(
        @SerializedName("email")
        val email: String,
        @SerializedName("password")
        val password: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("major")
        val major: String,
        @SerializedName("code")
        val code: String)