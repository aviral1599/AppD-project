package com.aviral.foodrunner.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aviral.foodrunner.R
import com.aviral.foodrunner.adapter.MenuRecyclerAdapter
import com.aviral.foodrunner.database.OrderDatabase
import com.aviral.foodrunner.database.OrderEntity
import com.aviral.foodrunner.database.RestaurantDatabase
import com.aviral.foodrunner.model.Menu
import com.aviral.foodrunner.model.Restaurant
import com.aviral.foodrunner.util.ConnectionManager
import org.json.JSONException

class MenuActivity : AppCompatActivity() {

    lateinit var recyclerDish: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    val MenuList = arrayListOf<Menu>()
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var recyclerAdapter: MenuRecyclerAdapter
    lateinit var toolbar: Toolbar
    var restaurant = ArrayList<Restaurant>()
    var orderList = ArrayList<OrderEntity>()

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var btnProceed: Button
    }

    var id: String? = "100"
    var name: String? = "Burger"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.food_menu)

        id = intent.getStringExtra("id")
        name = intent.getStringExtra("name")




        recyclerDish = findViewById(R.id.recyclerDish)
        progressLayout = findViewById(R.id.ProgressLayout)
        progressBar = findViewById(R.id.ProgressBar)
        btnProceed = findViewById(R.id.btnProceed)
        layoutManager = LinearLayoutManager(this@MenuActivity as Context)
        progressLayout.visibility = VISIBLE


        btnProceed = findViewById(R.id.btnProceed) as Button
        btnProceed.visibility = View.GONE


        if (intent != null) {
            id = intent.getStringExtra("id")
            name = intent.getStringExtra("name")
        } else {

            Toast.makeText(this@MenuActivity, "Error ocurred", Toast.LENGTH_SHORT).show()
        }
        if (id == "100" || name == "Burger") {
            Toast.makeText(this@MenuActivity, "Error ocurred", Toast.LENGTH_SHORT).show()
        }

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "$name"

        btnProceed.setOnClickListener {
            var intent = Intent(this@MenuActivity, OrderList::class.java)
            intent.putExtra("RestName", name)
            intent.putExtra("Restid", id)
            startActivity(intent)
        }


        val queue = Volley.newRequestQueue(this@MenuActivity as Context)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$id"

        if (ConnectionManager().checkConnectivity(this@MenuActivity as Context)) {

            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET, url, null, Response.Listener {
                    try {

                        progressLayout.visibility = View.GONE


                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")

                        if (success) {
                            val data = data.getJSONArray("data")

                            for (i in 0 until data.length()) {

                                val restaurantJsonObject = data.getJSONObject(i)
                                val restaurantObject = Menu(
                                    restaurantJsonObject.getString("id"),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("cost_for_one")


                                )

                                MenuList.add(restaurantObject)



                                recyclerAdapter =
                                    MenuRecyclerAdapter(
                                        this@MenuActivity as Context,
                                        MenuList,
                                        object : MenuRecyclerAdapter.OnItemClickListener {
                                            override fun onAddItemClick(restaurantObject: OrderEntity) {
                                                orderList.add(restaurantObject)
                                                if (orderList.size > 0) {
                                                    btnProceed.visibility = View.VISIBLE
                                                    MenuRecyclerAdapter.isCartEmpty = false
                                                }
                                            }

                                            override fun onRemoveItemClick(restaurantObject: OrderEntity) {
                                                orderList.remove(restaurantObject)
                                                if (orderList.isEmpty()) {
                                                    btnProceed.visibility = View.GONE
                                                    MenuRecyclerAdapter.isCartEmpty = true
                                                }
                                            }
                                        })

                                val mLayoutManager = LinearLayoutManager(this@MenuActivity)
                                recyclerDish.adapter = recyclerAdapter
                                recyclerDish.layoutManager = layoutManager

                            }


                        } else {
                            Toast.makeText(
                                this@MenuActivity as Context,
                                "Some error occured",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@MenuActivity as Context,
                            "Some Unexpected error occured",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                },
                Response.ErrorListener {

                    if (this@MenuActivity != null) {
                        Toast.makeText(
                            this@MenuActivity as Context,
                            "Volley error occured",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {

                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "07e70183bece9b"

                    return headers
                }

            }
            queue.add(jsonObjectRequest)

        } else {

            val dialog = AlertDialog.Builder(this@MenuActivity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Not found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                this@MenuActivity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this@MenuActivity as Activity)

            }
            dialog.create()
            dialog.show()
        }


    }

    class GetAllDishAsyncTask(
        context: Context
    ) :
        AsyncTask<Void, Void, List<OrderEntity>>() {

        val db = Room.databaseBuilder(context, OrderDatabase::class.java, "order-db").build()
        override fun doInBackground(vararg params: Void?): List<OrderEntity> {

            val list = db.OrderDao().getAllDishes()

            return list
        }
    }

    class ClearDBAsync(context: Context) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, OrderDatabase::class.java, "order-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.OrderDao().deleteAll()
            db.close()
            return true
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        MenuActivity.ClearDBAsync(this@MenuActivity).execute().get()
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
