package com.example.skogs.wifictrl.model

import android.content.Context
import android.net.ConnectivityManager
import java.io.Serializable
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.text.TextUtils
import java.util.ArrayList

/**
 * Data container for a Wi-Fi base station.
 */
data class WifiStation(
        val ssid: String?,
        val bssid: String? = null,
        val frequency: Int? = null,
        val level: Int? = null,
        val capabilities: String? = null) : Serializable {

    companion object {
        /**
         * Creates a new instance from a scan result.
         * @param sr A scan result.
         */
        fun newInstance(sr: ScanResult): WifiStation {
            return WifiStation(
                    ssid = sr.SSID,
                    bssid = sr.BSSID,
                    frequency = sr.frequency,
                    level = sr.level,
                    capabilities = sr.capabilities
            )
        }

        /**
         * Creates a new list of Wi-Fi stations from a list of scan results.
         * @param srs List of scan results.
         */
        fun newList(srs: List<ScanResult>): List<WifiStation> {
            val stations = ArrayList<WifiStation>()
            for (sr in srs) {
                stations.add(newInstance(sr))
            }
            return stations
        }

        /**
         * Get Wi-Fi AP security type
         * @param station Specified Wi-Fi station
         */
        fun getSecurity(station: WifiStation): Int {
            if (station.capabilities!!.contains("WEP")) {
                return 0;
            } else if (station.capabilities.contains("PSK")) {
                return 1;
            } else if (station.capabilities.contains("EAP")) {
                return 2;
            }
            return -1;
        }

    }
}