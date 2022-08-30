package com.example.vescdatalogger;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class Message {

    byte packetID;
    float RPM;
    float batteryVoltage;

    Date timestamp;

    public Message(byte[] bytes) {
        batteryVoltage = float16(bytes, 10, 29);
        Log.i("voltage", String.valueOf(batteryVoltage));
        RPM = float32(bytes, 1, 25);
        Log.i("RPM", String.valueOf(RPM));

    }

    public Date getTimestamp() {
        return timestamp;
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