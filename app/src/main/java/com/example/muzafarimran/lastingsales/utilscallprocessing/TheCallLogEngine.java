package com.example.muzafarimran.lastingsales.utilscallprocessing;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.CallLog;
import android.util.Log;

import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

import java.sql.Date;

/**
 * Created by ibtisam on 3/3/2017.
 */

public class TheCallLogEngine extends AsyncTask<Object, Void, Void> {
    public static final String TAG = "TheCallLogEngine";
    public static final String SUB_ID = "subscription";

    Context mContext;
    private boolean reRun = true;

    public TheCallLogEngine(Context context) {
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d("testlog", "TheCallLogEngine onPreExecute:");
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

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.d("testlog", "TheCallLogEngine onPostExecute:");
        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(mContext);
        dataSenderAsync.run();
    }

    public void CallLogFunc() {
        boolean showNotification = false;

        String latestCallQuery;
        Cursor managedCursor;

        if (LSCall.getCallHavingLatestCallLogId() != null) {
            Log.d(TAG, "getLatestCallLogId: " + LSCall.getCallHavingLatestCallLogId().getCallLogId());
            latestCallQuery = "_id >= " + LSCall.getCallHavingLatestCallLogId().getCallLogId();
            managedCursor = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,latestCallQuery , null, "date DESC");
        }
        else {
            latestCallQuery = null;
            managedCursor = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,latestCallQuery , null, "date DESC limit 10");
        }
//        Cursor managedCursor = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,latestCallQuery , null, "date DESC limit 10");
//        Cursor managedCursor = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, "_id >= 1" , null, "date DESC limit 100");
//        Cursor managedCursor = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, "_id = " + LSCall.getCallHavingLatestCallLogId().getCallLogId() , null, "date DESC limit 10");

        try {
            int id = managedCursor.getColumnIndex(CallLog.Calls._ID);
            int numbers = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
//            int accountId = managedCursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID); //in OPPO sim1 = 1 & sim2 = 3
//            int subs = managedCursor.getColumnIndex(SUB_ID); //in OPPO sim1 = 1 & sim2 = 3 // in Huawei sim1 = 0 & sim2 = 1

            managedCursor.moveToLast();
            do {
                if (managedCursor.isFirst()) {
                    Log.d(TAG, "CallLogFunc: Cursor is at First Now");
                    showNotification = true;
                }
                Log.d(TAG, "CallLogFunc: Index: " + managedCursor.getPosition());
                if (managedCursor.getPosition() == -1) {
                    return;
                }
                String callId = managedCursor.getString(id);
                String callNumber = managedCursor.getString(numbers);
                String callName = managedCursor.getString(name);
                String callType = managedCursor.getString(type);
                String callDate = managedCursor.getString(date);
                Date callDayTime = new Date(Long.valueOf(callDate));
                String callDuration = managedCursor.getString(duration);
//                String callAccountId = managedCursor.getString(accountId);
//                String callSubs = managedCursor.getString(subs);

                if (LSCall.ifExist(callId)) {
                    Log.d(TAG, "ID: " + callId);
                    Log.d(TAG, "Exists: ");
                    reRun = false;
                    continue;
                } else {
                    Log.d(TAG, "CallId: " + callId);
                    Log.d(TAG, "CallNumber: " + callNumber);
                    Log.d(TAG, "CallName: " + callName);
                    Log.d(TAG, "callType: " + callType);
                    Log.d(TAG, "callBeginTimeLong: " + callDate);
                    Log.d(TAG, "callDate: " + PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(Long.parseLong(callDate)));
                    Log.d(TAG, "callDayTime: " + callDayTime);
                    Log.d(TAG, "callDuration: " + callDuration);
//                    Log.d(TAG, "callAccountId: " + callAccountId);
//                    Log.d(TAG, "callSubs: " + callSubs);

                    String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(mContext, callNumber);
                    LSCall tempCall = new LSCall();
                    tempCall.setCallLogId(callId);
                    tempCall.setContactNumber(internationalNumber);
                    tempCall.setContactName(callName);
                    tempCall.setBeginTime(Long.parseLong(callDate));
                    tempCall.setDuration(Long.parseLong(callDuration));

                    if (callType.equals("1") && tempCall.getDuration() > 0L) {           //Incoming

                        tempCall.setType(LSCall.CALL_TYPE_INCOMING);

                    } else if (callType.equals("2") && tempCall.getDuration() == 0L) {  //Outgoing UnAnswered
                        tempCall.setType(LSCall.CALL_TYPE_UNANSWERED);

                    } else if (callType.equals("2")) {      //Outgoing Answered
                        tempCall.setType(LSCall.CALL_TYPE_OUTGOING);

                    } else if (callType.equals("3")) {      //Missed
                        tempCall.setType(LSCall.CALL_TYPE_MISSED);

                    } else if (callType.equals("5") || callType.equals("1") && tempCall.getDuration() == 0L) {        // Incoming Rejected
                        tempCall.setType(LSCall.CALL_TYPE_REJECTED);
                    }
                    try {
                        CallProcessor.Process(mContext, tempCall, showNotification);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } while (managedCursor.moveToPrevious());
            if (reRun) {
                CallLogFunc();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (managedCursor != null) {
                managedCursor.close();
            }
        }
    }
}