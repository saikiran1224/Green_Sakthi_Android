package com.greenshakthi.android.home

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.greenshakthi.android.models.OrderData
import com.greenshakthi.android.utils.AppPreferences
import com.razorpay.Checkout
import com.razorpay.ExternalWalletListener
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject
import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*


class PaymentActivity : AppCompatActivity(), PaymentResultWithDataListener, ExternalWalletListener {

    lateinit var txtBackButton: TextView

    lateinit var onlinePaymentCard: MaterialCardView
    lateinit var cashOnDeliveryCard: MaterialCardView

    private var finalPrice: String = ""

    private val TAG = PaymentActivity::class.java.simpleName
    private var alertDialogBuilder: AlertDialog.Builder? = null

    private var customerAddress: String = ""
    private var fuelName: String = ""
    private var fuelUnitPrice: String = ""
    private var selectedQuantity: String = ""

    lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.greenshakthi.android.R.layout.activity_payment)

        // Razorpay initialise
        Checkout.preload(this)

        AppPreferences.init(this)

        txtBackButton = findViewById(com.greenshakthi.android.R.id.txtBackButton)

        onlinePaymentCard = findViewById(com.greenshakthi.android.R.id.OnlinePaymentCard)
        cashOnDeliveryCard = findViewById(com.greenshakthi.android.R.id.cashOnDeliveryCard)

        db = Firebase.firestore

        alertDialogBuilder = AlertDialog.Builder(this@PaymentActivity)
        alertDialogBuilder!!.setCancelable(false)
        alertDialogBuilder!!.setTitle("Payment Result")
        alertDialogBuilder!!.setPositiveButton("Ok", DialogInterface.OnClickListener {
                dialog: DialogInterface?, which: Int ->
            // do nothing
        })

        val intent = intent
        finalPrice = "%.2f".format(intent.getFloatExtra("finalPrice",0.00f)).toString()
        customerAddress = intent.getStringExtra("custAddress").toString().trim()
        fuelName = intent.getStringExtra("fuelName").toString()
        fuelUnitPrice = intent.getStringExtra("fuelUnitPrice").toString()
        selectedQuantity = intent.getStringExtra("selectedQuantity").toString()

        txtBackButton.setOnClickListener {

            // showing Confirmation Dialog
            MaterialAlertDialogBuilder(this)
                .setTitle("Are you sure?")
                .setCancelable(false)
                .setMessage("The order will be cancelled and action could not be revesed")
                .setNegativeButton("No") { dialog, which ->
                    // Respond to negative button press
                    dialog.dismiss()
                }
                .setPositiveButton("Yes") { dialog, which ->
                    // Respond to positive button press

                    dialog.dismiss()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                .show()

        }

        onlinePaymentCard.setOnClickListener { startPayment(finalPrice) }

        cashOnDeliveryCard.setOnClickListener { placeOrder("COD","0") }

}

    override fun onPaymentSuccess(s: String?, paymentData: PaymentData?) {
        try {

            // placing Order
            placeOrder("Online", paymentData!!.paymentId.toString())

           // alertDialogBuilder!!.setMessage("Payment Successful :\nPayment ID: "+s+"\nPayment Data: "+paymentData!!.getData())
           // alertDialogBuilder!!.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onPaymentError(p0: Int, p1: String?, paymentData: PaymentData?) {
        try {

            // Some Error Occurred - redirect user to Order Status Page
            val intent = Intent(this, OrderStatusPage::class.java)
            intent.putExtra("status","Failure")
            startActivity(intent)

           // alertDialogBuilder!!.setMessage("Payment Failed:\nPayment Data: "+paymentData!!.getData())
           // alertDialogBuilder!!.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onExternalWalletSelected(p0: String?, paymentData: PaymentData?) {
        try {

            placeOrder("Online", paymentData!!.paymentId.toString())

          //  alertDialogBuilder!!.setMessage("Payment Successful :\nPayment ID: "+p0+"\nPayment Data: "+paymentData!!.getData())
          //  alertDialogBuilder!!.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun placeOrder(transactionMode: String, paymentID: String) {

        // generating an Unique ID based on time

        val key = db.collection("Orders_Data").document().id

        val substring_key = key.substring(0,5)

        val order_id = (Date().time / 1000L % Int.MAX_VALUE).toInt().toString().substring(0,6) + substring_key.toUpperCase()


        // retrieving the date and time the order is placed
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss a")
        val currentDateTime = sdf.format(Date())


        val orderData = OrderData(order_id,currentDateTime,finalPrice,customerAddress,"Placed",transactionMode, paymentID, fuelName, fuelUnitPrice, selectedQuantity, AppPreferences.customerName.toString(), AppPreferences.customerPhone.toString(), AppPreferences.customerID.toString(),key)

        db.collection("Orders_Data")
            .document(key)
            .set(orderData)
            .addOnSuccessListener {

                // Order placed successfully - redirect user to Order Success Page
                val intent = Intent(this, OrderStatusPage::class.java)
                intent.putExtra("status","Success")
                startActivity(intent)

            }
    }

    fun startPayment(finalPrice: String) {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        val activity: Activity = this
        val co = Checkout()
        co.setKeyID("rzp_test_lE2DlcWuyCnraO")

        try {
            val options = JSONObject()
            options.put("name", "Green Sakthi Fuels India Pvt. Ltd.")
            options.put("description", "Zero Carbon Emission Diesel")
            options.put("send_sms_hash", true)
            options.put("allow_rotation", true)
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://lh3.googleusercontent.com/a-/AOh14GggVXXZ53BLTOaYsWNRA_VsQyclib83q7MZZOq4=s360-p-rw-no")
            options.put("currency", "INR")
            options.put("amount", finalPrice.replace(".",""))

            val preFill = JSONObject()
            preFill.put("email","")
            preFill.put("contact", AppPreferences.customerPhone.toString())
            options.put("prefill", preFill)

            co.open(activity, options)

        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_SHORT)
                .show()
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        // Some Error Occurred - redirect user to Order Status Page
        val intent = Intent(this, OrderStatusPage::class.java)
        intent.putExtra("status","Failure")
        startActivity(intent)
    }
}