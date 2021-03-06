package com.gram.gramoproject.view.calendar

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.gram.gramo.R
import com.gram.gramo.databinding.CalendarActivityBinding
import com.gram.gramoproject.adapter.PicuRecyclerAdapter
import com.gram.gramoproject.adapter.PlanRecyclerAdapter
import com.gram.gramoproject.model.PicuBody
import com.gram.gramoproject.model.PlanBody
import com.gram.gramoproject.sharedpreferences.SharedPreferencesHelper
import com.gram.gramoproject.view.homework.HomeworkMainActivity
import com.gram.gramoproject.view.main.MainActivity.Companion.intent
import com.gram.gramoproject.view.main.MainActivity.Companion.toast
import com.gram.gramoproject.view.notice.NoticeActivity
import com.gram.gramoproject.view.sign.LoginActivity
import com.gram.gramoproject.viewmodel.CalendarViewModel
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.calendar_activity.*
import kotlinx.android.synthetic.main.calendar_appbar.*
import kotlinx.android.synthetic.main.homework_add_activity.*
import kotlinx.android.synthetic.main.leave_custom_dialog.*
import kotlinx.android.synthetic.main.login_activity.*
import kotlinx.android.synthetic.main.logout_custom_dialog.*
import kotlinx.android.synthetic.main.notice_activity.*
import kotlinx.android.synthetic.main.notice_activity.drawer_layout
import kotlinx.android.synthetic.main.notice_drawer.view.*
import kotlinx.android.synthetic.main.picu_custom_dialog.*
import kotlinx.android.synthetic.main.picu_item.*
import java.text.SimpleDateFormat
import java.util.*

class CalendarActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var LogoutDialog: Dialog
    private lateinit var LeaveDialog: Dialog
    private lateinit var picuDialog: Dialog
    private lateinit var planDialog: Dialog
    private lateinit var dataBinding: CalendarActivityBinding
    private lateinit var cal_date: String
    private lateinit var picuAdapter: PicuRecyclerAdapter
    private lateinit var planAdapter: PlanRecyclerAdapter
    private val viewModel: CalendarViewModel by viewModels()
    private val currentActivity = javaClass.simpleName.trim()
    private val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()
    val calendar = java.util.Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.calendar_activity)
        dataBinding.lifecycleOwner = this
        dataBinding.viewModel = viewModel

        picu_rv.layoutManager = LinearLayoutManager(this@CalendarActivity)
        special_rv.layoutManager = LinearLayoutManager(this@CalendarActivity)

        val now = System.currentTimeMillis()
        val date = Date(now)
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일")
        val apiDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val getDate = dateFormat.format(date)
        val apiGetDate = apiDateFormat.format(date)
        calendar_date_tv.text = getDate
        cal_date = apiGetDate

        initDialog()
        viewModel.getPicu(cal_date)
        viewModel.getPlan(cal_date)
        picu_user_name_tv.text = sharedPreferencesHelper.name

        calendar_date.setOnClickListener {
            datePicker()
        }

        cal_picu_plus_btn.setOnClickListener {
            add_picu_layout.visibility = View.VISIBLE
        }

        cal_plan_plus_btn.setOnClickListener {
            add_plan_layout.visibility = View.VISIBLE
        }

        cal_picu_accept_iv.setOnClickListener {
            if (user_description_et.text.toString().equals(""))
                toast(this@CalendarActivity, R.string.isEmpty, 0)
            else {
                viewModel.createPicu(PicuBody(user_description_et.getText().toString(), cal_date))
                add_picu_layout.visibility = View.GONE
                user_description_et.setText("")
                keyboardDown()
            }
        }

        cal_plan_accept_iv.setOnClickListener {
            if (plan_title.text.toString().equals("") || plan_description.text.toString().equals(""))
                toast(this@CalendarActivity, R.string.isEmpty, 0)
            else {
                viewModel.createPlan(
                    PlanBody(
                        plan_description.text.toString(),
                        plan_title.text.toString(),
                        cal_date
                    )
                )
                add_plan_layout.visibility = View.GONE
                plan_title.setText("")
                plan_description.setText("")
                keyboardDown()
            }
        }

        cal_cancel_iv.setOnClickListener {
            add_picu_layout.visibility = View.GONE
            user_description_et.setText("")
            keyboardDown()
        }

        cal_plan_cancel_iv.setOnClickListener {
            add_plan_layout.visibility = View.GONE
            plan_title.setText("")
            plan_description.setText("")
            keyboardDown()
        }

        viewModel.picuDeleteLiveData.observe(this, {
            when (it) {
                200 -> toast(this@CalendarActivity, R.string.success_picu_delete, 0)
                403 -> toast(this@CalendarActivity, R.string.does_not_delete_picu, 0)
                else -> toast(this@CalendarActivity, R.string.calendar_error, 0)
            }
        })
        viewModel.planDeleteLiveData.observe(this, {
            when (it) {
                200 -> toast(this@CalendarActivity, R.string.success_plan_delete, 0)
                else -> toast(this@CalendarActivity, R.string.calendar_error, 0)
            }
        })
        viewModel.picuLiveData.observe(this, {
            when (it) {
                200 -> {
                    picuAdapter = PicuRecyclerAdapter(viewModel.picuList.value!!)
                    picu_rv.adapter = picuAdapter
                    picuIvClick()
                }
            }
        })
        viewModel.planLiveData.observe(this, {
            when (it) {
                200 -> {
                    planAdapter = PlanRecyclerAdapter(viewModel.planList.value!!)
                    special_rv.adapter = planAdapter
                    planIvClick()
                }
            }
        })
        viewModel.createPicuLiveData.observe(this, {
            when (it) {
                201 -> {
                    viewModel.getPicu(cal_date)
                    toast(this@CalendarActivity, R.string.create_picu, 0)
                }
                else -> toast(this@CalendarActivity, R.string.calendar_error, 0)
            }
        })
        viewModel.createPlanLiveData.observe(this, {
            when (it) {
                201 -> {
                    viewModel.getPlan(cal_date)
                    toast(this@CalendarActivity, R.string.create_plan, 0)
                }
                else -> toast(this@CalendarActivity, R.string.calendar_error, 0)
            }
        })
        viewModel.logoutLiveData.observe(this, {
            when (it) {
                204 -> {
                    toast(this@CalendarActivity, R.string.logout_success, 0)
                    NoticeActivity.logoutCheck = true
                    LogoutDialog.dismiss()
                    intent(this@CalendarActivity, LoginActivity::class.java, true)
                }
                401 -> toast(this@CalendarActivity, R.string.logout_error, 0)

            }
        })
        viewModel.withDrawLiveData.observe(this, {
            when (it) {
                204 -> {
                    NoticeActivity.withCheck = true
                    toast(this@CalendarActivity, R.string.with_success, 0)
                    intent(this@CalendarActivity, LoginActivity::class.java, true)
                }
                401 -> {
                    NoticeActivity.withCheck = false
                    toast(this@CalendarActivity, R.string.with_error, 0)
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

        picuDialog = Dialog(this)
        picuDialog.setContentView(R.layout.picu_custom_dialog)

        planDialog = Dialog(this)
        planDialog.setContentView(R.layout.plan_custom_dialog)
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

    private fun datePicker() {
        val datePicker = DatePickerDialog(this, { _, i, i2, i3 ->
            calendar_date_tv.text = resources.getString(R.string.calendar_set_date, i, i2 + 1, i3)
            cal_date = resources.getString(R.string.calendar_set_date, i, i2 + 1, i3)
            viewModel.getPicu(cal_date)
            viewModel.getPlan(cal_date)
        }, year, month, day)

        datePicker.show()
    }

    private fun picuIvClick() {
        picuAdapter.setOnItemClickListener(object : PicuRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(v: View, position: Int) {
                val picuId = viewModel.picuList.value!!.picuContentResponses.get(position)!!.picuId
                picuDialog.show()
                picuDialog.picu_negative_btn.setOnClickListener {
                    picuDialog.dismiss()
                }
                picuDialog.picu_positive_btn.setOnClickListener {
                    picuDialog.dismiss()
                    viewModel.deletePicu(picuId,position,picuAdapter)
                }
            }
        })
    }

    private fun planIvClick() {
        planAdapter.setOnItemClickListener(object : PlanRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(v: View, position: Int) {
                val planId = viewModel.planList.value!!.planContentResponses.get(position)!!.planId
                planDialog.show()
                planDialog.picu_negative_btn.setOnClickListener {
                    planDialog.dismiss()
                }
                planDialog.picu_positive_btn.setOnClickListener {
                    planDialog.dismiss()
                    viewModel.deletePlan(planId, position, planAdapter)
                }
            }
        })
    }
    
    private fun keyboardDown() {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(cal_cancel_iv.windowToken, 0)
    }
}