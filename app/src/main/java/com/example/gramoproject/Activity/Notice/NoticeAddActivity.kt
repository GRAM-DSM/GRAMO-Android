package com.example.gramoproject.Activity.Notice

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gramo.R
import kotlinx.android.synthetic.main.notice_add_activity.*
import java.text.SimpleDateFormat
import java.util.*

class NoticeAddActivity : AppCompatActivity() {

    //현재 날짜 저장
    private val format = SimpleDateFormat("yyyy년 MM월 dd일")
    private val now = System.currentTimeMillis()
    private lateinit var date : Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notice_add_activity)

        notice_date_et.setText(getTime())

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
                intentToNotice.putExtra("name", notice_name_et.text.toString())
                intentToNotice.putExtra("date", notice_date_et.text.toString())
                intentToNotice.putExtra("title", notice_title_et.text.toString())
                intentToNotice.putExtra("contents", notice_content_et.text.toString())
                startActivity(intentToNotice)
                finish()
            }
        }

    }

    fun getTime(): String{
        date = Date(now)
        return format.format(date)
    }
}