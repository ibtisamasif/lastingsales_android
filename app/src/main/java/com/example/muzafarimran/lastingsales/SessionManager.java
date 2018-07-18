package com.example.muzafarimran.lastingsales;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSContactProfile;
import com.example.muzafarimran.lastingsales.providers.models.LSDeal;
import com.example.muzafarimran.lastingsales.providers.models.LSDynamicColumns;
import com.example.muzafarimran.lastingsales.providers.models.LSIgnoreList;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.providers.models.LSOrganization;
import com.example.muzafarimran.lastingsales.providers.models.LSProperty;
import com.example.muzafarimran.lastingsales.providers.models.LSStage;
import com.example.muzafarimran.lastingsales.providers.models.LSTask;
import com.example.muzafarimran.lastingsales.providers.models.LSWorkflow;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;

import java.util.Calendar;


public class SessionManager {
    public static final String TAG = "SessionManager";
    private static final String KEY_LOGIN_ID = "user_login_id";
    private static final String KEY_LOGIN_TOKEN = "user_login_token";
    private static final String KEY_LOGIN_TIMESTAMP = "user_login_timestamp";
    private static final String KEY_LOGIN_NUMBER = "user_login_number";
    private static final String KEY_LOGIN_FIRSTNAME = "user_login_firstname";
    private static final String KEY_LOGIN_LASTNAME = "user_login_lastname";
    private static final String KEY_LOGIN_IMAGEPATH = "user_login_imagepath";
    private static final String KEY_LOGIN_FIREBASE_REG_ID = "user_login_firebase_reg_id";
    private static final String KEY_LOGIN_EMAIL = "user_login_email";
    private static final String KEY_LOGIN_COMPANY_ID = "user_login_company_id";
    private static final String KEY_LOGIN_COMPANY_NAME = "user_login_company_name";
    private static final String KEY_LOGIN_ROLE_ID = "user_login_role_id";
    private static final String KEY_LOGIN_ROLE_NAME = "user_login_role_name";

    public static final String KEY_INIT_COMPLETED = "init_completed";
    public static final String KEY_INIT_TEAM_ADDED = "init_team_added";
    public static final String KEY_INIT_APP_DOWNLOADED = "init_app_downloaded";
    public static final String KEY_INIT_COMPANY_CREATED = "init_company_created";
    public static final String KEY_INIT_ACCOUNT_TYPE_SELECTED = "init_account_type_selected";

    private static final String KEY_LOGIN_MODE = "user_login_mode";
    public static final String MODE_NORMAL = "user_login_normal";
    public static final String MODE_NEW_INSTALL = "user_login_new_install";
    public static final String MODE_UPGRADE = "user_login_upgrade";

    public static final String LAST_APP_VISIT = "last_app_visit";

    public static final String FIRST_RUN_AFTER_LOGIN = "firstrun";

    public static final String KEY_IS_TRIAL_VALID = "is_trial_valid";

    public static final String KEY_IS_USER_ACTIVE = "is_user_active";

    public static final String KEY_IS_COMPANY_ACTIVE = "is_company_active";

    public static final String KEY_IS_COMPANY_PAYING = "is_company_paying";

    public static final String KEY_CAN_SYNC = "can_sync";

    public static final String KEY_UPDATE_AVAILABLE_VERSION = "update_available_version";

    public static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";





    // Sharedpref file name
    private static final String PREF_NAME = "ProjectLastingSalesPreffs";
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
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



