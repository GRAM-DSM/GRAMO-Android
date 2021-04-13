package com.example.gramo.Interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import com.example.gramo.Sharedpreferences.SharedPreferencesHelper
import com.example.gramoproject.`interface`.LoginInterface
import com.example.gramoproject.activity.client.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TokenAuthenticator : Interceptor {

    private val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        when(response.code){
            401 -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val refreshToken = SharedPreferencesHelper.getInstance().refreshToken
                    if(refreshToken != null){
                        getAccessToken(refreshToken)
                    }
                }
            }
        }

        return response
    }

    private suspend fun getAccessToken(refreshToken: String){
        val token = withContext(Dispatchers.IO){
            ApiClient.getClient().create(LoginInterface::class.java).tokenRefresh(refreshToken)
        }

        if(token.isSuccessful){
            if(token.code() == 200){
                sharedPreferencesHelper.accessToken = "Bearer " + token.body()
            } else{
                Log.e("TokenAuthenticator", "알 수 없는 오류")
            }
        } else{
            Log.e("TokenAuthenticator", token.message())
        }
    }
}