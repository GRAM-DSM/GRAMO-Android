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
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gramo.R
import com.example.gramo.Sharedpreferences.SharedPreferencesHelper
import com.example.gramoproject.`interface`.LoginInterface
import com.example.gramoproject.activity.client.ApiClient
import com.example.gramoproject.activity.homework.HomeworkMainActivity
import com.example.gramoproject.activity.sign.LoginActivity
import com.example.gramoproject.adapter.NoticeRecyclerAdapter
import com.example.gramoproject.dataclass.NoticeModel
import com.example.gramoproject.`interface`.NoticeInterface
import com.example.gramoproject.activity.calendar.CalendarActivity
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class NoticeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //현재 액티비티 확인
    private val currentActivity = javaClass.simpleName.trim()
    //어댑터에서도 사용하기 위해 companion object 선언
    companion object {
        lateinit var recyclerList: ArrayList<NoticeModel>
    }
    //커스텀 알림창
    private lateinit var LogoutDialog: Dialog
    private lateinit var LeaveDialog: Dialog
    private lateinit var UnloadDialog: Dialog

    //마지막으로 뒤로 가기 버튼을 누른 시간 저장
    private var backKeyPressedTime : Long = 0
    private lateinit var toast: Toast

    //리사이클러뷰 어댑터
    private lateinit var adapter : NoticeRecyclerAdapter

    //바텀시트
    private lateinit var bottomSheetDialog : BottomSheetDialog

    //API interface
    private lateinit var noticeInterface : NoticeInterface

    //페이지 수, 아이템 개수
    private var off_set = -5
    private val limit_num = 5
    private var existList = false

    private val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notice_activity)

        //custom dialog
        initDialog()

        initScrollListener()
        var layoutManager: LinearLayoutManager
        var fragmentManager: FragmentManager

        //retrofit2
