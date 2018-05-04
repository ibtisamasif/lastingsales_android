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
import com.example.muzafarimran.lastingsales.providers.models.LSDeal;
import com.example.muzafarimran.lastingsales.providers.models.LSDynamicColumns;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.providers.models.LSStage;
import com.example.muzafarimran.lastingsales.providers.models.LSWorkflow;
import com.example.muzafarimran.lastingsales.sync.MyURLs;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;
import com.example.muzafarimran.lastingsales.utilscallprocessing.InquiryManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
            fetchWorkflow();
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
        Log.d(TAG, "fetchAgentLeadsFunc: Fetching leads...");
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
                        String address = jsonobject.getString("address");
                        String dynamic_values = jsonobject.getString("dynamic_values");
                        int created_by = 0;
                        if (jsonobject.has("created_by")) {
                            created_by = jsonobject.getInt("created_by");
                        }
                        String updated_at = "";
                        if (jsonobject.has("updated_at")) {
                            updated_at = jsonobject.getString("updated_at");
                        }
                        int user_id = jsonobject.getInt("user_id");
                        String src = jsonobject.getString("src");

                        Log.d(TAG, "onResponse: ID: " + contactId);
                        Log.d(TAG, "onResponse: Name: " + contactName);
                        Log.d(TAG, "onResponse: Number: " + contactNumber);
                        Log.d(TAG, "onResponse: Status: " + contactStatus);
                        Log.d(TAG, "onResponse: lead_type: " + lead_type);
                        Log.d(TAG, "onResponse: email: " + email);
                        Log.d(TAG, "onResponse: address: " + address);
                        Log.d(TAG, "onResponse: dynamic_values: " + dynamic_values);
                        Log.d(TAG, "onResponse: created_by: " + created_by);
                        Log.d(TAG, "onResponse: updated_at: " + updated_at);
                        Log.d(TAG, "onResponse: user_id: " + user_id);
                        Log.d(TAG, "onResponse: src: " + src);

                        if (LSContact.getContactFromNumber(contactNumber) == null) {
                            LSContact tempContact = new LSContact();
                            tempContact.setServerId(contactId);
                            tempContact.setContactName(contactName);
                            tempContact.setPhoneOne(contactNumber);
                            tempContact.setContactEmail(email);
                            tempContact.setContactAddress(address);
                            tempContact.setDynamic(dynamic_values);
                            tempContact.setContactType(lead_type);
                            tempContact.setContactSalesStatus(contactStatus);
                            tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED);
                            if (created_by != 0) {
                                tempContact.setCreatedBy(created_by);
                            }
                            tempContact.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
                            tempContact.setUserId(user_id);
                            tempContact.setSrc(src);
                            tempContact.save();
                            Log.d(TAG, "onResponse: gettingDynamic: " + tempContact.getDynamic());
                            fetchAgentNotesFunc(tempContact);
                        }
                    }

                    LeadContactAddedEventModel mCallEvent = new LeadContactAddedEventModel();
                    TinyBus bus = TinyBus.from(mContext.getApplicationContext());
                    bus.post(mCallEvent);

                    fetchInquiries();
                    fetchDeals();
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
        Log.d(TAG, "fetchAgentNotesFunc: Fetching notes...");
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

    private void fetchWorkflow() {

        Log.d(TAG, "fetchWorkflow: Fetching workflow...");
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        final String BASE_URL = MyURLs.GET_WORKFLOW;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        Log.d(TAG, "fetchWorkflow: MYURL: " + myUrl);
        StringRequest sr = new StringRequest(Request.Method.GET, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() getWorkflow: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
                    JSONObject responseObject = jObj.getJSONObject("response");
                    String totalWorkflows = responseObject.getString("total");
                    Log.d(TAG, "onResponse: totalWorkflows: " + totalWorkflows);

                    JSONArray jsonarrayData = responseObject.getJSONArray("data");

                    for (int i = 0; i < jsonarrayData.length(); i++) {
                        JSONObject jsonDataObject = jsonarrayData.getJSONObject(i);
                        String id = jsonDataObject.getString("id");
                        String name = jsonDataObject.getString("name");
                        String status = jsonDataObject.getString("status");
                        String company_id = jsonDataObject.getString("company_id");
                        String created_by = jsonDataObject.getString("created_by");
                        String created_at = jsonDataObject.getString("created_at");
                        String updated_at = jsonDataObject.getString("updated_at");
                        String is_default = jsonDataObject.getString("is_default");

                        Log.d(TAG, "onResponse: ID: " + id);
                        Log.d(TAG, "onResponse: name: " + name);
                        Log.d(TAG, "onResponse: created_by: " + created_by);
                        Log.d(TAG, "onResponse: created_at: " + created_at);
                        Log.d(TAG, "onResponse: updated_at: " + updated_at);
                        Log.d(TAG, "onResponse: company_id: " + company_id);
                        Log.d(TAG, "onResponse: is_default: " + is_default);

                        LSWorkflow checkWorkflow = LSWorkflow.getWorkflowFromServerId(id);
                        if (checkWorkflow == null) {
                            LSWorkflow newWorkflow = new LSWorkflow();
                            newWorkflow.setServerId(id);
                            newWorkflow.setName(name);
                            newWorkflow.setStatus(status);
                            newWorkflow.setCreated_by(created_by);
                            Date updated_atDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(updated_at);
                            newWorkflow.setUpdated_at(updated_atDate);
                            newWorkflow.setCompanyId(company_id);
                            newWorkflow.setIsDefault(is_default);
                            newWorkflow.save();

                            JSONArray jsonarrayStages = jsonDataObject.getJSONArray("stages");
                            for (int j = 0; j < jsonarrayStages.length(); j++) {
                                JSONObject jsonStageObject = jsonarrayStages.getJSONObject(j);
                                String stage_id = jsonStageObject.getString("id");
                                String stage_workflow_id = jsonStageObject.getString("workflow_id");
                                String stage_company_id = jsonStageObject.getString("company_id");
                                String stage_name = jsonStageObject.getString("name");
                                String stage_description = jsonStageObject.getString("description");
                                String stage_created_by = jsonStageObject.getString("created_by");
                                String stage_created_at = jsonStageObject.getString("created_at");
                                String stage_updated_at = jsonStageObject.getString("updated_at");
                                String stage_next_to = jsonStageObject.getString("next_to");
                                String stage_position = jsonStageObject.getString("position");

                                Log.d(TAG, "onResponse: stage_id: " + stage_id);
                                Log.d(TAG, "onResponse: stage_workflow_id: " + stage_workflow_id);
                                Log.d(TAG, "onResponse: stage_company_id: " + stage_company_id);
                                Log.d(TAG, "onResponse: stage_name: " + stage_name);
                                Log.d(TAG, "onResponse: stage_description: " + stage_description);
                                Log.d(TAG, "onResponse: stage_created_by: " + stage_created_by);
                                Log.d(TAG, "onResponse: stage_created_at: " + stage_created_at);
                                Log.d(TAG, "onResponse: stage_updated_at: " + stage_updated_at);
                                Log.d(TAG, "onResponse: stage_next_to: " + stage_next_to);
                                Log.d(TAG, "onResponse: stage_position: " + stage_position);

                                LSStage checkSteps = LSStage.getStageFromServerId(id);
                                if (checkSteps == null) {
                                    LSStage newSteps = new LSStage();
                                    newSteps.setServerId(stage_id);
                                    newSteps.setCompanyId(stage_company_id);
                                    newSteps.setName(stage_name);
                                    newSteps.setDescription(stage_description);
                                    newSteps.setCreatedBy(stage_created_by);
//                                newSteps.setCreatedAt(stage_created_at);
                                    Date stage_updated_atDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(stage_updated_at);
                                    newSteps.setUpdatedAt(stage_updated_atDate);
                                    newSteps.setNextTo(stage_next_to);
                                    newSteps.setPosition(stage_position);
                                    newSteps.setWorkflowId(stage_workflow_id);
                                    newSteps.setWorkflow(newWorkflow);
                                    newSteps.save();
                                }
                            }
                        }
                    }
//                    LeadContactAddedEventModel mCallEvent = new LeadContactAddedEventModel();
//                    TinyBus bus = TinyBus.from(mContext);
//                    bus.post(mCallEvent);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onResponse: JSONException fetchWorkflow");
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onResponse: ParseException fetchWorkflow");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: CouldNotGETWorkflow");
                if (error != null) {
                    if (error.networkResponse != null) {
                        if (error.networkResponse.statusCode == 404) {
                        } else {
                        }
                    } else {
                    }
                }
            }
        });
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
        requestsCounter.incrementAndGet();
    }

    private void fetchDeals() {
        Log.d(TAG, "fetchAgentLeadsFunc: Fetching Deals...");
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        final String BASE_URL = MyURLs.GET_DEALS;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter("per_page", "50000")
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        StringRequest sr = new StringRequest(Request.Method.GET, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() getDeals: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONObject responseObject = jObj.getJSONObject("response");
                    String totalDeals = responseObject.getString("total");
                    Log.d(TAG, "onResponse: TotalDeals: " + totalDeals);

                    JSONArray jsonarray = responseObject.getJSONArray("data");

                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        String id = jsonobject.getString("id");
                        String name = jsonobject.getString("name");
                        int created_by = 0;
                        if (jsonobject.has("created_by")) {
                            created_by = jsonobject.getInt("created_by");
                        }
//                        int updated_by = 0;
//                        if (jsonobject.has("updated_by")) {
//                            updated_by = jsonobject.getInt("updated_by");
//                        }
                        String created_at = jsonobject.getString("created_at");
                        String updated_at = jsonobject.getString("updated_at");
                        int user_id = jsonobject.getInt("user_id");
                        int lead_id = jsonobject.getInt("lead_id");
                        int workflow_id = jsonobject.getInt("workflow_id");
                        int workflow_stage_id = jsonobject.getInt("workflow_stage_id");
                        String Status = jsonobject.getString("status");
//                        String follow_up_date = jsonobject.getgetStringInt("follow_up_date");
//                        String follow_up_description = jsonobject.getString("follow_up_description");
                        String dynamic_values = jsonobject.getString("dynamic_values");
                        int company_id = jsonobject.getInt("company_id");
                        String src = jsonobject.getString("src");
                        String src_id = jsonobject.getString("src_id");
                        String is_private = jsonobject.getString("is_private");
                        String version = jsonobject.getString("version");

                        String value = jsonobject.getString("value");
                        String currency = jsonobject.getString("currency");
                        String success_rate = jsonobject.getString("success_rate");
                        String success_eta = jsonobject.getString("success_eta");

                        Log.d(TAG, "onResponse: ID: " + id);
                        Log.d(TAG, "onResponse: Name: " + name);
                        Log.d(TAG, "onResponse: Status: " + Status);
                        Log.d(TAG, "onResponse: dynamic_values: " + dynamic_values);
                        Log.d(TAG, "onResponse: created_by: " + created_by);
                        Log.d(TAG, "onResponse: created_at: " + created_at);
                        Log.d(TAG, "onResponse: updated_at: " + updated_at);
                        Log.d(TAG, "onResponse: user_id: " + user_id);
                        Log.d(TAG, "onResponse: company_id: " + company_id);
                        Log.d(TAG, "onResponse: src_id: " + src_id);
                        Log.d(TAG, "onResponse: lead_id: " + lead_id);
                        Log.d(TAG, "onResponse: workflow_id: " + workflow_id);
                        Log.d(TAG, "onResponse: workflow_stage_id: " + workflow_stage_id);
                        Log.d(TAG, "onResponse: src: " + src);
                        Log.d(TAG, "onResponse: is_private: " + is_private);
                        Log.d(TAG, "onResponse: version: " + version);
                        Log.d(TAG, "onResponse: value: " + value);
                        Log.d(TAG, "onResponse: currency: " + currency);
                        Log.d(TAG, "onResponse: success_rate: " + success_rate);
                        Log.d(TAG, "onResponse: success_eta: " + success_eta);

                        if (LSDeal.getDealFromId(id) == null) {
                            LSContact lsContact = LSContact.getContactFromServerId(Integer.toString(lead_id));
                            if (lsContact != null) {
                                LSDeal tempDeal = new LSDeal();
                                tempDeal.setServerId(id);
                                tempDeal.setName(name);
                                tempDeal.setStatus(Status);
//                            tempDeal.setEmail(email);
                                tempDeal.setDynamic(dynamic_values);
                                if (created_by != 0) {
                                    tempDeal.setCreatedBy(Integer.toString(created_by));
                                }
                                tempDeal.setCreatedAt(created_at);
                                tempDeal.setUpdatedAt(Calendar.getInstance().getTime());
                                tempDeal.setUserId(Integer.toString(user_id));
                                tempDeal.setCompanyId(Integer.toString(company_id));
//                            tempDeal.setSrcId(Integer.toString(src_id));
//                                tempDeal.setLeadId(Integer.toString(lead_id));
                                tempDeal.setWorkflowId(Integer.toString(workflow_id));
                                tempDeal.setWorkflowStageId(Integer.toString(workflow_stage_id));
//                            tempDeal.setSrc(src);
                                tempDeal.setIsPrivate(is_private);
//                            tempDeal.setVersion(version);
                                tempDeal.setContact(lsContact);
                                tempDeal.setValue(value);
                                tempDeal.setCurrency(currency);
                                tempDeal.setSuccessRate(success_rate);
                                tempDeal.setSuccessEta(success_eta);
                                tempDeal.setSyncStatus(SyncStatus.SYNC_STATUS_DEAL_ADD_SYNCED);
                                tempDeal.save();
                            }
                        }
                    }

                    LeadContactAddedEventModel mCallEvent = new LeadContactAddedEventModel();
                    TinyBus bus = TinyBus.from(mContext.getApplicationContext());
                    bus.post(mCallEvent);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onResponse: JSONException Deals");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: CouldNotGETDeals");
            }
        });
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
                                JSONObject jsonobject = jsonarray.getJSONObject(i); //TODO JSON Exception typeMismatch
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
                    } else {
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