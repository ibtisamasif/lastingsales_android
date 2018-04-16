package com.example.muzafarimran.lastingsales.providers.models;

import android.database.sqlite.SQLiteException;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ahmad on 08-Nov-16.
 */

public class LSDeal extends SugarRecord {
    public static final String TAG = "LSDeal";

    @Ignore
    public static final String DEAL_STATUS_PENDING = "pending";
    @Ignore
    public static final String DEAL_STATUS_CLOSED_WON = "won";
    @Ignore
    public static final String DEAL_STATUS_CLOSED_LOST = "lost";

    @Ignore
    public static final String DEAL_VISIBILITY_STATUS_PUBLIC = "0";
    @Ignore
    public static final String DEAL_VISIBILITY_STATUS_PRIVATE = "1";

    private String name;
    private String serverId;
    private String userId;
    private String createdBy;
    private String companyId;
    private String status;
    private String workflowId;
    private String workflowStageId;
    private String createdAt;
    private Date updatedAt;
    private LSContact contact;
    private String syncStatus;
    private String dynamic;
    private String isPrivate;
    private String value;
    private String currency;
    private String successRate;
    private String successEta;

    public LSDeal() {
    }

    public static List<LSDeal> getDealFromWorkflowStageId(String id) {
        try {
            return LSDeal.findWithQuery(LSDeal.class, "Select * from LS_DEAL where workflow_stage_id = '" + id + "'" + " ORDER BY updated_at DESC");
        } catch (SQLiteException e) {
            return new ArrayList<LSDeal>();
        }
    }

    public static LSDeal getDealFromId(String id) {
        ArrayList<LSDeal> list = null;
        try {
            list = (ArrayList<LSDeal>) LSDeal.find(LSDeal.class, "id = ? ", id);
        } catch (IllegalArgumentException e) {
            return null;
        }
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public static LSDeal getDealFromServerId(String id) {
        ArrayList<LSDeal> list = null;
        try {
            list = (ArrayList<LSDeal>) LSDeal.find(LSDeal.class, "server_id = ? ", id);
        } catch (IllegalArgumentException e) {
            return null;
        }
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LSContact getContact() {
        return contact;
    }

    public void setContact(LSContact contact) {
        this.contact = contact;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getWorkflowStageId() {
        return workflowStageId;
    }

    public void setWorkflowStageId(String workflowStageId) {
        this.workflowStageId = workflowStageId;
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

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getDynamic() {
        return dynamic;
    }

    public void setDynamic(String dynamic) {
        this.dynamic = dynamic;
    }

    public String getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(String isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(String successRate) {
        this.successRate = successRate;
    }

    public String getSuccessEta() {
        return successEta;
    }

    public void setSuccessEta(String successEta) {
        this.successEta = successEta;
    }
}
