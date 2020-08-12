package com.aviral.foodrunner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aviral.foodrunner.R
import com.aviral.foodrunner.adapter.OrderHistoryRecyclerAdapter
import com.aviral.foodrunner.model.OrderHistory
import com.aviral.foodrunner.util.ConnectionManager
import kotlinx.android.synthetic.main.fragment_order_history.*
import org.json.JSONException


class OrderHistoryFragment : Fragment() {

    lateinit var recyclerOrder: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var recyclerAdapter: OrderHistoryRecyclerAdapter
    lateinit var sharedPreferences: SharedPreferences
    var OrderHistoryList = ArrayList<OrderHistory>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)
        sharedPreferences = activity!!.getSharedPreferences(
            getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )

        recyclerOrder = view.findViewById(R.id.recyclerOrderHistory)
        progressLayout = view.findViewById(R.id.ProgressLayout)
        progressBar = view.findViewById(R.id.ProgressBar)

        val userId = sharedPreferences.getString("user_id", "user_id")
        layoutManager = LinearLayoutManager(activity as Context)

        progressLayout.visibility = View.VISIBLE


        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"

        if (ConnectionManager().checkConnectivity(activity as Context)) {

            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET, url, null, Response.Listener {
                    try {

                        progressLayout.visibility = View.GONE
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")

                        if (success) {
                            val resArray = data.getJSONArray("data")

                            for (i in 0 until resArray.length()) {

                                val OrderJsonObject = resArray.getJSONObject(i)
                                val foodItems = OrderJsonObject.getJSONArray("food_items")
                                val orderDetails = OrderHistory(
                                    OrderJsonObject.getString("order_id"),
                                    OrderJsonObject.getString("restaurant_name"),
                                    OrderJsonObject.getString("total_cost"),
                                    OrderJsonObject.getString("order_placed_at"),
                                    foodItems
                                )



                                OrderHistoryList.add(orderDetails)


                                recyclerAdapter =
                                    OrderHistoryRecyclerAdapter(
                                        activity as Context,
                                        OrderHistoryList
                                    )
                                recyclerOrderHistory.adapter = recyclerAdapter
                                recyclerOrderHistory.layoutManager = layoutManager
                                recyclerOrderHistory.itemAnimator = DefaultItemAnimator()


                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some error occured",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some Unexpected error occured",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                },
                Response.ErrorListener {

                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
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

            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Not found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)

            }
            dialog.create()
            dialog.show()
        }

        return view
    }

}
