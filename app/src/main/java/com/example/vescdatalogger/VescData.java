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
    /*public int getTest() {
        return test;
    }*/
    public void addMessage(Message newMessage) {
        messageQueue.add(newMessage);
        Log.i("messagequeue", "added a message");
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

    private static VescData thisVescData;

    public static VescData get() {
        if (thisVescData == null) {
            thisVescData = new VescData();
        }
        return thisVescData;
    }
}
