package com.sonpm_cloud.explorea;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;
import java.util.List;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

public class Activity5_Fragment2 extends AbstractGoogleMapContainerFragment {

    Activity5_Fragment_ViewModel viewModel;

    private GoogleMap googleMap;

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    List<String> labelList = new LinkedList<>();

    public static Activity5_Fragment2 newInstance() { return new Activity5_Fragment2(); }


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(
                R.layout.activity5_pointslist_fragment2,
                container,
                false
        );
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(Activity5_Fragment_ViewModel.class);


        recyclerView = getView().findViewById(R.id.recycler_view);
        labelList = StreamSupport.stream(viewModel.getPoints())
                .map(markerStringPair -> markerStringPair.second)
                .collect(Collectors.toList());

        recyclerAdapter = new RecyclerAdapter(labelList);

        ItemTouchHelper.Callback callback = new ItemMoveCallback(recyclerAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    int getMapViewId() { return R.id.map_view2; }

    @Override
    String getMapViewBundleKey() { return "MapViewBundleKey2"; }

    @Override
    void refreshCamera() {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        Pair<LatLng, Double> location = fetchLocationCoords();
        googleMap.setMinZoomPreference(location.second.floatValue());
        LatLng latLng = location.first;
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }
}
