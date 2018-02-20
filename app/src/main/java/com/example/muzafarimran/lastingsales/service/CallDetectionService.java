package com.example.muzafarimran.lastingsales.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.SettingsManager;
import com.example.muzafarimran.lastingsales.activities.NavigationBottomMainActivity;
import com.example.muzafarimran.lastingsales.chatheadbubble.FlyerBubbleHelper;
import com.example.muzafarimran.lastingsales.events.IncomingCallEventModel;
import com.example.muzafarimran.lastingsales.events.MissedCallEventModel;
import com.example.muzafarimran.lastingsales.events.OutgoingCallEventModel;
import com.example.muzafarimran.lastingsales.listeners.PostExecuteListener;
import com.example.muzafarimran.lastingsales.migration.VersionManager;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.receivers.CallReceiver;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;
import com.example.muzafarimran.lastingsales.utilscallprocessing.TheCallLogEngine;

import java.util.ArrayList;
import java.util.Date;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 7/12/2017.
 */

public class CallDetectionService extends Service {
    private static final String TAG = CallDetectionService.class.getSimpleName();
    private static final int NOTIFICATION_ID = -1;
    private BroadcastReceiver mReceiver;

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;  //because the passed incoming is only valid in ringing
    private SessionManager sessionManager;
    private static boolean isBubbleShown = false;
    private SettingsManager settingsManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        settingsManager = new SettingsManager(getApplicationContext());
        Log.i(TAG, "CallDetectionService onCreate()");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        filter.addAction("android.intent.action.PHONE_STATE");
        filter.addAction(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(receiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "CallDetectionService onStartCommand()");
        Log.d(TAG, "onStartCommand()");
        showForegroundNotification("Click to open");
//        Toast.makeText(getApplicationContext(),"LS Running", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "CallDetectionService onDestroy()");
        unregisterReceiver(receiver);
        sendBroadcast(new Intent("YouWillNeverKillMe"));
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "onTaskRemoved: ");
        //create a intent that you want to start again..
        Intent intent = new Intent(getApplicationContext(), CallDetectionService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 5000, pendingIntent);
        super.onTaskRemoved(rootIntent);
    }

    final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            callStartTime = new Date();
            Log.d(TAG, "onReceive: Called");
            VersionManager versionManager = new VersionManager(context);
            if (!versionManager.runMigrations()) {
                // if migration has failed
                Toast.makeText(context, "Migration Failed", Toast.LENGTH_SHORT).show();
            }
            sessionManager = new SessionManager(context);
            if (!sessionManager.isUserSignedIn()) {
                return;
            }

            //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
            if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
            } else {
                String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                int state = 0;
                if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    state = TelephonyManager.CALL_STATE_IDLE;
                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    state = TelephonyManager.CALL_STATE_OFFHOOK;
                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    state = TelephonyManager.CALL_STATE_RINGING;
                }
                onCallStateChanged(context, state, number, intent);
            }

//            String action = intent.getAction();
//            if(action.equals("android.provider.Telephony.SMS_RECEIVED")){
//                //action for sms received
//            }
//            else if(action.equals(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED)){
//                //action for phone state changed
//            }
        }
    };

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

    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
//        AddEditLeadServiceBubbleHelper.getInstance(ctx.getApplicationContext()).hide(); // remove old bubble
        sessionManager = new SessionManager(ctx);
        if (!sessionManager.isUserSignedIn()) {
            return;
        }
        if (!isBubbleShown) {
            isBubbleShown = true;
            checkShowCallPopupFlyer(ctx, number);
        }
        Log.d(TAG, "onIncomingCallStarted:");
    }

    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
