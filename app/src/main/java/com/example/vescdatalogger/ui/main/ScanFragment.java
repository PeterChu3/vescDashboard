package com.example.vescdatalogger.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vescdatalogger.LocationPermissionFragment;
import com.example.vescdatalogger.Message;
import com.example.vescdatalogger.R;
import com.example.vescdatalogger.ScanResultAdapter;
import com.example.vescdatalogger.UART;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The Scan Fragment is the code where the user connects to the VESC. It is admittly cluttered.
 */
public class ScanFragment extends Fragment {
    private static final String TAG = "DataFragment";
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    //private ScanSettings scanSettings = ScanSettings.Builder()
    //scan filter
    private int setResultNum = 0;
    private final Handler mhandler2 = new Handler();
    private Runnable mTimer2;

    private BluetoothGatt bluetoothGatt;

    private BluetoothGattCharacteristic UART_RX;
    private BluetoothGattCharacteristic UART_TX;

    private List<ScanResult> scanResults = new ArrayList<>();
    public Message globalMessage = Message.get();
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
        @SuppressLint("MissingPermission")
        @Override
        public void onClick(View view) {
            Log.i("viewholderOnclick", "clicked it! from custom");
            Log.i("customOnclick", "Found BLE device: " + result.getDevice().getName() + ", address: " + result.getDevice().getAddress());
            if (isScanning) {
                stopBLEscan();
            }
            Log.w("customOnclick","connecting to " + result.getDevice().getAddress());
            if (!globalMessage.isConnected) {
                bluetoothGatt = result.getDevice().connectGatt(getContext(), false, bluetoothGattCallback);
            }
        }
    }
    customListener newListener = new customListener();

    private ScanResultAdapter scanResultAdapter = new ScanResultAdapter(scanResults, newListener);

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        @SuppressLint("MissingPermission")
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            //super.onConnectionStateChange(gatt, status, newState);
            String deviceAddress = gatt.getDevice().getAddress();
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.w("BluetoothGattCallback", "Successfully connected to " + deviceAddress);
                    //store a reference to bluetoothgatt
                    gatt.discoverServices();
                    globalMessage.isConnected = true;
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.w("BluetoothGattCallback", "Successfully disconnected from " + deviceAddress);
                    gatt.close();
                    globalMessage.isConnected = false;
                }
                else {
                    Log.w("BluetoothGattCallback", "Error " + status + " encountered for " + deviceAddress + "! Disconnecting...");
                    gatt.close();
                    globalMessage.isConnected = false;
                }
            }
        }
        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.w("BluetoothGattCallback", "onServicesDiscovered success");
                for (BluetoothGattService service : gatt.getServices()) {
                    Log.w("gattServices", service.getUuid().toString());
                    for (BluetoothGattCharacteristic gattCharacteristic : service.getCharacteristics()) {
                        Log.w("gattCharacteristic" , gattCharacteristic.getUuid().toString());
                        if (gattCharacteristic.getUuid().toString().equals("00002a00-0000-1000-8000-00805f9b34fb")) {
                            //readCharacteristic(gattCharacteristic);
                        }
                        if (gattCharacteristic.getUuid().toString().equals("6e400002-b5a3-f393-e0a9-e50e24dcca9e")) {
                            UART_RX = gattCharacteristic;
                            Log.i("button check", gattCharacteristic.getUuid() + " " + gattCharacteristic.getProperties() + " " + gattCharacteristic.getPermissions());
                            //writeCharacteristic(gattCharacteristic);
                            for (BluetoothGattDescriptor descriptor : gattCharacteristic.getDescriptors()) {
                                Log.i("descriptors", descriptor.toString());
                            }
                        } else if (gattCharacteristic.getUuid().toString().equals("6e400003-b5a3-f393-e0a9-e50e24dcca9e")) {
                            UART_TX = gattCharacteristic;
                            Log.i("button check", gattCharacteristic.getUuid() + " " + gattCharacteristic.getProperties() + " " + gattCharacteristic.getPermissions());
                            for (BluetoothGattDescriptor descriptor : gattCharacteristic.getDescriptors()) {
                                Log.i("descriptors", descriptor.getUuid().toString());
                            }
                            //readCharacteristic(gattCharacteristic);
                            //gatt.readCharacteristic(gattCharacteristic);
                            //gatt.setCharacteristicNotification(gattCharacteristic, true);
                            //writeCharacteristic(UART_RX);
                            //Log.i("gatt", "set characteristic notifications for TX");
                        }
                    }
                }
                boolean notify = gatt.setCharacteristicNotification(UART_TX, true);
                Log.i("gatt", "set characteristic notifications for TX " + notify);
                BluetoothGattDescriptor descriptor = UART_TX.getDescriptors().get(0);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
            } else {
                Log.w("BluetoothGattCallback", "onServicesDiscovered received " + status);
            }

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i("gatt",  "onCharacteristic read"); //this does not print
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i("data", characteristic.getStringValue(0));
                /*byte[] data = characteristic.getValue();
                for (byte element : data) {
                    Log.i("Data", element + "\n");
                }*/
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.i("gatt",  "onCharacteristicChanged");
            byte[] data = characteristic.getValue();
