package com.example.muzafarimran.lastingsales.providers;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public class LastingSalesContract {
//    public static final String CONTENT_AUTHORITY = "com.example.muzafarimran.lastingsales";
//    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
//    public static final String PATH_NOTE = "note";
//    public static final String PATH_FOLLOWUP = "followup";
//    public static final String PATH_CONTACT = "contact";
//    public static final String PATH_CALL = "call";

    private LastingSalesContract(){}

    public static class Contact implements BaseColumns {
//        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONTACT).build();
//        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
//                "/" + CONTENT_AUTHORITY + "/" + PATH_CONTACT;
//        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
//                "/" + CONTENT_AUTHORITY + "/" + PATH_CONTACT;

        public static final String TABLE_NAME = "contact";
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
        public static final String COLUMN_NAME_SALES_STATUS = "sales_status";
    }
    public static class Call implements BaseColumns
    {
//        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CALL).build();
//        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
//                "/" + CONTENT_AUTHORITY + "/" + PATH_CALL;
//        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
//                "/" + CONTENT_AUTHORITY + "/" + PATH_CALL;

        public static final String TABLE_NAME = "call";
        public static final String COLUMN_NAME_CONTACT_NUMBER = "contact_number";
        public static final String COLUMN_NAME_CONTACT_ID = "contact_id";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_DURATION = "duration";
        public static final String COLUMN_NAME_BEGIN_TIME = "begin_time";
        public static final String COLUMN_NAME_AUDIO_PATH = "audio_path";
    }
    public static class Note implements BaseColumns
    {
//        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_NOTE).build();
//        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
//                "/" + CONTENT_AUTHORITY + "/" + PATH_NOTE;
//        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
//                "/" + CONTENT_AUTHORITY + "/" + PATH_NOTE;

        public static final String TABLE_NAME = "note";
        public static final String COLUMN_NAME_TEXT = "text";
        public static final String COLUMN_NAME_CONTACT_ID = "contact_id";
        public static final String COLUMN_NAME_CREATED_AT = "created_at";
    }
    public static class Followup implements BaseColumns
    {
//        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FOLLOWUP).build();
//        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
//                "/" + CONTENT_AUTHORITY + "/" + PATH_FOLLOWUP;
//        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
//                "/" + CONTENT_AUTHORITY + "/" + PATH_FOLLOWUP;

        public static final String TABLE_NAME = "followup";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CONTACT_ID = "contact_id";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_CREATED_AT = "created_at";
    }
//    public static class User implements BaseColumns
//    {
//        public static final String TABLE_NAME = "user";
//        public static final String COLUMN_NAME_NAME = "name";
//        public static final String COLUMN_NAME_EMAIL = "email";
//        public static final String COLUMN_NAME_PHONE = "phone";
//        public static final String COLUMN_NAME_PASSWORD = "password";
//        public static final String COLUMN_NAME_TYPE = "type";
//        public static final String COLUMN_NAME_STATUS = "status";
//        public static final String COLUMN_NAME_IMAGE = "image";
//        public static final String COLUMN_NAME_REC_SALESMAN = "rec_salesman";
//        public static final String COLUMN_NAME_REC_MANAGER = "rec_manager";
//        public static final String COLUMN_NAME_CREATED_AT = "created_at";
//        public static final String COLUMN_NAME_UPDATED_AT = "updated_at";
//        public static final String COLUMN_NAME_DELETED_AT = "deleted_at";
//        public static final String COLUMN_NAME_CLIENT_ID = "client_id";
//        public static final String COLUMN_NAME_MANAGER_ID = "manager_id";
//    }
//    public static class UserAnalytics implements BaseColumns
//    {
//        public static final String TABLE_NAME = "useranalytics";
//        public static final String COLUMN_NAME_USER_ID = "user_id";
//        public static final String COLUMN_NAME_INACTIVE_LEADS = "inactive_leads";
//        public static final String COLUMN_NAME_MISSED_INQUIRIES = "missed_inquiries";
//        public static final String COLUMN_NAME_PENDING_PROSPECTS = "pending_prospects";
//        public static final String COLUMN_NAME_FOLLOWUPS_DUE = "followups_due";
//        public static final String COLUMN_NAME_FOLLOWUPS_DONE = "followups_done";
//        public static final String COLUMN_NAME_UNTAGGED_CONTACTS = "untagged_contacts";
//        public static final String COLUMN_NAME_INCOMING_CALL_COUNT = "incoming_call_count";
//        public static final String COLUMN_NAME_INCOMING_CALL_DURATION = "incoming_call_duration";
//        public static final String COLUMN_NAME_OUTGOING_CALL_COUNT = "outgoing_call_count";
//        public static final String COLUMN_NAME_OUTGOING_CALL_DURATION = "outgoing_call_duration";
//        public static final String COLUMN_NAME_AVG_LEAD_RESPONSE_TIME = "avg_lead_response_time";
//        public static final String COLUMN_NAME_MESSAGE_COUNT = "message_count";
//    }
}
