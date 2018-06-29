package com.example.muzafarimran.lastingsales.providers.models;

import com.orm.SugarRecord;

public class LSOrganization extends SugarRecord {


    String ID;
    String name;
    String email;
    String phone;
    String address;
    String createdAt;
    String updatedAt;
    String status;


    String userId;
    String followUpDate;
    String followUpDescription;
    String dynamicValues;
    String companyId;
    String image;
    String imagePath;
    String leadType;
    String gender;
    String src;
    String srcId;
    String version;








    public LSOrganization(String ID, String name, String email, String phone, String address, String createdAt, String updatedAt, String status, String userId, String followUpDate, String followUpDescription, String dynamicValues, String companyId, String image, String imagePath, String leadType, String gender, String src, String srcId, String version) {
        this.ID = ID;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
        this.userId = userId;
        this.followUpDate = followUpDate;
        this.followUpDescription = followUpDescription;
        this.dynamicValues = dynamicValues;
        this.companyId = companyId;
        this.image = image;
        this.imagePath = imagePath;
        this.leadType = leadType;
        this.gender = gender;
        this.src = src;
        this.srcId = srcId;
        this.version = version;
    }













    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFollowUpDate() {
        return followUpDate;
    }

    public void setFollowUpDate(String followUpDate) {
        this.followUpDate = followUpDate;
    }

    public String getFollowUpDescription() {
        return followUpDescription;
    }

    public void setFollowUpDescription(String followUpDescription) {
        this.followUpDescription = followUpDescription;
    }

    public String getDynamicValues() {
        return dynamicValues;
    }

    public void setDynamicValues(String dynamicValues) {
        this.dynamicValues = dynamicValues;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getLeadType() {
        return leadType;
    }

    public void setLeadType(String leadType) {
        this.leadType = leadType;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getSrcId() {
        return srcId;
    }

    public void setSrcId(String srcId) {
        this.srcId = srcId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LSOrganization() {
    }



}
