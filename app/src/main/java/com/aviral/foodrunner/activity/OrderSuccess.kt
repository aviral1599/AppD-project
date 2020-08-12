package com.aviral.foodrunner.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.aviral.foodrunner.R

class OrderSuccess : AppCompatActivity() {

    lateinit var imgSuccess: ImageView
    lateinit var txtSuccess: TextView
    lateinit var btnOk: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_success)

        imgSuccess = findViewById(R.id.imgSuccess)
        txtSuccess = findViewById(R.id.txtSuccess)
        btnOk = findViewById(R.id.btnOk)

        btnOk.setOnClickListener {

            var intent = Intent(this@OrderSuccess, Dashboard::class.java)
            startActivity(intent)
        }
    }
}
