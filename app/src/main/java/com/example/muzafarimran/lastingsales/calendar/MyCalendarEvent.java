package com.example.muzafarimran.lastingsales.calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.CalendarContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyCalendarEvent {

    public static void setGoogleEvent(Activity activity, String startDateTime, String endDateTime,
                                      String title, String description, String location, int calendarID) {

        java.util.Date dateStart = null;
        java.util.Date dateEnd = null;

        try {
            dateStart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDateTime);
            dateEnd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long startTime = dateStart.getTime();
        long endTime = dateEnd.getTime();

        if (android.os.Build.VERSION.SDK_INT < 23){
//            IcaCalendarHelper.initActivityObj(activity);
//            IcaCalendarHelper.IcsMakeNewCalendarEntry(title, description, location,
//                    startTime, endTime, 0, 1, ph, 1);
            CalendarHelper.MakeNewCalendarEntry(activity,title, description, location,
                    startTime, endTime, false, true, calendarID, 0);
        }else {
//            CalendarHelper.requestCalendarReadWritePermission(activity);
            CalendarHelper.listCalendarId(activity);
            CalendarHelper.MakeNewCalendarEntry(activity,title, description, location,
                    startTime, endTime, false, true, calendarID, 0);
        }
    }

    public static void calendarFunction(Context context) {
        Calendar cal = Calendar.getInstance();
        if (Build.VERSION.SDK_INT >= 14) {
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getTimeInMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cal.getTimeInMillis())
                    .putExtra(CalendarContract.Events.TITLE, "LastingSales Follow-up")
                    .putExtra(CalendarContract.Events.DESCRIPTION, "Integration of google calendar with lastingSales android app")
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, "Arfa Tower")
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                    .putExtra(Intent.EXTRA_EMAIL, "mannan541@live.com");
            context.startActivity(intent);
        }
        else {
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra("beginTime", cal.getTimeInMillis());
            intent.putExtra("allDay", true);
            intent.putExtra("rrule", "FREQ=YEARLY");
            intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
            intent.putExtra("title", "LastingSales Follow-up");
            context.startActivity(intent);
        }
    }

}
