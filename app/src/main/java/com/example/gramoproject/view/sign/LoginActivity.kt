package com.example.gramoproject.view.sign

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gramo.R
import com.example.gramo.Sharedpreferences.SharedPreferencesHelper
import com.example.gramoproject.api.ApiClient
import com.example.gramoproject.view.notice.NoticeActivity
import com.example.gramoproject.model.Login
import com.example.gramoproject.model.LoginUser
import com.example.gramoproject.`interface`.LoginInterface
import kotlinx.android.synthetic.main.login_activity.*
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private var backKeyPressedTime : Long = 0   
    private lateinit var toast : Toast
    private val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        activityInit()

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
        val actionBar = supportActionBar
        actionBar?.hide()

        login_activity.setOnClickListener {
            val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(login_email_et.windowToken, 0)
        }

        if(sharedPreferencesHelper.accessToken?.isNotEmpty() == true){
           noticeIntent()
        }

    }

    private fun login(){
        if(login_email_et.text.toString() == "" || login_pass_et.text.toString() == "")
            login_error_tv.text = getString(R.string.login_input_email_pass)
        else {
            val login = Login(login_email_et.text.toString(), login_pass_et.text.toString())
            val loginInterface = ApiClient.getFlaskClient().create(LoginInterface::class.java)
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

    override fun onBackPressed() {
        //super.onBackPressed()
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) { //2500ms = 2.5s
            backKeyPressedTime = System.currentTimeMillis()
            toast = Toast.makeText(this@LoginActivity, getString(R.string.back_pressed), Toast.LENGTH_SHORT)
            toast.show()
            return
        }
        if(System.currentTimeMillis() <= backKeyPressedTime + 2500){ //2500ms = 2.5s
            finishAffinity()
            toast.cancel()
        }
    }
}