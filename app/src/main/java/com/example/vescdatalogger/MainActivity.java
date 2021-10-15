package com.example.vescdatalogger;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.vescdatalogger.ui.main.DataFragment;
import com.example.vescdatalogger.ui.main.FileFragment;
import com.example.vescdatalogger.ui.main.ScanFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.vescdatalogger.ui.main.SectionsPagerAdapter;
import com.example.vescdatalogger.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final int ENABLE_BLUETOOTH_REQUEST_CODE = 1;

    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private void startBLEscan() {
        if (!hasLocationPermission()) {

        } else {

        }
    }

    private void requestLocationPermission() {
        LocationPermissionFragment locationAlert = new LocationPermissionFragment();
        //locationAlert.show(getSupportFragmentManager(), "locationAlertDialog");
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!bluetoothAdapter.isEnabled()) {
            promptEnableBluetooth();
        }
    }

    private void promptEnableBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE);
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        sectionsPagerAdapter.addFragment(new ScanFragment());
        sectionsPagerAdapter.addFragment(new DataFragment());
        sectionsPagerAdapter.addFragment(new FileFragment());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.fab;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button scanButton = findViewById(R.id.button2);
        /*scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBLEscan();
            }
        });*/
    }
}