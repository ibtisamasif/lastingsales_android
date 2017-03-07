package com.example.muzafarimran.lastingsales.receivers;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.FileObserver;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.events.IncomingCallEventModel;
import com.example.muzafarimran.lastingsales.events.MissedCallEventModel;
import com.example.muzafarimran.lastingsales.events.OutgoingCallEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSCallRecording;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.service.PopupUIService;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.CallEndNotification;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;
import com.example.muzafarimran.lastingsales.utilscallprocessing.TheCallLogEngine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import de.halfbit.tinybus.TinyBus;

import static android.content.Context.POWER_SERVICE;

/**
 * Created by ahmad on 08-Nov-16.
 */

public class CallsStatesReceiver extends CallReceiver implements PathFileObserver.FileObserverInterface {
    public static final String OUTGOINGCALL_CONTACT_ID = "outgoing_contact_id";
    public static final String INCOMINGCALL_CONTACT_ID = "incoming_contact_id";
    public static final String OUTGOINGCALL_CONTACT_NOTE_ID = "outgoing_contact_note_id";
    public static final String INCOMINGCALL_CONTACT_NOTE_ID = "incoming_contact_note_id";
    private static final String TAG = "CallsStatesReceiver";
    public static String mAudio_FilePath;  //TODO this one
    public static String mAudio_FolderName = "LastingSales";
    public static String mAudio_FileName = "";
    public static MediaRecorder recorder;
    private static boolean isRecordStarted = false;
    private static PowerManager powerManager = null;
    private static PowerManager.WakeLock wakeLock = null;
    private static PathFileObserver pathFileObserver;


    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        if (wakeLock == null) {
            powerManager = (PowerManager) ctx.getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
            wakeLock.acquire();
            Log.d(TAG, "onIncomingCallStarted: Wakelock Aquired");
        }
        if (recorder != null && isRecordStarted) {
            Log.d(TAG, "onIncomingCallStarted: RecorderStop");
            recorder.stop();
            isRecordStarted = false;
        }
        Toast.makeText(ctx, "Incoming call started", Toast.LENGTH_SHORT).show();
        checkShowCallPopup(ctx, number);
        String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(number);
        LSContact personalContactCheck = LSContact.getContactFromNumber(internationalNumber);
        if (personalContactCheck != null && personalContactCheck.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
            String lsDirectory = Environment.getExternalStorageDirectory() + "/" + mAudio_FolderName; // TODO clean the code
            pathFileObserver = new PathFileObserver(lsDirectory);
            pathFileObserver.startWatching();
            try {
                String myDirectory = Environment.getExternalStorageDirectory() + "/" + mAudio_FolderName;
                File sampleDir = new File(myDirectory.toString());
                if (!sampleDir.exists()) {
                    sampleDir.mkdirs();
                }
                mAudio_FileName = PhoneNumberAndCallUtils.generateUniqueFileName(mAudio_FileName);
                mAudio_FilePath = sampleDir.getAbsolutePath() + "/" + mAudio_FileName + ".amr";
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                recorder.setOutputFile(sampleDir.getAbsolutePath() + "/" + mAudio_FileName + ".amr");
                //recorder.setOutputFile("/storage/emulated/0/LastingSalesRecordings/abc.amr");
                recorder.prepare();
            } catch (IllegalStateException | IOException e) {
                e.printStackTrace();
                Log.e(TAG, "error while preparing recording", e);
                // You might want to inform the user too, with a Toast
            }
            if (recorder != null && !isRecordStarted) {
                recorder.start();
                isRecordStarted = true;
                Log.d(TAG, "onIncomingCallStarted: RecorderStart");
            }
//            Log.d(TAG, "onOutgoingCallStarted: File Location: " + mAudio_FilePath);

            LSCallRecording tempRecording = new LSCallRecording();
//            tempRecording.setLocalIdOfCall("" + tempCall.getId());
            Log.d(TAG, "onIncomingCallStarted: File Location1: " + mAudio_FilePath);
            tempRecording.setBeginTime(start.getTime());
            tempRecording.setAudioPath("" + mAudio_FilePath);
            tempRecording.setSyncStatus(SyncStatus.SYNC_STATUS_RECORDING_NOT_SYNCED);
            tempRecording.save();
            Log.d(TAG, "onIncomingCallStarted: RecordingBeginTime " + tempRecording.getBeginTime());

        }
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Log.d("LSTime", "onOutgoingCallStarted: CallReceiverLog BeginTime: " + start.getTime());
        if (wakeLock == null) {
            powerManager = (PowerManager) ctx.getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
            wakeLock.acquire();
            Log.d(TAG, "onIncomingCallStarted: Wakelock Aquired");
        }

