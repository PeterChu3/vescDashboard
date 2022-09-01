package com.example.vescdatalogger;
import android.util.Log;
public class Message {

    float RPM;
    float batteryVoltage;
    byte[] previousglobalbytes = new byte[24];
    byte[] globalbytes = new byte[24];

    int index = 0;
    public boolean isConnected = false;
    public Message() {
    }
    public void addBytes(byte[] bytes, int length) {
        try {
            if (bytes[0] == 2) {
                addNew(bytes, length);
            } else {
                addOld(bytes, length);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            previousglobalbytes = new byte[24];
            globalbytes = new byte[24];
            index = 0;
        }

    }
    public void printSpeed() {
        RPM = float32(previousglobalbytes, 1,7);
        Log.i("Speed ", String.valueOf(RPM));
    }
    public void printBattery() {
        batteryVoltage = float16(previousglobalbytes, 10,11);
        Log.i("Battery Voltage ", String.valueOf(batteryVoltage));
    }
    public float getSpeed() {
        batteryVoltage = float32(previousglobalbytes, 1,7);
        return batteryVoltage;
    }
    public float getBattery() {
        RPM = float16(previousglobalbytes, 10,11);
        return RPM;
    }
    public void addNew(byte[] bytes, int length) {
        for (int i = 0; i < globalbytes.length; i++) {
            previousglobalbytes[i] = globalbytes[i];
        }
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
    public void printGoodMessage() {
        for (byte element : globalbytes) {
            Log.i("Data_Message", element + "\n");
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
    public static Message thisMessage;

    public static Message get() {
        if (thisMessage == null) {
            thisMessage = new Message();
        }
        return thisMessage;
    }

}