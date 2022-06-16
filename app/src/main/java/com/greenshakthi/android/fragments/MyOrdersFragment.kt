package com.greenshakthi.android.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.greenshakthi.android.adapters.MyOrdersAdapter
import com.greenshakthi.android.R
import com.greenshakthi.android.models.OrderData
import com.greenshakthi.android.utils.AppPreferences
import java.util.*
import kotlin.collections.ArrayList

class MyOrdersFragment : Fragment() {


    lateinit var myOrdersRecycler: RecyclerView
    lateinit var myOrdersList: ArrayList<OrderData>

    /*lateinit var sortedList: List<OrderData>
    lateinit var myOrdersAdapter: MyOrdersAdapter*/

    lateinit var txtMyOrders: TextView

    private var customerUUID: String = ""

    lateinit var noDataAnim: LottieAnimationView
    lateinit var txtNoDataAnim: TextView

    lateinit var db: FirebaseFirestore

    lateinit var mainLayout: RelativeLayout
    lateinit var loadingAnim: LottieAnimationView

    lateinit var edtSearchOrders: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_my_orders, container, false)

        //retainInstance = true

        AppPreferences.init(requireContext())

        // Checking Internet Connection
        if(!AppPreferences.isOnline()) AppPreferences.showNetworkErrorPage(requireContext())

        customerUUID = AppPreferences.customerID.toString()

        mainLayout = view.findViewById(R.id.mainLayout)
        loadingAnim = view.findViewById(R.id.loadingAnim)

        txtMyOrders = view.findViewById(R.id.txtMyOrders)

        edtSearchOrders = view.findViewById(R.id.edtSearchOrder)
        // emptying the Edit Text
        edtSearchOrders.setText("")

        txtNoDataAnim = view.findViewById(R.id.txtNoDataAnim)
        noDataAnim = view.findViewById(R.id.no_data_anim)

        db = Firebase.firestore

        myOrdersRecycler = view.findViewById(R.id.myOrdersRecycler)
        myOrdersList = ArrayList()

        mainLayout.visibility = View.GONE
        loadingAnim.visibility = View.VISIBLE

        // loading Order Data
        loadOrdersData()


        return view
    }

/*
    private fun filter(text: String) {
        val filteredlist: ArrayList<OrderData> = ArrayList()
        for (item in sortedList) {
            if (item.fuelQuantitySelected!!.toLowerCase().contains(text.toLowerCase()) || item.dateTimePlaced!!.toLowerCase().contains(text.toLowerCase()) || item.orderStatus!!.toLowerCase().contains(text.toLowerCase()) || item.fuelName!!.toLowerCase().contains(text.toLowerCase()) || item.finalPrice!!.toLowerCase().contains(text.toLowerCase()) || item.orderID!!.toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item)
            }
        }
        myOrdersAdapter.filterList(filteredlist)
    }*/

    private fun loadOrdersData() {

        myOrdersList.clear()

        db.collection("Orders_Data")
            .whereEqualTo("custID",customerUUID)
            .get()
            .addOnSuccessListener {

                for(document in it.documents) {
                    val orderData = document.toObject<OrderData>()
                    myOrdersList.add(orderData!!)
                }


                if(!myOrdersList.isEmpty()) {

                    // Disabling the loading Animation
                    loadingAnim.visibility = View.GONE
                    mainLayout.visibility = View.VISIBLE

                    // Since the order data is present - Show the Recycler View
                    myOrdersRecycler.visibility = View.VISIBLE
                    edtSearchOrders.visibility = View.VISIBLE
                    txtMyOrders.visibility = View.VISIBLE

                    // Disabling the No Data Anim
                    txtNoDataAnim.visibility = View.GONE
                    noDataAnim.visibility = View.GONE

                    val sortedList = myOrdersList.sortedWith(compareByDescending { it.dateTimePlaced }, ) as List<OrderData>

                    val myOrdersAdapter = context?.let { MyOrdersAdapter(it, sortedList) }!!
                    val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    myOrdersRecycler.adapter = myOrdersAdapter
                    myOrdersRecycler.setHasFixedSize(true)
                    myOrdersRecycler.layoutManager = linearLayoutManager

                    // Search Option

                    // search EditText Listener
                    edtSearchOrders.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                        override fun afterTextChanged(s: Editable) {

                            // filtering the data based on the search phrase
                            val filteredlist: ArrayList<OrderData> = ArrayList()
                            for (item in sortedList) {
                                if (item.fuelQuantitySelected.lowercase(Locale.getDefault()).contains(s.toString().lowercase(Locale.getDefault())) ||
                                    item.dateTimePlaced.lowercase(Locale.getDefault()).contains(s.toString().lowercase(Locale.getDefault())) ||
                                    item.orderStatus.lowercase(Locale.getDefault()).contains(s.toString().lowercase(Locale.getDefault())) ||
                                    item.fuelName.lowercase(Locale.getDefault()).contains(s.toString().lowercase(Locale.getDefault())) ||
                                    item.finalPrice.lowercase(Locale.getDefault()).contains(s.toString().lowercase(Locale.getDefault())) ||
                                    item.orderID.lowercase(Locale.getDefault()).contains(s.toString().lowercase(Locale.getDefault()))) {

                                          filteredlist.add(item)

                                }
                            }
                            myOrdersAdapter.filterList(filteredlist)

                        }
                    })


                } else {

                    // Disabling the loading Animation
                    loadingAnim.visibility = View.GONE
                    mainLayout.visibility = View.VISIBLE

                    // Disabling the Recycler View
                    myOrdersRecycler.visibility = View.GONE
                    edtSearchOrders.visibility = View.GONE
                    txtMyOrders.visibility = View.GONE

                    // No Data in MyOrders - Disable My Orders Text and Edit Text
                    txtNoDataAnim.visibility = View.VISIBLE
                    noDataAnim.visibility = View.VISIBLE

                  //  Toast.makeText(context, "Sorry, No Orders found!", Toast.LENGTH_SHORT).show()
                }
        }

    }

}