    public  boolean getIsFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH,false);


    }


    public void storeVersionCodeNow() {

        final String PREFS_NAME = PREF_NAME;
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = 0;
        try {
            currentVersionCode = _context.getPackageManager().getPackageInfo(_context.getPackageName(), 0).versionCode;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            // handle exception
            e.printStackTrace();
        }

        // Get saved version code
        int savedVersionCode = pref.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {
            // This is just a normal run
            Log.d(TAG, "storeVersionCodeNow: NormalRun = 1");
            setLoginMode(SessionManager.MODE_NORMAL);

        } else if (savedVersionCode == DOESNT_EXIST) {
            Log.d(TAG, "storeVersionCodeNow: NewInstall = 2");
            setLoginMode(SessionManager.MODE_NEW_INSTALL);

        } else if (currentVersionCode > savedVersionCode) {
            Log.d(TAG, "storeVersionCodeNow: Upgrade = 3");
            setLoginMode(SessionManager.MODE_UPGRADE);
        }
        // Update the shared preferences with the current version code
        editor.putInt(PREF_VERSION_CODE_KEY, currentVersionCode);
        editor.commit();
    }

    public Boolean isFirstRunAfterLogin() {
        if (getReadyForFirstRun()) {
            setReadyForFirstRun(false);
            return true;
        } else {
            return false;
        }
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

    public void loginnUser(String userId, String token, Long timeStamp, String number, String firstName, String lastName, String imagePath, String email, String company_id, String company_name, String role_id, String role_role) {
        Log.d(TAG, "loginnUser: ");
        deleteDataIfDifferentUser(number);
        setLoginNumber(number);
        setLoginToken(token);
        setLoginTimestamp(timeStamp);
        setKeyLoginId(userId);
        setKeyLoginFirstName(firstName);
        setKeyLoginLastName(lastName);
        setKeyLoginImagePath(imagePath);
        setKeyLoginEmail(email);
        setKeyLoginCompanyId(company_id);
        setKeyLoginCompanyName(company_name);
        setKeyLoginRoleId(role_id);
        setKeyLoginRoleName(role_role);
        editor.commit();
    }

//    public void fetchData() {
//        if( isUserSignedIn()){
//            AgentDataFetchAsync agentDataFetchAsync = new AgentDataFetchAsync(_context);
//            agentDataFetchAsync.execute();
//        }
//        else {
//            Log.d(TAG, "fetchData: USER IS NOT SIGNED IN");
//        }
//    }

    private void deleteDataIfDifferentUser(String number) {
        String oldUserNumber = getLoginNumber();
        if (!oldUserNumber.equals(number)) {
            deleteAllUserData();
        }
    }

    public void deleteAllUserData() {
        Log.d(TAG, "Different Agent: deleting data...");
        if (LSInquiry.count(LSInquiry.class) > 0) {
            LSInquiry.deleteAll(LSInquiry.class);
        }
        if (LSCall.count(LSCall.class) > 0) {
            LSCall.deleteAll(LSCall.class);
        }
//        if (LSCallRecording.count(LSCallRecording.class) > 0) {
//            LSCallRecording.deleteAll(LSCallRecording.class);
//        }
        if (LSContact.count(LSContact.class) > 0) {
            LSContact.deleteAll(LSContact.class);
        }

        if (LSContactProfile.count(LSContactProfile.class) > 0) {
            LSContactProfile.deleteAll(LSContactProfile.class);
        }

        if (LSNote.count(LSNote.class) > 0) {
            LSNote.deleteAll(LSNote.class);
        }
        if (TempFollowUp.count(TempFollowUp.class) > 0) {
            TempFollowUp.deleteAll(TempFollowUp.class);
        }
        if (LSDynamicColumns.count(LSDynamicColumns.class) > 0) {
            LSDynamicColumns.deleteAll(LSDynamicColumns.class);
        }
        if (LSDeal.count(LSDeal.class) > 0) {
            LSDeal.deleteAll(LSDeal.class);
        }
        if (LSWorkflow.count(LSWorkflow.class) > 0) {
            LSWorkflow.deleteAll(LSWorkflow.class);
        }
        if (LSStage.count(LSStage.class) > 0) {
            LSStage.deleteAll(LSStage.class);
        }
        if (LSTask.count(LSTask.class) > 0) {
            LSTask.deleteAll(LSTask.class);
        }
        if (LSOrganization.count(LSOrganization.class) > 0) {
            LSOrganization.deleteAll(LSOrganization.class);
        }
        if (LSIgnoreList.count(LSIgnoreList.class) > 0) {
            LSIgnoreList.deleteAll(LSIgnoreList.class);
        }
        if (LSProperty.count(LSProperty.class) > 0) {
            LSProperty.deleteAll(LSProperty.class);
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

        setFirstTimeLaunch(false);
        setKeyLoginImagePath("");
        setReadyForFirstRun(true);

        //
        new SettingsManager(_context).trashData();

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

    public String getLoginMode() {
        return pref.getString(KEY_LOGIN_MODE, "");
    }

    public void setLoginMode(String mode) {
        editor.putString(KEY_LOGIN_MODE, mode);
        editor.commit();
    }

    public String getKeyLoginEmail() {
        return pref.getString(KEY_LOGIN_EMAIL, "");
    }

    public void setKeyLoginEmail(String email) {
        editor.putString(KEY_LOGIN_EMAIL, email);
        editor.commit();
    }

    public String getKeyLoginCompanyId() {
        return pref.getString(KEY_LOGIN_COMPANY_ID, "");
    }

    public void setKeyLoginCompanyId(String id) {
        editor.putString(KEY_LOGIN_COMPANY_ID, id);
        editor.commit();
    }

    public String getKeyLoginCompanyName() {
        return pref.getString(KEY_LOGIN_COMPANY_NAME, "");
    }

    public void setKeyLoginCompanyName(String name) {
        editor.putString(KEY_LOGIN_COMPANY_NAME, name);
        editor.commit();
    }

    public String getKeyLoginRoleId() {
        return pref.getString(KEY_LOGIN_ROLE_ID, "");
    }

    public void setKeyLoginRoleId(String email) {
        editor.putString(KEY_LOGIN_ROLE_ID, email);
        editor.commit();
    }

    public String getKeyLoginRoleName() {
        return pref.getString(KEY_LOGIN_ROLE_NAME, "");
    }

    public void setKeyLoginRoleName(String name) {
        editor.putString(KEY_LOGIN_ROLE_NAME, name);
        editor.commit();
    }


    public String getKeyInitCompleted() {
        return pref.getString(KEY_INIT_COMPLETED, "");
    }

    public void setKeyInitCompleted(String boolVal) {
        editor.putString(KEY_INIT_COMPLETED, boolVal);
        editor.commit();
    }

    public String getKeyInitTeamAdded() {
        return pref.getString(KEY_INIT_TEAM_ADDED, "");
    }

    public void setKeyInitTeamAdded(String boolVal) {
        editor.putString(KEY_INIT_TEAM_ADDED, boolVal);
        editor.commit();
    }

    public String getKeyInitAppDownloaded() {
        return pref.getString(KEY_INIT_APP_DOWNLOADED, "");
    }

    public void setKeyInitAppDownloaded(String boolVal) {
        editor.putString(KEY_INIT_APP_DOWNLOADED, boolVal);
        editor.commit();
    }

    public String getKeyInitCompanyCreated() {
        return pref.getString(KEY_INIT_COMPANY_CREATED, "");
    }

    public void setKeyInitCompanyCreated(String boolVal) {
        editor.putString(KEY_INIT_COMPANY_CREATED, boolVal);
        editor.commit();
    }

    public String getKeyInitAccountTypeSelected() {
        return pref.getString(KEY_INIT_ACCOUNT_TYPE_SELECTED, "");
    }

    public void setKeyInitAccountTypeSelected(String type) {
        editor.putString(KEY_INIT_ACCOUNT_TYPE_SELECTED, type);
        editor.commit();
    }

    public String getLastAppVisit() {
        return pref.getString(LAST_APP_VISIT, "");
    }

    public void setLastAppVisit(String time) {
        editor.putString(LAST_APP_VISIT, time);
        editor.commit();
    }

    public boolean getReadyForFirstRun() {
        return pref.getBoolean(FIRST_RUN_AFTER_LOGIN, true);
    }

    public void setReadyForFirstRun(boolean val) {
        editor.putBoolean(FIRST_RUN_AFTER_LOGIN, val);
        editor.commit();
    }

    public boolean getKeyIsTrialValid() {
        return pref.getBoolean(KEY_IS_TRIAL_VALID, true);
    }

    public void setKeyIsTrialValid(boolean val) {
        editor.putBoolean(KEY_IS_TRIAL_VALID, val);
        editor.commit();
    }

    public boolean getKeyIsUserActive() {
        return pref.getBoolean(KEY_IS_USER_ACTIVE, true);
    }

    public void setKeyIsUserActive(boolean val) {
        editor.putBoolean(KEY_IS_USER_ACTIVE, val);
        editor.commit();
    }

    public boolean getKeyIsCompanyActive() {
        return pref.getBoolean(KEY_IS_COMPANY_ACTIVE, true);
    }

    public void setKeyIsCompanyActive(boolean val) {
        editor.putBoolean(KEY_IS_COMPANY_ACTIVE, val);
        editor.commit();
    }

    public boolean getKeyIsCompanyPaying() {
        return pref.getBoolean(KEY_IS_COMPANY_PAYING, true);
    }

    public void setKeyIsCompanyPaying(boolean val) {
        editor.putBoolean(KEY_IS_COMPANY_PAYING, val);
        editor.commit();
    }

    public boolean getCanSync() {
        return pref.getBoolean(KEY_CAN_SYNC, true);
    }

    public void setCanSync(boolean val) {
        editor.putBoolean(KEY_CAN_SYNC, val);
        editor.commit();
    }

    public int getUpdateAvailableVersion() {
        return pref.getInt(KEY_UPDATE_AVAILABLE_VERSION, 0);
    }

    public void setUpdateAvailableVersion(int val) {
        editor.putInt(KEY_UPDATE_AVAILABLE_VERSION, val);
        editor.commit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, false);
    }


}