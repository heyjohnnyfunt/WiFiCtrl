package com.example.skogs.wifictrl.fragment

import android.app.Activity
import android.app.ListFragment
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.skogs.wifictrl.R
import com.example.skogs.wifictrl.WifiActivity
import com.example.skogs.wifictrl.adapter.SettingsAdapter
import com.example.skogs.wifictrl.model.Database
import java.util.*

/**
 * Created by skogs on 20.04.2016.
 */

class SettingsFragment : ListFragment() {

    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }

    private var emptyView: View? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        emptyView = view.findViewById(R.id.progress)
        listAdapter = SettingsAdapter(activity)
        listView.emptyView = emptyView
    }

    override fun onResume() {

        super.onResume()

        val activity = activity

        if (activity is WifiActivity) {
            updateItems(activity)
        }
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {

        super.onListItemClick(l, v, position, id)

        val activity = activity

        val item = listAdapter.getItem(position) as Map.Entry<*, *>

        val builder = AlertDialog.Builder(activity);
        builder.setMessage("Delete ${item.key} key? You will not be able to trust this hotspot.")
                .setPositiveButton("ОК") {
                    dialog, whichButton ->
                    Database.remove(activity, item.key.toString())
                    updateItems(activity)
                }
                .setNegativeButton("CANCEL") {
                    dialog, whichButton ->
                }
        val alert = builder.create();
        alert.show();
    }

    /**
     * Updates adapter and calls notify.
     *
     * @param keyPairs List of scan results.
     */
    fun updateItems(activity: Activity) {

        val keyPairs = Database.getAll(activity)
        val adapter = listAdapter

        if (adapter is SettingsAdapter) {

            adapter.clear()

            if (keyPairs != null) {

                emptyView?.visibility = if (keyPairs.size > 0) View.VISIBLE else View.GONE

                keyPairs.forEach {
                    adapter.add(it)
                }
//                adapter.addAll(keyPairs)
            }

            println(adapter)
            adapter.notifyDataSetChanged()
        }
    }

}

