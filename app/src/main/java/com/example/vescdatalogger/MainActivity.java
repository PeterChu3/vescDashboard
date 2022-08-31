package com.example.vescdatalogger;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

import com.example.vescdatalogger.ui.main.ConfigFragment;
import com.example.vescdatalogger.ui.main.DashboardFragment;
import com.example.vescdatalogger.ui.main.ScanFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import com.example.vescdatalogger.ui.main.SectionsPagerAdapter;
import com.example.vescdatalogger.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final int ENABLE_BLUETOOTH_REQUEST_CODE = 1;

    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onResume() {
        super.onResume();
        if (!bluetoothAdapter.isEnabled()) {
            promptEnableBluetooth();
        }
    }

    @SuppressLint("MissingPermission")
    private void promptEnableBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        sectionsPagerAdapter.addFragment(new ScanFragment());
        sectionsPagerAdapter.addFragment(new ConfigFragment());
        sectionsPagerAdapter.addFragment(new DashboardFragment());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);


    }
}