package com.example.muzafarimran.lastingsales.providers.models;

import com.orm.SugarRecord;

import java.util.ArrayList;

/**
 * Created by ibtisam on 9/22/2017.
 */

public class LSContactProfile extends SugarRecord {
    private static final String TAG = "LSContactProfile";

    private String firstName;
    private String lastName;
    private String dob;
    private String phone;
    private String address;
    private String city;
    private String country;
    private String email;
    private String fb;
    private String linkd;
    private String tweet;
    private String insta;
    private String whatsapp;
    private String company;
    private String work;
    private String social_image;
    private String comp_link;

    public LSContactProfile() {
    }

    public static LSContactProfile getProfileFromNumber(String number) {
        ArrayList<LSContactProfile> list = null;
        try {
            list = (ArrayList<LSContactProfile>) LSContactProfile.find(LSContactProfile.class, "phone = ? ", number);
        } catch (IllegalArgumentException e) {
            return null;
        }
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFb() {
        return fb;
    }

    public void setFb(String fb) {
        this.fb = fb;
    }

    public String getLinkd() {
        return linkd;
    }

    public void setLinkd(String linkd) {
        this.linkd = linkd;
    }

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

    public String getInsta() {
        return insta;
    }

    public void setInsta(String insta) {
        this.insta = insta;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getSocial_image() {
        return social_image;
    }

    public void setSocial_image(String social_image) {
        this.social_image = social_image;
    }

    public String getComp_link() {
        return comp_link;
    }

    public void setComp_link(String comp_link) {
        this.comp_link = comp_link;
    }

    @Override
    public String toString() {
        return "LSContactProfile{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dob='" + dob + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", email='" + email + '\'' +
                ", fb='" + fb + '\'' +
                ", linkd='" + linkd + '\'' +
                ", tweet='" + tweet + '\'' +
                ", insta='" + insta + '\'' +
                ", whatsapp='" + whatsapp + '\'' +
                ", company='" + company + '\'' +
                ", work='" + work + '\'' +
                ", social_image='" + social_image + '\'' +
                ", comp_link='" + comp_link + '\'' +
                '}';
    }


}
