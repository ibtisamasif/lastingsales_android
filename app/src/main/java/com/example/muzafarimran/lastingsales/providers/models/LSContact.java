package com.example.muzafarimran.lastingsales.providers.models;

import android.annotation.TargetApi;
import android.database.sqlite.SQLiteException;
import android.os.Build;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * Created by ahmad on 08-Nov-16.
 */

public class LSContact extends SugarRecord {

    @Ignore
    public static final String SALES_STATUS_PROSTPECT = "open_prostpect";
    @Ignore
    public static final String SALES_STATUS_LEAD = "open_lead";
    @Ignore
    public static final String SALES_STATUS_CLOSED_WON = "closed_won";
    @Ignore
    public static final String SALES_STATUS_CLOSED_LOST = "closed_lost";
    @Ignore
    public static final String CONTACT_TYPE_SALES = "type_sales";
    @Ignore
    public static final String CONTACT_TYPE_COLLEAGUE = "type_colleague";
    @Ignore
    public static final String CONTACT_TYPE_PERSONAL = "type_personal";
//    @Ignore
//    public static final String CONTACT_TYPE_NONE = "type_none";

    //    private int contactId;
    private String contactName;
    private String contactEmail;
    private String contactType;
    private String phoneOne;
    private String phoneTwo;
    private String contactDescription;
    private String contactCompany;
    private String contactAddress;
    private String contactCreated_at;
    private String contactUpdated_at;
    private String contactDeleted_at;
    private String contactSalesStatus;
    private boolean detailsDropDownOpen;

    public LSContact() {
    }

    public LSContact(String contactName, String contactEmail, String contactType, String phoneOne, String phoneTwo, String contactDescription, String contactCompany, String contactAddress) {
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        this.contactType = contactType;
        this.phoneOne = phoneOne;
        this.phoneTwo = phoneTwo;
        this.contactDescription = contactDescription;
        this.contactCompany = contactCompany;
        this.contactAddress = contactAddress;
    }

    public static List<LSContact> getContactsByType(String type) {
        try {
            return LSContact.find(LSContact.class, "contact_type = ? ", type);
        } catch (SQLiteException e) {
            return new ArrayList<LSContact>();
        }
    }

    public static List<LSContact> getContactsByLeadSalesStatus(String leadType) {
        try {
            return LSContact.find(LSContact.class, "contact_sales_status = ? ", leadType);
        } catch (SQLiteException e) {
            return new ArrayList<LSContact>();
        }
    }

    public static List<LSContact> getAllInactiveLeadContacts() {
        try { //TODO check weather below steps are required or not.
            ArrayList<LSContact> allColleagues = (ArrayList<LSContact>) LSContact.getContactsByType(LSContact.CONTACT_TYPE_COLLEAGUE);
            ArrayList<LSContact> allLeads = (ArrayList<LSContact>) LSContact.getContactsByLeadSalesStatus(SALES_STATUS_LEAD);
            allLeads.removeAll(allColleagues);
            ArrayList<LSContact> allInactiveLeads = new ArrayList<LSContact>();
//            long milisecondsIn3Days = 259200000;
            long milliSecondsIn1Min = 30000; // 30 seconds for now
            long now = Calendar.getInstance().getTimeInMillis();
            long threeDaysAgoTimestamp = now - milliSecondsIn1Min;
            for (LSContact oneContact : allLeads) {
                ArrayList<LSCall> allCallsOfThisLead = LSCall.getCallsFromNumber(oneContact.getPhoneOne());
                LSContact inactiveLead = null;
                if (allCallsOfThisLead != null && allCallsOfThisLead.size() > 0) {
                    for (LSCall oneCall : allCallsOfThisLead) {
                        if (oneCall.getBeginTime() > threeDaysAgoTimestamp) {
                            inactiveLead = null;
                            break;
                        } else {
                            inactiveLead = oneContact;
                        }
                    }
                    if (inactiveLead != null) {
                        allInactiveLeads.add(inactiveLead);
                    }
                }
            }
            return allInactiveLeads;
        } catch (SQLiteException e) {
            return new ArrayList<LSContact>();
        }
    }

    public static List<LSContact> getAllNotesContacts() {
        try {
            ArrayList<LSContact> contactsAllHavingNotes = new ArrayList<LSContact>();
            ArrayList<LSContact> contactsColleagues = (ArrayList<LSContact>)LSContact.getContactsByType(LSContact.CONTACT_TYPE_COLLEAGUE);
            ArrayList<LSContact> contactsSales = (ArrayList<LSContact>)LSContact.getContactsByType(LSContact.CONTACT_TYPE_SALES);
            ArrayList<LSContact> contacts = new ArrayList<LSContact>();
            contacts = contactsSales;
            contacts.addAll(contactsColleagues);
            for (LSContact oneContact : contacts){
                List<LSNote> allNotesOfThisContact = LSNote.getNotesByContactId(oneContact.getId());
                if(allNotesOfThisContact!=null && allNotesOfThisContact.size()>0){
                    contactsAllHavingNotes.add(oneContact);
                }
            }
            return contactsAllHavingNotes;
        } catch (SQLiteException e) {
            return new ArrayList<LSContact>();
        }
    }

