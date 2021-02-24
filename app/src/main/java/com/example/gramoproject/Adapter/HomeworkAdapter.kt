package com.example.gramoproject.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.gramo.R
import com.example.gramoproject.Activity.Homework.CriteriaViewHolder
import com.example.gramoproject.Activity.Homework.ListViewHolder
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

class HomeworkAdapter(groups: List<ExpandableGroup<*>?>?) :
    ExpandableRecyclerViewAdapter<CriteriaViewHolder, ListViewHolder>(groups) {
    override fun onCreateGroupViewHolder(parent: ViewGroup?, viewType: Int): CriteriaViewHolder {
        val v = LayoutInflater.from(parent?.context)
            .inflate(R.layout.homework_criteria, parent, false)
        return CriteriaViewHolder(v)

    }

    override fun onCreateChildViewHolder(parent: ViewGroup?, viewType: Int): ListViewHolder {
        val v = LayoutInflater.from(parent?.context)
            .inflate(R.layout.homework_list, parent, false)
        return ListViewHolder(v)
    }

    override fun onBindChildViewHolder(
        holder: ListViewHolder?,
        flatPosition: Int,
        group: ExpandableGroup<*>?,
        childIndex: Int
    ) {

    }

    override fun onBindGroupViewHolder(
        holder: CriteriaViewHolder?,
        flatPosition: Int,
        group: ExpandableGroup<*>?
    ) {
        TODO("Not yet implemented")
    }
}