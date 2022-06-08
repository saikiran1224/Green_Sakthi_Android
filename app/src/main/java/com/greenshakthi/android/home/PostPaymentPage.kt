package com.greenshakthi.android.home

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.greenshakthi.android.R
import org.w3c.dom.Text

class PostPaymentPage : AppCompatActivity() {

    lateinit var txtMyOrdersButton: TextView

    lateinit var lottieAnimView: LottieAnimationView
    lateinit var txtBigMessage: TextView
    lateinit var txtSmallMessage: TextView

    private var status: String = ""

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_status_page)

        txtMyOrdersButton = findViewById(R.id.txt_myOrdersPage)

        lottieAnimView = findViewById(R.id.displayedAnim)
        txtBigMessage = findViewById(R.id.successMessageBig)
        txtSmallMessage = findViewById(R.id.messageSmall)

        // retrieving the status of the transaction
        val intent = intent
        status = intent.getStringExtra("status").toString()

        if(status == "Success") {

            // set all messages related to Success
            lottieAnimView.setAnimation(R.raw.success_anim)

            txtBigMessage.text = "Order Placed Successfully!"
            txtBigMessage.setTextColor(resources.getColor(R.color.green))

            txtSmallMessage.text = "Your Order has been placed successfully. Please visit My Orders page to know the status of the order"

        } else if (status == "Failure") {

            // set all messages related to Failure
            lottieAnimView.setAnimation(R.raw.error_anim)

            txtBigMessage.text = "Uh-oh! Something went wrong"
            txtBigMessage.setTextColor(resources.getColor(R.color.red))

            txtSmallMessage.text = "Sorry, your order is not placed! Kindly navigate back and place order after some time. Regret for the inconvenience caused."

        }

        txtMyOrdersButton.setOnClickListener {

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }

    }
}