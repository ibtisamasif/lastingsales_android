package com.example.muzafarimran.lastingsales.helper;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Hashtable;
import java.util.TimeZone;

public class CalendarHelper {
    public static final String TAG = "CalendarHelper";

    public static void MakeNewCalendarEntry(Activity context, String title, String description,
                                            String location, long startTime, long endTime,
                                            boolean allDay, boolean hasAlarm, int calendarId,
                                            int selectedReminderValue) {

        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startTime);
        values.put(CalendarContract.Events.DTEND, endTime);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        values.put(CalendarContract.Events.STATUS, CalendarContract.Events.STATUS_CONFIRMED);
        if (allDay) {
            values.put(CalendarContract.Events.ALL_DAY, true);
        }

        if (hasAlarm) {
            values.put(CalendarContract.Events.HAS_ALARM, true);
        }
        //Get current timezone
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        Log.i(TAG, "Timezone retrieved=>" + TimeZone.getDefault().getID());
        try {
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            Log.d(TAG, "Uri returned=>" + uri.toString());
            // get the event ID that is the last element in the Uri
            long eventID = Long.parseLong(uri.getLastPathSegment());

            if (hasAlarm) {
                ContentValues reminders = new ContentValues();
                reminders.put(CalendarContract.Reminders.EVENT_ID, eventID);
                Toast.makeText(context, "update available", Toast.LENGTH_SHORT).show(); //TAG
                reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                reminders.put(CalendarContract.Reminders.MINUTES, selectedReminderValue);

                Uri uri2 = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminders);
                Log.d(TAG, "MakeNewCalendarEntry: SUCCESS");
            }
        } catch (Exception e) {
            Log.d(TAG, "permission denied: " + e);
        }
    }

    public static Hashtable listCalendarId(Context c) {
        if (haveCalendarReadWritePermissions((Activity) c)) {
            String projection[] = {"_id", "calendar_displayName"};
            Uri calendars;
            calendars = Uri.parse("content://com.android.calendar/calendars");
            ContentResolver contentResolver = c.getContentResolver();
            Cursor managedCursor = contentResolver.query(calendars, projection, null, null, null);

            if (managedCursor.moveToFirst()) {
                String calName;
                String calID;
                int cont = 0;
                int nameCol = managedCursor.getColumnIndex(projection[1]);
                int idCol = managedCursor.getColumnIndex(projection[0]);
                Hashtable<String, String> calendarIdTable = new Hashtable<>();
                do {
                    calName = managedCursor.getString(nameCol);
                    calID = managedCursor.getString(idCol);
                    Log.v(TAG, "CalendarName:" + calName + " ,id:" + calID);
                    calendarIdTable.put(calName, calID);
                    cont++;
                } while (managedCursor.moveToNext());
                managedCursor.close();
                return calendarIdTable;
            }
        }

        return null;

    }

    public static boolean haveCalendarReadWritePermissions(Activity caller) {
        int permissionCheck = ContextCompat.checkSelfPermission(caller,
                Manifest.permission.READ_CALENDAR);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            permissionCheck = ContextCompat.checkSelfPermission(caller,
                    Manifest.permission.WRITE_CALENDAR);

            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }
}