package com.example.muzafarimran.lastingsales.sync;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ibtisam on 10/12/2017.
 */

public class SyncUser {
    public static final String TAG = "SyncUser";
    private static SessionManager sessionManager;
    private static RequestQueue queue;
    private static String currentVersionCode;

    public static void getUserDataFromServer(Activity activity) {
        sessionManager = new SessionManager(activity);
        queue = Volley.newRequestQueue(activity);

        final int MY_SOCKET_TIMEOUT_MS = 60000;
        final String BASE_URL = MyURLs.GET_AGENT;
        Uri builtUri;
        builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        Log.d(TAG, "getUserDataFromServer: URL: " + myUrl);
        StringRequest sr = new StringRequest(Request.Method.GET, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() getUserDataFromServer: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
                    if (responseCode == 200) {
                        JSONObject responseObject = jObj.getJSONObject("response");


                    }
                } catch (JSONException e) {
                    Crashlytics.logException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse:SyncUser CouldNotGetUserDataFromServer");
            }
        }) {
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);

    }

    public static void updateUserLastSeenToServer(Activity activity) {
        sessionManager = new SessionManager(activity);
        queue = Volley.newRequestQueue(activity);

        try {
            currentVersionCode = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        final int MY_SOCKET_TIMEOUT_MS = 60000;
        final String BASE_URL = MyURLs.UPDATE_AGENT;
        Uri builtUri;
        builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter("last_seen", "" + PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(Long.parseLong(sessionManager.getLastAppVisit()), "yyyy-MM-dd kk:mm:ss"))
                .appendQueryParameter("app_version", "" + currentVersionCode)
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        Log.d(TAG, "updateUserLastSeenToServer: URL: " + myUrl);
        StringRequest sr = new StringRequest(Request.Method.PUT, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() updateUserLastSeenToServer: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
                    if (responseCode == 200) {
                        JSONObject responseObject = jObj.getJSONObject("response");
                        Log.d(TAG, "onResponse : LastSeenInLocal : " + sessionManager.getLastAppVisit());
                        Log.d(TAG, "onResponse : LastSeenFromServer : " + responseObject.getString("last_seen"));
                    }
                } catch (JSONException e) {
                    Crashlytics.logException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse:SyncUser CouldNotUpdateLastSeenToServer");
            }
        }) {
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);

    }
}
