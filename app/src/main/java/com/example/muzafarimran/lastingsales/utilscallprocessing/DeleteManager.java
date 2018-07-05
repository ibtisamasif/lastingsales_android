package com.example.muzafarimran.lastingsales.utilscallprocessing;

import android.content.Context;

import com.example.muzafarimran.lastingsales.app.MixpanelConfig;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSDeal;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.providers.models.LSOrganization;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.util.List;

/**
 * Created by ibtisam on 2/15/2018.
 */

public class DeleteManager {

    public static void deleteOrganization(Context context, LSOrganization selectedOrganization) {

//        //Flushing Deals Of Organization
//        List<LSDeal> allDealsOfThisContact = selectedOrganization.getAllDeals();
//        if (allDealsOfThisContact != null && allDealsOfThisContact.size() > 0) {
//            for (LSDeal oneDeal : allDealsOfThisContact) {
//                oneDeal.delete();
//            }
//        }
        //Flushing Notes Of Organization
        List<LSNote> allNotesOfThisContact = LSNote.getNotesByContactId(selectedOrganization.getId());
        if (allNotesOfThisContact != null && allNotesOfThisContact.size() > 0) {
            for (LSNote oneNote : allNotesOfThisContact) {
                oneNote.delete();
            }
        }
        //Flushing Followup Of lead
        List<TempFollowUp> allFollowupsOfThisContact = TempFollowUp.getFollowupsByContactId(selectedOrganization.getId());
        if (allFollowupsOfThisContact != null && allFollowupsOfThisContact.size() > 0) {
            for (TempFollowUp oneFollowup : allFollowupsOfThisContact) {
                oneFollowup.delete();
            }
        }
        //contact is deleted and will be hard deleted on syncing.
        selectedOrganization.setSyncStatus(SyncStatus.SYNC_STATUS_ORGANIZATION_DELETE_NOT_SYNCED);
        selectedOrganization.save();
        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(context);
        dataSenderAsync.run();

//        if (selectedOrganization.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
//            MixpanelAPI.getInstance(context, MixpanelConfig.projectToken).track("Lead deleted");
//        } else if (selectedOrganization.getContactType().equals(LSContact.CONTACT_TYPE_BUSINESS)) {
//            MixpanelAPI.getInstance(context, MixpanelConfig.projectToken).track("Colleague deleted");
//        } else if (selectedOrganization.getContactType().equals(LSContact.CONTACT_TYPE_IGNORED)) {
//            MixpanelAPI.getInstance(context, MixpanelConfig.projectToken).track("Ignored deleted");
//        }
    }

    public static void deleteContact(Context context, LSContact selectedContact) {
        //Flushing Inquiries Of lead
        InquiryManager.removeByContact(context, selectedContact);

        //Flushing Deals Of lead
        List<LSDeal> allDealsOfThisContact = selectedContact.getAllDeals();
        if (allDealsOfThisContact != null && allDealsOfThisContact.size() > 0) {
            for (LSDeal oneDeal : allDealsOfThisContact) {
                oneDeal.delete();
            }
        }
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

    public static void deleteDeal(Context context, LSDeal selectedDeal) {
        //contact is deleted and will be hard deleted on syncing.
        selectedDeal.setSyncStatus(SyncStatus.SYNC_STATUS_DEAL_DELETE_NOT_SYNCED);
        selectedDeal.save();
        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(context);
        dataSenderAsync.run();

//        if (selectedDeal.getStatus().equals(LSDeal.DEAL_STATUS_PENDING)) {
//            MixpanelAPI.getInstance(context, MixpanelConfig.projectToken).track("Pending deal deleted");
//        } else if (selectedDeal.getStatus().equals(LSDeal.DEAL_STATUS_PENDING)) {
//            MixpanelAPI.getInstance(context, MixpanelConfig.projectToken).track("Won deal deleted");
//        } else if (selectedDeal.getStatus().equals(LSDeal.DEAL_STATUS_PENDING)) {
//            MixpanelAPI.getInstance(context, MixpanelConfig.projectToken).track("Lost deal deleted");
//        }
    }
}
