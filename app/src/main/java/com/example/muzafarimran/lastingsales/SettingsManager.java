package com.example.muzafarimran.lastingsales;

import android.content.Context;
import android.content.SharedPreferences;


public class SettingsManager {
    public static final String TAG = "SettingsManager";

    private static final String KEY_STATE_FLYER = "state_flyer";
    private static final String KEY_STATE_CALL_END_DIALOG = "state_call_end_dialog";
    private static final String KEY_STATE_HOURLY_NOTIFICATION = "state_hourly_notification";
    private static final String KEY_STATE_DEFAULT_LEAD = "state_default_lead";
    private static final String KEY_STATE_PROTECTED_APP = "state_protected_app";

    // Sharedpref file name
    private static final String PREF_NAME = "ProjectLastingSalesPreffsSettings";
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Constructor
    public SettingsManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public Boolean getKeyStateFlyer() {
        return pref.getBoolean(KEY_STATE_FLYER, true);
    }

    public void setKeyStateFlyer(Boolean state) {
        editor.putBoolean(KEY_STATE_FLYER, state);
        editor.commit();
    }

    public Boolean getKeyStateCallEndDialog() {
        return pref.getBoolean(KEY_STATE_CALL_END_DIALOG, true);
    }

    public void setKeyStateCallEndDialog(Boolean state) {
        editor.putBoolean(KEY_STATE_CALL_END_DIALOG, state);
        editor.commit();
    }

    public Boolean getKeyStateHourlyNotification() {
        return pref.getBoolean(KEY_STATE_HOURLY_NOTIFICATION, false);
    }

    public void setKeyStateHourlyNotification(Boolean state) {
        editor.putBoolean(KEY_STATE_HOURLY_NOTIFICATION, state);
        editor.commit();
    }


    public Boolean getKeyStateDefaultLead() {
        return pref.getBoolean(KEY_STATE_DEFAULT_LEAD, false);
    }

    public void setKeyStateDefaultLead(Boolean state) {
        editor.putBoolean(KEY_STATE_DEFAULT_LEAD, state);
        editor.commit();
    }

    public Boolean getKeyStateProtectedApp() {
        return pref.getBoolean(KEY_STATE_PROTECTED_APP, false);
    }

    public void setKeyStateProtectedApp(Boolean state) {
        editor.putBoolean(KEY_STATE_PROTECTED_APP, state);
        editor.commit();
    }


}