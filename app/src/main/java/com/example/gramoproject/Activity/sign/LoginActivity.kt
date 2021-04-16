package com.example.gramoproject.activity.sign

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gramo.R
import com.example.gramo.Sharedpreferences.SharedPreferencesHelper
import com.example.gramoproject.activity.client.ApiClient
import com.example.gramoproject.activity.notice.NoticeActivity
import com.example.gramoproject.activity.sign.RegisterActivity
import com.example.gramoproject.DataClass.Login
import com.example.gramoproject.DataClass.LoginUser
import com.example.gramoproject.`interface`.LoginInterface
import kotlinx.android.synthetic.main.login_activity.*
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        login_btn.setOnClickListener{
            login_error_tv.text = ""
            login()
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

        if(sharedPreferencesHelper.accessToken!!.isNotEmpty()){
          noticeIntent()
        }

    }

    private fun login(){
        //빈칸 확인
        if(login_email_et.text.toString() == "" || login_pass_et.text.toString() == "")
            login_error_tv.text = getString(R.string.login_input_email_pass)
        else {
            val login = Login(login_email_et.text.toString(), login_pass_et.text.toString())
            val loginInterface = ApiClient.getClient().create(LoginInterface::class.java)
            var loginCall = loginInterface.signIn(login)

            loginCall.enqueue(object: Callback<LoginUser> {
                override fun onResponse(call: Call<LoginUser>, response: Response<LoginUser>) {
                    when(response.code()){
                        201 -> {
                            try {
                                val saveAccess = response.body()?.access_token
                                val saveRefresh = response.body()?.refresh_token
                                val saveName = response.body()?.name
                                val saveMajor = response.body()?.major

                                Log.i("LoginActivity", saveAccess!!)
                                Log.i("LoginActivity", saveRefresh!!)

                                sharedPreferencesHelper.accessToken = saveAccess
                                sharedPreferencesHelper.refreshToken = saveRefresh
                                sharedPreferencesHelper.name = saveName
                                sharedPreferencesHelper.major = saveMajor

                                Toast.makeText(this@LoginActivity, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                                noticeIntent()

                            } catch (e: JSONException){
                                e.printStackTrace()
                            }
                        }
                        400, 404 -> {
                            login_error_tv.text = getString(R.string.login_not_correct).toString()
                        }
                    }
                }

                override fun onFailure(call: Call<LoginUser>, t: Throwable) {
                    Log.d("LoginActivity", t.toString())
                }
            })
        }
    }

    private fun noticeIntent(){
        val intent = Intent(this@LoginActivity, NoticeActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    //뒤로 가기 버튼 오버라이드
    override fun onBackPressed() {
        //super.onBackPressed()
        //마지막으로 뒤로가기를 누른 후 2.5초가 지났을 경우
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) { //2500ms = 2.5s
            backKeyPressedTime = System.currentTimeMillis()
            toast = Toast.makeText(this@LoginActivity, getString(R.string.back_pressed), Toast.LENGTH_SHORT)
            toast.show()
            return
        }

        //마지막으로 뒤로가기를 누른 후 2.5초가 지나지 않았을 경우
        if(System.currentTimeMillis() <= backKeyPressedTime + 2500){ //2500ms = 2.5s
            finishAffinity()
            toast.cancel()
        }
    }
}