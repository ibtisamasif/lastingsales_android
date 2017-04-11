package com.example.muzafarimran.lastingsales.providers.models;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ibtisam on 4/5/2017.
 */

public class LSDynamicColumns extends SugarRecord {

    @Ignore
    public static final String COLUMN_TYPE_TEXT = "text";
    @Ignore
    public static final String COLUMN_TYPE_NUMBER = "number";
    @Ignore
    public static final String COLUMN_TYPE_SINGLE = "single";

    private String serverId;
    private String columnType;
    private String name;
    private String defaultValueOption;
    private String range;
    private String created_by;
    private String updated_by;
    private String created_at;
    private String updated_at;
    private String companyId;

    public LSDynamicColumns() {
    }

    public static LSDynamicColumns getColumnFromServerId(String id) {
        ArrayList<LSDynamicColumns> list = null;
        try {
            list = (ArrayList<LSDynamicColumns>) LSDynamicColumns.find(LSDynamicColumns.class, "server_id = ? ", id);
        } catch (IllegalArgumentException e) {
            return null;
        }
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public static List<LSDynamicColumns> getAllColumns() {
        ArrayList<LSDynamicColumns> list = null;
        try {
            list = (ArrayList<LSDynamicColumns>) LSDynamicColumns.listAll(LSDynamicColumns.class);
        } catch (IllegalArgumentException e) {
            return null;
        }
        return list;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultValueOption() {
        return defaultValueOption;
    }

    public void setDefaultValueOption(String defaultValueOption) {
        this.defaultValueOption = defaultValueOption;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}
