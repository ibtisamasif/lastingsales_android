package com.example.muzafarimran.lastingsales.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
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
import com.example.muzafarimran.lastingsales.events.NoteAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSDynamicColumns;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.sync.MyURLs;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;
import com.example.muzafarimran.lastingsales.utilscallprocessing.InquiryManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import de.halfbit.tinybus.TinyBus;

public class InitService extends IntentService {
    public static final String TAG = "InitService";
    private int result = Activity.RESULT_CANCELED;
    public static final String RESULT = "result";
    public static final String NOTIFICATION = "com.lastingsales.agent";

    private SessionManager sessionManager;
    private Context mContext;
    private static RequestQueue queue;
    AtomicInteger requestsCounter;

//    boolean initError = false;

    public InitService() {
        super("InitService");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        mContext = getApplicationContext();
        sessionManager = new SessionManager(mContext);
        queue = Volley.newRequestQueue(mContext);
        requestsCounter = new AtomicInteger(0);
        return super.onStartCommand(intent, flags, startId);
    }

    // called asynchronously be Android
    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        if (sessionManager.isUserSignedIn()) {
            fetchAgentLeadsFunc();
            fetchDynamicColumns();
        }

        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                Log.d(TAG, "onRequestFinished: " + requestsCounter.get());
                requestsCounter.decrementAndGet();
                if (requestsCounter.get() == 0) {
                    Log.d(TAG, "onRequestFinished: +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                    publishResults(result);
                }
            }
        });

    }

    private void fetchAgentLeadsFunc() {
        Log.d(TAG, "fetchAgentLeadsFunc: Fetching Data...");
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        final String BASE_URL = MyURLs.GET_CONTACTS;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter("per_page", "50000")
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

                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        String contactId = jsonobject.getString("id");
                        String contactName = jsonobject.getString("name");
                        String contactNumber = jsonobject.getString("phone");
                        String contactStatus = jsonobject.getString("status");
                        String lead_type = jsonobject.getString("lead_type");
                        String email = jsonobject.getString("email");
                        String dynamic_values = jsonobject.getString("dynamic_values");

                        Log.d(TAG, "onResponse: ID: " + contactId);
                        Log.d(TAG, "onResponse: Name: " + contactName);
                        Log.d(TAG, "onResponse: Number: " + contactNumber);
                        Log.d(TAG, "onResponse: Status: " + contactStatus);
                        Log.d(TAG, "onResponse: lead_type: " + lead_type);
                        Log.d(TAG, "onResponse: email: " + email);
                        Log.d(TAG, "onResponse: dynamic_values: " + dynamic_values);

                        if (LSContact.getContactFromNumber(contactNumber) == null) {
                            LSContact tempContact = new LSContact();
                            tempContact.setServerId(contactId);
                            tempContact.setContactName(contactName);
                            tempContact.setPhoneOne(contactNumber);
                            tempContact.setContactEmail(email);
                            tempContact.setDynamic(dynamic_values);
                            tempContact.setContactType(lead_type);
                            tempContact.setContactSalesStatus(contactStatus);
                            tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED);
                            tempContact.save();
                            Log.d(TAG, "onResponse: gettingDynamic: " + tempContact.getDynamic());
                            fetchAgentNotesFunc(tempContact);
                        }
                    }

                    LeadContactAddedEventModel mCallEvent = new LeadContactAddedEventModel();
                    TinyBus bus = TinyBus.from(mContext.getApplicationContext());
                    bus.post(mCallEvent);

                    fetchInquiries();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onResponse: JSONException Contacts");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: CouldNotGETContacts"); //404 no lead
//                initError = true;
//                fetchDynamicColumns();
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
        requestsCounter.incrementAndGet();
    }

    private void fetchAgentNotesFunc(LSContact contact) {
        Log.d(TAG, "fetchAgentNotesFunc: Fetching Data...");
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        final String BASE_URL = MyURLs.GET_NOTES;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath("" + contact.getServerId())
                .appendPath("notes")
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        StringRequest sr = new StringRequest(Request.Method.GET, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() getNote: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONArray jsonarray = jObj.getJSONArray("response");

                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        String id = jsonobject.getString("id");
                        String lead_id = jsonobject.getString("lead_id");
                        String description = jsonobject.getString("description");

                        Log.d(TAG, "onResponse: id: " + id);
                        Log.d(TAG, "onResponse: lead_id: " + lead_id);
                        Log.d(TAG, "onResponse: description: " + description);

                        LSNote tempNote = new LSNote();
                        tempNote.setServerId(id);
                        tempNote.setContactOfNote(LSContact.getContactFromServerId(lead_id));
                        tempNote.setNoteText(description);
                        tempNote.setSyncStatus(SyncStatus.SYNC_STATUS_NOTE_ADDED_SYNCED);
                        tempNote.setCreatedAt(PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(Calendar.getInstance().getTimeInMillis()));
                        tempNote.save();
                    }

                    NoteAddedEventModel mCallEvent = new NoteAddedEventModel();
                    TinyBus bus = TinyBus.from(mContext);
                    bus.post(mCallEvent);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onResponse: JSONException Notes");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: CouldNotGETNotes");
