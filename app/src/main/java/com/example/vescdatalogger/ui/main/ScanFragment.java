package com.example.vescdatalogger.ui.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.vescdatalogger.LocationPermissionFragment;
import com.example.vescdatalogger.R;
import com.google.android.material.snackbar.Snackbar;

public class ScanFragment extends Fragment {
    private static final String TAG = "DataFragment";

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        LocationPermissionFragment locationAlert = new LocationPermissionFragment();
        locationAlert.show(getFragmentManager(), "locationAlertDialog");
    }

    private void startBLEscan() {
        if (!hasLocationPermission()) {
            requestLocationPermission();
        } else {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        Button scanButton = view.findViewById(R.id.button2);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBLEscan();
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                       // .setAction("Action", null).show();
            }
        });
        return view;
    }

}