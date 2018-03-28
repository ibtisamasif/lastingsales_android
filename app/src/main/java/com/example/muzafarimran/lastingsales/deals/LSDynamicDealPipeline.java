package com.example.muzafarimran.lastingsales.deals;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;

/**
 * Created by ibtisam on 3/26/2018.
 */

public class LSDynamicDealPipeline extends SugarRecord {

    private String name;
    private String serverId;
    private String createdBy;
    private String sequence;
    private String companyId;

    public LSDynamicDealPipeline() {
    }

    public ArrayList<LSDynamicDealPipeline> getDealByName(String name){
        try {
            ArrayList<LSDynamicDealPipeline> deals = (ArrayList<LSDynamicDealPipeline>) Select.from(LSDynamicDealPipeline.class).where(Condition.prop("name").eq(name)).list();
            return deals;
        } catch (Exception e) {
            return new ArrayList<LSDynamicDealPipeline>();
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

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    @Override
    public String toString() {
        return "LSDynamicDealPipeline{" +
                "name='" + name + '\'' +
                ", serverId='" + serverId + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", sequence='" + sequence + '\'' +
                ", companyId='" + companyId + '\'' +
                '}';
    }
}
