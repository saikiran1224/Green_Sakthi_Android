package com.greenshakthi.android.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.greenshakthi.android.R
import com.greenshakthi.android.models.OrderData

class MyOrdersAdapter(private val context: Context, private val myOrdersList: ArrayList<OrderData>):
    RecyclerView.Adapter<MyOrdersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val  view = LayoutInflater.from(parent.context).inflate(R.layout.layout_order_info,parent,false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor", "UseCompatTextViewDrawableApis")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.fuelTitle.text = myOrdersList[position].fuelName
            holder.orderID.text = "#" + myOrdersList[position].orderID
            holder.orderStatus.text = myOrdersList[position].orderStatus
            holder.quantity_UnitPrice.text = myOrdersList[position].fuelQuantitySelected + " x " + myOrdersList[position].fuelUnitPrice
            holder.finalPrice.text = "₹ " + myOrdersList[position].finalPrice

            val orderStatus = myOrdersList[position].orderStatus
            if(orderStatus == "Placed") {
                holder.orderStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_placed, 0, 0, 0)
                holder.orderStatus.compoundDrawableTintList = ColorStateList.valueOf(context.resources.getColor(R.color.white))
            } else if(orderStatus == "Confirmed") {
                holder.orderStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_confirmed_svgrepo_com, 0, 0, 0)
                holder.orderStatus.compoundDrawableTintList = ColorStateList.valueOf(context.resources.getColor(R.color.white))
            } else if(orderStatus == "In Transit") {
                holder.orderStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_in_transit, 0, 0, 0)
                holder.orderStatus.compoundDrawableTintList = ColorStateList.valueOf(context.resources.getColor(R.color.white))
            } else {
                holder.orderStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_delivered, 0, 0, 0)
                holder.orderStatus.compoundDrawableTintList = ColorStateList.valueOf(context.resources.getColor(R.color.white))
            }

        if (myOrdersList[position].transactionMode == "COD") {
                holder.transactionStatus.text = "Amount Due"
                holder.transactionStatus.setTextColor(context.resources.getColor(R.color.amount_due_color))
            } else {
                holder.transactionStatus.text = "Amount Paid"
                holder.transactionStatus.setTextColor(context.resources.getColor(R.color.green))
            }

            holder.txtTimeStamp.text = myOrdersList[position].dateTimePlaced
    }

    override fun getItemCount(): Int {
        return myOrdersList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val fuelTitle = itemView.findViewById<TextView>(R.id.txtFuelName)
        val orderID = itemView.findViewById<TextView>(R.id.txtOrderId)
        val orderStatus = itemView.findViewById<TextView>(R.id.txtOrderStatus)
        val quantity_UnitPrice = itemView.findViewById<TextView>(R.id.quantity_UnitPrice)
        val finalPrice = itemView.findViewById<TextView>(R.id.txtFinalPrice)
        val transactionStatus = itemView.findViewById<TextView>(R.id.txtTransStatus)
        val txtTimeStamp = itemView.findViewById<TextView>(R.id.txtTimeStamp)

    }

}