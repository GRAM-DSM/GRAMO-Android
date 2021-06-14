package com.example.gramoproject.activity.sign

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.gramo.R
import com.example.gramo.databinding.RegisterActivityBinding
import com.example.gramoproject.adapter.HintAdapter
import com.example.gramoproject.model.RegisterUser
import com.example.gramoproject.view.main.MainActivity.Companion.intent
import com.example.gramoproject.view.main.MainActivity.Companion.toast
import com.example.gramoproject.view.sign.LoginActivity
import com.example.gramoproject.viewmodel.RegisterViewModel
import kotlinx.android.synthetic.main.register_activity.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: RegisterActivityBinding
    private val spinnerArray = arrayOf("iOS 개발자", "안드로이드 개발자", "서버 개발자", "디자이너", "분야를 선택해주세요")
    private val viewModel: RegisterViewModel by viewModels()
    private var authCheck = false
    private var major: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.register_activity)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        activityInit()
        spinnerInit()
        viewModelObserve()

        register_login_tv2.setOnClickListener {
            intent(this@RegisterActivity, LoginActivity::class.java, true)
        }
        register_back_btn.setOnClickListener {
            intent(this@RegisterActivity, LoginActivity::class.java, true)
        }

        register_register_btn.setOnClickListener {
            register_error_tv.text = ""
            if (register_pass_edit2.text.length < 5 || register_pass_edit2.text.length > 20)
                register_error_tv.text = getString(R.string.register_password_length)
            else if (!register_pass_edit2.text.toString()
                    .equals(register_passOverlap_edit.text.toString())
            )
                register_error_tv.text = getString(R.string.register_password_not_match)
            else {
                viewModel.register(
                    RegisterUser(
                        register_email_et.text.toString(), register_pass_edit2.text.toString(),
                        register_name_et.text.toString(), major!!
                    )
                )
            }
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            if (!register_name_et.text.toString().equals("") && !register_email_et.text.toString()
                    .equals("") && !register_code_et.text.toString().equals("") &&
                !register_pass_edit2.text.toString()
                    .equals("") && !register_passOverlap_edit.text.toString()
                    .equals("") && major != null
            ) {
                register_register_btn.isEnabled = true
                register_register_btn.setBackgroundColor(Color.parseColor("#112D4E"))
            } else {
                register_register_btn.isEnabled = false
                register_register_btn.setBackgroundColor(Color.parseColor("#909090"))
            }
        }
    }

    private fun viewModelObserve(){
        viewModel.emailLiveData.observe(this, {
            when (it) {
                0 -> toast(this@RegisterActivity, R.string.register_input_email, 1)
                200 -> {
                    register_error_tv.text = ""
                    toast(this@RegisterActivity, R.string.register_check_emailAuth, 1)
                }
                400 -> toast(this@RegisterActivity, R.string.register_bad_request, 1)
                409 -> register_error_tv.text = getString(R.string.register_already_used_email)
            }
        })
        viewModel.codeLiveData.observe(this, {
            when (it) {
                0 -> toast(this@RegisterActivity, R.string.register_inputCode, 1)
                200 -> {
                    authCheck = true
                    toast(this@RegisterActivity, R.string.register_auth_success, 1)
                }
                404 -> register_error_tv.text = getString(R.string.register_code_not_match)
                409 -> register_error_tv.text = getString(R.string.register_code_email_not_match)
            }
        })
        viewModel.registerLiveData.observe(this, {
            when (it) {
                201 -> {
                    toast(this@RegisterActivity, R.string.register_success, 0)
                    intent(this@RegisterActivity, LoginActivity::class.java, true)
                }
                400 -> toast(this@RegisterActivity, R.string.register_error, 0)

            }
        })
    }

    private fun activityInit() {
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

    private fun spinnerInit() {
        val hintAdapter = HintAdapter(this, android.R.layout.simple_list_item_1, spinnerArray)
        register_major_spinner.adapter = hintAdapter
        register_major_spinner.setSelection(hintAdapter.count)
        selectSpinner()

        register_major_spinner.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                val imm =
                    this@RegisterActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(register_email_et.windowToken, 0)
                return false
            }
        })
    }

    private fun selectSpinner() {
        register_major_spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position != 4) {
                        when (position) {
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
}