package com.aviral.foodrunner.activity


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.room.Room
import com.aviral.foodrunner.R
import com.aviral.foodrunner.database.RestaurantDatabase
import com.aviral.foodrunner.fragment.*
import com.google.android.material.navigation.NavigationView


class Dashboard : AppCompatActivity() {

    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var frameLayout: FrameLayout
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var toolbar: Toolbar

    lateinit var sharedPreferences: SharedPreferences
    var previousMenuItem: MenuItem? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)
        setContentView(R.layout.dashboard)


        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        drawerLayout = findViewById(R.id.drawerLayout)
        frameLayout = findViewById(R.id.frameLayout)
        navigationView = findViewById(R.id.navigationView)
        toolbar = findViewById(R.id.toolbar)

        setUpToolbar()

        val convertView = LayoutInflater.from(this@Dashboard).inflate(R.layout.drawer_header, null)
        val userName: TextView = convertView.findViewById(R.id.txtDrawerText)
        val userPhone: TextView = convertView.findViewById(R.id.txtDrawerSecondaryText)
        val appIcon: ImageView = convertView.findViewById(R.id.imgDrawerImage)
        userName.text = sharedPreferences.getString("user_name", null)
        val phoneText = "+91-${sharedPreferences.getString("user_mobile_number", null)}"
        userPhone.text = phoneText
        navigationView.addHeaderView(convertView)



        openHome()
        navigationView.setCheckedItem(R.id.home)
        drawerLayout.closeDrawers()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@Dashboard, drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when (it.itemId) {

                R.id.home -> {
                    openHome()
                    navigationView.setCheckedItem(R.id.home)
                    drawerLayout.closeDrawers()
                }
                R.id.MyProfile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            ProfileFragment()
                        )

                        .commit()
                    supportActionBar?.title = "My Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.favourites -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            FavoritesFragment()
                        )

                        .commit()
                    supportActionBar?.title = "Favourites"
                    drawerLayout.closeDrawers()
                }

                R.id.OrderHistory -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            OrderHistoryFragment()
                        )

                        .commit()
                    supportActionBar?.title = "My Previous Orders"
                    drawerLayout.closeDrawers()
                }

                R.id.Faqs -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            FaqFragment()
                        )

                        .commit()
                    supportActionBar?.title = "FAQs"
                    drawerLayout.closeDrawers()
                }
                R.id.logout -> {
                    val builder = AlertDialog.Builder(this@Dashboard)
                    builder.setTitle("Confirmation")
                        .setMessage("Are you sure you want exit?")
                        .setPositiveButton("Yes") { _, _ ->

                            Dashboard.ClearDBAsync(this@Dashboard)
                                .execute().get()
                            val editor = sharedPreferences.edit()
                            editor.clear()
                            editor.apply()
                            finish()
                            var intent = Intent(this@Dashboard, Login::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()


                        }
                        .setNegativeButton("No") { _, _ ->
                            openHome()
                        }
                        .create()
                        .show()
                    drawerLayout.closeDrawers()
                }


            }
            return@setNavigationItemSelectedListener true

        }

    }


    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Dashboard"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    fun openHome() {

        val fragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, HomeFragment())
        transaction.commit()
        supportActionBar?.title = "Home"
    }

    override fun onBackPressed() {

        val frag = supportFragmentManager.findFragmentById(R.id.frameLayout)

        when (frag) {

            is HomeFragment -> finishAffinity()
            !is HomeFragment -> openHome()
            else -> super.onBackPressed()
        }

    }

    class ClearDBAsync(context: Context) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.restaurantDao().deleteAll()
            db.close()
            return true
        }

    }


}



