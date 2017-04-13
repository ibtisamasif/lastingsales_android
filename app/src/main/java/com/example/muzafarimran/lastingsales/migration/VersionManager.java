package com.example.muzafarimran.lastingsales.migration;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSDynamicColumns;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;

import java.util.List;

/**
 * Created by ibtisam on 4/1/2017.
 */

public class VersionManager {
    public static final String TAG = "VersionManager";
    private Context mContext;
    private SessionManager sessionManager;

    public VersionManager(Context context) {
        mContext = context;
        sessionManager = new SessionManager(mContext);
    }

    public boolean runMigrations() {
        PackageInfo pInfo = null;
        try {
            pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int version = pInfo.versionCode;
        Log.d(TAG, "func: version: " + version);
        // no return should be out of IF ELSE condition ever :: application might consider migrations successful otherwise
        if (version == 7) {
            sessionManager.storeVersionCodeNow();
            Log.d(TAG, "func: Running Script for Ignored Contacts");
            if (sessionManager.getLoginMode().equals(SessionManager.MODE_NORMAL)) {
                Log.d(TAG, "func: case1");
            } else if (sessionManager.getLoginMode().equals(SessionManager.MODE_NEW_INSTALL)) {
                Log.d(TAG, "func: case2");
                // Do first run stuff here then set 'firstrun' as false
                // using the following line to edit/commit prefs
                //mark all null ignored contacts
                List<LSContact> ignoredNullContacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_IGNORED);
                if (ignoredNullContacts != null) {
                    Log.d(TAG, "found: size: " + ignoredNullContacts.size());
                    for (LSContact oneContact : ignoredNullContacts) {
                        if (oneContact.getSyncStatus() != null) {
                            if (oneContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED) || oneContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_UPDATE_SYNCED)) {
                                Log.d(TAG, "func: if");
                                oneContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
                                oneContact.setContactName("changed");
                                oneContact.save();
                            }
                        } else {
                            oneContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                            oneContact.save();
                        }
                    }
                } else {
                    Log.d(TAG, "not found");
                }
                //mark all Lead contacts
                List<LSContact> leadContacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_SALES);
                if (leadContacts != null) {
                    Log.d(TAG, "found: size: " + leadContacts.size());
                    for (LSContact oneLeadContact : leadContacts) {
                        if (oneLeadContact.getSyncStatus() != null) {
                            if (oneLeadContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED) || oneLeadContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_UPDATE_SYNCED)) {
                                Log.d(TAG, "func: if");
                                oneLeadContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
                                oneLeadContact.setContactName("changed");
                                oneLeadContact.save();
                            }
                        } else {
                            oneLeadContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                            oneLeadContact.save();
                        }
                    }
                } else {
                    Log.d(TAG, "not found");
                }
            } else if (sessionManager.getLoginMode().equals(SessionManager.MODE_UPGRADE)) {
                Log.d(TAG, "func: case3");
            }
            return false;
        } else if (version == 8) {
            try {
                sessionManager.storeVersionCodeNow();
                Log.d(TAG, "func: Running Script for Ignored Contacts");
                if (sessionManager.getLoginMode().equals(SessionManager.MODE_NORMAL)) {
                    Log.d(TAG, "func: case1");
                    return true;
                } else if (sessionManager.getLoginMode().equals(SessionManager.MODE_NEW_INSTALL)) {
                    Log.d(TAG, "func: case2");
                    // Do first run stuff here then set 'firstrun' as false
                    // using the following line to edit/commit prefs
                    //mark all null ignored contacts
                    List<LSContact> ignoredNullContacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_IGNORED);
                    if (ignoredNullContacts != null) {
                        Log.d(TAG, "found: size: " + ignoredNullContacts.size());
                        for (LSContact oneContact : ignoredNullContacts) {
                            if (oneContact.getSyncStatus() != null) {
                                if (oneContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED) || oneContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_UPDATE_SYNCED)) {
                                    Log.d(TAG, "func: if");
                                    oneContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
                                    oneContact.save();
                                }
                            } else {
                                oneContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                                oneContact.save();
                            }
                        }
                    } else {
                        Log.d(TAG, "not found");
                    }
                    return true;
                } else if (sessionManager.getLoginMode().equals(SessionManager.MODE_UPGRADE)) {
                    Log.d(TAG, "func: case3");
                    // Do first run stuff here then set 'firstrun' as false
                    // using the following line to edit/commit prefs
                    //mark all null ignored contacts
                    List<LSContact> ignoredNullContacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_IGNORED);
                    if (ignoredNullContacts != null) {
                        Log.d(TAG, "found: size: " + ignoredNullContacts.size());
                        for (LSContact oneContact : ignoredNullContacts) {
                            if (oneContact.getSyncStatus() != null) {
                                if (oneContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED) || oneContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_UPDATE_SYNCED)) {
                                    Log.d(TAG, "func: if");
                                    oneContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
                                    oneContact.save();
                                }
                            } else {
                                oneContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                                oneContact.save();
                            }
                        }
                    } else {
                        Log.d(TAG, "not found");
                    }
                    return true;
                } else {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        }
        else if (version == 10) {
            try {
                sessionManager.storeVersionCodeNow();
                Log.d(TAG, "func: Running Script for Dynamic Columns");
                if (sessionManager.getLoginMode().equals(SessionManager.MODE_NORMAL)) {
                    Log.d(TAG, "func: case1");
                    return true;
                } else if (sessionManager.getLoginMode().equals(SessionManager.MODE_NEW_INSTALL)) {
                    Log.d(TAG, "func: case2");
                    // Do first run stuff here then set 'firstrun' as false
                    // using the following line to edit/commit prefs
                    return true;
                } else if (sessionManager.getLoginMode().equals(SessionManager.MODE_UPGRADE)) {
                    Log.d(TAG, "func: case3");
                    // Do first run stuff here then set 'firstrun' as false
                    // using the following line to edit/commit prefs
//                    LSDynamicColumns lsDynamicColumns = new LSDynamicColumns();
//                    lsDynamicColumns.setName("Name");
//                    lsDynamicColumns.save();
////                    lsDynamicColumns.delete();
//
//                    LSContact lsContact = new LSContact();
//                    lsContact.setContactName("name");
//                    lsContact.setDynamicValues("dynVal");
//                    lsContact.save();
//                    lsContact.delete();
                    return true;
                } else {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        else {
            return true;
        }
    }
}