//            for (byte element : data) {
//                Log.i("Data_OUT", element + "\n");
//            }
            SystemClock.sleep(50);
            globalMessage.addBytes(data, data.length);
            globalMessage.printBattery();
            globalMessage.printSpeed();
            //globalMessage.printGoodMessage();
            writeCharacteristic(UART_RX);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i("write", characteristic.getUuid().toString() + " " + status);
            //gatt.setCharacteristicNotification(UART_TX, true); //might need to set the descriptor to true first
//            boolean notify = gatt.setCharacteristicNotification(UART_TX, true);
//            Log.i("gatt", "set characteristic notifications for TX " + notify);
//            BluetoothGattDescriptor descriptor = UART_TX.getDescriptors().get(0);
//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            gatt.writeDescriptor(descriptor);
            //Log.i("gatt", "set characteristic notifications for TX" + UART_TX.getUuid().toString());
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.i("descriptor write", descriptor.getUuid().toString() + " " + status);
            writeCharacteristic(UART_RX);
//            try {
//                Thread.sleep(200);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            boolean notify = gatt.setCharacteristicNotification(UART_TX, true);
//            Log.i("gatt", "set characteristic notifications for TX " + notify);
        }

    };

    public static boolean isScanning = false; //change the button to switch from scan to stop scanning eventually

    private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                @SuppressLint("MissingPermission")
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
    @SuppressLint("MissingPermission")
    private void startBLEscan() {
        if (!hasLocationPermission()) {
            requestLocationPermission();
        } else {
            scanResults.clear();
            scanResultAdapter.notifyDataSetChanged();



            List<ScanFilter> filters = new ArrayList<ScanFilter>();
            ScanFilter filter = new ScanFilter.Builder()
                    .setServiceUuid( new ParcelUuid(UUID.fromString( "6e400001-b5a3-f393-e0a9-e50e24dcca9e" )) )
                    .build();
            filters.add(filter);

            ScanSettings settings = new ScanSettings.Builder()
                    .setScanMode( ScanSettings.SCAN_MODE_LOW_LATENCY )
                    .build();

            bluetoothLeScanner.startScan(filters,settings,leScanCallback);
            isScanning = true;
        }
    }
    @SuppressLint("MissingPermission")
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
        Button scanButton = view.findViewById(R.id.buttonScanStop);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("ScanCallBack", "Scan button onclick");
                if (!isScanning) {
                    startBLEscan();
                    scanButton.setText("Stop Scan");
                } else {
                    stopBLEscan();
                    scanButton.setText("Scan");
                }
            }
        });
        //setup recycler view
        RecyclerView rvDevices = (RecyclerView) view.findViewById(R.id.scan_results_recycler_view);
        rvDevices.setAdapter(scanResultAdapter);
        rvDevices.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDevices.setNestedScrollingEnabled(false);

        //RecyclerView.ItemAnimator animator = rvDevices.getItemAnimator();
        //set onclicklistener here
        //hard code the characteristic? hopefully it works
        UUID rxUUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
        BluetoothGattCharacteristic hardcodeUART_RX = new BluetoothGattCharacteristic(rxUUID, 12, 0);
        UUID txUUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");
        BluetoothGattCharacteristic hardcodeUART_TX = new BluetoothGattCharacteristic(txUUID, 16, 0);

        return view;
    }
    @SuppressLint("MissingPermission")
    private void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        byte writeValue[] = {2,5,UART.COMM_GET_VALUES_SETUP_SELECTIVE,0,0,1,-128, 0, 0, 3};
        char crc = UART.crc16(writeValue,5);
        writeValue[7] = (byte) (crc >>> 8);
        writeValue[8] = (byte) (crc & 0xFF);
        characteristic.setValue(writeValue);
        boolean success = bluetoothGatt.writeCharacteristic(characteristic);
        Log.i("gatt", "write characteristic " + success);
    }
    @SuppressLint("MissingPermission")
    private void readCharacteristic(BluetoothGattCharacteristic characteristic) { //not used
        bluetoothGatt.readCharacteristic(characteristic);
        Log.i("gatt", "read characteristic");
    }



}