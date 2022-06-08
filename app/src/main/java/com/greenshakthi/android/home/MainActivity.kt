package com.greenshakthi.android.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.greenshakthi.android.R
import com.greenshakthi.android.fragments.HomeFragment
import com.greenshakthi.android.fragments.MyOrdersFragment
import com.greenshakthi.android.fragments.ProfileFragment

class MainActivity : AppCompatActivity() {

    private var homeFragment = HomeFragment()
    private var myOrdersFragment = MyOrdersFragment()
    private var profileFragment = ProfileFragment()

    lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
}