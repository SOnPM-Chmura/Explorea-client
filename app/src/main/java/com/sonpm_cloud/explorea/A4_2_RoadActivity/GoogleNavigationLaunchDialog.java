package com.sonpm_cloud.explorea.A4_2_RoadActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.sonpm_cloud.explorea.R;
import com.sonpm_cloud.explorea.data_classes.APIDirectionsDAO;

public class GoogleNavigationLaunchDialog
        extends DialogFragment {

    private APIDirectionsDAO.By selectedMethod;
    private LatLng[] points;
    private View rootView;

    static GoogleNavigationLaunchDialog newInstance(@Nullable APIDirectionsDAO.By what,
                                                    @NonNull String encodedRoute) {
        GoogleNavigationLaunchDialog dialog = new GoogleNavigationLaunchDialog();
        Bundle args = new Bundle();
        if (what != null)
            args.putInt("BY_WHAT", what.ordinal()+1);
        args.putString("ENCODED_ROUTE", encodedRoute);
        dialog.setArguments(args);
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            int by_what_ordinal = getArguments().getInt("BY_WHAT") - 1;
            if (by_what_ordinal >= 0 && by_what_ordinal < APIDirectionsDAO.By.values().length)
                selectedMethod = APIDirectionsDAO.By.values()[by_what_ordinal];

            points = PolyUtil.decode(getArguments().getString("ENCODED_ROUTE")).toArray(new LatLng[0]);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(true);
        setCancelable(true);
        getDialog().setTitle(getString(R.string.select_nav_mthd));
        rootView = inflater.inflate(R.layout.dialog_navigation_lauch_mode_selector, container, false);

        rootView.findViewById(R.id.walk_toggle).setOnClickListener(this::startMethodToggleHandler);
        rootView.findViewById(R.id.bike_toggle).setOnClickListener(this::startMethodToggleHandler);
        rootView.findViewById(R.id.buttonPos).setOnClickListener(this::okHandler);
        rootView.findViewById(R.id.buttonNeg).setOnClickListener(v -> this.dismiss());
        if (selectedMethod == null) rootView.findViewById(R.id.buttonPos).setEnabled(false);
        else switch (selectedMethod) {
            case Foot:
                rootView.findViewById(R.id.walk_toggle).callOnClick();
                break;
            case Bike:
                rootView.findViewById(R.id.bike_toggle).callOnClick();
                break;
        }

        return rootView;
    }

    private void startMethodToggleHandler(View view) {
        rootView.findViewById(R.id.buttonPos).setEnabled(true);
        ((Button) rootView.findViewById(R.id.buttonPos))
                .setTextColor(requireContext().getColor(R.color.design_default_color_primary));
        switch (view.getId()) {
            case R.id.walk_toggle:
                ((ImageView) view).setImageTintList(ColorStateList.valueOf(requireContext().getColor(R.color.routeFoot)));
                selectedMethod = APIDirectionsDAO.By.Foot;
                break;
            case R.id.bike_toggle:
                ((ImageView) view).setImageTintList(ColorStateList.valueOf(requireContext().getColor(R.color.routeBike)));
                selectedMethod = APIDirectionsDAO.By.Bike;
                break;
        }
        rootView.findViewById(R.id.walk_toggle).setOnClickListener(this::methodToggleHandler);
        rootView.findViewById(R.id.bike_toggle).setOnClickListener(this::methodToggleHandler);
        methodToggleHandler(view);
    }

    private void methodToggleHandler(View view) {
        final ColorStateList TINT_DISABLED = ColorStateList.valueOf(requireContext().getColor(android.R.color.darker_gray));
        final ColorStateList TINT_FOOT = ColorStateList.valueOf(requireContext().getColor(R.color.routeFoot));
        final ColorStateList TINT_BIKE = ColorStateList.valueOf(requireContext().getColor(R.color.routeBike));

        switch (view.getId()) {
            case R.id.walk_toggle:
                if (selectedMethod == APIDirectionsDAO.By.Bike) {
                    selectedMethod = APIDirectionsDAO.By.Foot;
                    ((ImageView) view).setImageTintList(TINT_FOOT);
                    ((ImageView) rootView.findViewById(R.id.bike_toggle)).setImageTintList(TINT_DISABLED);
                }
                break;
            case R.id.bike_toggle:
                if (selectedMethod == APIDirectionsDAO.By.Foot) {
                    selectedMethod = APIDirectionsDAO.By.Bike;
                    ((ImageView) view).setImageTintList(TINT_BIKE);
                    ((ImageView) rootView.findViewById(R.id.walk_toggle)).setImageTintList(TINT_DISABLED);
                }
                break;
        }
    }

    private void okHandler(View view) {
        String url = APIDirectionsDAO.createGoogleNavigationURL(points, selectedMethod);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        requireContext().startActivity(intent);
        dismiss();
    }
}