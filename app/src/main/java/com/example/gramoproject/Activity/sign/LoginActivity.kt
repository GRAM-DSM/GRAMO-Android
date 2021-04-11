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
import com.example.gramoproject.`interface`.LoginInterface
import com.example.gramoproject.activity.client.ApiClient
import com.example.gramoproject.activity.notice.NoticeActivity
import com.example.gramoproject.dataclass.Login
import com.example.gramoproject.dataclass.TokenModel
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
    }

    private fun login(){
        //빈칸 확인
        if(login_email_et.text.toString() == "" || login_pass_et.text.toString() == "")
            login_error_tv.text = "이메일 또는 비밀번호를 입력해주세요."
        else {
            val login = Login(login_email_et.text.toString(), login_pass_et.text.toString())
            val loginInterface = ApiClient.getClient().create(LoginInterface::class.java)
            var loginCall = loginInterface.signIn(login)

            loginCall.enqueue(object: Callback<TokenModel> {
                override fun onResponse(call: Call<TokenModel>, response: Response<TokenModel>) {
                    when(response.code()){
                        201 -> {
                            try {
                                val saveAccess = response.body()?.access_token
                                val saveRefresh = response.body()?.refresh_token

                                Log.i("LoginActivity", saveAccess!!)
                                Log.i("LoginActivity", saveRefresh!!)

                                sharedPreferencesHelper.accessToken = saveAccess
                                sharedPreferencesHelper.refreshToken = saveRefresh

                                Toast.makeText(this@LoginActivity, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@LoginActivity, NoticeActivity::class.java)
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)

                            } catch (e: JSONException){
                                e.printStackTrace()
                            }
                        }
                        400, 404 -> {
                            login_error_tv.text = "아이디 또는 비밀번호가 올바르지 않습니다"
                        }
                    }
                }

                override fun onFailure(call: Call<TokenModel>, t: Throwable) {
                    Log.d("LoginActivity", t.toString())
                }
            })
        }
    }
}