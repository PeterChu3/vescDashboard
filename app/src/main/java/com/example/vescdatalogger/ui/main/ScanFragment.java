package com.example.vescdatalogger.ui.main;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
    private int setResultNum = 0;

    private BluetoothGatt bluetoothGatt;

    private List<ScanResult> scanResults = new ArrayList<>();

    public class customListener implements View.OnClickListener {
        ScanResult result;

        public customListener (ScanResult result) {
            this.result = result;
        }
        public customListener () {

        }
        public void setResult(ScanResult result) {
            this.result = result;
            Log.i("customSetResult", "setResult");
        }
        @Override
        public void onClick(View view) {
            Log.i("viewholderOnclick", "clicked it! from custom");
            Log.i("customOnclick", "Found BLE device: " + result.getDevice().getName() + ", address: " + result.getDevice().getAddress());
            if (isScanning) {
                stopBLEscan();
            }
            Log.w("customOnclick","connecting to " + result.getDevice().getAddress());
            bluetoothGatt = result.getDevice().connectGatt(getContext(), false, bluetoothGattCallback);
        }
    }
    customListener newListener = new customListener();

    private ScanResultAdapter scanResultAdapter = new ScanResultAdapter(scanResults, newListener);

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            //super.onConnectionStateChange(gatt, status, newState);
            String deviceAddress = gatt.getDevice().getAddress();
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.w("BluetoothGattCallback", "Successfully connected to " + deviceAddress);
                    //store a reference to bluetoothgatt
                    gatt.discoverServices();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.w("BluetoothGattCallback", "Successfully disconnected from " + deviceAddress);
                    gatt.close();
                }
                else {
                    Log.w("BluetoothGattCallback", "Error " + status + " encountered for " + deviceAddress + "! Disconnecting...");
                    gatt.close();
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.w("BluetoothGattCallback", "onServicesDiscovered success");
                for (BluetoothGattService service : gatt.getServices()) {
                    Log.w("gattServices", service.getUuid().toString());
                    for (BluetoothGattCharacteristic gattCharacteristic : service.getCharacteristics()) {
                        Log.w("gattCharacteristic" , gattCharacteristic.getUuid().toString());
                    }
                }
            } else {
                Log.w("BluetoothGattCallback", "onServicesDiscovered received " + status);
            }
        }

        //override oncharacteristic read
    };

    public static boolean isScanning = false; //change the button to switch from scan to stop scanning eventually

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

    /*private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals("gatt connected")) {
                //connected = true;
            } else if (action.equals("gatt disconnected")) {
                //connected = false
            } else if (action.equals("gatt services discovered")) {
                //display gatt services
            }
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }*/

}