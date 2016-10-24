package com.passageway;

/**
 * Created by Jaxon on 10/24/2016.
 */

public class Utils {

    private Utils() {
    }

    public static String formatMAC(String pMac) {
        String upper = pMac.substring(2).toUpperCase();
        String ret = "";
        for (int i = 0; i < upper.length(); i++) {
            ret += upper.charAt(i);
            if (i % 2 == 1 && i != upper.length() - 1) {
                ret += ":";
            }
        }
        return ret;
    }
}
