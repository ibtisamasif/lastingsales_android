package com.example.muzafarimran.lastingsales.providers.models;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Date;

public class LSOrganization extends SugarRecord {

    private String name;
    private String email;
    private String phone;
    private String address;
    private String createdAt;
    private Date updatedAt;
    private String status;

    private String userId;
    private String followUpDate;
    private String followUpDescription;
    private String dynamicValues;
    private String companyId;
    private String image;
    private String imagePath;
    private String leadType;
    private String gender;
    private String src;
    private String srcId;
    private String version;
    private String serverId;
    private String syncStatus;

    public LSOrganization() {
    }

    public static LSOrganization getOrganizationFromServerId(String id) {
        ArrayList<LSOrganization> list = null;
        try {
            list = (ArrayList<LSOrganization>) LSOrganization.find(LSOrganization.class, "server_id = ? ", id);
        } catch (IllegalArgumentException e) {
            return null;
        }
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public ArrayList<LSDeal> getAllDeals() {
        ArrayList<LSDeal> allDealsOfThisContact = null;
        allDealsOfThisContact = (ArrayList<LSDeal>) LSDeal.findWithQuery(LSDeal.class, "Select * from LS_DEAL where organization = '" + getId() + "'" + " ORDER BY updated_at DESC");
        return allDealsOfThisContact;
    }

    public ArrayList<LSNote> getAllNotes() {
        ArrayList<LSNote> allNotesOfThisContact = null;
        allNotesOfThisContact = (ArrayList<LSNote>) LSNote.find(LSNote.class, "organization_of_note = ? ", getId() + "");
        return allNotesOfThisContact;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
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

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
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

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }
}
