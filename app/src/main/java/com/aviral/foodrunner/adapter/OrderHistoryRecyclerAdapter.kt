package com.aviral.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aviral.foodrunner.R
import com.aviral.foodrunner.model.Menu
import com.aviral.foodrunner.model.OrderHistory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderHistoryRecyclerAdapter(
    val context: Context,
    val OrderHistoryList: ArrayList<OrderHistory>
) : RecyclerView.Adapter<OrderHistoryRecyclerAdapter.OrderHistoryViewHolder>() {

    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtRestName: TextView = view.findViewById(R.id.txtRestName)
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val txtPrice: TextView = view.findViewById(R.id.txtPrice)
        val recyclerResHistory: RecyclerView = view.findViewById(R.id.recyclerMenuHistory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_history_single_row, parent, false)
        return OrderHistoryRecyclerAdapter.OrderHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return OrderHistoryList.size
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {

        val orderHistoryObject = OrderHistoryList[position]
        holder.txtRestName.text = orderHistoryObject.restaurantName
        holder.txtPrice.text = orderHistoryObject.totalCost
        holder.txtDate.text = formatDate(orderHistoryObject.orderTime)
        setUpRecycler(holder.recyclerResHistory, orderHistoryObject)
    }

    private fun setUpRecycler(recyclerResHistory: RecyclerView, orderHistoryList: OrderHistory) {
        val foodItemsList = ArrayList<Menu>()
        for (i in 0 until orderHistoryList.foodItems.length()) {
            val foodJson = orderHistoryList.foodItems.getJSONObject(i)
            foodItemsList.add(
                Menu(
                    foodJson.getString("food_item_id"),
                    foodJson.getString("name"),
                    foodJson.getString("cost")
                )
            )

        }
        val cartItemAdapter = CartItemAdapter(context, foodItemsList)
        val mLayoutManager = LinearLayoutManager(context)
        recyclerResHistory.layoutManager = mLayoutManager
        recyclerResHistory.itemAnimator = DefaultItemAnimator()
        recyclerResHistory.adapter = cartItemAdapter

    }

    private fun formatDate(dateString: String): String? {
        val inputFormatter = SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.ENGLISH)
        val date: Date = inputFormatter.parse(dateString) as Date

        val outputFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return outputFormatter.format(date)
    }
}