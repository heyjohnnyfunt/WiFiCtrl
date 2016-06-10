package com.example.skogs.wifictrl.fragment

import android.app.ListFragment
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.example.skogs.wifictrl.R
import com.example.skogs.wifictrl.WifiActivity
import com.example.skogs.wifictrl.adapter.WifiListAdapter
import com.example.skogs.wifictrl.model.WifiStation
import java.util.*

/**
 * Fragment for listing Wi-Fi base stations.
 */

open class WifiListFragment() : ListFragment() {

    companion object {
        fun newInstance(): WifiListFragment {
            return WifiListFragment()
        }
    }

    private var emptyView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_hotspots, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emptyView = view.findViewById(R.id.progress)

        listAdapter = WifiListAdapter(activity, getCurrentWifi(activity)?.SSID)
        listView.emptyView = emptyView
    }

    override fun onResume() {

        super.onResume()

        val activity = activity

        if (activity is WifiActivity) {
            activity.onResumeFragment(this)
            activity.updateConnectedWifi()
        }
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {

        super.onListItemClick(l, v, position, id)

        val activity = activity

        if (activity is WifiActivity) {
            val item = l.getItemAtPosition(position) as WifiStation
            activity.transitionToDetail(item)
        }
    }

    /**
     * Updates adapter and calls notify.
     *
     * @param stations List of scan results.
     */
    fun updateItems(stations: List<ScanResult>? = null) {

        val adapter = listAdapter

        // ScanResult comparator
        val comparator: Comparator<ScanResult> = Comparator { lhs, rhs -> if (lhs.level > rhs.level) -1 else if (lhs.level === rhs.level) 0 else 1 }

        if (adapter is WifiListAdapter) {

            adapter.clear()

            if (stations != null) {

                emptyView?.visibility = if (stations.size > 0) View.VISIBLE else View.GONE
                Collections.sort(stations, comparator);

                adapter.addAll(WifiStation.newList(stations))
            }

            adapter.notifyDataSetChanged()
        }
    }

    /**
     * Clears adapter items and calls notify.
     */
    fun clearItems() {
        updateItems()
    }

    fun getCurrentWifi(context: Context): WifiConfiguration? {

        var currentStation: WifiConfiguration? = null

        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)

        if (networkInfo.isConnected) {

            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val connectedInfo = wifiManager.connectionInfo

            if (connectedInfo != null && !TextUtils.isEmpty(connectedInfo.ssid)) {

                var activeConfig: WifiConfiguration? = null;
                val conn = wifiManager.configuredNetworks

                conn.forEach {
                    val wifi = it
                    if (wifi.status == WifiConfiguration.Status.CURRENT) {
                        activeConfig = wifi;
                    }
                }
                if (activeConfig != null) {
                    currentStation = activeConfig
                    // erase quotes in Android < 5.0
                    currentStation?.SSID = currentStation?.SSID?.replace("\"","")
                }
            }
        }
        return currentStation
    }
}
