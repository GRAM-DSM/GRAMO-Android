package com.example.gramoproject.activity.sign

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gramo.R
import com.example.gramo.Sharedpreferences.SharedPreferencesHelper
import kotlinx.android.synthetic.main.login_activity.*

class LoginActivity : AppCompatActivity() {

    //마지막으로 뒤로 가기 버튼을 누른 시간 저장
    private var backKeyPressedTime : Long = 0
    private lateinit var toast : Toast
    private val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        //액티비티 설정
        activityInit()

        //회원가입 클릭시 화면 이동
        register_tv.setOnClickListener{
            val intentToRegister = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intentToRegister)
        }
    }

    private fun activityInit(){
        //타이틀바 제거
        val actionBar = supportActionBar
        actionBar?.hide()

        //배경 터치 시 키보드 내리기
        login_activity.setOnClickListener {
            val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(login_email_et.windowToken, 0)
        }
    }
}