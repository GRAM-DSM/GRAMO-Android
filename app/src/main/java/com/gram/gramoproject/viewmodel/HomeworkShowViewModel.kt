package com.gram.gramoproject.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gram.gramoproject.api.ApiClient
import com.gram.gramoproject.api.HomeworkInterface
import com.gram.gramoproject.model.HomeworkContentResponseData
import com.gram.gramoproject.sharedpreferences.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeworkShowViewModel : ViewModel() {
    private val homeworkInterface = ApiClient.getClient().create(HomeworkInterface::class.java)
    private val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()
    val homeworkContent = MutableLiveData<HomeworkContentResponseData>()
    val contentLiveData = MutableLiveData<Int>()
    val deleteLiveData = MutableLiveData<Int>()
    val submitLiveData = MutableLiveData<Int>()
    val rejectLiveData = MutableLiveData<Int>()

    fun getHomeworkContent(homeworkID: Int) {
        homeworkInterface.getHomeworkContent(
            homeworkID,
            "Bearer " + sharedPreferencesHelper.accessToken!!
        )
            .enqueue(object : Callback<HomeworkContentResponseData> {
                override fun onResponse(
                    call: Call<HomeworkContentResponseData>,
                    response: Response<HomeworkContentResponseData>
                ) {
                    when (response.code()) {
                        200 -> {
                            if (response.body() != null && response.isSuccessful) {
                                homeworkContent.value = response.body()!!
                            }
                        }
                    }
                    contentLiveData.value = response.code()
                }

                override fun onFailure(call: Call<HomeworkContentResponseData>, t: Throwable) {
                    Log.d("HomeworkShowActivity", t.toString())
                }
            })

    }

    fun deleteHomework(homeworkID: Int) {
        homeworkInterface.deleteHomework(
            "Bearer " + sharedPreferencesHelper.accessToken!!,
            homeworkID
        ).enqueue(object : Callback<Unit> {
            override fun onResponse(
                call: Call<Unit>,
                response: Response<Unit>
            ) {
                deleteLiveData.value = response.code()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.d("HomeworkShowActivity", t.toString())
            }

        })

    }

    fun submitHomework(homeworkID: Int) {
        homeworkInterface.submitHomework(
            "Bearer " + sharedPreferencesHelper.accessToken!!,
            homeworkID
        ).enqueue(object : Callback<Unit> {
            override fun onResponse(
                call: Call<Unit>,
                response: Response<Unit>
            ) {
                submitLiveData.value = response.code()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.d("HomeworkShowActivity", t.toString())
            }

        })
    }

    fun rejectHomework(homeworkID: Int) {
        homeworkInterface.rejectHomework(
            "Bearer " + sharedPreferencesHelper.accessToken!!,
            homeworkID
        ).enqueue(object : Callback<Unit> {
            override fun onResponse(
                call: Call<Unit>,
                response: Response<Unit>
            ) {
                rejectLiveData.value = response.code()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.d("HomeworkShowActivity", t.toString())
            }

        })
    }
}