package com.example.skogs.wifictrl.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.skogs.wifictrl.R;

import java.util.List;

/**
 * Created by skogs on 22.04.2016.
 */
public class HotspotAdapter extends RecyclerView.Adapter<HotspotAdapter.HotspotHolder> {

    private List<String> data;

    public HotspotAdapter(List<String> data) {
        this.data = data;
    }

    @Override
    public HotspotHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hotspot_item, parent, false);

        return new HotspotHolder(view);
    }

    @Override
    public void onBindViewHolder(HotspotHolder holder, int position) {
        holder.title.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class HotspotHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        TextView title;

        public HotspotHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardView);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }

}
