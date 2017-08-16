package com.hfad.throttlemonitor;

public class NMEA_CKSM {
    static boolean CKSM = false;

    public static String calcCheckSum(String toDecode) {
        int calcdcksm = 0;
        int stringSize = toDecode.length();

        for (int i = 0; i < stringSize; i++) {
            calcdcksm ^= toDecode.charAt(i);//XOR CKSM
        }
        return Integer.toHexString(calcdcksm);
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
