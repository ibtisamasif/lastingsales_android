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
import com.example.muzafarimran.lastingsales.events.NoteAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSDynamicColumns;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.sync.MyURLs;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 3/14/2017.
 */

public class AgentDataFetchAsync extends AsyncTask<Object, Void, Void> {
    private static final String TAG = "AgentDataFetchAsync";
    private SessionManager sessionManager;
    private Context mContext;
    private static RequestQueue queue;

    public AgentDataFetchAsync(Context context) {
        mContext = context;
        sessionManager = new SessionManager(mContext);
        queue = Volley.newRequestQueue(mContext);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Object... objects) {
        fetchAgentLeadsFunc();
        fetchDynamicColumns();
        return null;
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
        Log.d(TAG, "fetchDynamicColumns: MYURL: "+myUrl);
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, "onErrorResponse: CouldNotSyncGETDynamicColumns");
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

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    private void fetchAgentLeadsFunc() {
        Log.d(TAG, "fetchAgentLeadsFunc: Fetching Data...");
        final int MY_SOCKET_TIMEOUT_MS = 60000;
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
                            Log.d(TAG, "onResponse: gettingDynamic: "+tempContact.getDynamic());
                            fetchAgentNotesFunc(tempContact);
                        }
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
                        tempNote.save();
                    }

                    NoteAddedEventModel mCallEvent = new NoteAddedEventModel();
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
                Log.d(TAG, "onErrorResponse: CouldNotSyncGETNotes");
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