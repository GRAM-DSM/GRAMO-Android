package com.example.gramoproject.view.sign

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.gramo.R
import com.example.gramoproject.sharedpreferences.SharedPreferencesHelper
import com.example.gramo.databinding.LoginActivityBinding
import com.example.gramoproject.activity.sign.RegisterActivity
import com.example.gramoproject.model.Login
import com.example.gramoproject.view.main.MainActivity.Companion.intent
import com.example.gramoproject.view.main.MainActivity.Companion.toast
import com.example.gramoproject.view.notice.NoticeActivity
import com.example.gramoproject.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.login_activity.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginActivityBinding
    private var backKeyPressedTime: Long = 0
    private val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.login_activity)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        activityInit()
        viewModelObserve()

        register_tv.setOnClickListener {
            intent(this@LoginActivity, RegisterActivity::class.java, false)
        }
        login_btn.setOnClickListener {
            login_error_tv.text = ""
            if (login_email_et.text.toString() == "" || login_pass_et.text.toString() == "")
                login_error_tv.text = getString(R.string.login_input_email_pass)
            else {
                val login = Login(login_email_et.text.toString(), login_pass_et.text.toString())
                viewModel.login(login)
            }
        }
    }

    private fun activityInit() {
        val actionBar = supportActionBar
        actionBar?.hide()

        login_activity.setOnClickListener {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(login_email_et.windowToken, 0)
        }

        if (sharedPreferencesHelper.accessToken?.isNotEmpty() == true) {
            intent(this@LoginActivity, NoticeActivity::class.java, true)
        }

    }

    private fun viewModelObserve(){
        viewModel.loginLiveData.observe(this, {
            when (it) {
                201 -> {
                    toast(this@LoginActivity, R.string.login_success, 0)
                    intent(this@LoginActivity, NoticeActivity::class.java, true)
                }
                400, 404 -> login_error_tv.text = getString(R.string.login_not_correct)
            }
        })
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) { //2500ms = 2.5s
            backKeyPressedTime = System.currentTimeMillis()
            toast(this@LoginActivity, R.string.back_pressed, 0)
            return
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) { //2500ms = 2.5s
            finishAffinity()
        }
    }
}