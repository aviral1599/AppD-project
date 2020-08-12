package com.aviral.foodrunner.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.aviral.foodrunner.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_page)

        title = "FoodRunner"

        Handler().postDelayed(
            {
                val startAct = Intent(this@MainActivity, Login::class.java)
                startActivity(startAct)
            }, 1000
        )
    }
}
