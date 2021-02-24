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
import com.example.gramoproject.Adapter.NoticeRecyclerAdapter
import com.example.gramoproject.DataClass.NoticeItem
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.notice_activity.*
import kotlinx.android.synthetic.main.notice_appbar.*

class NoticeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //현재 액티비티 확인
    private val currentActivity = javaClass.simpleName.trim()
    //로그아웃 알림창
    private lateinit var dialog: Dialog

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

            }
            R.id.assignment_menu -> {
                TODO()
            }

        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
