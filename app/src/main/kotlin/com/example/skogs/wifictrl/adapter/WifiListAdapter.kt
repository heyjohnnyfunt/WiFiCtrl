package com.example.skogs.wifictrl.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.example.skogs.wifictrl.R
import com.example.skogs.wifictrl.model.WifiStation

/**
 * Created by skogs on 22.04.2016.
 */

/**
 * Array adapter for stations.
 */
class WifiListAdapter(context: Context, currentSsid: String) : ArrayAdapter<WifiStation>(context, 0) {

    private val inflater = LayoutInflater.from(context)
    private val ssid = currentSsid

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val item = getItem(position)
        val view = convertView ?: this.inflater.inflate(R.layout.row_hotspot, parent, false)

        val ssidView = view.findViewById(R.id.txt_ssid) as TextView
        val bssidView = view.findViewById(R.id.txt_bssid) as TextView
        val frequencyView = view.findViewById(R.id.txt_frequency) as TextView
        val levelView = view.findViewById(R.id.txt_level) as TextView

        val isConnected = view.findViewById(R.id.connected_ssid) as TextView

        if (item.ssid == this.ssid){
            println("CONNECTED WIFI ==================> " + item.ssid)
            isConnected.visibility = View.VISIBLE;
        }

        ssidView.text = item.ssid
        bssidView.text = item.bssid
        frequencyView.text = context.getString(R.string.station_frequency, item.frequency)
        levelView.text = context.getString(R.string.station_level, item.level)

        return view
    }

}
/*class HotspotAdapter(private val data: List<String>) : RecyclerView.Adapter<HotspotAdapter.HotspotHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotspotHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_wifi_detail, parent, false)

        return HotspotHolder(view)
    }

    override fun onBindViewHolder(holder: HotspotHolder, position: Int) {
        holder.title.text = data[position]
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class HotspotHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var cardView: CardView
        internal var title: TextView

        init {

            cardView = itemView.findViewById(R.id.cardView) as CardView
            title = itemView.findViewById(R.id.title) as TextView
        }
    }

}*/
