package com.example.gramoproject.view.homework

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.gramo.R
import com.example.gramo.Sharedpreferences.SharedPreferencesHelper
import com.example.gramoproject.model.HomeworkContentResponseData
import com.example.gramoproject.api.HomeworkInterface
import com.example.gramoproject.api.ApiClient
import kotlinx.android.synthetic.main.homework_show_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeworkShowActivity : AppCompatActivity() {

    private val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homework_show_activity)

        val homeworkID = intent.getIntExtra("homeworkID", 1)
        val builder = AlertDialog.Builder(this)
        val service = ApiClient.getClient().create(HomeworkInterface::class.java)

        service.getHomeworkContent(homeworkID, "Bearer " + sharedPreferencesHelper.accessToken!!)
            .enqueue(object : Callback<HomeworkContentResponseData> {
                override fun onResponse(
                    call: Call<HomeworkContentResponseData>,
                    response: Response<HomeworkContentResponseData>
                ) {
                    if (response.isSuccessful) {
                        val homeworkResponse = response.body()
                        hmwk_endDate_tv.text = homeworkResponse?.endDate
                        hmwk_title_tv.text = homeworkResponse?.title
                        hmwk_description_tv.text = homeworkResponse?.description
                        hmwk_major_tv.text = homeworkResponse?.major
                        hmwk_date_tv.text = homeworkResponse?.startDate
                        hmwk_name_tv.text = homeworkResponse?.teacherName
                        if (homeworkResponse?.isMine == true) {
                            hmwk_delete_tv.visibility = View.VISIBLE
                            hmwk_reject_tv.visibility = View.VISIBLE
                            hmwk_submit_tv.text = "완료하기"
                        }
                        if (homeworkResponse?.isSubmitted == true) {
                            hmwk_submit_tv.setOnClickListener {
                                finish()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<HomeworkContentResponseData>, t: Throwable) {

                }

            })

        hmwk_back_tv.setOnClickListener {
            finish()
        }

        hmwk_back_btn.setOnClickListener {
            finish()
        }

        hmwk_submit_tv.setOnClickListener {
            if (hmwk_submit_tv.text == "제출하기") {
                service.submitHomework("Bearer " + sharedPreferencesHelper.accessToken!! ,homeworkID).enqueue(object : Callback<Unit> {
                    override fun onResponse(
                        call: Call<Unit>,
                        response: Response<Unit>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@HomeworkShowActivity,
                                R.string.homework_submit_success,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            finish()
                        }
                    }

                    override fun onFailure(call: Call<Unit>, t: Throwable) {

                    }

                })
            } else {
                service.deleteHomework("Bearer " + sharedPreferencesHelper.accessToken!!, homeworkID).enqueue(object : Callback<Unit> {
                    override fun onResponse(
                        call: Call<Unit>,
                        response: Response<Unit>
                    ) {
                        if (response.isSuccessful) {
                            showToast(R.string.homework_check_success)
                        }
                    }

                    override fun onFailure(call: Call<Unit>, t: Throwable) {

                    }

                })
            }
        }

        hmwk_reject_tv.setOnClickListener {
            builder.setMessage("숙제를 반환하시겠습니까?")
            builder.setNeutralButton(
                "취소"
            ) { _: DialogInterface?, _: Int -> }
            builder.setNegativeButton(
                "반환"
            ) { _: DialogInterface?, _: Int ->
                service.rejectHomework("Bearer " + sharedPreferencesHelper.accessToken!!, homeworkID).enqueue(object : Callback<Unit> {
                    override fun onResponse(
                        call: Call<Unit>,
                        response: Response<Unit>
                    ) {
                        if (response.isSuccessful) {
                            showToast(R.string.homework_reject_success)
                        }
                        else if(response.code() == 409)
                            showToast(R.string.homework_does_not_submit)
                    }

                    override fun onFailure(call: Call<Unit>, t: Throwable) {

                    }

                })
            }
            builder.setCancelable(false)
            builder.show()

        }

        hmwk_delete_tv.setOnClickListener {
            builder.setMessage("숙제를 삭제하시겠습니까?")
            builder.setNeutralButton(
                "취소"
            ) { _: DialogInterface?, _: Int -> }
            builder.setNegativeButton(
                "삭제"
            ) { _: DialogInterface?, _: Int ->
                service.deleteHomework("Bearer " + sharedPreferencesHelper.accessToken!!, homeworkID).enqueue(object : Callback<Unit> {
                    override fun onResponse(
                        call: Call<Unit>,
                        response: Response<Unit>
                    ) {
                        if (response.isSuccessful) {
                            showToast(R.string.homework_delete_success)
                        }
                    }

                    override fun onFailure(call: Call<Unit>, t: Throwable) {

                    }
                })
            }
            builder.setCancelable(false)
            builder.show()
        }
    }

    fun showToast(text: Int) {
        Toast.makeText(
            this@HomeworkShowActivity,
            text,
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }
}