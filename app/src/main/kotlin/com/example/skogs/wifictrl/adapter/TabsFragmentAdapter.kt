package com.example.skogs.wifictrl.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.skogs.wifictrl.fragment.AbstractTabFragment
import com.example.skogs.wifictrl.fragment.HotspotFragment
import com.example.skogs.wifictrl.fragment.KeysFragment
import com.example.skogs.wifictrl.fragment.SettingsFragment

import java.util.HashMap

/**
 * Created by skogs on 20.04.2016.
 */

class TabsFragmentAdapter(private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private var tabs: MutableMap<Int, AbstractTabFragment>? = null

    init {
        initTabMaps(context)
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabs!![position]!!.title.toString()
    }

    override fun getItem(position: Int): Fragment {
        return tabs!![position]!!
    }

    override fun getCount(): Int {
        return tabs!!.size
    }

    private fun initTabMaps(context: Context) {
        tabs = HashMap<Int, AbstractTabFragment>()
        tabs!!.put(0, KeysFragment.getInstance(context))
//        tabs!!.put(1, HotspotFragment.getInstance(context))
        tabs!!.put(2, SettingsFragment.getInstance(context))
    }
}
