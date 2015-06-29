package cesi.com.tchatapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import cesi.com.tchatapp.R;

/**
 * Created by sca on 29/06/15.
 */
public class PreferenceHelper {

    private static String PREFS = "prefs";

    public static String LOGIN = "login";

    public static void setValue(final Context context, String key, String value){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getValue(final Context context, String key){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }
}
