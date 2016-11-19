package com.example.muzafarimran.lastingsales.receivers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.Events.IncomingCallEventModel;
import com.example.muzafarimran.lastingsales.Events.OutgoingCallEventModel;
import com.example.muzafarimran.lastingsales.Utils.PhoneNumberAndCallUtils;
import com.example.muzafarimran.lastingsales.Events.MissedCallEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import java.util.Date;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ahmad on 08-Nov-16.
 */

public class CallsStatesReceiver extends CallReceiver {

    MissedCallEventModel mCallEvent;

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {

        Toast.makeText(ctx, "Incoming call started", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Toast.makeText(ctx, "Outgoing call started", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Toast.makeText(ctx, "Incoming call Ended", Toast.LENGTH_SHORT).show();

//        String normalisedNumber = PhoneNumberUtils.formatNumber(number, "92");


//        phoneNumberUtil.isValidNumber()


        LSCall tempCall = new LSCall();

        String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(number);
        tempCall.setContactNumber(internationalNumber);
        tempCall.setType(LSCall.CALL_TYPE_INCOMING);
//        tempCall.setBeginTime(setAlarm.toString());
        tempCall.setBeginTime(start.getTime());

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
//        bus.register(mCallEvent);
        bus.post(mCallEvent);
        Log.d("IncomingCallReceiver", "onIncomingCall() called with: ctx = [" + ctx + "], number = [" + number + "], setAlarm = [" + start + "]");


    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Toast.makeText(ctx, "Outgoing call Ended", Toast.LENGTH_SHORT).show();

        LSCall tempCall = new LSCall();

        String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(number);
        tempCall.setContactNumber(internationalNumber);
        tempCall.setType(LSCall.CALL_TYPE_OUTGOING);
//        tempCall.setBeginTime(setAlarm.toString());
        tempCall.setBeginTime(start.getTime());

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
        } catch (IllegalArgumentException e)
        {
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

        LSContact contact = LSContact.getContactFromNumber(internationalNumber);

        if (contact != null) {
            tempCall.setContact(contact);
        } else {
            tempCall.setContact(null);
        }


        tempCall.setType(LSCall.CALL_TYPE_MISSED);
//        tempCall.setBeginTime(setAlarm.toString());
        tempCall.setBeginTime(start.getTime());

        tempCall.save();


        MissedCallEventModel mCallEvent = new MissedCallEventModel(MissedCallEventModel.CALL_TYPE_MISSED);

        TinyBus bus = TinyBus.from(ctx.getApplicationContext());
//        bus.register(mCallEvent);
        bus.post(mCallEvent);
        Log.d("MissedCallReceiver", "onMissedCall() called with: ctx = [" + ctx + "], number = [" + number + "], setAlarm = [" + start + "]");

    }


}