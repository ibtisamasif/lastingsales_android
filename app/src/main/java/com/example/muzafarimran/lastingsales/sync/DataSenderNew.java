package com.example.muzafarimran.lastingsales.sync;

import android.content.Context;
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
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.utils.NetworkAccess;

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
                    addContactToServer();
//                    addNoteToServer();
                }
            } else {
                Log.d(TAG, "SyncNoInternet");
            }
        } catch (Exception e) {
            Log.d(TAG, "SyncingException: " + e.getMessage());
        }

        return null;
    }

    private void addContactToServer() {
        List<LSContact> contactsList = null;
        if (LSContact.count(LSContact.class) > 0){
            contactsList = LSNote.find(LSContact.class, "sync_status = ? and contact_type = ?", SyncStatus.SYNC_STATUS_NOT_SYNCED, LSContact.CONTACT_TYPE_SALES);
            Log.d(TAG, "addContactsToServer: count : "+contactsList.size());
            for (LSContact oneContact : contactsList) {
                Log.d(TAG, "Found Contacts");
                addContactToServerSync(oneContact);
            }
        }
//        List<LSContact> salesContacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_SALES);
//        if (salesContacts.size() != 0) {
//            for (LSContact oneContacts : salesContacts) {
//                Log.d(TAG, "Found Contacts");
//                addContactToServerSync(oneContacts);
//            }
//        } else {
//            Log.d(TAG,  "No Contact to sync");
//        }
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
//                sessionManager.setLoginToken("cvRSzCteK1yUnKreXzBJTLlMf2fVoIvvLx3JgPPX6EA61GebwuDNQxQqNdbE");

                Map<String, String> params = new HashMap<String, String>();
//                params.put("name", "dummy name");
//                params.put("email", "email@email.com");
//                params.put("phone", "033333333");
//                params.put("address", "123 address");
//                params.put("status", "lead");
//                params.put("user_id", "145");
//                params.put("api_token", "cvRSzCteK1yUnKreXzBJTLlMf2fVoIvvLx3JgPPX6EA61GebwuDNQxQqNdbE");

                params.put("name", ""+contact.getContactName());
                params.put("email", "email@email.com");
                params.put("phone", ""+contact.getPhoneOne());
                params.put("address", "");
                params.put("status", ""+contact.getContactSalesStatus());
                params.put("user_id", ""+sessionManager.getKeyLoginId());
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

    private void addNoteToServer() {

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
//        int id = 145;
//        Long l = new Long(id);
//        note.getContactOfNote().setId(l);
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