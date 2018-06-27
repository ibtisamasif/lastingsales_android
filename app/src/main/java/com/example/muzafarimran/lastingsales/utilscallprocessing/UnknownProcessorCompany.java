package com.example.muzafarimran.lastingsales.utilscallprocessing;

import android.content.Context;
import android.util.Log;

import com.example.muzafarimran.lastingsales.events.LeadContactAddedEventModel;
import com.example.muzafarimran.lastingsales.events.MissedCallEventModel;
import com.example.muzafarimran.lastingsales.events.UnlabeledContactAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.CallEndTagBoxService;

import java.util.Calendar;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 3/4/2017.
 */

class UnknownProcessorCompany {
    public static final String TAG = "UnknownProcessorCompany";
    private static final long MILLIS_10_MINUTES = 600000;

    public static void Process(Context mContext, LSCall call, boolean showNotification) {
        //new untagged contact is created, saved, entered in call entry
        LSContact tempContact = new LSContact();
        tempContact.setContactType(LSContact.CONTACT_TYPE_SALES);
        tempContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
        tempContact.setPhoneOne(call.getContactNumber());
        tempContact.setContactName(call.getContactName());
        tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
        tempContact.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
        tempContact.save();
        // Check if type is incoming , outgoing or missed
        if (call.getType().equals(LSCall.CALL_TYPE_INCOMING) && call.getDuration() > 0L) {
            //Incoming with whome Agent have talked
            if (showNotification && call.getBeginTime() + MILLIS_10_MINUTES > Calendar.getInstance().getTimeInMillis()) {
                CallEndTagBoxService.checkShowCallPopupNew(mContext, call.getContactName(), call.getContactNumber());
//                NotificationBuilder.showTagNumberPopup(mContext, call.getContactName(), call.getContactNumber());
            }
            InquiryManager.removeByCall(mContext, call);
            // Call Saved
            call.setContact(tempContact);
            call.setInquiryHandledState(LSCall.INQUIRY_HANDLED);
            call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            call.save();
            MissedCallEventModel mCallEventModel = new MissedCallEventModel(MissedCallEventModel.CALL_TYPE_MISSED);
            TinyBus bus = TinyBus.from(mContext.getApplicationContext());
            bus.post(mCallEventModel);

        } else if (call.getType().equals(LSCall.CALL_TYPE_OUTGOING) && call.getDuration() > 0L) {
            //Outgoing with whome Agent have talked
            if (showNotification && call.getBeginTime() + MILLIS_10_MINUTES > Calendar.getInstance().getTimeInMillis()) {
                Log.d(TAG, "Process: CALL IS NOT OLD ENOUGH: " + call.getContactNumber());
                CallEndTagBoxService.checkShowCallPopupNew(mContext, call.getContactName(), call.getContactNumber());
//                NotificationBuilder.showTagNumberPopup(mContext, call.getContactName(), call.getContactNumber());
            } else {
                Log.d(TAG, "Process: CALL IS VERY OLD: " + call.getContactNumber());
            }
            InquiryManager.removeByCall(mContext, call);
            // Call Saved
            call.setContact(tempContact);
            call.setInquiryHandledState(LSCall.INQUIRY_HANDLED);
            call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            call.save();
            MissedCallEventModel mCallEventModel = new MissedCallEventModel(MissedCallEventModel.CALL_TYPE_MISSED);
            TinyBus bus = TinyBus.from(mContext.getApplicationContext());
            bus.post(mCallEventModel);

        } else if (call.getType().equals(LSCall.CALL_TYPE_OUTGOING)) { //TODO what is this ?
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
            InquiryManager.createOrUpdate(mContext, call);
            call.setInquiryHandledState(LSCall.INQUIRY_NOT_HANDLED);
            call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            call.save();
            MissedCallEventModel mCallEventModel = new MissedCallEventModel(MissedCallEventModel.CALL_TYPE_MISSED);
            TinyBus bus = TinyBus.from(mContext.getApplicationContext());
            bus.post(mCallEventModel);

        } else if (call.getType().equals(LSCall.CALL_TYPE_REJECTED) || call.getType().equals(LSCall.CALL_TYPE_INCOMING) && call.getDuration() == 0L) {
            //Incoming Rejected
            InquiryManager.createOrUpdate(mContext, call);
            call.setInquiryHandledState(LSCall.INQUIRY_NOT_HANDLED);
            call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            call.save();
            MissedCallEventModel mCallEventModel = new MissedCallEventModel(MissedCallEventModel.CALL_TYPE_MISSED);
            TinyBus bus = TinyBus.from(mContext.getApplicationContext());
            bus.post(mCallEventModel);
        }

        UnlabeledContactAddedEventModel mCallEvent = new UnlabeledContactAddedEventModel();
        TinyBus bus = TinyBus.from(mContext.getApplicationContext());
        bus.post(mCallEvent);
        LeadContactAddedEventModel mCallEventLead = new LeadContactAddedEventModel();
        TinyBus busLead = TinyBus.from(mContext.getApplicationContext());
        busLead.post(mCallEventLead);
    }
}
