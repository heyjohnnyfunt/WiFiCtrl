package com.example.skogs.wifictrl.fragment

import android.content.Context
import android.os.Bundle
import com.example.skogs.wifictrl.R
import java.util.*

/**
 * Created by skogs on 20.04.2016.
 */
class HotspotFragment : AbstractTabFragment() {

    override val layoutId: Int = R.layout.fragment_hotspots

    companion object {

        fun getInstance(context: Context): HotspotFragment {
            val args = Bundle()
            val fragment = HotspotFragment()
            fragment.arguments = args
            fragment.context = context
            fragment.title = context.getString(R.string.hotspots)

            return fragment
        }
    }

    /*override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewPage = inflater!!.inflate(layoutId, container, false)

        val rv = viewPage!!.findViewById(R.id.wifi_list) as RecyclerView
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = HotspotAdapter(createMockData())

        return viewPage
    }*/

    private fun createMockData(): List<String> {

        val data = ArrayList<String>()
        data.add("Item 1")
        data.add("Item 1")
        data.add("Item 1")
        data.add("Item 1")
        data.add("Item 1")
        data.add("Item 1")

        return data
    }
}
