package com.example.gramoproject.Activity.Homework

import android.app.DatePickerDialog
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.gramo.R
import com.example.gramoproject.DataClass.HomeworkModel
import kotlinx.android.synthetic.main.homework_add_activity.*

class HomeworkAddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homework_add_activity)

    }
}