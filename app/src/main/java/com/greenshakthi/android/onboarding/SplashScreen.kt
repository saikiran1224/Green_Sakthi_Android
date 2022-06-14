package com.greenshakthi.android.onboarding

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.greenshakthi.android.BuildConfig
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

        // making Remind Later true such that the app checks for the version
        AppPreferences.remindLater = true


        //window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        Handler().postDelayed({

            if(AppPreferences.isLogin == true) {

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

            } else {
                val intent = Intent(this, GetStartedActivity::class.java)
                startActivity(intent)
                finish()
            }

        },2800)
    }
}