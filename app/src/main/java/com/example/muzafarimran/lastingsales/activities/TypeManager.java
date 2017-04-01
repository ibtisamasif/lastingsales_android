package com.example.muzafarimran.lastingsales.activities;

import android.content.Context;

import com.example.muzafarimran.lastingsales.providers.models.LSContact;

/**
 * Created by ibtisam on 3/21/2017.
 */

public class TypeManager {
    public static void ConvertTo(Context context, LSContact tempContact, String oldType, String newtype) {

        if (oldType.equals(LSContact.CONTACT_TYPE_SALES)) {
            // from sales to any type
            LeadManager.convertTo(context, tempContact, newtype);

        } else if (oldType.equals(LSContact.CONTACT_TYPE_UNLABELED)) {
            // from unlabeled to any type
            UnlabeledManager.convertTo(context, tempContact, newtype);

        } else if (oldType.equals(LSContact.CONTACT_TYPE_IGNORED)) {
            // from ignored to any type
            IgnoreManager.convertTo(context, tempContact, newtype);

        } else if (oldType.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
            // from business to any type
            BusinessManager.convertTo(context, tempContact, newtype);

        }

//    private static void moveToContactDetailScreen(LSContact contact) {
//        Intent detailsActivityIntent = new Intent(mContext, ContactDetailsTabActivity.class);
//        long contactId = contact.getId();
//        detailsActivityIntent.putExtra(ContactDetailsTabActivity.KEY_CONTACT_ID, contactId + "");
//        mContext.startActivity(detailsActivityIntent);
//    }
    }
}
