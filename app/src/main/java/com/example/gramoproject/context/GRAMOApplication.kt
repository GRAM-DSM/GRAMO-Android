package com.example.gramoproject.context

import android.app.Application
import android.content.Context
import com.example.gramoproject.sharedpreferences.SharedPreferencesHelper

class GRAMOApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = this
        sharedPreferencesHelper.prefs = applicationContext!!.getSharedPreferences("Gramo-prefs", Context.MODE_PRIVATE)
    }

    companion object{
        lateinit var context: Context
            private set
        val sharedPreferencesHelper = SharedPreferencesHelper
    }
}