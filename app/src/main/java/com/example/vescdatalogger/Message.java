package com.example.vescdatalogger;

import android.util.Log;

public class Message {

    public static void parse( byte[] data) {
        Log.i("Data_Parse", data[7] + "\n");
        Log.i("Data_Parse", data[8] + "\n");

    }
}
