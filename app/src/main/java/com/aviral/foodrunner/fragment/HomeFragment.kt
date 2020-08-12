package com.aviral.foodrunner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aviral.foodrunner.R
import com.aviral.foodrunner.adapter.HomeRecyclerAdapter
import com.aviral.foodrunner.model.Restaurant
import com.aviral.foodrunner.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class HomeFragment : Fragment() {

    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    val restaurantInfoList = arrayListOf<Restaurant>()
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var sharedPreferences: SharedPreferences
    var name = "avi"
    var MobileNo = 1234567890
    var EmailAddress = "abc@xyz.com"
    var address = "newDelhi"
    var password = "12345"


    var ratingComparator = Comparator<Restaurant> { rest1, rest2 ->

        if (rest1.Rating.compareTo(rest2.Rating, true) == 0) {
            rest1.RestaurantName.compareTo(rest2.RestaurantName, true)
        } else {
            rest1.Rating.compareTo(rest2.Rating, true)
        }
    }
    var costComparator = Comparator<Restaurant> { rest1, rest2 ->

        if (rest1.Cost.compareTo(rest2.Cost, true) == 0) {
            rest1.RestaurantName.compareTo(rest2.RestaurantName, true)
        } else {
            rest1.Cost.compareTo(rest2.Cost, true)
        }
    }


    lateinit var recyclerAdapter: HomeRecyclerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sharedPreferences = activity!!.getSharedPreferences(
            getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)


        progressLayout = view.findViewById(R.id.ProgressLayout)
        progressBar = view.findViewById(R.id.ProgressBar)
        recyclerHome = view.findViewById(R.id.recyclerHome)

        layoutManager = LinearLayoutManager(activity)

        progressLayout.visibility = View.VISIBLE


        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/ "

        if (ConnectionManager().checkConnectivity(activity as Context)) {

            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {

                        progressLayout.visibility = View.GONE
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")

                        if (success) {
                            val data = data.getJSONArray("data")

                            for (i in 0 until data.length()) {

                                val restaurantJsonObject = data.getJSONObject(i)
                                val restaurantObject = Restaurant(
                                    restaurantJsonObject.getString("id"),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("rating"),
                                    restaurantJsonObject.getString("cost_for_one"),
                                    restaurantJsonObject.getString("image_url")

                                )

                                sharedPreferences.edit()
                                    .putString("rest_id", restaurantJsonObject.getString("id"))
                                    .apply()
                                sharedPreferences.edit()
                                    .putString("rest_name", restaurantJsonObject.getString("name"))
                                    .apply()





                                restaurantInfoList.add(restaurantObject)




                                recyclerAdapter =
                                    HomeRecyclerAdapter(
                                        activity as Context,
                                        restaurantInfoList
                                    )
                                recyclerHome.adapter = recyclerAdapter
                                recyclerHome.layoutManager = layoutManager


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
                }, Response.ErrorListener {

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


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        if (id == R.id.Rating) {
            Collections.sort(restaurantInfoList, ratingComparator)
            restaurantInfoList.reverse()
        } else if (id == R.id.LowtoHigh) {
            Collections.sort(restaurantInfoList, costComparator)
        } else if (id == R.id.HightoLow) {
            Collections.sort(restaurantInfoList, costComparator)
            restaurantInfoList.reverse()
        }
        recyclerAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }


}


