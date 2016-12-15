package com.example.muzafarimran.lastingsales.receivers;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.Events.IncomingCallEventModel;
import com.example.muzafarimran.lastingsales.Events.MissedCallEventModel;
import com.example.muzafarimran.lastingsales.Events.OutgoingCallEventModel;
import com.example.muzafarimran.lastingsales.Service.PopupUIService;
import com.example.muzafarimran.lastingsales.Utils.CallEndNotification;
import com.example.muzafarimran.lastingsales.Utils.PhoneNumberAndCallUtils;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;

import java.util.ArrayList;
import java.util.Date;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ahmad on 08-Nov-16.
 */

public class CallsStatesReceiver extends CallReceiver {
    private static final String TAG = "CallsStatesReceiver";
    private NotificationManager mNotificationManager;
    public static final String OUTGOINGCALL_CONTACT_ID = "outgoing_contact_id";
    public static final String INCOMINGCALL_CONTACT_ID = "incoming_contact_id";
    public static final String OUTGOINGCALL_CONTACT_NOTE_ID = "outgoing_contact_note_id";
    public static final String INCOMINGCALL_CONTACT_NOTE_ID = "incoming_contact_note_id";
    private String intlNumber;

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
        tempCall.setBeginTime(start.getTime());
        String phoneBookContactName = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(ctx, internationalNumber);
        if (phoneBookContactName == null) {
            tempCall.setContactName(null);
        } else if (!phoneBookContactName.equals("")) {
            tempCall.setContactName(phoneBookContactName);
        }
        long callDuration = PhoneNumberAndCallUtils.secondsFromStartAndEndDates(start, end);
        tempCall.setDuration(callDuration + "");
        LSContact contact = LSContact.getContactFromNumber(internationalNumber);
        if (contact != null) {
            tempCall.setContact(contact);
        } else {
            tempCall.setContact(null);
        }
        tempCall.save();
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
        tempCall.setBeginTime(start.getTime());
        String phoneBookContactName = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(ctx, internationalNumber);
        if (phoneBookContactName == null) {
            tempCall.setContactName(null);
        } else if (!phoneBookContactName.equals("")) {
            tempCall.setContactName(phoneBookContactName);
        }
        long callDuration = PhoneNumberAndCallUtils.secondsFromStartAndEndDates(start, end);
        tempCall.setDuration(callDuration + "");
        LSContact contact = LSContact.getContactFromNumber(internationalNumber);
        if (contact != null) {
            tempCall.setContact(contact);
        } else {
            tempCall.setContact(null);
        }
        tempCall.save();
        OutgoingCallEventModel mCallEvent = new OutgoingCallEventModel(OutgoingCallEventModel.CALL_TYPE_OUTGOING);
        TinyBus bus = TinyBus.from(ctx.getApplicationContext());
        try {
            bus.register(mCallEvent);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        bus.post(mCallEvent);
        Log.d("OutgoingCallReceiver", "onOutgoingCall() called with: ctx = [" + ctx + "], number = [" + number + "], setAlarm = [" + start + "]");
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        Toast.makeText(ctx, "Missed Call Detected", Toast.LENGTH_SHORT).show();
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
        } else {
            tempCall.setContact(null);
        }
        tempCall.setType(LSCall.CALL_TYPE_MISSED);
        tempCall.setBeginTime(start.getTime());
        tempCall.save();
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

        intlNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(number);
        LSContact tempContact = LSContact.getContactFromNumber(intlNumber);

        if (tempContact!=null) {
            if(!tempContact.getContactType().equals(LSContact.CONTACT_TYPE_PERSONAL)) {
                // If caller is already Tagged and is not Business contact show NOTIFICATION
                String name = tempContact.getContactName();
                Long contact_id = tempContact.getId();
                mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(CallEndNotification.NOTIFICATION_ID, CallEndNotification.createNotification(ctx, name , contact_id));
            }
        }
        else {
            // If caller is UNTAGGED i.e not stored in app then show PopUp
            //PopUP
//            intlNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(number);
//            Intent myIntent = new Intent(ctx, TagNumberActivity.class);
//            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            myIntent.putExtra(TagNumberActivity.NUMBER_TO_TAG, intlNumber);
//            ctx.startActivity(myIntent);

            //Notification
            mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(CallEndNotification.NOTIFICATION_ID, CallEndNotification.createFollowUpNotification(ctx , intlNumber));
            }

        }
    }