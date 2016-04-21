package com.example.skogs.wifictrl.fragment;

import android.content.Context;
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
public class KeysFragment extends AbstractTabFragment {

    private static final int LAYOUT = R.layout.fragment_hotspots;

    public static KeysFragment getInstance(Context context){
        Bundle args = new Bundle();
        KeysFragment fragment = new KeysFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.tab_keys));

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        return view;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
