package com.sonpm_cloud.explorea.A5_CreateRoad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.sonpm_cloud.explorea.A2_Login.LoginActivity;
import com.sonpm_cloud.explorea.A4_2_RoadActivity.RoadActivity;
import com.sonpm_cloud.explorea.R;
import com.sonpm_cloud.explorea.data_classes.DirectionsRoute;
import com.sonpm_cloud.explorea.data_classes.MutablePair;
import com.sonpm_cloud.explorea.data_classes.Route;
import com.sonpm_cloud.explorea.data_classes.U;
import com.sonpm_cloud.explorea.maps.AbstractGoogleMapContainerFragment;
import com.sonpm_cloud.explorea.maps.route_creating.DirectionsCreatingStrategy;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

public class FragmentActivityPointsList extends AbstractGoogleMapContainerFragment {
    private static final String TAG = "@@@@@@";//MainActivity.class.getCanonicalName();
    private String url = "https://explorea-server.azurewebsites.net";
    private RequestQueue requestQueue;

    private Polyline lastPolyFoot;
    private Polyline lastPolyBike;

    private boolean footVisible = false;
    private boolean bikeVisible = false;

    private long lastCalculation;
    private int lastDistFoot;
    private int lastDistBike;
    private int lastTimeFoot;
    private int lastTimeBike;
    private String lastCity;

