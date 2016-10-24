package com.example.muzafarimran.lastingsales.providers.models;

public class UserAnalytics {
    private int id;
    private int user_id;
    private int inactive_leads;
    private int missed_inquiries;
    private int pending_prospects;
    private int followups_due;
    private int followups_done;
    private int untagged_contacts;
    private int incoming_call_count;
    private int incoming_call_duration;
    private int outgoing_call_count;
    private int outgoing_call_duration;
    private String avg_lead_response_time;
    private int message_count;

    // public constructor to create an object.
    public UserAnalytics(int user_id, int inactive_leads, int missed_inquiries, int pending_prospects,
                         int followups_due, int followups_done, int untagged_contacts,
                         int incoming_call_count, int incoming_call_duration, int outgoing_call_count,
                         int outgoing_call_duration, String avg_lead_response_time, int message_count)
    {
        //TODO: Assign id here too
        this.user_id = user_id;
        this.inactive_leads = inactive_leads;
        this.missed_inquiries = missed_inquiries;
        this.pending_prospects = pending_prospects;
        this.followups_due = followups_due;
        this.followups_done = followups_done;
        this.untagged_contacts = untagged_contacts;
        this.incoming_call_count = incoming_call_count;
        this.incoming_call_duration = incoming_call_duration;
        this.outgoing_call_count = outgoing_call_count;
        this.outgoing_call_duration = outgoing_call_duration;
        this.avg_lead_response_time = avg_lead_response_time;
        this.message_count = message_count;
    }

    // setters for private member variables
    public void setId(int id) {
        this.id = id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setInactive_leads(int inactive_leads) {
        this.inactive_leads = inactive_leads;
    }

    public void setMissed_inquiries(int missed_inquiries) {
        this.missed_inquiries = missed_inquiries;
    }

    public void setPending_prospects(int pending_prospects) {
        this.pending_prospects = pending_prospects;
    }

    public void setFollowups_due(int followups_due) {
        this.followups_due = followups_due;
    }

    public void setFollowups_done(int followups_done) {
        this.followups_done = followups_done;
    }

    public void setUntagged_contacts(int untagged_contacts) {
        this.untagged_contacts = untagged_contacts;
    }

    public void setIncoming_call_count(int incoming_call_count) {
        this.incoming_call_count = incoming_call_count;
    }

    public void setIncoming_call_duration(int incoming_call_duration) {
        this.incoming_call_duration = incoming_call_duration;
    }

    public void setOutgoing_call_count(int outgoing_call_count) {
        this.outgoing_call_count = outgoing_call_count;
    }

    public void setOutgoing_call_duration(int outgoing_call_duration) {
        this.outgoing_call_duration = outgoing_call_duration;
    }

    public void setAvg_lead_response_time(String avg_lead_response_time) {
        this.avg_lead_response_time = avg_lead_response_time;
    }

    public void setMessage_count(int message_count) {
        this.message_count = message_count;
    }

    // getters for private member variables
    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getInactive_leads() {
        return inactive_leads;
    }

    public int getMissed_inquiries() {
        return missed_inquiries;
    }

    public int getPending_prospects() {
        return pending_prospects;
    }

    public int getFollowups_due() {
        return followups_due;
    }

    public int getFollowups_done() {
        return followups_done;
    }

    public int getUntagged_contacts() {
        return untagged_contacts;
    }

    public int getIncoming_call_count() {
        return incoming_call_count;
    }

    public int getIncoming_call_duration() {
        return incoming_call_duration;
    }

    public int getOutgoing_call_count() {
        return outgoing_call_count;
    }

    public int getOutgoing_call_duration() {
        return outgoing_call_duration;
    }

    public String getAvg_lead_response_time() {
        return avg_lead_response_time;
    }

    public int getMessage_count() {
        return message_count;
    }
}

