package com.example.gramoproject.Context

import android.app.Application
import android.content.Context
import com.example.gramo.Sharedpreferences.SharedPreferencesHelper

class GRAMOApplication : Application() {
    private var context: Context? = null
    override fun onCreate() {

        sharedPreferencesHelper.prefs = applicationContext!!.getSharedPreferences("Gramo-prefs", Context.MODE_PRIVATE)
        super.onCreate()
    }

    companion object{
        var context: Context? = null
            private set
        val sharedPreferencesHelper = SharedPreferencesHelper
    }
}