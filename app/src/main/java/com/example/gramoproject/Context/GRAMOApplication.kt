package com.example.gramo.Context

import android.app.Application
import android.content.Context

class GRAMOApplication : Application() {
    override fun onCreate() {
        instance = this
        super.onCreate()
    }

    companion object{
        var instance : GRAMOApplication? = null
        val context: Context?
            get() = instance
    }
}