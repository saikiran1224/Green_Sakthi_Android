package com.greenshakthi.android.home

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.greenshakthi.android.R
import com.greenshakthi.android.models.FuelData
import com.greenshakthi.android.utils.AppPreferences
import org.w3c.dom.Text

class OrderCheckoutActivity : AppCompatActivity() {

    lateinit var txtFuelTitle: TextView
    lateinit var txtFuelPrice: TextView
    lateinit var selectedQuantity: TextView

    lateinit var edtAddress: EditText
    lateinit var txtSubTotal_Price: TextView
    lateinit var txtShippingCharges_Price: TextView
    lateinit var txtTotal_Price: TextView

    lateinit var txtBackButton: TextView

    lateinit var proceedToPaymentCard: MaterialCardView

    private var finalPrice: Float = 0.00f
    private var quantityValue: String = ""

    lateinit var db: FirebaseFirestore

    private var fuelUnitPrice: String = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_details)

        AppPreferences.init(this)

        // initialising Firestore
        db = Firebase.firestore

        txtFuelTitle = findViewById(R.id.txtFuelTitle)
        txtFuelPrice = findViewById(R.id.txtFuelPrice)

        selectedQuantity = findViewById(R.id.selectedQuantity)

        edtAddress = findViewById(R.id.edt_address)

        txtSubTotal_Price = findViewById(R.id.txtSubTotal_Price)
        txtShippingCharges_Price = findViewById(R.id.txtShippingCharges_Price)

        txtTotal_Price = findViewById(R.id.txtTotal_Price)

        txtBackButton = findViewById(R.id.txtBackButton)

        proceedToPaymentCard = findViewById(R.id.proceedToPaymentCard)

        // checking whether already the address is present
        val prevAddress = AppPreferences.customerAddress.toString()
        if (prevAddress.isNotEmpty())
            edtAddress.setText(prevAddress)


        txtBackButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Retrieving fuel title, price, selectedQuantity, subtotalPrice
        val intent = intent!!

        finalPrice = "%.2f".format(intent.getFloatExtra("finalPrice", 0.00f)).toString().toFloat()
        quantityValue = intent.getStringExtra("selectedQuantity").toString()

        txtFuelTitle.text = intent.getStringExtra("fuelTitle").toString()
        txtFuelPrice.text = "₹ " + "%.2f".format(intent.getFloatExtra("fuelPrice", 0.00f))
            .toString() + " per Litre"
        selectedQuantity.text =
            "Quantity: " + intent.getStringExtra("selectedQuantity") + " Litres"

        txtSubTotal_Price.text = "₹ " + "%.2f".format(intent.getFloatExtra("finalPrice", 0.00f))
        txtShippingCharges_Price.text = "₹ 0.00"
        txtTotal_Price.text = "₹ " + "%.2f".format(intent.getFloatExtra("finalPrice", 0.00f))

        proceedToPaymentCard.setOnClickListener {

            val addressEntered = edtAddress.text.toString().trim()

            if(addressEntered.isEmpty())
                Toast.makeText(this, "Please enter Address", Toast.LENGTH_LONG).show()
            else {

                // saving/overwriting the Address
                AppPreferences.customerAddress = addressEntered

                //checking whether there is a change in fuelUnitPrice
                checkFuelUnitPriceDetails(addressEntered, txtFuelPrice.text.split(" ")[1])

            }
        }

    }


    @SuppressLint("SetTextI18n")
    private fun checkFuelUnitPriceDetails(addressEntered: String, oldFuelPrice: String) {
        db = Firebase.firestore
        db.collection("Fuels_Data")
            .get()
            .addOnSuccessListener {

                for(document in it.documents) {

                    val fuelData = document.toObject<FuelData>()

                    fuelUnitPrice = "%.2f".format(fuelData!!.fuelPrice.toString().toFloat())

                    if(fuelUnitPrice.toString() == oldFuelPrice) {

                        // proceed to Payment Page
                        val intent = Intent(this, PaymentActivity::class.java)
                        intent.putExtra("fuelName", txtFuelTitle.text.toString())
                        intent.putExtra("fuelUnitPrice",txtFuelPrice.text.toString())
                        intent.putExtra("selectedQuantity",quantityValue)
                        intent.putExtra("custAddress",addressEntered)
                        intent.putExtra("finalPrice", finalPrice)
                        startActivity(intent)

                    }  else {

                        // Price has changed - navigate to Home Fragment and show a Toast message
                        Toast.makeText(this, "Some Error Occurred! Please try again...", Toast.LENGTH_LONG).show()

                        val intent_back = Intent(this, MainActivity::class.java)
                        startActivity(intent_back)

                    }
                }

            }.addOnFailureListener {

                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }

    }

}