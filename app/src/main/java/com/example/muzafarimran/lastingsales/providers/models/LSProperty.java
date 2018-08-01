package com.example.muzafarimran.lastingsales.providers.models;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

public class LSProperty extends SugarRecord {

    @Ignore
    public static final String STORABLE_TYPE_APP_LEAD = "App\\Lead";
    public static final String STORABLE_TYPE_APP_DEAL = "App\\Deal";
    public static final String STORABLE_TYPE_APP_ORGANIZATION = "App\\Organization";

    private LSContact contactOfProperty;
    private LSOrganization organizationOfProperty;
    private LSDeal dealOfProperty;
    private String userId;
    private String companyId;
    private String columnId;
    private String storableId;
    private String storableType;
    private String value;
    private String createdBy;
    private String updatedBy;
    private String updatedAt;
    private String serverId;
    private String syncStatus;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getStorableId() {
        return storableId;
    }

    public void setStorableId(String storableId) {
        this.storableId = storableId;
    }

    public String getStorableType() {
        return storableType;
    }

    public void setStorableType(String storableType) {
        this.storableType = storableType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUserId() {
        return userId;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public LSContact getContactOfProperty() {
        return contactOfProperty;
    }

    public void setContactOfProperty(LSContact contactOfProperty) {
        this.contactOfProperty = contactOfProperty;
    }

    public LSOrganization getOrganizationOfProperty() {
        return organizationOfProperty;
    }

    public void setOrganizationOfProperty(LSOrganization organizationOfProperty) {
        this.organizationOfProperty = organizationOfProperty;
    }

    public LSDeal getDealOfProperty() {
        return dealOfProperty;
    }

    public void setDealOfProperty(LSDeal dealOfProperty) {
        this.dealOfProperty = dealOfProperty;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }
}
