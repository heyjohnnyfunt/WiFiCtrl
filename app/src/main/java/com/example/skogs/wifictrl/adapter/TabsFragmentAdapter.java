package com.example.skogs.wifictrl.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.example.skogs.wifictrl.fragment.AbstractTabFragment;
import com.example.skogs.wifictrl.fragment.HotspotFragment;
import com.example.skogs.wifictrl.fragment.KeysFragment;
import com.example.skogs.wifictrl.fragment.SettingsFragment;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by skogs on 20.04.2016.
 */
public class TabsFragmentAdapter extends FragmentPagerAdapter{

    private Map<Integer, AbstractTabFragment> tabs;
    private Context context;

    public TabsFragmentAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        initTabMaps(context);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position).getTitle();
    }

    @Override
    public Fragment getItem(int position) {
        return tabs.get(position);
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    private void initTabMaps(Context context) {
        tabs = new HashMap<>();
        tabs.put(0, KeysFragment.getInstance(context));
        tabs.put(1, HotspotFragment.getInstance(context));
        tabs.put(2, SettingsFragment.getInstance(context));
    }
}
