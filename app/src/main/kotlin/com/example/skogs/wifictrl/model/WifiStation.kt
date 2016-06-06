package com.example.skogs.wifictrl.model

import java.io.Serializable
import android.net.wifi.ScanResult
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
         *
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
         *
         * @param srs List of scan results.
         */
        fun newList(srs: List<ScanResult>): List<WifiStation> {
            val stations = ArrayList<WifiStation>()
            for (sr in srs) {
                stations.add(newInstance(sr))
            }
            return stations
        }

        fun getSecurity(result: WifiStation): Int {
            if (result.capabilities!!.contains("WEP")) {
                return 0;
            } else if (result.capabilities.contains("PSK")) {
                return 1;
            } else if (result.capabilities.contains("EAP")) {
                return 2;
            }
            return -1;
        }
    }
}