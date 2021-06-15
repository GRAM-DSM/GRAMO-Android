package com.example.gramoproject.api

import com.example.gramoproject.interceptor.TokenAuthenticator
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor

object ApiClient {
    private const val BASE_URI = "http://211.38.86.92:8001"
    private const val BASE_URI2 = "http://13.209.8.210:5000"
    private var retrofit: Retrofit
    private var retrofit_flask : Retrofit

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

        retrofit_flask = Retrofit.Builder()
                .baseUrl(BASE_URI2)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
    }

    fun getClient(): Retrofit {
        return retrofit
    }

    fun getFlaskClient(): Retrofit{
        return retrofit_flask
    }
}