package com.greenshakthi.android.utils

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.greenshakthi.android.R

class NetworkErrorActivity : AppCompatActivity() {

    lateinit var btnRetry: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_error)

        //initialising App Preferences
        AppPreferences.init(this)

        btnRetry = findViewById(R.id.btnRetry)

        btnRetry.setOnClickListener {

            if(AppPreferences.isOnline()) {

                // returns True - navigate User back to previous activity
                onBackPressed()

            } else {

                // returns False - Reloading the activity
                recreate()
            }

        }
    }
}