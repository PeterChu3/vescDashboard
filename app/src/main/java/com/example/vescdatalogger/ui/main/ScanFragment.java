package com.example.vescdatalogger.ui.main;

import android.Manifest;
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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
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
import com.example.vescdatalogger.UART;
import com.example.vescdatalogger.VescData;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScanFragment extends Fragment {
    private static final String TAG = "DataFragment";
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    //private ScanSettings scanSettings = ScanSettings.Builder()
    //scan filter
    private int setResultNum = 0;

    private BluetoothGatt bluetoothGatt;

    private BluetoothGattCharacteristic UART_RX;
    private BluetoothGattCharacteristic UART_TX;

    private Button writeUART; // can't do getView().findViewById(R.id.button3) here
    private Button readUART;

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
            for (byte element : data) {
                Log.i("Data", element + "\n");
            }
            Message newMessage = new Message(data);
            VescData.get().addMessage(newMessage);
            SystemClock.sleep(500);
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
        writeUART = view.findViewById(R.id.button3);
        readUART = view.findViewById(R.id.button4);
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
        //set onclicklistener here

        //hard code the characteristic? hopefully it works
        UUID rxUUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
        BluetoothGattCharacteristic hardcodeUART_RX = new BluetoothGattCharacteristic(rxUUID, 12, 0);
        UUID txUUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");
        BluetoothGattCharacteristic hardcodeUART_TX = new BluetoothGattCharacteristic(txUUID, 16, 0);

        return view;
    }

    private void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        byte writeValue[] = new byte[6];
        writeValue[0] = 2;
        writeValue[1] = 1;
        writeValue[2] = UART.COMM_GET_VALUES;
        byte payload[] = new byte[1];
        payload[0] = UART.COMM_GET_VALUES;
        char crc = UART.crc16(payload,1);
        writeValue[3] = (byte) (crc >>> 8); //MSB CRC
        Log.i("crc", "crc MSB is " + writeValue[3]); //40
        writeValue[4] = (byte) (crc & 0xFF);
        Log.i("crc", "crc LSB is " + writeValue[4]); //83
        writeValue[5] = 3;
        characteristic.setValue(writeValue);
        boolean success = bluetoothGatt.writeCharacteristic(characteristic);
        Log.i("gatt", "write characteristic " + success);
    }

    private void readCharacteristic(BluetoothGattCharacteristic characteristic) { //not used
        bluetoothGatt.readCharacteristic(characteristic);
        Log.i("gatt", "read characteristic");
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