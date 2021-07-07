package com.gram.gramoproject.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gram.gramoproject.sharedpreferences.SharedPreferencesHelper
import com.gram.gramoproject.model.NoticeItem
import com.gram.gramoproject.api.NoticeInterface
import com.gram.gramoproject.api.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class NoticeAddViewModel : ViewModel() {
    val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()
    var liveData = MutableLiveData<Int>()

    val now = System.currentTimeMillis()
    val date = Date(now)
    val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일")
    val getDate = dateFormat.format(date)

    fun createNotice(notice : NoticeItem){
        val noticeInterface = ApiClient.getFlaskClient().create(NoticeInterface::class.java)
        val call = noticeInterface.createNotice("Bearer " + sharedPreferencesHelper.accessToken!!, notice)
        call.enqueue(object: Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                liveData.value = response.code()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.d("NoticeAddActivity", t.toString())
            }
        })
    }
}