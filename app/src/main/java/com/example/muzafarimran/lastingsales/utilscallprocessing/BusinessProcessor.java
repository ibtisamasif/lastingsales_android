package com.example.muzafarimran.lastingsales.utilscallprocessing;

import android.content.Context;
import android.util.Log;

import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;

/**
 * Created by ibtisam on 3/16/2017.
 */

class BusinessProcessor {
    public static final String TAG = "BusinessProcessor";

    public static void Process(Context mContext, LSCall call) {
        Log.d(TAG, "BusinessProcessor: Process() Entered");
        // Check if type is incoming , outgoing or missed
        if (call.getType().equals(LSCall.CALL_TYPE_INCOMING)) {
            //Incoming
            call.setInquiryHandledState(LSCall.INQUIRY_HANDLED);
            call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            call.save();

        }else if (call.getType().equals(LSCall.CALL_TYPE_OUTGOING))  {
            //Outgoing
            call.setInquiryHandledState(LSCall.INQUIRY_HANDLED);
            call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            call.save();

        }else if (call.getType().equals(LSCall.CALL_TYPE_UNANSWERED)) {
            //Outgoing Unanswered
            call.setInquiryHandledState(LSCall.INQUIRY_NOT_HANDLED);
            call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            call.save();

        } else if (call.getType().equals(LSCall.CALL_TYPE_MISSED)){
            //Missed
            call.setInquiryHandledState(LSCall.INQUIRY_NOT_HANDLED);
            call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            call.save();
        } else if (call.getType().equals(LSCall.CALL_TYPE_REJECTED)) {
            //Incoming Rejected
            call.setInquiryHandledState(LSCall.INQUIRY_NOT_HANDLED);
            call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            call.save();

        }
    }
}
