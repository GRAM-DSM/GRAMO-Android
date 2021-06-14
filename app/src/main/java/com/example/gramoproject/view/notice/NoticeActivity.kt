package com.example.gramoproject.view.notice

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.gramo.R
import com.example.gramo.Sharedpreferences.SharedPreferencesHelper
import com.example.gramoproject.model.NoticeList
import com.example.gramoproject.view.sign.LoginActivity
import com.example.gramoproject.model.GetDetailNotice
import com.example.gramoproject.api.ApiClient
import com.example.gramoproject.view.homework.HomeworkMainActivity
import com.example.gramoproject.adapter.NoticeRecyclerAdapter
import com.example.gramoproject.`interface`.NoticeInterface
import com.example.gramoproject.view.calendar.CalendarActivity
import com.example.gramoproject.`interface`.LoginInterface
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Runnable

open class NoticeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val currentActivity = javaClass.simpleName.trim()

    companion object {
        lateinit var recyclerList: NoticeList
        var logoutCheck: Boolean = false
        var withCheck: Boolean = false
    }

    private lateinit var LogoutDialog: Dialog
    private lateinit var LeaveDialog: Dialog
    private lateinit var UnloadDialog: Dialog
    private var backKeyPressedTime: Long = 0
    private lateinit var toast: Toast
    private lateinit var adapter: NoticeRecyclerAdapter
    private lateinit var noticeInterface: NoticeInterface
    private val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()
    private var off_set = -10
    private val limit_num = 10
    private var isNext = false
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notice_activity)

        noticeInterface = ApiClient.getFlaskClient().create(NoticeInterface::class.java)

        getNotice()
        swipeRefresh()
        initDialog()
        initScrollListener()

        notice_add_btn.setOnClickListener {
            val intent = Intent(this@NoticeActivity, NoticeAddActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initScrollListener() {
        notice_recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (isNext && !isLoading) {
                    if (!notice_recyclerview.canScrollVertically(1)) {
                        loadMorePage()
                        isLoading = true
                    }
                }
            }
        })
    }

    private fun loadMorePage() {
        runOnUiThread {
            recyclerList.notice.add(null)
            adapter.notifyItemInserted(recyclerList.notice.size - 1)

            val handler = Handler(Looper.getMainLooper())
            val call = noticeInterface.getNoticeList("Bearer " + sharedPreferencesHelper.accessToken!!, getOffSet(), limit_num)

            handler.postDelayed(object : Runnable {
                override fun run() {
                    call.enqueue(object : Callback<NoticeList> {
                        override fun onResponse(call: Call<NoticeList>, response: Response<NoticeList>) {
                            when (response.code()) {
                                200 -> {
                                    recyclerList.notice.removeAt(recyclerList.notice.size - 1)
                                    adapter.notifyItemRemoved(recyclerList.notice.size)

                                    val getDataMore: NoticeList? = response.body()
                                    if (getDataMore != null && response.isSuccessful) {
                                        isNext = getDataMore.next_page
                                        recyclerList.notice.addAll(getDataMore.notice)
                                    }
                                    adapter.notifyDataSetChanged()
                                    isLoading = false
                                }
                                404 -> {
                                    Toast.makeText(this@NoticeActivity, getString(R.string.notice_not_exist), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<NoticeList>, t: Throwable) {
                            Log.d("NoticeActivity", t.toString())
                        }

                    })
                }

            }, 1000)
        }
    }

    private fun getOffSet(): Int {
        off_set += limit_num

        return off_set
    }

    private fun NavInitializeLayout() {
        setSupportActionBar(notice_toolbar2)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24)
        supportActionBar?.setTitle("")

        val actionBarDrawerToggle = ActionBarDrawerToggle(this, drawer_layout, notice_toolbar2, R.string.open, R.string.closed)
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
                    val intent = Intent(this, NoticeActivity::class.java)
                    startActivity(intent)
                }
            }
            R.id.calender_menu -> {
                if (currentActivity.equals("Calendar")) {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    return false
                } else {
                    val intent = Intent(this, CalendarActivity::class.java)
                    startActivity(intent)
                }
            }
            R.id.assignment_menu -> {
                if (currentActivity.equals("HomeworkMainActivity")) {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    return false
                } else {
                    val intent = Intent(this, HomeworkMainActivity::class.java)
                    startActivity(intent)
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
            //super.onBackPressed()
            if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
                backKeyPressedTime = System.currentTimeMillis()
                toast = Toast.makeText(this@NoticeActivity, getString(R.string.back_pressed), Toast.LENGTH_SHORT)
                toast.show()
                return
            }
            if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
                finishAffinity()
                toast.cancel()
            }
        }
    }

    private fun initDialog() {
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

    private fun getNotice() {
        var layoutManager: LinearLayoutManager
        var fragmentManager: FragmentManager

        val recyclerCall = noticeInterface.getNoticeList("Bearer " + sharedPreferencesHelper.accessToken!!, getOffSet(), limit_num)

        recyclerCall.enqueue(object : Callback<NoticeList> {
            override fun onResponse(call: Call<NoticeList>, response: Response<NoticeList>) {
                when (response.code()) {
                    200 -> {
                        Log.d("NoticeActivity", response.body().toString())
                        Log.d("NoticeActivity", response.code().toString())
                        if (response.body() != null && response.isSuccessful) {
                            recyclerList = response.body()!!
                            isNext = response.body()!!.next_page

                            fragmentManager = supportFragmentManager
                            layoutManager = LinearLayoutManager(this@NoticeActivity)
                            notice_recyclerview.layoutManager = layoutManager
                            adapter = NoticeRecyclerAdapter(recyclerList, fragmentManager)
                            notice_recyclerview.adapter = adapter

                            rvItemClick()
                        } else {
                            Toast.makeText(this@NoticeActivity, getString(R.string.notice_not_exist), Toast.LENGTH_LONG).show()
                        }
                    }
                    401 -> {
                        Toast.makeText(this@NoticeActivity, getString(R.string.notice_add_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<NoticeList>, t: Throwable) {
                Log.d("NoticeActivity", t.toString())
            }
        })
    }

    private fun rvItemClick() {
        adapter.setOnItemClickListener(object : NoticeRecyclerAdapter.OnNoticeItemClickListener {
            override fun onItemClick(v: View, data: NoticeList.GetNotice, position: Int) {
                val id = recyclerList.notice.get(position)!!.id
                val bottomSheetDialog = BottomSheetDialog(this@NoticeActivity)
                bottomSheetDialog.setContentView(R.layout.notice_bottomsheet)

                val bottomSheetCall = noticeInterface.getNoticeDetail("Bearer " + sharedPreferencesHelper.accessToken!!, id)
                bottomSheetCall.enqueue(object : Callback<GetDetailNotice> {
                    override fun onResponse(call: Call<GetDetailNotice>, response: Response<GetDetailNotice>) {
                        when (response.code()) {
                            200 -> {
                                val getDetail = response.body()!!
                                if (getDetail != null && response.isSuccessful) {


                                    bottomSheetDialog.notice_name_et.text = getDetail.notice.name
                                    bottomSheetDialog.notice_date_et.text = getDetail.notice.created_at
                                    bottomSheetDialog.notice_title_tv2.text = getDetail.notice.title
                                    bottomSheetDialog.notice_contents_tv2.text = getDetail.notice.content

                                    bottomSheetDialog.show()
                                }
                            }
                            404 -> {
                                Toast.makeText(this@NoticeActivity, getString(R.string.notice_not_match_to_id), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<GetDetailNotice>, t: Throwable) {
                        Log.d("NoticeActivity", t.toString())
                    }

                })
                bottomSheetDialog.notice_unload_btn.setOnClickListener {
                    UnloadDialog.show()
                    UnloadDialog.unload_negative_btn.setOnClickListener {
                        UnloadDialog.dismiss()
                    }
                    UnloadDialog.unload_positive_btn.setOnClickListener {
                        val dismissCall = noticeInterface.deleteNotice("Bearer " + sharedPreferencesHelper.accessToken!!, id)
                        dismissCall.enqueue(object : Callback<Unit> {
                            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                                when (response.code()) {
                                    200 -> {
                                        UnloadDialog.dismiss()
                                        bottomSheetDialog.dismiss()
                                        adapter.removeItem(position)

                                        Toast.makeText(this@NoticeActivity, getString(R.string.notice_delete), Toast.LENGTH_SHORT).show()
                                    }
                                    403 -> {
                                        UnloadDialog.dismiss()
                                        Toast.makeText(this@NoticeActivity, getString(R.string.notice_other_user_delete), Toast.LENGTH_SHORT).show()
                                    }
                                    404 -> {
                                        UnloadDialog.dismiss()
                                        Toast.makeText(this@NoticeActivity, getString(R.string.notice_not_match_to_id), Toast.LENGTH_SHORT).show()
                                        adapter.notifyItemChanged(id)
                                    }
                                }
                            }

                            override fun onFailure(call: Call<Unit>, t: Throwable) {
                                Log.d("NoticeActivity", t.toString())
                            }
                        })
                    }
                }
            }
        })
    }

    private fun logout() {
        val accessToken = "Bearer " + sharedPreferencesHelper.accessToken
        val logoutInterface = ApiClient.getFlaskClient().create(LoginInterface::class.java)
        val logoutCall = logoutInterface.logout(accessToken)

        logoutCall.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                when (response.code()) {
                    200 -> {
                        Toast.makeText(this@NoticeActivity, getString(R.string.logout_success), Toast.LENGTH_SHORT).show()

                        sharedPreferencesHelper.accessToken = ""
                        sharedPreferencesHelper.refreshToken = ""

                        logoutCheck = true
                        LogoutDialog.dismiss()
                        loginIntent()
                    }
                    401 -> {
                        Toast.makeText(this@NoticeActivity, getString(R.string.logout_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.e("NoticeActivity", t.toString())
            }

        })
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
                        Toast.makeText(this@NoticeActivity, getString(R.string.with_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.e("NoticeActivity", t.toString())
            }

        })
    }

    private fun swipeRefresh() {
        swipe_refresh_layout.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                off_set = -10
                getNotice()
                swipe_refresh_layout.isRefreshing = false
            }
        })
    }

    private fun loginIntent() {
        val intent = Intent(this@NoticeActivity, LoginActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}
