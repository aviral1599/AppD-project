package com.aviral.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.aviral.foodrunner.R
import com.aviral.foodrunner.activity.MenuActivity
import com.aviral.foodrunner.database.RestaurantDatabase
import com.aviral.foodrunner.database.RestaurantEntity
import com.aviral.foodrunner.model.Restaurant
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter(val context: Context, val itemList: ArrayList<Restaurant>) :
    RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {


    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtNameRestaurant: TextView = view.findViewById(R.id.txtNameRestaurant)
        val txtPrice: TextView = view.findViewById(R.id.txtPrice)
        val txtRating: TextView = view.findViewById(R.id.txtRating)
        val llContent: CardView = view.findViewById(R.id.llContent)
        val imgListImage: ImageView = view.findViewById(R.id.imgListImage)
        val imgFav: ImageView = view.findViewById(R.id.imgFav)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_home_single_row, parent, false)
        return HomeViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val restaurant = itemList[position]
        holder.txtNameRestaurant.text = restaurant.RestaurantName
        holder.txtPrice.text = restaurant.Cost
        holder.txtRating.text = restaurant.Rating
        Picasso.get().load(restaurant.image).error(R.drawable.logo).into(holder.imgListImage)
        holder.llContent.setOnClickListener {
            val intent = Intent(context, MenuActivity::class.java)
            intent.putExtra("id", restaurant.id)
            intent.putExtra("name", restaurant.RestaurantName)
            context.startActivity(intent)
        }

        val listOfFavourites = GetAllFavAsyncTask(context).execute().get()

        if (listOfFavourites.isNotEmpty() && listOfFavourites.contains(restaurant.id.toString())) {
            holder.imgFav.setImageResource(R.drawable.ic_favfill)
        } else {
            holder.imgFav.setImageResource(R.drawable.ic_fav)
        }


        holder.imgFav.setOnClickListener {
            val restaurantEntity = RestaurantEntity(
                restaurant.id?.toInt(),
                restaurant.RestaurantName,
                restaurant.Rating,
                restaurant.Cost,
                restaurant.image
            )

            if (!DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val async =
                    DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    holder.imgFav.setImageResource(R.drawable.ic_favfill)
                    Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                val async = DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()

                if (result) {
                    holder.imgFav.setImageResource(R.drawable.ic_fav)
                    Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }


    }

    class DBAsyncTask(context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {


            when (mode) {

                1 -> {
                    val res: RestaurantEntity? =
                        db.restaurantDao().getRestaurantById(restaurantEntity.id.toString())
                    db.close()
                    return res != null
                }

                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }

                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }

            return false
        }


    }


    class GetAllFavAsyncTask(
        context: Context
    ) :
        AsyncTask<Void, Void, List<String>>() {

        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): List<String> {

            val list = db.restaurantDao().getAllRestaurants()
            val listOfIds = arrayListOf<String>()
            for (i in list) {
                listOfIds.add(i.id.toString())
            }
            return listOfIds
        }
    }
}