package com.example.muzafarimran.lastingsales.utilscallprocessing;

import android.content.Context;

import com.example.muzafarimran.lastingsales.SettingsManager;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSIgnoreList;

/**
 * Created by ibtisam on 3/4/2017.
 */

class CallProcessor {

    public static void Process(Context mContext, LSCall call, boolean showNotification) {
        SettingsManager settingsManager = new SettingsManager(mContext);
        if (settingsManager.getKeyStateIsCompanyPhone()) { // COMPANY PHONE

            LSIgnoreList ignoredContactCheck = LSIgnoreList.getContactFromNumber(call.getContactNumber());
            if (ignoredContactCheck != null) {
                // DO NOTHING

            } else {

                // check if contact exists
                LSContact contact = LSContact.getContactFromNumber(call.getContactNumber());
                if (contact != null) {
                    //TODO show after call dialog & save call log

                } else {
                    // new call
                }
            }


        } else { // PERSONAL PHONE

            LSIgnoreList ignoredContactCheck = LSIgnoreList.getContactFromNumber(call.getContactNumber());
            if (ignoredContactCheck != null) {
                // DO NOTHING

            } else {

                // check if contact exists
                LSContact contact = LSContact.getContactFromNumber(call.getContactNumber());
                if (contact != null) {
                    //TODO show after call dialog & save call log

                } else {
                    // new call
                }
            }

        }
    }
}


