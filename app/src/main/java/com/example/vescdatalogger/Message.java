package com.example.vescdatalogger;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class Message {

    byte packetID;
    float RPM;
    float batteryVoltage;
    byte[] previousglobalbytes = new byte[12];
    byte[] globalbytes = new byte[12];

    Date timestamp;
    int index = 0;

    public Message() {


//        batteryVoltage = float16(bytes, 10, 29);
//        Log.i("voltage", String.valueOf(batteryVoltage));
//        RPM = float32(bytes, 1, 25);
//        Log.i("RPM", String.valueOf(RPM));

    }
    public void addBytes(byte[] bytes, int length) {
        if (bytes[0] == 2) {
            addNew(bytes, length);
        } else {
            addOld(bytes, length);
        }

    }
    public void addNew(byte[] bytes, int length) {
        for (int i = 0; i < globalbytes.length; i++) {
            previousglobalbytes[i] = globalbytes[i];
        }
        printGoodMessage();
        for (int i = 0; i < length; i++) {
            globalbytes[i] = bytes[i];
        }
        index = length;
    }
    public void addOld(byte[] bytes, int length) {
        for (int i = 0; i < length; i++) {
            globalbytes[i + index] = bytes[i];
        }
        index = length + index;
    }
    public Date getTimestamp() {
        return timestamp;
    }
    public void printGoodMessage() {
        for (byte element : globalbytes) {
            Log.i("Data_Message", element + "\n");
        }
    }
    public float getParameter(String parameterName) {
        switch(parameterName) {
            case "Battery Voltage":
                return batteryVoltage;
            case "ERPM":
                return RPM;
            default:
                return 0;
        }
    }

    private static float float16(byte[] bytes, float scale, int index) {
        int firstByte = ((bytes[index] << 8) & 0x0000ff00);
        int secondByte = (bytes[index + 1] & 0x000000ff);
        int res = firstByte | secondByte;
        return (float) res / scale;
    }
    private static float float32(byte[] bytes, float scale, int index) {
        int firstByte = (bytes[index] << 24 & 0xff000000);
        int secondByte = ((bytes[index + 1] << 16) & 0x00ff0000);
        int thirdByte = ((bytes[index + 2] << 8) & 0x0000ff00);
        int forthByte = ((bytes[index + 3]) & 0x000000ff);
        int res = firstByte | secondByte | thirdByte | forthByte;
        return (float) res / scale;
    }
    private static int int16(byte[] bytes, int index) {
        int firstByte = ((bytes[index] << 8) & 0x0000ff00);
        int secondByte = (bytes[index + 1] & 0x000000ff);
        int res = firstByte | secondByte;
        return res;
    }
    private static int int32(byte[] bytes, int index) {
        int firstByte = (bytes[index] << 24 & 0xff000000);
        int secondByte = ((bytes[index + 1] << 16) & 0x00ff0000);
        int thirdByte = ((bytes[index + 2] << 8) & 0x0000ff00);
        int forthByte = ((bytes[index + 3]) & 0x000000ff);
        int res = firstByte | secondByte | thirdByte | forthByte;
        return res;
    }

}