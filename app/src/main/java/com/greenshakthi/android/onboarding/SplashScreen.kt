package com.greenshakthi.android.onboarding

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.greenshakthi.android.R
import com.greenshakthi.android.home.MainActivity
import com.greenshakthi.android.utils.AppPreferences

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // initialising App Preferences
        AppPreferences.init(this)

        //window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        Handler().postDelayed({

            if(AppPreferences.isLogin == true) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

            } else {
                val intent = Intent(this, GetStartedActivity::class.java)
                startActivity(intent)
            }

        },3000)
    }
}