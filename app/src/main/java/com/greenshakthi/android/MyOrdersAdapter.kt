package com.greenshakthi.android

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.greenshakthi.android.models.OrderData
import org.w3c.dom.Text

class MyOrdersAdapter(private val context: Context, private val myOrdersList: ArrayList<OrderData>):
    RecyclerView.Adapter<MyOrdersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrdersAdapter.ViewHolder {
        val  view = LayoutInflater.from(parent.context).inflate(R.layout.layout_order_info,parent,false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onBindViewHolder(holder: MyOrdersAdapter.ViewHolder, position: Int) {

            holder.fuelTitle.text = myOrdersList[position].fuelName
            holder.orderID.text = "#" + myOrdersList[position].orderID
            holder.orderStatus.text = myOrdersList[position].orderStatus
            holder.quantity_UnitPrice.text = myOrdersList[position].fuelQuantitySelected + " x " + myOrdersList[position].fuelUnitPrice
            holder.finalPrice.text = "₹ " + myOrdersList[position].finalPrice

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