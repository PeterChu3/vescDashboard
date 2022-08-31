package com.example.vescdatalogger;

public class MechSetup {
    public static Message thisSetup;

    int cellsSeries = 12;
    int polePairs = 7;
    int motorGear = 11;
    int wheelGear = 66;
    int wheelDiameter = 305;
    public MechSetup() {

    }
    public static Message get() {
        if (thisSetup == null) {
            thisSetup = new Message();
        }
        return thisSetup;
    }
}
