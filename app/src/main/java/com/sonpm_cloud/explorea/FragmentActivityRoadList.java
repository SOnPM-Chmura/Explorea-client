package com.sonpm_cloud.explorea;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentActivityRoadList extends Fragment implements AdapterView.OnItemSelectedListener{

    private String[] transports = {"rower", "pieszo"};
    private String chosenTransport;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity4_roadlist_fragment2, container, false);

        Spinner transportSpinner = (Spinner) view.findViewById(R.id.transportSpinner);

        ArrayAdapter<String> arrayLongitude = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, transports);
        arrayLongitude.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        transportSpinner.setAdapter(arrayLongitude);

//        transportSpinner.setOnItemSelectedListener(this.getActivity());

        view.findViewById(R.id.buttonRoad1).setOnClickListener(v -> startActivity(new Intent(v.getContext(), RoadActivity.class)));

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(String.valueOf(view).contains("transportSpinner")) {
            chosenTransport = transports[position];
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        if(String.valueOf(parent).contains("transportSpinner")) {
            chosenTransport = "pieszo";
        }

    }
}
