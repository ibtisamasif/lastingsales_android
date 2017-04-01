package com.example.muzafarimran.lastingsales.migration;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;

import java.util.List;

/**
 * Created by ibtisam on 3/30/2017.
 */

public class ScriptForIgnoredContacts extends AsyncTask<Object, Void, Void> {
    private static final String TAG = "NaviDrawerActivity";
    private final Context context;
    //    private SharedPreferences prefs;
    SessionManager sessionManager;

    public ScriptForIgnoredContacts(Context context) {
        this.context = context;
        sessionManager = new SessionManager(context);
    }

    @Override
    protected Void doInBackground(Object... params) {
        func();
        return null;
    }

    private void func() {
        sessionManager.storeVersionCodeNow();

        Log.d(TAG, "func: Running Script for Ignored Contacts");
        if (sessionManager.getLoginMode().equals(SessionManager.MODE_NORMAL)) {
            Log.d(TAG, "func: case1");
        }else if(sessionManager.getLoginMode().equals(SessionManager.MODE_NEW_INSTALL)){
            Log.d(TAG, "func: case2");
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            //mark all null ignored contacts
            List<LSContact> ignoredNullContacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_IGNORED);
            if (ignoredNullContacts != null) {
                Log.d(TAG, "found: size: " + ignoredNullContacts.size());
                for (LSContact oneContact : ignoredNullContacts) {
                    if(oneContact.getSyncStatus() != null) {
                        if (oneContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED) || oneContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_UPDATE_SYNCED)) {
                            Log.d(TAG, "func: if");
                            oneContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
                            oneContact.setContactName("changed");
                            oneContact.save();
                        } else {
                            Log.d(TAG, "func: else");
                            oneContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                            oneContact.save();
                        }
                    }
                    else {
                        oneContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                        oneContact.save();
                    }
                }
            } else {
                Log.d(TAG, "not found");
            }
        }else if(sessionManager.getLoginMode().equals(SessionManager.MODE_UPGRADE)){
            Log.d(TAG, "func: case3");
        }
    }
}