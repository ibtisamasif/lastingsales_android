package com.example.muzafarimran.lastingsales;

/**
 * Created by ahmad on 15-Feb-16.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSCallRecording;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;

import java.util.Calendar;


public class SessionManager {
    public static final String TAG = "SessionManager";
    public static final String KEY_IS_APP_INITIALIZED = "isSDKIniatialized";
    static final String KEY_FEEDBACK_ENABLED = "feedback enabled key";
    private static final String KEY_LOGIN_ID = "user_login_id";
    private static final String KEY_LOGIN_TOKEN = "user_login_token";
    private static final String KEY_LOGIN_TIMESTAMP = "user_login_timestamp";
    private static final String KEY_LOGIN_NUMBER = "user_login_number";
    private static final String KEY_LOGIN_FIRSTNAME = "user_login_firstname";
    private static final String KEY_LOGIN_LASTNAME = "user_login_lastname";
    private static final String KEY_LOGIN_IMAGEPATH = "user_login_imagepath";
    private static final String KEY_LOGIN_FIREBASE_REG_ID = "user_login_firebase_reg_id";


    // Sharedpref file name
    private static final String PREF_NAME = "ProjectLastingSalesPreffs";
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
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
        Long oldAnd24Hours = oldTimestamp + 15552000000L; //Six months expiry
        if (currentTimestamp > oldAnd24Hours) {
            return false;
        }
        return true;
    }

    public void loginnUser(String userId, String token, Long timeStamp, String number, String firstName, String lastName, String imagePath) {
        Log.d(TAG, "loginnUser: ");
        deleteDataIfDifferentUser(number);
        fetchData();
        setLoginNumber(number);
        setLoginToken(token);
        setLoginTimestamp(timeStamp);
        setKeyLoginId(userId);
        setKeyLoginFirstName(firstName);
        setKeyLoginLastName(lastName);
        setKeyLoginImagePath(imagePath);
        editor.commit();
    }

    private void fetchData() {
        // TODO need to implement
        AgentDataFetchAsync agentDataFetchAsync = new AgentDataFetchAsync(_context);
        agentDataFetchAsync.execute();
    }

    private void deleteDataIfDifferentUser(String number) {
        String oldUserNumber = getLoginNumber();
        if (!oldUserNumber.equals(number)) {
            deleteAllUserData();
        }
    }

    private void deleteAllUserData() {
        Log.d(TAG, "Different Agent: deleting data...");
        if (LSInquiry.count(LSInquiry.class) > 0) {
            LSInquiry.deleteAll(LSInquiry.class);
        }
        if (LSCall.count(LSCall.class) > 0) {
            LSInquiry.deleteAll(LSCall.class);
        }
        if (LSCallRecording.count(LSCallRecording.class) > 0) {
            LSCallRecording.deleteAll(LSCallRecording.class);
        }
        if (LSContact.count(LSContact.class) > 0) {
            LSContact.deleteAll(LSContact.class);
        }
        if (LSNote.count(LSNote.class) > 0) {
            LSNote.deleteAll(LSNote.class);
        }
        if (TempFollowUp.count(TempFollowUp.class) > 0) {
            TempFollowUp.deleteAll(TempFollowUp.class);
        }
    }

    /**
     * saves the data of user in the Shared preffrences which later can be accessed to perform user specific operations
     */
    public void logoutUser() {
        deleteAllUserData();
        setLoginTimestamp(00L);
        setLoginToken("");
        setKeyLoginFirstName("");
        setKeyLoginLastName("");
        setKeyLoginImagePath("");
        editor.commit();
    }

    public String getLoginToken() {
        return pref.getString(KEY_LOGIN_TOKEN, "");
    }

    public void setLoginToken(String loginToken) {
        editor.putString(KEY_LOGIN_TOKEN, loginToken);
        editor.commit();
    }

    public String getLoginNumber() {
        return pref.getString(KEY_LOGIN_NUMBER, "");
    }

    public void setLoginNumber(String number) {
        editor.putString(KEY_LOGIN_NUMBER, number);
        editor.commit();
    }

    public Long getLoginTimestamp() {
        return pref.getLong(KEY_LOGIN_TIMESTAMP, 00l);
    }

    public void setLoginTimestamp(Long timestamp) {
        editor.putLong(KEY_LOGIN_TIMESTAMP, timestamp);
        editor.commit();
    }

    public String getKeyLoginFirstName() {
        return pref.getString(KEY_LOGIN_FIRSTNAME, "");
    }

    public void setKeyLoginFirstName(String name) {
        editor.putString(KEY_LOGIN_FIRSTNAME, name);
        editor.commit();
    }

    public String getKeyLoginLastName() {
        return pref.getString(KEY_LOGIN_LASTNAME, "");
    }

    public void setKeyLoginLastName(String name) {
        editor.putString(KEY_LOGIN_LASTNAME, name);
        editor.commit();
    }

    public String getKeyLoginImagePath() {
        return pref.getString(KEY_LOGIN_IMAGEPATH, "");
    }

    public void setKeyLoginImagePath(String path) {
        editor.putString(KEY_LOGIN_IMAGEPATH, path);
        editor.commit();
    }

    public String getKeyLoginId() {
        return pref.getString(KEY_LOGIN_ID, "");
    }

    public void setKeyLoginId(String path) {
        editor.putString(KEY_LOGIN_ID, path);
        editor.commit();
    }

    public String getKeyLoginFirebaseRegId() {
        return pref.getString(KEY_LOGIN_FIREBASE_REG_ID, "");
    }

    public void setKeyLoginFirebaseRegId(String token) {
        editor.putString(KEY_LOGIN_FIREBASE_REG_ID, token);
        editor.commit();
    }
}