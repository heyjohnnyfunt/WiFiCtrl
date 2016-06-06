package com.example.skogs.wifictrl.fragment

import android.app.Fragment
import android.os.Bundle
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.TextView
import android.os.SystemClock
import android.util.Log
import com.example.skogs.wifictrl.R
import com.example.skogs.wifictrl.model.WifiStation
import rx.schedulers.Schedulers
import rx.subscriptions.Subscriptions

/**
 * Details view for a Wi-Fi base station.
 *
 * @author Mike Gouline
 */
open class WifiDetailFragment : Fragment() {


    companion object {
        var currentWifi: WifiStation? = null

        fun newInstance(item: WifiStation): WifiDetailFragment {
            currentWifi = item;
            println(currentWifi.toString())
            return WifiDetailFragment()
        }
    }

    private var ssidTextView: TextView? = null
    private var authTextView: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wifi_detail, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ssidTextView = view?.findViewById(R.id.wlan_ssid) as TextView
        authTextView = view?.findViewById(R.id.wlan_auth) as TextView

        ssidTextView?.text = currentWifi?.ssid
        authTextView?.text = currentWifi?.capabilities

        val security = WifiStation.getSecurity(currentWifi!!);

        if (security !== 0 || security !== -1){
            view?.findViewById(R.id.password_input)?.visibility = View.VISIBLE
        }

    }
}