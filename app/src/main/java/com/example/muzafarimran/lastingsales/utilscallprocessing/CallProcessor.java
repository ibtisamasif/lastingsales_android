package com.example.muzafarimran.lastingsales.utilscallprocessing;

import android.content.Context;

import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

/**
 * Created by ibtisam on 3/4/2017.
 */

class CallProcessor {

    public static void Process(Context mContext, LSCall call) {

        LSContact personalContactCheck = LSContact.getContactFromNumber(call.getContactNumber());
        // Check the category of call i.e UnLabeled , Lead or Ignored
        if (personalContactCheck != null && personalContactCheck.getContactType().equals(LSContact.CONTACT_TYPE_UNTAGGED)) {
            // Unlabeled

            UnlabeledProcessor.Process(mContext, call);

        } else if (personalContactCheck != null && personalContactCheck.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
            // Lead

            LeadProcessor.Process(mContext, call);

        } else if (personalContactCheck != null && personalContactCheck.getContactType().equals(LSContact.CONTACT_TYPE_PERSONAL)) {
            // Contact is in Ignored list. Do Nothing

        } else {
            // new call
            UnknownProcessor.Process(mContext, call);
        }
    }
}


