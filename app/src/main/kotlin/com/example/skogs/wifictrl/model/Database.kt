package com.example.skogs.wifictrl.model

import android.app.Activity
import android.content.Context

/**
 * Created by skogs on 07.06.2016.
 */
class Database {
    companion object {

        fun set(activity: Activity, chosenWifi: WifiStation, data: String) {
            val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString(chosenWifi.ssid + " " + chosenWifi.bssid, data)
            editor.apply()
        }

        fun get(activity: Activity, chosenWifi: WifiStation) : String? {
            val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)

            return sharedPref.getString(chosenWifi.ssid + " " + chosenWifi.bssid, "")
        }

        fun getAll(activity: Activity): MutableMap<String, *>? {
            val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
            return sharedPref.all
        }

        fun remove(activity: Activity, key: String) {
            val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
            if (sharedPref.contains(key)) {
                val editor = sharedPref.edit()
                editor.remove(key)
                editor.apply()
            }
        }
    }
}