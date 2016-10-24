package com.example.muzafarimran.lastingsales.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LastingSalesDatabaseHelper extends SQLiteOpenHelper
{
    private static LastingSalesDatabaseHelper dbInstance;
    //database logcat tag
    private static final String LOG = "LastingSalesDatabaseHelper";
    //database version number
    private static final int DATABASE_VERSION = 1;
    // database name
    private static final String DATABASE_NAME = "LastingSales.db";


    // private constructor to avoid creation of new objects
    private LastingSalesDatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // method to create or return only one object of the class
    public static synchronized LastingSalesDatabaseHelper getInstance(Context context)
    {
        if (dbInstance == null)
            dbInstance = new LastingSalesDatabaseHelper(context.getApplicationContext());
        return dbInstance;
    }

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
                    LastingSalesContract.Contact.COLUMN_NAME_SALES_STATUS + " TEXT," + ")" +
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
                    "FOREIGN KEY(" + LastingSalesContract.Followup.COLUMN_NAME_CONTACT_ID + ") REFERENCES " +
                    LastingSalesContract.Contact.TABLE_NAME + "(" + LastingSalesContract.Contact._ID + ")" +
                    " );";



    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // execute create sql queries for all tables here
        db.execSQL(SQL_CREATE_TABLE_CONTACT);
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
        db.execSQL(delete_table + LastingSalesContract.Contact.TABLE_NAME);

        // create new tables here
        onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onUpgrade(db, oldVersion, newVersion);
    }
}
