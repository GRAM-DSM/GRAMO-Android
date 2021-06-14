package com.example.gramoproject.adapter

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class AssignorAdapter(context: Context, userList: List<String>, val spinnerListener: () -> Unit) : ArrayAdapter<String>(
    context,
    android.R.layout.simple_spinner_dropdown_item,
    userList
) {

    override fun getCount(): Int {
        return super.getCount() - 1
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = setCentered(super.getView(position, convertView, parent))
        if (position == count) {
            (v.findViewById<View>(android.R.id.text1) as TextView).text =
                getItem(count)
        }

        return v
    }

    fun setCentered(view: View): View {
        val textView: TextView = view.findViewById(android.R.id.text1)
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        textView.gravity = Gravity.CENTER
        return view
    }

    override fun getDropDownView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val view: TextView = super.getDropDownView(
            position,
            convertView,
            parent
        ) as TextView
        view.textAlignment = View.TEXT_ALIGNMENT_CENTER
        spinnerListener()

        return view
    }
}