package com.example.gramoproject.viewmodel

import android.app.Activity
import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.gramo.Sharedpreferences.SharedPreferencesHelper
import com.example.gramoproject.model.GetDetailNotice
import com.example.gramoproject.model.NoticeList
import com.example.gramoproject.`interface`.LoginInterface
import com.example.gramoproject.`interface`.NoticeInterface
import com.example.gramoproject.api.ApiClient
import com.example.gramoproject.adapter.NoticeRecyclerAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NoticeViewModel(application: Application) : AndroidViewModel(application) {
    private val noticeInterface = ApiClient.getFlaskClient().create(NoticeInterface::class.java)
    private val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()
    private val context = getApplication<Application>().applicationContext
    val noticeLiveData = MutableLiveData<Int>()
    val itemLiveData = MutableLiveData<Int>()
    val loadMoreLiveData = MutableLiveData<Int>()
    val removeLiveData = MutableLiveData<Int>()
    val noticeList = MutableLiveData<NoticeList>()
    val noticeDetail = MutableLiveData<GetDetailNotice>()
    val logoutLiveData = MutableLiveData<Int>()
    val withDrawLiveData = MutableLiveData<Int>()
    var isNext = false
    var isLoading = false
    var off_set = -10
    val limit_num = 10

    fun getNotice(){
        val recyclerCall = noticeInterface.getNoticeList("Bearer " + sharedPreferencesHelper.accessToken!!, getOffSet(), limit_num)

        recyclerCall.enqueue(object : Callback<NoticeList> {
            override fun onResponse(call: Call<NoticeList>, response: Response<NoticeList>) {
                when (response.code()) {
                    200 -> {
                        if (response.body() != null && response.isSuccessful) {
                            noticeList.value = response.body()!!
                            isNext = response.body()!!.next_page
                        }
                    }
                }
                noticeLiveData.value = response.code()
            }

            override fun onFailure(call: Call<NoticeList>, t: Throwable) {
                Log.d("NoticeActivity", t.toString())
            }
        })
    }

    fun rvItemClick(id : Int){
        val bottomSheetCall = noticeInterface.getNoticeDetail("Bearer " + sharedPreferencesHelper.accessToken!!, id)
        bottomSheetCall.enqueue(object : Callback<GetDetailNotice> {
            override fun onResponse(call: Call<GetDetailNotice>, response: Response<GetDetailNotice>) {
                when (response.code()) {
                    200 -> noticeDetail.value = response.body()!!
                }
                itemLiveData.value = response.code()
            }
            override fun onFailure(call: Call<GetDetailNotice>, t: Throwable) {
                Log.d("NoticeActivity", t.toString())
            }

        })
    }

    fun loadMore(adapter: NoticeRecyclerAdapter){
        (context as Activity).runOnUiThread {
            noticeList.value!!.notice.add(null)
            adapter.notifyItemInserted(noticeList.value!!.notice.size - 1)
            val handler = Handler(Looper.getMainLooper())
            val call = noticeInterface.getNoticeList("Bearer " + sharedPreferencesHelper.accessToken!!, getOffSet(), limit_num)

            handler.postDelayed(object : Runnable {
                override fun run() {
                    call.enqueue(object : Callback<NoticeList> {
                        override fun onResponse(call: Call<NoticeList>, response: Response<NoticeList>) {
                            when (response.code()) {
                                200 -> {
                                    noticeList.value!!.notice.removeAt(noticeList.value!!.notice.size - 1)
                                    adapter.notifyItemRemoved(noticeList.value!!.notice.size)

                                    val getDataMore: NoticeList? = response.body()
                                    if (getDataMore != null && response.isSuccessful) {
                                        isNext = getDataMore.next_page
                                        noticeList.value!!.notice.addAll(getDataMore.notice)
                                    }
                                    adapter.notifyDataSetChanged()
                                    isLoading = false
                                }
                            }
                            loadMoreLiveData.value = response.code()
                        }

                        override fun onFailure(call: Call<NoticeList>, t: Throwable) {
                            Log.d("NoticeActivity", t.toString())
                        }

                    })
                }

            }, 1000)
        }
    }

    fun noticeRemove(position : Int, id : Int, adapter : NoticeRecyclerAdapter){
        val dismissCall = noticeInterface.deleteNotice("Bearer " + sharedPreferencesHelper.accessToken!!, id)
        dismissCall.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                when(response.code()){
                    204 -> adapter.removeItem(position)
                }
                removeLiveData.value = response.code()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.d("NoticeActivity", t.toString())
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
                logoutLiveData.value = response.code()
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
                withDrawLiveData.value = response.code()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.e("NoticeActivity", t.toString())
            }

        })
    }

    private fun getOffSet(): Int {
        off_set += limit_num
        return off_set
    }

}