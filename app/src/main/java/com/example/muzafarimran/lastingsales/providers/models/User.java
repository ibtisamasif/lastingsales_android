package com.example.muzafarimran.lastingsales.providers.models;

public class User {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String type;
    private int status;
    private String image;
    private int rec_salesman;
    private int rec_manager;
    private String created_at;
    private String updated_at;
    private String deleted_at;
    private int client_id;
    private int manager_id;

    // public constructor to create an object.
    public User(String name, String email, String phone, String password, String type,
                int status, String image, int rec_salesman, int rec_manager, int client_id,
                int manager_id)
    {
        //TODO: Assign id here too
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.type = type;
        this.status = status;
        this.image = image;
        this.rec_salesman = rec_salesman;
        this.rec_manager = rec_manager;
        //TODO: find current date here
        this.created_at = "current_date";
        this.updated_at = null;
        this.deleted_at = null;
        this.client_id = client_id;
        this.manager_id = manager_id;
    }

    // setters for private member variables
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setRec_salesman(int rec_salesman) {
        this.rec_salesman = rec_salesman;
    }

    public void setRec_manager(int rec_manager) {
        this.rec_manager = rec_manager;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public void setManager_id(int manager_id) {
        this.manager_id = manager_id;
    }

    // getters for private member variables
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public String getType() {
        return type;
    }

    public int getStatus() {
        return status;
    }

    public String getImage() {
        return image;
    }

    public int getRec_salesman() {
        return rec_salesman;
    }

    public int getRec_manager() {
        return rec_manager;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public int getClient_id() {
        return client_id;
    }

    public int getManager_id() {
        return manager_id;
    }
}
