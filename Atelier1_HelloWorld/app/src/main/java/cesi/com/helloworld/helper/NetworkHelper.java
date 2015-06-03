package cesi.com.helloworld.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by sca on 29/05/15.
 */
public class NetworkHelper {


    public static boolean isInternetAvailable(Context context) {
        try {
            ConnectivityManager cm
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        } catch (Exception e) {
            Log.e("HelloWorld", "Error on checking internet:", e);

        }
        //default allowed to access internet
        return true;
    }
}
