package com.example.skogs.wifictrl.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.example.skogs.wifictrl.fragment.HotspotFragment;

/**
 * Created by skogs on 20.04.2016.
 */
public class TabsPagerFragmentAdapter extends FragmentPagerAdapter{

    private String[] tabs;

    public TabsPagerFragmentAdapter(FragmentManager fm) {
        super(fm);
        tabs = new String[] {
                "Tab 1",
                "Hotspots",
                "Settings"
        };
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return HotspotFragment.getInstance();
            case 1:
                return HotspotFragment.getInstance();
            case 2:
                return HotspotFragment.getInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabs.length;
    }
}
