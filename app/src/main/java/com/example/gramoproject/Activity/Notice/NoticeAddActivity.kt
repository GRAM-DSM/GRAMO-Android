package com.example.gramoproject.Activity.Notice

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gramo.R
import com.example.gramo.Sharedpreferences.SharedPreferencesHelper
import com.example.gramoproject.Activity.Client.ApiClient
import com.example.gramoproject.DataClass.NoticeModel
import com.example.gramoproject.Interface.NoticeInterface
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

        //취소 클릭
        notice_cancel_tv.setOnClickListener{
            val intentToNotice = Intent(applicationContext, NoticeActivity::class.java)
            intentToNotice.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intentToNotice)
            finish()
        }

        //완료 클릭
        notice_complete_tv.setOnClickListener{
            //제목, 내용이 비어있을 경우 경고문 표시
            if(notice_title_et.text.toString().equals("") || notice_content_et.text.toString().equals("")){
                Toast.makeText(applicationContext, "제목 또는 내용을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else {
                val intentToNotice = Intent(applicationContext, NoticeActivity::class.java)
                intentToNotice.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                val noticeInterface = ApiClient.getClient().create(NoticeInterface::class.java)
                val call = noticeInterface.createNotice(sharedPreferencesHelper.accessToken!!, notice_title_et.text.toString(), notice_content_et.text.toString())
                call.enqueue(object: Callback<Unit> {
                    override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    }

                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                        Log.d("NoticeAddActivity", t.toString())
                    }
                })

                startActivity(intentToNotice)
                finish()
            }
        }
    }
}