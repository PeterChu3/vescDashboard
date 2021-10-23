package com.example.vescdatalogger;

import android.bluetooth.le.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ScanResultAdapter extends RecyclerView.Adapter<ScanResultAdapter.ViewHolder> {

    private List<ScanResult> items;
    //add an onclicklistener

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView device_name;
        public TextView mac_address;
        public TextView signal_strength;

        public ViewHolder(View view) {
            super(view);

            device_name = (TextView) view.findViewById(R.id.device_name);
            mac_address = (TextView) view.findViewById(R.id.mac_address);
            signal_strength = (TextView) view.findViewById(R.id.signal_strength);
        }

        public void bind(ScanResult result) {
            if (result.getDevice().getName() == null || result.getDevice().getName().isEmpty()) {
                device_name.setText("Unknown");
            } else device_name.setText(result.getDevice().getName());
            mac_address.setText(result.getDevice().getAddress());
            String signalStrengthString = result.getRssi() + " dBM";
            signal_strength.setText(signalStrengthString);
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

    public ScanResultAdapter(List<ScanResult> results) {
        items = results;
    }
}
