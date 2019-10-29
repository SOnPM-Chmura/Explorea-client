package com.sonpm_cloud.explorea;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentActivitySettings extends Fragment implements AdapterView.OnItemSelectedListener {

    private String[] transports = {"rower", "pieszo"};
    private String[] times = {"30 min", "60 min"};
    private String chosenCity;
    private String chosenTransport;
    private int chosenTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity4_settings_fragment1, container, false);

        EditText cityText = view.findViewById(R.id.citySelected);

        Spinner transportSpinner = view.findViewById(R.id.transportSpinner1);
        Spinner timeSpinner = view.findViewById(R.id.timeSpinner1);

        ArrayAdapter<String> arrayTransport = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, transports);
        arrayTransport.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        transportSpinner.setAdapter(arrayTransport);

        ArrayAdapter<String> arrayTime = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, times);
        arrayTime.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        timeSpinner.setAdapter(arrayTime);

        transportSpinner.setOnItemSelectedListener(this);
        timeSpinner.setOnItemSelectedListener(this);

        System.out.println(chosenTransport);
        System.out.println(chosenTime);

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(String.valueOf(parent).contains("transportSpinner1")) {
            chosenTransport = transports[position];
            System.out.println(chosenTransport);
        }
        else if (String.valueOf(parent).contains("timeSpinner1")) {
            chosenTime = Integer.valueOf(times[position].substring(0 , times[position].lastIndexOf(" ")));
            System.out.println(chosenTime);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        if(String.valueOf(parent).contains("transportSpinner1")) {
            chosenTransport = "pieszo";
        }
        else if(String.valueOf(parent).contains("timeSpinner1")) {
            chosenTime = 30;
        }
    }

}
