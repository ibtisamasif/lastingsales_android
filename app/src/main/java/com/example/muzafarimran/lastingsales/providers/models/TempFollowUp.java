package com.example.muzafarimran.lastingsales.providers.models;

import com.orm.SugarRecord;

/**
 * Created by ahmad on 01-Nov-16.
 */

public class TempFollowUp extends SugarRecord {
    private String title;
    private Long dateTimeForFollowup;
    private LSContact contact;

    public TempFollowUp() {
    }

    public TempFollowUp(String title, Long dateTimeForFollowup, LSContact contactID) {
        this.title = title;
        this.dateTimeForFollowup = dateTimeForFollowup;
        this.contact = contactID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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