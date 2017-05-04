package com.example.muzafarimran.lastingsales.activities;

import android.content.Context;

import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utilscallprocessing.InquiryManager;

/**
 * Created by ibtisam on 3/21/2017.
 */

class LeadManager {
    public static void convertTo(Context context, LSContact tempContact, String newtype) {

        // from sales to ignored
        if(newtype.equals(LSContact.CONTACT_TYPE_IGNORED)){

            InquiryManager.RemoveByContact(context,tempContact);
            if (tempContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED) || tempContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_UPDATE_SYNCED)) {
                tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
            }
            tempContact.save();

            // from sales to business
        }else if(newtype.equals(LSContact.CONTACT_TYPE_BUSINESS)){

            InquiryManager.RemoveByContact(context,tempContact);
            if (tempContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED) || tempContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_UPDATE_SYNCED)) {
                tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
            }
            tempContact.save();

            // from sales to unlabeled
        }else if(newtype.equals(LSContact.CONTACT_TYPE_UNLABELED)){
        }
    }
}
