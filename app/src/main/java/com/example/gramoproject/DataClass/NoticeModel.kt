package com.example.gramoproject.DataClass

import com.google.gson.annotations.SerializedName

data class NoticeModel(
        @SerializedName("id")
        val id: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("content")
        val content: String,
        @SerializedName("email")
        val email: String,
        @SerializedName("dateTime")
        val dateTime: String)
