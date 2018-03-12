package com.example.muzafarimran.lastingsales.utilscallprocessing;

import android.content.Context;

import com.example.muzafarimran.lastingsales.app.MixpanelConfig;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.util.List;

/**
 * Created by ibtisam on 2/15/2018.
 */

public class DeleteManager {
    public static void deleteContact(Context context, LSContact selectedContact) {
        //Flushing Inquiries Of lead
        InquiryManager.removeByContact(context, selectedContact);

        //Flushing Notes Of lead
        List<LSNote> allNotesOfThisContact = LSNote.getNotesByContactId(selectedContact.getId());
        if (allNotesOfThisContact != null && allNotesOfThisContact.size() > 0) {
            for (LSNote oneNote : allNotesOfThisContact) {
                oneNote.delete();
            }
        }
        //Flushing Followup Of lead
        List<TempFollowUp> allFollowupsOfThisContact = TempFollowUp.getFollowupsByContactId(selectedContact.getId());
        if (allFollowupsOfThisContact != null && allFollowupsOfThisContact.size() > 0) {
            for (TempFollowUp oneFollowup : allFollowupsOfThisContact) {
                oneFollowup.delete();
            }
        }
        //contact is deleted and will be hard deleted on syncing.
        selectedContact.setLeadDeleted(true);
        selectedContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_DELETE_NOT_SYNCED);
        selectedContact.save();
        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(context);
        dataSenderAsync.run();

        if (selectedContact.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
            MixpanelAPI.getInstance(context, MixpanelConfig.projectToken).track("Lead deleted");
        } else if (selectedContact.getContactType().equals(LSContact.CONTACT_TYPE_BUSINESS)) {
            MixpanelAPI.getInstance(context, MixpanelConfig.projectToken).track("Colleague deleted");
        } else if (selectedContact.getContactType().equals(LSContact.CONTACT_TYPE_IGNORED)) {
            MixpanelAPI.getInstance(context, MixpanelConfig.projectToken).track("Ignored deleted");
        }
    }
}
