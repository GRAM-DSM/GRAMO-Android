package com.example.gramoproject.activity.notice

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.gramo.R
import com.example.gramoproject.sharedpreferences.SharedPreferencesHelper
import com.example.gramo.databinding.NoticeAddActivityBinding
import com.example.gramoproject.model.NoticeItem
import com.example.gramoproject.view.main.MainActivity.Companion.intent
import com.example.gramoproject.view.main.MainActivity.Companion.toast
import com.example.gramoproject.view.notice.NoticeActivity
import com.example.gramoproject.viewmodel.NoticeAddViewModel
import kotlinx.android.synthetic.main.notice_add_activity.*
import java.util.*

class NoticeAddActivity : AppCompatActivity() {
    private lateinit var dataBinding : NoticeAddActivityBinding
    private val viewModel : NoticeAddViewModel by viewModels()
    val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.notice_add_activity)
        dataBinding.lifecycleOwner = this
        dataBinding.viewModel = viewModel

        notice_cancel_tv.setOnClickListener{
            finish()
        }

        notice_complete_tv.setOnClickListener{
            if(notice_title_et.text.toString().equals("") || notice_content_et.text.toString().equals("")){
                toast(this@NoticeAddActivity, R.string.notice_add_insert, 0)
            }
            else{
                viewModel.createNotice(NoticeItem(notice_title_et.text.toString(), notice_content_et.text.toString()))
            }
        }

        viewModel.liveData.observe(this, {
            when(it){
                201 -> {
                    toast(this@NoticeAddActivity, R.string.notice_add_success, 0)
                    intent(this@NoticeAddActivity, NoticeActivity::class.java, true)
                    // finish()
                }
                400 -> toast(this@NoticeAddActivity, R.string.notice_add_error, 0)
            }
        })
    }
}