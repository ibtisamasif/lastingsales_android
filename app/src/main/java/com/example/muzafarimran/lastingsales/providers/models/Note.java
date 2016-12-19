package com.example.muzafarimran.lastingsales.providers.models;
@Deprecated
public class Note {
    private int id;
    private String text;
    private int contact_id;
    private String created_at;

    // public constructor to create an object.
    public Note(String text, int contact_id) {
        //TODO: Assign id here too
        this.text = text;
        this.contact_id = contact_id;
        //TODO: find current date here
        this.created_at = "current_date";
    }

    // getters for private member variables
    public int getId() {
        return id;
    }

    // setters for private member variables
    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
}