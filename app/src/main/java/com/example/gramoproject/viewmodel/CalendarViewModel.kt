package com.example.gramoproject.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gramoproject.api.ApiClient
import com.example.gramoproject.api.CalendarInterface
import com.example.gramoproject.api.LoginInterface
import com.example.gramoproject.model.PicuList
import com.example.gramoproject.model.PlanList
import com.example.gramoproject.sharedpreferences.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CalendarViewModel : ViewModel() {
    private val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()
    private val _logoutLiveData = MutableLiveData<Int>()
    private val _withDrawLiveData = MutableLiveData<Int>()
    private val _picuLivaData = MutableLiveData<Int>()
    private val _planLiveData = MutableLiveData<Int>()
    val logoutLiveData : LiveData<Int> get() = _logoutLiveData
    val withDrawLiveData : LiveData<Int> get() = _withDrawLiveData
    val picuLiveData : LiveData<Int> get() = _picuLivaData
    val planLiveData : LiveData<Int> get() = _planLiveData
    val picuList = MutableLiveData<PicuList>()
    val planList = MutableLiveData<PlanList>()

    fun getPicu(date : String){
        val picuCall = ApiClient.getClient().create(CalendarInterface::class.java).getPicu("Bearer " + sharedPreferencesHelper.accessToken, date)
        picuCall.enqueue(object : Callback<PicuList> {
            override fun onResponse(call: Call<PicuList>, response: Response<PicuList>) {
                when(response.code()){
                    200 -> if(response.body() != null && response.isSuccessful)
                                picuList.value = response.body()!!
                }
                _picuLivaData.value = response.code()
            }

            override fun onFailure(call: Call<PicuList>, t: Throwable) {
            }

        })
    }

    fun getPlan(date : String){
        val planCall = ApiClient.getClient().create(CalendarInterface::class.java).getPlan("Bearer " + sharedPreferencesHelper.accessToken, date)
        planCall.enqueue(object : Callback<PlanList>{
            override fun onResponse(call: Call<PlanList>, response: Response<PlanList>) {
                when(response.code()){
                    200 -> if(response.body() != null && response.isSuccessful)
                                planList.value = response.body()
                }
                _planLiveData.value = response.code()
            }

            override fun onFailure(call: Call<PlanList>, t: Throwable) {
            }

        })
    }

    fun deletePicu(picuId : Int){
        val delPicuCall = ApiClient.getClient().create(CalendarInterface::class.java).deletePicu("Bearer " + sharedPreferencesHelper.accessToken, picuId)
        delPicuCall.enqueue(object : Callback<Unit>{
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {

            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {

            }

        })
    }

    fun logout(){
        val logoutCall = ApiClient.getFlaskClient().create(LoginInterface::class.java).logout("Bearer " + sharedPreferencesHelper.accessToken)
        logoutCall.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                when (response.code()) {
                    204 -> {
                        sharedPreferencesHelper.accessToken = ""
                        sharedPreferencesHelper.refreshToken = ""
                    }
                }
                _logoutLiveData.value = response.code()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.e("NoticeActivity", t.toString())
            }
        })
    }

    fun withDrawal(){
        val withCall = ApiClient.getFlaskClient().create(LoginInterface::class.java).withDrawal("Bearer " + sharedPreferencesHelper.accessToken)
        withCall.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                when (response.code()) {
                    204 -> {
                        sharedPreferencesHelper.accessToken = ""
                        sharedPreferencesHelper.refreshToken = ""
                    }
                }
                _withDrawLiveData.value = response.code()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.e("NoticeActivity", t.toString())
            }

        })
    }
}