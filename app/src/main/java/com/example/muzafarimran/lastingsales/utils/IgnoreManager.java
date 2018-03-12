package com.example.muzafarimran.lastingsales.utils;

import android.content.Context;

import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;

/**
 * Created by ibtisam on 3/21/2017.
 */

class IgnoreManager {
    public static void convertTo(Context context, LSContact tempContact, String newtype) {
        if(newtype.equals(LSContact.CONTACT_TYPE_SALES)){
            tempContact.setContactType(LSContact.CONTACT_TYPE_SALES);
            if (tempContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED) || tempContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_UPDATE_SYNCED)) {
                tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
            }
            tempContact.save();
        }else if(newtype.equals(LSContact.CONTACT_TYPE_BUSINESS)){
            tempContact.setContactType(LSContact.CONTACT_TYPE_BUSINESS);
            if (tempContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED) || tempContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_UPDATE_SYNCED)) {
                tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
            }
            tempContact.save();
        }else if(newtype.equals(LSContact.CONTACT_TYPE_UNLABELED)){
            tempContact.setContactType(LSContact.CONTACT_TYPE_UNLABELED);
            if (tempContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED) || tempContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_UPDATE_SYNCED)) {
                tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
            }
            tempContact.save();
        }
    }
}
