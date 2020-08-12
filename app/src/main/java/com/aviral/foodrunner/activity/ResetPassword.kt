package com.aviral.foodrunner.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aviral.foodrunner.R
import com.aviral.foodrunner.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ResetPassword : AppCompatActivity() {

    lateinit var etOtp: EditText
    lateinit var etNewPass: EditText
    lateinit var etConfPass: EditText
    lateinit var btnSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        etOtp = findViewById(R.id.etOtp)
        etNewPass = findViewById(R.id.etNewPass)
        etConfPass = findViewById(R.id.etConfPass)
        btnSubmit = findViewById(R.id.btnSubmit)

        val Mobile = intent.getStringExtra("number")

        btnSubmit.setOnClickListener {

            val Otp = etOtp.text.toString()
            val NewPass = etNewPass.text.toString()
            val ConfPass = etConfPass.text.toString()

            val queue = Volley.newRequestQueue(this@ResetPassword as Context)

            val url = "http://13.235.250.119/v2/reset_password/fetch_result"
            val params = JSONObject()
            params.put("mobile_number", Mobile)
            params.put("password", NewPass)
            params.put("otp", Otp)

            if (ConnectionManager().checkConnectivity(this@ResetPassword as Context)) {

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.POST, url, params, Response.Listener {
                        try {

                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            val message = data.getString("successMessage")

                            if (success) {
                                Toast.makeText(
                                    this@ResetPassword as Context,
                                    "$message",
                                    Toast.LENGTH_LONG
                                )
                                    .show()

                                var intent = Intent(this@ResetPassword, Login::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this@ResetPassword as Context,
                                    "Some Error Occurred",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }


                        } catch (e: JSONException) {
                            Toast.makeText(
                                this@ResetPassword as Context,
                                "Some Unexpected error occured",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    },
                    Response.ErrorListener {

                        if (this@ResetPassword != null) {
                            Toast.makeText(
                                this@ResetPassword as Context,
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

                val dialog = AlertDialog.Builder(this@ResetPassword as Context)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Not found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    this@ResetPassword?.finish()
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@ResetPassword as Activity)

                }
                dialog.create()
                dialog.show()
            }

        }

    }
}
