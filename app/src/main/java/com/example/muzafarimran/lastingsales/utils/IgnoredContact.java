package com.example.muzafarimran.lastingsales.utils;

import android.content.Context;

import com.example.muzafarimran.lastingsales.app.SyncStatus;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;

import java.util.Calendar;

/**
 * Created by ibtisam on 3/16/2017.
 */

@Deprecated
public class IgnoredContact { //Please try to use this class it now needs to be modified according to dev_i6

    public static void AddAsIgnoredContact(Context context, String contactPhone, String contactName) {
        if (contactPhone != null && contactPhone != "") {
            String intlNum = PhoneNumberAndCallUtils.numberToInterNationalNumber(context, contactPhone);
            LSContact checkContact;
            checkContact = LSContact.getContactFromNumber(intlNum);
            if (checkContact != null) {
                if (checkContact.getContactType().equals(LSContact.CONTACT_TYPE_UNLABELED)) {
                    checkContact.setPhoneOne(contactPhone);
                    checkContact.setContactName(contactName);
                    checkContact.setContactType(LSContact.CONTACT_TYPE_IGNORED);
                    checkContact.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
                    checkContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
                    checkContact.save();
                }
            } else {
                LSContact tempContact = new LSContact();
                tempContact.setPhoneOne(contactPhone);
                tempContact.setContactName(contactName);
                tempContact.setContactType(LSContact.CONTACT_TYPE_IGNORED);
                tempContact.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
                tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                tempContact.save();
            }
        }
        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(context);
        dataSenderAsync.run();
    }
}
