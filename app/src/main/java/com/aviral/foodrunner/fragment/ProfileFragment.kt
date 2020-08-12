package com.aviral.foodrunner.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.aviral.foodrunner.R

class ProfileFragment : Fragment() {

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {


        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        sharedPreferences = activity!!.getSharedPreferences(
            getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )


        val name = sharedPreferences.getString("user_name", "name")
        val Name = view.findViewById<TextView>(R.id.RecName).apply {
            text = name
        }

        val mobile = sharedPreferences.getString("user_mobile_number", "mobile_number")
        val Mobile = view.findViewById<TextView>(R.id.recMobileNo).apply {
            text = mobile
        }

        val email = sharedPreferences.getString("user_email", "email")
        val Email = view.findViewById<TextView>(R.id.recEmailAddress).apply {
            text = email
        }

        val address = sharedPreferences.getString("user_address", "address")
        val Address = view.findViewById<TextView>(R.id.recAddress).apply {
            text = address
        }
        return view


    }


}
