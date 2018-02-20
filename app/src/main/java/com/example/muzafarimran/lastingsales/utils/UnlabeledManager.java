package com.example.muzafarimran.lastingsales.utils;

import android.content.Context;

import com.example.muzafarimran.lastingsales.app.MixpanelConfig;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.utilscallprocessing.InquiryManager;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

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
            InquiryManager.removeByContact(context, tempContact);
            String projectToken = MixpanelConfig.projectToken;
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, projectToken);
            mixpanel.track("Unlabeled To Colleague");
        } else if (newtype.equals(LSContact.CONTACT_TYPE_IGNORED)) {
            // from unlabeled to ignored
            InquiryManager.removeByContact(context, tempContact);
            String projectToken = MixpanelConfig.projectToken;
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, projectToken);
            mixpanel.track("Unlabeled To Ignored");
        }
    }
}
