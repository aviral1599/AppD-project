package com.aviral.foodrunner.model

import org.json.JSONArray

data class OrderHistory(

    val orderId: String,
    val restaurantName: String,
    val totalCost: String,
    val orderTime: String,
    val foodItems: JSONArray

)