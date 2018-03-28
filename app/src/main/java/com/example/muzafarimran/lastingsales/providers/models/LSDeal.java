package com.example.muzafarimran.lastingsales.providers.models;

import android.database.sqlite.SQLiteException;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.query.Condition;
import com.orm.query.Select;

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

    private String name;
    private String serverId;
    private String userId;
    private String createdBy;
    private String leadId;
    private String companyId;
    private String ascCompanyId;
    private String status;
    private String workflowId;
    private String workflowStageId;
    private String createdAt;
    private Date updatedAt;
    private LSContact contact;

    public LSDeal() {
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

    public ArrayList<LSDeal> getDealByName(String name){
        try {
            ArrayList<LSDeal> deals = (ArrayList<LSDeal>) Select.from(LSDeal.class).where(Condition.prop("name").eq(name)).list();
            return deals;
        } catch (Exception e) {
            return new ArrayList<LSDeal>();
        }
    }


    // used in All leads
    public static List<LSDeal> getDateArrangedDeals() {
        try {
            return LSDeal.findWithQuery(LSDeal.class, "Select * from LS_Deal where (is_lead_deleted = 0 or is_lead_deleted IS NULL) and contact_type = 'type_sales' ORDER BY updated_at DESC");
        } catch (SQLiteException e) {
            return new ArrayList<LSDeal>();
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

    public String getLeadId() {
        return leadId;
    }

    public void setLeadId(String leadId) {
        this.leadId = leadId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getAscCompanyId() {
        return ascCompanyId;
    }

    public void setAscCompanyId(String ascCompanyId) {
        this.ascCompanyId = ascCompanyId;
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

    @Override
    public String toString() {
        return "LSDeal{" +
                "name='" + name + '\'' +
                ", serverId='" + serverId + '\'' +
                ", userId='" + userId + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", leadId='" + leadId + '\'' +
                ", companyId='" + companyId + '\'' +
                ", ascCompanyId='" + ascCompanyId + '\'' +
                ", status='" + status + '\'' +
                ", workflowId='" + workflowId + '\'' +
                ", workflowStageId='" + workflowStageId + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt=" + updatedAt +
                ", contact=" + contact +
                '}';
    }
}
