package com.example.muzafarimran.lastingsales;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LastingSalesDatabaseHelper extends SQLiteOpenHelper
{
    //database logcat tag
    private static final String LOG = "LastingSalesDatabaseHelper";
    //database version number
    private static final int DATABASE_VERSION = 1;
    // database name
    private static final String DATABASE_NAME = "LastingSales.db";

    // sql query to create table user
    private static final String SQL_CREATE_TABLE_USER =
            "CREATE TABLE " + LastingSalesContract.User.TABLE_NAME + " (" +
                    LastingSalesContract.User._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    LastingSalesContract.User.COLUMN_NAME_NAME + " TEXT NOT NULL," +
                    LastingSalesContract.User.COLUMN_NAME_EMAIL + " TEXT UNIQUE," +
                    LastingSalesContract.User.COLUMN_NAME_PHONE + " TEXT NOT NULL UNIQUE," +
                    LastingSalesContract.User.COLUMN_NAME_PASSWORD + " TEXT NOT NULL," +
                    LastingSalesContract.User.COLUMN_NAME_TYPE + " TEXT NOT NULL DEFAULT 'salesman'," +
                    LastingSalesContract.User.COLUMN_NAME_STATUS + " INTEGER NOT NULL DEFAULT 0," +
                    LastingSalesContract.User.COLUMN_NAME_IMAGE + " TEXT," +
                    LastingSalesContract.User.COLUMN_NAME_REC_SALESMAN + " INTEGER NOT NULL DEFAULT 0," +
                    LastingSalesContract.User.COLUMN_NAME_REC_MANAGER + " INTEGER NOT NULL DEFAULT 0," +
                    LastingSalesContract.User.COLUMN_NAME_CREATED_AT + " TEXT NOT NULL," +
                    LastingSalesContract.User.COLUMN_NAME_UPDATED_AT + " TEXT," +
                    LastingSalesContract.User.COLUMN_NAME_DELETED_AT + " TEXT," +
                    LastingSalesContract.User.COLUMN_NAME_CLIENT_ID + " INTEGER NOT NULL," +
                    LastingSalesContract.User.COLUMN_NAME_MANAGER_ID + " INTEGER," +
                    "FOREIGN KEY(" + LastingSalesContract.User.COLUMN_NAME_MANAGER_ID + ") REFERENCES " +
                    LastingSalesContract.User.TABLE_NAME + "(" + LastingSalesContract.User._ID + ")" +
                    " );";

    // sql query to create table useranalytics
    private static final String SQL_CREATE_TABLE_USERANALYTICS =
            "CREATE TABLE " + LastingSalesContract.UserAnalytics.TABLE_NAME + " (" +
                    LastingSalesContract.UserAnalytics._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    LastingSalesContract.UserAnalytics.COLUMN_NAME_USER_ID + " INTEGER NOT NULL," +
                    LastingSalesContract.UserAnalytics.COLUMN_NAME_INACTIVE_LEADS + " INTEGER NOT NULL," +
                    LastingSalesContract.UserAnalytics.COLUMN_NAME_MISSED_INQUIRIES + " INTEGER NOT NULL," +
                    LastingSalesContract.UserAnalytics.COLUMN_NAME_PENDING_PROSPECTS + " INTEGER NOT NULL," +
                    LastingSalesContract.UserAnalytics.COLUMN_NAME_FOLLOWUPS_DUE + " INTEGER NOT NULL," +
                    LastingSalesContract.UserAnalytics.COLUMN_NAME_FOLLOWUPS_DONE + " INTEGER NOT NULL," +
                    LastingSalesContract.UserAnalytics.COLUMN_NAME_UNTAGGED_CONTACTS + " INTEGER NOT NULL," +
                    LastingSalesContract.UserAnalytics.COLUMN_NAME_INCOMING_CALL_COUNT + " INTEGER NOT NULL," +
                    LastingSalesContract.UserAnalytics.COLUMN_NAME_INCOMING_CALL_DURATION + " INTEGER NOT NULL," +
                    LastingSalesContract.UserAnalytics.COLUMN_NAME_OUTGOING_CALL_COUNT + " INTEGER NOT NULL," +
                    LastingSalesContract.UserAnalytics.COLUMN_NAME_OUTGOING_CALL_DURATION + " INTEGER NOT NULL," +
                    LastingSalesContract.UserAnalytics.COLUMN_NAME_AVG_LEAD_RESPONSE_TIME + " TEXT NOT NULL," +
                    LastingSalesContract.UserAnalytics.COLUMN_NAME_MESSAGE_COUNT + " INTEGER NOT NULL," +
                    "FOREIGN KEY(" + LastingSalesContract.UserAnalytics.COLUMN_NAME_USER_ID + ") REFERENCES " +
                    LastingSalesContract.User.TABLE_NAME + "(" + LastingSalesContract.User._ID + ")" +
                    " );";

    // sql query to create table contact
    private static final String SQL_CREATE_TABLE_CONTACT =
            "CREATE TABLE " + LastingSalesContract.Contact.TABLE_NAME + " (" +
                    LastingSalesContract.Contact._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    LastingSalesContract.Contact.COLUMN_NAME_NAME + " TEXT NOT NULL," +
                    LastingSalesContract.Contact.COLUMN_NAME_EMAIL + " TEXT," +
                    LastingSalesContract.Contact.COLUMN_NAME_TYPE + " TEXT," +
                    LastingSalesContract.Contact.COLUMN_NAME_PHONE1 + " TEXT NOT NULL," +
                    LastingSalesContract.Contact.COLUMN_NAME_PHONE2 + " TEXT," +
                    LastingSalesContract.Contact.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    LastingSalesContract.Contact.COLUMN_NAME_COMPANY + " TEXT," +
                    LastingSalesContract.Contact.COLUMN_NAME_ADDRESS + " TEXT," +
                    LastingSalesContract.Contact.COLUMN_NAME_CREATED_AT + " TEXT NOT NULL," +
                    LastingSalesContract.Contact.COLUMN_NAME_UPDATED_AT + " TEXT NOT NULL," +
                    LastingSalesContract.Contact.COLUMN_NAME_DELETED_AT + " TEXT NOT NULL," +
                    LastingSalesContract.Contact.COLUMN_NAME_USER_ID + " INTEGER," +
                    LastingSalesContract.Contact.COLUMN_NAME_SALES_STATUS + " TEXT," +
                    "FOREIGN KEY(" + LastingSalesContract.Contact.COLUMN_NAME_USER_ID + ") REFERENCES " +
                    LastingSalesContract.User.TABLE_NAME + "(" + LastingSalesContract.User._ID + ")" +
                    " );";

    // sql query to create table salescontactdetail
    private static final String SQL_CREATE_TABLE_SALESCONTACTDETAIL =
            "CREATE TABLE " + LastingSalesContract.SalesContactDetail.TABLE_NAME + " (" +
                    LastingSalesContract.SalesContactDetail._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    LastingSalesContract.SalesContactDetail.COLUMN_NAME_CONTACT_ID + " INTEGER NOT NULL," +
                    LastingSalesContract.SalesContactDetail.COLUMN_NAME_CLOSED_DATE + " TEXT," +
                    LastingSalesContract.SalesContactDetail.COLUMN_NAME_CLOSING_REMARKS + " TEXT," +
                    "FOREIGN KEY(" + LastingSalesContract.SalesContactDetail.COLUMN_NAME_CONTACT_ID + ") REFERENCES " +
                    LastingSalesContract.Contact.TABLE_NAME + "(" + LastingSalesContract.Contact._ID + ")" +
                    " );";

    // sql query to create table call
    private static final String SQL_CREATE_TABLE_CALL =
            "CREATE TABLE " + LastingSalesContract.Call.TABLE_NAME + " (" +
                    LastingSalesContract.Call._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    LastingSalesContract.Call.COLUMN_NAME_CONTACT_NUMBER + " TEXT NOT NULL," +
                    LastingSalesContract.Call.COLUMN_NAME_USER_ID + " INTEGER NOT NULL," +
                    LastingSalesContract.Call.COLUMN_NAME_CONTACT_ID + " INTEGER," +
                    LastingSalesContract.Call.COLUMN_NAME_TYPE + " TEXT NOT NULL," +
                    LastingSalesContract.Call.COLUMN_NAME_DURATION + " TEXT NOT NULL," +
                    LastingSalesContract.Call.COLUMN_NAME_BEGIN_TIME + " TEXT NOT NULL," +
                    LastingSalesContract.Call.COLUMN_NAME_AUDIO_PATH + " TEXT," +
                    "FOREIGN KEY(" + LastingSalesContract.Call.COLUMN_NAME_USER_ID + ") REFERENCES " +
                    LastingSalesContract.User.TABLE_NAME + "(" + LastingSalesContract.User._ID + ")," +
                    "FOREIGN KEY(" + LastingSalesContract.Call.COLUMN_NAME_CONTACT_ID + ") REFERENCES " +
                    LastingSalesContract.Contact.TABLE_NAME + "(" + LastingSalesContract.Contact._ID + ")" +
                    " );";

    // sql query to create table note
    private static final String SQL_CREATE_TABLE_NOTE =
            "CREATE TABLE " + LastingSalesContract.Note.TABLE_NAME + " (" +
                    LastingSalesContract.Note._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    LastingSalesContract.Note.COLUMN_NAME_TEXT + " TEXT NOT NULL," +
                    LastingSalesContract.Note.COLUMN_NAME_USER_ID + " INTEGER NOT NULL," +
                    LastingSalesContract.Note.COLUMN_NAME_CONTACT_ID + " INTEGER NOT NULL," +
                    LastingSalesContract.Note.COLUMN_NAME_CREATED_AT + " TEXT NOT NULL," +
                    "FOREIGN KEY(" + LastingSalesContract.Note.COLUMN_NAME_USER_ID + ") REFERENCES " +
                    LastingSalesContract.User.TABLE_NAME + "(" + LastingSalesContract.User._ID + ")," +
                    "FOREIGN KEY(" + LastingSalesContract.Note.COLUMN_NAME_CONTACT_ID + ") REFERENCES " +
                    LastingSalesContract.Contact.TABLE_NAME + "(" + LastingSalesContract.Contact._ID + ")" +
                    " );";

    // sql query to create table followup
    private static final String SQL_CREATE_TABLE_FOLLOWUP =
            "CREATE TABLE " + LastingSalesContract.Followup.TABLE_NAME + " (" +
                    LastingSalesContract.Followup._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    LastingSalesContract.Followup.COLUMN_NAME_TITLE + " TEXT," +
                    LastingSalesContract.Followup.COLUMN_NAME_USER_ID + " INTEGER NOT NULL," +
                    LastingSalesContract.Followup.COLUMN_NAME_CONTACT_ID + " INTEGER NOT NULL," +
                    LastingSalesContract.Followup.COLUMN_NAME_TIME + " TEXT NOT NULL," +
                    "FOREIGN KEY(" + LastingSalesContract.Followup.COLUMN_NAME_USER_ID + ") REFERENCES " +
                    LastingSalesContract.User.TABLE_NAME + "(" + LastingSalesContract.User._ID + ")," +
                    "FOREIGN KEY(" + LastingSalesContract.Followup.COLUMN_NAME_CONTACT_ID + ") REFERENCES " +
                    LastingSalesContract.Contact.TABLE_NAME + "(" + LastingSalesContract.Contact._ID + ")" +
                    " );";



    public LastingSalesDatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // execute create sql queries for all tables here
        db.execSQL(SQL_CREATE_TABLE_USER);
        db.execSQL(SQL_CREATE_TABLE_USERANALYTICS);
        db.execSQL(SQL_CREATE_TABLE_CONTACT);
        db.execSQL(SQL_CREATE_TABLE_SALESCONTACTDETAIL);
        db.execSQL(SQL_CREATE_TABLE_CALL);
        db.execSQL(SQL_CREATE_TABLE_NOTE);
        db.execSQL(SQL_CREATE_TABLE_FOLLOWUP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        String delete_table = "DROP TABLE IF EXISTS ";
        // drop all tables here
        db.execSQL(delete_table + LastingSalesContract.Followup.TABLE_NAME);
        db.execSQL(delete_table + LastingSalesContract.Note.TABLE_NAME);
        db.execSQL(delete_table + LastingSalesContract.Call.TABLE_NAME);
        db.execSQL(delete_table + LastingSalesContract.SalesContactDetail.TABLE_NAME);
        db.execSQL(delete_table + LastingSalesContract.Contact.TABLE_NAME);
        db.execSQL(delete_table + LastingSalesContract.UserAnalytics.TABLE_NAME);
        db.execSQL(delete_table + LastingSalesContract.User.TABLE_NAME);

        // create new tables here
        onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onUpgrade(db, oldVersion, newVersion);
    }
}
