package com.greenshakthi.android.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.greenshakthi.android.R
import com.greenshakthi.android.onboarding.GetStartedActivity
import com.greenshakthi.android.utils.AppPreferences


class ProfileFragment : Fragment() {

    lateinit var txtCustomerName: TextView
    lateinit var txtCustomerPhone: TextView

    lateinit var txtLogoutBtn: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        AppPreferences.init(requireContext())

        // Checking Internet Connection
        if(!AppPreferences.isOnline()) AppPreferences.showNetworkErrorPage(requireContext())

        txtCustomerName = view.findViewById(R.id.txtCustName)
        txtCustomerPhone = view.findViewById(R.id.txtCustPhone)

        //txtLogoutBtn = view.findViewById(R.id.txtLogout)

        txtCustomerName.text = AppPreferences.customerName.toString()
        txtCustomerPhone.text = AppPreferences.customerPhone.toString()

       /* txtLogoutBtn.setOnClickListener {

            val logoutDialog = Dialog(requireContext())
            logoutDialog.setContentView(R.layout.logout_dialog)
            logoutDialog.setCancelable(false)
            logoutDialog.setCanceledOnTouchOutside(false)
            logoutDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

            logoutDialog.findViewById<Button>(R.id.btnLogout).setOnClickListener{
                // TODO: Ask user Logout dialog once again

                AppPreferences.isLogin = false

                // logging out from Firebase
                FirebaseAuth.getInstance().signOut()

                // redirecting back to Get Started Page
                val intent = Intent(requireContext(), GetStartedActivity::class.java)
                startActivity(intent)
                requireActivity().finish()

            }

            logoutDialog.findViewById<Button>(R.id.btnCancel).setOnClickListener {
                logoutDialog.dismiss()
            }

            logoutDialog.show()

        }*/


        return view
    }

}