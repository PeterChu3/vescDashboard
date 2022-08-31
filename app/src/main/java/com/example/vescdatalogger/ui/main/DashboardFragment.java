package com.example.vescdatalogger.ui.main;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.vescdatalogger.Message;
import com.example.vescdatalogger.R;

import java.text.DecimalFormat;

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
                try {
                    if (globalMessage.isConnected) {
                        batteryVoltage = globalMessage.getBattery();
                        ERPM = globalMessage.getSpeed() / 7;
                        DecimalFormat df = new DecimalFormat("#.#");
                        String batPercentage = df.format(((batteryVoltage-(3.3 * 12))/12)*100); //Change this to run withMechSetup
                        String velocity = df.format(ERPM * 3.142 * (60.0/1609.0) * 0.305 * (11.0/66.0));
                        Log.i("OUT_velocity", velocity);
                        TextView voltageText = (TextView) getView().findViewById(R.id.batteryView);
                        String batteryString = batPercentage + " %";
                        voltageText.setText(batteryString);
                        voltageText.setBackgroundResource(R.color.green);

                        TextView rpmText = (TextView) getView().findViewById(R.id.rpmView);
                        String rpmString = velocity + " MPH";
                        rpmText.setText(rpmString);
                    } else {
                        TextView voltageText = (TextView) getView().findViewById(R.id.batteryView);
                        String batteryString = "XX.X%";
                        voltageText.setText(batteryString);

                        TextView rpmText = (TextView) getView().findViewById(R.id.rpmView);
                        String rpmString = "XX.X MPH";
                        rpmText.setText(rpmString);
                    }
                } catch (NullPointerException ignored) {

                }


                mhandler.postDelayed(this, 20);
            }

        };
        mhandler.postDelayed(mTimer1, 50);
    }
}
