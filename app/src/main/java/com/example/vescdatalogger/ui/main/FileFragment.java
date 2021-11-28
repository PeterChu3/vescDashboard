package com.example.vescdatalogger.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.vescdatalogger.R;

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
                TextView minBattCurrent = (TextView) getView().findViewById(R.id.minBattCurrent);
                String newText = "batt: " + test;
                test++;
                minBattCurrent.setText(newText);
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(updateTimer, 1000);
    }

    private void writeToFile(String data, Context context) {
        try {
            File path = getContext().getApplicationContext().getFilesDir();
            FileOutputStream writer = new FileOutputStream(new File(path, "file.txt"));
            writer.write("hello".getBytes());
            writer.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

}
