package com.gram.gramoproject.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gram.gramoproject.sharedpreferences.SharedPreferencesHelper
import com.gram.gramoproject.model.GetDetailNotice
import com.gram.gramoproject.model.NoticeList
import com.gram.gramoproject.api.LoginInterface
import com.gram.gramoproject.api.NoticeInterface
import com.gram.gramoproject.api.ApiClient
import com.gram.gramoproject.adapter.NoticeRecyclerAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NoticeViewModel : ViewModel() {
    private val noticeInterface = ApiClient.getFlaskClient().create(NoticeInterface::class.java)
    private val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()
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
    var page = 0

    fun getNotice() {
        val recyclerCall = noticeInterface.getNoticeList(
            "Bearer " + sharedPreferencesHelper.accessToken!!,
            getNoticePage()
        )

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

    fun rvItemClick(id: Int) {
        val bottomSheetCall =
            noticeInterface.getNoticeDetail("Bearer " + sharedPreferencesHelper.accessToken!!, id)
        bottomSheetCall.enqueue(object : Callback<GetDetailNotice> {
            override fun onResponse(
                call: Call<GetDetailNotice>,
                response: Response<GetDetailNotice>
            ) {
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

    fun loadMore(adapter: NoticeRecyclerAdapter) {
        val call = noticeInterface.getNoticeList(
            "Bearer " + sharedPreferencesHelper.accessToken!!,
            getNoticePage()
        )
        call.enqueue(object : Callback<NoticeList> {
            override fun onResponse(call: Call<NoticeList>, response: Response<NoticeList>) {
                when (response.code()) {
                    200 -> {
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

    fun noticeRemove(position: Int, id: Int, adapter: NoticeRecyclerAdapter) {
        val dismissCall =
            noticeInterface.deleteNotice("Bearer " + sharedPreferencesHelper.accessToken!!, id)
        dismissCall.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                when (response.code()) {
                    200 -> adapter.removeItem(position)
                }
                removeLiveData.value = response.code()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.d("NoticeActivity", t.toString())
            }
        })
    }

    fun logout() {
        val logoutCall = ApiClient.getFlaskClient().create(LoginInterface::class.java)
            .logout("Bearer " + sharedPreferencesHelper.accessToken)
        logoutCall.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                when (response.code()) {
                    200 -> {
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
                    200 -> {
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

    private fun getNoticePage(): Int {
        return page++
    }

}