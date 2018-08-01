package com.example.muzafarimran.lastingsales.utils;

import android.net.Uri;
import android.util.Log;

import com.facebook.drawee.view.SimpleDraweeView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyDateTimeStamp {

    public static String getTime(String hour, String minute) {
        String aTime = hour + ':' + minute;
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
        Date date = null;
        try {
            date = displayFormat.parse(aTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String time = aTime;
        try {
            time = "" + parseFormat.format(date);
        } catch (Exception e) {
        }
        return time;
    }

    public static long dateTimeToLong(String dateTimeString) {
        Log.d("DateTest", "dateTimeToLong: " + dateTimeString);
        java.util.Date dateStart = null;
        long dateTimeLong = 0;
        try {
            dateTimeLong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTimeString).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTimeLong;
    }

    public static void setFrescoImage(SimpleDraweeView imageView, String url) {
        Uri uri = Uri.parse(url);
        imageView.setImageURI(uri);
    }

    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
