package com.example.muzafarimran.lastingsales.migration;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.app.MixpanelConfig;
import com.example.muzafarimran.lastingsales.app.MyURLs;
import com.example.muzafarimran.lastingsales.app.SyncStatus;
import com.example.muzafarimran.lastingsales.events.LeadContactAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.service.InitService;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.halfbit.tinybus.TinyBus;

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
        try {
            Log.d(TAG, "runMigrations: STORE VERSION CODE");
            sessionManager.storeVersionCodeNow();
            Log.d(TAG, "runMigrations: MixPanel Instantiated");
            String projectToken = MixpanelConfig.projectToken;
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(mContext, projectToken);
            mixpanel.identify(sessionManager.getKeyLoginId()); //user_id
            mixpanel.getPeople().identify(sessionManager.getKeyLoginId());

            JSONObject props = new JSONObject();

            props.put("$first_name", "" + sessionManager.getKeyLoginFirstName());
            props.put("$last_name", "" + sessionManager.getKeyLoginLastName());
            props.put("activated", "yes");
            props.put("Last Activity", "" + PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(Calendar.getInstance().getTimeInMillis()));
            mixpanel.getPeople().set(props);
        } catch (JSONException e) {
            Log.e("mixpanel", "Unable to add properties to JSONObject", e);
        }
        // no return should be out of IF ELSE condition ever :: application might consider migrations successful otherwise
        if (version == 1) {
            sessionManager.storeVersionCodeNow();
            if (sessionManager.getLoginMode().equals(SessionManager.MODE_NORMAL)) {
                Log.d(TAG, "func: case1");
                return true;
            } else if (sessionManager.getLoginMode().equals(SessionManager.MODE_NEW_INSTALL)) {
                Log.d(TAG, "func: case2");
                return true;
            } else if (sessionManager.getLoginMode().equals(SessionManager.MODE_UPGRADE)) {
                Log.d(TAG, "func: case3");
                return true;
            }
            return false;
        } else if (version == 2) {
            try {
                sessionManager.storeVersionCodeNow();
                if (sessionManager.getLoginMode().equals(SessionManager.MODE_NORMAL)) {
                    Log.d(TAG, "func: case1");
                    return true;
                } else if (sessionManager.getLoginMode().equals(SessionManager.MODE_NEW_INSTALL)) {
                    Log.d(TAG, "func: case2");
                    return true;
                } else if (sessionManager.getLoginMode().equals(SessionManager.MODE_UPGRADE)) {
                    Log.d(TAG, "func: case3");
                    return true;
                } else {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        } else {
            return true;
        }
    }
}