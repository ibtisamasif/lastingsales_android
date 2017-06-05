package com.example.muzafarimran.lastingsales.utilscallprocessing;

import android.content.Context;

import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.app.MixpanelConfig;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.util.Calendar;

/**
 * Created by ibtisam on 3/4/2017.
 */

public class InquiryManager {
    public static final String TAG = "InquiryManager";

    public static void RemoveByContact(Context context, LSContact tempContact) {
        //update inquiry as well if exists
        LSInquiry tempInquiry = LSInquiry.getInquiryByNumberIfExists(tempContact.getPhoneOne());
        if (tempInquiry != null) {
            tempInquiry.setContact(tempContact);
            tempInquiry.save();
            tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_DELETE_NOT_SYNCED);
            tempInquiry.save();
            DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(context);
            dataSenderAsync.run();
        }
    }

    public static void Remove(Context context, LSCall call) {
        LSInquiry tempInquiry = LSInquiry.getPendingInquiryByNumberIfExists(call.getContactNumber());
        if (tempInquiry != null && tempInquiry.getAverageResponseTime() <= 0) {
            Calendar now = Calendar.getInstance();
            tempInquiry.setAverageResponseTime(now.getTimeInMillis() - tempInquiry.getBeginTime());
            tempInquiry.setStatus(LSInquiry.INQUIRY_STATUS_ATTENDED);
            if (tempInquiry.getSyncStatus().equals(SyncStatus.SYNC_STATUS_INQUIRY_PENDING_SYNCED)) {
                tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_ATTENDED_NOT_SYNCED);
            } else {
                tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_PENDING_NOT_SYNCED);
            }
            tempInquiry.save();

            String projectToken = MixpanelConfig.projectToken;
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, projectToken);
            mixpanel.track("Inquiry Followed");
        }
    }

    public static void CreateOrUpdate(LSCall call) {
        LSInquiry tempInquiry = LSInquiry.getPendingInquiryByNumberIfExists(call.getContactNumber());
        if (tempInquiry != null) {
            tempInquiry.setCountOfInquiries(tempInquiry.getCountOfInquiries() + 1);
            tempInquiry.save();
        } else {
            LSInquiry newInquiry = new LSInquiry();
            newInquiry.setContactNumber(call.getContactNumber());
            newInquiry.setContactName(call.getContactName());
            newInquiry.setContact(call.getContact());
            newInquiry.setBeginTime(call.getBeginTime());
            newInquiry.setDuration(call.getDuration());
            newInquiry.setCountOfInquiries(1);
            newInquiry.setStatus(LSInquiry.INQUIRY_STATUS_PENDING);
            newInquiry.setAverageResponseTime(0L);
            newInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_PENDING_NOT_SYNCED);
            newInquiry.save();
        }
    }
}