        if (recorder != null && isRecordStarted) {
            Log.d(TAG, "onOutgoingCallStarted: RecorderStop");
            recorder.stop();
            isRecordStarted = false;
        }
        Toast.makeText(ctx, "Outgoing call started", Toast.LENGTH_SHORT).show();
        checkShowCallPopup(ctx, number);
        String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(number);
        LSContact personalContactCheck = LSContact.getContactFromNumber(internationalNumber);
        if (personalContactCheck != null && personalContactCheck.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
            String lsDirectory = Environment.getExternalStorageDirectory() + "/" + mAudio_FolderName;
            pathFileObserver = new PathFileObserver(lsDirectory);
            pathFileObserver.startWatching();

            try {
                String myDirectory = Environment.getExternalStorageDirectory() + "/" + mAudio_FolderName;
                File sampleDir = new File(myDirectory.toString());
                if (!sampleDir.exists()) {
                    sampleDir.mkdirs();
                }
                mAudio_FileName = PhoneNumberAndCallUtils.generateUniqueFileName(mAudio_FileName);
                mAudio_FilePath = sampleDir.getAbsolutePath() + "/" + mAudio_FileName + ".amr";
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                recorder.setOutputFile(sampleDir.getAbsolutePath() + "/" + mAudio_FileName + ".amr");
//                Log.d(TAG, "onOutgoingCallStarted: myDir: " + sampleDir.getAbsolutePath());
                // recorder.setOutputFile("/storage/emulated/0/LastingSalesRecordings/abc.amr");
                recorder.prepare();
            } catch (IllegalStateException | IOException e) {
                e.printStackTrace();
                Log.e(TAG, "error while preparing recording", e);
                // You might want to inform the user too, with a Toast
            }
            if (recorder != null && !isRecordStarted) {
                recorder.start();
                isRecordStarted = true;
                Log.d(TAG, "onOutgoingCallStarted: RecorderStart");
            }
//            Log.d(TAG, "onOutgoingCallStarted: File Location: " + mAudio_FilePath);

            LSCallRecording tempRecording = new LSCallRecording();
//            tempRecording.setLocalIdOfCall("" + tempCall.getId());
            Log.d(TAG, "onOutgoingCallStarted: File Location1: " + mAudio_FilePath);
            tempRecording.setBeginTime(start.getTime());
            tempRecording.setAudioPath("" + mAudio_FilePath);
            tempRecording.setSyncStatus(SyncStatus.SYNC_STATUS_RECORDING_NOT_SYNCED);
            tempRecording.save();
            Log.d(TAG, "onOutgoingCallStarted: RecordingBeginTime " + tempRecording.getBeginTime());


        }
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Toast.makeText(ctx, "Incoming call Ended", Toast.LENGTH_SHORT).show();
//        showTagNumberPopupIfNeeded(ctx, number);
        endServiceAndCallPopup(ctx);
//        String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(number);
//        LSContact personalContactCheck = LSContact.getContactFromNumber(internationalNumber);
//        Log.d(TAG, "onIncomingCallEnded: 0");
//        if (personalContactCheck == null || !personalContactCheck.getContactType().equals(LSContact.CONTACT_TYPE_PERSONAL)) {
//            Log.d(TAG, "onIncomingCallEnded: 1");
//            LSCall tempCall = new LSCall();
//            tempCall.setContactNumber(internationalNumber);
//            tempCall.setType(LSCall.CALL_TYPE_INCOMING);
//            tempCall.setInquiryHandledState(LSCall.INQUIRY_HANDLED);
//            tempCall.setBeginTime(start.getTime());
//            tempCall.setAudioPath(mAudio_FilePath); //TODO Need to pass to engine
//            String phoneBookContactName = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(ctx, internationalNumber);
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
//                tempContact.setContactType(LSContact.CONTACT_TYPE_UNTAGGED);
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
//            DataSenderAsync dataSenderAsync = new DataSenderAsync(ctx);
//            dataSenderAsync.execute();
//            IncomingCallEventModel mCallEvent = new IncomingCallEventModel(IncomingCallEventModel.CALL_TYPE_INCOMING);
//            TinyBus bus = TinyBus.from(ctx.getApplicationContext());
//            bus.post(mCallEvent);
//        }
        if (recorder != null && isRecordStarted) {
            recorder.stop();
            recorder.reset();
            recorder.release();
            isRecordStarted = false;
            Log.d(TAG, "onIncomingCallEnded: RecorderStopped");
        }

        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
            Log.d(TAG, "onIncomingCallStarted: Wakelock Released");
        }
        Log.d("IncomingCallReceiver", "onIncomingCall() called with: ctx = [" + ctx + "], number = [" + number + "], setAlarm = [" + start + "]");
        TheCallLogEngine theCallLogEngine = new TheCallLogEngine(ctx);
        theCallLogEngine.execute();
        DataSenderAsync dataSenderAsync = new DataSenderAsync(ctx);
        dataSenderAsync.execute();
        IncomingCallEventModel mCallEvent = new IncomingCallEventModel(IncomingCallEventModel.CALL_TYPE_INCOMING);
        TinyBus bus = TinyBus.from(ctx.getApplicationContext());
        bus.post(mCallEvent);
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Toast.makeText(ctx, "Outgoing call Ended", Toast.LENGTH_SHORT).show();
//        showTagNumberPopupIfNeeded(ctx, number);
        endServiceAndCallPopup(ctx);
