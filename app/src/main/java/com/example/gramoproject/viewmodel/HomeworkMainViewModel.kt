package com.example.gramoproject.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gramoproject.api.ApiClient
import com.example.gramoproject.api.HomeworkInterface
import com.example.gramoproject.api.LoginInterface
import com.example.gramoproject.model.HomeworkResponse
import com.example.gramoproject.sharedpreferences.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeworkMainViewModel : ViewModel() {
    private val homeworkInterface = ApiClient.getClient().create(HomeworkInterface::class.java)
    private val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()
    val logoutLiveData = MutableLiveData<Int>()
    val withDrawLiveData = MutableLiveData<Int>()
    val assignLiveData = MutableLiveData<Int>()
    val orderLiveData = MutableLiveData<Int>()
    val submitLiveData = MutableLiveData<Int>()
    val homeworkList = MutableLiveData<List<HomeworkResponse>>()

    fun getHomework() {
        val assignCall = homeworkInterface.getAssignedHomeworkList("Bearer " + sharedPreferencesHelper.accessToken!!)
        val orderCall = homeworkInterface.getOrderedHomeworkList("Bearer " + sharedPreferencesHelper.accessToken!!)
        val submitCall = homeworkInterface.getSubmittedHomeworkList("Bearer " + sharedPreferencesHelper.accessToken!!)

        orderCall.enqueue(object : Callback<List<HomeworkResponse>> {
            override fun onResponse(
                call: Call<List<HomeworkResponse>>,
                response: Response<List<HomeworkResponse>>
            ) {
                when (response.code()) {
                    200 -> {
                        if (response.body() != null && response.isSuccessful) {
                            homeworkList.value = response.body()!!
                        }
                    }
                }
                orderLiveData.value = response.code()
            }

            override fun onFailure(call: Call<List<HomeworkResponse>>, t: Throwable) {
                Log.d("HomeworkMainActivity", t.toString())
            }
        })

        assignCall.enqueue(object : Callback<List<HomeworkResponse>> {
            override fun onResponse(
                call: Call<List<HomeworkResponse>>,
                response: Response<List<HomeworkResponse>>
            ) {
                when (response.code()) {
                    200 -> {
                        if (response.body() != null && response.isSuccessful) {
                            homeworkList.value = response.body()!!
                        }
                    }
                }
                assignLiveData.value = response.code()
            }

            override fun onFailure(call: Call<List<HomeworkResponse>>, t: Throwable) {
                Log.d("HomeworkMainActivity", t.toString())
            }
        })

        submitCall.enqueue(object : Callback<List<HomeworkResponse>> {
            override fun onResponse(
                call: Call<List<HomeworkResponse>>,
                response: Response<List<HomeworkResponse>>
            ) {
                when (response.code()) {
                    200 -> {
                        if (response.body() != null && response.isSuccessful) {
                            homeworkList.value = response.body()!!
                        }
                    }
                }
                submitLiveData.value = response.code()
            }

            override fun onFailure(call: Call<List<HomeworkResponse>>, t: Throwable) {
                Log.d("HomeworkMainActivity", t.toString())
            }
        })
    }

    fun logout() {
        val logoutCall = ApiClient.getFlaskClient().create(LoginInterface::class.java)
            .logout("Bearer " + sharedPreferencesHelper.accessToken)
        logoutCall.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                when (response.code()) {
                    204 -> {
                        sharedPreferencesHelper.accessToken = ""
                        sharedPreferencesHelper.refreshToken = ""
                    }
                }
                logoutLiveData.value = response.code()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.e("NoticeActivity", t.toString())
            }
        })
    }

    fun withDrawal() {
        val withCall = ApiClient.getFlaskClient().create(LoginInterface::class.java)
            .withDrawal("Bearer " + sharedPreferencesHelper.accessToken)
        withCall.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                when (response.code()) {
                    204 -> {
                        sharedPreferencesHelper.accessToken = ""
                        sharedPreferencesHelper.refreshToken = ""
                    }
                }
                withDrawLiveData.value = response.code()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.e("NoticeActivity", t.toString())
            }

        })
    }
}