    @SuppressLint("NewApi")
    private synchronized void changeParameters(DirectionsRoute route) {
        if (googleMap == null) return;
        if (lastCalculation > route.queryTime) return;
        if (lastPolyFoot != null) {
            lastPolyFoot.remove();
        } else {
            footVisible = true;
            requireView().findViewById(R.id.walk_toggle).setClickable(true);
            ((ImageView) requireView().findViewById(R.id.walk_toggle))
                    .setImageTintList(ColorStateList.valueOf(requireContext().getColor(R.color.routeFoot)));
            requireView().findViewById(R.id.walk_toggle)
                    .setOnClickListener(this::routeToggleHandler);
        }
        if (lastPolyBike != null) {
            lastPolyBike.remove();
        } else {
            bikeVisible = true;
            requireView().findViewById(R.id.bike_toggle).setClickable(true);
            ((ImageView) requireView().findViewById(R.id.bike_toggle))
                    .setImageTintList(ColorStateList.valueOf(requireContext().getColor(R.color.routeBike)));
            requireView().findViewById(R.id.bike_toggle)
                   .setOnClickListener(this::routeToggleHandler);
        }
        PolylineOptions newFoot = new PolylineOptions().addAll(PolyUtil.decode(route.encodedDirectionsByFoot))
                .color(requireContext().getColor(R.color.routeFoot));
        PolylineOptions newBike = new PolylineOptions().addAll(PolyUtil.decode(route.encodedDirectionsByBike))
                .color(requireContext().getColor(R.color.routeBike));
        lastPolyFoot = googleMap.addPolyline(newFoot);
        lastPolyBike = googleMap.addPolyline(newBike);
        lastPolyFoot.setVisible(footVisible);
        lastPolyBike.setVisible(bikeVisible);
        lastCalculation = route.queryTime;
        lastDistFoot = route.lengthByFoot;
        lastDistBike = route.lengthByBike;
        lastTimeFoot = route.timeByFoot;
        lastTimeBike = route.timeByBike;
        lastCity = route.city;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(route.bounds,
                (int) U.dp_px(32, requireContext())));
    }

    private FragmentViewModel viewModel;

    private GoogleMap googleMap;
    private HashMap<Marker, LatLng> markers;
    private boolean isMapReady = false;

    private RecyclerView recyclerView;
    private FragmentViewModel.RecyclerAdapter recyclerAdapter;

    public static FragmentActivityPointsList newInstance() { return new FragmentActivityPointsList(); }


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View inflate = inflater.inflate(
                R.layout.activity5_pointslist_fragment2,
                container,
                false
        );

        return inflate;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requestQueue =  Volley.newRequestQueue(getActivity());
        viewModel = ViewModelProviders.of(requireActivity()).get(FragmentViewModel.class);

        recyclerView = requireView().findViewById(R.id.recycler_view);

        recyclerAdapter = viewModel.getAdapter();

        ItemTouchHelper.Callback callback = new FragmentViewModel
                .ItemMoveCallback(recyclerAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(recyclerAdapter);
        requireView().findViewById(R.id.buttonCheck).setOnClickListener(this::sendRoute);
    }

    @Override
    protected int getMapViewId() { return R.id.map_view2; }

    @Override
    protected String getMapViewBundleKey() { return "MapViewBundleKey2"; }

    @Override
    protected void refreshCamera() {  }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.markers = new HashMap<>();
        MutablePair<LatLng, Double> location = fetchLocationCoords();
        LatLng latLng = location.first;
        float zoom = location.second.floatValue();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(latLng, zoom)
        ));

        googleMap.getUiSettings().setAllGesturesEnabled(false);

        googleMap.setOnMarkerClickListener(marker -> true);

        List<MutablePair<LatLng, String>> points = viewModel.getListPoints();

        Marker marker;
        for (MutablePair<LatLng, String> point : points) {
            markers.put(
                    marker = googleMap
                            .addMarker(new MarkerOptions().position(point.first)),
                    point.first);
            marker.setDraggable(true);
        }

        viewModel.getPoints().observe(this, _points -> {

            if (_points.size() > 1 && _points.size() < 25) {
                if (lastPolyFoot != null) lastPolyFoot.setColor(requireContext().getColor(android.R.color.darker_gray));
                if (lastPolyBike != null) lastPolyBike.setColor(requireContext().getColor(android.R.color.darker_gray));
                new DirectionsGetTask(this).execute(StreamSupport.stream(viewModel.getListPoints())
                        .map(p -> p.first)
                        .toArray(LatLng[]::new));
            }

            List<LatLng> toRemove = new LinkedList<>(markers.values());
            List<LatLng> toAdd = StreamSupport.stream(_points).map(p -> p.first)
                    .collect(Collectors.toList());

            List<LatLng> temp = new LinkedList<>(toRemove);

            toRemove.removeAll(toAdd);
            toAdd.removeAll(temp);

            if (toRemove.size() > 0) {
                List<Marker> removing = StreamSupport.stream(markers.entrySet())
                        .filter(m -> toRemove.contains(m.getValue()))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());

                StreamSupport.stream(removing).forEach(m -> {
                    m.remove();
                    markers.remove(m);
                });
            }

            if (toAdd.size() > 0) {
                StreamSupport.stream(toAdd).forEach(m -> {
                    Marker mm = googleMap.addMarker(new MarkerOptions().position(m));
                    markers.put(mm, m);
                });
            }
        });
        isMapReady = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.googleMap != null && isMapReady) {
            LatLng[] markers = StreamSupport.stream(this.markers.entrySet())
                    .map(Map.Entry::getValue)
                    .toArray(LatLng[]::new);
            if (this.markers.size() > 1) {
                float dp = 32;
                LatLngBounds.Builder bounds = LatLngBounds.builder();
                for (LatLng marker : markers) bounds.include(marker);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(),
                        (int) U.dp_px(dp, requireContext())));
                int viewHeight = (int) U.dp_px(U.px_dp(
                        mapView.getHeight(), requireContext())/2 - dp/2, requireContext());
                int viewWidth = mapView.getWidth()/2;
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(
                        googleMap.getProjection()
                                .fromScreenLocation(new Point(viewWidth, viewHeight))));
            } else if (this.markers.size() == 1) {
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                        CameraPosition.fromLatLngZoom(markers[0], getDefZoom())));
            }
        }
    }

    private void sendRoute(View view) {

        final int DIST_FOOT_MIN = 0;
        final int DIST_FOOT_MAX = 10000;
        final int DIST_BIKE_MIN = 0;
        final int DIST_BIKE_MAX = 30000;
        final int SIZE_POINT_MIN = 2;
        final int SIZE_POINT_MAX = 25;


        if (lastDistFoot < DIST_FOOT_MIN) {
            Toast.makeText(requireContext(),
                           getString(R.string.distFootTooShort)
                                   .replaceAll("%1", String.valueOf(lastDistFoot))
                                   .replaceAll("%2", String.valueOf(DIST_FOOT_MIN)),
                           Toast.LENGTH_LONG).show();
            return;
        }
        if (lastDistFoot > DIST_FOOT_MAX) {
            Toast.makeText(requireContext(),
                    getString(R.string.distFootTooLong)
                            .replaceAll("%1", String.valueOf(lastDistFoot))
                            .replaceAll("%2", String.valueOf(DIST_FOOT_MAX)),
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (lastDistBike < DIST_BIKE_MIN) {
            Toast.makeText(requireContext(),
                           getString(R.string.distBikeTooShort)
                                   .replaceAll("%1", String.valueOf(lastDistBike))
                                   .replaceAll("%2", String.valueOf(DIST_BIKE_MIN)),
                           Toast.LENGTH_LONG).show();
            return;
        }
        if (lastDistBike > DIST_BIKE_MAX) {
            Toast.makeText(requireContext(),
                    getString(R.string.distBikeTooLong)
                            .replaceAll("%1", String.valueOf(lastDistBike))
                            .replaceAll("%2", String.valueOf(DIST_BIKE_MAX)),
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (markers.size() < SIZE_POINT_MIN) {
            Toast.makeText(requireContext(),
                           getString(R.string.pointCountTooSmall)
                                   .replaceAll("%1", String.valueOf(markers.size()))
                                   .replaceAll("%2", String.valueOf(SIZE_POINT_MIN)),
                           Toast.LENGTH_LONG).show();
            return;
        }
        if (markers.size() > SIZE_POINT_MAX) {
            Toast.makeText(requireContext(),
                    getString(R.string.pointCountTooBig)
                            .replaceAll("%1", String.valueOf(markers.size()))
                            .replaceAll("%2", String.valueOf(SIZE_POINT_MAX)),
                    Toast.LENGTH_LONG).show();
            return;
        }
        List<LatLng> lll = StreamSupport.stream(viewModel.getListPoints())
                .map(p -> p.first)
                .collect(Collectors.toList());
        Route ret = new Route(-1,
                lll,
                0f,
                lastDistFoot,
                lastDistBike,
                lastTimeFoot,
                lastTimeBike,
                lastCity);

        Context context = getContext();
        Map<String, String> params = new HashMap<>();
        params.put("codedRoute", Route.hexEncode(ret.encodedRoute));
        params.put("lengthByFoot", String.valueOf(ret.timeByFoot));
        params.put("lengthByBike", String.valueOf(ret.lengthByBike));
        params.put("timeByFoot", String.valueOf(ret.timeByFoot));
        params.put("timeByBike", String.valueOf(ret.timeByBike));
        params.put("city", "Łodź");//String.valueOf(ret.city));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST,
                url + "/routes",
                new JSONObject(params),
                response -> {
//                    Log.d(" RESPONSE JSONPost", response.toString());
                    Log.d(" RESPONSE JSONPost", "DODANO TRASE");
                },
                error -> {
                    Toast.makeText(context, getString(R.string.request_error_response_msg), Toast.LENGTH_LONG)
                            .show();
                    Log.w(TAG, "request response:failed time=" + error.getNetworkTimeMs());
                    Log.w(TAG, "request response:failed msg=" + error.getMessage());
                }
        ) {
            /** Passing some request headers* */
            @Override
            public Map getHeaders() {
                HashMap headers = new HashMap();
                headers.put("authorization", "Bearer " + LoginActivity.account.getIdToken());
                return headers;
            }
        };
        requestQueue.add(jsonObjReq);

        Log.e("sendRoute", ret.toString());
        Intent intent = new Intent(requireContext(), RoadActivity.class);
        intent.putExtra("ROUTE", ret);
        startActivity(intent);
    }

    private void routeToggleHandler(View view) {

        boolean isFoot = lastPolyFoot.isVisible();
        boolean isBike = lastPolyBike.isVisible();

        final ColorStateList TINT_DISABLED = ColorStateList.valueOf(requireContext().getColor(android.R.color.darker_gray));
        final ColorStateList TINT_FOOT = ColorStateList.valueOf(requireContext().getColor(R.color.routeFoot));
        final ColorStateList TINT_BIKE = ColorStateList.valueOf(requireContext().getColor(R.color.routeBike));

        ImageView footToogle = requireView().findViewById(R.id.walk_toggle);
        ImageView bikeToogle = requireView().findViewById(R.id.bike_toggle);

        switch (view.getId()) {
            case R.id.walk_toggle:
                if (!isFoot) {
                    lastPolyFoot.setVisible(true);
                    footVisible = true;
                    footToogle.setImageTintList(TINT_FOOT);
                } else if (isFoot && !isBike) {
                    lastPolyFoot.setVisible(false);
                    footVisible = false;
                    footToogle.setImageTintList(TINT_DISABLED);
                    lastPolyBike.setVisible(true);
                    bikeVisible = true;
                    bikeToogle.setImageTintList(TINT_BIKE);
                } else if (isFoot && isBike) {
                    lastPolyFoot.setVisible(false);
                    footVisible = false;
                    footToogle.setImageTintList(TINT_DISABLED);
                }
                break;
            case R.id.bike_toggle:
                if (!isBike) {
                    lastPolyBike.setVisible(true);
                    bikeVisible = true;
                    bikeToogle.setImageTintList(TINT_BIKE);
                } else if (isBike && !isFoot) {
                    lastPolyBike.setVisible(false);
                    bikeVisible = false;
                    bikeToogle.setImageTintList(TINT_DISABLED);
                    lastPolyFoot.setVisible(true);
                    footVisible = true;
                    footToogle.setImageTintList(TINT_FOOT);
                } else if (isBike && isFoot) {
                    lastPolyBike.setVisible(false);
                    bikeVisible = false;
                    bikeToogle.setImageTintList(TINT_DISABLED);
                }
                break;
        }
    }

    private static class DirectionsGetTask
            extends AsyncTask<LatLng, Void, DirectionsRoute> {

        FragmentActivityPointsList fragment;

        DirectionsGetTask(FragmentActivityPointsList fragment) {
            this.fragment = fragment;
        }

        @Override
        protected DirectionsRoute doInBackground(LatLng... latLngs) {

            return DirectionsCreatingStrategy
                    .getRecommendedStrategy(latLngs, fragment.requireContext())
                    .createDirectionsRoute();
        }

        @Override
        protected void onPostExecute(DirectionsRoute result) {

            if (result == null) {
                Toast.makeText(fragment.requireContext(),
                               fragment.getString(R.string.directions_null),
                               Toast.LENGTH_LONG).show();
                return;
            }
            fragment.changeParameters(result);
        }
    }
}
