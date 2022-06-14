package com.greenshakthi.android.home

import `in`.myinnos.androidscratchcard.ScratchCard
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.card.MaterialCardView
import com.greenshakthi.android.R
import com.greenshakthi.android.utils.AppPreferences


class PostPaymentPage : AppCompatActivity() {

    lateinit var txtMyOrdersButton: TextView

    lateinit var lottieAnimView: LottieAnimationView
    lateinit var txtBigMessage: TextView
    lateinit var txtSmallMessage: TextView

    private var status: String = ""

    private var selectedQuantity: String = ""

    lateinit var scratchCardLayout: MaterialCardView

    lateinit var scratchCard: ScratchCard

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_payment_page)

        // initialising App Preferences
        AppPreferences.init(this)

        // Checking Internet Connection
        if (!AppPreferences.isOnline()) AppPreferences.showNetworkErrorPage(this)

        txtMyOrdersButton = findViewById(R.id.txt_myOrdersPage)

        lottieAnimView = findViewById(R.id.displayedAnim)
        txtBigMessage = findViewById(R.id.successMessageBig)
        txtSmallMessage = findViewById(R.id.messageSmall)

        scratchCardLayout = findViewById(R.id.scratchCardLayout)

        // retrieving the status of the transaction
        val intent = intent
        status = intent.getStringExtra("status").toString()
        selectedQuantity = intent.getStringExtra("quantitySelected").toString()


        if(status == "Success") {

            // set all messages related to Success
            lottieAnimView.setAnimation(R.raw.success_anim)

            // showing the Scratch Card Layout
            scratchCardLayout.visibility = View.VISIBLE

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
            finish()

        }

        // Animation for MaterialCardView
        val animation: Animation = AlphaAnimation(1f, 0.5f) // Change alpha from fully visible to invisible

        animation.duration = 500 // duration - half a second
        animation.interpolator = LinearInterpolator() // do not alter animation rate
        animation.repeatCount = Animation.INFINITE // Repeat animation infinitely
        animation.repeatMode = Animation.REVERSE // Reverse animation at the end so the button will fade back in

        scratchCardLayout.startAnimation(animation)

        // on click listener for ScratchCard Layout
        scratchCardLayout.setOnClickListener {

            // showing Scratch Card Dialog
            val scratchCardDialog = Dialog(this)
            scratchCardDialog.setContentView(R.layout.layout_scratch_card_dialog)
            scratchCardDialog.setCancelable(false)
            scratchCardDialog.setCanceledOnTouchOutside(false)
            scratchCardDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

            scratchCardDialog.findViewById<Button>(R.id.btnThanks).setOnClickListener {
                scratchCardDialog.dismiss()
            }

            // Setting the Cashback earned based on the Quantity -
            // E.g User selects quantity b/w 10 - 20 then 20 * 3 = Rs. 60 [ Highest Number in range * Rs. 3]
            if(selectedQuantity.toInt() in 10..20)
                scratchCardDialog.findViewById<TextView>(R.id.txtCashVoucher).text = "You have earned ₹ 60"
            else if(selectedQuantity.toInt() in 21..30)
                scratchCardDialog.findViewById<TextView>(R.id.txtCashVoucher).text = "You have earned ₹ 90"
            else if(selectedQuantity.toInt() in 31..40)
                scratchCardDialog.findViewById<TextView>(R.id.txtCashVoucher).text = "You have earned ₹ 120"
            else if(selectedQuantity.toInt() in 41..50)
                scratchCardDialog.findViewById<TextView>(R.id.txtCashVoucher).text = "You have earned ₹ 150"
            else if(selectedQuantity.toInt() in 51..60)
                scratchCardDialog.findViewById<TextView>(R.id.txtCashVoucher).text = "You have earned ₹ 180"
            else
                scratchCardDialog.findViewById<TextView>(R.id.txtCashVoucher).text = "You have earned ₹ 200"

            // binding scratch Card
            scratchCard = scratchCardDialog.findViewById(R.id.scratchCard)

            scratchCard.setOnScratchListener { scratchCard, visiblePercent ->
                if(visiblePercent > 0.3) {
                    scratchCard.visibility = View.GONE
                    scratchCardDialog.findViewById<TextView>(R.id.txtVoucherDelivery).visibility = View.VISIBLE
                }
            }

            scratchCardDialog.show()
        }
    }

    override fun onBackPressed() {

    }
}