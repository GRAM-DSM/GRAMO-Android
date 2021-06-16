package com.example.gramoproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gramo.R
import com.example.gramoproject.model.PlanList
import kotlinx.android.synthetic.main.picu_item.view.*
import kotlinx.android.synthetic.main.plan_item.view.*

class PlanRecyclerAdapter(private val items : PlanList) : RecyclerView.Adapter<PlanRecyclerAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(v: View, position: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflateView: View
        inflateView = LayoutInflater.from(parent.context).inflate(R.layout.plan_item, parent, false)
        return ViewHolder(inflateView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.planContentResponses[position]
        holder.apply {
            bind(item!!)
            itemView.tag = item
        }
    }

    override fun getItemCount() = items.planContentResponses.size

    fun removePlan(position: Int) {
        items.planContentResponses.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
        notifyItemChanged(position)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val view: View = v
        fun bind(item: PlanList.Plan) {
            view.plan_title.text = item.title
            view.doing_tv.text = item.description

            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                view.plan_delete_iv.setOnClickListener {
                    listener!!.onItemClick(view, position)
                }
            }
        }
    }
}