package com.gram.gramoproject.view.homework

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.gram.gramo.R
import com.gram.gramo.databinding.HomeworkAddActivityBinding
import com.gram.gramo.model.UserResponse
import com.gram.gramoproject.adapter.AssignorAdapter
import com.gram.gramoproject.model.HomeworkBodyData
import com.gram.gramoproject.sharedpreferences.SharedPreferencesHelper
import com.gram.gramoproject.view.main.MainActivity.Companion.intent
import com.gram.gramoproject.view.main.MainActivity.Companion.toast
import com.gram.gramoproject.viewmodel.HomeworkAddViewModel
import kotlinx.android.synthetic.main.homework_add_activity.*
import java.text.SimpleDateFormat
import java.util.*

class HomeworkAddActivity : AppCompatActivity() {

    private lateinit var dataBinding: HomeworkAddActivityBinding
    private val viewModel: HomeworkAddViewModel by viewModels()
    val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()
    var userInfo: UserResponse? = null
    var major = ""
    private val now = System.currentTimeMillis()
    private val date = Date(now)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    private val currnetDate = dateFormat.format(date)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.homework_add_activity)
        dataBinding.lifecycleOwner = this
        dataBinding.viewModel = viewModel

        viewModel.getUserList()
        majorSpinnerInit()

        hmwk_name_tv.text = SharedPreferencesHelper.getInstance().name

        hmwk_date_tv.text = currnetDate

        hmwk_endDate_tv.setOnClickListener {
            showDatePickerDialog()
        }

        hmwk_cancel_tv.setOnClickListener {
            finish()
        }

        hmwk_complete_tv.setOnClickListener {
            addHomework()
        }

        viewModel.homeworkLiveData.observe(this, {
            when (it) {
                201 -> {
                    intent(this@HomeworkAddActivity, HomeworkMainActivity::class.java, true)
                    toast(this@HomeworkAddActivity, R.string.homework_add_success, 0)
                }
            }
        })
        viewModel.userLiveData.observe(this, {
            when (it) {
                200 -> userSpinnerInit()
            }
        })
    }

    private fun showDatePickerDialog() {
        val year = java.util.Calendar.getInstance().get(Calendar.YEAR)
        val month = java.util.Calendar.getInstance().get(Calendar.MONTH)
        val day = java.util.Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this, { _, i, i2, i3 ->
            val getDate = resources.getString(R.string.calendar_set_date, i, i2 + 1, i3)

            if (getDate.replace("-", "").toInt() < currnetDate.replace("-", "").toInt()) {
                Toast.makeText(this, R.string.homework_choice_correct_date, Toast.LENGTH_SHORT)
                    .show()
            } else {
                hmwk_endDate_tv.text = getDate
            }
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun majorSpinnerInit() {
        val majorItems = resources.getStringArray(R.array.majorList)
        val majorAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, majorItems)
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
    }

    private fun userSpinnerInit() {
        val studentItems: List<UserResponse> =
            viewModel.userList.value!!.userInfoResponses
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

    private fun addHomework() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("숙제를 추가하시겠습니까?")
        builder.setNeutralButton(
            "취소"
        ) { _: DialogInterface?, _: Int -> }
        builder.setNegativeButton(
            "추가"
        ) { _: DialogInterface?, _: Int ->
            if (major == "분야선택" || hmwk_endDate_tv.text.toString() == "마감일 선택하기" || userInfo?.email == null ||
                hmwk_title_tv.text.toString() == "" || hmwk_description_tv.text.toString() == ""
            ) {
                toast(this, R.string.homework_add_content, 0)
            } else {
                viewModel.createHomework(
                    HomeworkBodyData(
                        major,
                        hmwk_endDate_tv.text.toString(),
                        userInfo?.email.toString(),
                        hmwk_description_tv?.text.toString(),
                        hmwk_title_tv.text.toString()
                    )
                )
            }
        }
        builder.setCancelable(false)
        builder.show()
    }
}
