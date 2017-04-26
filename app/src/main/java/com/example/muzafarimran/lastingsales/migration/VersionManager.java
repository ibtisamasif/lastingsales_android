package com.example.muzafarimran.lastingsales.migration;

import android.content.Context;
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
import com.example.muzafarimran.lastingsales.events.LeadContactAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSDynamicColumns;
import com.example.muzafarimran.lastingsales.sync.MyURLs;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.MixpanelConfig;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

        } else if (version == 10) {
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

                    // get all contacts and update their dynamic column
                    List<LSContact> allContacts = (List<LSContact>) LSContact.listAll(LSContact.class);
                    Log.d(TAG, "onCreate: allContactsSize: " + allContacts.size());
                    for (final LSContact oneContact : allContacts) {
                        Log.d(TAG, "Fetching Only Dynamic Columns per Contact...");
                        final int MY_SOCKET_TIMEOUT_MS = 60000;
                        RequestQueue queue = Volley.newRequestQueue(mContext);
                        final String BASE_URL = MyURLs.GET_CONTACTS;
                        Uri builtUri = Uri.parse(BASE_URL)
                                .buildUpon()
                                .appendPath("" + oneContact.getServerId())
                                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                                .build();
                        final String myUrl = builtUri.toString();
                        StringRequest sr = new StringRequest(Request.Method.GET, myUrl, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "onResponse() getContact: response = [" + response + "]");
                                try {
                                    JSONObject jObj = new JSONObject(response);
                                    int responseCode = jObj.getInt("responseCode");
//                    if (responseCode == 200) {
                                    JSONObject responseObject = jObj.getJSONObject("response");
                                    String dynamic_values = responseObject.getString("dynamic_values");
                                    oneContact.setDynamic(dynamic_values);
                                    oneContact.save();
                                    Log.d(TAG, "onResponse: DynamicValue is " + oneContact.getDynamic());
//                                }
                                    LeadContactAddedEventModel mCallEvent = new LeadContactAddedEventModel();
                                    TinyBus bus = TinyBus.from(mContext);
                                    bus.post(mCallEvent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                Log.d(TAG, "onErrorResponse: CouldNotSyncGETContacts");
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<String, String>();
                                return params;
                            }
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                return params;
                            }
                        };
                        sr.setRetryPolicy(new DefaultRetryPolicy(
                                MY_SOCKET_TIMEOUT_MS,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        queue.add(sr);
                    }
                    return true;
                } else {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        } else if (version == 14) {
            try {
                sessionManager.storeVersionCodeNow();
                Log.d(TAG, "func: Running Script for Mixpanel");
                if (sessionManager.getLoginMode().equals(SessionManager.MODE_NORMAL)) {
                    Log.d(TAG, "func: case1");
                    try {
                        String projectToken = MixpanelConfig.projectToken;
                        MixpanelAPI mixpanel = MixpanelAPI.getInstance(mContext, projectToken);
                        mixpanel.identify(sessionManager.getKeyLoginId()); //user_id
                        mixpanel.getPeople().identify(sessionManager.getKeyLoginId());

                        JSONObject props = new JSONObject();

                        props.put("$first_name", ""+sessionManager.getKeyLoginFirstName());
                        props.put("$last_name", ""+sessionManager.getKeyLoginLastName());
                        props.put("activated", "yes");
                        mixpanel.getPeople().set(props);

                        mixpanel.track("User Logged in", props);
                    } catch (JSONException e) {
                        Log.e("MYAPP", "Unable to add properties to JSONObject", e);
                    }
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
                    return true;
                } else {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        } else {
            return true;
        }
    }
}