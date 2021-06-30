package com.example.gramoproject.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gramo.R
import com.example.gramoproject.view.homework.HomeworkShowActivity
import com.example.gramoproject.model.HomeworkResponse
import kotlinx.android.synthetic.main.homework_list.view.*

class HomeworkAdapter : RecyclerView.Adapter<HomeworkAdapter.HomeworkViewHolder>() {

    private var items: ArrayList<HomeworkResponse> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeworkViewHolder {
        return HomeworkViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.homework_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HomeworkViewHolder, position: Int) {
        holder.run {
            bind(items[position])
            itemView.run {
                setOnClickListener {
                    val intent = Intent(context, HomeworkShowActivity::class.java)
                    intent.putExtra("homeworkID", items[position].homeworkId)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    class HomeworkViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: HomeworkResponse) {
            view.hmwk_endDate_tv.text = item.endDate
            view.hmwk_title_tv.text = item.title
            view.hmwk_description_tv.text = item.description
            view.hmwk_major_tv.text = item.major
            view.hmwk_name_tv.text = item.teacherName
            view.hmwk_date_tv.text = item.startDate
        }
    }

    fun addHomeworkData(input: ArrayList<HomeworkResponse>) {
        items.clear()
        items.addAll(input)
        notifyDataSetChanged()
    }

}