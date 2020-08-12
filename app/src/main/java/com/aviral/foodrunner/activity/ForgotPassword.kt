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

class ForgotPassword : AppCompatActivity() {


    lateinit var etMobileNumber: EditText
    lateinit var etEmail: EditText
    lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        etMobileNumber = findViewById(R.id.etMobileNumber)
        etEmail = findViewById(R.id.etEmail)
        btnNext = findViewById(R.id.btnNext)

        btnNext.setOnClickListener {

            val mobileNumber = etMobileNumber.text.toString()
            val email = etEmail.text.toString()

            val queue = Volley.newRequestQueue(this@ForgotPassword as Context)

            val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
            val params = JSONObject()
            params.put("mobile_number", mobileNumber)
            params.put("email", email)

            if (ConnectionManager().checkConnectivity(this@ForgotPassword as Context)) {

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.POST, url, params, Response.Listener {
                        try {

                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            val response = data.getBoolean("first_try")

                            if (success) {
                                if (response) {

                                    var intent =
                                        Intent(this@ForgotPassword, ResetPassword::class.java)
                                    intent.putExtra("number", mobileNumber)
                                    startActivity(intent)
                                    Toast.makeText(
                                        this@ForgotPassword as Context,
                                        "OTP has been send to your registered email-id",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                } else {
                                    var intent =
                                        Intent(this@ForgotPassword, ResetPassword::class.java)
                                    intent.putExtra("number", mobileNumber)
                                    startActivity(intent)
                                    Toast.makeText(
                                        this@ForgotPassword as Context,
                                        "OTP has been send to your registered email-id(Enter the previous OTP)",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                }
                            } else {
                                Toast.makeText(
                                    this@ForgotPassword as Context,
                                    "Some Error Occurred",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }


                        } catch (e: JSONException) {
                            Toast.makeText(
                                this@ForgotPassword as Context,
                                "Some Unexpected error occured",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            var intent = Intent(this@ForgotPassword, ForgotPassword::class.java)
                            startActivity(intent)
                        }
                    },
                    Response.ErrorListener {

                        if (this@ForgotPassword != null) {
                            Toast.makeText(
                                this@ForgotPassword as Context,
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

                val dialog = AlertDialog.Builder(this@ForgotPassword as Context)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Not found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    this@ForgotPassword?.finish()
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@ForgotPassword as Activity)

                }
                dialog.create()
                dialog.show()
            }


        }
    }

}
