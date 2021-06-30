package com.example.gramoproject.view.homework

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gramo.R
import com.example.gramo.databinding.HomeworkMainActivityBinding
import com.example.gramoproject.adapter.HomeworkAdapter
import com.example.gramoproject.model.HomeworkResponse
import com.example.gramoproject.sharedpreferences.SharedPreferencesHelper
import com.example.gramoproject.view.calendar.CalendarActivity
import com.example.gramoproject.view.main.MainActivity
import com.example.gramoproject.view.notice.NoticeActivity
import com.example.gramoproject.view.sign.LoginActivity
import com.example.gramoproject.viewmodel.HomeworkMainViewModel
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.homework_appbar.*
import kotlinx.android.synthetic.main.homework_list.*
import kotlinx.android.synthetic.main.homework_main_activity.*
import kotlinx.android.synthetic.main.leave_custom_dialog.*
import kotlinx.android.synthetic.main.logout_custom_dialog.*
import kotlinx.android.synthetic.main.notice_activity.*
import kotlinx.android.synthetic.main.notice_appbar.*
import kotlinx.android.synthetic.main.notice_drawer.view.*

class HomeworkMainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val currentActivity = javaClass.simpleName.trim()

    private lateinit var dataBinding: HomeworkMainActivityBinding
    private lateinit var LogoutDialog: Dialog
    private lateinit var LeaveDialog: Dialog
    private val assignedAdapter = HomeworkAdapter()
    private val orderedAdapter = HomeworkAdapter()
    private val submittedAdapter = HomeworkAdapter()
    private val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()
    private val viewModel: HomeworkMainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homework_main_activity)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.homework_main_activity)
        dataBinding.lifecycleOwner = this
        dataBinding.viewModel = viewModel

        viewModel.getHomework()
        initDialog()
        swipeRefresh()
        viewModelObserve()

        hmwk_add_tv.setOnClickListener {
            val intent = Intent(this, HomeworkAddActivity::class.java)
            startActivity(intent)
        }
    }

    private fun viewModelObserve() {
        viewModel.assignLiveData.observe(this, {
            when(it) {
                200 -> {
                    setRecyclerView(hmwk_ordered_recyclerView, assignedAdapter)
                    addData(assignedAdapter, viewModel.homeworkList.value!!)
                }
                400 -> MainActivity.toast(this@HomeworkMainActivity, R.string.notice_add_error, 0)
            }
        })
        viewModel.orderLiveData.observe(this, {
            when(it) {
                200 -> {
                    setRecyclerView(hmwk_assigned_recyclerView, orderedAdapter)
                    addData(orderedAdapter, viewModel.homeworkList.value!!)
                }
                400 -> MainActivity.toast(this@HomeworkMainActivity, R.string.notice_add_error, 0)
            }
        })
        viewModel.submitLiveData.observe(this, {
            when(it) {
                200 -> {
                    setRecyclerView(hmwk_submitted_recyclerView, submittedAdapter)
                    addData(submittedAdapter, viewModel.homeworkList.value!!)
                }
                400 -> MainActivity.toast(this@HomeworkMainActivity, R.string.notice_add_error, 0)
            }
        })
        viewModel.logoutLiveData.observe(this, {
            when(it){
                204 -> {
                    MainActivity.toast(this@HomeworkMainActivity, R.string.logout_success, 0)
                    NoticeActivity.logoutCheck = true
                    LogoutDialog.dismiss()
                    MainActivity.intent(this@HomeworkMainActivity, LoginActivity::class.java, true)
                }
                401 -> MainActivity.toast(this@HomeworkMainActivity, R.string.logout_error, 0)

            }
        })
        viewModel.withDrawLiveData.observe(this, {
            when(it){
                204 -> {
                    NoticeActivity.withCheck = true
                    MainActivity.toast(this@HomeworkMainActivity, R.string.with_success, 0)
                    MainActivity.intent(this@HomeworkMainActivity, LoginActivity::class.java, true)
                }
                401 -> {
                    NoticeActivity.withCheck = false
                    MainActivity.toast(this@HomeworkMainActivity, R.string.with_error, 0)
                }
            }
        })
    }

    private fun NavInitializeLayout() {
        setSupportActionBar(homework_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24)
        supportActionBar?.title = ""

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            hmwk_drawer_layout,
            homework_toolbar,
            R.string.open,
            R.string.closed
        )
        hmwk_drawer_layout.addDrawerListener(actionBarDrawerToggle)

        val header = homework_nav_view.getHeaderView(0)
        header.notice_name_et3.text = sharedPreferencesHelper.name
        header.notice_major_tv.text = sharedPreferencesHelper.major
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.notice_menu -> {
                if (currentActivity.equals("NoticeActivity")) {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    return false
                } else {
                    MainActivity.intent(this, NoticeActivity::class.java, false)
                }
            }
            R.id.calender_menu -> {
                if (currentActivity.equals("Calendar")) {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    return false
                } else {
                    MainActivity.intent(this, CalendarActivity::class.java, false)
                }
            }
            R.id.assignment_menu -> {
                if (currentActivity.equals("HomeworkMainActivity")) {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    return false
                } else {
                    MainActivity.intent(this, HomeworkMainActivity::class.java, false)
                }
            }

        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun initDialog() {
        NavInitializeLayout()
        homework_nav_view.setNavigationItemSelectedListener(this)

        LogoutDialog = Dialog(this)
        LogoutDialog.setContentView(R.layout.logout_custom_dialog)
        hmwk_side_logout_btn.setOnClickListener {
            showLogoutDialog()
        }

        LeaveDialog = Dialog(this)
        LeaveDialog.setContentView(R.layout.leave_custom_dialog)
        hmwk_side_leave_btn.setOnClickListener {
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

    private fun setRecyclerView(recyclerView: RecyclerView, homeAdapter: HomeworkAdapter) {
        recyclerView.run {
            this.adapter = homeAdapter
            layoutManager = LinearLayoutManager(this@HomeworkMainActivity)
        }
    }

    private fun addData(adapter: HomeworkAdapter, data: List<HomeworkResponse>) {
        adapter.addHomeworkData(ArrayList(data))
        adapter.notifyDataSetChanged()
    }

    private fun swipeRefresh() {
        hmwk_swipe_refresh_layout.setOnRefreshListener {
            viewModel.getHomework()
            hmwk_swipe_refresh_layout.isRefreshing = false
        }
    }
}