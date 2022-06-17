package com.greenshakthi.android.fragments

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.greenshakthi.android.R
import com.greenshakthi.android.onboarding.GetStartedActivity
import com.greenshakthi.android.utils.AppPreferences

class ProfileFragment : Fragment() {

    lateinit var txtCustomerName: TextView
    lateinit var txtCustomerPhone: TextView

    lateinit var txtLogoutBtn: TextView
    lateinit var txtWriteToUs: TextView
    lateinit var txtAbout: TextView
    lateinit var txtShare: TextView
    lateinit var txtRateUs: TextView
    lateinit var txtLogOut: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        AppPreferences.init(requireContext())

        // Checking Internet Connection
        if(!AppPreferences.isOnline()) AppPreferences.showNetworkErrorPage(requireContext())

        txtCustomerName = view.findViewById(R.id.txtCustName)
        txtCustomerPhone = view.findViewById(R.id.txtCustPhone)

        txtWriteToUs = view.findViewById(R.id.txtWriteToUs)
        txtAbout = view.findViewById(R.id.txtAbout)
        txtShare = view.findViewById(R.id.txtShare)
        txtRateUs = view.findViewById(R.id.txtRateUsOnPlayStore)
        txtLogoutBtn = view.findViewById(R.id.txtLogout)

        txtCustomerName.text = AppPreferences.customerName.toString()
        txtCustomerPhone.text = AppPreferences.customerPhone.toString()

        txtWriteToUs.setOnClickListener {

           val emailMessage: String = "Write your message here....\n\n\n" +
                   "Customer ID: ${AppPreferences.customerID} \n" +
                   "Customer Name: ${AppPreferences.customerName} \n" +
                   "Customer Phone: ${AppPreferences.customerPhone}"

            // send an email
            val intent = Intent(Intent.ACTION_SENDTO)
            //intent.data = Uri.parse("mailto:greenshakthifuels@gmail.com")
            intent.putExtra(Intent.EXTRA_EMAIL,"greenshakthifuels@gmail.com" )
            intent.putExtra(Intent.EXTRA_SUBJECT, "[CONTACT] Write to us - Green Shakti Fuels India Pvt. Ltd." )
            intent.putExtra(Intent.EXTRA_TEXT, emailMessage)
            intent.data = Uri.parse("mailto:greenshakthifuels@gmail.com")
            if(intent.resolveActivity(requireContext().packageManager) != null)
                startActivity(Intent.createChooser(intent, "Select your Email app"))
            else
                Toast.makeText(requireContext(), "Sorry, There is no application supports sending mail.", Toast.LENGTH_LONG).show()
       }

        txtAbout.setOnClickListener {

           // opening the website
           val intent = Intent(Intent.ACTION_VIEW)
           intent.data = Uri.parse("http://greenshaktifuels.com/")
           startActivity(intent)

       }

        txtShare.setOnClickListener {

           val shareMessage = "Hey, Install this Awesome App now and book *Zero Carbon Diesel* in affordable price and get it delivered to your home and become a part of Greener Economy.\n\nInstall from Play Store: https://play.google.com/store/apps/details?id=com.greenshakthi.android"

           // Share the above message to all Messaging Apps
           val intent = Intent(Intent.ACTION_SEND)
           intent.type = "text/plain"
           intent.putExtra(Intent.EXTRA_TEXT, shareMessage.toString())
           startActivity(Intent.createChooser(intent, "Share via"))

       }

        txtRateUs.setOnClickListener {

            // Redirecting to Play Store
            // Navigate to Play Store Store Listing
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${requireContext().packageName}")))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${requireContext().packageName}")))
            }

        }

       txtLogoutBtn.setOnClickListener {

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

        }

        return view
    }
}