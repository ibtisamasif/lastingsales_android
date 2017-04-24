package com.example.muzafarimran.lastingsales.utilscallprocessing;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.CallLog;
import android.util.Log;

import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

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
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CallLogFunc();
        return null;
    }

    public void CallLogFunc() {
        boolean showNotification = false;
        Cursor managedCursor = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, "date DESC limit 10");
        try {
            int id = managedCursor.getColumnIndex(CallLog.Calls._ID);
            int numbers = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);

            managedCursor.moveToLast();
            do {
                if(managedCursor.isFirst()){
                    Log.d(TAG, "CallLogFunc: Cursor is at First Now");
                    showNotification = true;
                }
                String callId = managedCursor.getString(id); // TODO crash here if call log is empty since exception is handled so only execution is disturbed
                String callNumber = managedCursor.getString(numbers);
                String callName = managedCursor.getString(name);
                String callType = managedCursor.getString(type);
                String callDate = managedCursor.getString(date);
                Date callDayTime = new Date(Long.valueOf(callDate));
                String callDuration = managedCursor.getString(duration);

                if (LSCall.ifExist(callId)) {
                    Log.d(TAG, "Exists: ");
                    continue;
                } else {
                    Log.d(TAG, "CallId: " + callId);
                    Log.d(TAG, "CallNumber: " + callNumber);
                    Log.d(TAG, "CallName: " + callName);
                    Log.d(TAG, "callType: " + callType);
                    Log.d("LSTime", "callBeginTimeLong: " + callDate);
                    Log.d(TAG, "callDate: " + PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(Long.parseLong(callDate)));
                    Log.d(TAG, "callDayTime: " + callDayTime);
                    Log.d(TAG, "callDuration: " + callDuration);

                    String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(callNumber);
                    LSCall tempCall = new LSCall();
                    tempCall.setCallLogId(callId);
                    tempCall.setContactNumber(internationalNumber);
                    tempCall.setContactName(callName);
                    tempCall.setBeginTime(Long.parseLong(callDate));
                    tempCall.setDuration(Long.parseLong(callDuration));

                    if (callType.equals("1")) {             //Incoming

                        tempCall.setType(LSCall.CALL_TYPE_INCOMING);

                    } else if (callType.equals("2") && tempCall.getDuration() == 0L) {  //Outgoing UnAnswered
                        tempCall.setType(LSCall.CALL_TYPE_UNANSWERED);

                    } else if (callType.equals("2")) {      //Outgoing Answered
                        tempCall.setType(LSCall.CALL_TYPE_OUTGOING);

                    } else if (callType.equals("3")) {      //Missed
                        tempCall.setType(LSCall.CALL_TYPE_MISSED);
                    }
                    else if (callType.equals("5")) {        // incoming Rejected
                        tempCall.setType(LSCall.CALL_TYPE_REJECTED);
                    }

                    CallProcessor.Process(mContext, tempCall, showNotification);
                }
            } while (managedCursor.moveToPrevious());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            managedCursor.close();
        }
    }
}