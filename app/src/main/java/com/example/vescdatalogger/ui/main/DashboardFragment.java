package com.example.vescdatalogger.ui.main;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.vescdatalogger.Message;
import com.example.vescdatalogger.R;

public class DashboardFragment extends Fragment {
    private final Handler mhandler = new Handler();
    private Runnable mTimer1;
    public Message globalMessage = Message.get();

    float batteryVoltage;
    float ERPM;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        return view;
    }
    public void onResume() {
        super.onResume();
        mTimer1 = new Runnable() {
            @Override
            public void run() {
                if (globalMessage.isConnected) {
                    batteryVoltage = globalMessage.getBattery();
                    ERPM = globalMessage.getSpeed();

                    TextView voltageText = (TextView) getView().findViewById(R.id.batteryView);
                    String batteryString = batteryVoltage + " V";
                    voltageText.setText(batteryString);

                    TextView rpmText = (TextView) getView().findViewById(R.id.rpmView);
                    String rpmString = ERPM + " MPH";
                    rpmText.setText(rpmString);
                } else {
                    TextView voltageText = (TextView) getView().findViewById(R.id.batteryView);
                    String batteryString = "XX%";
                    voltageText.setText(batteryString);

                    TextView rpmText = (TextView) getView().findViewById(R.id.rpmView);
                    String rpmString = "XX MPH";
                    rpmText.setText(rpmString);
                }
                mhandler.postDelayed(this, 20);
            }

        };
        mhandler.postDelayed(mTimer1, 50);
    }
}
