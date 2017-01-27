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
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;
import com.example.muzafarimran.lastingsales.utils.NetworkAccess;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSenderNew extends AsyncTask<Object, Void, Void> {
    public static final String TAG = "DataSenderNew";
    Context mContext;
    SessionManager sessionManager;

    public DataSenderNew(Context context1) {
        mContext = context1;
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
//                    addContactsToServer();
//                    addCallsToServer();
//                    addNotesToServer();
//                    addFollowupsToServer();
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
            contactsList = LSContact.find(LSContact.class, "sync_status = ? and contact_type = ?", SyncStatus.SYNC_STATUS_NOT_SYNCED, LSContact.CONTACT_TYPE_SALES);
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
                Log.d(TAG, "onResponse() called with: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
//                    Toast.makeText(getApplicationContext(), "response: "+response.toString(), Toast.LENGTH_LONG).show();

                    if (responseCode ==  200) {
                        JSONObject responseObject = jObj.getJSONObject("response");
                        contact.setSyncStatus(SyncStatus.SYNC_STATUS_SYNCED);
                        contact.save();
                    } else if (responseCode ==  409) {
                        JSONObject responseObject = jObj.getJSONObject("response");
                        contact.setSyncStatus(SyncStatus.SYNC_STATUS_SYNCED);
                        contact.save();
                        Log.d(TAG, "onResponse: Lead already Exists");
                    }
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
                String status = "lost";
                if (contact.getContactSalesStatus().equals(LSContact.SALES_STATUS_PROSTPECT)) {
                    status = "prospect";
                } else if (contact.getContactSalesStatus().equals(LSContact.SALES_STATUS_LEAD)) {
                    status = "lead";
                } else if (contact.getContactSalesStatus().equals(LSContact.SALES_STATUS_CLOSED_WON)) {
                    status = "won";
                } else if (contact.getContactSalesStatus().equals(LSContact.SALES_STATUS_CLOSED_LOST)) {
                    status = "lost";
                }

                Log.d(TAG, "getParams: "+contact.getContactName());
                Log.d(TAG, "getParams: "+contact.getPhoneOne());
                Log.d(TAG, "getParams: "+contact.getContactSalesStatus());

                params.put("name", ""+contact.getContactName());
                params.put("phone", ""+contact.getPhoneOne());
                params.put("status", ""+status);
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

    private void addCallsToServer() {

        List<LSCall> callsList = null;
        if (LSCall.count(LSCall.class) > 0){
            callsList = LSCall.find(LSCall.class, "sync_status = ?", SyncStatus.SYNC_STATUS_NOT_SYNCED);
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
                Log.d(TAG, "onResponse() called with: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
//                    Toast.makeText(getApplicationContext(), "response: "+response.toString(), Toast.LENGTH_LONG).show();

                    if (responseCode == 200) {
                        JSONObject responseObject = jObj.getJSONObject("response");
                        String contactNumber = responseObject.getString("contact_number");
                        call.setSyncStatus(SyncStatus.SYNC_STATUS_SYNCED);
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

    private void addNotesToServer() {

        List<LSNote> notesList = null;
        if (LSNote.count(LSNote.class) > 0){
            notesList = LSNote.find(LSNote.class, "sync_status = ?", SyncStatus.SYNC_STATUS_NOT_SYNCED);
            Log.d(TAG, "addNoteToServer: count : "+notesList.size());
            for (LSNote oneNote : notesList) {
                Log.d(TAG, "Found Notes");
                addNoteToServerSync(oneNote);
            }
        }

//        List<LSContact> salesContactsHavingNotes = (List<LSContact>) LSContact.getAllSalesContactsHavingNotes();
//        if (salesContactsHavingNotes.size() != 0) {
//            Log.d(TAG, "addNoteToServer1: "+salesContactsHavingNotes.size());
//            for (LSContact oneContact : salesContactsHavingNotes) {
//                List<LSNote> allNotesOfThisContact = LSNote.getNotesByContactId(oneContact.getId());
//                if (allNotesOfThisContact != null && allNotesOfThisContact.size() > 0) {
//                    Log.d(TAG, "addNoteToServer2: "+allNotesOfThisContact.size());
//                    for (LSNote oneNote : allNotesOfThisContact) {
//                        Log.d(TAG, "Found Notes");
//                        addNoteToServerSync(oneNote);
//                    }
//                }
//            }
//        } else {
//            Log.d(TAG, "No Contact to sync Note");
//        }
    }

    public void addNoteToServerSync(final LSNote note) {
        Log.d(TAG, "LeadIdOfNote: "+note.getContactOfNote().getId());
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest sr = new StringRequest(Request.Method.POST, MyURLs.ADD_NOTE+"/"+note.getContactOfNote().getId()+"/notes", new Response.Listener<String>() {
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
                        note.setSyncStatus(SyncStatus.SYNC_STATUS_SYNCED);
                        note.save();
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
                Log.d(TAG, "onErrorResponse: CouldNotSyncAddNote");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
//                sessionManager.setLoginToken("rWD5qNG9yzogbFLBV7pkuMp2pNNxG1zMmmJ8Fy3OTsiNLrpXcEsEz5YH03bS");
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

    private void addFollowupsToServer() {

        List<TempFollowUp> followupsList = null;
        if (TempFollowUp.count(TempFollowUp.class) > 0){
            followupsList = TempFollowUp.find(TempFollowUp.class, "sync_status = ?", SyncStatus.SYNC_STATUS_NOT_SYNCED);
            Log.d(TAG, "addFollowupToServer: count : "+followupsList.size());
            for (TempFollowUp oneFollowup: followupsList) {
                Log.d(TAG, "Found Followup");
                addFollowupToServerSync(oneFollowup);
            }
        }
    }

    public void addFollowupToServerSync(final TempFollowUp followup) {
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest sr = new StringRequest(Request.Method.POST, MyURLs.ADD_FOLLOWUP+"/"+followup.getContact().getId()+"/followup", new Response.Listener<String>() {
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
                        followup.setSyncStatus(SyncStatus.SYNC_STATUS_SYNCED);
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
            contactsList = LSContact.find(LSContact.class, "sync_status = ? and contact_type = ?", SyncStatus.SYNC_STATUS_DELETE_REQUIRE, LSContact.CONTACT_TYPE_SALES);
            Log.d(TAG, "deleteContactsFromServer: count : " + contactsList.size());
            for (LSContact oneContact : contactsList) {
                Log.d(TAG, "Found Contact : "+oneContact.getContactName());
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
                .appendPath("201") //contact.getServerId();
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