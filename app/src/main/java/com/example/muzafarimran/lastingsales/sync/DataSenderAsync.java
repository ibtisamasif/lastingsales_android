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
import com.example.muzafarimran.lastingsales.events.LeadContactDeletedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;
import com.example.muzafarimran.lastingsales.utils.NetworkAccess;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.halfbit.tinybus.TinyBus;

public class DataSenderAsync extends AsyncTask<Object, Void, Void> {
    public static final String TAG = "DataSenderAsync";
    Context mContext;
    SessionManager sessionManager;

    public DataSenderAsync(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        sessionManager = new SessionManager(mContext);
    }

    @Override
    protected Void doInBackground(Object... params) {
        try {
            if (NetworkAccess.isNetworkAvailable(mContext)) {
                if (sessionManager.isUserSignedIn()) {
                    Log.d(TAG, "Syncing");
                    Log.d(TAG, "Token : "+sessionManager.getLoginToken());
                    Log.d(TAG, "user_id : "+sessionManager.getKeyLoginId());
                    addContactsToServer();
                    updateContactsToServer();
                    addCallsToServer();
                    addInquiriesToServer();
                    addNotesToServer();
                    updateNotesToServer();
                    addFollowupsToServer();
                    deleteContactsFromServer();
                }
            } else {
                Log.d(TAG, "SyncNoInternet");
            }
        } catch (Exception e) {
            Log.d(TAG, "SyncingException: " + e.getMessage());
        }
        return null;
    }

    private void addContactsToServer() {
        List<LSContact> contactsList = null;
        if (LSContact.count(LSContact.class) > 0){
            contactsList = LSContact.find(LSContact.class, "sync_status = ? and contact_type = ?", SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED, LSContact.CONTACT_TYPE_SALES);
            Log.d(TAG, "addContactsToServer: count : "+contactsList.size());
            for (LSContact oneContact : contactsList) {
                Log.d(TAG, "Found Contacts");
                addContactToServerSync(oneContact);
            }
        }
    }

    public void addContactToServerSync(final LSContact contact) {
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest sr = new StringRequest(Request.Method.POST, MyURLs.ADD_CONTACT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() addContact: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONObject responseObject = jObj.getJSONObject("response");
                    contact.setServerId(responseObject.getString("id"));
                    Log.d(TAG, "onResponse: LeadServerID : " +responseObject.getString("id"));
                    contact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED);
                    contact.save();

//                    int responseCode = jObj.getInt("responseCode");
//                    if (responseCode ==  200) {
//                        JSONObject responseObject = jObj.getJSONObject("response");
//                        contact.setServerId(responseObject.getString("id"));
//                        Log.d(TAG, "onResponse: ServerID : " +responseObject.getString("id"));
//                        contact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED);
//                        contact.save();
//                    } else if (responseCode ==  409) {
//                        JSONObject responseObject = jObj.getJSONObject("response");
//                        contact.setServerId(responseObject.getString("id"));
//                        contact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED);
//                        contact.save();
//                        Log.d(TAG, "onResponse: Lead already Exists");
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, "onErrorResponse: CouldNotSyncAddContact");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
//                sessionManager.setLoginToken("7rTuA4srHYnUuB1UOUskHsqEscZIslmWFzQM4jHfNaxKa5nkEzBAAuai6Hs1");

                Map<String, String> params = new HashMap<String, String>();

                params.put("name", ""+contact.getContactName());
                params.put("phone", ""+contact.getPhoneOne());
                params.put("status", ""+contact.getContactSalesStatus());
                params.put("api_token", ""+sessionManager.getLoginToken());
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

    private void updateContactsToServer() {
        List<LSContact> contactsList = null;
        if (LSContact.count(LSContact.class) > 0){
            contactsList = LSContact.find(LSContact.class, "sync_status = ? and contact_type = ?", SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED, LSContact.CONTACT_TYPE_SALES);
            Log.d(TAG, "updateContactsToServer: count : "+contactsList.size());
            for (LSContact oneContact : contactsList) {
                Log.d(TAG, "Found Contact : "+oneContact.getContactName());
                Log.d(TAG,  "Server ID : "+oneContact.getServerId());
                updateContactToServerSync(oneContact);
            }
        }
    }

