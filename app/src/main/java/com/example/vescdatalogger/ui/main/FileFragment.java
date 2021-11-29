package com.example.vescdatalogger.ui.main;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.vescdatalogger.R;
import com.example.vescdatalogger.VescData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class FileFragment extends Fragment {
    private static final String TAG = "FileFragment";

    private Runnable updateTimer;
    private final Handler handler = new Handler();
    private int test = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file, container, false);

        Button exportButton = view.findViewById(R.id.button_export);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("export", "clicked button");
                String data = "hello!\n";
                writeToFile(data, getContext());
                Log.i("export", "returned from writeToFile");
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTimer = new Runnable() {
            @Override
            public void run() {
                if (VescData.get().queueSize() != 0) {
                    TextView minBattCurrent = (TextView) getView().findViewById(R.id.minBattCurrent);
                    String newText = "Min Battery Current: " + VescData.get().minBatteryCurrent;
                    minBattCurrent.setText(newText);
                    TextView maxBattCurrent = (TextView) getView().findViewById(R.id.maxBattCurrent);
                    newText = "Max Battery Current: " + VescData.get().maxBatteryCurrent;
                    maxBattCurrent.setText(newText);
                    TextView avgBattCurrent = (TextView) getView().findViewById(R.id.avgBattCurrent);
                    newText = "Avg Battery Current: " + VescData.get().avgBatteryCurrent;
                    avgBattCurrent.setText(newText);
                    TextView minRPM = (TextView) getView().findViewById(R.id.minRPM);
                    newText = "Min RPM: " + VescData.get().minRPM;
                    minRPM.setText(newText);
                    TextView maxRPM = (TextView) getView().findViewById(R.id.maxRPM);
                    newText = "Max RPM: " + VescData.get().maxRPM;
                    maxRPM.setText(newText);
                    TextView avgRPM = (TextView) getView().findViewById(R.id.avgRPM);
                    newText = "Avg RPM: " + VescData.get().avgRPM;
                    avgRPM.setText(newText);
                    TextView minMosfetTemp = (TextView) getView().findViewById(R.id.minFETTemp);
                    newText = "Min MOSFET Temp: " + VescData.get().minMosfetTemp;
                    minMosfetTemp.setText(newText);
                    TextView maxMosfetTemp = (TextView) getView().findViewById(R.id.maxFETTEMP);
                    newText = "Max MOSFET Temp: " + VescData.get().maxMosfetTemp;
                    maxMosfetTemp.setText(newText);
                    TextView avgMosfetTemp = (TextView) getView().findViewById(R.id.avgFETTEMP);
                    newText = "Avg MOSFET Temp: " + VescData.get().avgMosfetTemp;
                    avgMosfetTemp.setText(newText);
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(updateTimer, 1000);
    }

    private void writeToFile(String data, Context context) {
        try {
            File path = getContext().getApplicationContext().getFilesDir();
            //FileOutputStream writer = new FileOutputStream(new File(path, "file.txt"));
            FileOutputStream writer = new FileOutputStream(new File("/storage/self/primary/Documents", "file.txt"));
            writer.write("hello".getBytes());
            writer.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private boolean hasFileIOPermission() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestFileIOPermission() {

    }

}
