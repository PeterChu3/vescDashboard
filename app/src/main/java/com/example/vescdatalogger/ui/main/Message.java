package com.example.vescdatalogger.ui.main;

/**
 * This is
 */
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
    float MotorCurrent;
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

    public Message(byte[] bytes) {
        //Assign values using methods below
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