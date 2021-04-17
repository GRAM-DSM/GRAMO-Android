package com.example.gramo.Interceptor

import android.app.Activity
import android.util.Log
import androidx.core.app.ActivityCompat
import okhttp3.Interceptor
import okhttp3.Response
import com.example.gramo.Sharedpreferences.SharedPreferencesHelper
import com.example.gramoproject.DataClass.TokenRefresh
import com.example.gramoproject.`interface`.LoginInterface
import com.example.gramoproject.activity.client.ApiClient
import com.example.gramoproject.activity.notice.NoticeActivity
import retrofit2.Call
import retrofit2.Callback


class TokenAuthenticator : Interceptor {

    private val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        when(response.code){
            401 -> {
                if(NoticeActivity.logoutCheck == false) {
                    val refreshToken = "Bearer " + SharedPreferencesHelper.getInstance().refreshToken
                    if (refreshToken != null) {
                        getAccessToken(refreshToken)
                    }
                }
            }
        }
        return response
    }

    private fun getAccessToken(refreshToken: String){
        val token = ApiClient.getClient().create(LoginInterface::class.java).tokenRefresh(refreshToken)

        token.enqueue(object: Callback<TokenRefresh>{
            override fun onResponse(call: Call<TokenRefresh>, response: retrofit2.Response<TokenRefresh>) {
                when(response.code()){
                    201 -> {
                        val saveAccess = response.body()!!.access_token
                        sharedPreferencesHelper.accessToken = saveAccess
                    }
                    else -> {
                        Log.e("TokenAuthenticator", "알 수 없는 오류")
                    }
                }
            }

            override fun onFailure(call: Call<TokenRefresh>, t: Throwable) {
                Log.e("TokenAuthenticator", t.message.toString())
            }

        })
    }
}