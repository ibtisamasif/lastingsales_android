package com.example.muzafarimran.lastingsales.utilscallprocessing;

import android.content.Context;
import com.example.muzafarimran.lastingsales.events.MissedCallEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.CallEndTagBoxService;

import java.util.Calendar;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 3/4/2017.
 */

public class UnlabeledProcessor {

    public static void Process(Context mContext, LSCall call, boolean showNotification) {
        LSContact contact = LSContact.getContactFromNumber(call.getContactNumber());
        if(contact != null){
            contact.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
            contact.save();
        }
        // Check if type is incoming , outgoing or missed
        if (call.getType().equals(LSCall.CALL_TYPE_INCOMING) && call.getDuration() > 0L) {
            //Incoming
            if(showNotification) {
                CallEndTagBoxService.checkShowCallPopupNew(mContext, call.getContactName(), call.getContactNumber());
//                NotificationBuilder.showTagNumberPopup(mContext, call.getContactName(), call.getContactNumber());
            }
            call.setInquiryHandledState(LSCall.INQUIRY_HANDLED);
            InquiryManager.remove(mContext, call);
            call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            call.save();
            MissedCallEventModel mCallEventModel = new MissedCallEventModel(MissedCallEventModel.CALL_TYPE_MISSED);
            TinyBus bus = TinyBus.from(mContext.getApplicationContext());
            bus.post(mCallEventModel);

        } else if (call.getType().equals(LSCall.CALL_TYPE_OUTGOING)) {
            //Outgoing
            if(showNotification) {
                CallEndTagBoxService.checkShowCallPopupNew(mContext, call.getContactName(), call.getContactNumber());
//                NotificationBuilder.showTagNumberPopup(mContext, call.getContactName(), call.getContactNumber());
            }
            call.setInquiryHandledState(LSCall.INQUIRY_HANDLED);
            InquiryManager.remove(mContext, call);
            call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            call.save();
            MissedCallEventModel mCallEventModel = new MissedCallEventModel(MissedCallEventModel.CALL_TYPE_MISSED);
            TinyBus bus = TinyBus.from(mContext.getApplicationContext());
            bus.post(mCallEventModel);

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

        } else if (call.getType().equals(LSCall.CALL_TYPE_REJECTED)|| call.getType().equals(LSCall.CALL_TYPE_INCOMING) && call.getDuration() == 0L) {
            //Rejected
            InquiryManager.createOrUpdate(mContext, call);
            call.setInquiryHandledState(LSCall.INQUIRY_NOT_HANDLED);
            call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            call.save();
            MissedCallEventModel mCallEventModel = new MissedCallEventModel(MissedCallEventModel.CALL_TYPE_MISSED);
            TinyBus bus = TinyBus.from(mContext.getApplicationContext());
            bus.post(mCallEventModel);
        }
    }

//    private static void checkShowCallPopupOld(Context ctx, String name, String number) {
//        Log.wtf("testlog", "UnlabeledProcessor checkShowCallPopupNew: ");
//        String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(number);
////        String name = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(ctx, internationalNumber);
//        Intent intent = new Intent(ctx, AddEditLeadService.class);
//        intent.putExtra(TagNotificationDialogActivity.TAG_LAUNCH_MODE_CONTACT_TYPE, LSContact.CONTACT_TYPE_SALES);
//        intent.putExtra(TagNotificationDialogActivity.TAG_LAUNCH_MODE_PHONE_NUMBER, internationalNumber);
//        intent.putExtra(TagNotificationDialogActivity.TAG_LAUNCH_MODE_CONTACT_NAME, name);
//        intent.putExtra(TagNotificationDialogActivity.TAG_LAUNCH_MODE_CONTACT_ID, ""); //backward compatibility
//        ctx.startService(intent);
//    }
}
