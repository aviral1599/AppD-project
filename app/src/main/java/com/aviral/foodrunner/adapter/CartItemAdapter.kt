package com.aviral.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aviral.foodrunner.R
import com.aviral.foodrunner.model.Menu

class CartItemAdapter(val context: Context, private val cartList: ArrayList<Menu>) :
    RecyclerView.Adapter<CartItemAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtDishName: TextView = view.findViewById(R.id.txtDishName)
        val txtCost: TextView = view.findViewById(R.id.txtCost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item_single_row, parent, false)
        return CartItemAdapter.CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartObject = cartList[position]
        holder.txtDishName.text = cartObject.name
        holder.txtCost.text = cartObject.cost
    }
}