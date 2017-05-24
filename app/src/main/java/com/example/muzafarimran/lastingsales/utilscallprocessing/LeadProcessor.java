package com.example.muzafarimran.lastingsales.utilscallprocessing;

import android.content.Context;
import android.util.Log;

import com.example.muzafarimran.lastingsales.events.MissedCallEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.NotificationBuilder;

import java.util.Calendar;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 3/4/2017.
 */

public class LeadProcessor {
    public static final String TAG = "LeadProcessor";

    public static void Process(Context mContext, LSCall call, boolean showNotification) {
        Log.d(TAG, "LeadProcessor: Process() Entered");
        LSContact contact = LSContact.getContactFromNumber(call.getContactNumber());
        if(contact != null){
            contact.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
            contact.save();
        }
        // Check if type is incoming , outgoing or missed
        if (call.getType().equals(LSCall.CALL_TYPE_INCOMING) && call.getDuration() > 0L) {
            //Incoming
            if(showNotification) {
                NotificationBuilder.showTagNumberPopup(mContext, call.getContactName(), call.getContactNumber());
            }
            call.setInquiryHandledState(LSCall.INQUIRY_HANDLED);
            InquiryManager.Remove(mContext, call);
            call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            call.save();
            MissedCallEventModel mCallEventModel = new MissedCallEventModel(MissedCallEventModel.CALL_TYPE_MISSED);
            TinyBus bus = TinyBus.from(mContext.getApplicationContext());
            bus.post(mCallEventModel);

        }else if (call.getType().equals(LSCall.CALL_TYPE_OUTGOING))  {
            //Outgoing
            if(showNotification) {
                NotificationBuilder.showTagNumberPopup(mContext, call.getContactName(), call.getContactNumber());
            }
            call.setInquiryHandledState(LSCall.INQUIRY_HANDLED);
            InquiryManager.Remove(mContext, call);
            call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            call.save();
            MissedCallEventModel mCallEventModel = new MissedCallEventModel(MissedCallEventModel.CALL_TYPE_MISSED);
            TinyBus bus = TinyBus.from(mContext.getApplicationContext());
            bus.post(mCallEventModel);

        }else if (call.getType().equals(LSCall.CALL_TYPE_UNANSWERED)) {
            //Outgoing Unanswered
            call.setInquiryHandledState(LSCall.INQUIRY_NOT_HANDLED);
            call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            call.save();

        } else if (call.getType().equals(LSCall.CALL_TYPE_MISSED)){
            //Missed
            InquiryManager.CreateOrUpdate(call);
            call.setInquiryHandledState(LSCall.INQUIRY_NOT_HANDLED);
            call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            call.save();
            MissedCallEventModel mCallEventModel = new MissedCallEventModel(MissedCallEventModel.CALL_TYPE_MISSED);
            TinyBus bus = TinyBus.from(mContext.getApplicationContext());
            bus.post(mCallEventModel);
        } else if (call.getType().equals(LSCall.CALL_TYPE_REJECTED)|| call.getType().equals(LSCall.CALL_TYPE_INCOMING) && call.getDuration() == 0L) {
            //Incoming Rejected
            InquiryManager.CreateOrUpdate(call);
            call.setInquiryHandledState(LSCall.INQUIRY_NOT_HANDLED);
            call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            call.save();
            MissedCallEventModel mCallEventModel = new MissedCallEventModel(MissedCallEventModel.CALL_TYPE_MISSED);
            TinyBus bus = TinyBus.from(mContext.getApplicationContext());
            bus.post(mCallEventModel);
        }
    }
}
