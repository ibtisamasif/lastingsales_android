package com.example.muzafarimran.lastingsales.receivers;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.events.IncomingCallEventModel;
import com.example.muzafarimran.lastingsales.events.MissedCallEventModel;
import com.example.muzafarimran.lastingsales.events.OutgoingCallEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.service.PopupUIService;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.CallEndNotification;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

import java.util.ArrayList;
import java.util.Date;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ahmad on 08-Nov-16.
 */

public class CallsStatesReceiver extends CallReceiver {
    public static final String OUTGOINGCALL_CONTACT_ID = "outgoing_contact_id";
    public static final String INCOMINGCALL_CONTACT_ID = "incoming_contact_id";
    public static final String OUTGOINGCALL_CONTACT_NOTE_ID = "outgoing_contact_note_id";
    public static final String INCOMINGCALL_CONTACT_NOTE_ID = "incoming_contact_note_id";
    private static final String TAG = "CallsStatesReceiver";
    private NotificationManager mNotificationManager;

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        Toast.makeText(ctx, "Incoming call started", Toast.LENGTH_SHORT).show();
        checkShowCallPopup(ctx, number);
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Toast.makeText(ctx, "Outgoing call started", Toast.LENGTH_SHORT).show();
        checkShowCallPopup(ctx, number);
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Toast.makeText(ctx, "Incoming call Ended", Toast.LENGTH_SHORT).show();
        showTagNumberPopupIfNeeded(ctx, number);
        endServiceAndCallPopup(ctx);
        LSCall tempCall = new LSCall();
        String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(number);
        tempCall.setContactNumber(internationalNumber);
        tempCall.setType(LSCall.CALL_TYPE_INCOMING);
        tempCall.setInquiryHandledState(LSCall.INQUIRY_HANDLED);
        tempCall.setBeginTime(start.getTime());
        String phoneBookContactName = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(ctx, internationalNumber);
        if (phoneBookContactName == null) {
            tempCall.setContactName(null);
        } else if (!phoneBookContactName.equals("")) {
            tempCall.setContactName(phoneBookContactName);
        }
        long callDuration = PhoneNumberAndCallUtils.secondsFromStartAndEndDates(start, end);
        tempCall.setDuration(callDuration);
        // new untagged contact is created, saved, entered in call entry
        LSContact tempContact = new LSContact();
        tempContact.setContactType(LSContact.CONTACT_TYPE_UNTAGGED);
        tempContact.setPhoneOne(internationalNumber);
        tempContact.setContactName(phoneBookContactName);
        tempContact.save();
        tempCall.setContact(tempContact);
        tempCall.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
        tempCall.save();
        LSInquiry tempInquiry = LSInquiry.getInquiryByNumberIfExists(internationalNumber);
        if (tempInquiry != null) {
            tempInquiry.delete();
            //TODO SYNC
        }
        DataSenderAsync dataSenderAsync = new DataSenderAsync(ctx);
        dataSenderAsync.execute();
        IncomingCallEventModel mCallEvent = new IncomingCallEventModel(IncomingCallEventModel.CALL_TYPE_INCOMING);
        TinyBus bus = TinyBus.from(ctx.getApplicationContext());
        bus.post(mCallEvent);
        Log.d("IncomingCallReceiver", "onIncomingCall() called with: ctx = [" + ctx + "], number = [" + number + "], setAlarm = [" + start + "]");
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Toast.makeText(ctx, "Outgoing call Ended", Toast.LENGTH_SHORT).show();
        showTagNumberPopupIfNeeded(ctx, number);
        endServiceAndCallPopup(ctx);
        LSCall tempCall = new LSCall();
        String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(number);
        tempCall.setContactNumber(internationalNumber);
        tempCall.setType(LSCall.CALL_TYPE_OUTGOING);
        tempCall.setInquiryHandledState(LSCall.INQUIRY_HANDLED);
        tempCall.setBeginTime(start.getTime());
        String phoneBookContactName = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(ctx, internationalNumber);
        if (phoneBookContactName == null) {
            tempCall.setContactName(null);
        } else if (!phoneBookContactName.equals("")) {
            tempCall.setContactName(phoneBookContactName);
        }
        long callDuration = PhoneNumberAndCallUtils.secondsFromStartAndEndDates(start, end);
        Toast.makeText(ctx, "Duration " + callDuration, Toast.LENGTH_SHORT).show();
        tempCall.setDuration(callDuration);
        LSContact contact = LSContact.getContactFromNumber(internationalNumber);
        // new untagged contact is created, saved, entered in call entry
        LSContact tempContact = new LSContact();
        tempContact.setContactType(LSContact.CONTACT_TYPE_UNTAGGED);
        tempContact.setPhoneOne(internationalNumber);
        tempContact.setContactName(phoneBookContactName);
        tempContact.save();
        tempCall.setContact(tempContact);
        tempCall.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
        tempCall.save();
        LSInquiry tempInquiry = LSInquiry.getInquiryByNumberIfExists(internationalNumber);
        if (tempInquiry != null) {
            tempInquiry.delete();
            //TODO SYNC
        }
        DataSenderAsync dataSenderAsync = new DataSenderAsync(ctx);
        dataSenderAsync.execute();
        OutgoingCallEventModel mCallEvent = new OutgoingCallEventModel(OutgoingCallEventModel.CALL_TYPE_OUTGOING);
        TinyBus bus = TinyBus.from(ctx.getApplicationContext());
        try {
            bus.register(mCallEvent);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
//        bus.register(mCallEvent); //Fixed but not sure // Update = still crashing
        bus.post(mCallEvent); //crashed here didnt fixed
        Log.d("OutgoingCallReceiver", "onOutgoingCall() called with: ctx = [" + ctx + "], number = [" + number + "], setAlarm = [" + start + "]");
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        Toast.makeText(ctx, "Missed Call Detected", Toast.LENGTH_SHORT).show();
//        showTagNumberPopupIfNeeded(ctx, number);
        String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(number);
        LSCall tempCall = new LSCall();
        tempCall.setContactNumber(internationalNumber);
        String phoneBookContactName = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(ctx, internationalNumber);
        if (phoneBookContactName == null) {
            tempCall.setContactName(null);
        } else if (!phoneBookContactName.equals("")) {
            tempCall.setContactName(phoneBookContactName);
        }
        LSContact contact = LSContact.getContactFromNumber(internationalNumber);
        if (contact != null) {
            tempCall.setContact(contact);
            tempCall.setContactName(contact.getContactName());
        } else {
            tempCall.setContact(null);
        }
        tempCall.setType(LSCall.CALL_TYPE_MISSED);
        tempCall.setInquiryHandledState(LSCall.INQUIRY_NOT_HANDLED);
        tempCall.setBeginTime(start.getTime());
        tempCall.setDuration(0L);
        tempCall.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
        tempCall.save();
        LSInquiry tempInquiry = LSInquiry.getInquiryByNumberIfExists(tempCall.getContactNumber());
        if (tempInquiry != null) {
            Toast.makeText(ctx, "Already Exists", Toast.LENGTH_SHORT).show();
            tempInquiry.setCountOfInquiries(tempInquiry.getCountOfInquiries() + 1);
            tempInquiry.setBeginTime(start.getTime());
            tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_HANDLE_STATE_NOT_HANDLED);
            tempInquiry.save();
            Log.d(TAG, "onMissedCall: getCountOfInquiries: " + tempInquiry.getCountOfInquiries());
            Log.d(TAG, "onMissedCall: tempInquiry :" + tempInquiry.toString());
        } else {
            Toast.makeText(ctx, "Doesnt Exist", Toast.LENGTH_SHORT).show();
            LSInquiry newInquiry = new LSInquiry();
            newInquiry.setContactNumber(tempCall.getContactNumber());
            newInquiry.setContactName(tempCall.getContactName());
            newInquiry.setContact(tempCall.getContact());
            newInquiry.setBeginTime(tempCall.getBeginTime());
            newInquiry.setDuration(tempCall.getDuration());
            newInquiry.setCountOfInquiries(1);
            newInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_HANDLE_STATE_NOT_HANDLED);
            newInquiry.save();
            Log.d(TAG, "onMissedCall: newInquiry: " + newInquiry.toString());
        }
        DataSenderAsync dataSenderAsync = new DataSenderAsync(ctx);
        dataSenderAsync.execute();
        MissedCallEventModel mCallEvent = new MissedCallEventModel(MissedCallEventModel.CALL_TYPE_MISSED);
        TinyBus bus = TinyBus.from(ctx.getApplicationContext());
        bus.post(mCallEvent);
        Log.d("MissedCallReceiver", "onMissedCall() called with: ctx = [" + ctx + "], number = [" + number + "], setAlarm = [" + start + "]");
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
        String intlNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(number);
        LSContact tempContact = LSContact.getContactFromNumber(intlNumber);

        if (tempContact != null && tempContact.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
            String name = tempContact.getContactName();
            Long contact_id = tempContact.getId();
            mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(CallEndNotification.NOTIFICATION_ID, CallEndNotification.createFollowUpNotification(ctx, name, contact_id));

        } else if (tempContact != null && tempContact.getContactType().equals(LSContact.CONTACT_TYPE_UNTAGGED)) {
            mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(CallEndNotification.NOTIFICATION_ID, CallEndNotification.createTagNotification(ctx, intlNumber));
        } else if (tempContact == null) {
            mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(CallEndNotification.NOTIFICATION_ID, CallEndNotification.createTagNotification(ctx, intlNumber));
        }
    }
}