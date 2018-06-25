package com.example.muzafarimran.lastingsales.utilscallprocessing;

import android.content.Context;

import com.example.muzafarimran.lastingsales.SettingsManager;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

/**
 * Created by ibtisam on 3/4/2017.
 */

class CallProcessor {

    public static void Process(Context mContext, LSCall call, boolean showNotification) {
        SettingsManager settingsManager = new SettingsManager(mContext);
        if (settingsManager.getKeyStateIsCompanyPhone()) { // COMPANY PHONE
            LSContact personalContactCheck = LSContact.getContactFromNumber(call.getContactNumber());
            if (personalContactCheck != null) {
                // Lead
                UnlabeledProcessor.Process(mContext, call, showNotification);
            }
            else {
                // new call
                UnknownProcessor.Process(mContext, call, showNotification);
            }
        }else{ // PERSONAL PHONE

            LSContact personalContactCheck = LSContact.getContactFromNumber(call.getContactNumber());
            if (personalContactCheck != null) {
                // Lead
                UnlabeledProcessor.Process(mContext, call, showNotification);
            }
            else {
                // new call
                UnknownProcessor.Process(mContext, call, showNotification);
            }
        }
    }
}


