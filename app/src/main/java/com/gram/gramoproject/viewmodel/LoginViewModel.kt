package com.gram.gramoproject.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gram.gramoproject.sharedpreferences.SharedPreferencesHelper
import com.gram.gramoproject.model.Login
import com.gram.gramoproject.model.LoginUser
import com.gram.gramoproject.api.LoginInterface
import com.gram.gramoproject.api.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {
    private val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()
    private val _loginLiveData = MutableLiveData<Int>()
    val loginLiveData : LiveData<Int> get() = _loginLiveData

    fun login(login : Login){
        val loginCall = ApiClient.getFlaskClient().create(LoginInterface::class.java).signIn(login)
        loginCall.enqueue(object: Callback<LoginUser> {
            override fun onResponse(call: Call<LoginUser>, response: Response<LoginUser>) {
                _loginLiveData.value = response.code()
                when(response.code()){
                    200 -> managePref(response)
                }
            }

            override fun onFailure(call: Call<LoginUser>, t: Throwable) {
                Log.d("LoginActivity", t.toString())
            }
        })
    }

    private fun managePref(user : Response<LoginUser>){
        sharedPreferencesHelper.accessToken = user.body()!!.access_token
        sharedPreferencesHelper.refreshToken = user.body()!!.refresh_token
        sharedPreferencesHelper.name = user.body()!!.name
        sharedPreferencesHelper.major = user.body()!!.major
    }
}