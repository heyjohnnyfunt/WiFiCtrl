package com.example.skogs.wifictrl.adapter

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.skogs.wifictrl.R

/**
 * Created by skogs on 22.04.2016.
 */
class HotspotAdapter(private val data: List<String>) : RecyclerView.Adapter<HotspotAdapter.HotspotHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotspotHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hotspot_item, parent, false)

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

}
