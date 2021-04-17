package com.example.gramoproject.activity.notice

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.gramo.R
import com.example.gramo.Sharedpreferences.SharedPreferencesHelper
import com.example.gramoproject.DataClass.NoticeList
import com.example.gramoproject.activity.sign.LoginActivity
import com.example.gramoproject.DataClass.GetDetailNotice
import com.example.gramoproject.activity.client.ApiClient
import com.example.gramoproject.activity.homework.HomeworkMainActivity
import com.example.gramoproject.adapter.NoticeRecyclerAdapter
import com.example.gramoproject.`interface`.NoticeInterface
import com.example.gramoproject.activity.calendar.CalendarActivity
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.internal.notifyAll
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class NoticeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //현재 액티비티 확인
    private val currentActivity = javaClass.simpleName.trim()

    //어댑터에서도 사용하기 위해 companion object 선언
    companion object {
        lateinit var recyclerList: NoticeList
        var logoutCheck: Boolean = false
    }

    //커스텀 알림창
    private lateinit var LogoutDialog: Dialog
    private lateinit var LeaveDialog: Dialog
    private lateinit var UnloadDialog: Dialog

    //마지막으로 뒤로 가기 버튼을 누른 시간 저장
    private var backKeyPressedTime: Long = 0
    private lateinit var toast: Toast

    //리사이클러뷰 어댑터
    private lateinit var adapter: NoticeRecyclerAdapter

    //바텀시트
    //private lateinit var bottomSheetDialog: BottomSheetDialog

    //API interface
    private lateinit var noticeInterface: NoticeInterface

    //페이지 수, 아이템 개수
    private var off_set = -10
    private val limit_num = 10

    private val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notice_activity)
        noticeInterface = ApiClient.getClient().create(NoticeInterface::class.java)

        initDialog()
        initScrollListener()
        swipeRefresh()
        getNotice()



        //notice_add로 이동
        notice_add_btn.setOnClickListener {
            val intentToNoticeAdd = Intent(this@NoticeActivity, NoticeAddActivity::class.java)
            startActivity(intentToNoticeAdd)
        }
    }


    //스크롤 리스너
    private fun initScrollListener() {
        notice_recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                //스크롤이 끝에 도달했는지 확인
                if (!notice_recyclerview.canScrollVertically(1)) {
//                    recyclerList.notice.removeAt(recyclerList.notice.lastIndex)

                    //리스트 업데이트 하는 것이 느리기 때문에 코루틴을 통해 UI 변경을 천천히 하게 만듦
                    CoroutineScope(Dispatchers.Main).launch {
                        val temp = CoroutineScope(Dispatchers.IO).async {
                            loadMorePage()
                        }.await()

                        adapter.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    //리사이클러뷰의 데이터를 더 로드하는 경우
    private fun loadMorePage() {
        val call = noticeInterface.getNoticeList("Bearer " + sharedPreferencesHelper.accessToken!!, getOffSet(), limit_num)
        call.enqueue(object : Callback<NoticeList> {
            override fun onResponse(call: Call<NoticeList>, response: Response<NoticeList>) {
                when (response.code()) {
                    200 -> {
                        val getDataMore: NoticeList? = response.body()
                        if (getDataMore != null && response.isSuccessful) {
                            recyclerList.notice.addAll(getDataMore.notice)
                        }
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

    private fun getOffSet(): Int {
        off_set += limit_num

        return off_set
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

        val header = notice_nav_view.getHeaderView(0)

        header.notice_name_et3.text = sharedPreferencesHelper.name
        header.notice_major_tv.text = sharedPreferencesHelper.major
    }

    //상단 메뉴바의 아이템 클릭시 활동
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
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    //뒤로가기 처리
    override fun onBackPressed() {
        //drawer가 열려있을 경우
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawers()
        } else {
            //super.onBackPressed()
            //마지막으로 뒤로가기를 누른 후 2.5초가 지났을 경우
            if (System.currentTimeMillis() > backKeyPressedTime + 2500) { //2500ms = 2.5s
                backKeyPressedTime = System.currentTimeMillis()
                toast = Toast.makeText(this@NoticeActivity, getString(R.string.back_pressed), Toast.LENGTH_SHORT)
                toast.show()
                return
            }

            //마지막으로 뒤로가기를 누른 후 2.5초가 지나지 않았을 경우
            if (System.currentTimeMillis() <= backKeyPressedTime + 2500) { //2500ms = 2.5s
                finishAffinity()
                toast.cancel()
            }
        }
    }

    private fun initDialog() {
        //Navigation Drawer init
        NavInitializeLayout()
        notice_nav_view.setNavigationItemSelectedListener(this)

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
        side_leave_btn.setOnClickListener {
            showLeaveDialog()
        }

        //공지 내리기 dialog 커스텀
        UnloadDialog = Dialog(this)
        UnloadDialog.setContentView(R.layout.notice_unload_dialog)
    }

    //Logout Custom Dialog
    private fun showLogoutDialog() {
        LogoutDialog.show()

        //Negative Button
        LogoutDialog.logout_negative_btn.setOnClickListener {
            LogoutDialog.dismiss()
        }

        //Positive Button
        LogoutDialog.logout_positive_btn.setOnClickListener {
            val accessToken = "Bearer " + sharedPreferencesHelper.accessToken
            val logoutInterface = ApiClient.getClient().create(LoginInterface::class.java)
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
                            val intent = Intent(this@NoticeActivity, LoginActivity::class.java)
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
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
    }

    //Leave Custom Dialog
    private fun showLeaveDialog() {
        LeaveDialog.show()

        //Negative Button
        LeaveDialog.leave_negative_btn.setOnClickListener {
            LeaveDialog.dismiss()
        }

        //Positive Button
        LeaveDialog.leave_positive_btn.setOnClickListener {
            val intentToLogin = Intent(this@NoticeActivity, LoginActivity::class.java)
            intentToLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intentToLogin)
        }
    }

    private fun getNotice(){
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
                            // recyclerList.notice.addAll(listOf(response.body() as Notice.GetNotice))
                            recyclerList = response.body()!!

                            //리사이클러뷰 레이아웃 매니저
                            fragmentManager = supportFragmentManager
                            layoutManager = LinearLayoutManager(this@NoticeActivity)
                            notice_recyclerview.layoutManager = layoutManager

                            //리사이클러뷰 어댑터 설정
                            adapter = NoticeRecyclerAdapter(recyclerList, fragmentManager)
                            notice_recyclerview.adapter = adapter

                            rvItemClick()
                        } else {
                            Toast.makeText(this@NoticeActivity, getString(R.string.notice_not_exist), Toast.LENGTH_LONG).show()
                        }
                    }
                    401 -> {
                        Toast.makeText(this@NoticeActivity, getString(R.string.notice_add_error), Toast.LENGTH_LONG).show()
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
                val id = recyclerList.notice.get(position).id
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
                    //Negative Button
                    UnloadDialog.unload_negative_btn.setOnClickListener {
                        UnloadDialog.dismiss()
                    }
                    //Positive Button
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

    private fun swipeRefresh(){
        swipe_refresh_layout.setOnRefreshListener(object: SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                off_set = -10
                getNotice()
                swipe_refresh_layout.isRefreshing = false
            }
        })
    }
}
