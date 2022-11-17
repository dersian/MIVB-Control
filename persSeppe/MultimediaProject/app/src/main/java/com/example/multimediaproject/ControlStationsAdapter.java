package com.example.multimediaproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class ControlStationsAdapter extends BaseAdapter {
    private Context context;
    private List<String> stationsToCheck;
    private List<String> controlStations;

    public ControlStationsAdapter(Context context, List stationsToCheck, List controlStations){
        super();
        this.context = context;
        this.stationsToCheck = stationsToCheck;
        this.controlStations = controlStations;
    }

    @Override
    public int getCount() {
        return stationsToCheck.size();
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
        view = inflater.inflate(R.layout.popupcontrol_list_view, parent, false);

        TextView tv_stationName = (TextView) view.findViewById(R.id.tvControlLine);
        Button btnYes = (Button) view.findViewById(R.id.btnCLYes);
        Button btnNo = (Button) view.findViewById(R.id.btnCLNo);

        tv_stationName.setText(stationsToCheck.get(i));

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if already in control list
                if(controlStations.contains(stationsToCheck.get(i))){
                    // Stays in list
                }
                else{ // Not yet in list
                    controlStations.add(stationsToCheck.get(i));
                }
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Has to be in the control list
                controlStations.remove((stationsToCheck.get(i)));
            }
        });

        return view;
    }
}
