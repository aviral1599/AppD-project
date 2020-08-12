package com.aviral.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aviral.foodrunner.R
import com.aviral.foodrunner.database.OrderEntity

class OrderRecyclerAdapter(val context: Context, val itemList: List<OrderEntity>) :
    RecyclerView.Adapter<OrderRecyclerAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtDishName: TextView = view.findViewById(R.id.txtDishName)
        val txtCost: TextView = view.findViewById(R.id.txtCost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_order_single_row, parent, false)
        return OrderRecyclerAdapter.OrderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = itemList[position]
        holder.txtDishName.text = order.DishName
        holder.txtCost.text = order.DishPrice


    }
}