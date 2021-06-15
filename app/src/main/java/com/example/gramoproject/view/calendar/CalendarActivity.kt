package com.example.gramoproject.view.calendar

import android.app.Dialog
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.example.gramo.R
import com.example.gramo.databinding.CalendarActivityBinding
import com.example.gramoproject.sharedpreferences.SharedPreferencesHelper
import com.example.gramoproject.view.homework.HomeworkMainActivity
import com.example.gramoproject.view.main.MainActivity
import com.example.gramoproject.view.main.MainActivity.Companion.intent
import com.example.gramoproject.view.notice.NoticeActivity
import com.example.gramoproject.view.sign.LoginActivity
import com.example.gramoproject.viewmodel.CalendarViewModel
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.calendar_activity.*
import kotlinx.android.synthetic.main.calendar_appbar.*
import kotlinx.android.synthetic.main.leave_custom_dialog.*
import kotlinx.android.synthetic.main.logout_custom_dialog.*
import kotlinx.android.synthetic.main.notice_activity.*
import kotlinx.android.synthetic.main.notice_activity.drawer_layout
import kotlinx.android.synthetic.main.notice_drawer.view.*

class CalendarActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var LogoutDialog: Dialog
    private lateinit var LeaveDialog: Dialog
    private lateinit var dataBinding : CalendarActivityBinding
    private val viewModel: CalendarViewModel by viewModels()
    private val currentActivity = javaClass.simpleName.trim()
    private val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.calendar_activity)
        dataBinding.lifecycleOwner = this
        dataBinding.viewModel = viewModel
        initDialog()
        viewModelObseve()
    }

    private fun viewModelObseve(){
        viewModel.logoutLiveData.observe(this, {
            when(it){
                204 -> {
                    MainActivity.toast(this@CalendarActivity, R.string.logout_success, 0)
                    NoticeActivity.logoutCheck = true
                    LogoutDialog.dismiss()
                    intent(this@CalendarActivity, LoginActivity::class.java, true)
                }
                401 -> MainActivity.toast(this@CalendarActivity, R.string.logout_error, 0)

            }
        })
        viewModel.withDrawLiveData.observe(this, {
            when(it){
                204 -> {
                    NoticeActivity.withCheck = true
                    MainActivity.toast(this@CalendarActivity, R.string.with_success, 0)
                    intent(this@CalendarActivity, LoginActivity::class.java, true)
                }
                401 -> {
                    NoticeActivity.withCheck = false
                    MainActivity.toast(this@CalendarActivity, R.string.with_error, 0)
                }
            }
        })
    }

    private fun NavInitializeLayout() {
        setSupportActionBar(calendar_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24)
        supportActionBar?.setTitle("")

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            calendar_toolbar,
            R.string.open,
            R.string.closed
        )
        drawer_layout.addDrawerListener(actionBarDrawerToggle)

        val header = calendar_nav_view.getHeaderView(0)
        header.notice_name_et3.text = sharedPreferencesHelper.name
        header.notice_major_tv.text = sharedPreferencesHelper.major

        calendar_nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.notice_menu -> {
                if (currentActivity.equals("NoticeActivity")) {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    return false
                } else {
                    intent(this@CalendarActivity, NoticeActivity::class.java, false)
                    finish()
                }
            }
            R.id.calender_menu -> {
                if (currentActivity.equals("CalendarActivity")) {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    return false
                } else {
                    intent(this@CalendarActivity, CalendarActivity::class.java, false)
                    finish()
                }
            }
            R.id.assignment_menu -> {
                if (currentActivity.equals("HomeworkMainActivity")) {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    return false
                } else {
                    intent(this@CalendarActivity, HomeworkMainActivity::class.java, false)
                    finish()
                }
            }

        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun initDialog() {
        NavInitializeLayout()

        LogoutDialog = Dialog(this)
        LogoutDialog.setContentView(R.layout.logout_custom_dialog)
        cal_side_logout_btn.setOnClickListener {
            showLogoutDialog()
        }

        LeaveDialog = Dialog(this)
        LeaveDialog.setContentView(R.layout.leave_custom_dialog)
        cal_side_leave_btn.setOnClickListener {
            showLeaveDialog()
        }
    }

    private fun showLogoutDialog() {
        LogoutDialog.show()
        LogoutDialog.logout_negative_btn.setOnClickListener {
            LogoutDialog.dismiss()
        }
        LogoutDialog.logout_positive_btn.setOnClickListener {
            viewModel.logout()
        }
    }

    private fun showLeaveDialog() {
        LeaveDialog.show()
        LeaveDialog.leave_negative_btn.setOnClickListener {
            LeaveDialog.dismiss()
        }
        LeaveDialog.leave_positive_btn.setOnClickListener {
            LeaveDialog.dismiss()
            viewModel.withDrawal()
        }
    }
}