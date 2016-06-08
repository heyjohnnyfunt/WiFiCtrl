package com.example.skogs.wifictrl.fragment

import android.app.Fragment
import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.skogs.wifictrl.R
import com.example.skogs.wifictrl.model.Database
import com.example.skogs.wifictrl.model.WifiStation

/**
 * Details view for a Wi-Fi base station.
 *
 * @author Mike Gouline
 */
open class WifiDetailFragment : Fragment() {

    companion object {

        var chosenWifi: WifiStation? = null

        fun newInstance(item: WifiStation): WifiDetailFragment {

            chosenWifi = item;

            return WifiDetailFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wifi_detail, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val connectBtn = view?.findViewById(R.id.connect_btn) as Button
        val ssidTextView = view?.findViewById(R.id.wlan_ssid) as TextView
        val authTextView = view?.findViewById(R.id.wlan_auth) as TextView
        val passEditText = view?.findViewById(R.id.wlan_pass) as EditText
        val errorText = view?.findViewById(R.id.error_text) as TextView

        ssidTextView.text = chosenWifi?.ssid
        authTextView.text = chosenWifi?.capabilities

        val security = WifiStation.getSecurity(chosenWifi!!);

        if (security !== -1) {
            view?.findViewById(R.id.password_input)?.visibility = View.VISIBLE
        }

        connectBtn.setOnClickListener {
            if (connect(chosenWifi, passEditText.text.toString(), security)) {

                errorText.visibility = View.INVISIBLE
                Database.save(activity, chosenWifi!!)

                val builder = AlertDialog.Builder(activity);
                builder
                        .setMessage("Connected successfully")
                        .setPositiveButton("ОК") {
                    dialog, whichButton ->
                }
                val alert = builder.create();
                alert.show();

            } else {
                errorText.text = "Invalid password"
                errorText.visibility = View.VISIBLE
                errorText.setTextColor(resources.getColor(R.color.colorAccent));
            }
        }
    }


    fun connect(chosenWifi: WifiStation?, pass: String, type: Int): Boolean {

        if (chosenWifi == null)
            return false

        var result = false
        val conf = WifiConfiguration()

        conf.SSID = "\"" + chosenWifi.ssid + "\"";

        when (type) {
            -1 -> {
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            }
        //WEP
            0 -> {
                conf.wepKeys[0] = "\"$pass\"";
                conf.wepTxKeyIndex = 0;
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            }
        //WPA-PSK
            1 -> {
                conf.preSharedKey = "\"$pass\"";
            }
        //WPA-EAP
            2 -> {

            }
        }

        val wifiManager = activity.getSystemService(Context.WIFI_SERVICE) as WifiManager;
        wifiManager.addNetwork(conf);

        val list: List<WifiConfiguration> = wifiManager.configuredNetworks

        list.forEach {
            if (it.SSID != null && it.SSID.equals("\"${chosenWifi.ssid}\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(it.networkId, true);
                result = wifiManager.reconnect();
            }
        }
        return result
    }

}