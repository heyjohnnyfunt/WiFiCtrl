package com.example.skogs.wifictrl.model

import android.app.Activity
import android.content.Context

/**
 * Created by skogs on 07.06.2016.
 */
class Database {
    companion object {

        fun save(activity: Activity, chosenWifi: WifiStation){

            val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString(chosenWifi.ssid, chosenWifi.bssid)
            editor.apply()
        }

        fun getAll(activity: Activity): MutableMap<String, *>? {
            val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
            return sharedPref.all
        }
    }
}