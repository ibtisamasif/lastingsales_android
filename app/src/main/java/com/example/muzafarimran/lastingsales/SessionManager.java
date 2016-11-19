package com.example.muzafarimran.lastingsales;

/**
 * Created by ahmad on 15-Feb-16.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class SessionManager {
    static final String KEY_DIALED_NUMBER = "dialedNumberKey";
    static final String KEY_SERVICE_START_TYPE = "service setAlarm type";
    static final String KEY_SIGNED_IN_USER_EMAIL = "email_address";
    static final String KEY_SIGNED_IN_USER_NAME = "user_name";
    static final String KEY_SIGNED_IN_USER_SERVER_ID = "long_id_from_server";

    static final String KEY_LAST_PING_TIME = "last ping time";
    private static final String KEY_LOGIN_TOKEN = "user_login_token";
    private static final String KEY_LOGIN_TIMESTAMP = "user_login_timestamp";
    private static final String KEY_LOGIN_NUMBER = "user_login_number";


    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "ProjectLastingSalesPreffs";

    public static final String KEY_IS_APP_INITIALIZED = "isSDKIniatialized";
    static final String KEY_FEEDBACK_ENABLED = "feedback enabled key";


    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    /**
     * Quick check for SDK has been initialized
     **/
    // Get App shared prefferences state
    public boolean isAPPInitialized() {
        return pref.getBoolean(KEY_IS_APP_INITIALIZED, false);
    }

    public Boolean isUserSignedIn() {
        if (getLoginToken().equals("")) {
            return false;
        }

        if (getLoginTimestamp() == 00L) {
            return false;
        }

        Long oldTimestamp = getLoginTimestamp();
        Long currentTimestamp = Calendar.getInstance().getTimeInMillis();

        Long oldAnd24Hours = oldTimestamp + 86400000;
        if (currentTimestamp > oldAnd24Hours) {
            return false;
        }


        return true;
    }

    public void loginnUser(String token, Long timeStamp, String number) {

        deleteDataIfDifferentUser(number);
        setLoginNumber(number);
        setLoginToken(token);
        setLoginTimestamp(timeStamp);
        editor.commit();
    }

    private void deleteDataIfDifferentUser(String number) {
        String oldUserNumber = getLoginNumber();
        if (!oldUserNumber.equals(number)) {

            deleteAllUserData();

        }

    }

    private void deleteAllUserData() {
        //            TODO implement deleteData method


    }

    /**
     * saves the data of uer in the Shared preffrences which later can be accessed to perform user specific operations
     */
    public void logoutUser() {
        setLoginTimestamp(00L);
        setLoginToken("");
        editor.commit();
    }
/*
    public void initializeAPP(Context _context)
    {
        if (!isAPPInitialized())
        {
            if (TotemConstants.DEBUGGIN_ON)
            {
                if (TotemConstants.SEED_DB_ON)
                {
                    CommunicationManager communicationManager = new CommunicationManager();
                    communicationManager.hardcodeData(_context);
                }
            }
            editor.putBoolean(KEY_IS_APP_INITIALIZED,true);
            editor.commit();
        }
    }
*/


    public void setLoginToken(String loginToken) {
        editor.putString(KEY_LOGIN_TOKEN, loginToken);
        editor.commit();
    }

    public String getLoginToken() {
        return pref.getString(KEY_LOGIN_TOKEN, "");
    }

    public void setLoginNumber(String number) {
        editor.putString(KEY_LOGIN_NUMBER, number);
        editor.commit();
    }

    public String getLoginNumber() {
        return pref.getString(KEY_LOGIN_NUMBER, "");
    }

    public void setLoginTimestamp(Long timestamp) {
        editor.putLong(KEY_LOGIN_TIMESTAMP, timestamp);
        editor.commit();
    }

    public Long getLoginTimestamp() {
        return pref.getLong(KEY_LOGIN_TIMESTAMP, 00l);
    }

}