package com.greenshakthi.android.home

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.greenshakthi.android.utils.AppPreferences
import com.razorpay.Checkout
import com.razorpay.ExternalWalletListener
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject


class PaymentActivity : AppCompatActivity(), PaymentResultWithDataListener, ExternalWalletListener {

    lateinit var txtBackButton: TextView

    lateinit var onlinePaymentCard: MaterialCardView

    private var finalPrice: String = ""

    private val TAG = PaymentActivity::class.java.simpleName
    private var alertDialogBuilder: AlertDialog.Builder? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.greenshakthi.android.R.layout.activity_payment)

        // Razorpay initialise
        Checkout.preload(this)

        AppPreferences.init(this)

        txtBackButton = findViewById(com.greenshakthi.android.R.id.txtBackButton)

        onlinePaymentCard = findViewById(com.greenshakthi.android.R.id.OnlinePaymentCard)

        alertDialogBuilder = AlertDialog.Builder(this@PaymentActivity)
        alertDialogBuilder!!.setCancelable(false)
        alertDialogBuilder!!.setTitle("Payment Result")
        alertDialogBuilder!!.setPositiveButton("Ok", DialogInterface.OnClickListener {
                dialog: DialogInterface?, which: Int ->
            // do nothing
        })

        val intent = intent
        finalPrice = "%.2f".format(intent.getFloatExtra("finalPrice",0.00f)).toString().replace(".","")

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

}

    override fun onPaymentSuccess(s: String?, paymentData: PaymentData?) {
        try {
            alertDialogBuilder!!.setMessage("Payment Successful :\nPayment ID: "+s+"\nPayment Data: "+paymentData!!.getData())
            alertDialogBuilder!!.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onPaymentError(p0: Int, p1: String?, paymentData: PaymentData?) {
        try {
            alertDialogBuilder!!.setMessage("Payment Failed:\nPayment Data: "+paymentData!!.getData())
            alertDialogBuilder!!.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onExternalWalletSelected(p0: String?, paymentData: PaymentData?) {
        try {
            alertDialogBuilder!!.setMessage("Payment Successful :\nPayment ID: "+p0+"\nPayment Data: "+paymentData!!.getData())
            alertDialogBuilder!!.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
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
            options.put("amount", finalPrice)

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

   /* private fun startPayment() {

        val checkout = Checkout()
        checkout.setKeyID("rzp_test_lE2DlcWuyCnraO")

        checkout.setImage(R.drawable.green_sakthi_logo)

        val activity: Activity = this





    }*/
}