package com.example.muzafarimran.lastingsales.utilscallprocessing;

import android.content.Context;

import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.NotificationBuilder;

/**
 * Created by ibtisam on 3/4/2017.
 */

public class UnlabeledProcessor {

    public static void Process(Context mContext, LSCall call) {
        // Check if type is incoming , outgoing or missed
        if (call.getType().equals(LSCall.CALL_TYPE_INCOMING)) {
            //Incoming
            NotificationBuilder.showTagNumberPopup(mContext, call.getContactNumber());
            call.setInquiryHandledState(LSCall.INQUIRY_HANDLED);
            InquiryManager.Remove(call);
            call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            call.save();

        } else if (call.getType().equals(LSCall.CALL_TYPE_OUTGOING)) {
            //Outgoing
            NotificationBuilder.showTagNumberPopup(mContext, call.getContactNumber());
            call.setInquiryHandledState(LSCall.INQUIRY_HANDLED);
            InquiryManager.Remove(call);
            call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            call.save();

        } else if (call.getType().equals(LSCall.CALL_TYPE_UNANSWERED)) {
            //Unanswered
            call.setInquiryHandledState(LSCall.INQUIRY_NOT_HANDLED);
            call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            call.save();

        } else if (call.getType().equals(LSCall.CALL_TYPE_MISSED)) {
            //Missed
            InquiryManager.CreateOrUpdate(call);
            call.setInquiryHandledState(LSCall.INQUIRY_NOT_HANDLED);
            call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            call.save();

        } else if (call.getType().equals(LSCall.CALL_TYPE_REJECTED)) {
            //Rejected
            InquiryManager.CreateOrUpdate(call);
            call.setInquiryHandledState(LSCall.INQUIRY_NOT_HANDLED);
            call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            call.save();

        }
    }
}
