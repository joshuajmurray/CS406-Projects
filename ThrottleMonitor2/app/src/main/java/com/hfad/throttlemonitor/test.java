package com.hfad.throttlemonitor;

import android.content.Intent;

import java.util.Arrays;
import java.util.Random;

public class test {
    private byte setup;
    private byte blipMs;
    private byte delayMs;
    private byte clutchType;
    private byte brakeType;
    private byte status;

    private static final String GET = "01";
    private static final String SET = "02";
    private static final String STATUS = "03";

    NMEA_CKSM n = new NMEA_CKSM();
    Random rand = new Random();

    public String sim(String req) {
        String[] received = req.split(":");//first element is data second is cksm
        String[] msgBody;
        if(received[0].contains(",")) {
            msgBody = received[0].split(",");//first element is command
        }
        else {
            msgBody = received;
        }
        String command = msgBody[0];
        String cksm = received[1].trim();//could error on no return!
        String calcdCksm = n.calcCheckSum(received[0]).trim();
        StringBuilder reply = new StringBuilder();

        if(cksm.equals(calcdCksm)) {
            switch (command) {
                case GET://build reply here
                    reply.append("00");//Status
                    reply.append(",");
                    reply.append(rand.nextInt(100-0) + 0);//blip
                    reply.append(",");
                    reply.append(rand.nextInt(100-0) + 0);//delay
                    reply.append(",");
                    reply.append(String.format("%02d",rand.nextInt(2-0) + 0));//clutchType
                    reply.append(",");
                    reply.append(String.format("%02d",rand.nextInt(2-0) + 0));//brakeType
                    reply.append(":");
                    String[] getReply = reply.toString().split(":");//first element is data second is cksm location
                    reply.append(n.calcCheckSum(getReply[0]));//CKSM
                    break;
                case SET:
                    reply.append(String.format("%02d",rand.nextInt(2-0) + 0));//Status random ok
                    reply.append(":");
                    String[] setReply = reply.toString().split(":");//first element is data second is cksm location
                    reply.append(n.calcCheckSum(setReply[0]));//CKSM
                    break;
                case STATUS:
                    reply.append(rand.nextInt(8-0) + 0);//clutch,brake,blip status (bit mapped) 00000,clutch,brake,blip
                    reply.append(":");
                    String[] statusReply = reply.toString().split(":");//first element is data second is cksm location
                    reply.append(n.calcCheckSum(statusReply[0]));//CKSM
                    break;
                default:
                    break;
            }
        }
        return reply.toString();
    }

    private int cksmLocation(String command){
        int loc = 0;
        switch (command) {
            case GET:
                loc = 1;
                break;
            case SET:
                loc = 5;
                break;
            case STATUS:
                loc = 1;
                break;
            default:
                break;
        }
        return loc;
    }
}
