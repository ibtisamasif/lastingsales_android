package com.example.muzafarimran.lastingsales.activities;

import android.content.Context;

import com.example.muzafarimran.lastingsales.providers.models.LSContact;

/**
 * Created by ibtisam on 3/21/2017.
 */

class IgnoreManager {
    public static void convertTo(Context context, LSContact tempContact, String newtype) {
        if(newtype.equals(LSContact.CONTACT_TYPE_SALES)){

        }else if(newtype.equals(LSContact.CONTACT_TYPE_BUSINESS)){

        }else if(newtype.equals(LSContact.CONTACT_TYPE_UNLABELED)){

        }
    }
}
