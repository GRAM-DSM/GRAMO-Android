package com.example.gramoproject.activity.client

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    val BASE_URL = "https://13.209.8.210:5000"
    lateinit var retrofit: Retrofit

    fun getClient(): Retrofit{
            retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

        return retrofit
    }
}