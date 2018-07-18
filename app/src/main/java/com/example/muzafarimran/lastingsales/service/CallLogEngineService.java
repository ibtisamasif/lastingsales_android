package com.example.muzafarimran.lastingsales.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;
import com.example.muzafarimran.lastingsales.utilscallprocessing.CallProcessor;
import com.example.muzafarimran.lastingsales.utilscallprocessing.CallTypeManager;


import java.util.Date;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class CallLogEngineService extends Service {

    public static final String TAG = "TheCallLogEngine";
//    private static final String TAG = "AppInitializationTest";

    public static final String SUB_ID = "subscription";

    private Context mContext;
    private boolean reRun = true;


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mContext = getApplicationContext();
        Log.e(TAG, "TheCallLogEngine doInBackground:");
        try {
            Thread.sleep(1000); // Delay is important as android might not have saved new call in call logs yet.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CallLogFunc();

//        TheCallLogEngine theCallLogEngine=new TheCallLogEngine(getApplicationContext());
//        theCallLogEngine.execute();


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void CallLogFunc() {

        List<LSCall> ls = LSCall.listAll(LSCall.class);
        List<LSInquiry> lsInquiries = LSInquiry.listAll(LSInquiry.class);



        boolean showNotification = false, showDialog = false;

        String latestCallQuery;
        Cursor managedCursor;

        List<LSCall> list = LSCall.listAll(LSCall.class);


        LSCall myCall = LSCall.getCallHavingLatestCallLogId();
        if (myCall != null) {
            Log.d(TAG, "getLatestCallLogId: " + myCall.getCallLogId());
            latestCallQuery = "_id >= " + myCall.getCallLogId();
            managedCursor = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, latestCallQuery, null, "date DESC");
        } else {
            Log.d(TAG, "getLatestCallLogId: is NULL");
            latestCallQuery = null;
            managedCursor = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, latestCallQuery, null, "date DESC limit 10");
        }
//        Cursor managedCursor = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,latestCallQuery , null, "date DESC limit 10");
//        Cursor managedCursor = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, "_id >= 1" , null, "date DESC limit 100");
//        Cursor managedCursor = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, "_id = " + LSCall.getCallHavingLatestCallLogId().getCallLogId() , null, "date DESC limit 10");

        try {

            Log.e(TAG, "CallLogFunc: managedCursor: " + Arrays.toString(managedCursor.getColumnNames()));

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
                    Log.d(TAG, "***************** CallLogFunc: Cursor is at First Now ***************");
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

                Log.d("date", callDate);

                Date callDayTime = new Date(Long.valueOf(callDate));


                Date curDate = java.util.Calendar.getInstance().getTime();


                Log.d("get call minutes", String.valueOf(callDayTime.getMinutes()));
                Log.d("get cur minutes", String.valueOf(curDate.getMinutes()));

                long sub = curDate.getMinutes() - callDayTime.getMinutes();

                if (curDate.getDay() - callDayTime.getDay() == 0) {
                    if (sub > 10) {
                        Log.d("greater than 10", "dateobj");
                        showDialog = false;
                    } else {
                        Log.d("not greater than 10", "dateobj");
                        showDialog = true;

                    }
                }


                String callDuration = managedCursor.getString(duration);

//                String callAccountId = managedCursor.getString(accountId);
//                String callSubs = managedCursor.getString(subs);

                if (LSCall.ifExist(callId)) {
                    Log.d(TAG, "ID: " + callId);
                    Log.d(TAG, "Exists: ");
                    reRun = false;
                    continue;
                } else {
//                    Log.d(TAG, "SUBSCRIBER_ID: " + callAccountId);
//                    Log.d(TAG, "SUBSCRIBER: " + callSubs);

                    Log.d(TAG, "CallId: " + callId);
                    Log.d(TAG, "CallNumber: " + callNumber);
                    Log.d(TAG, "CallName: " + callName);
                    Log.d(TAG, "callType: " + callType);
                    Log.d(TAG, "callBeginTimeLong: " + callDate);
                    Log.d(TAG, "callDate: " + PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(Long.parseLong(callDate)));
                    Log.d(TAG, "callDayTime: " + callDayTime);
                    Log.d(TAG, "callDuration: " + callDuration);

                    String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(mContext, callNumber);
                    if (internationalNumber == null) {
                        continue;
                    }
                    LSCall tempCall = new LSCall();
                    tempCall.setCallLogId(callId);
                    tempCall.setContactNumber(internationalNumber);
                    tempCall.setContactName(callName);
                    tempCall.setBeginTime(Long.parseLong(callDate));
                    tempCall.setDuration(Long.parseLong(callDuration));
                    tempCall.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);

                    tempCall.setType(CallTypeManager.getCallType(callType, callDuration));

                  /*  if (callType.equals("1") && tempCall.getDuration() > 0L) {           //Incoming

                        tempCall.setType(LSCall.CALL_TYPE_INCOMING);

                    } else if (callType.equals("2") && tempCall.getDuration() == 0L) {  //Outgoing UnAnswered
                        tempCall.setType(LSCall.CALL_TYPE_UNANSWERED);

                    } else if (callType.equals("2")) {      //Outgoing Answered
                        tempCall.setType(LSCall.CALL_TYPE_OUTGOING);

                    } else if (callType.equals("3")) {      //Missed
                        tempCall.setType(LSCall.CALL_TYPE_MISSED);

                    } else if (callType.equals("5") || callType.equals("1") || callType.equals("10") && tempCall.getDuration() == 0L) {        // Incoming Rejected
                        tempCall.setType(LSCall.CALL_TYPE_REJECTED);
                    }*/
                    try {
                        CallProcessor.Process(mContext, tempCall, showNotification,showDialog);
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


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
