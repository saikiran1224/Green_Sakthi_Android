package com.greenshakthi.android.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.greenshakthi.android.R
import com.greenshakthi.android.utils.AppPreferences

class VerifyPhoneNumberActivity : AppCompatActivity() {

    lateinit var txtRequestOTP: TextView
    lateinit var edtPhoneNumber: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_phone_number)

        // initialising App Preferences
        AppPreferences.init(this)

        // Checking Internet Connection
        if(!AppPreferences.isOnline()) AppPreferences.showNetworkErrorPage(this)

        txtRequestOTP = findViewById(R.id.txt_next)
        edtPhoneNumber = findViewById(R.id.phone_number_edt)

        txtRequestOTP.setOnClickListener {

            val enteredPhoneNum = edtPhoneNumber.text.toString().trim()

            if(enteredPhoneNum.isEmpty() or (enteredPhoneNum.length!=10))
                Toast.makeText(this, "Please enter Valid Phone Number", Toast.LENGTH_LONG).show()
            else {

                if(!AppPreferences.isOnline()) {
                    AppPreferences.showToast(this, "There is No Internet Connection. Please check your Wifi or Mobile Data once.")
                } else {

                    // sending the Phone number to next Activity
                    val intent = Intent(this, OTPActivity::class.java)
                    intent.putExtra("phoneNumber", enteredPhoneNum.toString())
                    startActivity(intent)
                    finish()
                }
            }


        }
    }
}