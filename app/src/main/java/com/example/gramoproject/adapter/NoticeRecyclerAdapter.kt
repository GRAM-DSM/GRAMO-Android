package com.example.gramoproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gramo.R
import com.example.gramoproject.model.NoticeList
import kotlinx.android.synthetic.main.notice_recycler_item.view.*
import kotlinx.android.synthetic.main.progressbar.view.*

class NoticeRecyclerAdapter(private val items: NoticeList, fragmentManager: FragmentManager) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mfragmentManager : FragmentManager = fragmentManager
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    interface OnNoticeItemClickListener{
        fun onItemClick(v: View, data: NoticeList.GetNotice, position: Int)
    }

    private var listener: OnNoticeItemClickListener? = null
    fun setOnItemClickListener(listener: OnNoticeItemClickListener){
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflatedView: View

        if(viewType == VIEW_TYPE_ITEM) {
            inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.notice_recycler_item, parent, false)
            return ViewHolder(inflatedView)
        } else {
            inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.progressbar, parent, false)
            return LoadingViewHolder(inflatedView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items.notice[position]
        if(holder is ViewHolder) {
            holder.apply {
                bind(item!!, mfragmentManager)
                itemView.tag = item
            }
        } else {
            (holder as LoadingViewHolder).progressBar.isIndeterminate = true
        }
    }

    override fun getItemCount() = items.notice.size

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    fun removeItem(position: Int){
        items.notice.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
        notifyItemChanged(position)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val view : View = v
        fun bind(item: NoticeList.GetNotice, fragmentManager: FragmentManager){
            view.notice_name_tv.text = item.user_name
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

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var progressBar : ProgressBar

        init{
            progressBar = itemView.progressBar
        }
    }

    private fun showLoadingView(holder: LoadingViewHolder, position: Int){
    }

    override fun getItemViewType(position: Int): Int {
        if(items.notice.get(position) == null){
            return VIEW_TYPE_LOADING
        }
        else
            return VIEW_TYPE_ITEM
    }
}