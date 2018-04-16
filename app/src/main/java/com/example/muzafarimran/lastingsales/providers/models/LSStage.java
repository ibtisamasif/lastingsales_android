package com.example.muzafarimran.lastingsales.providers.models;

import android.database.sqlite.SQLiteException;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ibtisam on 3/29/2018.
 */

public class LSStage extends SugarRecord {

    private String serverId;
    private String workflowId; //TODO workflowServerId;
    private String companyId;
    private String name;
    private String description;
    private String createdBy;
    private Date updatedAt;
    private String nextTo;
    private String position;
    private LSWorkflow workflow;

    public LSStage() {
    }

    public static LSStage getStageByWorkflowServerIdAndPosition(String workflowServerId, String position) {// TODO CHANGE IT
        List<LSStage> list = null;
        try {
            list = Select.from(LSStage.class)
                    .where(Condition.prop("workflow_id").eq(workflowServerId),
                            Condition.prop("position").eq(position))
                    .list();
            if (list.size() > 0) {
                return list.get(0);
            } else {
                return null;
            }
        } catch (SQLiteException | IllegalArgumentException e) {
            return null;
        }
    }

    public static ArrayList<LSStage> getAllStagesInPositionSequenceByWorkflowServerId(String id) {
        ArrayList<LSStage> list = null;
        try {
            list = (ArrayList<LSStage>) LSStage.findWithQuery(LSStage.class, "Select * from LS_STAGE where workflow_id = '" + id + "' order by position ASC");
            return list;
        } catch (IllegalArgumentException e) {
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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
