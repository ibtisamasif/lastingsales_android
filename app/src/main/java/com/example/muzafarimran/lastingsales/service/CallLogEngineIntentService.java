package com.example.muzafarimran.lastingsales.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.muzafarimran.lastingsales.SettingsManager;
import com.example.muzafarimran.lastingsales.app.SyncStatus;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;
import com.example.muzafarimran.lastingsales.utilscallprocessing.CallProcessor;
import com.example.muzafarimran.lastingsales.utilscallprocessing.CallTypeManager;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by amir & Modified by ibtisam on 7/29/2018.
 */

public class CallLogEngineIntentService extends IntentService {

    public static final String TAG = "CallLogEngineIntentServ";

    public static final String SUB_ID = "subscription";

    private Context mContext;
    private boolean reRun = true;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param //name Used to name the worker thread, important only for debugging.
     */
    public CallLogEngineIntentService() {
        super("CallLogEngineIntentService");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            Thread.sleep(1000); // Delay is important as android might not have saved new call in call logs yet.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mContext = getApplicationContext();
        CallLogFunc();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void CallLogFunc() {

        boolean showNotification = false;

        String latestCallQuery;
        Cursor managedCursor;

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

        try {

            Log.e(TAG, "CallLogFunc: managedCursor: " + Arrays.toString(managedCursor.getColumnNames()));

            int id = managedCursor.getColumnIndex(CallLog.Calls._ID);
            int numbers = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);

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
                Date callDayTime = new Date(Long.valueOf(callDate));
                String callDuration = managedCursor.getString(duration);

                if (LSCall.ifExist(callId)) {
                    Log.d(TAG, "ID: " + callId);
                    Log.d(TAG, "Exists: ");
                    reRun = false;
                    continue;
                } else {
                    Log.v(TAG, "CallId: " + callId);
                    Log.w(TAG, "CallNumber: " + callNumber);
                    Log.v(TAG, "CallName: " + callName);
                    Log.v(TAG, "callType: " + callType);
                    Log.v(TAG, "callBeginTimeLong: " + callDate);
                    Log.v(TAG, "callDate: " + PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(Long.parseLong(callDate)));
                    Log.v(TAG, "callDayTime: " + callDayTime);
                    Log.v(TAG, "callDuration: " + callDuration);

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

                    try {
                        CallProcessor.Process(mContext, tempCall, showNotification);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } while (managedCursor.moveToPrevious());
            if (new SettingsManager(mContext).getKeyStateIsCompanyPhone()) {
                if (reRun) {
                    CallLogFunc();
                }
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
