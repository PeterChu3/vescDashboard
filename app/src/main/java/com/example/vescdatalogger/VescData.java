package com.example.vescdatalogger;

import com.example.vescdatalogger.ui.main.Message;

import java.util.LinkedList;
import java.util.Queue;

public class VescData {
    //private int test;

    private Queue<Message> messageQueue = new LinkedList<>();

    private VescData(){
        //test = 1;
    }
    /*public int getTest() {
        return test;
    }*/
    private static VescData thisVescData;
    public static VescData get() {
        if (thisVescData == null) {
            thisVescData = new VescData();
        }
        return thisVescData;
    }
}
