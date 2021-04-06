package com.example.gramo.Sharedpreferences

import android.content.Context
import android.content.SharedPreferences
import com.example.gramo.Context.GRAMOApplication

class SharedPreferencesHelper {
    private val prefs : SharedPreferences = GRAMOApplication.context!!.getSharedPreferences(
            "myPrefs", Context.MODE_PRIVATE
    )

    var accessToken : String?
        get() = prefs.getString(TOKEN, null)
        set(value){
            val editor = prefs.edit()
            editor.putString(TOKEN, value)
            editor.apply()
        }

    var refreshToken : String?
        get() = prefs.getString(REFRESH, null)
        set(value){
            val editor = prefs.edit()
            editor.putString(REFRESH, value)
            editor.apply()
        }


    companion object {
        private const val TOKEN = "access_Token"
        private const val REFRESH = "refresh_Token"
        private var instance: SharedPreferencesHelper? = null

        @Synchronized
        fun getInstance() : SharedPreferencesHelper {
            if(instance == null)
                instance = SharedPreferencesHelper()
            return instance!!
        }
    }
}