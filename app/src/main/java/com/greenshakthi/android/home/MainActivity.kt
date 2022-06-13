package com.greenshakthi.android.home

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.greenshakthi.android.BuildConfig
import com.greenshakthi.android.R
import com.greenshakthi.android.fragments.HomeFragment
import com.greenshakthi.android.fragments.MyOrdersFragment
import com.greenshakthi.android.fragments.ProfileFragment
import com.greenshakthi.android.utils.AppPreferences

class MainActivity : AppCompatActivity() {

    private var homeFragment = HomeFragment()
    private var myOrdersFragment = MyOrdersFragment()
    private var profileFragment = ProfileFragment()

    lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialising App Prefs
        AppPreferences.init(this)


        if(AppPreferences.remindLater == true)
            checkAppVersion()

        bottomNavigationView = findViewById(R.id.bottomNav)

        replaceFragment(homeFragment)

        bottomNavigationView.setOnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.nav_homeIcon -> {
                    replaceFragment(homeFragment)
                    true
                }
                R.id.nav_myOrdersIcon -> {
                    replaceFragment(myOrdersFragment)
                    true
                }
                R.id.nav_profileIcon -> {
                    replaceFragment(profileFragment)
                    true
                }
                else -> false
            }

        }

    }

    // This method is used to replace Fragment
    private fun replaceFragment(fragment : Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer,fragment)
        transaction.commit()
    }

    // Checking the App Version to notify about the Update
    private fun checkAppVersion() {

        // Retrieving the App Utils data from Firestore
        val db = Firebase.firestore

        val docRef = db.collection("App_Utils").document("b7NJ1YMyH94VDv0ytR0h")
        docRef.get().addOnSuccessListener {

            val versionName = it.data!!.get("Version").toString()
            val versionCode = it.data!!.get("VersionCode").toString()

            // INFO: Here we are going to check whether the current version of app is old when compared to new one
            // There are two ways: 1. Using Version Code 2. Using Version Name
            // 1. Using Version Code is just a number directly we can compare
            // 2. Using Version Name is a bit complicated since it involves two to three digit numbers in it.

            if((versionCode.toInt() > BuildConfig.VERSION_CODE) && (versionName!= BuildConfig.VERSION_NAME))
                showUpdateAppDialog()

            // Using Version Name - CODE
            /*if(getAppVersionFromString(versionName) > getAppVersionFromString(BuildConfig.VERSION_NAME)) {
                // Newer version of App is available
                showUpdateAppDialog()
            }*/


        }
    }

    private fun showUpdateAppDialog() {

        val updateAppDialog = Dialog(this)
        updateAppDialog.setContentView(R.layout.update_app_dialog)
        updateAppDialog.setCancelable(false)
        updateAppDialog.setCanceledOnTouchOutside(false)
        updateAppDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        updateAppDialog.findViewById<CardView>(R.id.btnProceed).setOnClickListener {

            // Navigate to Playstore Store Listing
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
            }
        }

        updateAppDialog.findViewById<CardView>(R.id.btnRemindLater).setOnClickListener {

            AppPreferences.remindLater = false
            updateAppDialog.dismiss()
        }

        updateAppDialog.show()


    }

    fun getAppVersionFromString(version: String): Int { // "2.3.5"
        val versions = version.split(".")
        val major = versions[0].toInt() * 10000 // 20000
        val minor = versions[1].toInt() * 1000 // 3000
        val patch = versions[2].toInt() * 100 // 500
        return major + minor + patch
    }


}