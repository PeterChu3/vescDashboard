package com.example.vescdatalogger.ui.main;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.vescdatalogger.R;
import com.example.vescdatalogger.VescData;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "DataFragment";

    private static Date mostRecentPlotted;

    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    private LineGraphSeries<DataPoint> mSeries1;

    private static String currentParameter = "Battery Voltage"; //parameter currently plotted
    private static int timeWindow_ms = 5000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data, container, false);

        mostRecentPlotted = new Date(0);

        Spinner parameterSpinner = (Spinner) view.findViewById(R.id.parameter_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.parameter_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        parameterSpinner.setAdapter(adapter);
        parameterSpinner.setOnItemSelectedListener(this);

        Spinner windowSpinner = (Spinner) view.findViewById(R.id.window_spinner);
        ArrayAdapter<CharSequence> adapterWindow = ArrayAdapter.createFromResource(getContext(),
                R.array.window_array, android.R.layout.simple_spinner_item);
        adapterWindow.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        windowSpinner.setAdapter(adapterWindow);
        windowSpinner.setOnItemSelectedListener(this);

        GraphView graph = (GraphView) view.findViewById(R.id.graph);
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    Format formatter = new SimpleDateFormat("h:mm a");
                    return formatter.format(value);
                } else {
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setMaximumFractionDigits(1);
                    return nf.format(value);
                }
            }
        });
        graph.getGridLabelRenderer().setPadding(100);
        mSeries1 = new LineGraphSeries<DataPoint>();
        graph.addSeries(mSeries1);
        Date startXAxis = new Date();
        graph.getViewport().setMaxX(startXAxis.getTime());
        graph.getViewport().setMinX(startXAxis.getTime() - timeWindow_ms);
        graph.getViewport().setXAxisBoundsManual(true);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        mTimer1 = new Runnable() {
            @Override
            public void run() {
                if (VescData.get().queueSize() != 0) {
                    Message recentMessage = VescData.get().getRecent();
                    GraphView graph = (GraphView) getView().findViewById(R.id.graph);
                    graph.getViewport().setMinX(recentMessage.timestamp.getTime() - timeWindow_ms); //change this number to expand time window (in milliseconds)
                    graph.getViewport().setMaxX(recentMessage.timestamp.getTime());
                    mSeries1.appendData(new DataPoint(recentMessage.timestamp.getTime(), recentMessage.getParameter(currentParameter)), true, 400);
                    //set the last timestamp to this message's timestamp, don't add the new one if it is the same timestamp
                    TextView voltageText = (TextView) getView().findViewById(R.id.voltageText);
                    String newVoltage = "Voltage: " + recentMessage.batteryVoltage;
                    voltageText.setText(newVoltage);
                    TextView batteryCurrentText = (TextView) getView().findViewById(R.id.batteryCurrentText);
                    String newCurrent = "Battery Current: " + recentMessage.batteryCurrent;
                    batteryCurrentText.setText(newCurrent);
                    TextView RPMText = (TextView) getView().findViewById(R.id.RPMText);
                    String newRPM = "RPM: " + recentMessage.RPM;
                    RPMText.setText(newRPM);
                    TextView MOSFETTempText = (TextView) getView().findViewById(R.id.MOSFETTempText);
                    String newMOSFETTemp = "MOSFET Temp: " + recentMessage.mosfetTemp;
                    MOSFETTempText.setText(newMOSFETTemp);
                }
                mHandler.postDelayed(this, 200);
            }
        };
        mHandler.postDelayed(mTimer1, 1000);
    }


    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String item = (String) parent.getItemAtPosition(pos);
        Log.i("spinner", item); //it is selected at startup. string 'item' here is the string from the string array corresponding to that item
        //check if it is a certain parameter, then reset the plot to what was selected.
        if (item.substring(item.length() - 1).equals("s")) {
            int newTime = Integer.parseInt(item.substring(0, item.length()-1));
            if (timeWindow_ms != newTime * 1000) { //need to change window size
                timeWindow_ms = newTime * 1000;
                if (VescData.get().queueSize() != 0) {
                    Message recentMessage = VescData.get().getRecent();
                    GraphView graph = (GraphView) getView().findViewById(R.id.graph);
                    graph.getViewport().setMinX(recentMessage.timestamp.getTime() - timeWindow_ms); //change this number to expand time window (in milliseconds)
                    graph.getViewport().setMaxX(recentMessage.timestamp.getTime());
                }
            }
        } else if (!currentParameter.equals(item)) {
            currentParameter = item;
            DataPoint[] newData = new DataPoint[VescData.get().queueSize()];
            Message thisMessage;
            for (int i = 0; i < VescData.get().queueSize(); i++) {
                thisMessage = VescData.get().at(i);
                newData[i] = new DataPoint(thisMessage.timestamp, thisMessage.getParameter(item));
                Log.i("spinner", "added new data for" + item);
            }
            mSeries1.resetData(newData);
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }

}
