package com.example.gramoproject.view.notice

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.gramo.R
import com.example.gramoproject.sharedpreferences.SharedPreferencesHelper
import com.example.gramo.databinding.NoticeActivityBinding
import com.example.gramoproject.activity.notice.NoticeAddActivity
import com.example.gramoproject.model.NoticeList
import com.example.gramoproject.adapter.NoticeRecyclerAdapter
import com.example.gramoproject.view.calendar.CalendarActivity
import com.example.gramoproject.view.homework.HomeworkMainActivity
import com.example.gramoproject.view.main.MainActivity.Companion.intent
import com.example.gramoproject.view.main.MainActivity.Companion.toast
import com.example.gramoproject.view.sign.LoginActivity
import com.example.gramoproject.viewmodel.NoticeViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.leave_custom_dialog.*
import kotlinx.android.synthetic.main.logout_custom_dialog.*
import kotlinx.android.synthetic.main.notice_activity.*
import kotlinx.android.synthetic.main.notice_appbar.*
import kotlinx.android.synthetic.main.notice_bottomsheet.*
import kotlinx.android.synthetic.main.notice_drawer.*
import kotlinx.android.synthetic.main.notice_drawer.view.*
import kotlinx.android.synthetic.main.notice_unload_dialog.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.coroutines.*
import java.lang.Runnable

class NoticeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val currentActivity = javaClass.simpleName.trim()

    companion object {
        var logoutCheck: Boolean = false
        var withCheck: Boolean = false
    }
    private lateinit var dataBinding: NoticeActivityBinding
    private lateinit var LogoutDialog: Dialog
    private lateinit var LeaveDialog: Dialog
    private lateinit var UnloadDialog: Dialog
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var adapter: NoticeRecyclerAdapter
    private var backKeyPressedTime: Long = 0
    private val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()
    private val viewModel: NoticeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.notice_activity)
        dataBinding.lifecycleOwner = this
        dataBinding.viewModel = viewModel

        var layoutManager = LinearLayoutManager(this@NoticeActivity)
        notice_recyclerview.layoutManager = layoutManager

        viewModel.getNotice()
        viewModelObserve()
        swipeRefresh()
        initDialog()
        initScrollListener()

        notice_add_btn.setOnClickListener {
            intent(this@NoticeActivity, NoticeAddActivity::class.java, false)
        }

    }

    private fun viewModelObserve(){
        var fragmentManager = supportFragmentManager
        viewModel.noticeLiveData.observe(this, {
            when (it) {
                200 -> {
                    adapter = NoticeRecyclerAdapter(viewModel.noticeList.value!!, fragmentManager)
                    notice_recyclerview.adapter = adapter
                    rvItemClick()
                }
                401 -> toast(this@NoticeActivity, R.string.notice_add_error, 0)
            }
        })
        viewModel.itemLiveData.observe(this, {
            when (it) {
                200 -> {
                    bottomSheetDialog.notice_name_et.text = viewModel.noticeDetail.value!!.notice.name
                    bottomSheetDialog.notice_date_et.text = viewModel.noticeDetail.value!!.notice.created_at
                    bottomSheetDialog.notice_title_tv2.text = viewModel.noticeDetail.value!!.notice.title
                    bottomSheetDialog.notice_contents_tv2.text = viewModel.noticeDetail.value!!.notice.content
                    bottomSheetDialog.show()
                }
                404 -> toast(this@NoticeActivity, R.string.notice_not_match_to_id, 0)
            }
        })
        viewModel.loadMoreLiveData.observe(this, {
            when (it) {
                404 -> toast(this@NoticeActivity, R.string.notice_not_exist, 0)
            }
        })
        viewModel.logoutLiveData.observe(this, {
            when(it){
                204 -> {
                    toast(this@NoticeActivity, R.string.logout_success, 0)
                    logoutCheck = true
                    LogoutDialog.dismiss()
                    intent(this@NoticeActivity, LoginActivity::class.java, true)
                }
                401 -> toast(this@NoticeActivity, R.string.logout_error, 0)

            }
        })
        viewModel.withDrawLiveData.observe(this, {
            when(it){
                204 -> {
                    withCheck = true
                    toast(this@NoticeActivity, R.string.with_success, 0)
                    intent(this@NoticeActivity, LoginActivity::class.java, true)
                }
                401 -> {
                    withCheck = false
                    toast(this@NoticeActivity, R.string.with_error, 0)
                }
            }
        })
    }

    private fun initScrollListener() {
        notice_recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (viewModel.isNext && !viewModel.isLoading) {
                    if (!notice_recyclerview.canScrollVertically(1)) {
                        runOnUiThread {
                            val handler = Handler(Looper.getMainLooper())
                            handler.postDelayed(object : Runnable {
                                override fun run() {
                                    viewModel.loadMore(adapter)
                                    viewModel.isLoading = true
                                }
                            }, 1000)
                        }
                    }
                }
            }
        })
    }

    private fun NavInitializeLayout() {
        setSupportActionBar(notice_toolbar2)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24)
        supportActionBar?.setTitle("")

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            notice_toolbar2,
            R.string.open,
            R.string.closed
        )
        drawer_layout.addDrawerListener(actionBarDrawerToggle)

        val header = notice_nav_view.getHeaderView(0)
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
                    intent(this, NoticeActivity::class.java, false)
                }
            }
            R.id.calender_menu -> {
                if (currentActivity.equals("Calendar")) {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    return false
                } else {
                    intent(this, CalendarActivity::class.java, false)
                }
            }
            R.id.assignment_menu -> {
                if (currentActivity.equals("HomeworkMainActivity")) {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    return false
                } else {
                    intent(this, HomeworkMainActivity::class.java, false)
                }
            }

        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawers()
        } else {
            if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
                backKeyPressedTime = System.currentTimeMillis()
                toast(this@NoticeActivity, R.string.back_pressed, 0)
                return
            }
            if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
                finishAffinity()
            }
        }
    }

    private fun initDialog() {
        bottomSheetDialog = BottomSheetDialog(this@NoticeActivity)
        NavInitializeLayout()
        notice_nav_view.setNavigationItemSelectedListener(this)

        LogoutDialog = Dialog(this)
        LogoutDialog.setContentView(R.layout.logout_custom_dialog)
        side_logout_btn.setOnClickListener {
            showLogoutDialog()
        }

        LeaveDialog = Dialog(this)
        LeaveDialog.setContentView(R.layout.leave_custom_dialog)
        side_leave_btn.setOnClickListener {
            showLeaveDialog()
        }

        UnloadDialog = Dialog(this)
        UnloadDialog.setContentView(R.layout.notice_unload_dialog)
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

    private fun rvItemClick() {
        adapter.setOnItemClickListener(object : NoticeRecyclerAdapter.OnNoticeItemClickListener {
            override fun onItemClick(v: View, data: NoticeList.GetNotice, position: Int) {
                val id = viewModel.noticeList.value!!.notice.get(position)!!.id
                bottomSheetDialog.setContentView(R.layout.notice_bottomsheet)
                viewModel.rvItemClick(id)

                bottomSheetDialog.notice_unload_btn.setOnClickListener {
                    UnloadDialog.show()
                    UnloadDialog.unload_negative_btn.setOnClickListener {
                        UnloadDialog.dismiss()
                    }
                    UnloadDialog.unload_positive_btn.setOnClickListener {
                        noticeRemove(position, id)
                        viewModel.removeLiveData.value = 0
                    }
                }
            }
        })
    }

    private fun noticeRemove(position: Int, id: Int) {
        viewModel.noticeRemove(position, id, adapter)
        viewModel.removeLiveData.observe(this, {
            when (it) {
                204 -> {
                    UnloadDialog.dismiss()
                    bottomSheetDialog.dismiss()
                    toast(this@NoticeActivity, R.string.notice_delete, 0)
                }
                403 -> {
                    UnloadDialog.dismiss()
                    toast(this@NoticeActivity, R.string.notice_other_user_delete, 0)
                }
                404 -> {
                    UnloadDialog.dismiss()
                    toast(this@NoticeActivity, R.string.notice_not_match_to_id, 0)
                    adapter.notifyItemChanged(id)
                }
            }
        })
    }

    private fun swipeRefresh() {
        swipe_refresh_layout.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                viewModel.off_set = -10
                viewModel.getNotice()
                swipe_refresh_layout.isRefreshing = false
            }
        })
    }
}
