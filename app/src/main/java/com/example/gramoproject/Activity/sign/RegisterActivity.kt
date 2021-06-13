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
import com.example.gramoproject.Adapter.HintAdapter
import com.example.gramoproject.DataClass.EmailAuth
import com.example.gramoproject.DataClass.RegisterUser
import com.example.gramoproject.`interface`.RegisterInterface
import com.example.gramoproject.activity.client.ApiClient
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.register_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private val spinnerArray = arrayOf("iOS 개발자", "안드로이드 개발자", "서버 개발자", "디자이너", "분야를 선택해주세요")
    private var authCheck = false
    private var major: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)

        activityInit()
        spinnerInit()

        register_login_tv2.setOnClickListener {
            loginIntent()
        }
        register_back_btn.setOnClickListener {
            loginIntent()
        }

        register_certificate_btn.setOnClickListener {
            register_error_tv.text = ""
            emailAuth()
        }
        register_check_btn.setOnClickListener {
            register_error_tv.text = ""
            checkCode()
        }
        register_register_btn.setOnClickListener {
            register_error_tv.text = ""
            register()
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
        override fun afterTextChanged(s: Editable?) {
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
        val actionBar = supportActionBar
        actionBar?.hide()

        register_activity.setOnClickListener {
            val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(register_name_et.windowToken, 0)
        }

        register_name_et.addTextChangedListener(textWatcher)
        register_email_et.addTextChangedListener(textWatcher)
        register_code_et.addTextChangedListener(textWatcher)
        register_pass_edit2.addTextChangedListener(textWatcher)
        register_passOverlap_edit.addTextChangedListener(textWatcher)
    }

    private fun spinnerInit(){
        val hintAdapter = HintAdapter(this, android.R.layout.simple_list_item_1, spinnerArray)
        register_major_spinner.adapter = hintAdapter
        register_major_spinner.setSelection(hintAdapter.count)
        selectSpinner()

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
                {
                    when(position){
                        0 -> major = "IOS"
                        1 -> major = "ANDROID"
                        2 -> major = "BACKEND"
                        3 -> major = "DESIGN"
                    }
                }
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

        val registerInterface = ApiClient.getFlaskClient().create(RegisterInterface::class.java)
        var emailObject = JsonObject()
        var email = register_email_et.text.toString()

        if (email.equals("")) {
            Toast.makeText(this@RegisterActivity, getString(R.string.register_input_email), Toast.LENGTH_LONG).show()
        } else {
            email = register_email_et.text.toString()
            emailObject.addProperty("email", email)
            val registerAuth = registerInterface.sendEmail(emailObject)
            registerAuth.enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    when (response.code()) {
                        200 -> {
                            register_error_tv.text = ""
                            Toast.makeText(this@RegisterActivity, "$email " + getString(R.string.register_check_emailAuth), Toast.LENGTH_LONG).show()
                        }
                        400 -> {
                            Toast.makeText(this@RegisterActivity, getString(R.string.register_bad_request), Toast.LENGTH_LONG).show()
                            Log.d("RegisterActivity", response.message())
                        }
                        409 -> {
                            register_error_tv.text = getString(R.string.register_already_used_email)
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
            Toast.makeText(this@RegisterActivity, getString(R.string.register_inputCode), Toast.LENGTH_LONG).show()
        } else {
            val authInfo = EmailAuth(register_email_et.text.toString(), Integer.parseInt(register_code_et.text.toString()))
            val registerInterface = ApiClient.getFlaskClient().create(RegisterInterface::class.java)
            val registerConfirm = registerInterface.checkEmailAuthenticationCode(authInfo)

            registerConfirm.enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    when (response.code()) {
                        200 -> {
                            authCheck = true
                            Toast.makeText(this@RegisterActivity, getString(R.string.register_auth_success), Toast.LENGTH_LONG).show()
                        }
                        404, 409 -> {
                            authCheck = false
                            register_error_tv.text = getString(R.string.register_code_not_match)
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
            register_error_tv.text = getString(R.string.register_password_length)
        }
        else if (!register_pass_edit2.text.toString().equals(register_passOverlap_edit.text.toString()))
            register_error_tv.text = getString(R.string.register_password_not_match)
        else if(!authCheck)
            register_error_tv.text = getString(R.string.register_need_email_auth)
        else {
            register_error_tv.text = ""
            val user = RegisterUser(register_email_et.text.toString(), register_pass_edit2.text.toString(), register_name_et.text.toString(), major!!  )
            val registerInterface = ApiClient.getFlaskClient().create(RegisterInterface::class.java)
            val registerCall = registerInterface.signUp(user)

            registerCall.enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {

                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    Toast.makeText(this@RegisterActivity, getString(R.string.register_success), Toast.LENGTH_SHORT).show()
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