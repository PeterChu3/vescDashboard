package com.example.vescdatalogger;

import android.util.Log;

import com.example.vescdatalogger.ui.main.Message;

import java.util.LinkedList;
import java.util.Queue;

public class VescData {
    //private int test;

    private LinkedList<Message> messageQueue = new LinkedList<>();

    private VescData(){
        //test = 1;
    }

    public float minBatteryCurrent;
    public float maxBatteryCurrent;
    public float avgBatteryCurrent;
    public float minRPM;
    public float maxRPM;
    public float avgRPM;
    public float minMosfetTemp;
    public float maxMosfetTemp;
    public float avgMosfetTemp;

    /*public int getTest() {
        return test;
    }*/
    public void addMessage(Message newMessage) {
        int oldSize = messageQueue.size();
        messageQueue.add(newMessage);
        int newSize = messageQueue.size();
        Log.i("messagequeue", "added a message");
        float avgCalc = 0;
        float newBatteryCurrent = newMessage.getParameter("Battery Current");
        if (newBatteryCurrent < minBatteryCurrent || oldSize == 0) minBatteryCurrent = newBatteryCurrent;
        else if (newBatteryCurrent > maxBatteryCurrent || oldSize == 0) maxBatteryCurrent = newBatteryCurrent;
        avgCalc = ((avgBatteryCurrent * oldSize) + newBatteryCurrent) / newSize;
        avgBatteryCurrent = avgCalc;
        float newRPM = newMessage.getParameter("ERPM");
        if (newRPM < minRPM || oldSize == 0) minRPM = newRPM;
        else if (newBatteryCurrent > maxRPM || oldSize == 0) maxRPM = newRPM;
        avgCalc = ((avgRPM * oldSize) + newRPM) / newSize;
        avgRPM = avgCalc;
        float newMosfetTemp = newMessage.getParameter("MOSFET Temp");
        avgCalc = ((avgMosfetTemp * oldSize) + newMosfetTemp) / newSize;
        if (newMosfetTemp < minMosfetTemp || oldSize == 0) minMosfetTemp = newMosfetTemp;
        else if (newMosfetTemp > maxMosfetTemp || oldSize == 0) maxMosfetTemp = newMosfetTemp;
        avgMosfetTemp = avgCalc;
    }

    public Message getRecent() {
        return messageQueue.peekLast();
    }

    public void removeMessage() {
        messageQueue.remove();
    }

    public int queueSize() {
        return messageQueue.size();
    }

    public Message at(int i){return messageQueue.get(i);}

    private static VescData thisVescData;

    public static VescData get() {
        if (thisVescData == null) {
            thisVescData = new VescData();
        }
        return thisVescData;
    }
}
