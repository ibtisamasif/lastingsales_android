package com.example.muzafarimran.lastingsales.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

/**
 * Created by ahmad.
 */
public class CommunicationManager
{




    public static boolean isNetworkAvailable(Context _context)
    {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }





/*
    public static String getDevicePingAddress()
    {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http");
        builder.authority(TotemConstants.CON_BASE_ADDRESS);
        builder.appendPath("mobipak");
        builder.appendPath("public");
        builder.appendPath("api");
        builder.appendPath(TotemConstants.CON_DEVICE_PING_END_LINK);
        return builder.build().toString();
    }

*/




    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}
