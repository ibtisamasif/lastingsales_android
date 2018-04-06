package com.example.muzafarimran.lastingsales.providers.models;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ibtisam on 3/29/2018.
 */

public class LSWorkflow extends SugarRecord {

    private String serverId;
    private String name;
    private String status;
    private String created_by;
    private String updated_by;
    private Date updated_at;
    private String companyId;
    private String isDefault;

    public LSWorkflow() {
    }

    public static LSWorkflow getDefaultWorkflow() {
        ArrayList<LSWorkflow> list = null;
        try {
            list = (ArrayList<LSWorkflow>) LSWorkflow.find(LSWorkflow.class, "is_default = ? ", "1");
        } catch (IllegalArgumentException e) {
            return null;
        }
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public static LSWorkflow getWorkflowFromServerId(String id) {
        ArrayList<LSWorkflow> list = null;
        try {
            list = (ArrayList<LSWorkflow>) LSWorkflow.find(LSWorkflow.class, "server_id = ? ", id);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }
}
