package com.gram.gramoproject.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gram.gramoproject.model.EmailAuth
import com.gram.gramoproject.model.RegisterUser
import com.gram.gramoproject.api.RegisterInterface
import com.gram.gramoproject.api.ApiClient
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {
    val registerInterface = ApiClient.getFlaskClient().create(RegisterInterface::class.java)
    private val _emailLiveData = MutableLiveData<Int>()
    private val _codeLiveData = MutableLiveData<Int>()
    private val _registerLiveData = MutableLiveData<Int>()

    val emailLiveData: LiveData<Int> get() = _emailLiveData
    val codeLiveData: LiveData<Int> get() = _codeLiveData
    val registerLiveData: LiveData<Int> get() = _registerLiveData

    fun emailAuth(email: String) {
        val emailObject = JsonObject()

        if (email.equals("")) {
            _emailLiveData.value = 0
        } else {
            emailObject.addProperty("email", email)
            val registerAuth = registerInterface.sendEmail(emailObject)
            registerAuth.enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    _emailLiveData.value = response.code()
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.d("RegisterActivity", t.toString())
                }
            })
        }
    }

    fun checkCode(email: String, code: String) {
        if (code == "") {
            _codeLiveData.value = 0
        } else {
            val authInfo = EmailAuth(email, Integer.parseInt(code))
            val registerConfirm = registerInterface
                .checkEmailAuthenticationCode(authInfo)
            registerConfirm.enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    _codeLiveData.value = response.code()

                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.d("RegisterActivity", t.toString())
                }

            })
        }
    }

    fun register(user : RegisterUser){
        val registerCall =
            registerInterface.signUp(user)
        registerCall.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                _registerLiveData.value = response.code()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.d("RegisterActivity", t.toString())
            }

        })
    }
}