//        String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(number);
//        LSContact personalContactCheck = LSContact.getContactFromNumber(internationalNumber);
////        Log.d(TAG, "onOutgoingCallEnded: 0");
//        if (personalContactCheck == null || !personalContactCheck.getContactType().equals(LSContact.CONTACT_TYPE_PERSONAL)) {
////            Log.d(TAG, "onOutgoingCallEnded: 1");
//            LSCall tempCall = new LSCall();
//            tempCall.setContactNumber(internationalNumber);
//            tempCall.setType(LSCall.CALL_TYPE_OUTGOING);
//            tempCall.setInquiryHandledState(LSCall.INQUIRY_HANDLED);
//            tempCall.setBeginTime(start.getTime());
//            tempCall.setAudioPath(mAudio_FilePath);
//            String phoneBookContactName = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(ctx, internationalNumber);
//            if (phoneBookContactName == null) {
//                tempCall.setContactName(null);
//            } else if (!phoneBookContactName.equals("")) {
//                tempCall.setContactName(phoneBookContactName);
//            }
//            long callDuration = PhoneNumberAndCallUtils.secondsFromStartAndEndDates(start, end);
//            Toast.makeText(ctx, "Duration " + callDuration, Toast.LENGTH_SHORT).show();
//            tempCall.setDuration(callDuration);
//            LSContact contact = LSContact.getContactFromNumber(internationalNumber);
////      if contact is null that means contact is not already saved with this number
//            if (contact != null) {
//                tempCall.setContact(contact);
//            } else {
////            new untagged contact is created, saved, entered in call entry
//                LSContact tempContact = new LSContact();
//                tempContact.setContactType(LSContact.CONTACT_TYPE_UNTAGGED);
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
//                } else { // TODO check if else is needed or not.
//                    tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_PENDING_NOT_SYNCED);
//                    Toast.makeText(ctx, "Let me know when this Toast Appears", Toast.LENGTH_SHORT).show();
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
//            DataSenderAsync dataSenderAsync = new DataSenderAsync(ctx);
//            dataSenderAsync.execute();
//            OutgoingCallEventModel mCallEvent = new OutgoingCallEventModel(OutgoingCallEventModel.CALL_TYPE_OUTGOING);
//            TinyBus bus = TinyBus.from(ctx.getApplicationContext());
//            bus.post(mCallEvent);
//        }
        if (recorder != null && isRecordStarted) {
            recorder.stop();
            recorder.reset();
            recorder.release();
            isRecordStarted = false;
            Log.d(TAG, "onOutgoingCallEnded: RecorderStopped");
        }
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
            Log.d(TAG, "onIncomingCallStarted: Wakelock Released");
        }
        Log.d("OutgoingCallReceiver", "onOutgoingCall() called with: ctx = [" + ctx + "], number = [" + number + "], setAlarm = [" + start + "]");
        TheCallLogEngine theCallLogEngine = new TheCallLogEngine(ctx);
        theCallLogEngine.execute();
        DataSenderAsync dataSenderAsync = new DataSenderAsync(ctx);
        dataSenderAsync.execute();
        OutgoingCallEventModel mCallEvent = new OutgoingCallEventModel(OutgoingCallEventModel.CALL_TYPE_OUTGOING);
        TinyBus bus = TinyBus.from(ctx.getApplicationContext());
        bus.post(mCallEvent);

    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        Toast.makeText(ctx, "Missed Call Detected", Toast.LENGTH_SHORT).show();
