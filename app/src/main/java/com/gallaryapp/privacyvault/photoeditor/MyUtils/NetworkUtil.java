package com.gallaryapp.privacyvault.photoeditor.MyUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

public class NetworkUtil {

    public static Boolean isNetworkConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkCapabilities networkCapabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
        if (networkCapabilities == null) {
            return false;
        } else {
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true;
            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true;
            } else {
                return false;
            }
        }
    }
}
