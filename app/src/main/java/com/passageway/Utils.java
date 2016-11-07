package com.passageway;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

class Utils {

    private Utils() {
    }

    static String formatMAC(String pMac) {
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

    static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
