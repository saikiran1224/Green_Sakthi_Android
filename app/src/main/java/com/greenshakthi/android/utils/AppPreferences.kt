package com.greenshakthi.android.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import java.io.IOException

object AppPreferences {

    private const val NAME = "Green Sakthi"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    //SharedPreferences variables
    private val IS_LOGIN = Pair("is_login", false)
    private val CUST_ID = Pair("custID", "")
    private val CUST_NAME = Pair("custName", "")
    private val CUST_ADDRESS = Pair("custAddress","")
    private val CUST_PHONE = Pair("custPhone","")

    private val REMIND_LATER = Pair("remindLater",true)

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    //an inline function to put variable and save it
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    // TCP/HTTP/DNS (depending on the port, 53=DNS, 80=HTTP, etc.)
    fun isOnline(): Boolean {
        val runtime = Runtime.getRuntime()
        try {
            val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
            val exitValue = ipProcess.waitFor()
            return exitValue == 0
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return false
    }

    fun showNetworkErrorPage(context: Context) {

        val intent = Intent(context, NetworkErrorActivity::class.java)
        context.startActivity(intent)
    }

    fun showToast(context: Context, message: String?) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

    //SharedPreferences variables getters/setters
    var remindLater: Boolean?
        get() = preferences.getBoolean(REMIND_LATER.first, REMIND_LATER.second)
        set(value) = preferences.edit {
            it.putBoolean(REMIND_LATER.first, value!!)
        }


    var isLogin: Boolean?
        get() = preferences.getBoolean(IS_LOGIN.first, IS_LOGIN.second)
        set(value) = preferences.edit {
            it.putBoolean(IS_LOGIN.first, value!!)
        }

    var customerID: String?
        get() = preferences.getString(CUST_ID.first, CUST_ID.second)
        set(value) = preferences.edit {
            it.putString(CUST_ID.first, value)
        }

    var customerName: String?
        get() = preferences.getString(CUST_NAME.first, CUST_NAME.second)
        set(value) = preferences.edit {
            it.putString(CUST_NAME.first, value)
        }

    var customerAddress: String?
        get() = preferences.getString(CUST_ADDRESS.first, CUST_ADDRESS.second)
        set(value) = preferences.edit {
            it.putString(CUST_ADDRESS.first, value)
        }

    var customerPhone: String?
        get() = preferences.getString(CUST_PHONE.first, CUST_PHONE.second)
        set(value) = preferences.edit {
            it.putString(CUST_PHONE.first, value)
        }


}