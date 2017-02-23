package com.example.muzafarimran.lastingsales.providers.models;
@Deprecated
public class Followup {
    private int id;
    private String title;
    private int contact_id;
    private String time;
    private String created_at;

    // public constructor to create an object.
    Followup(String title, int contact_id, String time) {
        this.title = title;
        this.contact_id = contact_id;
        this.created_at = "current_date";
        this.time = time;
    }

    // getters for private member variables
    public int getId() {
        return id;
    }

    // setters for private member variables
    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getContact_id() {
        return contact_id;
    }

    public void setContact_id(int contact_id) {
        this.contact_id = contact_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}