package com.example.muzafarimran.lastingsales.activities;

import android.content.Context;

import com.example.muzafarimran.lastingsales.providers.models.LSContact;

/**
 * Created by ibtisam on 3/21/2017.
 */

public class TypeManager {
// from sales to other types
    public static void ConvertTo(Context context, LSContact tempContact, String oldType, String newtype) {


        if (oldType.equals(LSContact.CONTACT_TYPE_SALES)) {

            LeadManager.convertTo(context, tempContact, newtype);

        } else if (oldType.equals(LSContact.CONTACT_TYPE_UNLABELED)) {

            UnlabeledManager.convertTo(context, tempContact, newtype);

        } else if (oldType.equals(LSContact.CONTACT_TYPE_IGNORED)) {

            IgnoreManager.convertTo(context, tempContact, newtype);

        } else if (oldType.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
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
