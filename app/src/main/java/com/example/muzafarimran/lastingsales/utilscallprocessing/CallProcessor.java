package com.example.muzafarimran.lastingsales.utilscallprocessing;

import android.content.Context;

import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.orm.dsl.Ignore;

/**
 * Created by ibtisam on 3/4/2017.
 */

class CallProcessor {

    public static void Process(Context mContext, LSCall call, boolean showNotification) {

        LSContact personalContactCheck = LSContact.getContactFromNumber(call.getContactNumber());
        // Check the category of call i.e UnLabeled , Lead or Ignored
        if (personalContactCheck != null && personalContactCheck.getContactType().equals(LSContact.CONTACT_TYPE_UNLABELED)) {
            // Unlabeled

            UnlabeledProcessor.Process(mContext, call, showNotification);

        } else if (personalContactCheck != null && personalContactCheck.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
            // Lead

            LeadProcessor.Process(mContext, call, showNotification);

        } else if (personalContactCheck != null && personalContactCheck.getContactType().equals(LSContact.CONTACT_TYPE_BUSINESS)) {
            // Business Contact
            BusinessProcessor.Process(mContext, call);

        } else if (personalContactCheck != null && personalContactCheck.getContactType().equals(LSContact.CONTACT_TYPE_IGNORED)) {
            // Contact is in Ignored list. Do Nothing
            // TODO reminder. No ignored processor hence contact updatedAt will not be updated upon call.
        } else {
            // new call
            UnknownProcessor.Process(mContext, call, showNotification);
        }
    }
}


