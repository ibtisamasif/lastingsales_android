package com.example.muzafarimran.lastingsales.utilscallprocessing;

import android.content.Context;

import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.NotificationBuilder;

/**
 * Created by ibtisam on 3/4/2017.
 */

class UnknownProcessor {
    public static final String TAG = "UnknownProcessor";

    public static void Process(Context mContext, LSCall call, boolean showNotification) {
        // Check if type is incoming , outgoing or missed

        if (call.getType().equals(LSCall.CALL_TYPE_INCOMING) && call.getDuration() > 0L) {
            //Incoming with whome Agent have talked
            if (showNotification) {
                NotificationBuilder.showTagNumberPopup(mContext, call.getContactNumber());
            }
            //new untagged contact is created, saved, entered in call entry
            LSContact tempContact = new LSContact();
            tempContact.setContactType(LSContact.CONTACT_TYPE_UNLABELED);
            tempContact.setPhoneOne(call.getContactNumber());
            tempContact.setContactName(call.getContactName());
            tempContact.save();
            InquiryManager.Remove(call);
            // Call Saved
            call.setContact(tempContact);
            call.setInquiryHandledState(LSCall.INQUIRY_HANDLED);
            call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            call.save();

        } else if (call.getType().equals(LSCall.CALL_TYPE_INCOMING)) {
            //Incoming
            call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            call.save();

        } else if (call.getType().equals(LSCall.CALL_TYPE_OUTGOING) && call.getDuration() > 0L) {
            //Outgoing with whome Agent have talked
            if (showNotification) {
                NotificationBuilder.showTagNumberPopup(mContext, call.getContactNumber());
            }
            //new untagged contact is created, saved, entered in call entry
            LSContact tempContact = new LSContact();
            tempContact.setContactType(LSContact.CONTACT_TYPE_UNLABELED);
            tempContact.setPhoneOne(call.getContactNumber());
            tempContact.setContactName(call.getContactName());
            tempContact.save();
            InquiryManager.Remove(call);
            // Call Saved
            call.setContact(tempContact);
            call.setInquiryHandledState(LSCall.INQUIRY_HANDLED);
            call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            call.save();

        } else if (call.getType().equals(LSCall.CALL_TYPE_OUTGOING)) {
            //Outgoing
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
            //Incoming Rejected
            InquiryManager.CreateOrUpdate(call);
            call.setInquiryHandledState(LSCall.INQUIRY_NOT_HANDLED);
            call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            call.save();

        }
        call.save();
    }
}
