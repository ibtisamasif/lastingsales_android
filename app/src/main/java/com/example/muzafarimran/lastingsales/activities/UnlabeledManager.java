package com.example.muzafarimran.lastingsales.activities;

import android.content.Context;
import android.util.Log;

import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.MixpanelConfig;
import com.example.muzafarimran.lastingsales.utilscallprocessing.InquiryManager;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ibtisam on 3/21/2017.
 */

class UnlabeledManager {
    public static void convertTo(Context context, LSContact tempContact, String newtype) {

        if (newtype.equals(LSContact.CONTACT_TYPE_SALES)) {
            // from unlabeled to sales
            String projectToken = MixpanelConfig.projectToken;
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, projectToken);
            mixpanel.track("Unlabeled To Lead");
        } else if (newtype.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
            // from unlabeled to business
            InquiryManager.RemoveByContact(context, tempContact);
            String projectToken = MixpanelConfig.projectToken;
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, projectToken);
            mixpanel.track("Unlabeled To Colleague");
        } else if (newtype.equals(LSContact.CONTACT_TYPE_IGNORED)) {
            // from unlabeled to ignored
            InquiryManager.RemoveByContact(context, tempContact);
            String projectToken = MixpanelConfig.projectToken;
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, projectToken);
            mixpanel.track("Unlabeled To Ignored");
        }
    }
}
