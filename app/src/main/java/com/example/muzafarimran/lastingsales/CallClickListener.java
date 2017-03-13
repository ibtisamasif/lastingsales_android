package com.example.muzafarimran.lastingsales;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.example.muzafarimran.lastingsales.events.MissedCallEventModel;

import de.halfbit.tinybus.TinyBus;

/**
 * Class for click events on call icon.
 */
public class CallClickListener implements View.OnClickListener {

    //private ArrayList<Contact> contacts = null;
    private Context context;

    public CallClickListener(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        if (ContextCompat.checkSelfPermission(this.context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
        }
        String number = v.getTag().toString();
//        ArrayList<LSCall> allCallsForThisNumber = LSCall.getCallsFromNumber(PhoneNumberAndCallUtils.numberToInterNationalNumber(number));
//        if (allCallsForThisNumber != null) {
//            for (LSCall oneCall : allCallsForThisNumber) {
//                oneCall.setInquiryHandledState(LSCall.INQUIRY_HANDLED);
//                oneCall.setCountOfInquiries(49);
//                oneCall.save();
//            }
//        }
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        this.context.startActivity(intent);
        MissedCallEventModel mCallEvent = new MissedCallEventModel(MissedCallEventModel.CALL_TYPE_MISSED);
        TinyBus bus = TinyBus.from(context.getApplicationContext());
        bus.post(mCallEvent);
    }
}