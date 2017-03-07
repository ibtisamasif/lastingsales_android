package com.example.muzafarimran.lastingsales.utilscallprocessing;

import android.util.Log;

import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;

import java.util.Calendar;

/**
 * Created by ibtisam on 3/4/2017.
 */

class InquiryManager {
    public static final String TAG = "InquiryManager";

    public static void Remove(LSCall call) {
        LSInquiry tempInquiry = LSInquiry.getPendingInquiryByNumberIfExists(call.getContactNumber());
        if (tempInquiry != null && tempInquiry.getAverageResponseTime() <= 0) {
            Calendar now = Calendar.getInstance();
            tempInquiry.setAverageResponseTime(now.getTimeInMillis() - tempInquiry.getBeginTime());
            tempInquiry.setStatus(LSInquiry.INQUIRY_STATUS_ATTENDED);
            if (tempInquiry.getSyncStatus().equals(SyncStatus.SYNC_STATUS_INQUIRY_PENDING_SYNCED)) {
                tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_ATTENDED_NOT_SYNCED);
            } else { // TODO USELESS REMOVE IT
                tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_PENDING_NOT_SYNCED);
            }
            tempInquiry.save();
        }
    }

    public static void CreateOrUpdate(LSCall call) {
        LSInquiry tempInquiry = LSInquiry.getPendingInquiryByNumberIfExists(call.getContactNumber());
        if (tempInquiry != null) {
            tempInquiry.setCountOfInquiries(tempInquiry.getCountOfInquiries() + 1);
//            tempInquiry.setBeginTime(start.getTime());
//            tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_PENDING_NOT_SYNCED);
            tempInquiry.save();
            Log.d(TAG, "onMissedCall: getCountOfInquiries: " + tempInquiry.getCountOfInquiries());
            Log.d(TAG, "onMissedCall: tempInquiry :" + tempInquiry.toString());
        } else {
//                Toast.makeText(mContext, "Doesnt Exist", Toast.LENGTH_SHORT).show();
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
            Log.d(TAG, "onMissedCall: newInquiry: " + newInquiry.toString());
        }

    }
}
