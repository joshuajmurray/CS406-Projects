package com.hfad.throttlemonitor;

import java.io.UnsupportedEncodingException;

public class NMEA_CKSM {
    static boolean CKSM = false;

    public static String calcCheckSum(String toDecode) {
        int calcdcksm = 0;
        int stringSize = toDecode.length();
        for (int i = 0; i < stringSize; i++) {
            calcdcksm ^= toDecode.charAt(i);//XOR CKSM
        }
        return String.format("%02d", calcdcksm);//pads with a 0 if single digit
    }

    public static int calcCheckSum(int input) {
        int calcdcksm = 0;
        String toDecode = Integer.toString(input);
        int stringSize = toDecode.length();

        for (int i = 0; i < stringSize; i++) {
            calcdcksm ^= toDecode.charAt(i);//XOR CKSM
        }
        return calcdcksm;
    }

    public static int calcCheckSum(byte[] input) {
        int calcdcksm = 0;
        StringBuilder toDecode = new StringBuilder();

        for (int i = 0; i < input.length; i++){
            if(input[i+1] == 0x0D){//look ahead for termination so cksm isn't included
                break;
            }
            toDecode.append(Byte.toString(input[i]));
        }

        int stringSize = toDecode.length();

        for (int i = 0; i < stringSize; i++) {
            calcdcksm ^= toDecode.charAt(i);//XOR CKSM
        }
        return calcdcksm;
    }

    public static boolean checkCKSM(String toDecode, int checksum) {
        int calcdcksm = 0;
        int stringSize = toDecode.length();
        CKSM = false;

        if (calcdcksm == checksum) {
            CKSM = true;
        } else {
            CKSM = false;
        }
        return CKSM;
    }
}
