package com.example.muzafarimran.lastingsales.providers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.muzafarimran.lastingsales.providers.models.Call;
import com.example.muzafarimran.lastingsales.providers.models.Contact;
import com.example.muzafarimran.lastingsales.providers.models.Followup;
import com.example.muzafarimran.lastingsales.providers.models.Note;

import java.util.ArrayList;
import java.util.List;

public class LastingSalesDatabaseHelper extends SQLiteOpenHelper
{
    private static LastingSalesDatabaseHelper dbInstance;
    //database logcat tag
    private static final String LOG = "LastingSalesDatabaseHelper";
    //database version number
    private static final int DATABASE_VERSION = 2;
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
                    LastingSalesContract.Contact.COLUMN_NAME_DELETED_AT + " TEXT," +
                    LastingSalesContract.Contact.COLUMN_NAME_SALES_STATUS + " TEXT" +
                    " );";

    // sql query to create table call
    private static final String SQL_CREATE_TABLE_CALL =
            "CREATE TABLE " + LastingSalesContract.Call.TABLE_NAME + " (" +
                    LastingSalesContract.Call._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    LastingSalesContract.Call.COLUMN_NAME_CONTACT_NUMBER + " TEXT NOT NULL," +
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
                    LastingSalesContract.Followup.COLUMN_NAME_CONTACT_ID + " INTEGER NOT NULL," +
                    LastingSalesContract.Followup.COLUMN_NAME_TIME + " TEXT NOT NULL," +
                    LastingSalesContract.Followup.COLUMN_NAME_CREATED_AT + " TEXT NOT NULL," +
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

