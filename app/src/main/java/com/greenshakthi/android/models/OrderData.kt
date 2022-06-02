package com.greenshakthi.android.models

data class OrderData(
    var orderID: String = "",
    var dateTimePlaced: String = "",
    var finalPrice: String = "",
    var address: String = "",
    var orderStatus: String = "",
    var transactionMode: String = "",
    var transactionID: String = "",
    var fuelName: String = "",
    var fuelUnitPrice: String = "",
    var fuelQuantitySelected: String = "",
    var custName: String = "",
    var custPhone: String = "",
    var custID: String = "",
    var key: String = ""
)
