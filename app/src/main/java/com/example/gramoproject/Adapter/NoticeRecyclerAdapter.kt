package com.example.gramoproject.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gramo.R
import com.example.gramoproject.DataClass.NoticeItem
import com.example.gramoproject.DataClass.NoticeModel
import kotlinx.android.synthetic.main.notice_recycler_item.view.*

class NoticeRecyclerAdapter(private val items: ArrayList<NoticeModel>, fragmentManager: FragmentManager) : RecyclerView.Adapter<NoticeRecyclerAdapter.ViewHolder>(){
    private var mfragmentManager : FragmentManager = fragmentManager

    interface OnNoticeItemClickListener{
        fun onItemClick(v: View, data: NoticeModel, position: Int)
    }
    private var listener: OnNoticeItemClickListener? = null
    fun setOnItemClickListener(listener: OnNoticeItemClickListener){
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.notice_recycler_item, parent, false)
        return ViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.apply {
            bind(item, mfragmentManager)
            itemView.tag = item
        }
    }

    override fun getItemCount() = items.size

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    fun removeItem(position: Int){
        items.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val view : View = v
        fun bind(item: NoticeModel, fragmentManager: FragmentManager){
            view.notice_name_tv.text = item.id
            view.notice_date_tv.text = item.created_at
            view.notice_title_tv.text = item.title
            view.notice_contents_tv.text = item.content

            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                itemView.setOnClickListener{
                    listener?.onItemClick(itemView, item, position)
                }
            }
        }
    }
}