    public long createContact(Contact contact)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LastingSalesContract.Contact.COLUMN_NAME_NAME, contact.getName());
        values.put(LastingSalesContract.Contact.COLUMN_NAME_EMAIL, contact.getEmail());
        values.put(LastingSalesContract.Contact.COLUMN_NAME_TYPE, contact.getType());
        values.put(LastingSalesContract.Contact.COLUMN_NAME_PHONE1, contact.getPhone1());
        values.put(LastingSalesContract.Contact.COLUMN_NAME_PHONE2, contact.getPhone2());
        values.put(LastingSalesContract.Contact.COLUMN_NAME_DESCRIPTION, contact.getDescription());
        values.put(LastingSalesContract.Contact.COLUMN_NAME_COMPANY, contact.getCompany());
        values.put(LastingSalesContract.Contact.COLUMN_NAME_ADDRESS, contact.getAddress());
        values.put(LastingSalesContract.Contact.COLUMN_NAME_CREATED_AT, contact.getCreated_at());
        values.put(LastingSalesContract.Contact.COLUMN_NAME_UPDATED_AT, contact.getUpdated_at());
        values.put(LastingSalesContract.Contact.COLUMN_NAME_DELETED_AT, contact.getDeleted_at());
        values.put(LastingSalesContract.Contact.COLUMN_NAME_SALES_STATUS, contact.getSales_status());

        // insert row TODO: check for correct insertion here
        long new_contact_id = db.insert(LastingSalesContract.Contact.TABLE_NAME, null, values);

        return new_contact_id;
    }

    public List<Contact> searchContacts(String name)
    {
        List<Contact> contacts = new ArrayList<Contact>();
        String[] projection = {
                LastingSalesContract.Contact._ID,
                LastingSalesContract.Contact.COLUMN_NAME_NAME,
                LastingSalesContract.Contact.COLUMN_NAME_EMAIL,
                LastingSalesContract.Contact.COLUMN_NAME_TYPE,
                LastingSalesContract.Contact.COLUMN_NAME_PHONE1,
                LastingSalesContract.Contact.COLUMN_NAME_PHONE2,
                LastingSalesContract.Contact.COLUMN_NAME_DESCRIPTION,
                LastingSalesContract.Contact.COLUMN_NAME_COMPANY,
                LastingSalesContract.Contact.COLUMN_NAME_ADDRESS,
                LastingSalesContract.Contact.COLUMN_NAME_SALES_STATUS
        };
        String selection = null;
        String[] selectionArgs = null;
        if(name != null)
        {
            selection = LastingSalesContract.Contact.COLUMN_NAME_NAME + " LIKE ?";
            selectionArgs = new String[1];
            selectionArgs[0] = "%" + name +"%";
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                LastingSalesContract.Contact.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);

        try
        {
            while (cursor.moveToNext()) {
                Contact contact = new Contact();
                contact.setId(cursor.getInt(cursor.getColumnIndex(LastingSalesContract.Contact._ID)));
                contact.setName(cursor.getString(cursor.getColumnIndex(LastingSalesContract.Contact.COLUMN_NAME_NAME)));
                contact.setEmail(cursor.getString(cursor.getColumnIndex(LastingSalesContract.Contact.COLUMN_NAME_EMAIL)));
                contact.setType(cursor.getString(cursor.getColumnIndex(LastingSalesContract.Contact.COLUMN_NAME_TYPE)));
                contact.setPhone1(cursor.getString(cursor.getColumnIndex(LastingSalesContract.Contact.COLUMN_NAME_PHONE1)));
                contact.setPhone2(cursor.getString(cursor.getColumnIndex(LastingSalesContract.Contact.COLUMN_NAME_PHONE2)));
                contact.setDescription(cursor.getString(cursor.getColumnIndex(LastingSalesContract.Contact.COLUMN_NAME_DESCRIPTION)));
                contact.setCompany(cursor.getString(cursor.getColumnIndex(LastingSalesContract.Contact.COLUMN_NAME_COMPANY)));
                contact.setAddress(cursor.getString(cursor.getColumnIndex(LastingSalesContract.Contact.COLUMN_NAME_ADDRESS)));
                contact.setSales_status(cursor.getString(cursor.getColumnIndex(LastingSalesContract.Contact.COLUMN_NAME_SALES_STATUS)));

                contacts.add(contact);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            cursor.close();
        }

        return contacts;
    }

    public void deleteContact(long contact_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = LastingSalesContract.Contact.TABLE_NAME + " = ?";
        String[] selectionArgs = {Long.toString(contact_id)};
        db.delete(
                LastingSalesContract.Contact.TABLE_NAME,
                selection,
                selectionArgs
        );
    }

    public long createFollowup(Followup followup)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LastingSalesContract.Followup.COLUMN_NAME_TITLE, followup.getTitle());
        values.put(LastingSalesContract.Followup.COLUMN_NAME_TIME, followup.getTime());
        values.put(LastingSalesContract.Followup.COLUMN_NAME_CONTACT_ID, followup.getContact_id());
        values.put(LastingSalesContract.Followup.COLUMN_NAME_CREATED_AT, followup.getCreated_at());

        // insert row TODO: check for correct insertion here
        long new_followup_id = db.insert(LastingSalesContract.Followup.TABLE_NAME, null, values);

        return new_followup_id;
    }

    public long createCall(Call call)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LastingSalesContract.Call.COLUMN_NAME_CONTACT_NUMBER, call.getContact_number());
        values.put(LastingSalesContract.Call.COLUMN_NAME_CONTACT_ID, call.getContact_number());
        values.put(LastingSalesContract.Call.COLUMN_NAME_TYPE, call.getType());
        values.put(LastingSalesContract.Call.COLUMN_NAME_DURATION, call.getDuration());
        values.put(LastingSalesContract.Call.COLUMN_NAME_BEGIN_TIME, call.getBegin_time());
        values.put(LastingSalesContract.Call.COLUMN_NAME_AUDIO_PATH, call.getAudio_path());

        // insert row TODO: check for correct insertion here
        long new_call_id = db.insert(LastingSalesContract.Call.TABLE_NAME, null, values);

        return new_call_id;
    }

    public long createNote(Note note)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LastingSalesContract.Note.COLUMN_NAME_TEXT, note.getText());
        values.put(LastingSalesContract.Note.COLUMN_NAME_CONTACT_ID, note.getContact_id());
        values.put(LastingSalesContract.Note.COLUMN_NAME_CREATED_AT, note.getCreated_at());

        // insert row TODO: check for correct insertion here
        long new_note_id = db.insert(LastingSalesContract.Note.TABLE_NAME, null, values);

        return new_note_id;


    }
}
