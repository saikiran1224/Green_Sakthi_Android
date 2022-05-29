package com.greenshakthi.android.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.greenshakthi.android.R
import com.greenshakthi.android.home.MainActivity
import com.greenshakthi.android.models.UserData
import com.greenshakthi.android.utils.AppPreferences
import java.util.*

class CustomerNameActivity : AppCompatActivity() {

    lateinit var txtNext: TextView
    lateinit var edtName: EditText

    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_name)

        // initialsing App Preferences
        AppPreferences.init(this)

        db = Firebase.firestore

        txtNext = findViewById(R.id.txt_next)
        edtName = findViewById(R.id.edt_name)

        txtNext.setOnClickListener {

            val enteredName = edtName.text.toString().trim()

            if(enteredName.isEmpty())
                Toast.makeText(this,"Please enter Valid Name",Toast.LENGTH_LONG).show()
            else {

                // Pass details to Cloud Firestore - Name and Phone Number
                val custID = UUID.randomUUID().toString()
                val phoneNumber = intent.getStringExtra("phoneNumber")

                val customerData = UserData(custID,enteredName,phoneNumber.toString())
                db.collection("Customer_Data")
                    .add(customerData)
                    .addOnSuccessListener {

                        Toast.makeText(this,"Congratulations, You are Registered Successfully!",Toast.LENGTH_LONG).show()

                        // Setting the details of the Customer globally
                        AppPreferences.isLogin = true
                        AppPreferences.customerID = custID
                        AppPreferences.customerName = enteredName
                        AppPreferences.customerPhone = phoneNumber

                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("customerName",enteredName)
                        startActivity(intent)
                    }


            }

        }
    }
}