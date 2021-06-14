package com.example.gramoproject.view.homework

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gramo.R
import com.example.gramoproject.sharedpreferences.SharedPreferencesHelper
import com.example.gramoproject.api.LoginInterface
import com.example.gramoproject.adapter.HomeworkAdapter
import com.example.gramoproject.api.ApiClient
import com.example.gramoproject.api.HomeworkInterface
import com.example.gramoproject.model.HomeworkResponse
import com.example.gramoproject.view.calendar.CalendarActivity
import com.example.gramoproject.view.notice.NoticeActivity
import com.example.gramoproject.view.sign.LoginActivity
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.homework_appbar.*
import kotlinx.android.synthetic.main.homework_list.*
import kotlinx.android.synthetic.main.homework_main_activity.*
import kotlinx.android.synthetic.main.leave_custom_dialog.*
import kotlinx.android.synthetic.main.logout_custom_dialog.*
import kotlinx.android.synthetic.main.notice_activity.*
import kotlinx.android.synthetic.main.notice_appbar.*
import kotlinx.android.synthetic.main.notice_drawer.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeworkMainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        var logoutCheck: Boolean = false
        var withCheck: Boolean = false
    }

    val assignedAdapter = HomeworkAdapter()
    val orderedAdapter = HomeworkAdapter()
    val submittedAdapter = HomeworkAdapter()
    private lateinit var LogoutDialog: Dialog
    private lateinit var LeaveDialog: Dialog
    private var off_set = -10
    private val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()
    private val currentActivity = javaClass.simpleName.trim()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homework_main_activity)

        initDialog()
        swipeRefresh()

        setRecyclerView(hmwk_assigned_recyclerView, orderedAdapter)
        setRecyclerView(hmwk_ordered_recyclerView, assignedAdapter)
        setRecyclerView(hmwk_submitted_recyclerView, submittedAdapter)

        hmwk_add_tv.setOnClickListener {
            val intent = Intent(this, HomeworkAddActivity::class.java)
            startActivity(intent)
        }
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
                    val intentToNotice = Intent(this, NoticeActivity::class.java)
                    startActivity(intentToNotice)
                }
            }
            R.id.calender_menu -> {
                if (currentActivity.equals("Calendar")) {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    return false
                } else {
                    val intentToCalendar = Intent(this, CalendarActivity::class.java)
                    startActivity(intentToCalendar)
                }
            }
            R.id.assignment_menu -> {
                if (currentActivity.equals("HomeworkMainActivity")) {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    return false
                } else {
                    val intentToHomework = Intent(this, HomeworkMainActivity::class.java)
                    startActivity(intentToHomework)
                }
            }

        }
        hmwk_drawer_layout.closeDrawer(GravityCompat.START)
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
            logout()
        }
    }

    private fun showLeaveDialog() {
        LeaveDialog.show()
        LeaveDialog.leave_negative_btn.setOnClickListener {
            LeaveDialog.dismiss()
        }
        LeaveDialog.leave_positive_btn.setOnClickListener {
            LeaveDialog.dismiss()
            withDrawal()
        }
    }

    private fun withDrawal() {
        val accessToken = "Bearer " + sharedPreferencesHelper.accessToken
        val withInterface = ApiClient.getFlaskClient().create(LoginInterface::class.java)
        val withCall = withInterface.withDrawal(accessToken)

        withCall.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                when (response.code()) {
                    200 -> {
                        withCheck = true
                        sharedPreferencesHelper.accessToken = ""
                        sharedPreferencesHelper.refreshToken = ""
                        loginIntent()
                    }
                    401 -> {
                        withCheck = false
                        Toast.makeText(
                            this@HomeworkMainActivity,
                            getString(R.string.with_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.e("HomeworkMainActivity", t.toString())
            }

        })
    }

    override fun onResume() {
        val service = ApiClient.getClient().create(HomeworkInterface::class.java)
        service.getOrderedHomeworkList("Bearer " + sharedPreferencesHelper.accessToken!!)
            .enqueue(object : Callback<List<HomeworkResponse>> {
                override fun onResponse(
                    call: Call<List<HomeworkResponse>>,
                    response: Response<List<HomeworkResponse>>
                ) {
                    if (response.isSuccessful) {
                        addData(orderedAdapter, response.body()!!)
                    }
                }

                override fun onFailure(call: Call<List<HomeworkResponse>>, t: Throwable) {
                }

            })

        service.getAssignedHomeworkList("Bearer " + sharedPreferencesHelper.accessToken!!)
            .enqueue(object : Callback<List<HomeworkResponse>> {
                override fun onResponse(
                    call: Call<List<HomeworkResponse>>,
                    response: Response<List<HomeworkResponse>>
                ) {
                    if (response.isSuccessful) {
                        addData(assignedAdapter, response.body()!!)
                    }
                }

                override fun onFailure(call: Call<List<HomeworkResponse>>, t: Throwable) {

                }

            })

        service.getSubmittedHomeworkList("Bearer " + sharedPreferencesHelper.accessToken!!)
            .enqueue(object : Callback<List<HomeworkResponse>> {
                override fun onResponse(
                    call: Call<List<HomeworkResponse>>,
                    response: Response<List<HomeworkResponse>>
                ) {
                    if (response.isSuccessful) {
                        addData(submittedAdapter, response.body()!!)

                    }
                }

                override fun onFailure(call: Call<List<HomeworkResponse>>, t: Throwable) {

                }

            })

        super.onResume()
    }

    fun setRecyclerView(recyclerView: RecyclerView, homeAdapter: HomeworkAdapter) {
        recyclerView.run {
            this.adapter = homeAdapter
            layoutManager = LinearLayoutManager(this@HomeworkMainActivity)
            adapter?.notifyDataSetChanged()
        }
    }

    fun addData(adapter: HomeworkAdapter, data: List<HomeworkResponse>) {
        adapter.addHomeworkData(ArrayList(data))
        adapter.notifyDataSetChanged()
    }

    private fun logout() {
        val accessToken = "Bearer " + sharedPreferencesHelper.accessToken
        val logoutInterface = ApiClient.getFlaskClient().create(LoginInterface::class.java)
        val logoutCall = logoutInterface.logout(accessToken)

        logoutCall.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                when (response.code()) {
                    204 -> {
                        Toast.makeText(this@HomeworkMainActivity, getString(R.string.logout_success), Toast.LENGTH_SHORT).show()

                        sharedPreferencesHelper.accessToken = ""
                        sharedPreferencesHelper.refreshToken = ""

                        logoutCheck = true
                        LogoutDialog.dismiss()
                        loginIntent()
                    }
                    401 -> {
                        Toast.makeText(this@HomeworkMainActivity, getString(R.string.logout_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.e("NoticeActivity", t.toString())
            }

        })
    }

    private fun swipeRefresh() {
        hmwk_swipe_refresh_layout.setOnRefreshListener {
            onResume()
            off_set = -10
            hmwk_swipe_refresh_layout.isRefreshing = false
        }
    }

    private fun loginIntent() {
        val intent = Intent(this@HomeworkMainActivity, LoginActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}