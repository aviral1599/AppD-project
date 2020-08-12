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
import android.widget.TextView
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

class Login : AppCompatActivity() {

    lateinit var etMobileNumber: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin: Button
    lateinit var txtForgotPass: TextView
    lateinit var txtRegister: TextView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        setContentView(R.layout.login)
        if (isLoggedIn) {
            val intent = Intent(this@Login, Dashboard::class.java)
            startActivity(intent)
            finish()
        }
        title = "LogIn"

        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgotPass = findViewById(R.id.txtForgotPass)
        txtRegister = findViewById(R.id.txtRegister)

        btnLogin.setOnClickListener {

            val mobileNumber = etMobileNumber.text.toString()
            val password = etPassword.text.toString()

            val mobile = mobileNumber.length
            val pass = password.length

            if (mobile == 10 && pass > 4) {
                val queue = Volley.newRequestQueue(this@Login as Context)

                val url = "http://13.235.250.119/v2/login/fetch_result "
                val params = JSONObject()
                params.put("mobile_number", mobileNumber)
                params.put("password", password)




                if (ConnectionManager().checkConnectivity(this@Login as Context)) {

                    val jsonObjectRequest = object : JsonObjectRequest(
                        Request.Method.POST, url, params, Response.Listener {
                            try {

                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")

                                if (success) {
                                    val response = data.getJSONObject("data")

                                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                                    sharedPreferences.edit()
                                        .putString("user_id", response.getString("user_id")).apply()
                                    sharedPreferences.edit()
                                        .putString("user_name", response.getString("name")).apply()
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
                                        .putString("user_address", response.getString("address"))
                                        .apply()


                                    var intent = Intent(this@Login, Dashboard::class.java)
                                    startActivity(intent)


                                } else {
                                    Toast.makeText(
                                        this@Login as Context,
                                        "Please enter Valid Mobile Number or Password",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@Login as Context,
                                    "Some Unexpected error occured",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                var intent = Intent(this@Login, Login::class.java)
                                startActivity(intent)
                            }
                        },
                        Response.ErrorListener {

                            if (this@Login != null) {
                                Toast.makeText(
                                    this@Login as Context,
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

                    val dialog = AlertDialog.Builder(this@Login as Context)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Not found")
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        this@Login?.finish()
                    }
                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this@Login as Activity)

                    }
                    dialog.create()
                    dialog.show()
                }
            } else {
                Toast.makeText(
                    this@Login as Context,
                    "Please enter Valid Mobile Number or Password",
                    Toast.LENGTH_SHORT
                )
                    .show()

            }

        }

        txtRegister.setOnClickListener {

            var intent = Intent(this@Login, Signup::class.java)
            startActivity(intent)
        }

        txtForgotPass.setOnClickListener {

            var intent = Intent(this@Login, ForgotPassword::class.java)
            startActivity(intent)

        }

    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}
