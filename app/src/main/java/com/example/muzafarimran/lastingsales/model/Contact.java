package com.example.muzafarimran.lastingsales.model;

public class Contact {
    private int id;
    private String name;
    private String email;
    private String type;
    private String phone1;
    private String phone2;
    private String description;
    private String company;
    private String address;
    private String created_at;
    private String updated_at;
    private String deleted_at;
    private int user_id;
    private String sales_status;

    // public constructor to create an object.
    public Contact(String name, String email, String type, String phone1, String phone2,
                   String description, String company, String address, int user_id,
                   String sales_status)
    {
        //TODO: Assign id here too
        this.name = name;
        this.email = email;
        this.type = type;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.description = description;
        this.company = company;
        this.address = address;
        //TODO: find current date here
        this.created_at = "current_date";
        this.user_id = user_id;
        this.sales_status = sales_status;
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

    public void setType(String type) {
        this.type = type;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setSales_status(String sales_status) {
        this.sales_status = sales_status;
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

    public String getType() {
        return type;
    }

    public String getPhone1() {
        return phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public String getDescription() {
        return description;
    }

    public String getCompany() {
        return company;
    }

    public String getAddress() {
        return address;
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

    public int getUser_id() {
        return user_id;
    }

    public String getSales_status() {
        return sales_status;
    }
}
