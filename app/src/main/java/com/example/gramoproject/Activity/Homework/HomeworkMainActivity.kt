package com.example.gramoproject.activity.homework

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gramo.R
import com.example.gramoproject.adapter.HomeworkAdapter
import com.example.gramoproject.activity.client.ApiClient
import com.example.gramoproject.DataClass.HomeworkResponse
import com.example.gramoproject.Interface.HomeworkInterface
import kotlinx.android.synthetic.main.homework_list.*
import kotlinx.android.synthetic.main.homework_main_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeworkMainActivity : AppCompatActivity() {

    val assignedAdapter = HomeworkAdapter()
    val orderedAdapter = HomeworkAdapter()
    val submittedAdapter = HomeworkAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homework_main_activity)

//        Navigation Drawer
//        NavInitializeLayout()
//        notice_nav_view.setNavigationItemSelectedListener(this)

        setRecyclerView(hmwk_assigned_recyclerView, orderedAdapter)
        setRecyclerView(hmwk_ordered_recyclerView, assignedAdapter)
        setRecyclerView(hmwk_submitted_recyclerView, submittedAdapter)

        hmwk_add_tv.setOnClickListener {
            val intent = Intent(this, HomeworkAddActivity::class.java)
            startActivity(intent)
        }

//        //Navigation Drawer 설정
//        private fun NavInitializeLayout() {
//            //toolbar를 통해 Appbar 생성
//            setSupportActionBar(notice_toolbar2)
//            //Appbar 좌측 Drawer를 열기위한 아이콘 생성
//            supportActionBar?.setDisplayHomeAsUpEnabled(true)
//            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24)
//            supportActionBar?.setTitle("")
//
//            val actionBarDrawerToggle = ActionBarDrawerToggle(this, drawer_layout, notice_toolbar2, R.string.open, R.string.closed)
//            drawer_layout.addDrawerListener(actionBarDrawerToggle)
//        }
//
//        //상단 메뉴바의 아이템 클릭시 활동
//        override fun onNavigationItemSelected(item: MenuItem): Boolean {
//            when (item.itemId) {
//                R.id.notice_menu -> {
//                    if(currentActivity.equals("NoticeActivity")) {
//                        drawer_layout.closeDrawer(GravityCompat.START)
//                        return false
//                    }
//                    else {
//                        val intentToNotice = Intent(this, NoticeActivity::class.java)
//                        startActivity(intentToNotice)
//                    }
//                }
//                R.id.calender_menu -> {
//                    if(currentActivity.equals("Calendar")){
//                        drawer_layout.closeDrawer(GravityCompat.START)
//                        return false
//                    }
//                    else{
//                        val intentToCalendar = Intent(this, CalendarActivity::class.java)
//                        startActivity(intentToCalendar)
//                    }
//                }
//                R.id.assignment_menu -> {
//                    if(currentActivity.equals("HomeworkMainActivity")){
//                        drawer_layout.closeDrawer(GravityCompat.START)
//                        return false
//                    }
//                    else{
//                        val intentToHomework = Intent(this, HomeworkMainActivity::class.java)
//                        startActivity(intentToHomework)
//                    }
//                }
//
//            }
//            drawer_layout.closeDrawer(GravityCompat.START)
//            return true
//        }
    }

    override fun onResume() {
        val service = ApiClient.getClient().create(HomeworkInterface::class.java)
        service.getOrderedHomeworkList().enqueue(object : Callback<List<HomeworkResponse>> {
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

        service.getAssignedHomeworkList().enqueue(object : Callback<List<HomeworkResponse>> {
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

        service.getSubmittedHomeworkList().enqueue(object : Callback<List<HomeworkResponse>> {
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
}