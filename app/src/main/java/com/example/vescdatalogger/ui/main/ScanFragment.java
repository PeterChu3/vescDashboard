package com.example.vescdatalogger.ui.main;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vescdatalogger.LocationPermissionFragment;
import com.example.vescdatalogger.R;
import com.example.vescdatalogger.ScanResultAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ScanFragment extends Fragment {
    private static final String TAG = "DataFragment";
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    //private ScanSettings scanSettings = ScanSettings.Builder()
    //scan filter

    private List<ScanResult> scanResults = new ArrayList<>();
    private ScanResultAdapter scanResultAdapter = new ScanResultAdapter(scanResults);


    private boolean isScanning = false; //change the button to switch from scan to stop scanning eventually

    private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    //super.onScanResult(callbackType, result); //do you need the super call?
                    int indexQuery = -1;
                    for (int i = 0; i < scanResults.size(); i++) {
                        if (scanResults.get(i).getDevice().getAddress() == result.getDevice().getAddress()) {
                            indexQuery = i;
                            break;
                        }
                    }
                    if (indexQuery != -1) {
                        scanResults.set(indexQuery, result);
                        scanResultAdapter.notifyItemChanged(indexQuery);
                        Log.i("ScanCallBack", "in if block");
                    } else {
                        Log.i("ScanCallBack", "Found BLE device: " + result.getDevice().getName() + ", address: " + result.getDevice().getAddress());
                        scanResults.add(result);
                        scanResultAdapter.notifyItemInserted(scanResults.size() - 1);
                    }
                }

                @Override
                public void onScanFailed(int errorCode) {
                    Log.e("ScanCallBack", "onScanFailed: code " + errorCode);
                }
            };

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
            scanResults.clear();
            scanResultAdapter.notifyDataSetChanged();
            bluetoothLeScanner.startScan(leScanCallback);
            isScanning = true;
        }
    }

    private void stopBLEscan() {
        bluetoothLeScanner.stopScan(leScanCallback);
        isScanning = false;
    }

    /*private void setupRecyclerView() {
        RecyclerView rvDevices = (RecyclerView) findViewById(R.id.scan_results_recycler_view);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        Button scanButton = view.findViewById(R.id.button2);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("ScanCallBack", "Scan button onclick");
                if (!isScanning) {
                    startBLEscan();
                } else {
                    stopBLEscan();
                }
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                       // .setAction("Action", null).show();
            }
        });
        //setup recycler view
        RecyclerView rvDevices = (RecyclerView) view.findViewById(R.id.scan_results_recycler_view);
        rvDevices.setAdapter(scanResultAdapter);
        rvDevices.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDevices.setNestedScrollingEnabled(false);

        //RecyclerView.ItemAnimator animator = rvDevices.getItemAnimator();

        return view;
    }

}