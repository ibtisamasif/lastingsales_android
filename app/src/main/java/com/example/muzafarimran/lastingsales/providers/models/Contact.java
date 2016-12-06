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
    private String createdAt;
    private String updatedAt;
    private String deletedAt;
    private String salesStatus;

    public Contact() {
    }

    // public constructor to create an object.
    public Contact(String name, String email, String type, String phone1, String phone2,
                   String description, String company, String address, String salesStatus) {
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
        this.createdAt = "current_date";
        this.updatedAt = "current_date";
        this.deletedAt = null;
        this.salesStatus = salesStatus;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object obj) {
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
                        Objects.equals(c.getCreatedAt(), this.createdAt) &&
                        Objects.equals(c.getUpdatedAt(), this.updatedAt) &&
                        Objects.equals(c.getDeletedAt(), this.deletedAt) &&
                        Objects.equals(c.getSalesStatus(), this.salesStatus)
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
                (this.createdAt != null ? this.createdAt.hashCode() : 0) +
                (this.updatedAt != null ? this.updatedAt.hashCode() : 0) +
                (this.deletedAt != null ? this.deletedAt.hashCode() : 0) +
                (this.salesStatus != null ? this.salesStatus.hashCode() : 0)
                ;
    }

    // getters for private member variables
    public int getId() {
        return id;
    }

    // setters for private member variables
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getSalesStatus() {
        return salesStatus;
    }

    public void setSalesStatus(String salesStatus) {
        this.salesStatus = salesStatus;
    }
}