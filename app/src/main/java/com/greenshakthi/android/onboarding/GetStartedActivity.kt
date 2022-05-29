package com.greenshakthi.android.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.greenshakthi.android.R

class GetStartedActivity : AppCompatActivity() {

    lateinit var txtGetStarted: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)

        txtGetStarted = findViewById(R.id.txt_getStarted)

        txtGetStarted.setOnClickListener {
            val intent = Intent(this, VerifyPhoneNumberActivity::class.java)
            startActivity(intent)
        }
    }
}