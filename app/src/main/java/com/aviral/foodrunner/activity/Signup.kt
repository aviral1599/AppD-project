package com.aviral.foodrunner.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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

class Signup : AppCompatActivity() {

    lateinit var etMobileNo: EditText
    lateinit var etPass: EditText
    lateinit var btnRegister: Button
    lateinit var etConfPass: EditText
    lateinit var etAddress: EditText
    lateinit var etName: EditText
    lateinit var etEmailAddress: EditText
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        sharedPreferences =
            getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_signup)

        title = "Register"

        etMobileNo = findViewById(R.id.etMobileNo)
        etPass = findViewById(R.id.etPass)
        btnRegister = findViewById(R.id.btnRegister)
        etConfPass = findViewById(R.id.etConfPass)
        etAddress = findViewById(R.id.etAddress)
        etName = findViewById(R.id.etName)
        etEmailAddress = findViewById(R.id.etEmailAddress)

        btnRegister.setOnClickListener {

            val mobileNo = etMobileNo.text.toString()
            val pass = etPass.text.toString()
            val confPass = etConfPass.text.toString()
            val address = etAddress.text.toString()
            val name = etName.text.toString()
            val email = etEmailAddress.text.toString()

            val mobile = mobileNo.length
            val password = pass.length

            if (mobile == 10 && password > 4) {

                if (confPass == pass) {
                    val queue = Volley.newRequestQueue(this@Signup as Context)

                    val url = "http://13.235.250.119/v2/register/fetch_result"

                    val params = JSONObject()
                    params.put("name", name)
                    params.put("mobile_number", mobileNo)
                    params.put("password", pass)
                    params.put("address", address)
                    params.put("̥email", email)




                    if (ConnectionManager().checkConnectivity(this@Signup as Context)) {

                        val jsonObjectRequest = object : JsonObjectRequest(
                            Request.Method.POST, url, params, Response.Listener {
                                try {

                                    val data = it.getJSONObject("data")
                                    val success = data.getBoolean("success")

                                    if (success) {
                                        val response = data.getJSONObject("data")

                                        sharedPreferences.edit()
                                            .putString("user_id", response.getString("user_id"))
                                            .apply()
                                        sharedPreferences.edit()
                                            .putString("user_name", response.getString("name"))
                                            .apply()
                                        sharedPreferences.edit()
                                            .putString("user_email", response.getString("email"))
                                            .apply()
                                        sharedPreferences.edit()
                                            .putString(
                                                "user_mobile_number",
                                                response.getString("mobile_number")
                                            )
                                            .apply()
                                        sharedPreferences.edit()
                                            .putString(
                                                "user_address",
                                                response.getString("address")
                                            )
                                            .apply()



                                        startActivity(
                                            Intent(
                                                this@Signup,
                                                Dashboard::class.java
                                            )
                                        )
                                        finish()

                                    } else {
                                        val errorMessage = data.getString("errorMessage")
                                        Toast.makeText(
                                            this@Signup as Context,
                                            errorMessage,
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                } catch (e: JSONException) {
                                    Toast.makeText(
                                        this@Signup as Context,
                                        "Some Unexpected error occured",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    var intent = Intent(this@Signup, Signup::class.java)
                                    startActivity(intent)
                                    e.printStackTrace()
                                }
                            },
                            Response.ErrorListener {

                                if (this@Signup != null) {
                                    Toast.makeText(
                                        this@Signup as Context,
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

                        val dialog = AlertDialog.Builder(this@Signup as Context)
                        dialog.setTitle("Error")
                        dialog.setMessage("Internet Not found")
                        dialog.setPositiveButton("Open Settings") { text, listener ->
                            val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                            startActivity(settingsIntent)
                            this@Signup?.finish()
                        }
                        dialog.setNegativeButton("Exit") { text, listener ->
                            ActivityCompat.finishAffinity(this@Signup as Activity)

                        }
                        dialog.create()
                        dialog.show()
                    }


                } else {
                    Toast.makeText(
                        this@Signup as Context,
                        "Password Does not Match",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } else {

                Toast.makeText(
                    this@Signup as Context,
                    "Please Enter a 10 digit MobileNo. and Password greater than 4 characters",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }
}
