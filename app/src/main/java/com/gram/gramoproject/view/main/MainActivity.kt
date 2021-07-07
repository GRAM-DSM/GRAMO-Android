package com.gram.gramoproject.view.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.gram.gramo.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    companion object{
        fun toast(context : Context, string : Int, length : Int){
            when(length){
                0 -> Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
                1 -> Toast.makeText(context, string, Toast.LENGTH_LONG).show()
            }
        }

        fun<T> intent(packageContext : Context, cls : Class<T>, flags : Boolean){
            val intent = Intent(packageContext, cls)
            when(flags){
                false -> packageContext.startActivity(intent)
                true -> {
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    packageContext.startActivity(intent)
                }
            }
        }
    }

}