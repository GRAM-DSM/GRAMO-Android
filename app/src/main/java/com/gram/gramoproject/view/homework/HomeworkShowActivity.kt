package com.gram.gramoproject.view.homework

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.gram.gramo.R
import com.gram.gramo.databinding.HomeworkShowActivityBinding
import com.gram.gramoproject.view.main.MainActivity.Companion.toast
import com.gram.gramoproject.viewmodel.HomeworkShowViewModel
import kotlinx.android.synthetic.main.homework_show_activity.*

class HomeworkShowActivity : AppCompatActivity() {

    private lateinit var dataBinding: HomeworkShowActivityBinding
    private val viewModel: HomeworkShowViewModel by viewModels()
    private var onPressed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.homework_show_activity)
        dataBinding.lifecycleOwner = this
        dataBinding.viewModel = viewModel
        val homeworkID = intent.getIntExtra("homeworkID", 1)

        viewModel.getHomeworkContent(homeworkID)

        hmwk_back_tv.setOnClickListener {
            finish()
        }

        hmwk_back_btn.setOnClickListener {
            finish()
        }

        hmwk_submit_tv.setOnClickListener {
            if (hmwk_submit_tv.text == "완료하기") {
                onPressed = true
                deleteHomework(homeworkID)
            } else {
                submitHomework(homeworkID)
            }
        }

        hmwk_reject_tv.setOnClickListener {
            rejectHomework(homeworkID)
        }

        hmwk_delete_tv.setOnClickListener {
            onPressed = false
            deleteHomework(homeworkID)
        }

        viewModel.contentLiveData.observe(this, {
            when (it) {
                200 -> {
                    hmwk_endDate_tv.text = viewModel.homeworkContent.value?.endDate
                    hmwk_title_tv.text = viewModel.homeworkContent.value?.title
                    hmwk_description_tv.text = viewModel.homeworkContent.value?.description
                    hmwk_major_tv.text = viewModel.homeworkContent.value?.major
                    hmwk_date_tv.text = viewModel.homeworkContent.value?.startDate
                    hmwk_name_tv.text = viewModel.homeworkContent.value?.teacherName
                    if (viewModel.homeworkContent.value?.isMine == true) {
                        hmwk_delete_tv.visibility = View.VISIBLE
                        hmwk_reject_tv.visibility = View.VISIBLE
                        hmwk_submit_tv.text = "완료하기"
                    }
                }
            }
        })
        viewModel.deleteLiveData.observe(this, {
            when (it) {
                200 -> {
                    if(onPressed) {
                        toast(this@HomeworkShowActivity, R.string.homework_check_success, 0)
                        finish()
                    } else {
                        toast(this@HomeworkShowActivity, R.string.homework_delete_success, 0)
                        finish()
                    }
                }
            }
        })
        viewModel.submitLiveData.observe(this, {
            when (it) {
                201 -> {
                    toast(this@HomeworkShowActivity, R.string.homework_submit_success, 0)
                    finish()
                }
                409 -> {
                    toast(this@HomeworkShowActivity, R.string.homewokr_already_submitted, 0)
                }
            }
        })
        viewModel.rejectLiveData.observe(this, {
            when (it) {
                201 -> {
                    toast(this@HomeworkShowActivity, R.string.homework_reject_success, 0)
                    finish()
                }
                409 -> {
                    toast(this@HomeworkShowActivity, R.string.homework_does_not_submit, 0)
                }
            }
        })
    }

    fun submitHomework(homeworkID: Int) {
        if (hmwk_submit_tv.text == "제출하기") {
            viewModel.submitHomework(homeworkID)
        } else {
            viewModel.deleteHomework(homeworkID)
        }
    }

    fun rejectHomework(homeworkID: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("숙제를 반환하시겠습니까?")
        builder.setNeutralButton(
            "취소"
        ) { _: DialogInterface?, _: Int -> }
        builder.setNegativeButton(
            "반환"
        ) { _: DialogInterface?, _: Int ->
            viewModel.rejectHomework(homeworkID)
        }
        builder.setCancelable(false)
        builder.show()
    }

    fun deleteHomework(homeworkID: Int) {
        viewModel.deleteHomework(homeworkID)
    }
}