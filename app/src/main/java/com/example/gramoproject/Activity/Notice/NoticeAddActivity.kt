package com.example.gramoproject.activity.notice

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gramo.R
import com.example.gramo.Sharedpreferences.SharedPreferencesHelper
import com.example.gramoproject.activity.client.ApiClient
import com.example.gramoproject.`interface`.NoticeInterface
import com.example.gramoproject.dataclass.NoticeItem
import kotlinx.android.synthetic.main.notice_add_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NoticeAddActivity : AppCompatActivity() {
    val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notice_add_activity)

        //취소 클릭
        notice_cancel_tv.setOnClickListener{
            val intent = Intent(this@NoticeAddActivity, NoticeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
            finish()
        }

        //완료 클릭
        notice_complete_tv.setOnClickListener{
            //제목, 내용이 비어있을 경우 경고문 표시
            if(notice_title_et.text.toString().equals("") || notice_content_et.text.toString().equals("")){
                Toast.makeText(this@NoticeAddActivity, getString(R.string.notice_add_insert), Toast.LENGTH_SHORT).show()
            }
            else {
                val intent = Intent(this@NoticeAddActivity, NoticeActivity::class.java)
                val notice = NoticeItem(notice_title_et.text.toString(), notice_content_et.text.toString())
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                val noticeInterface = ApiClient.getClient().create(NoticeInterface::class.java)
                val call = noticeInterface.createNotice(sharedPreferencesHelper.accessToken!!, notice)
                call.enqueue(object: Callback<Unit> {
                    override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                        when(response.code()){
                            201 -> {
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
}