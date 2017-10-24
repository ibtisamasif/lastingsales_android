package com.example.muzafarimran.lastingsales.sync;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.SettingsManager;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ibtisam on 10/12/2017.
 */

public class SyncLastSeen {
    public static final String TAG = "SyncLastSeen";
    private static SessionManager sessionManager;
    private static RequestQueue queue;

    public static void updateLastSeenToServer(Activity activity) {
        sessionManager = new SessionManager(activity);
        queue = Volley.newRequestQueue(activity);

        final int MY_SOCKET_TIMEOUT_MS = 60000;
        final String BASE_URL = MyURLs.UPDATE_AGENT;
        Uri builtUri;
        builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter("last_seen", "" + PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(Long.parseLong(sessionManager.getLastAppVisit()), "yyyy-MM-dd kk:mm:ss"))
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        StringRequest sr = new StringRequest(Request.Method.PUT, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() updateLastSeenToServer: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
                    if (responseCode == 200) {
                        JSONObject responseObject = jObj.getJSONObject("response");
                        Log.d(TAG, "onResponse : LastSeenInLocal : " + sessionManager.getLastAppVisit());
                        Log.d(TAG, "onResponse : LastSeenFromServer : " + responseObject.getString("last_seen"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, "onErrorResponse: CouldNotUpdateupdateLastSeenToServer");
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
