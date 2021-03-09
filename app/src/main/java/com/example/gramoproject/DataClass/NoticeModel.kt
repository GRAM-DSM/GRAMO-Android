package com.example.gramoproject.DataClass

import com.google.gson.annotations.SerializedName

data class NoticeModel(
        @SerializedName("id")
        val id: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("content")
        val content: String,
        @SerializedName("user_email")
        val user_email: String,
        @SerializedName("created_at")
        val created_at: String)
