package com.example.muzafarimran.lastingsales;

import android.provider.BaseColumns;


public class LastingSalesContract {
    private LastingSalesContract(){}

    public static class Contact implements BaseColumns
    {
        public static final String TABLE_NAME = "contact" ;
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_PHONE1 = "phone1";
        public static final String COLUMN_NAME_PHONE2 = "phone2";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_COMPANY = "company";
        public static final String COLUMN_NAME_ADDRESS = "address";
        public static final String COLUMN_NAME_CREATED_AT = "created_at";
        public static final String COLUMN_NAME_UPDATED_AT = "updated_at";
        public static final String COLUMN_NAME_DELETED_AT = "deleted_at";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_SALES_STATUS = "sales_status";

    }
    public static class SalesContactDetail implements BaseColumns
    {
        public static final String TABLE_NAME = "salescontactdetail";
        public static final String COLUMN_NAME_CONTACT_ID = "contact_id";
        public static final String COLUMN_NAME_CLOSED_DATE = "closed_date";
        public static final String COLUMN_NAME_CLOSING_REMARKS = "closing_remarks";
    }
    public static class Call implements BaseColumns
    {
        public static final String TABLE_NAME = "call";
        public static final String COLUMN_NAME_CONTACT_NUMBER = "contact_number";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_CONTACT_ID = "contact_id";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_DURATION = "duration";
        public static final String COLUMN_NAME_BEGIN_TIME = "begin_time";
        public static final String COLUMN_NAME_AUDIO_PATH = "audio_path";
    }
    public static class Note implements BaseColumns
    {
        public static final String TABLE_NAME = "note";
        public static final String COLUMN_NAME_TEXT = "text";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_CONTACT_ID = "contact_id";
        public static final String COLUMN_NAME_CREATED_AT = "created_at";
    }
    public static class Followup implements BaseColumns
    {
        public static final String TABLE_NAME = "followup";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_CONTACT_ID = "contact_id";
        public static final String COLUMN_NAME_TIME = "time";
    }
    public static class User implements BaseColumns
    {
        public static final String TABLE_NAME = "user";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_PHONE = "phone";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_REC_SALESMAN = "rec_salesman";
        public static final String COLUMN_NAME_REC_MANAGER = "rec_manager";
        public static final String COLUMN_NAME_CREATED_AT = "created_at";
        public static final String COLUMN_NAME_UPDATED_AT = "updated_at";
        public static final String COLUMN_NAME_DELETED_AT = "deleted_at";
        public static final String COLUMN_NAME_CLIENT_ID = "client_id";
        public static final String COLUMN_NAME_MANAGER_ID = "manager_id";
    }
    public static class UserAnalytics implements BaseColumns
    {
        public static final String TABLE_NAME = "useranalytics";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_INACTIVE_LEADS = "inactive_leads";
        public static final String COLUMN_NAME_MISSED_INQUIRIES = "missed_inquiries";
        public static final String COLUMN_NAME_PENDING_PROSPECTS = "pending_prospects";
        public static final String COLUMN_NAME_FOLLOWUPS_DUE = "followups_due";
        public static final String COLUMN_NAME_FOLLOWUPS_DONE = "followups_done";
        public static final String COLUMN_NAME_UNTAGGED_CONTACTS = "untagged_contacts";
        public static final String COLUMN_NAME_INCOMING_CALL_COUNT = "incoming_call_count";
        public static final String COLUMN_NAME_INCOMING_CALL_DURATION = "incoming_call_duration";
        public static final String COLUMN_NAME_OUTGOING_CALL_COUNT = "outgoing_call_count";
        public static final String COLUMN_NAME_OUTGOING_CALL_DURATION = "outgoing_call_duration";
        public static final String COLUMN_NAME_AVG_LEAD_RESPONSE_TIME = "avg_lead_response_time";
        public static final String COLUMN_NAME_MESSAGE_COUNT = "message_count";
    }
}
