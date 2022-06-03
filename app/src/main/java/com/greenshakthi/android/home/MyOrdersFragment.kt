package com.greenshakthi.android.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
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

class MyOrdersFragment : Fragment() {


    lateinit var myOrdersRecycler: RecyclerView
    lateinit var myOrdersList: ArrayList<OrderData>

    private var customerUUID: String = ""

    lateinit var noDataAnim: LottieAnimationView
    lateinit var txtNoDataAnim: TextView

    lateinit var db: FirebaseFirestore

    lateinit var mainLayout: RelativeLayout
    lateinit var loadingAnim: LottieAnimationView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_my_orders, container, false)

        AppPreferences.init(requireContext())

        customerUUID = AppPreferences.customerID.toString()

        mainLayout = view.findViewById(R.id.mainLayout)
        loadingAnim = view.findViewById(R.id.loadingAnim)

        txtNoDataAnim = view.findViewById(R.id.txtNoDataAnim)
        noDataAnim = view.findViewById(R.id.no_data_anim)

        db = Firebase.firestore

        myOrdersRecycler = view.findViewById(R.id.myOrdersRecycler)
        myOrdersList = ArrayList()

        mainLayout.visibility = View.GONE
        loadingAnim.visibility = View.VISIBLE

        loadOrdersData()


        return view
    }

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

                    myOrdersRecycler.visibility = View.VISIBLE

                    txtNoDataAnim.visibility = View.GONE
                    noDataAnim.visibility = View.GONE

                    val sortedList = myOrdersList.sortedWith(compareByDescending { it.dateTimePlaced }, ) as List<OrderData>

                    val myOrdersAdapter = context?.let { MyOrdersAdapter(it, sortedList) }
                    val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
                    myOrdersRecycler.adapter = myOrdersAdapter
                    myOrdersRecycler.layoutManager = linearLayoutManager


                    loadingAnim.visibility = View.GONE
                    mainLayout.visibility = View.VISIBLE

                } else {

                    loadingAnim.visibility = View.GONE
                    mainLayout.visibility = View.VISIBLE
                      
                    // No Data in MyOrders
                    txtNoDataAnim.visibility = View.VISIBLE
                    noDataAnim.visibility = View.VISIBLE

                    myOrdersRecycler.visibility = View.GONE

                  //  Toast.makeText(context, "Sorry, No Orders found!", Toast.LENGTH_SHORT).show()
                }
        }

    }

}