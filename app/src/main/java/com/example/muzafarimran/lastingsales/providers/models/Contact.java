package com.example.muzafarimran.lastingsales.providers.models;
import android.annotation.TargetApi;
import android.os.Build;

import java.util.Objects;

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
    private String sales_status;

    public Contact(){}

    // public constructor to create an object.
    public Contact(String name, String email, String type, String phone1, String phone2,
                   String description, String company, String address, String sales_status)
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
        this.updated_at = "current_date";

        this.deleted_at = null;
        this.sales_status = sales_status;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object obj)
    {
        Contact c = (Contact) obj;

        return (
                Objects.equals(c.getId(), this.id) &&
                Objects.equals(c.getName(), this.name) &&
                Objects.equals(c.getEmail(), this.email) &&
                Objects.equals(c.getType(), this.type) &&
                Objects.equals(c.getPhone1(), this.phone1) &&
                Objects.equals(c.getPhone2(), this.phone2) &&
                Objects.equals(c.getCompany(), this.company) &&
                Objects.equals(c.getDescription(), this.description) &&
                Objects.equals(c.getAddress(), this.address) &&
                Objects.equals(c.getCreated_at(), this.created_at) &&
                Objects.equals(c.getUpdated_at(), this.updated_at) &&
                Objects.equals(c.getDeleted_at(), this.deleted_at) &&
                Objects.equals(c.getSales_status(), this.sales_status)
        );
    }

    @Override
    public int hashCode() {
        return this.id +
                (this.name != null ? this.name.hashCode() : 0) +
                (this.email != null ? this.email.hashCode() : 0) +
                (this.type != null ? this.type.hashCode() : 0) +
                (this.phone1 != null ? this.phone1.hashCode() : 0) +
                (this.phone2 != null ? this.phone2.hashCode() : 0) +
                (this.company != null ? this.company.hashCode() : 0) +
                (this.description != null ? this.description.hashCode() : 0) +
                (this.address != null ? this.address.hashCode() : 0) +
                (this.created_at != null ? this.created_at.hashCode() : 0) +
                (this.updated_at != null ? this.updated_at.hashCode() : 0) +
                (this.deleted_at != null ? this.deleted_at.hashCode() : 0) +
                (this.sales_status != null ? this.sales_status.hashCode() : 0)
                ;
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

    public String getSales_status() {
        return sales_status;
    }
}
