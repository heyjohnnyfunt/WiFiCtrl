package com.example.skogs.wifictrl.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.skogs.wifictrl.R

/**
 * Created by skogs on 20.04.2016.
 */
class SettingsFragment : AbstractTabFragment() {

    override val layoutId: Int = R.layout.fragment_hotspots

    companion object {

//        private val LAYOUT = R.layout.fragment_hotspots

        fun getInstance(context: Context): SettingsFragment {
            val args = Bundle()
            val fragment = SettingsFragment()
            fragment.arguments = args
            fragment.context = context
            fragment.title = context.getString(R.string.tab_setting)

            return fragment
        }
    }

    /*override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewPage = inflater!!.inflate(LAYOUT, container, false)
        return viewPage
    }*/

}