//        noticeInterface = ApiClient.getClient().create(NoticeInterface::class.java)
//
//        val recyclerCall = noticeInterface.getNoticeList(sharedPreferencesHelper.accessToken!! ,getOffSet(), limit_num)
//
//        recyclerCall.enqueue(object : Callback<NoticeModel> {
//            override fun onResponse(call: Call<NoticeModel>, response: Response<NoticeModel>) {
//                when (response.code()) {
//                    200 -> {
//                        Log.d("NoticeActivity", response.body().toString())
//
//                        if (response.isSuccessful) {
//                            recyclerList.add(response.body()!!)
//
//                            //리사이클러뷰 레이아웃 매니저
//                            fragmentManager = supportFragmentManager
//                            layoutManager = LinearLayoutManager(this@NoticeActivity)
//                            notice_recyclerview.layoutManager = layoutManager
//
//                            //리사이클러뷰 어댑터 설정
//                            adapter = NoticeRecyclerAdapter(recyclerList, fragmentManager)
//                            notice_recyclerview.adapter = adapter
//                        }
//
//                        existList = true
//                    }
//                    404 -> {
//                        Toast.makeText(this@NoticeActivity, "공지사항이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<NoticeModel>, t: Throwable) {
//                Log.d("NoticeActivity", t.toString())
//            }
//        })

        //notice_add로 이동
        notice_add_btn.setOnClickListener {
            val intentToNoticeAdd = Intent(this@NoticeActivity, NoticeAddActivity::class.java)
            startActivity(intentToNoticeAdd)
        }

        //리사이클러뷰 아이템 클릭 이벤트 (공지사항 상세보기)
        if (existList) {
            adapter.setOnItemClickListener(object : NoticeRecyclerAdapter.OnNoticeItemClickListener {
                override fun onItemClick(v: View, data: NoticeModel, position: Int) {
                    val bottomSheetCall = noticeInterface.getNoticeDetail(sharedPreferencesHelper.accessToken!! ,position)
                    if (existList){
                        bottomSheetCall.enqueue(object : Callback<NoticeModel> {
                            override fun onResponse(call: Call<NoticeModel>, response: Response<NoticeModel>) {
                                when (response.code()) {
                                    200 -> {
                                        val getDetail = response.body()
                                        if (getDetail != null && response.isSuccessful) {
                                            bottomSheetDialog = BottomSheetDialog(this@NoticeActivity)
                                            bottomSheetDialog.setContentView(R.layout.notice_bottomsheet)

                                            bottomSheetDialog.notice_name_et.text = getDetail.name
                                            bottomSheetDialog.notice_date_et.text = getDetail.created_at
                                            bottomSheetDialog.notice_title_tv2.text = getDetail.title
                                            bottomSheetDialog.notice_contents_tv2.text = getDetail.content
                                        }
                                    }
                                    404 -> {
                                        Toast.makeText(this@NoticeActivity, "이 아이디와 일치하는 공지사항을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<NoticeModel>, t: Throwable) {
                                Log.d("NoticeActivity", t.toString())
                            }

                        })
                    }

                    //bottom sheet 띄우기
                    bottomSheetDialog.show()

                    //공지 내리기
                    bottomSheetDialog.notice_unload_btn.setOnClickListener {
                        UnloadDialog.show()
                        //Negative Button
                        UnloadDialog.unload_negative_btn.setOnClickListener {
                            UnloadDialog.dismiss()
                        }
                        //Positive Button
                        UnloadDialog.unload_positive_btn.setOnClickListener {
                            val dismissCall = noticeInterface.deleteNotice(sharedPreferencesHelper.accessToken!!, position)
                            dismissCall.enqueue(object : Callback<NoticeModel> {
                                override fun onResponse(call: Call<NoticeModel>, response: Response<NoticeModel>) {
                                    when (response.code()) {
                                        204 -> {
                                            UnloadDialog.dismiss()
                                            bottomSheetDialog.dismiss()
                                            adapter.removeItem(position)
                                            Toast.makeText(this@NoticeActivity, "공지사항이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                                        }
                                        403 -> {
                                            Toast.makeText(this@NoticeActivity, "다른 사용자가 만든 공지사항을 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show()
                                        }
                                        404 -> {
                                            Toast.makeText(this@NoticeActivity, "이 아이디와 일치하는 공지사항을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }

                                override fun onFailure(call: Call<NoticeModel>, t: Throwable) {
                                    Log.d("NoticeActivity", t.toString())
                                }
                            })
                        }
                    }
                }
            })
        }
    }
    //스크롤 리스너
    private fun initScrollListener(){
        notice_recyclerview.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                //스크롤이 끝에 도달했는지 확인
                if(!notice_recyclerview.canScrollVertically(1)){
                    recyclerList.removeAt(recyclerList.lastIndex)

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
    private fun loadMorePage(){
        val call = noticeInterface.getNoticeList(sharedPreferencesHelper.accessToken!!, getOffSet(), limit_num)
        call.enqueue(object : Callback<NoticeModel> {
            override fun onResponse(call: Call<NoticeModel>, response: Response<NoticeModel>) {
                when(response.code()) {
                    200 -> {
                        val getDataMore = response.body()
                        if (getDataMore != null && response.isSuccessful) {
                            recyclerList.add(getDataMore)
                        }
                    }
                    404 -> {
                        Toast.makeText(this@NoticeActivity, "공지사항이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<NoticeModel>, t: Throwable) {
                Log.d("NoticeActivity", t.toString())
            }

        })
    }

    private fun getOffSet(): Int{
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
            //super.onBackPressed()
            //마지막으로 뒤로가기를 누른 후 2.5초가 지났을 경우
            if (System.currentTimeMillis() > backKeyPressedTime + 2500) { //2500ms = 2.5s
                backKeyPressedTime = System.currentTimeMillis()
                toast = Toast.makeText(this@NoticeActivity, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT)
                toast.show()
                return
            }

            //마지막으로 뒤로가기를 누른 후 2.5초가 지나지 않았을 경우
            if(System.currentTimeMillis() <= backKeyPressedTime + 2500){ //2500ms = 2.5s
                finishAffinity()
                toast.cancel()
            }
        }
    }

    private fun initDialog(){
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
        side_leave_btn.setOnClickListener{
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
        LogoutDialog.logout_negative_btn.setOnClickListener{
            LogoutDialog.dismiss()
        }

        //Positive Button
        LogoutDialog.logout_positive_btn.setOnClickListener{
            val accessToken = "Bearer " + sharedPreferencesHelper.accessToken
            val logoutInterface = ApiClient.getClient().create(LoginInterface::class.java)
            val logoutCall = logoutInterface.logout(accessToken)

            logoutCall.enqueue(object : Callback<Unit>{
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    when(response.code()){
                        200 -> {
                            Toast.makeText(this@NoticeActivity, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()

                            sharedPreferencesHelper.accessToken = ""
                            sharedPreferencesHelper.refreshToken = ""

                            LogoutDialog.dismiss()
                            val intent = Intent(this@NoticeActivity, LoginActivity::class.java)
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                        }
                        401 -> {
                            Toast.makeText(this@NoticeActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
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
    private fun showLeaveDialog(){
        LeaveDialog.show()

        //Negative Button
        LeaveDialog.leave_negative_btn.setOnClickListener{
            LeaveDialog.dismiss()
        }

        //Positive Button
        LeaveDialog.leave_positive_btn.setOnClickListener{
            val intentToLogin = Intent(this@NoticeActivity, LoginActivity::class.java)
            intentToLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intentToLogin)
        }
    }
}
