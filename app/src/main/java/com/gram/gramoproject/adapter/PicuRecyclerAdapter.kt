package com.gram.gramoproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gram.gramo.R
import com.gram.gramoproject.model.PicuList
import kotlinx.android.synthetic.main.picu_item.view.*

class PicuRecyclerAdapter(private val items: PicuList) : RecyclerView.Adapter<PicuRecyclerAdapter.ViewHolder>() {


    interface OnItemClickListener{
        fun onItemClick(v: View, position: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicuRecyclerAdapter.ViewHolder {
        val inflateView: View
        inflateView = LayoutInflater.from(parent.context).inflate(R.layout.picu_item, parent, false)
        return ViewHolder(inflateView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.picuContentResponses[position]
        holder.apply{
            bind(item!!)
            itemView.tag = item
        }
    }

    override fun getItemCount() = items.picuContentResponses.size

    fun removePicu(position: Int){
        items.picuContentResponses.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
        notifyItemChanged(position)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v){
        private val view : View = v
        fun bind(item: PicuList.Picu){
            view.user_name_tv.text = item.userName
            view.user_description_tv.text = item.description

            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                view.picu_delete_iv.setOnClickListener{
                    listener!!.onItemClick(view ,position)
                }
            }
        }
    }
}