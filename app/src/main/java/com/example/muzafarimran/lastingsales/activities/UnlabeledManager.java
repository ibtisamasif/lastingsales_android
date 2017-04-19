package com.example.muzafarimran.lastingsales.activities;

import android.content.Context;

import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utilscallprocessing.InquiryManager;

/**
 * Created by ibtisam on 3/21/2017.
 */

class UnlabeledManager {
    public static void convertTo(Context context, LSContact tempContact, String newtype) {

        // from unlabeled to sales
        if (newtype.equals(LSContact.CONTACT_TYPE_SALES)) {

            // from unlabeled to business
        } else if (newtype.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
            InquiryManager.RemoveByContact(context, tempContact);

            // from unlabeled to ignored
        } else if (newtype.equals(LSContact.CONTACT_TYPE_IGNORED)) {
            InquiryManager.RemoveByContact(context, tempContact);

        }
    }
}
