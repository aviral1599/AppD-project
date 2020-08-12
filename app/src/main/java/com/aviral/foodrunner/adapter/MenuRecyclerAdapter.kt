package com.aviral.foodrunner.adapter

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.aviral.foodrunner.R
import com.aviral.foodrunner.database.OrderDatabase
import com.aviral.foodrunner.database.OrderEntity

class MenuRecyclerAdapter(
    val context: Context,
    val MenuList: ArrayList<com.aviral.foodrunner.model.Menu>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<MenuRecyclerAdapter.MenuViewHolder>() {

    companion object {
        var isCartEmpty = true
    }

    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtDishName: TextView = view.findViewById(R.id.txtDishName)
        val txtPrice: TextView = view.findViewById(R.id.txtPrice)
        val txtSno: TextView = view.findViewById(R.id.txtSno)
        val llContent: RelativeLayout = view.findViewById(R.id.llContent)
        val btnAdd: Button = view.findViewById(R.id.btnAdd)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_foodmenu_single_row, parent, false)
        return MenuRecyclerAdapter.MenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return MenuList.size
    }

    interface OnItemClickListener {
        fun onAddItemClick(foodItem: OrderEntity)
        fun onRemoveItemClick(foodItem: OrderEntity)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menu = MenuList[position]
        holder.txtDishName.text = menu.name
        holder.txtPrice.text = menu.cost
        holder.txtSno.text = (position + 1).toString()

        val orderEntity = OrderEntity(
            menu.id?.toInt(),
            menu.name,
            menu.cost
        )


        val checkDish = MenuRecyclerAdapter.DBAsyncTask(context, orderEntity, 1).execute()
        val isDish = checkDish.get()
        if (isDish) {

            holder.btnAdd.text = "Remove"
            val color = ContextCompat.getColor(context, R.color.cart_button)
            holder.btnAdd.setBackgroundColor(color)
        } else {
            holder.btnAdd.text = "Add"
            val color = ContextCompat.getColor(context, R.color.cart_add)
            holder.btnAdd.setBackgroundColor(color)

        }


        holder.btnAdd.setOnClickListener {

            if (!MenuRecyclerAdapter.DBAsyncTask(context, orderEntity, 1).execute().get()) {
                val async =
                    MenuRecyclerAdapter.DBAsyncTask(context, orderEntity, 2).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT)
                        .show()
                    holder.btnAdd.text = "Remove"
                    val color = ContextCompat.getColor(context, R.color.cart_button)
                    holder.btnAdd.setBackgroundColor(color)
                    listener.onAddItemClick(orderEntity)
                }
            } else {
                val async = MenuRecyclerAdapter.DBAsyncTask(context, orderEntity, 3).execute()
                val result = async.get()

                if (result) {

                    Toast.makeText(context, "Removed from Cart", Toast.LENGTH_SHORT)
                        .show()
                    holder.btnAdd.text = "Add"
                    val color = ContextCompat.getColor(context, R.color.cart_add)
                    holder.btnAdd.setBackgroundColor(color)
                    listener.onRemoveItemClick(orderEntity)
                }
            }
        }

    }


    class DBAsyncTask(context: Context, val orderEntity: OrderEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db = Room.databaseBuilder(context, OrderDatabase::class.java, "order-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {


            when (mode) {

                1 -> {
                    val dish: OrderEntity? =
                        db.OrderDao().getDishById(orderEntity.id.toString())
                    db.close()
                    return dish != null
                }

                2 -> {
                    db.OrderDao().insertDish(orderEntity)
                    db.close()
                    return true
                }

                3 -> {
                    db.OrderDao().deleteDish(orderEntity)
                    db.close()
                    return true
                }
            }

            return false
        }


    }

    class GetAllDishAsyncTask(
        context: Context
    ) :
        AsyncTask<Void, Void, List<String>>() {

        val db = Room.databaseBuilder(context, OrderDatabase::class.java, "order-db").build()
        override fun doInBackground(vararg params: Void?): List<String> {

            val list = db.OrderDao().getAllDishes()
            val listOfIds = arrayListOf<String>()
            for (i in list) {
                listOfIds.add(i.id.toString())
            }
            return listOfIds
        }
    }


}