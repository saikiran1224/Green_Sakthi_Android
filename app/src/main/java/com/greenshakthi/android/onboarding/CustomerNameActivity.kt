package com.greenshakthi.android.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
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

    lateinit var custImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_name)

        // initialsing App Preferences
        AppPreferences.init(this)

        // Checking Internet Connection
        if(!AppPreferences.isOnline()) AppPreferences.showNetworkErrorPage(this)

        db = Firebase.firestore

        txtNext = findViewById(R.id.txt_next)
        edtName = findViewById(R.id.edt_name)

        custImage = findViewById(R.id.img_user)

        Glide.with(this).load(R.drawable.user).into(custImage)

        txtNext.setOnClickListener {

            val enteredName = edtName.text.toString().trim()

            if(enteredName.isEmpty())
                Toast.makeText(this,"Please enter Valid Name",Toast.LENGTH_LONG).show()
            else {

                if (!AppPreferences.isOnline())
                    AppPreferences.showToast(this, "There is No Internet Connection. Please check your Wifi or Mobile Data once.")
                else {

                    // Pass details to Cloud Firestore - Name and Phone Number
                    val custID = UUID.randomUUID().toString()
                    val phoneNumber = intent.getStringExtra("phoneNumber")

                    val customerData = UserData(custID, enteredName, phoneNumber.toString())
                    db.collection("Customer_Data")
                        .add(customerData)
                        .addOnSuccessListener {

                            Toast.makeText(
                                this,
                                "Congratulations, You are Registered Successfully!",
                                Toast.LENGTH_LONG
                            ).show()

                            // Setting the details of the Customer globally
                            AppPreferences.isLogin = true
                            AppPreferences.customerID = custID
                            AppPreferences.customerName = enteredName
                            AppPreferences.customerPhone = phoneNumber

                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("customerName", enteredName)
                            startActivity(intent)
                            finish()
                        }
                }
            }

        }
    }
}