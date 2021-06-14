package com.example.gramoproject.view.notice

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gramo.R
import com.example.gramo.Sharedpreferences.SharedPreferencesHelper
import com.example.gramoproject.api.ApiClient
import com.example.gramoproject.`interface`.NoticeInterface
import com.example.gramoproject.activity.notice.NoticeActivity
import com.example.gramoproject.model.NoticeItem
import kotlinx.android.synthetic.main.notice_add_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class NoticeAddActivity : AppCompatActivity() {
    val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notice_add_activity)

        noticeAddInit()

        notice_cancel_tv.setOnClickListener{
            val intent = Intent(this@NoticeAddActivity, NoticeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
            finish()
        }

        notice_complete_tv.setOnClickListener{
            createNotice()
        }
    }
    fun noticeAddInit(){
        val now = System.currentTimeMillis()
        val date = Date(now)
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일")
        val getDate = dateFormat.format(date)

        notice_name_et.text = sharedPreferencesHelper.name
        notice_date_et.text = getDate
    }

    fun createNotice(){
        if(notice_title_et.text.toString().equals("") || notice_content_et.text.toString().equals("")){
            Toast.makeText(this@NoticeAddActivity, getString(R.string.notice_add_insert), Toast.LENGTH_SHORT).show()
        }
        else {
            val intent = Intent(this@NoticeAddActivity, NoticeActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

            val notice = NoticeItem(notice_title_et.text.toString(), notice_content_et.text.toString())
            val noticeInterface = ApiClient.getFlaskClient().create(NoticeInterface::class.java)
            val call = noticeInterface.createNotice("Bearer " + sharedPreferencesHelper.accessToken!!, notice)
            call.enqueue(object: Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    when(response.code()){
                        200 -> {
                            Toast.makeText(this@NoticeAddActivity, getString(R.string.notice_add_success), Toast.LENGTH_SHORT).show()
                            startActivity(intent)
                            finish()
                        }
                        400 -> {
                            Toast.makeText(this@NoticeAddActivity, getString(R.string.notice_add_error), Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.d("NoticeAddActivity", t.toString())
                }
            })


        }
    }
}