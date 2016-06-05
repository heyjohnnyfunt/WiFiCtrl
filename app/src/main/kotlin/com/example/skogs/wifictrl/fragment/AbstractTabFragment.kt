package com.example.skogs.wifictrl.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by skogs on 22.04.2016.
 */
abstract class AbstractTabFragment : Fragment() {

    var title: String? = null
    protected var context: Context? = null
        get() = field
        set(value) {
            field = value
        }

    protected var viewPage: View? = null

    abstract val layoutId: Int

    /* protected fun setContext(context: Context) {
         this.context = context
     }*/

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewPage = inflater!!.inflate(layoutId, container, false)
        return viewPage
    }
}
