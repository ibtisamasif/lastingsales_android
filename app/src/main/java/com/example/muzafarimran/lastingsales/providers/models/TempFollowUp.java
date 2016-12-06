package com.example.muzafarimran.lastingsales.providers.models;

import com.orm.SugarRecord;

/**
 * Created by ahmad on 01-Nov-16.
 */

public class TempFollowUp extends SugarRecord {
    private String note;
    private Long dateTimeForFollowup;
    private LSContact contact;

    public TempFollowUp() {
    }

    public TempFollowUp(String note, Long dateTimeForFollowup, LSContact contactID) {
        this.note = note;
        this.dateTimeForFollowup = dateTimeForFollowup;
        this.contact = contactID;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getDateTimeForFollowup() {
        return dateTimeForFollowup;
    }

    public void setDateTimeForFollowup(Long dateTimeForFollowup) {
        this.dateTimeForFollowup = dateTimeForFollowup;
    }

    public LSContact getContact() {
        return contact;
    }

    public void setContact(LSContact contactID) {
        this.contact = contactID;
    }
}