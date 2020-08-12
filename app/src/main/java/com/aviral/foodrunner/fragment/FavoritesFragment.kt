package com.aviral.foodrunner.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.aviral.foodrunner.R
import com.aviral.foodrunner.adapter.HomeRecyclerAdapter
import com.aviral.foodrunner.database.RestaurantDatabase
import com.aviral.foodrunner.database.RestaurantEntity
import com.aviral.foodrunner.model.Restaurant


class FavoritesFragment : Fragment() {

    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var rlNoItem: RelativeLayout
    var listOfIds = arrayListOf<Restaurant>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_favorites, container, false)


        progressLayout = view.findViewById(R.id.ProgressLayout)
        progressBar = view.findViewById(R.id.ProgressBar)
        rlNoItem = view.findViewById(R.id.rlNoItem)
        progressLayout.visibility = View.VISIBLE

        setUpRecycler(view)

        return view
    }

    fun setUpRecycler(view: View) {

        recyclerHome = view.findViewById(R.id.recyclerFav)

        val backgroundList = GetAllFavAsyncTask(activity as Context).execute().get()

        if (backgroundList.isEmpty()) {
            progressLayout.visibility = View.GONE
            rlNoItem.visibility = View.VISIBLE
        } else {
            progressLayout.visibility = View.GONE
            rlNoItem.visibility = View.GONE
            for (i in backgroundList) {
                listOfIds.add(
                    Restaurant(
                        i.id.toString(),
                        i.RestaurantName,
                        i.Rating,
                        i.Cost,
                        i.image
                    )
                )
            }
            recyclerAdapter = HomeRecyclerAdapter(activity as Context, listOfIds)
            layoutManager = LinearLayoutManager(activity)
            recyclerHome.layoutManager = layoutManager
            recyclerHome.itemAnimator = DefaultItemAnimator()
            recyclerHome.adapter = recyclerAdapter
            recyclerHome.setHasFixedSize(true)
        }
    }


    class GetAllFavAsyncTask(
        context: Context
    ) :
        AsyncTask<Void, Void, List<RestaurantEntity>>() {

        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {

            return db.restaurantDao().getAllRestaurants()
        }
    }

}
