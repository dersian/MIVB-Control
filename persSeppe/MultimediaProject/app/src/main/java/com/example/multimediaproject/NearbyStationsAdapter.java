package com.example.multimediaproject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class NearbyStationsAdapter extends BaseAdapter {
    private Context context;
    private List<NearbyStations> nearbyStations;

    public NearbyStationsAdapter(Context context, List nearbyStations){
        super();
        this.context = context;
        this.nearbyStations = nearbyStations;
    }

    @Override
    public int getCount() {
        return nearbyStations.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        LayoutInflater  inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.nearbystations_list_view, parent, false);

        TextView tv_stationName = (TextView) view.findViewById(R.id.tvNearbyStationName);
        TextView tv_distance = (TextView) view.findViewById(R.id.tvNearbyStationDistance);

        tv_stationName.setText(nearbyStations.get(i).getStation());
        tv_distance.setText(String.format("%.2f", nearbyStations.get(i).getDistance()) + "m");

        return view;
    }
}
