package com.example.muzafarimran.lastingsales.model;

public class Followup {
    private int id;
    private String title;
    private int user_id;
    private int contact_id;
    private String created_at;


    // public constructor to create an object.
    Followup(String title, int user_id, int contact_id)
    {
        //TODO: Assign id here too
        this.title = title;
        this.user_id = user_id;
        this.contact_id = contact_id;
        //TODO: find current date here
        this.created_at = "current_date";
    }

    // setters for private member variables
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setContact_id(int contact_id) {
        this.contact_id = contact_id;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    // getters for private member variables
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getContact_id() {
        return contact_id;
    }

    public String getCreated_at() {
        return created_at;
    }
}
