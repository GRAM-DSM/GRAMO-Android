package com.example.gramoproject.Activity.Client

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    val BASE_URI = ""
    lateinit var retrofit: Retrofit

    fun getClient(): Retrofit{
        if(retrofit == null){
            retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URI)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }

        return retrofit
    }
}