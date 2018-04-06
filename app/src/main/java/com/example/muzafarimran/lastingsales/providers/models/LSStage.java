package com.example.muzafarimran.lastingsales.providers.models;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ibtisam on 3/29/2018.
 */

public class LSStage extends SugarRecord {

    private String serverId;
    private String workflowId;
    private String companyId;
    private String name;
    private String description;
    private String createdBy;
    private Date updatedAt;
    private String nextTo;
    private LSWorkflow workflow;

    public LSStage() {
    }

    public static LSStage getFirstStage() {
        ArrayList<LSStage> list = null;
        try {
            list = (ArrayList<LSStage>) LSStage.find(LSStage.class, "next_to = ? ", "0");
        } catch (IllegalArgumentException e) {
            return null;
        }
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public static ArrayList <LSStage> getAllStagesInSequence() {
        ArrayList<LSStage> list = null;
        try {
            list = (ArrayList<LSStage>) LSStage.listAll(LSStage.class);
            return list;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static LSStage getStageByName(String name) {
        ArrayList<LSStage> list = null;
        try {
            list = (ArrayList<LSStage>) LSStage.find(LSStage.class, "name = ? ", name);
        } catch (IllegalArgumentException e) {
            return null;
        }
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public static LSStage getStageFromServerId(String id) {
        ArrayList<LSStage> list = null;
        try {
            list = (ArrayList<LSStage>) LSStage.find(LSStage.class, "server_id = ? ", id);
        } catch (IllegalArgumentException e) {
            return null;
        }
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getNextTo() {
        return nextTo;
    }

    public void setNextTo(String nextTo) {
        this.nextTo = nextTo;
    }

    public LSWorkflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(LSWorkflow workflow) {
        this.workflow = workflow;
    }

    @Override
    public String toString() {
        return "LSStage{" +
                "serverId='" + serverId + '\'' +
                ", workflowId='" + workflowId + '\'' +
                ", companyId='" + companyId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", updatedAt=" + updatedAt +
                ", nextTo='" + nextTo + '\'' +
                ", workflow=" + workflow +
                '}';
    }
}
