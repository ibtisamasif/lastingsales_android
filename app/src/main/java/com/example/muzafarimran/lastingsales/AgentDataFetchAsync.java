package com.example.muzafarimran.lastingsales;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.sync.MyURLs;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ibtisam on 3/14/2017.
 */

class AgentDataFetchAsync extends AsyncTask<Object, Void, Void> {
    private static final String TAG = "AgentDataFetchAsync";
    private SessionManager sessionManager;
    private Context mContext;
//    private ProgressDialog pdLoading;

    public AgentDataFetchAsync(Context context) {
        mContext = context;
        sessionManager = new SessionManager(mContext);
//        pdLoading = new ProgressDialog(this.mContext);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        this.pdLoading.setMessage("Progress start");
//        this.pdLoading.show();
    }

    @Override
    protected Void doInBackground(Object... objects) {
        fetchAgentDataFunc();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
//        if (pdLoading.isShowing()) {
//            pdLoading.dismiss();
//        }
    }

    private void fetchAgentDataFunc() {
        Log.d(TAG, "fetchAgentDataFunc: Fetching Data...");
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        final String BASE_URL = MyURLs.GET_CONTACTS;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        StringRequest sr = new StringRequest(Request.Method.GET, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() getContact: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONObject responseObject = jObj.getJSONObject("response");
                    String totalLeads = responseObject.getString("total");
                    Log.d(TAG, "onResponse: TotalLeads: " + totalLeads);

                    JSONArray jsonarray = responseObject.getJSONArray("data");
                    ;
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        String contactId = jsonobject.getString("id");
                        String contactName = jsonobject.getString("name");
                        String contactNumber = jsonobject.getString("phone");
                        String contactStatus = jsonobject.getString("status");

                        Log.d(TAG, "onResponse: ID: " + contactId);
                        Log.d(TAG, "onResponse: Name: " + contactName);
                        Log.d(TAG, "onResponse: Number: " + contactNumber);
                        Log.d(TAG, "onResponse: Status: " + contactStatus);

                        LSContact tempContact = new LSContact();
                        tempContact.setServerId(contactId);
                        tempContact.setContactName(contactName);
                        tempContact.setPhoneOne(contactNumber);
                        tempContact.setContactType(LSContact.CONTACT_TYPE_SALES);
                        tempContact.setContactSalesStatus(contactStatus);
                        tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED);
                        tempContact.save();
                    }

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
//                sessionManager.setLoginToken("gVLqb2w8XEpdaQOK8wU7MpNXL9ZpZtBhiN1sbxImCuIOIiFQbMN3AHN098Ua");
                Map<String, String> params = new HashMap<String, String>();
//                params.put("api_token", "" + sessionManager.getLoginToken());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }
}