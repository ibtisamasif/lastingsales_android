package com.example.muzafarimran.lastingsales.receivers;

/**
 * Created by ahmad on 08-Nov-16.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.telephony.TelephonyManager;

import com.example.muzafarimran.lastingsales.SessionManager;

import java.util.Date;

public abstract class CallReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = "CallReceiver";
    //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;  //because the passed incoming is only valid in ringing
    private SessionManager sessionManager;

    @Override
    public void onReceive(Context context, final Intent intent) {
//        callStartTime = new Date();
//        Log.d(TAG, "onReceive: Called");
//        Log.d("testlog", "CallReceiver onReceive(): Called");
//        VersionManager versionManager = new VersionManager(context);
//        if (!versionManager.runMigrations()) {
//            // if migration has failed
//            Toast.makeText(context, "Migration Failed", Toast.LENGTH_SHORT).show();
//        }
//        sessionManager = new SessionManager(context);
//        if (!sessionManager.isUserSignedIn()) {
//            return;
//        }
//
//        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
//        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
//            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
//        } else {
//            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
//            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
//            int state = 0;
//            if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
//                state = TelephonyManager.CALL_STATE_IDLE;
//            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
//                state = TelephonyManager.CALL_STATE_OFFHOOK;
//            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
//                state = TelephonyManager.CALL_STATE_RINGING;
//            }
//            onCallStateChanged(context, state, number, intent);
//        }
    }

    //Derived classes should override these to respond to specific events of interest
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
    }

    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
    }

    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end, Intent intent) {
    }

    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end, Intent intent) {
    }

    protected void onMissedCall(Context ctx, String number, Date start, Intent intent) {
    }

    //Deals with actual events
    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    public void onCallStateChanged(Context context, int state, String number, Intent intent) {
        if (lastState == state) {
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
//                callStartTime = new Date();
                savedNumber = number;
                onIncomingCallStarted(context, number, callStartTime);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;
//                    callStartTime = new Date();
                    onOutgoingCallStarted(context, savedNumber, callStartTime);
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    //Ring but no pickup-  a miss
                    onMissedCall(context, savedNumber, callStartTime, intent);
                } else if (isIncoming) {
                    onIncomingCallEnded(context, savedNumber, callStartTime, new Date(), intent);
                } else {
                    onOutgoingCallEnded(context, savedNumber, callStartTime, new Date(), intent);
                }
                break;
        }
        lastState = state;
    }
}