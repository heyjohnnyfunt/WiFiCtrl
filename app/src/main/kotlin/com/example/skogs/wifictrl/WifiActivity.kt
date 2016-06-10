package com.example.skogs.wifictrl

import android.app.Activity
import android.app.Fragment
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.widget.TextView
import android.widget.Toast
import com.example.skogs.wifictrl.fragment.SettingsFragment
import com.example.skogs.wifictrl.fragment.WifiDetailFragment
import com.example.skogs.wifictrl.fragment.WifiListFragment
import com.example.skogs.wifictrl.model.WifiStation

/**
 * Root activity for Wi-Fi list and details
 */
open class WifiActivity() : Activity() {

    companion object {

        private var connectedWifi: WifiConfiguration? = null

        private var toolbar: Toolbar? = null
        private var drawerLayout: DrawerLayout? = null
        private var navigationView: NavigationView? = null

        private var wifiList: WifiListFragment? = null
        private var wifiDetail: WifiDetailFragment? = null
        private var wifiListVisible: Boolean = false

        private var wifiManager: WifiManager? = null
    }


    private val wifiReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            val results = wifiManager?.scanResults

            if (wifiListVisible && results != null) {
                wifiList?.updateItems(results)
            }

            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager;
            val activeNetInfo = connectivityManager.activeNetworkInfo;

            if (activeNetInfo != null && activeNetInfo.type == ConnectivityManager.TYPE_WIFI) {
                Toast.makeText(context, "Wifi Connected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Wifi Not Connected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi)
        setTitle(R.string.app_name)

        initToolbar()
        initNavigationView()

        transitionToList()

        wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (wifiManager?.isWifiEnabled == false) {
            Toast.makeText(this, R.string.prompt_enabling_wifi, Toast.LENGTH_SHORT).show()
            wifiManager?.isWifiEnabled = true
        }

    }

    private fun initToolbar() {
        toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar!!.setTitle(R.string.app_name)

        toolbar!!.setOnMenuItemClickListener {
            when (it.itemId) {
            // toolbar refresh icon
                R.id.refresh -> {
                    refreshList()
                }
            }
            true
        }
        toolbar!!.inflateMenu(R.menu.menu)
    }

    private fun initNavigationView() {
        drawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.view_navigation_open, R.string.view_navigation_close)
        drawerLayout!!.setDrawerListener(toggle)
        toggle.syncState()

        // navigation view on the left side
        navigationView = findViewById(R.id.navigation) as NavigationView

        navigationView!!.setNavigationItemSelectedListener { menuItem ->
            drawerLayout!!.closeDrawers()
            when (menuItem.itemId) {
                R.id.actionHotspotsItem -> {
                    transitionToList()
                }
                R.id.settings -> {
                    transitionToSettings()
                }
                R.id.refresh -> {
                    refreshList()
                }
                R.id.disconnect -> {
                    disconnect()
                }
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(wifiReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
    }

    fun onResumeFragment(fragment: Fragment) {
        toolbar!!.setTitle(R.string.app_name)
        wifiListVisible = false

        if (fragment == wifiList) {
            wifiListVisible = true
            refreshList()
        }
    }

    override fun onPause() {
        unregisterReceiver(wifiReceiver)
        super.onPause()
    }

    private fun transition(fragment: Fragment, add: Boolean = false) {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.layout_frame, fragment)
        if (add) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    fun transitionToList() {
        toolbar!!.setTitle(R.string.app_name)
        wifiList = WifiListFragment.newInstance()
        transition(wifiList!!)
    }

    fun transitionToSettings() {
        toolbar!!.setTitle(R.string.settings)
        val settingsFragment = SettingsFragment.newInstance()
        transition(settingsFragment, add = true)
    }

    fun transitionToDetail(item: WifiStation) {
        toolbar!!.title = item.ssid.toString()
        wifiDetail = WifiDetailFragment.newInstance(item)

        val args = Bundle();
        if (connectedWifi != null) {
            args.putCharArray(Constants.connctedWifiArg, connectedWifi!!.SSID!!.toCharArray());
        } else {
            args.putCharArray(Constants.connctedWifiArg, null);
        }

        wifiDetail!!.arguments = args;
        transition(wifiDetail!!, add = true)
    }

    fun disconnect() {
        val args = Bundle();
        args.putCharArray(Constants.connctedWifiArg, null);
        wifiDetail?.arguments = args;

        val currentWifiTextView = findViewById(R.id.connected_wifi_ssid) as TextView
        currentWifiTextView.text = "Not connected"

        val networkId = wifiManager?.connectionInfo?.networkId;
        wifiManager?.removeNetwork(networkId as Int);
        wifiManager?.saveConfiguration();
        wifiManager?.disconnect();
    }

    private fun refreshList() {
        wifiList?.clearItems()
        wifiManager?.startScan()
        updateConnectedWifi()
    }

    fun updateConnectedWifi() {
        connectedWifi = wifiList?.getCurrentWifi(this)

        val currentWifiTextView = findViewById(R.id.connected_wifi_ssid) as TextView

        if (connectedWifi != null) {
            currentWifiTextView.text = connectedWifi?.SSID
        } else {
            currentWifiTextView.text = "Not connected"
        }
    }


}
