package dotwaves.androidradiostreamingapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckNetwork {
    private static ConnectivityManager cm;
    private static NetworkInfo info, wifi, mobile;

    // public static boolean isNetworkAvailable(Context context) {
    //     cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
    //      info = cm.getActiveNetworkInfo();
    //     wifi = cm.getNetworkInfo(cm.TYPE_WIFI);
    //      mobile = cm.getNetworkInfo(cm.TYPE_MOBILE);

    //     if (info != null && info.isConnectedOrConnecting()
    //                 || wifi != null && wifi.isConnectedOrConnecting()
    //               || mobile != null && mobile.isConnectedOrConnecting()) {
    //         return true;
    //     } else return false;
    //   }
//}

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
        } else {
            return false;
        }

    }
}