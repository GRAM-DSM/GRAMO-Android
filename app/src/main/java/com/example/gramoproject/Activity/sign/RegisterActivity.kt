package com.example.gramoproject.activity.sign

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gramo.R
import com.example.gramo.Sharedpreferences.SharedPreferencesHelper
import com.example.gramoproject.`interface`.RegisterInterface
import com.example.gramoproject.activity.client.ApiClient
import com.example.gramoproject.adapter.HintAdapter
import com.example.gramoproject.dataclass.EmailAuth
import com.example.gramoproject.dataclass.UserModel
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.register_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private val spinnerArray = arrayOf("iOS 개발자", "안드로이드 개발자", "서버 개발자", "디자이너", "분야를 선택해주세요")
    private var authCheck = false //이메일 코드 인증 여부
    private var major: String? = null //전공을 담기 위한 변수
    private val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)

        activityInit()
        spinnerInit()

        //로그인 버튼 클릭시 로그인 페이지로 이동
        register_login_tv2.setOnClickListener {
            loginIntent()
        }
        register_back_btn.setOnClickListener {
            loginIntent()
        }

        //이메일 인증 버튼
        register_certificate_btn.setOnClickListener {
            register_error_tv.text = ""
            emailAuth()
        }
        //인증 확인 버튼
        register_check_btn.setOnClickListener {
            register_error_tv.text = ""
            checkCode()
        }
        //회원가입 버튼 클릭
        register_register_btn.setOnClickListener {
            register_error_tv.text = ""
            register()
        }
    }

    //EditText 입력 변화
    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // 입력하기 전에 조치
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // 입력란에 변화가 있을 시 조치
        }

        override fun afterTextChanged(s: Editable?) {
            // 입력이 끝났을 때 조치

            if (!register_name_et.text.toString().equals("") && !register_email_et.text.toString().equals("") && !register_code_et.text.toString().equals("") &&
                    !register_pass_edit2.text.toString().equals("") && !register_passOverlap_edit.text.toString().equals("") && major != null) {
                register_register_btn.isEnabled = true
                register_register_btn.setBackgroundColor(Color.parseColor("#112D4E"))
            } else {
                register_register_btn.isEnabled = false
                register_register_btn.setBackgroundColor(Color.parseColor("#909090"))
            }
        }
    }

    private fun activityInit(){
        register_register_btn.isEnabled = false

        //타이틀바 제거
        val actionBar = supportActionBar
        actionBar?.hide()

        //배경 터치 시 키보드 내리기
        register_activity.setOnClickListener {
            val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(register_name_et.windowToken, 0)
        }

        //editText입력 변화 이벤트 받기
        register_name_et.addTextChangedListener(textWatcher)
        register_email_et.addTextChangedListener(textWatcher)
        register_code_et.addTextChangedListener(textWatcher)
        register_pass_edit2.addTextChangedListener(textWatcher)
        register_passOverlap_edit.addTextChangedListener(textWatcher)
    }

    private fun spinnerInit(){
        //스피너 설정
        val hintAdapter = HintAdapter(this, android.R.layout.simple_list_item_1, spinnerArray)
        register_major_spinner.adapter = hintAdapter
        register_major_spinner.setSelection(hintAdapter.count)
        selectSpinner()

        //스피너 선택시 가상 키보드 내리기
        register_major_spinner.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                val imm = this@RegisterActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(register_email_et.windowToken, 0)
                return false
            }
        })
    }

    private fun selectSpinner() {
        register_major_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position != 4)
                    major = spinnerArray[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }

    private fun loginIntent(){
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun emailAuth(){
        val imm = this@RegisterActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(register_email_et.windowToken, 0)

        val registerInterface = ApiClient.getClient().create(RegisterInterface::class.java)
        var emailObject = JsonObject()
        var email = register_email_et.text.toString()

        if (email.equals("")) {
            Toast.makeText(this@RegisterActivity, R.string.register_input_email, Toast.LENGTH_SHORT).show()
        } else {
            email = register_email_et.text.toString()
            emailObject.addProperty("email", email)
            val registerAuth = registerInterface.sendEmail(emailObject)
            registerAuth.enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    when (response.code()) {
                        200 -> {
                            register_error_tv.text = ""
                            Toast.makeText(this@RegisterActivity, "$email " + R.string.register_check_emailAuth, Toast.LENGTH_SHORT).show()
                        }
                        400 -> {
                            Toast.makeText(this@RegisterActivity, R.string.register_bad_request, Toast.LENGTH_SHORT).show()
                            Log.d("RegisterActivity", response.message())
                        }
                        409 -> {
                            register_error_tv.text = R.string.register_already_used_email.toString()
                        }
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.d("RegisterActivity", t.toString())
                }

            })
        }
    }

    private fun checkCode(){
        val imm = this@RegisterActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(register_email_et.windowToken, 0)

        if (register_code_et.text.toString() == "") {
            Toast.makeText(this@RegisterActivity, R.string.register_inputCode, Toast.LENGTH_SHORT).show()
        } else {
            val authInfo = EmailAuth(register_email_et.text.toString(), Integer.parseInt(register_code_et.text.toString()))
            val registerInterface = ApiClient.getClient().create(RegisterInterface::class.java)
            val registerConfirm = registerInterface.checkEmailAuthenticationCode(authInfo)

            registerConfirm.enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    when (response.code()) {
                        200 -> {
                            authCheck = true
                            Toast.makeText(this@RegisterActivity, R.string.register_auth_success, Toast.LENGTH_SHORT).show()
                        }
                        404, 409 -> {
                            authCheck = false
                            register_error_tv.text = R.string.register_code_not_match.toString()
                        }
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.d("RegisterActivity", t.toString())
                }

            })
        }
    }

    private fun register(){
        if (register_pass_edit2.text.length < 5 || register_pass_edit2.text.length > 20){
            register_error_tv.text = R.string.register_password_length.toString()
        }
        else if (!register_pass_edit2.text.toString().equals(register_passOverlap_edit.text.toString()))
            register_error_tv.text = R.string.register_code_not_match.toString()
        else {
            register_error_tv.text = ""
            val user = UserModel(register_email_et.text.toString(), register_pass_edit2.text.toString(), register_name_et.text.toString(), major.toString())
            val registerInterface = ApiClient.getClient().create(RegisterInterface::class.java)
            val registerCall = registerInterface.signUp(user)

            registerCall.enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    sharedPreferencesHelper.name = register_name_et.text.toString()
                    sharedPreferencesHelper.major = major

                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    Toast.makeText(this@RegisterActivity, R.string.register_success, Toast.LENGTH_SHORT).show()
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.d("RegisterActivity", t.toString())
                }

            })
        }
    }
}