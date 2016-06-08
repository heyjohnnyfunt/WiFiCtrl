package com.example.skogs.wifictrl.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.skogs.wifictrl.R

/**
 * Created by skogs on 07.06.2016.
 */

class SettingsAdapter(context: Context): ArrayAdapter<Map.Entry<String, *>>(context, 0) {

    private val inflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val item = getItem(position)
        val view = convertView ?: this.inflater.inflate(R.layout.row_setting, parent, false)

        val ssidView = view.findViewById(R.id.wlan_ssid) as TextView
        val keyView = view.findViewById(R.id.wlan_key) as TextView

        ssidView.text = item.key
        keyView.text = item.value.toString()

        return view
    }
}