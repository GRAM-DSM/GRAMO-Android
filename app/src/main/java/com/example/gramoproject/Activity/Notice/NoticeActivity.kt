package com.example.gramoproject.Activity.Notice

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gramo.BottomSheet.NoticeBottomSheetDialogFragment
import com.example.gramo.R
import com.example.gramoproject.Activity.Calendar.CalendarActivity
import com.example.gramoproject.Activity.Homework.HomeworkMainActivity
import com.example.gramoproject.Activity.SignInUp.LoginActivity
import com.example.gramoproject.Adapter.NoticeRecyclerAdapter
import com.example.gramoproject.DataClass.NoticeItem
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.leave_custom_dialog.*
import kotlinx.android.synthetic.main.logout_custom_dialog.*
import kotlinx.android.synthetic.main.notice_activity.*
import kotlinx.android.synthetic.main.notice_appbar.*

class NoticeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //현재 액티비티 확인
    private val currentActivity = javaClass.simpleName.trim()
    //로그아웃 알림창
    private lateinit var LogoutDialog: Dialog
    private lateinit var LeaveDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notice_activity)

        //Navigation Drawer
        NavInitializeLayout()
        notice_nav_view.setNavigationItemSelectedListener(this)

        //notice_add로 이동
        notice_add_btn.setOnClickListener{
            val intentToNoticeAdd = Intent(applicationContext, NoticeAddActivity::class.java)
            startActivity(intentToNoticeAdd)
        }

        //리사이클러뷰
        val getIntent = intent
        val list = ArrayList<NoticeItem>()
        val fragmentManager = supportFragmentManager
        val layoutManager = LinearLayoutManager(this)
        notice_recyclerview.layoutManager = layoutManager
        if(getIntent.hasExtra("contents")) {
            list.add(
                    NoticeItem(
                            getIntent.getStringExtra("name")!!, getIntent.getStringExtra("date")!!,
                            getIntent.getStringExtra("title")!!, getIntent.getStringExtra("contents")!!
                    )
            )
        }
        val adapter = NoticeRecyclerAdapter(list, fragmentManager)
        notice_recyclerview.adapter = adapter

        //리사이클러뷰 아이템 클릭 이벤트
        adapter.setOnItemClickListener(object : NoticeRecyclerAdapter.OnNoticeItemClickListener{
            override fun onItemClick(v: View, data: NoticeItem, position: Int) {
                //bottom sheet 띄우기
                val bottomSheetDialogFragment = NoticeBottomSheetDialogFragment()
                bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)

            }
        })

        //로그아웃 dialog 커스텀
        LogoutDialog = Dialog(this)
        LogoutDialog.setContentView(R.layout.logout_custom_dialog)
        //로그아웃 클릭
        side_logout_btn.setOnClickListener {
            showLogoutDialog()
        }

        //탈퇴 dialog 커스텀
        LeaveDialog = Dialog(this)
        LeaveDialog.setContentView(R.layout.leave_custom_dialog)
        //탈퇴 클릭
        side_leave_btn.setOnClickListener{
            showLeaveDialog()
        }

    }

    //Navigation Drawer 설정
    private fun NavInitializeLayout() {
        //toolbar를 통해 Appbar 생성
        setSupportActionBar(notice_toolbar2)
        //Appbar 좌측 Drawer를 열기위한 아이콘 생성
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24)
        supportActionBar?.setTitle("")

        val actionBarDrawerToggle = ActionBarDrawerToggle(this, drawer_layout, notice_toolbar2, R.string.open, R.string.closed)
        drawer_layout.addDrawerListener(actionBarDrawerToggle)
    }

    //상단 메뉴바의 아이템 클릭시 활동
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.notice_menu -> {
                if(currentActivity.equals("NoticeActivity")) {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    return false
                }
                else {
                    val intentToNotice = Intent(this, NoticeActivity::class.java)
                    startActivity(intentToNotice)
                }
            }
            R.id.calender_menu -> {
                if(currentActivity.equals("Calendar")){
                    drawer_layout.closeDrawer(GravityCompat.START)
                    return false
                }
                else{
                    val intentToCalendar = Intent(this, CalendarActivity::class.java)
                    startActivity(intentToCalendar)
                }
            }
            R.id.assignment_menu -> {
                if(currentActivity.equals("HomeworkMainActivity")){
                    drawer_layout.closeDrawer(GravityCompat.START)
                    return false
                }
                else{
                    val intentToHomework = Intent(this, HomeworkMainActivity::class.java)
                    startActivity(intentToHomework)
                }
            }

        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    //뒤로가기 처리
    override fun onBackPressed() {
        //drawer가 열려있을 경우
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawers()
        } else{
            super.onBackPressed()
        }
    }

    //Logout Custom Dialog
    private fun showLogoutDialog() {
        LogoutDialog.show()

        //Negative Button
        LogoutDialog.logout_negative_btn.setOnClickListener{
            LogoutDialog.dismiss()
        }

        //Positive Button
        LogoutDialog.logout_positive_btn.setOnClickListener{
            val intentToLogin = Intent(applicationContext, LoginActivity::class.java)
            intentToLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intentToLogin)
        }
    }

    //Leave Custom Dialog
    private fun showLeaveDialog(){
        LeaveDialog.show()

        //Negative Button
        LeaveDialog.leave_negative_btn.setOnClickListener{
            LeaveDialog.dismiss()
        }

        //Positive Button
        LeaveDialog.leave_positive_btn.setOnClickListener{
            val intentToLogin = Intent(applicationContext, LoginActivity::class.java)
            intentToLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intentToLogin)
        }
    }
}
