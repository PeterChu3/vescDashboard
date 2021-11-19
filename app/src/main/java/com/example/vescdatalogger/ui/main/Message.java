package com.example.vescdatalogger.ui.main;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class Message {
    /*
    public static void main(String args[])
    {
        byte[] bytes = {(byte)0x00, (byte)0xFF, (byte)0x6F,(byte)0xFF,(byte)0xFF,(byte)0xFF};
        System.out.println(float16(bytes, 10, 0));
        System.out.println(float32(bytes, 10, 2));
        System.out.println(int16(bytes, 0));
        System.out.println(int32(bytes, 2));
    }*/
    byte packetID;
    float mosfetTemp;
    float motorTemp;
    float motorCurrent;
    float batteryCurrent;
    float Id;
    float Iq;
    float dutyCycle;
    float RPM;
    float batteryVoltage;
    float AhDischarged;
    float AhChaged;
    float WhDischarged;
    float WhCharged;
    int distance;
    int distanceABS;
    byte fault;
    float pidPosition;
    byte currentController;
    float mosTemp1;
    float mosTemp2;
    float mosTemp3;
    float vd;
    float vq;

    Date timestamp;

    public Message(byte[] bytes) {
        //Assign values using methods below
        timestamp = Calendar.getInstance().getTime();
        Log.i("timestamp", timestamp.toString());
        Log.i("voltage hex 29", String.valueOf(bytes[29]));
        Log.i("voltage hex 30", String.valueOf(bytes[30]));
        batteryVoltage = float16(bytes, 10, 29);
        Log.i("voltage", String.valueOf(batteryVoltage));
        mosfetTemp = float16(bytes, 10, 3);
        Log.i("mosfetTemp", String.valueOf(mosfetTemp));
        motorTemp = float16(bytes, 10, 5);
        motorCurrent = float32(bytes, 100, 7);
        batteryCurrent = float32(bytes, 100, 11);
        dutyCycle = float16(bytes, 1000, 23);
        RPM = float32(bytes, 1, 25);
    }

    public float getParameter(String parameterName) {
        switch(parameterName) {
            case "Battery Voltage":
                return batteryVoltage;
            case "MOSFET Temp":
                return mosfetTemp;
            case "Battery Current":
                return batteryCurrent;
            case "Motor Current":
                return motorCurrent;
            case "Duty Cycle":
                return dutyCycle;
            case "ERPM":
                return RPM;
            case "Motor Temp":
                return motorTemp;
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