package com.example.vescdatalogger.ui.main;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.vescdatalogger.MainActivity;
import com.example.vescdatalogger.R;
import com.example.vescdatalogger.VescData;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "DataFragment";

    public static int counter = 1;
    public static int x = 5;

    private static Date mostRecentPlotted;

    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    private LineGraphSeries<DataPoint> mSeries1;

    private static String currentParameter = "Battery Voltage"; //parameter currently plotted

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data, container, false);

        mostRecentPlotted = new Date(0);

        Spinner spinner = (Spinner) view.findViewById(R.id.parameter_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.parameter_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        GraphView graph = (GraphView) view.findViewById(R.id.graph);
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    Format formatter = new SimpleDateFormat("h:mm a");
                    return formatter.format(value);
                }
                return super.formatLabel(value, isValueX);
            }
        });
        /*mSeries1 = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });*/
        mSeries1 = new LineGraphSeries<DataPoint>();
        graph.addSeries(mSeries1);
        //mSeries1.appendData(new DataPoint(x++, counter++), true, 400);

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
                    if (currentParameter == "Battery Voltage") {
                        mSeries1.appendData(new DataPoint(recentMessage.timestamp.getTime(), recentMessage.batteryVoltage), false, 400);
                        //call graphView.notifyDataSetChanged?
                    }
                    //set the last timestamp to this message's timestamp, don't add the new one if it is the same timestamp
                    TextView voltageText = (TextView) getView().findViewById(R.id.voltageText);
                    String newVoltage = "Voltage: " + recentMessage.batteryVoltage;
                    voltageText.setText(newVoltage);
                }
                //mSeries1.appendData(new DataPoint(x++, counter++), false, 400);
                mHandler.postDelayed(this, 200);
            }
        };
        mHandler.postDelayed(mTimer1, 1000);
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addEntry();
                        }
                    });
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {

                    }
                }
            }
        }).start();*/
    }


    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String item = (String) parent.getItemAtPosition(pos);
        Log.i("spinner", item); //it is selected at startup. string 'item' here is the string from the string array corresponding to that item
        //check if it is a certain parameter, then reset the plot to what was selected.
        if (item.equals("MOSFET Temp") && !currentParameter.equals(item)) {
            currentParameter = item;
            DataPoint[] newData = new DataPoint[VescData.get().queueSize()];
            Message thisMessage;
            for (int i = 0; i < VescData.get().queueSize(); i++) {
                thisMessage = VescData.get().at(i);
                newData[i] = new DataPoint(thisMessage.timestamp, thisMessage.mosfetTemp);
                Log.i("spinner", "added new mosfet temp data");
            }
            mSeries1.resetData(newData);
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        //nothing i think
    }

}