//        AddEditLeadServiceBubbleHelper.getInstance(ctx.getApplicationContext()).hide(); // remove old bubble
        sessionManager = new SessionManager(ctx);
        if (!sessionManager.isUserSignedIn()) {
            return;
        }
        if (!isBubbleShown) {
            isBubbleShown = true;
            checkShowCallPopupFlyer(ctx, number);
        }
        Log.d(TAG, "onOutgoingCallStarted:");
    }

    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end, final Intent intent) {
        sessionManager = new SessionManager(ctx);
        if (!sessionManager.isUserSignedIn()) {
            return;
        }
        if (isBubbleShown) {
            endServiceAndCallPopupFlyer(ctx);
            isBubbleShown = false;
        }
        Log.d(TAG, "onIncomingCall() called with: ctx = [" + ctx + "], number = [" + number + "], setAlarm = [" + start + "]");
        final TheCallLogEngine theCallLogEngine = new TheCallLogEngine(ctx);
        theCallLogEngine.execute();
        IncomingCallEventModel InCallEvent = new IncomingCallEventModel(IncomingCallEventModel.CALL_TYPE_INCOMING);
        TinyBus inBus = TinyBus.from(ctx.getApplicationContext());
        inBus.post(InCallEvent);
        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(ctx);
        dataSenderAsync.setDataSenderOnPostExecuteListener(new PostExecuteListener() {
            @Override
            public void onPostExecuteListener() {
                Log.d(TAG, "onPostExecuteListener: I AM Listened");
                if (theCallLogEngine.getStatus() == AsyncTask.Status.FINISHED) {
                    Log.d(TAG, "TheCallLogEngine Completed: completeWakefulIntent");
                    CallReceiver.completeWakefulIntent(intent);
                }
            }
        });
        Log.d(TAG, "onIncomingCallEnded:");
    }

    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end, final Intent intent) {
        sessionManager = new SessionManager(ctx);
        if (!sessionManager.isUserSignedIn()) {
            return;
        }
        if (isBubbleShown) {
            endServiceAndCallPopupFlyer(ctx);
            isBubbleShown = false;
        }
        Log.d(TAG, "onOutgoingCall() called with: ctx = [" + ctx + "], number = [" + number + "], setAlarm = [" + start + "]");
        final TheCallLogEngine theCallLogEngine = new TheCallLogEngine(ctx);
        theCallLogEngine.execute();
        OutgoingCallEventModel outCallEvent = new OutgoingCallEventModel(OutgoingCallEventModel.CALL_TYPE_OUTGOING);
        TinyBus outBus = TinyBus.from(ctx.getApplicationContext());
        outBus.post(outCallEvent);
        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(ctx);
        dataSenderAsync.setDataSenderOnPostExecuteListener(new PostExecuteListener() {
            @Override
            public void onPostExecuteListener() {
                Log.d(TAG, "onPostExecuteListener: I AM Listened");
                if (theCallLogEngine.getStatus() == AsyncTask.Status.FINISHED) {
                    Log.d(TAG, "TheCallLogEngine Completed: completeWakefulIntent");
                    CallReceiver.completeWakefulIntent(intent);
                }
            }
        });
        Log.d(TAG, "onOutgoingCallEnded:");
    }

    protected void onMissedCall(Context ctx, String number, Date start, final Intent intent) {
        if (isBubbleShown) {
            endServiceAndCallPopupFlyer(ctx);
            isBubbleShown = false;
        }
        Log.d("MissedCallReceiver", "onMissedCall() called with: ctx = [" + ctx + "], number = [" + number + "], setAlarm = [" + start + "]");
        final TheCallLogEngine theCallLogEngine = new TheCallLogEngine(ctx);
        theCallLogEngine.execute();
        MissedCallEventModel mCallEvent = new MissedCallEventModel(MissedCallEventModel.CALL_TYPE_MISSED);
        TinyBus mBus = TinyBus.from(ctx.getApplicationContext());
        mBus.post(mCallEvent);
        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(ctx);
        dataSenderAsync.setDataSenderOnPostExecuteListener(new PostExecuteListener() {
            @Override
            public void onPostExecuteListener() {
                Log.d(TAG, "onPostExecuteListener: I AM Listened");
                if (theCallLogEngine.getStatus() == AsyncTask.Status.FINISHED) {
                    Log.d(TAG, "TheCallLogEngine Completed: completeWakefulIntent");
                    CallReceiver.completeWakefulIntent(intent);
                }
            }
        });
        Log.d(TAG, "onMissedCall: End Line");
    }

    public void checkShowCallPopupFlyer(Context ctx, String number) {
        Log.wtf(TAG, "checkShowCallPopupFlyer: ");
        if(settingsManager.getKeyStateFlyer()){
            String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(ctx, number);
            LSContact oneContact;
            oneContact = LSContact.getContactFromNumber(internationalNumber);
            ArrayList<LSNote> notesForContact = null;
            if (oneContact != null && !oneContact.getContactType().equals(LSContact.CONTACT_TYPE_IGNORED)) {
                notesForContact = (ArrayList<LSNote>) LSNote.getNotesByContactId(oneContact.getId());
                if (notesForContact != null && notesForContact.size() > 0) {
                    notesForContact.size();

                    FlyerBubbleHelper.getInstance(ctx).show(notesForContact.get(notesForContact.size() - 1).getId(), internationalNumber, oneContact);

                } else {
                    FlyerBubbleHelper.getInstance(ctx).show(internationalNumber);
                }
            } else if (oneContact == null){
                FlyerBubbleHelper.getInstance(ctx).show(internationalNumber);
            }
        }
    }

    private void endServiceAndCallPopupFlyer(Context ctx) {

        FlyerBubbleHelper.getInstance(ctx).hide();

//        Log.wtf(TAG, "endServiceAndCallPopupFlyer: ");
//        Intent intent = new Intent(ctx, AddEditLeadService.class);
//        ctx.stopService(intent);
    }

//    public void checkShowCallPopupOld(Context ctx, String name, String number) {
//        Log.wtf(TAG, "checkShowCallPopupNew: ");
//        String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(number);
////        String name = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(ctx, internationalNumber);
//        Intent intent = new Intent(ctx, AddEditLeadService.class);
//        intent.putExtra(TagNotificationDialogActivity.TAG_LAUNCH_MODE_CONTACT_TYPE, LSContact.CONTACT_TYPE_BUSINESS);
//        intent.putExtra(TagNotificationDialogActivity.TAG_LAUNCH_MODE_PHONE_NUMBER, internationalNumber);
//        intent.putExtra(TagNotificationDialogActivity.TAG_LAUNCH_MODE_CONTACT_NAME, name);
//        intent.putExtra(TagNotificationDialogActivity.TAG_LAUNCH_MODE_CONTACT_ID, ""); //backward compatibility
//        ctx.startService(intent);
//    }

    private void showForegroundNotification(String contentText) {
        // Create intent that will bring our app to the front, as if it was tapped in the app
        // launcher
        Intent showTaskIntent = new Intent(this, NavigationBottomMainActivity.class);
        showTaskIntent.setAction(Intent.ACTION_MAIN);
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, showTaskIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(this)
                .setContentIntent(contentIntent)
                .setContentTitle("LastingSales")
                .setTicker("LastingSales")
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_notification), 128, 80, false))
//                .setWhen(0)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MAX)
                .build();
        //actually run the notification
        startForeground(NOTIFICATION_ID, notification);
    }
}