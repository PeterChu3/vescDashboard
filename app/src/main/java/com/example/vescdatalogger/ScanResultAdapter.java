package com.example.vescdatalogger;

import android.annotation.SuppressLint;
import android.bluetooth.le.ScanResult;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.vescdatalogger.ui.main.ScanFragment;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is for the scan results on the recyclerView of the UI. This allows the user to see
 * and select the device the user wants to connect to.
 */
public class ScanResultAdapter extends RecyclerView.Adapter<ScanResultAdapter.ViewHolder> {

    private List<ScanResult> items;
    private ScanFragment.customListener customListener;

    public class ViewHolder extends RecyclerView.ViewHolder { //does it need to be static?
        public TextView device_name;
        public TextView mac_address;
        public TextView signal_strength;

        public ViewHolder(View view) {
            super(view);

            device_name = (TextView) view.findViewById(R.id.device_name);
            mac_address = (TextView) view.findViewById(R.id.mac_address);
            signal_strength = (TextView) view.findViewById(R.id.signal_strength);
        }

        @SuppressLint("MissingPermission")
        public void bind(ScanResult result) {
            if (result.getDevice().getName() == null || result.getDevice().getName().isEmpty()) {
                device_name.setText("Unknown");
            } else device_name.setText(result.getDevice().getName());
            mac_address.setText(result.getDevice().getAddress());
            String signalStrengthString = result.getRssi() + " dBM";
            signal_strength.setText(signalStrengthString);
            customListener.setResult(result); //this is being called everytime a new one is added to the list
            ScanFragment.customListener customListener1 = new ScanFragment().new customListener(result);
            this.itemView.setOnClickListener(customListener1);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int ViewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_scan_result, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        ScanResult item = items.get(position);
        viewHolder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public ScanResultAdapter(List<ScanResult> results, ScanFragment.customListener onClickListener) {

        items = results;
        this.customListener = onClickListener;
    }
}
