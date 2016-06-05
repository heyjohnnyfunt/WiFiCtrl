package com.example.skogs.wifictrl

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.example.skogs.wifictrl.adapter.TabsFragmentAdapter

/**
 * Created by skogs on 24.04.2016.
 */
open class MainActivity : AppCompatActivity() {

    companion object {
        private val LAYOUT = R.layout.activity_main
    }

    private var toolbar: Toolbar? = null
    private var drawerLayout: DrawerLayout? = null
    private var viewPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppDefault)
        super.onCreate(savedInstanceState)
        setContentView(LAYOUT)

        initToolbar()
        initNavigationView()
        initTabs()
    }

    private fun initToolbar() {
        toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar!!.setTitle(R.string.app_name)
        toolbar!!.setOnMenuItemClickListener { false }
        toolbar!!.inflateMenu(R.menu.menu)
    }

    private fun initTabs() {
        viewPager = findViewById(R.id.viewPager) as ViewPager
        val adapter = TabsFragmentAdapter(this, supportFragmentManager)
        viewPager!!.adapter = adapter

        val tabLayout = findViewById(R.id.tabLayout) as TabLayout
        tabLayout.setupWithViewPager(viewPager!!)

    }

    private fun initNavigationView() {
        drawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.view_navigation_open, R.string.view_navigation_close)
        drawerLayout!!.setDrawerListener(toggle)
        toggle.syncState()

        // navigation view on the left side
        val navigationView = findViewById(R.id.navigation) as NavigationView
        navigationView.setNavigationItemSelectedListener { menuItem ->
            drawerLayout!!.closeDrawers()
            when (menuItem.itemId) {
                R.id.actionHotspotsItem -> {
                   /* val listFragment = HotspotFragment.newInstance()
                    val transaction = getFragmentManager().beginTransaction()
                    transaction.replace(R.id.layout_frame, listFragment)
                    transaction.commit()*/
                    showHotspotsTab()
                }
            }
            true
        }
    }

    private fun showHotspotsTab() {
        viewPager!!.currentItem = Constants.TAB_2
    }
}
