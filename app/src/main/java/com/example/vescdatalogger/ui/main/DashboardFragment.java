package com.example.vescdatalogger.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.vescdatalogger.Message;
import com.example.vescdatalogger.R;

public class DashboardFragment extends Fragment {
    private static final String TAG = "FileFragment";
    public Message globalMessage = Message.get();

    float batteryVoltage = globalMessage.getBattery();
    float ERPM = globalMessage.getSpeed();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        return view;
    }
}