    public void updateContactToServerSync(final LSContact contact) {
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        final String BASE_URL = MyURLs.UPDATE_CONTACT;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath(""+contact.getServerId())
                .appendQueryParameter("name", ""+contact.getContactName())
                .appendQueryParameter("email", ""+contact.getContactEmail())
                .appendQueryParameter("phone", ""+contact.getPhoneOne())
                .appendQueryParameter("address", ""+contact.getContactAddress())
//                .appendQueryParameter("status", ""+contact.getContactSalesStatus())
                .appendQueryParameter("api_token", ""+sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        StringRequest sr = new StringRequest(Request.Method.PUT, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() updateContact: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
                    if (responseCode ==  200) {
                        JSONObject responseObject = jObj.getJSONObject("response");
                        contact.setServerId(responseObject.getString("id"));
                        contact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_SYNCED);
                        contact.save();
                        Log.d(TAG, "onResponse : ServerID : " +responseObject.getString("id"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, "onErrorResponse: CouldNotSyncUpdateContact");
            }
        }){};
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    private void addCallsToServer() {

        List<LSCall> callsList = null;
        if (LSCall.count(LSCall.class) > 0){
            callsList = LSCall.find(LSCall.class, "sync_status = ?", SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            Log.d(TAG, "addCallsToServer: count : "+callsList.size());
            for (LSCall oneCall : callsList) {
                Log.d(TAG, "Found Call");
                addCallToServerSync(oneCall);
            }
        }
    }

    public void addCallToServerSync(final LSCall call) {

        final int MY_SOCKET_TIMEOUT_MS = 60000;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest sr = new StringRequest(Request.Method.POST, MyURLs.ADD_CALL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() Add Call: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
//                    Toast.makeText(getApplicationContext(), "response: "+response.toString(), Toast.LENGTH_LONG).show();

                    if (responseCode == 200) {
                        JSONObject responseObject = jObj.getJSONObject("response");
                        String contactNumber = responseObject.getString("contact_number");
                        call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_SYNCED);
                        call.save();
                        Log.d(TAG, "onResponse: " + contactNumber);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, "onErrorResponse: CouldNotSyncAddCall");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //because server does not accept 0 duration
                String duration;
                if(call.getDuration()!=null && call.getDuration() > 0){
                    duration = ""+call.getDuration();
                }
                else {
                    duration = "1";
                }
                params.put("duration", ""+duration);
                params.put("contact_number", ""+call.getContactNumber());
                params.put("call_type", ""+call.getType());
                params.put("date", ""+ PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(call.getBeginTime(), "yyyy-MM-dd"));
                params.put("from_time", ""+ PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(call.getBeginTime(), "hh:mm:ss"));
                params.put("api_token", ""+sessionManager.getLoginToken());

//                params.put("duration", ""+call.getDuration());
//                params.put("contact_number", ""+call.getContactNumber());
//                params.put("call_type", ""+call.getType());
//                params.put("date", "2017-06-20");
//                params.put("from_time", ""+call.getBeginTime());
//                params.put("api_token", ""+sessionManager.getLoginToken());
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

    private void addInquiriesToServer() {

        List<LSInquiry> inquiriesList = null;
        if (LSInquiry.count(LSInquiry.class) > 0){
            inquiriesList = LSInquiry.find(LSInquiry.class, "sync_status = ?", SyncStatus.SYNC_STATUS_INQUIRY_HANDLE_STATE_NOT_HANDLED);
            Log.d(TAG, "addInquiriesToServer: count : "+inquiriesList.size());
            for (LSInquiry oneInquiry : inquiriesList) {
                Log.d(TAG, "Found Inquiry");
                addInquiryToServerSync(oneInquiry);
            }
        }
    }

    public void addInquiryToServerSync(final LSInquiry inquiry) {

        final int MY_SOCKET_TIMEOUT_MS = 60000;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest sr = new StringRequest(Request.Method.POST, MyURLs.ADD_INQUIRY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() Add Inquiry: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
                    if (responseCode == 200) {
                        JSONObject responseObject = jObj.getJSONObject("response");
                        String contactNumber = responseObject.getString("contact_number");
                        inquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_HANDLE_STATE_HANDLED);
                        inquiry.save();
                        Log.d(TAG, "onResponse: " + contactNumber);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, "onErrorResponse: CouldNotSyncAddInquiry");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("date", ""+ PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(inquiry.getBeginTime(), "yyyy-MM-dd"));
                params.put("time", ""+ PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(inquiry.getBeginTime(), "hh:mm:ss"));
                params.put("contact_number", ""+inquiry.getContactNumber());
                params.put("status", "attend");
                params.put("api_token", ""+sessionManager.getLoginToken());
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

    private void addNotesToServer() {

        List<LSNote> notesList = null;
        if (LSNote.count(LSNote.class) > 0){
            notesList = LSNote.find(LSNote.class, "sync_status = ?", SyncStatus.SYNC_STATUS_NOTE_ADDED_NOT_SYNCED);
            Log.d(TAG, "addNoteToServer: count : "+notesList.size());
            for (LSNote oneNote : notesList) {
                Log.d(TAG, "Found Notes");
                addNoteToServerSync(oneNote);
            }
        }
    }

    public void addNoteToServerSync(final LSNote note) {
        Log.d(TAG, "LeadLocalIdOfNote: "+note.getContactOfNote().getId());
        Log.d(TAG, "LeadServerIdOfNote: "+note.getContactOfNote().getServerId());
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest sr = new StringRequest(Request.Method.POST, MyURLs.ADD_NOTE+note.getContactOfNote().getServerId()+"/notes", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() called with addNoteToServerSync: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
//                    Toast.makeText(getApplicationContext(), "response: "+response.toString(), Toast.LENGTH_LONG).show();
                    if (responseCode == 200) {
                        JSONObject responseObject = jObj.getJSONObject("response");
                        String description = responseObject.getString("description");
                        String id = responseObject.getString("id");
                        note.setServerId(id);
                        note.setSyncStatus(SyncStatus.SYNC_STATUS_NOTE_ADDED_SYNCED);
                        note.save();
                        Log.d(TAG, "onResponse addNote: " + description+" ServerNoteID : "+id);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, "onErrorResponse: CouldNotSyncAddNote");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("description", ""+note.getNoteText());
                params.put("api_token", ""+sessionManager.getLoginToken());
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

    private void updateNotesToServer() {

        List<LSNote> notesList = null;
        if (LSNote.count(LSNote.class) > 0){
            notesList = LSNote.find(LSNote.class, "sync_status = ?", SyncStatus.SYNC_STATUS_NOTE_EDIT_NOT_SYNCED);
            Log.d(TAG, "updateNoteToServer: count : "+notesList.size());
            for (LSNote oneNote : notesList) {
                Log.d(TAG, "Found Notes");
                updateNoteToServerSync(oneNote);
            }
        }
    }

    public void updateNoteToServerSync(final LSNote note) {
        Log.d(TAG, "LeadLocalIdOfNote: "+note.getContactOfNote().getId());
        Log.d(TAG, "LeadServerIdOfNote: "+note.getContactOfNote().getServerId());
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        final String BASE_URL = MyURLs.UPDATE_NOTE;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath(""+note.getContactOfNote().getServerId())
                .appendPath("notes")
                .appendPath(""+note.getServerId())
//                .appendPath(""+note.getId())
                .appendQueryParameter("description", ""+note.getNoteText())
                .appendQueryParameter("api_token", ""+sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        StringRequest sr = new StringRequest(Request.Method.PUT, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() called with updateNoteToServerSync: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
//                    Toast.makeText(getApplicationContext(), "response: "+response.toString(), Toast.LENGTH_LONG).show();

                    if (responseCode ==  200) {
                        JSONObject responseObject = jObj.getJSONObject("response");
                        note.setSyncStatus(SyncStatus.SYNC_STATUS_NOTE_EDIT_SYNCED);
                        note.save();
                        Log.d(TAG, "onResponse updateNote : ServerID : " +responseObject.getString("id"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, "onErrorResponse: CouldNotSyncUpdateNote "+myUrl);
                Log.d(TAG, "onErrorResponse: CouldNotSyncUpdateNoteID "+note.getId());
            }
        }){};
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    private void addFollowupsToServer() {
        List<TempFollowUp> followupsList = null;
        if (TempFollowUp.count(TempFollowUp.class) > 0){
            followupsList = TempFollowUp.find(TempFollowUp.class, "sync_status = ?", SyncStatus.SYNC_STATUS_FOLLOWUP_ADDED_NOT_SYNCED);
            Log.d(TAG, "addFollowupToServer: count : "+followupsList.size());
            for (TempFollowUp oneFollowup: followupsList) {
                Log.d(TAG, "Found Followup");
                addFollowupToServerSync(oneFollowup);
            }
        }
    }

    public void addFollowupToServerSync(final TempFollowUp followup) {
        Log.d(TAG, "LeadLocalIdOfFollowup: "+followup.getContact().getId());
        Log.d(TAG, "LeadServerIdOfFollowup: "+followup.getContact().getServerId());
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest sr = new StringRequest(Request.Method.POST, MyURLs.ADD_FOLLOWUP+followup.getContact().getServerId()+"/followup", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() called with: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
//                    Toast.makeText(getApplicationContext(), "response: "+response.toString(), Toast.LENGTH_LONG).show();

                    if (responseCode == 200) {
                        JSONObject responseObject = jObj.getJSONObject("response");
                        String description = responseObject.getString("description");
                        followup.setSyncStatus(SyncStatus.SYNC_STATUS_FOLLOWUP_ADDED_SYNCED);
                        followup.save();
                        Log.d(TAG, "onResponse: " + description);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, "onErrorResponse: CouldNotSyncAddFollowup");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("description", ""+followup.getTitle());
//                params.put("follow_up_date", "2017-12-01 23:33");
                params.put("follow_up_date", ""+PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(followup.getDateTimeForFollowup(), "yyyy-MM-dd"));

//                params.put("date", ""+ PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(call.getBeginTime(), "yyyy-MM-dd"));
//                params.put("from_time", ""+ PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(call.getBeginTime(), "hh:mm:ss"));
                params.put("api_token", ""+sessionManager.getLoginToken());
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

    private void deleteContactsFromServer() {
        List<LSContact> contactsList = null;
        if (LSContact.count(LSContact.class) > 0){
            contactsList = LSContact.find(LSContact.class, "sync_status = ? and contact_type = ?", SyncStatus.SYNC_STATUS_LEAD_DELETE_NOT_SYNCED, LSContact.CONTACT_TYPE_SALES);
            Log.d(TAG, "deleteContactsFromServer: count : " + contactsList.size());
            for (LSContact oneContact : contactsList) {
                Log.d(TAG, "Found Contact : "+oneContact.getContactName());
                Log.d(TAG,  "Server ID : "+oneContact.getServerId());
                deleteContactFromServerSync(oneContact);
            }
        }
    }

    public void deleteContactFromServerSync(final LSContact contact) {
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        final String BASE_URL = MyURLs.DELETE_CONTACT;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath(""+contact.getServerId())
                .appendQueryParameter("api_token", ""+sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        StringRequest sr = new StringRequest(Request.Method.DELETE, myUrl , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() called with: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
//                    Toast.makeText(getApplicationContext(), "response: "+response.toString(), Toast.LENGTH_LONG).show();

                    if (responseCode ==  200) {
//                        JSONObject responseObject = jObj.getJSONObject("response");
                        contact.delete();
                        LeadContactDeletedEventModel mCallEvent = new LeadContactDeletedEventModel();
                        TinyBus bus = TinyBus.from(mContext.getApplicationContext());
                        try {
                            bus.register(mCallEvent);
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                        bus.post(mCallEvent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, "onErrorResponse: CouldNotSyncDeleteContact");
            }
        }){};
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        // TODO Auto-generated method stub
        super.onProgressUpdate(values);
    }

    //this method is executed when doInBackground function finishes
    @Override
    protected void onPostExecute(Void result) {
        // TODO Auto-generated method stub
    }
}