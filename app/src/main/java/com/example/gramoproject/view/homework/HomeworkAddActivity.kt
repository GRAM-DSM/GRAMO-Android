package com.example.gramoproject.view.homework

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.gramo.R
import com.example.gramo.Sharedpreferences.SharedPreferencesHelper
import com.example.gramoproject.adapter.AssignorAdapter
import com.example.gramo.model.UserResponse
import com.example.gramoproject.api.ApiClient
import com.example.gramoproject.model.HomeworkBodyData
import com.example.gramoproject.model.HomeworkedUserData
import com.example.gramoproject.api.HomeworkInterface
import kotlinx.android.synthetic.main.homework_add_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeworkAddActivity : AppCompatActivity() {

    var studentItems: List<UserResponse> = listOf()
    var userInfo: UserResponse? = null
    private val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homework_add_activity)

        var major = ""
        val builder = AlertDialog.Builder(this)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatted = LocalDateTime.now().format(formatter)
        val calendar = java.util.Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val majorItems = resources.getStringArray(R.array.majorList)
        val majorAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, majorItems)

        hmwk_name_tv.text = SharedPreferencesHelper.getInstance().name

        hmwk_date_tv.text = formatted

        hmwk_major_spinner.setSelection(0)

        hmwk_major_spinner.adapter = majorAdapter

        hmwk_major_spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                major = majorItems[position]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        val datePickerDialog = DatePickerDialog(this, { _, i, i2, i3 ->

            hmwk_endDate_tv.text = resources.getString(R.string.set_date, i, i2 + 1, i3)
        }, year, month, day)

        hmwk_endDate_tv.setOnClickListener {
            datePickerDialog.show()
        }

        val userResponse = ApiClient.getClient().create(HomeworkInterface::class.java)
        userResponse.getUserList("Bearer " + sharedPreferencesHelper.accessToken!!).enqueue(object : Callback<HomeworkedUserData> {
            override fun onResponse(
                call: Call<HomeworkedUserData>,
                response: Response<HomeworkedUserData>
            ) {
                if (response.isSuccessful) {
                    studentItems = response.body()?.userInfoResponses ?: listOf()
                    val userList = studentItems.map { "${it.name} (${it.major})" }
                        .toMutableList().apply { this.add("할당자 선택하기") }
                    hmwk_student_spinner.adapter =
                        AssignorAdapter(this@HomeworkAddActivity, userList) {
                            hmwk_student_spinner.onItemSelectedListener = object :
                                AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    userInfo = studentItems[position]
                                }

                                override fun onNothingSelected(p0: AdapterView<*>?) {

                                }
                            }
                        }

                    hmwk_student_spinner.setSelection(studentItems.size)
                }
            }

            override fun onFailure(call: Call<HomeworkedUserData>, t: Throwable) {

            }

        })

        hmwk_cancel_tv.setOnClickListener {
            finish()
        }

        hmwk_complete_tv.setOnClickListener {
            val bodyData = HomeworkBodyData(
                major,
                hmwk_endDate_tv.text.toString(),
                userInfo!!.email,
                hmwk_description_tv.text.toString(),
                hmwk_title_tv.text.toString()
            )
            builder.setMessage("숙제를 추가하시겠습니까?")
            builder.setNeutralButton(
                "취소"
            ) { _: DialogInterface?, _: Int -> }
            builder.setNegativeButton(
                "추가"
            ) { _: DialogInterface?, _: Int ->
                userResponse.createHomework("Bearer " + sharedPreferencesHelper.accessToken!!, bodyData).enqueue(object : Callback<Unit> {
                    override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                        Toast.makeText(
                            this@HomeworkAddActivity,
                            R.string.homework_add_success,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    override fun onFailure(call: Call<Unit>, t: Throwable) {

                    }

                })
                Handler().postDelayed({
                    finish()
                }, 300L)

            }
            builder.setCancelable(false)
            builder.show()
        }
    }
}