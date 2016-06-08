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
class KeysFragment : AbstractTabFragment() {

    override val layoutId: Int = R.layout.fragment_hotspots

    companion object {

        fun getInstance(context: Context): KeysFragment {
            val args = Bundle()
            val fragment = KeysFragment()
            fragment.arguments = args
            fragment.context = context
            fragment.title = context.getString(R.string.key)

            return fragment
        }
    }

    /*override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewPage = inflater!!.inflate(layoutId, container, false)
        return viewPage
    }*/
}
