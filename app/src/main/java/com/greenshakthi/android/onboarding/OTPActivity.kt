package com.greenshakthi.android.onboarding

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.chaos.view.PinView
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.greenshakthi.android.R
import com.greenshakthi.android.home.MainActivity
import com.greenshakthi.android.models.UserData
import com.greenshakthi.android.utils.AppPreferences
import java.util.concurrent.TimeUnit

class OTPActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private var storedVerificationId: String? = ""
    lateinit var token: PhoneAuthProvider.ForceResendingToken
    private var phoneNumber: String? = ""


    lateinit var txtOTPSentMsg: TextView

    lateinit var otpView: PinView
    lateinit var txtResendButton: TextView

    lateinit var loadingLayout: RelativeLayout

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpactivity)

        // initialising App Preferences
        AppPreferences.init(this)

        // Checking Internet Connection
        if(!AppPreferences.isOnline()) AppPreferences.showNetworkErrorPage(this)


        txtOTPSentMsg = findViewById(R.id.txt_otp_verification)
        txtResendButton = findViewById(R.id.txt_resend)

        otpView = findViewById(R.id.otp_view)

        loadingLayout = findViewById(R.id.loadingLayout)


        auth = Firebase.auth

        Toast.makeText(this, "Please wait for some time, ReCAPTCHA Verification under progress!", Toast.LENGTH_LONG).show()

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.

                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(this@OTPActivity, e.localizedMessage, Toast.LENGTH_LONG).show()
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(this@OTPActivity, e.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                this@OTPActivity.token = token

                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId

            }
        }
        // [END phone_auth_callbacks]

        // checking the intent whether phone number came or not
        val intent = intent
        if (intent.getStringExtra("phoneNumber")!!.isNotEmpty()) {

            txtOTPSentMsg.text = "OTP sent successfully to \n+91 " + intent.getStringExtra("phoneNumber")
            phoneNumber = "+91"+intent.getStringExtra("phoneNumber")

            startPhoneNumberVerification(phoneNumber!!)
        }

        // Resend Button
        val timer = object: CountDownTimer(45000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                txtResendButton.isEnabled = false

                if ((millisUntilFinished/1000)<10)
                    txtResendButton.text = "Resend in 00:0" + (millisUntilFinished/1000)
                else
                    txtResendButton.text = "Resend in 00:" + (millisUntilFinished/1000)
            }

            override fun onFinish() {
                // once timer gets finished
                txtResendButton.text = "Resend"
                txtResendButton.isEnabled = true
                txtResendButton.setOnClickListener {

                    if(token.toString().isNotEmpty() and phoneNumber.toString().isNotEmpty()) {
                        resendVerificationCode(phoneNumber!!, token)
                        Toast.makeText(this@OTPActivity, "OTP re-sent successfully! reCAPTCHA verification under progress", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        timer.start()


        // Instead of a Button, once the user enters all the characters the below function gets invoked
        otpView.doOnTextChanged { text, start, before, count ->

            if(!AppPreferences.isOnline())
                AppPreferences.showToast(this, "There is No Internet Connection. Please check your Wifi or Mobile Data once.")

            else {

                if (text!!.length == 6) {

                    // showing Loading Layout VISIBLE
                    loadingLayout.visibility = View.VISIBLE

                    val codeEntered = otpView.text.toString()
                    verifyPhoneNumberWithCode(storedVerificationId, codeEntered)
                }
            }
        }
    }


    private fun startPhoneNumberVerification(phoneNumber: String) {
        // [START start_phone_auth]
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        // [END start_phone_auth]
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        // [START verify_with_code]
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
        // [END verify_with_code]
    }

    // [START resend_verification]
    private fun resendVerificationCode(phoneNumber: String, token: PhoneAuthProvider.ForceResendingToken?) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user

                    // Pass the Phone Number to DB and check whether the user is already registered
                    val db = Firebase.firestore
                    db.collection("Customer_Data")
                        .whereEqualTo("phoneNumber",phoneNumber.toString())
                        .get()
                        .addOnSuccessListener {

                            if(it.documents.size > 0)
                                for(document in it.documents) {

                                    val  customerData = document.toObject<UserData>()!!

                                    //Toast.makeText(this, "Name: " + customerData.customerName + " Phone: " + customerData.phoneNumber, Toast.LENGTH_LONG).show()

                                    // Setting the details of the Customer globally
                                    AppPreferences.isLogin = true
                                    AppPreferences.customerID = customerData.custID
                                    AppPreferences.customerName = customerData.customerName
                                    AppPreferences.customerPhone = customerData.phoneNumber

                                    // disabling Loading Layout
                                    loadingLayout.visibility = View.GONE

                                    // Since the user is already registered, move him directly to the Main Activity
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            else {

                                // disabling Loading Layout
                                loadingLayout.visibility = View.GONE

                                // User hasn't already registered - send him to Customer Name Activity
                                val intent = Intent(this,CustomerNameActivity::class.java)
                                intent.putExtra("phoneNumber", phoneNumber)
                                startActivity(intent)
                                finish()

                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, it.localizedMessage!!.toString(),Toast.LENGTH_LONG).show()
                        }

                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid

                        // disabling Loading Layout
                        loadingLayout.visibility = View.GONE

                        Toast.makeText(this, "Invalid OTP Entered!",Toast.LENGTH_LONG).show()
                    }
                    // Update UI
                }
            }
    }
    // [END sign_in_with_phone]

    override fun onBackPressed() {
        // Disabling onBackPress
    }
}