package com.example.muzafarimran.lastingsales.sync;

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
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.events.LeadContactAddedEventModel;
import com.example.muzafarimran.lastingsales.utils.NetworkAccess;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;
import com.example.muzafarimran.lastingsales.utilscallprocessing.InquiryManager;
import com.example.muzafarimran.lastingsales.utilscallprocessing.TheCallLogEngine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 12/26/2017.
 */

public class AgentInquiriesFetchAsync extends AsyncTask<Object, Void, Void> {
        private static final String TAG = "AgentInquiriesFetchA";
//    private static final String TAG = "AppInitializationTest";

    private SessionManager sessionManager;
    private Context mContext;
    private static RequestQueue queue;

    public AgentInquiriesFetchAsync(Context context) {
        mContext = context;
        sessionManager = new SessionManager(mContext);
        queue = Volley.newRequestQueue(mContext);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.e(TAG, "AgentInquiriesFetchAsync onPreExecute: ");
    }

    @Override
    protected Void doInBackground(Object... objects) {
        Log.e(TAG, "AgentInquiriesFetchAsync doInBackground: ");
        if (NetworkAccess.isNetworkAvailable(mContext)) {
            fetchInquiries();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.e(TAG, "AgentInquiriesFetchAsync onPostExecute: ");
        if (sessionManager.isFirstRunAfterLogin()) {
            Log.d(TAG, "initFirst: isFirstRun TRUE");
            TheCallLogEngine theCallLogEngine = new TheCallLogEngine(mContext);
            theCallLogEngine.execute();
        }
    }

    private void fetchInquiries() {
        Log.d(TAG, "fetchInquiries: Fetching Data...");
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        final String BASE_URL = MyURLs.GET_INQUIRY;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter("status", "pending")
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
                    String totalInquiries = responseObject.getString("total");
                    Log.d(TAG, "onResponse: TotalInquiries: " + totalInquiries);

                    JSONArray jsonarray = responseObject.getJSONArray("data");

                    for (int i = 0; i < jsonarray.length(); i++) {
                        Log.d(TAG, "INDEX " + i);
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        String inquiry_id = jsonobject.getString("id");
                        String agent_id = jsonobject.getString("agent_id");
                        String date = jsonobject.getString("date");
                        String time = jsonobject.getString("time");
                        String contactNumber = jsonobject.getString("contact_number");
                        String status_of_inquiry = jsonobject.getString("status");
                        String inquiry_created_by = jsonobject.getString("created_by");
                        String user_id_of_inquiry = jsonobject.getString("user_id");
                        String company_id_of_inquiry = jsonobject.getString("company_id");
                        String avg_response_time = jsonobject.getString("avg_response_time");

//                        JSONObject leadObj = jsonobject.getJSONObject("lead");
//                        String lead_id = leadObj.getString("id");
//                        String name = leadObj.getString("name");
////                        String email = leadObj.getString("email");
//                        String phone = leadObj.getString("phone");
////                        String address = leadObj.getString("address");
//                        String lead_created_by = leadObj.getString("created_by");
////                        String updated_by = leadObj.getString("updated_by");
//                        String created_at = leadObj.getString("created_at");
//                        String updated_at = leadObj.getString("updated_at");
//                        String status_of_lead = leadObj.getString("status");
//                        String follow_up_date = leadObj.getString("follow_up_date");
//                        String follow_up_description = leadObj.getString("follow_up_description");
//                        String dynamic_values = leadObj.getString("dynamic_values");
//                        String company_id_of_lead = leadObj.getString("company_id");
//                        String image = leadObj.getString("image");
//                        String image_path = leadObj.getString("image_path");
//                        String lead_type = leadObj.getString("lead_type");

                        Log.d(TAG, "onResponse: inquiry_id: " + inquiry_id);
                        Log.d(TAG, "onResponse: agent_id: " + agent_id);
                        Log.d(TAG, "onResponse: date: " + date);
                        Log.d(TAG, "onResponse: time: " + time);
                        Log.d(TAG, "onResponse: contactNumber: " + contactNumber);
                        Log.d(TAG, "onResponse: status_of_inquiry: " + status_of_inquiry);
                        Log.d(TAG, "onResponse: inquiry_created_by: " + inquiry_created_by);
                        Log.d(TAG, "onResponse: user_id_of_inquiry: " + user_id_of_inquiry);
                        Log.d(TAG, "onResponse: company_id_of_inquiry: " + company_id_of_inquiry);
                        Log.d(TAG, "onResponse: avg_response_time: " + avg_response_time);

                        Log.d(TAG, "onResponse: date + time: " + date + " " + time);
                        long beginTimeFromServer = PhoneNumberAndCallUtils.getMillisFromSqlFormattedDateAndTime(date + " " + time);
                        Log.d(TAG, "onResponse: date + time in Millis: " + beginTimeFromServer);

//                        Log.d(TAG, "onResponse: lead_id: " + lead_id);
//                        Log.d(TAG, "onResponse: name: " + name);
//                        Log.d(TAG, "onResponse: phone: " + phone);
//                        Log.d(TAG, "onResponse: lead_created_by: " + lead_created_by);
//                        Log.d(TAG, "onResponse: created_at: " + created_at);
//                        Log.d(TAG, "onResponse: updated_at: " + updated_at);
//                        Log.d(TAG, "onResponse: status_of_lead: " + status_of_lead);
//                        Log.d(TAG, "onResponse: follow_up_date: " + follow_up_date);
//                        Log.d(TAG, "onResponse: follow_up_description: " + follow_up_description);
//                        Log.d(TAG, "onResponse: dynamic_values: " + dynamic_values);
//                        Log.d(TAG, "onResponse: company_id_of_lead: " + company_id_of_lead);
//                        Log.d(TAG, "onResponse: image: " + image);
//                        Log.d(TAG, "onResponse: image_path: " + image_path);
//                        Log.d(TAG, "onResponse: lead_type: " + lead_type);
//                        Log.d(TAG, "onResponse: lead_type: " + lead_type);

                        InquiryManager.createOrUpdate(mContext, inquiry_id, status_of_inquiry, beginTimeFromServer, contactNumber);

                    }

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
                Log.e(TAG, "onErrorResponse: CouldNotSyncGETInquiries");
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
