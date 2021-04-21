package com.example.gramo.Sharedpreferences

import android.content.SharedPreferences

class SharedPreferencesHelper {

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

    var name : String?
        get() = prefs.getString(NAME, null)
        set(value){
            val editor = prefs.edit()
            editor.putString(NAME, value)
            editor.apply()
        }

    var major : String?
        get() = prefs.getString(MAJOR, null)
        set(value){
            val editor = prefs.edit()
            editor.putString(MAJOR, value)
            editor.apply()
        }

    companion object {
        private const val TOKEN = "access_Token"
        private const val REFRESH = "refresh_Token"
        private const val NAME = "name"
        private const val MAJOR = "major"
        private const val TOKEN_TYPE = "token_type"
        private var instance: SharedPreferencesHelper? = null
        lateinit var prefs : SharedPreferences

        @Synchronized
        fun getInstance() : SharedPreferencesHelper {
            if(instance == null)
                instance = SharedPreferencesHelper()
            return instance!!
        }
    }
}