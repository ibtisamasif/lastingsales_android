package com.example.muzafarimran.lastingsales.utils;

import android.content.Context;

import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.utilscallprocessing.InquiryManager;

/**
 * Created by ibtisam on 3/21/2017.
 */

public class LeadManager {
    public static void convertTo(Context context, LSContact tempContact, String newtype) {

        // from sales to ignored
        if (newtype.equals(LSContact.CONTACT_TYPE_IGNORED)) {
            InquiryManager.removeByContact(context, tempContact);

            // from sales to business
        } else if (newtype.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
            InquiryManager.removeByContact(context, tempContact);

            // from sales to unlabeled
        } else if (newtype.equals(LSContact.CONTACT_TYPE_UNLABELED)) {

        }
    }
}
