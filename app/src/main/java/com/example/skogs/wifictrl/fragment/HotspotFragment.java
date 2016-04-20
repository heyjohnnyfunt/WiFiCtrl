package com.example.skogs.wifictrl.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.skogs.wifictrl.R;

/**
 * Created by skogs on 20.04.2016.
 */
public class HotspotFragment extends Fragment {

    private static final int LAYOUT = R.layout.fragment_hotspots;

    private View view;

    public static HotspotFragment getInstance(){
        Bundle args = new Bundle();
        HotspotFragment fragment = new HotspotFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        return view;
    }
}
