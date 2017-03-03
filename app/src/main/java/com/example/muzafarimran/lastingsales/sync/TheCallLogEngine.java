package com.example.muzafarimran.lastingsales.sync;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.CallLog;
import android.util.Log;

import java.sql.Date;


/**
 * Created by ibtisam on 3/3/2017.
 */

public class TheCallLogEngine extends AsyncTask<Object, Void, Void> {
    public static final String TAG = "TheCallLogEngine";
    Context mContext;

    public TheCallLogEngine(Context context) {
        this.mContext = context;
    }

    @Override
    protected Void doInBackground(Object... objects) {

        CallLogFunc();
        return null;
    }

    public void CallLogFunc() {
        StringBuffer sb = new StringBuffer();

        Cursor managedCursor = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, "date DESC");
        int numbers = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int id = managedCursor.getColumnIndex(CallLog.Calls._ID);
        sb.append("Call Details :");

        Log.d(TAG, "ColumnNumber: " + numbers);

        managedCursor.moveToFirst();
//        while (managedCursor.isLast()) {
//            managedCursor.moveToFirst();
        String phNumber = managedCursor.getString(numbers);
        String namee = managedCursor.getString(name);
        String callType = managedCursor.getString(type);
        String callDate = managedCursor.getString(date);
        Date callDayTime = new Date(Long.valueOf(callDate));
        String callDuration = managedCursor.getString(duration);

        Log.d(TAG, "PhNumber: " + phNumber);
        Log.d(TAG, "namee: " + namee);
        Log.d(TAG, "callType: " + callType);
        Log.d(TAG, "callDate: " + callDate);
        Log.d(TAG, "callDayTime: " + callDayTime);
        Log.d(TAG, "callDuration: " + callDuration);

//            managedCursor.moveToNext();
//        }


    }
}