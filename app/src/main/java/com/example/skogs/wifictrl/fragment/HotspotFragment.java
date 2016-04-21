package com.example.skogs.wifictrl.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.skogs.wifictrl.R;
import com.example.skogs.wifictrl.adapter.HotspotAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by skogs on 20.04.2016.
 */
public class HotspotFragment extends AbstractTabFragment {

    private static final int LAYOUT = R.layout.fragment_hotspots;

    public static HotspotFragment getInstance(Context context){
        Bundle args = new Bundle();
        HotspotFragment fragment = new HotspotFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.tab_hotspots));

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setAdapter(new HotspotAdapter(createMockData()));

        return view;
    }

    private List<String> createMockData() {

        List<String> data = new ArrayList<>();
        data.add("Item 1");
        data.add("Item 1");
        data.add("Item 1");
        data.add("Item 1");
        data.add("Item 1");
        data.add("Item 1");

        return data;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
