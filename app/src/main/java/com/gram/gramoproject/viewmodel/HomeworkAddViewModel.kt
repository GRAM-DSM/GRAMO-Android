package com.gram.gramoproject.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gram.gramoproject.api.ApiClient
import com.gram.gramoproject.api.HomeworkInterface
import com.gram.gramoproject.model.HomeworkBodyData
import com.gram.gramoproject.model.HomeworkedUserData
import com.gram.gramoproject.sharedpreferences.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeworkAddViewModel : ViewModel() {
    private val homeworkInterface = ApiClient.getClient().create(HomeworkInterface::class.java)
    private val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()
    private val _userLiveDate = MutableLiveData<Int>()
    private val _homeworkLiveDate = MutableLiveData<Int>()
    val userList = MutableLiveData<HomeworkedUserData>()
    val userLiveData: LiveData<Int> get() = _userLiveDate
    val homeworkLiveData: LiveData<Int> get() = _homeworkLiveDate

    fun getUserList() {
        homeworkInterface.getUserList("Bearer " + sharedPreferencesHelper.accessToken!!)
            .enqueue(object : Callback<HomeworkedUserData> {
                override fun onResponse(
                    call: Call<HomeworkedUserData>,
                    response: Response<HomeworkedUserData>
                ) {
                    when (response.code()) {
                        200 -> if (response.body() != null && response.isSuccessful)
                            userList.value = response.body()
                    }
                    _userLiveDate.value = response.code()
                }

                override fun onFailure(call: Call<HomeworkedUserData>, t: Throwable) {
                    Log.d("HomeworkAddActivity", t.toString())
                }
            })
    }

    fun createHomework(homework: HomeworkBodyData) {
        homeworkInterface.createHomework(
            "Bearer " + sharedPreferencesHelper.accessToken!!,
            homework
        ).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                _homeworkLiveDate.value = response.code()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.d("HomeworkAddActivity", t.toString())
            }
        })
    }
}