//                initError = true;
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
        requestsCounter.incrementAndGet();
    }

    private void fetchDynamicColumns() {

        Log.d(TAG, "fetchDynamicColumns: Fetching columns...");
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        final String BASE_URL = MyURLs.GET_COLUMNS;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        Log.d(TAG, "fetchDynamicColumns: MYURL: " + myUrl);
        StringRequest sr = new StringRequest(Request.Method.GET, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() getColumn: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONObject responseObject = jObj.getJSONObject("response");
                    String totalColumns = responseObject.getString("total");
                    Log.d(TAG, "onResponse: totalColumns: " + totalColumns);

                    JSONArray jsonarray = responseObject.getJSONArray("data");

                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        String id = jsonobject.getString("id");
                        String column_type = jsonobject.getString("column_type");
                        String name = jsonobject.getString("name");
                        String default_value_options = jsonobject.getString("default_value_options");
                        String range = jsonobject.getString("range");
                        String created_by = jsonobject.getString("created_by");
                        String updated_by = jsonobject.getString("updated_by");
                        String created_at = jsonobject.getString("created_at");
                        String updated_at = jsonobject.getString("updated_at");
                        String company_id = jsonobject.getString("company_id");

                        Log.d(TAG, "onResponse: ID: " + id);
                        Log.d(TAG, "onResponse: column_type: " + column_type);
                        Log.d(TAG, "onResponse: name: " + name);
                        Log.d(TAG, "onResponse: default_value_options: " + default_value_options);
                        Log.d(TAG, "onResponse: range: " + range);
                        Log.d(TAG, "onResponse: created_by: " + created_by);
                        Log.d(TAG, "onResponse: updated_by: " + updated_by);
                        Log.d(TAG, "onResponse: created_at: " + created_at);
                        Log.d(TAG, "onResponse: updated_at: " + updated_at);
                        Log.d(TAG, "onResponse: company_id: " + company_id);

//                        LSContact lsContact = new LSContact();
//                        lsContact.setContactName("name");
//                        lsContact.setDynamicValues("dynVal");
//                        lsContact.save();
//                        lsContact.delete();
//
//                        LSDynamicColumns lsDynamicColumns = new LSDynamicColumns();
//                        lsDynamicColumns.setName("Name");
//                        lsDynamicColumns.save();
//                        Log.d(TAG, "CHECKKKKKKKKKKKKKKKKKKKKkkonResponse: "+lsDynamicColumns.getName());
//                        lsDynamicColumns.delete();

                        LSDynamicColumns checkColumn = LSDynamicColumns.getColumnFromServerId(id);
                        if (checkColumn == null) {
                            LSDynamicColumns newColumn = new LSDynamicColumns();
                            newColumn.setServerId(id);
                            newColumn.setColumnType(column_type);
                            newColumn.setName(name);
                            newColumn.setDefaultValueOption(default_value_options);
                            newColumn.setRange(range);
                            newColumn.setCreated_by(created_by);
                            newColumn.setUpdated_by(updated_by);
                            newColumn.setCreated_at(created_at);
                            newColumn.setUpdated_at(updated_at);
                            newColumn.setCompanyId(company_id);
                            newColumn.save();

                        }
                    }
//                    LeadContactAddedEventModel mCallEvent = new LeadContactAddedEventModel();
//                    TinyBus bus = TinyBus.from(mContext);
//                    bus.post(mCallEvent);

                    fetchInquiries();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onResponse: JSONException DynamicColumns");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: CouldNotGETDynamicColumns");
                // for no dynamic columns i am getting 412 instead of 404
                if (error != null) {
                    if (error.networkResponse != null) {
                        if (error.networkResponse.statusCode == 404) {
                            // No dynamic columns
                        } else {
//                            initError = true;
                        }
                    } else {
//                        initError = true;
                    }
                }
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
        requestsCounter.incrementAndGet();
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
                Log.d(TAG, "onResponse() getContact: response = " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    if (!jObj.isNull("response")) {
                        JSONObject responseObject = jObj.getJSONObject("response");

                        if (responseObject != null) {
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


//                    if (sessionManager.isFirstRunAfterLogin()) {
//                        Log.d(TAG, "initFirst: isFirstRun TRUE");
//                        TheCallLogEngine theCallLogEngine = new TheCallLogEngine(mContext);
//                        theCallLogEngine.execute();
//                    }
                        }
                    }else {
                        Log.d(TAG, "onResponse: No Inquiries");
                    }
                    result = Activity.RESULT_OK;
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onResponse: JSONException Inquiries");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: CouldNotGETInquiries");

//                initError = true;

//                if (sessionManager.isFirstRunAfterLogin()) {
//                    Log.d(TAG, "initFirst: isFirstRun TRUE");
//                    TheCallLogEngine theCallLogEngine = new TheCallLogEngine(mContext);
//                    theCallLogEngine.execute();
//                }
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
        requestsCounter.incrementAndGet();
    }

    private void publishResults(int result) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }
}