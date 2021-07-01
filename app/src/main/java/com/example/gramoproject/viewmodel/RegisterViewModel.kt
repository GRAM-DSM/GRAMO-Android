package com.example.gramoproject.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gramoproject.model.EmailAuth
import com.example.gramoproject.model.RegisterUser
import com.example.gramoproject.api.RegisterInterface
import com.example.gramoproject.api.ApiClient
import com.example.gramoproject.sharedpreferences.SharedPreferencesHelper
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {
    private val TAG = "RegisterActivity"
    private val _emailLiveData = MutableLiveData<Int>()
    private val _codeLiveData = MutableLiveData<Int>()
    private val _registerLiveData = MutableLiveData<Int>()

    val emailLiveData: LiveData<Int> get() = _emailLiveData
    val codeLiveData: LiveData<Int> get() = _codeLiveData
    val registerLiveData: LiveData<Int> get() = _registerLiveData

    fun emailAuth(email: String) {
        val registerInterface = ApiClient.getFlaskClient().create(RegisterInterface::class.java)
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
            val registerConfirm = ApiClient.getFlaskClient().create(RegisterInterface::class.java)
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
            ApiClient.getFlaskClient().create(RegisterInterface::class.java).signUp(user)
        registerCall.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                when(response.code()){
                    201 -> getFCMToken()
                }
                _registerLiveData.value = response.code()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.d("RegisterActivity", t.toString())
            }

        })
    }

    fun getFCMToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if(!task.isSuccessful){
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val fcm_token = task.result
            SharedPreferencesHelper.getInstance().fcmToken = fcm_token
        })
    }
}