//        showTagNumberPopupIfNeeded(ctx, number);
//        String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(number);
//        LSContact personalContactCheck = LSContact.getContactFromNumber(internationalNumber);
//        if (personalContactCheck != null && personalContactCheck.getContactType().equals(LSContact.CONTACT_TYPE_PERSONAL)) {
//            //Do nothing
//            Log.d(TAG, "onMissedCall: Contact is Ignored.");
//        } else {
//            LSCall tempCall = new LSCall();
//            tempCall.setContactNumber(internationalNumber);
//            String phoneBookContactName = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(ctx, internationalNumber);
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
//                Toast.makeText(ctx, "Already Exists", Toast.LENGTH_SHORT).show();
//                tempInquiry.setCountOfInquiries(tempInquiry.getCountOfInquiries() + 1);
////            tempInquiry.setBeginTime(start.getTime());
////            tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_PENDING_NOT_SYNCED);
//                tempInquiry.save();
//                Log.d(TAG, "onMissedCall: getCountOfInquiries: " + tempInquiry.getCountOfInquiries());
//                Log.d(TAG, "onMissedCall: tempInquiry :" + tempInquiry.toString());
//            } else {
//                Toast.makeText(ctx, "Doesnt Exist", Toast.LENGTH_SHORT).show();
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
//            DataSenderAsync dataSenderAsync = new DataSenderAsync(ctx);
//            dataSenderAsync.execute();
//            MissedCallEventModel mCallEvent = new MissedCallEventModel(MissedCallEventModel.CALL_TYPE_MISSED);
//            TinyBus bus = TinyBus.from(ctx.getApplicationContext());
//            bus.post(mCallEvent);
//        }
        if (recorder != null && isRecordStarted) {
            recorder.stop();
            recorder.reset();
            recorder.release();
            isRecordStarted = false;
            Log.d(TAG, "onMissedCall: RecorderStopped");
        }
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
            Log.d(TAG, "onIncomingCallStarted: Wakelock Released");
        }
        Log.d("MissedCallReceiver", "onMissedCall() called with: ctx = [" + ctx + "], number = [" + number + "], setAlarm = [" + start + "]");
        TheCallLogEngine theCallLogEngine = new TheCallLogEngine(ctx);
        theCallLogEngine.execute();
        DataSenderAsync dataSenderAsync = new DataSenderAsync(ctx);
        dataSenderAsync.execute();
        MissedCallEventModel mCallEvent = new MissedCallEventModel(MissedCallEventModel.CALL_TYPE_MISSED);
        TinyBus bus = TinyBus.from(ctx.getApplicationContext());
        bus.post(mCallEvent);
    }

    public void checkShowCallPopup(Context ctx, String number) {
        String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(number);
        LSContact oneContact;
        oneContact = LSContact.getContactFromNumber(internationalNumber);
        ArrayList<LSNote> notesForContact = null;
        if (oneContact != null) {
            notesForContact = (ArrayList<LSNote>) LSNote.getNotesByContactId(oneContact.getId());
            if (notesForContact != null && notesForContact.size() > 0) {
                notesForContact.size();
                Intent intent = new Intent(ctx, PopupUIService.class);
                intent.putExtra(OUTGOINGCALL_CONTACT_NOTE_ID, notesForContact.get(notesForContact.size() - 1).getId() + "");
                ctx.startService(intent);
            }
        }
    }

    private void endServiceAndCallPopup(Context ctx) {
        Intent intent = new Intent(ctx, PopupUIService.class);
        ctx.stopService(intent);
    }

    private void showTagNumberPopupIfNeeded(Context ctx, String number) {
        NotificationManager mNotificationManager;
        String intlNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(number);
        LSContact tempContact = LSContact.getContactFromNumber(intlNumber);

        if (tempContact != null && tempContact.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
            String name = tempContact.getContactName();
            Long contact_id = tempContact.getId();
            mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(CallEndNotification.NOTIFICATION_ID, CallEndNotification.createFollowUpNotification(ctx, number, contact_id));

        } else if (tempContact != null && tempContact.getContactType().equals(LSContact.CONTACT_TYPE_UNTAGGED)) {
            mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(CallEndNotification.NOTIFICATION_ID, CallEndNotification.createTagNotification(ctx, intlNumber));
        } else if (tempContact == null) {
            Log.d(TAG, "showTagNumberPopupIfNeeded: tempContact is NULL");
            mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(CallEndNotification.NOTIFICATION_ID, CallEndNotification.createTagNotification(ctx, intlNumber));
        }
    }

    @Override
    public void onEvent(int event, String path) {
        switch (event) {
            case FileObserver.CREATE:
                Log.d(TAG, "Event CREATE:");
//                Log.d(TAG, "CREATE:" + rootPath + path);
                break;
            case FileObserver.DELETE:
                Log.d(TAG, "Event DELETE:");
//                Log.d(TAG, "DELETE:" + rootPath + path);
                break;
            case FileObserver.DELETE_SELF:
                Log.d(TAG, "Event DELETE_SELF:");
//                Log.d(TAG, "DELETE_SELF:" + rootPath + path);
                break;
            case FileObserver.MODIFY:
//                Log.d(TAG, "Event MODIFY:");
//                Log.d(TAG, "MODIFY:" + rootPath + path);
                break;
            case FileObserver.MOVED_FROM:
                Log.d(TAG, "Event MOVED_FROM:");
//                Log.d(TAG, "MOVED_FROM:" + rootPath + path);
                break;
            case FileObserver.MOVED_TO:
                Log.d(TAG, "MOVED_TO:" + path);
                break;
            case FileObserver.MOVE_SELF:
                Log.d(TAG, "MOVE_SELF:" + path);
                break;
            case FileObserver.CLOSE_WRITE:
                Log.d(TAG, "CLOSE_WRITE:" + path);
                RecordingManager recordingManager = new RecordingManager();
                recordingManager.execute();
                break;
            default:
                // just ignore
                break;
        }
    }
}