package com.example.gramoproject.activity.client

import com.example.gramo.Interceptor.TokenAuthenticator
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient{
    private const val BASE_URI = "http://13.209.8.210:5000"
    private var retrofit: Retrofit

    init {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(TokenAuthenticator())
                .build()

        retrofit = Retrofit.Builder()
                .baseUrl(BASE_URI)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
    }

    fun getClient(): Retrofit{
        return retrofit
    }

}