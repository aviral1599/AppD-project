package com.aviral.foodrunner.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
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
import com.aviral.foodrunner.adapter.OrderRecyclerAdapter
import com.aviral.foodrunner.database.OrderDatabase
import com.aviral.foodrunner.database.OrderEntity
import com.aviral.foodrunner.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class OrderList : AppCompatActivity() {

    lateinit var recyclerOrder: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var recyclerAdapter: OrderRecyclerAdapter
    lateinit var toolbar: Toolbar
    lateinit var sharedPreferences: SharedPreferences
    lateinit var btnPlace: Button
    var dbRestList = listOf<OrderEntity>()


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_order_list)


        recyclerOrder = findViewById(R.id.RecyclerDish)
        progressLayout = findViewById(R.id.ProgressLayout)
        progressBar = findViewById(R.id.ProgressBar)
        btnPlace = findViewById(R.id.btnPlace)



        layoutManager = LinearLayoutManager(this@OrderList)

        val userId = sharedPreferences.getString("user_id", "user_id")

        val name = intent.getStringExtra("RestName")
        val Name = findViewById<TextView>(R.id.txtrecRestaurant).apply {
            text = name
        }
        val restId = intent.getStringExtra("Restid")


        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"

        val listOfIds = GetAllDishAsyncTask(this@OrderList).execute().get()
        dbRestList = RetrieveOrder(this@OrderList as Context).execute().get()
        val total = calculateDBAsync(this@OrderList as Context).execute().get()
        val Total = findViewById<Button>(R.id.btnPlace).apply {
            var Total = total.toString()
            text = "Place Order(Total: Rs. $Total)"
        }

        btnPlace.setOnClickListener {

            val queue = Volley.newRequestQueue(this@OrderList as Context)

            val url = "http://13.235.250.119/v2/place_order/fetch_result/"


            val params = JSONObject()
            params.put("user_id", userId)
            params.put("restaurant_id", restId)
            params.put("total_cost", total)
            val foodArray = JSONArray()
            for (i in 0 until listOfIds.size) {

                val foodId = JSONObject()
                foodId.put("food_item_id", listOfIds[i])
                foodArray.put(i, foodId)
            }
            params.put("food", foodArray)


            if (ConnectionManager().checkConnectivity(this@OrderList as Context)) {

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.POST, url, params, Response.Listener {
                        try {

                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")

                            if (success) {

                                startActivity(
                                    Intent(
                                        this@OrderList,
                                        OrderSuccess::class.java
                                    )
                                )
                                OrderList.ClearDBAsync(this@OrderList as Context).execute().get()

                            } else {
                                val errorMessage = data.getString("Some Error Occurred")
                                Toast.makeText(
                                    this@OrderList as Context,
                                    errorMessage,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } catch (e: JSONException) {
                            Toast.makeText(
                                this@OrderList as Context,
                                "Some Unexpected error occured",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            e.printStackTrace()
                        }
                    },
                    Response.ErrorListener {

                        if (this@OrderList != null) {
                            Toast.makeText(
                                this@OrderList as Context,
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

                val dialog = AlertDialog.Builder(this@OrderList as Context)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Not found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    this@OrderList?.finish()
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@OrderList as Activity)

                }
                dialog.create()
                dialog.show()
            }


        }


        if (this@OrderList != null) {
            progressLayout.visibility = View.GONE
            recyclerAdapter = OrderRecyclerAdapter(this@OrderList as Context, dbRestList)
            recyclerOrder.adapter = recyclerAdapter
            recyclerOrder.layoutManager = layoutManager

        }

    }

    class RetrieveOrder(val context: Context) : AsyncTask<Void, Void, List<OrderEntity>>() {
        override fun doInBackground(vararg params: Void?): List<OrderEntity> {

            val db = Room.databaseBuilder(context, OrderDatabase::class.java, "order-db").build()

            return db.OrderDao().getAllDishes()
        }

    }

    class calculateDBAsync(context: Context) : AsyncTask<Void, Void, Int>() {
        val db = Room.databaseBuilder(context, OrderDatabase::class.java, "order-db").build()
        override fun doInBackground(vararg params: Void?): Int {
            val sum = db.OrderDao().totalCost()
            db.close()
            return sum
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

    class ClearDBAsync(context: Context) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, OrderDatabase::class.java, "order-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.OrderDao().deleteAll()
            db.close()
            return true
        }

    }

}

