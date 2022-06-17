package com.greenshakthi.android.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.greenshakthi.android.BuildConfig
import com.greenshakthi.android.R
import com.greenshakthi.android.home.OrderCheckoutActivity
import com.greenshakthi.android.models.FuelData
import com.greenshakthi.android.utils.AppPreferences


class HomeFragment : Fragment() {

    lateinit var txtCustomerName: TextView

    lateinit var txtFuelTitle: TextView
    lateinit var txtFuelDesc: TextView
    lateinit var txtDieselPrice: TextView

    private var fuelTitle: String = ""
    private var fuelPrice: Float = 0.00f

    lateinit var db: FirebaseFirestore

    lateinit var mainLayout: RelativeLayout
    lateinit var maintenanceLayout: RelativeLayout
    lateinit var loadingAnim: LottieAnimationView

    lateinit var homeBgImage: ImageView

    @SuppressLint("SetTextI18n", "CutPasteId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // initialising App Preferences for Customer Name
        AppPreferences.init(requireContext())

        // Checking Internet Connection
        if(!AppPreferences.isOnline()) AppPreferences.showNetworkErrorPage(requireContext())

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        txtCustomerName = view.findViewById(R.id.txtCustomerName)

        mainLayout = view.findViewById(R.id.mainLayout)
        maintenanceLayout = view.findViewById(R.id.maintenanceLayout)
        loadingAnim = view.findViewById(R.id.loadingAnim)

        homeBgImage = view.findViewById(R.id.homeBackgroundImage)

        Glide.with(requireContext()).load(R.drawable.home_bg).into(homeBgImage)

        // Fuel Details
        txtFuelTitle = view.findViewById(R.id.txtTitle)
        txtFuelDesc = view.findViewById(R.id.txtDescription)
        txtDieselPrice = view.findViewById(R.id.txtDieselPrice)

        val finalPriceMaterialCardView: MaterialCardView = view.findViewById(R.id.finalPriceCard)
        val txtFinalPrice: TextView = view.findViewById(R.id.txtFinalPrice)
        val txtPayButton: TextView = view.findViewById(R.id.txtPayButton)

        val addToCartLayout: LinearLayout = view.findViewById(R.id.addToCartLayout)
        val itemQuantity: TextView = view.findViewById(R.id.item_quantity)
        val itemPlus: TextView = view.findViewById(R.id.item_plus)
        val itemMinus: TextView = view.findViewById(R.id.item_minus)

        // making Loading layout visible
        mainLayout.visibility = View.GONE
        maintenanceLayout.visibility = View.GONE
        loadingAnim.visibility = View.VISIBLE

        // retrieving the details of Fuel Availability
        db = Firebase.firestore

        val docRef = db.collection("App_Utils").document("b7NJ1YMyH94VDv0ytR0h")
        docRef.get().addOnSuccessListener {

            val avbltyStatus = it.data!!.get("Availability").toString()

            if (avbltyStatus == "Online")
                loadFuelDetails()
            else {

                // Fuel is unavailable(OFFLINE) - show Maintenance Icon

                loadingAnim.visibility = View.GONE
                mainLayout.visibility = View.GONE

                maintenanceLayout.visibility = View.VISIBLE

            }
        }

        // retrieving the Customer Name and setting in the greeting message
        val customerName = AppPreferences.customerName.toString()
        val str = SpannableStringBuilder("Hello $customerName,\nFuel up your vehicle Now!")
        str.setSpan(StyleSpan(Typeface.BOLD), 6, 6 + customerName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        txtCustomerName.text = str

        // Add to Cart Layout
        // normal AddToCart Layout
        var tempQuantity = 0
        var finalPrice: Float = 0.00f

        // When User clicks Add to Cart Button
        addToCartLayout.setOnClickListener {

            if (itemQuantity.text.equals("Add to Cart")) {

                // changing the text color of +, - and Quantity
                itemPlus.setTextColor(requireContext().resources.getColor(R.color.white))
                itemMinus.setTextColor(requireContext().resources.getColor(R.color.white))
                itemQuantity.setTextColor(requireContext().resources.getColor(R.color.white))

                itemPlus.visibility = View.VISIBLE
                itemMinus.visibility = View.VISIBLE

                // since its the first time we are setting the quantity to 1
                itemQuantity.text = 10.toString()

                // showing the Final Price card along with the price
                finalPriceMaterialCardView.visibility = View.VISIBLE
                txtFinalPrice.text = "₹ " + "%.2f".format((itemQuantity.text.toString().toInt() * fuelPrice)).toString()

            }
        }

        itemPlus.setOnClickListener {

            // retrieving the current itemQuantity
            tempQuantity = (itemQuantity.text.toString()).toInt()

            // incrementing the quantity
            tempQuantity++

            // setting on the itemQuantity
            itemQuantity.text = tempQuantity.toString()

            // updating the text on Final Price Card
            txtFinalPrice.text = "₹ " + "%.2f".format((itemQuantity.text.toString().toInt() * fuelPrice))

        }

        itemMinus.setOnClickListener {

            // retrieving the current itemQuantity
            tempQuantity = (itemQuantity.text.toString()).toInt()

            // decrementing the quantity
            tempQuantity--

            // if quantity reached 0 then disable colored layout and get back to normal layout
            if (tempQuantity < 10 ) {

                // disappearing the Final Price Material Card View
                finalPriceMaterialCardView.visibility = View.GONE

                itemPlus.visibility = View.GONE
                itemMinus.visibility = View.GONE
                itemQuantity.setTextColor(requireContext().resources.getColor(R.color.white))
                itemQuantity.text = "Add to Cart"

            }
            // if quantity > 0 remain the same layout and perform the basic operation as in add operation
            else {

                // setting on the itemQuantity
                itemQuantity.text = tempQuantity.toString()

                // updating the price on Final Price Card
                txtFinalPrice.text = "₹ " + "%.2f".format((itemQuantity.text.toString().toInt() * fuelPrice))

            }
        }

        // finalPriceCard onClick Listener
        finalPriceMaterialCardView.setOnClickListener {

            val str_finalPrice = txtFinalPrice.text.toString()
            val list = str_finalPrice.split(" ")
            val finalPrice_splitted: Float = list[1].toFloat()

            sendToOrderCheckout(itemQuantity.text.toString(),finalPrice_splitted)
        }

        // Next Page Button onClickListener
        txtPayButton.setOnClickListener {

            val str_finalPrice = txtFinalPrice.text.toString()
            val list = str_finalPrice.split(" ")
            val finalPrice_splitted: Float = list[1].toFloat()

            sendToOrderCheckout(itemQuantity.text.toString(), finalPrice_splitted)

        }

        return view
    }

    @SuppressLint("SetTextI18n")
    private fun loadFuelDetails() {
        db = Firebase.firestore
        db.collection("Fuels_Data")
            .get()
            .addOnSuccessListener {

                for(document in it.documents) {

                    val fuelData = document.toObject<FuelData>()

                    // globally setting the Fuel Price
                    fuelTitle = fuelData!!.fuelName.toString()
                    fuelPrice = fuelData.fuelPrice.toString().toFloat()

                    // Setting the details of fuel dynamically
                    txtFuelTitle.text = fuelData.fuelName.toString()
                    txtFuelDesc.text = fuelData.fuelDesc.toString()
                    txtDieselPrice.text = "₹ " + fuelData.fuelPrice

                    // disabling the loading layout
                    loadingAnim.visibility = View.GONE
                    mainLayout.visibility = View.VISIBLE
                }
            }.addOnFailureListener {


                loadingAnim.visibility = View.VISIBLE
                maintenanceLayout.visibility = View.GONE

                // disabling the Main Layout
                mainLayout.visibility = View.GONE

                Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    private fun sendToOrderCheckout(itemQuantity: String, finalPrice: Float) {

        val intent = Intent(context, OrderCheckoutActivity::class.java)
        intent.putExtra("fuelTitle", fuelTitle)
        intent.putExtra("fuelPrice", fuelPrice)
        intent.putExtra("selectedQuantity", itemQuantity)
        intent.putExtra("finalPrice", finalPrice)
        startActivity(intent)

    }
}