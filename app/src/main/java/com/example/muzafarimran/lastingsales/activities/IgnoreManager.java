package com.example.muzafarimran.lastingsales.activities;

import android.content.Context;

import com.example.muzafarimran.lastingsales.providers.models.LSContact;

/**
 * Created by ibtisam on 3/21/2017.
 */

class IgnoreManager {
    public static void convertTo(Context context, LSContact tempContact, String newtype) {
        if(newtype.equals(LSContact.CONTACT_TYPE_SALES)){
            tempContact.setContactType(LSContact.CONTACT_TYPE_SALES);
            tempContact.save();
        }else if(newtype.equals(LSContact.CONTACT_TYPE_BUSINESS)){
            tempContact.setContactType(LSContact.CONTACT_TYPE_BUSINESS);
            tempContact.save();
        }else if(newtype.equals(LSContact.CONTACT_TYPE_UNLABELED)){
            tempContact.setContactType(LSContact.CONTACT_TYPE_UNLABELED);
            tempContact.save();
        }
    }
}
