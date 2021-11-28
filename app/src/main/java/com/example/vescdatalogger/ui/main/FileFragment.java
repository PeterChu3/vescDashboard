package com.example.vescdatalogger.ui.main;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.vescdatalogger.R;

public class FileFragment extends Fragment {
    private static final String TAG = "FileFragment";

    private Runnable updateTimer;
    private final Handler handler = new Handler();
    private int test = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file, container, false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTimer = new Runnable() {
            @Override
            public void run() {
                TextView minBattCurrent = (TextView) getView().findViewById(R.id.minBattCurrent);
                String newText = "batt: " + test;
                test++;
                minBattCurrent.setText(newText);
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(updateTimer, 1000);
    }
}