    @Deprecated
    public static List<LSContact> getAllPendingProspectsContacts() {
        try {

            ArrayList<LSContact> allProspects = (ArrayList<LSContact>) LSContact.getContactsByLeadSalesStatus(SALES_STATUS_PROSTPECT);
            ArrayList<LSContact> allNonPendingProspects = new ArrayList<LSContact>();
            long secondsThresholdValue = 10;

            for (LSContact oneContact : allProspects) {
                ArrayList<LSCall> allCallsOfThisProspect = LSCall.getCallsFromNumber(oneContact.getPhoneOne());
                if (allCallsOfThisProspect != null && allCallsOfThisProspect.size() > 0) {
                    for (LSCall oneCall : allCallsOfThisProspect) {
                        if (oneCall.getDuration() > secondsThresholdValue) {
                            allNonPendingProspects.add(oneContact);
                            break;
                        }
                    }
                }
            }
            allProspects.removeAll(allNonPendingProspects);
            return allProspects;
        } catch (SQLiteException e) {
            return new ArrayList<LSContact>();
        }
    }
    public static LSContact getContactFromNumber(String number) {
        ArrayList<LSContact> list = null;
        try {
            list = (ArrayList<LSContact>) LSContact.find(LSContact.class, "phone_one = ? ", number);
        } catch (IllegalArgumentException e) {
            return null;
        }
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public ArrayList<TempFollowUp> getAllFollowups() {
        ArrayList<TempFollowUp> allFollowupsOfThisContact = null;
        allFollowupsOfThisContact = (ArrayList<TempFollowUp>) TempFollowUp.find(TempFollowUp.class, "contact = ? ", getId() + "");
        return allFollowupsOfThisContact;
    }

//    public ArrayList<LSNote> getAllNotes() {
//        ArrayList<LSNote> allNotesOfThisContact = null;
//        allNotesOfThisContact = (ArrayList<LSNote>) LSNote.find(LSNote.class, "contact_of_note = ? ", getId() + "");
//        return allNotesOfThisContact;
//    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object obj) {
        LSContact c = (LSContact) obj;
        return (
                Objects.equals(c.getContactName(), this.contactName) &&
                        Objects.equals(c.getContactEmail(), this.contactEmail) &&
                        Objects.equals(c.getContactType(), this.contactType) &&
                        Objects.equals(c.getPhoneOne(), this.phoneOne) &&
                        Objects.equals(c.getPhoneTwo(), this.phoneTwo) &&
                        Objects.equals(c.getContactCompany(), this.contactCompany) &&
                        Objects.equals(c.getContactDescription(), this.contactDescription) &&
                        Objects.equals(c.getContactAddress(), this.contactAddress) &&
                        Objects.equals(c.getContactCreated_at(), this.contactCreated_at) &&
                        Objects.equals(c.getContactUpdated_at(), this.contactUpdated_at) &&
                        Objects.equals(c.getContactDeleted_at(), this.contactDeleted_at) &&
                        Objects.equals(c.getContactSalesStatus(), this.contactSalesStatus)
        );
    }

    @Override
    public int hashCode() {
        return
                (this.contactName != null ? this.contactName.hashCode() : 0) +
                        (this.contactEmail != null ? this.contactEmail.hashCode() : 0) +
                        (this.contactType != null ? this.contactType.hashCode() : 0) +
                        (this.phoneOne != null ? this.phoneOne.hashCode() : 0) +
                        (this.phoneTwo != null ? this.phoneTwo.hashCode() : 0) +
                        (this.contactCompany != null ? this.contactCompany.hashCode() : 0) +
                        (this.contactDescription != null ? this.contactDescription.hashCode() : 0) +
                        (this.contactAddress != null ? this.contactAddress.hashCode() : 0) +
                        (this.contactCreated_at != null ? this.contactCreated_at.hashCode() : 0) +
                        (this.contactUpdated_at != null ? this.contactUpdated_at.hashCode() : 0) +
                        (this.contactDeleted_at != null ? this.contactDeleted_at.hashCode() : 0) +
                        (this.contactSalesStatus != null ? this.contactSalesStatus.hashCode() : 0)
                ;
    }

    public boolean isDetailsDropDownOpen() {
        return detailsDropDownOpen;
    }

    public void setDetailsDropDownOpen(boolean detailsDropDownOpen) {
        this.detailsDropDownOpen = detailsDropDownOpen;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public String getPhoneOne() {
        return phoneOne;
    }

    public void setPhoneOne(String phoneOne) {
        this.phoneOne = phoneOne;
    }

    public String getPhoneTwo() {
        return phoneTwo;
    }

    public void setPhoneTwo(String phoneTwo) {
        this.phoneTwo = phoneTwo;
    }

    public String getContactDescription() {
        return contactDescription;
    }

    public void setContactDescription(String contactDescription) {
        this.contactDescription = contactDescription;
    }

    public String getContactCompany() {
        return contactCompany;
    }

    public void setContactCompany(String contactCompany) {
        this.contactCompany = contactCompany;
    }

    public String getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public String getContactCreated_at() {
        return contactCreated_at;
    }

    public void setContactCreated_at(String contactCreated_at) {
        this.contactCreated_at = contactCreated_at;
    }

    public String getContactUpdated_at() {
        return contactUpdated_at;
    }

    public void setContactUpdated_at(String contactUpdated_at) {
        this.contactUpdated_at = contactUpdated_at;
    }

    public String getContactDeleted_at() {
        return contactDeleted_at;
    }

    public void setContactDeleted_at(String contactDeleted_at) {
        this.contactDeleted_at = contactDeleted_at;
    }

    public String getContactSalesStatus() {
        return contactSalesStatus;
    }

    public void setContactSalesStatus(String contactSalesStatus) {
        this.contactSalesStatus = contactSalesStatus;
    }
}