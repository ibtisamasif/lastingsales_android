package com.example.muzafarimran.lastingsales.receivers;

import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.FileObserver;
import android.os.PowerManager;
import android.util.Log;

import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.chatheadbubble.BubbleHelper;
import com.example.muzafarimran.lastingsales.events.IncomingCallEventModel;
import com.example.muzafarimran.lastingsales.events.MissedCallEventModel;
import com.example.muzafarimran.lastingsales.events.OutgoingCallEventModel;
import com.example.muzafarimran.lastingsales.listeners.PostExecuteListener;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.service.CallDetectionService;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;
import com.example.muzafarimran.lastingsales.utilscallprocessing.TheCallLogEngine;

import java.util.ArrayList;
import java.util.Date;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ahmad on 08-Nov-16.
 */

public class CallsStatesReceiver extends CallReceiver{
    public static final String OUTGOINGCALL_CONTACT_ID = "outgoing_contact_id";
    public static final String INCOMINGCALL_CONTACT_ID = "incoming_contact_id";
    public static final String OUTGOINGCALL_CONTACT_NOTE_ID = "outgoing_contact_note_id";
    public static final String INCOMINGCALL_CONTACT_NOTE_ID = "incoming_contact_note_id";
    private static final String TAG = "CallsStatesReceiver";
    public static String mAudio_FilePath;
    public static String mAudio_FolderName = "LastingSales";
    public static String mAudio_FileName = "";
    public static MediaRecorder recorder;
    private static boolean isRecordStarted = false;
    private static PowerManager powerManager = null;
    private static PowerManager.WakeLock wakeLock = null;
    private SessionManager sessionManager;
    private static boolean isBubbleShown = false;

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        sessionManager = new SessionManager(ctx);
        if (!sessionManager.isUserSignedIn()) {
            return;
        }
//        Toast.makeText(context, "Incoming call started", Toast.LENGTH_SHORT).show();
//        if (wakeLock == null) {
//            powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
//            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
//            wakeLock.acquire();
//            Log.d(TAG, "onIncomingCallStarted: Wakelock Aquired");
//        }
//        if (recorder != null && isRecordStarted) {
//            Log.d(TAG, "onIncomingCallStarted: RecorderStop");
//            recorder.stop();
//            isRecordStarted = false;
//        }
        if (!isBubbleShown) {
            isBubbleShown = true;
            checkShowCallPopupFlyer(ctx, number);
        }
//        String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(number);
//        LSContact personalContactCheck = LSContact.getContactFromNumber(internationalNumber);
//        if (personalContactCheck != null && personalContactCheck.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
//            String lsDirectory = Environment.getExternalStorageDirectory() + "/" + mAudio_FolderName;
//            pathFileObserver = new PathFileObserver(lsDirectory);
//            pathFileObserver.startWatching();
//            try {
//                String myDirectory = Environment.getExternalStorageDirectory() + "/" + mAudio_FolderName;
//                File sampleDir = new File(myDirectory.toString());
//                if (!sampleDir.exists()) {
//                    sampleDir.mkdirs();
//                }
//                mAudio_FileName = PhoneNumberAndCallUtils.generateUniqueFileName(mAudio_FileName);
//                mAudio_FilePath = sampleDir.getAbsolutePath() + "/" + mAudio_FileName + ".amr";
//                recorder = new MediaRecorder();
//                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
//                recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
//                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//                recorder.setOutputFile(sampleDir.getAbsolutePath() + "/" + mAudio_FileName + ".amr");
//                //recorder.setOutputFile("/storage/emulated/0/LastingSalesRecordings/abc.amr");
//                recorder.prepare();
//
//                if (recorder != null && !isRecordStarted) {
//                    recorder.start();
//                    isRecordStarted = true;
//                    Log.d(TAG, "onIncomingCallStarted: RecorderStart");
//                }
//            } catch (IllegalStateException | IOException e) {
//                e.printStackTrace();
//                Log.e(TAG, "error while preparing recording", e);
//                // You might want to inform the user too, with a Toast
//            }
//
////            Log.d(TAG, "onOutgoingCallStarted: File Location: " + mAudio_FilePath);
//
//            LSCallRecording tempRecording = new LSCallRecording();
////            tempRecording.setLocalIdOfCall("" + tempCall.getId());
//            Log.d(TAG, "onIncomingCallStarted: File Location1: " + mAudio_FilePath);
//            tempRecording.setContactNumber(internationalNumber);
//            tempRecording.setBeginTime(start.getTime());
//            tempRecording.setAudioPath("" + mAudio_FilePath);
//            tempRecording.setSyncStatus(SyncStatus.SYNC_STATUS_RECORDING_NOT_SYNCED);
//            tempRecording.save();
//            Log.d(TAG, "onIncomingCallStarted: RecordingBeginTime " + tempRecording.getBeginTime());
//        }
        Log.d("testlog", "onIncomingCallStarted:");
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        sessionManager = new SessionManager(ctx);
        if (!sessionManager.isUserSignedIn()) {
            return;
        }
//        Toast.makeText(context, "Outgoing call started", Toast.LENGTH_SHORT).show();
//        Log.d("LSTime", "onOutgoingCallStarted: CallReceiverLog BeginTime: " + start.getTime());
//        if (wakeLock == null) {
//            powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
//            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
//            wakeLock.acquire();
//            Log.d(TAG, "onOutgoingCallStarted: Wakelock Aquired");
//        }
//
//        if (recorder != null && isRecordStarted) {
//            Log.d(TAG, "onOutgoingCallStarted: RecorderStop");
//            recorder.stop();
//            isRecordStarted = false;
//        }
        if (!isBubbleShown) {
            isBubbleShown = true;
            checkShowCallPopupFlyer(ctx, number);
        }
//        String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(number);
//        LSContact personalContactCheck = LSContact.getContactFromNumber(internationalNumber);
//        if (personalContactCheck != null && personalContactCheck.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
//            String lsDirectory = Environment.getExternalStorageDirectory() + "/" + mAudio_FolderName;
//            pathFileObserver = new PathFileObserver(lsDirectory);
//            pathFileObserver.startWatching();
//
//            try {
//                String myDirectory = Environment.getExternalStorageDirectory() + "/" + mAudio_FolderName;
//                File sampleDir = new File(myDirectory.toString());
//                if (!sampleDir.exists()) {
//                    sampleDir.mkdirs();
//                }
//                mAudio_FileName = PhoneNumberAndCallUtils.generateUniqueFileName(mAudio_FileName);
//                mAudio_FilePath = sampleDir.getAbsolutePath() + "/" + mAudio_FileName + ".amr";
//                recorder = new MediaRecorder();
//                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
//                recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
//                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//                recorder.setOutputFile(sampleDir.getAbsolutePath() + "/" + mAudio_FileName + ".amr");
////                Log.d(TAG, "onOutgoingCallStarted: myDir: " + sampleDir.getAbsolutePath());
//                // recorder.setOutputFile("/storage/emulated/0/LastingSalesRecordings/abc.amr");
//                recorder.prepare();
//
//                if (recorder != null && !isRecordStarted) {
//                    recorder.start();
//                    isRecordStarted = true;
//                    Log.d(TAG, "onOutgoingCallStarted: RecorderStart");
//                }
//            } catch (IllegalStateException | IOException e) {
//                e.printStackTrace();
//                Log.e(TAG, "error while preparing recording", e);
//                // You might want to inform the user too, with a Toast
//            }
//
////            Log.d(TAG, "onOutgoingCallStarted: File Location: " + mAudio_FilePath);
//
//            LSCallRecording tempRecording = new LSCallRecording();
////            tempRecording.setLocalIdOfCall("" + tempCall.getId());
//            Log.d(TAG, "onOutgoingCallStarted: File Location1: " + mAudio_FilePath);
//            tempRecording.setContactNumber(internationalNumber);
//            tempRecording.setBeginTime(start.getTime());
//            tempRecording.setAudioPath("" + mAudio_FilePath);
//            tempRecording.setSyncStatus(SyncStatus.SYNC_STATUS_RECORDING_NOT_SYNCED);
//            tempRecording.save();
//            Log.d(TAG, "onOutgoingCallStarted: RecordingBeginTime " + tempRecording.getBeginTime());
//        }
        Log.d("testlog", "onOutgoingCallStarted:");
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end, final Intent intent) {
        sessionManager = new SessionManager(ctx);
        if (!sessionManager.isUserSignedIn()) {
            return;
        }
//        showTagNumberPopupIfNeeded(context, number);
        if (isBubbleShown) {
            endServiceAndCallPopupFlyer(ctx);
            isBubbleShown = false;
        }
//        String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(number);
//        LSContact personalContactCheck = LSContact.getContactFromNumber(internationalNumber);
//        Log.d(TAG, "onIncomingCallEnded: 0");
//        if (personalContactCheck == null || !personalContactCheck.getContactType().equals(LSContact.CONTACT_TYPE_IGNORED)) {
//            Log.d(TAG, "onIncomingCallEnded: 1");
//            LSCall tempCall = new LSCall();
//            tempCall.setContactNumber(internationalNumber);
//            tempCall.setType(LSCall.CALL_TYPE_INCOMING);
//            tempCall.setInquiryHandledState(LSCall.INQUIRY_HANDLED);
//            tempCall.setBeginTime(start.getTime());
//            tempCall.setAudioPath(mAudio_FilePath);
//            String phoneBookContactName = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(context, internationalNumber);
//            if (phoneBookContactName == null) {
//                tempCall.setContactName(null);
//            } else if (!phoneBookContactName.equals("")) {
//                tempCall.setContactName(phoneBookContactName);
//            }
//            long callDuration = PhoneNumberAndCallUtils.secondsFromStartAndEndDates(start, end);
//            tempCall.setDuration(callDuration);
//            LSContact contact = LSContact.getContactFromNumber(internationalNumber);
////      if contact is null that means contact is not already saved with this number
//            if (contact != null) {
//                tempCall.setContact(contact);
//            } else {
////            new untagged contact is created, saved, entered in call entry
//                LSContact tempContact = new LSContact();
//                tempContact.setContactType(LSContact.CONTACT_TYPE_UNLABELED);
//                tempContact.setPhoneOne(internationalNumber);
//                tempContact.setContactName(phoneBookContactName);
//                tempContact.save();
//                tempCall.setContact(tempContact);
//            }
//            tempCall.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
//            tempCall.save();
//            LSInquiry tempInquiry = LSInquiry.getPendingInquiryByNumberIfExists(internationalNumber);
//            if (tempInquiry != null && tempInquiry.getAverageResponseTime() <= 0) {
//                Calendar now = Calendar.getInstance();
//                tempInquiry.setAverageResponseTime(now.getTimeInMillis() - tempInquiry.getBeginTime());
//                tempInquiry.setStatus(LSInquiry.INQUIRY_STATUS_ATTENDED);
//                if (tempInquiry.getSyncStatus().equals(SyncStatus.SYNC_STATUS_INQUIRY_PENDING_SYNCED)) {
//                    tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_ATTENDED_NOT_SYNCED);
//                } else { // TODO USELESS REMOVE IT
//                    tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_PENDING_NOT_SYNCED);
//
//                }
//                tempInquiry.save();
//            }
//            LSCallRecording tempRecording = new LSCallRecording();
//            tempRecording.setLocalIdOfCall("" + tempCall.getId());
////            Log.d(TAG, "onIncomingCallEnded: File Location1: " + mAudio_FilePath);
//            tempRecording.setAudioPath("" + mAudio_FilePath);
//            tempRecording.setSyncStatus(SyncStatus.SYNC_STATUS_RECORDING_NOT_SYNCED);
//            tempRecording.save();
////            Log.d(TAG, "onIncomingCallEnded: File Location2: " + tempRecording.getAudioPath());
//            DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(context);
//            dataSenderAsync.run();
//            IncomingCallEventModel mCallEvent = new IncomingCallEventModel(IncomingCallEventModel.CALL_TYPE_INCOMING);
//            TinyBus bus = TinyBus.from(context.getApplicationContext());
//            bus.post(mCallEvent);
//        }
//        if (recorder != null && isRecordStarted) {
//            recorder.stop();
//            recorder.reset();
//            recorder.release();
//            isRecordStarted = false;
//            Log.d(TAG, "onIncomingCallEnded: RecorderStopped");
//        }
//
//        if (wakeLock != null && wakeLock.isHeld()) {
//            wakeLock.release();
//            wakeLock = null;
//            Log.d(TAG, "onIncomingCallStarted: Wakelock Released");
//        }
        Log.d(TAG, "onIncomingCall() called with: context = [" + ctx + "], number = [" + number + "], setAlarm = [" + start + "]");
//        TheCallLogEngine theCallLogEngine = new TheCallLogEngine(context);
//        theCallLogEngine.execute();
//        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(context);
//        dataSenderAsync.run();
//        IncomingCallEventModel mCallEvent = new IncomingCallEventModel(IncomingCallEventModel.CALL_TYPE_INCOMING);
//        TinyBus bus = TinyBus.from(context.getApplicationContext());
//        bus.post(mCallEvent);

        final TheCallLogEngine theCallLogEngine = new TheCallLogEngine(ctx);
        theCallLogEngine.execute();
        IncomingCallEventModel InCallEvent = new IncomingCallEventModel(IncomingCallEventModel.CALL_TYPE_INCOMING);
        TinyBus inBus = TinyBus.from(ctx.getApplicationContext());
        inBus.post(InCallEvent);
        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(ctx);
        dataSenderAsync.setDataSenderOnPostExecuteListener(new PostExecuteListener() {
            @Override
            public void onPostExecuteListener() {
                Log.d("testlog", "onPostExecuteListener: I AM Listened");
                if (theCallLogEngine.getStatus() == AsyncTask.Status.FINISHED) {
                    Log.d("testlog", "TheCallLogEngine Completed: completeWakefulIntent");
                    CallReceiver.completeWakefulIntent(intent);
                }
            }
        });
        Log.d("testlog", "onIncomingCallEnded:");
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end, final Intent intent) {
        sessionManager = new SessionManager(ctx);
        if (!sessionManager.isUserSignedIn()) {
            return;
        }
//        Toast.makeText(context, "Outgoing call Ended", Toast.LENGTH_SHORT).show();
//        showTagNumberPopupIfNeeded(context, number);
        if (isBubbleShown) {
            endServiceAndCallPopupFlyer(ctx);
            isBubbleShown = false;
        }

//        String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(number);
//        LSContact personalContactCheck = LSContact.getContactFromNumber(internationalNumber);
////        Log.d(TAG, "onOutgoingCallEnded: 0");
//        if (personalContactCheck == null || !personalContactCheck.getContactType().equals(LSContact.CONTACT_TYPE_IGNORED)) {
////            Log.d(TAG, "onOutgoingCallEnded: 1");
//            LSCall tempCall = new LSCall();
//            tempCall.setContactNumber(internationalNumber);
//            tempCall.setType(LSCall.CALL_TYPE_OUTGOING);
//            tempCall.setInquiryHandledState(LSCall.INQUIRY_HANDLED);
//            tempCall.setBeginTime(start.getTime());
//            tempCall.setAudioPath(mAudio_FilePath);
//            String phoneBookContactName = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(context, internationalNumber);
//            if (phoneBookContactName == null) {
//                tempCall.setContactName(null);
//            } else if (!phoneBookContactName.equals("")) {
//                tempCall.setContactName(phoneBookContactName);
//            }
//            long callDuration = PhoneNumberAndCallUtils.secondsFromStartAndEndDates(start, end);
//            Toast.makeText(context, "Duration " + callDuration, Toast.LENGTH_SHORT).show();
//            tempCall.setDuration(callDuration);
//            LSContact contact = LSContact.getContactFromNumber(internationalNumber);
////      if contact is null that means contact is not already saved with this number
//            if (contact != null) {
//                tempCall.setContact(contact);
//            } else {
////            new untagged contact is created, saved, entered in call entry
//                LSContact tempContact = new LSContact();
//                tempContact.setContactType(LSContact.CONTACT_TYPE_UNLABELED);
//                tempContact.setPhoneOne(internationalNumber);
//                tempContact.setContactName(phoneBookContactName);
//                tempContact.save();
//                tempCall.setContact(tempContact);
//            }
//            tempCall.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
//            tempCall.save();
////            Log.d(TAG, "onOutgoingCallEnded: tempCallAudioPath: " + tempCall.getAudioPath());
//            LSInquiry tempInquiry = LSInquiry.getPendingInquiryByNumberIfExists(internationalNumber);
//            if (tempInquiry != null && tempInquiry.getAverageResponseTime() <= 0) {
//                Calendar now = Calendar.getInstance();
//                tempInquiry.setAverageResponseTime(now.getTimeInMillis() - tempInquiry.getBeginTime());
//                tempInquiry.setStatus(LSInquiry.INQUIRY_STATUS_ATTENDED);
//                if (tempInquiry.getSyncStatus().equals(SyncStatus.SYNC_STATUS_INQUIRY_PENDING_SYNCED)) {
//                    tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_ATTENDED_NOT_SYNCED);
//                } else {
//                    tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_PENDING_NOT_SYNCED);
//                    Toast.makeText(context, "Let me know when this Toast Appears", Toast.LENGTH_SHORT).show();
//                }
//                tempInquiry.save();
//            }
//            LSCallRecording tempRecording = new LSCallRecording();
//            tempRecording.setLocalIdOfCall("" + tempCall.getId());
////            Log.d(TAG, "onOutgoingCallEnded: File Location1: " + mAudio_FilePath);
//            tempRecording.setAudioPath("" + mAudio_FilePath);
//            tempRecording.setSyncStatus(SyncStatus.SYNC_STATUS_RECORDING_NOT_SYNCED);
//            tempRecording.save();
////            Log.d(TAG, "onOutgoingCallEnded: File Location2: " + tempRecording.getAudioPath());
//            DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(context);
//            dataSenderAsync.run();
//            OutgoingCallEventModel mCallEvent = new OutgoingCallEventModel(OutgoingCallEventModel.CALL_TYPE_OUTGOING);
//            TinyBus bus = TinyBus.from(context.getApplicationContext());
//            bus.post(mCallEvent);
//        }
//        if (recorder != null && isRecordStarted) {
//            recorder.stop();
//            recorder.reset();
//            recorder.release();
//            isRecordStarted = false;
//            Log.d(TAG, "onOutgoingCallEnded: RecorderStopped");
//        }
//        if (wakeLock != null && wakeLock.isHeld()) {
//            wakeLock.release();
//            wakeLock = null;
//            Log.d(TAG, "onIncomingCallStarted: Wakelock Released");
//        }
        Log.d(TAG, "onOutgoingCall() called with: context = [" + ctx + "], number = [" + number + "], setAlarm = [" + start + "]");
//        TheCallLogEngine theCallLogEngine = new TheCallLogEngine(context);
//        theCallLogEngine.execute();
//        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(context);
//        dataSenderAsync.run();
//        OutgoingCallEventModel mCallEvent = new OutgoingCallEventModel(OutgoingCallEventModel.CALL_TYPE_OUTGOING);
//        TinyBus bus = TinyBus.from(context.getApplicationContext());
//        bus.post(mCallEvent);
        final TheCallLogEngine theCallLogEngine = new TheCallLogEngine(ctx);
        theCallLogEngine.execute();
        OutgoingCallEventModel outCallEvent = new OutgoingCallEventModel(OutgoingCallEventModel.CALL_TYPE_OUTGOING);
        TinyBus outBus = TinyBus.from(ctx.getApplicationContext());
        outBus.post(outCallEvent);
        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(ctx);
        dataSenderAsync.setDataSenderOnPostExecuteListener(new PostExecuteListener() {
            @Override
            public void onPostExecuteListener() {
                Log.d("testlog", "onPostExecuteListener: I AM Listened");
                if (theCallLogEngine.getStatus() == AsyncTask.Status.FINISHED) {
                    Log.d("testlog", "TheCallLogEngine Completed: completeWakefulIntent");
                    CallReceiver.completeWakefulIntent(intent);
                }
            }
        });
        Log.d("testlog", "onOutgoingCallEnded:");
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start, final Intent intent) {
//        Toast.makeText(context, "Missed Call Detected", Toast.LENGTH_SHORT).show();
//        showTagNumberPopupIfNeeded(context, number);
        if (isBubbleShown) {
            endServiceAndCallPopupFlyer(ctx);
            isBubbleShown = false;
        }
//        String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(number);
//        LSContact personalContactCheck = LSContact.getContactFromNumber(internationalNumber);
//        if (personalContactCheck != null && personalContactCheck.getContactType().equals(LSContact.CONTACT_TYPE_IGNORED)) {
//            //Do nothing
//            Log.d(TAG, "onMissedCall: Contact is Ignored.");
//        } else {
//            LSCall tempCall = new LSCall();
//            tempCall.setContactNumber(internationalNumber);
//            String phoneBookContactName = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(context, internationalNumber);
//            if (phoneBookContactName == null) {
//                tempCall.setContactName(null);
//            } else if (!phoneBookContactName.equals("")) {
//                tempCall.setContactName(phoneBookContactName);
//            }
//            LSContact contact = LSContact.getContactFromNumber(internationalNumber);
//            if (contact != null) {
//                tempCall.setContact(contact);
//                tempCall.setContactName(contact.getContactName());
//            } else {
//                tempCall.setContact(null);
//            }
//            tempCall.setType(LSCall.CALL_TYPE_MISSED);
//            tempCall.setInquiryHandledState(LSCall.INQUIRY_NOT_HANDLED);
//            tempCall.setBeginTime(start.getTime());
//            tempCall.setDuration(0L);
//            tempCall.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
//            tempCall.save();
//            LSInquiry tempInquiry = LSInquiry.getPendingInquiryByNumberIfExists(tempCall.getContactNumber());
//            if (tempInquiry != null) {
//                Toast.makeText(context, "Already Exists", Toast.LENGTH_SHORT).show();
//                tempInquiry.setCountOfInquiries(tempInquiry.getCountOfInquiries() + 1);
////            tempInquiry.setBeginTime(start.getTime());
////            tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_PENDING_NOT_SYNCED);
//                tempInquiry.save();
//                Log.d(TAG, "onMissedCall: getCountOfInquiries: " + tempInquiry.getCountOfInquiries());
//                Log.d(TAG, "onMissedCall: tempInquiry :" + tempInquiry.toString());
//            } else {
//                Toast.makeText(context, "Doesnt Exist", Toast.LENGTH_SHORT).show();
//                LSInquiry newInquiry = new LSInquiry();
//                newInquiry.setContactNumber(tempCall.getContactNumber());
//                newInquiry.setContactName(tempCall.getContactName());
//                newInquiry.setContact(tempCall.getContact());
//                newInquiry.setBeginTime(tempCall.getBeginTime());
//                newInquiry.setDuration(tempCall.getDuration());
//                newInquiry.setCountOfInquiries(1);
//                newInquiry.setStatus(LSInquiry.INQUIRY_STATUS_PENDING);
//                newInquiry.setAverageResponseTime(0L);
//                newInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_PENDING_NOT_SYNCED);
//                newInquiry.save();
//                Log.d(TAG, "onMissedCall: newInquiry: " + newInquiry.toString());
//            }
//            DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(context);
//            dataSenderAsync.run();
//            MissedCallEventModel mCallEvent = new MissedCallEventModel(MissedCallEventModel.CALL_TYPE_MISSED);
//            TinyBus bus = TinyBus.from(context.getApplicationContext());
//            bus.post(mCallEvent);
//        }
//        if (recorder != null && isRecordStarted) {
//            recorder.stop();
//            recorder.reset();
//            recorder.release();
//            isRecordStarted = false;
//            Log.d(TAG, "onMissedCall: RecorderStopped");
//        }
//        if (wakeLock != null && wakeLock.isHeld()) {
//            wakeLock.release();
//            wakeLock = null;
//            Log.d(TAG, "onIncomingCallStarted: Wakelock Released");
//        }
        Log.d("MissedCallReceiver", "onMissedCall() called with: context = [" + ctx + "], number = [" + number + "], setAlarm = [" + start + "]");
//        TheCallLogEngine theCallLogEngine = new TheCallLogEngine(context);
//        theCallLogEngine.execute();
//        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(context);
//        dataSenderAsync.run();
//        MissedCallEventModel mCallEvent = new MissedCallEventModel(MissedCallEventModel.CALL_TYPE_MISSED);
//        TinyBus bus = TinyBus.from(context.getApplicationContext());
//        bus.post(mCallEvent);
        final TheCallLogEngine theCallLogEngine = new TheCallLogEngine(ctx);
        theCallLogEngine.execute();
        MissedCallEventModel mCallEvent = new MissedCallEventModel(MissedCallEventModel.CALL_TYPE_MISSED);
        TinyBus mBus = TinyBus.from(ctx.getApplicationContext());
        mBus.post(mCallEvent);
        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(ctx);
        dataSenderAsync.setDataSenderOnPostExecuteListener(new PostExecuteListener() {
            @Override
            public void onPostExecuteListener() {
                Log.d("testlog", "onPostExecuteListener: I AM Listened");
                if (theCallLogEngine.getStatus() == AsyncTask.Status.FINISHED) {
                    Log.d("testlog", "TheCallLogEngine Completed: completeWakefulIntent");
                    CallReceiver.completeWakefulIntent(intent);
                }
            }
        });
        Log.d("testlog", "onMissedCall: End Line");
    }

    public void checkShowCallPopupFlyer(Context ctx, String number) {
        Log.wtf(TAG, "checkShowCallPopupFlyer: ");
        String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(number);
        LSContact oneContact;
        oneContact = LSContact.getContactFromNumber(internationalNumber);
        ArrayList<LSNote> notesForContact = null;
        if (oneContact != null) {
            notesForContact = (ArrayList<LSNote>) LSNote.getNotesByContactId(oneContact.getId());
            if (notesForContact != null && notesForContact.size() > 0) {
                notesForContact.size();

                BubbleHelper.getInstance(ctx).show(notesForContact.get(notesForContact.size() - 1).getId(), internationalNumber, oneContact);

//                Intent intent = new Intent(context, AddEditLeadService.class);
//                intent.putExtra(OUTGOINGCALL_CONTACT_NOTE_ID, notesForContact.get(notesForContact.size() - 1).getId() + "");
//                context.startService(intent);
            } else {
                BubbleHelper.getInstance(ctx).show(internationalNumber);
            }
        } else {
            BubbleHelper.getInstance(ctx).show(internationalNumber);
        }
        Intent intent = new Intent(ctx, CallDetectionService.class);
        ctx.startService(intent);
    }

    private void endServiceAndCallPopupFlyer(Context ctx) {
        BubbleHelper.getInstance(ctx).hide();
    }
}