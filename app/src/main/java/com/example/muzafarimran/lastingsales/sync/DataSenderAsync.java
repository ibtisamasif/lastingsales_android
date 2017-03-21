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
import com.example.muzafarimran.lastingsales.events.InquiryDeletedEventModel;
import com.example.muzafarimran.lastingsales.events.LeadContactDeletedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSCallRecording;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;
import com.example.muzafarimran.lastingsales.utils.AndroidMultiPartEntity;
import com.example.muzafarimran.lastingsales.utils.AndroidMultiPartEntity.ProgressListener;
import com.example.muzafarimran.lastingsales.utils.NetworkAccess;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.halfbit.tinybus.TinyBus;

public class DataSenderAsync extends AsyncTask<Object, Void, Void> {
    public static final String TAG = "DataSenderAsync";
    Context mContext;
    SessionManager sessionManager;
    long totalSize = 0;

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
                    Log.d(TAG, "Token : " + sessionManager.getLoginToken());
                    Log.d(TAG, "user_id : " + sessionManager.getKeyLoginId());
                    addContactsToServer();
                    updateContactsToServer();
                    addCallsToServer();
                    addInquiriesToServer();
                    updateInquiriesToServer();
                    deleteInquiriesFromServer();
                    addNotesToServer();
                    updateNotesToServer();
//                    addFollowupsToServer();
                    deleteContactsFromServer();
                    if (NetworkAccess.isWifiConnected(mContext)) {
                        addRecordingToServer();
                    }
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
        if (LSContact.count(LSContact.class) > 0) {
            contactsList = LSContact.find(LSContact.class, "sync_status = ? and contact_type = ?", SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED, LSContact.CONTACT_TYPE_SALES);
            Log.d(TAG, "addContactsToServer: count : " + contactsList.size());
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
                    Log.d(TAG, "onResponse: LeadServerID : " + responseObject.getString("id"));
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
                try {
                    JSONObject jObj = new JSONObject(new String(error.networkResponse.data));
                    int responseCode = jObj.getInt("responseCode");
                    if (responseCode == 409) {
                        JSONObject responseObject = jObj.getJSONObject("response");
                        contact.setServerId(responseObject.getString("id"));
                        contact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED);
                        contact.save();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
//                sessionManager.setLoginToken("7rTuA4srHYnUuB1UOUskHsqEscZIslmWFzQM4jHfNaxKa5nkEzBAAuai6Hs1");

                Map<String, String> params = new HashMap<String, String>();

                params.put("name", "" + contact.getContactName());
                if (contact.getContactEmail() != null) {
                    params.put("email", "" + contact.getContactEmail());
                }
                if (contact.getContactAddress() != null) {
                    params.put("address", "" + contact.getContactAddress());
                }
                params.put("phone", "" + contact.getPhoneOne());
                params.put("status", "" + contact.getContactSalesStatus());
                params.put("api_token", "" + sessionManager.getLoginToken());
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
        if (LSContact.count(LSContact.class) > 0) {
            contactsList = LSContact.find(LSContact.class, "sync_status = ? and contact_type = ?", SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED, LSContact.CONTACT_TYPE_SALES);
            Log.d(TAG, "updateContactsToServer: count : " + contactsList.size());
            for (LSContact oneContact : contactsList) {
                Log.d(TAG, "Found Contact : " + oneContact.getContactName());
                Log.d(TAG, "Server ID : " + oneContact.getServerId());
                updateContactToServerSync(oneContact);
            }
        }
    }

    public void updateContactToServerSync(final LSContact contact) {
        String email = "";
        String address = "";

        final int MY_SOCKET_TIMEOUT_MS = 60000;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        if (contact.getContactEmail() != null) {
            email = contact.getContactEmail();
        }
        if (contact.getContactAddress() != null) {
            address = contact.getContactAddress();
        }

        final String BASE_URL = MyURLs.UPDATE_CONTACT;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath("" + contact.getServerId())
                .appendQueryParameter("name", "" + contact.getContactName())
                .appendQueryParameter("email", "" + email)
                .appendQueryParameter("phone", "" + contact.getPhoneOne())
                .appendQueryParameter("address", "" + address)
                .appendQueryParameter("status", "" + contact.getContactSalesStatus())
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        StringRequest sr = new StringRequest(Request.Method.PUT, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() updateContact: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
//                    if (responseCode == 200) {
                    JSONObject responseObject = jObj.getJSONObject("response");
                    contact.setServerId(responseObject.getString("id"));
                    contact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_SYNCED);
                    contact.save();
                    Log.d(TAG, "onResponse : ServerID : " + responseObject.getString("id"));
//                    }
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
        }) {
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    private void addCallsToServer() {

        List<LSCall> callsList = null;
        if (LSCall.count(LSCall.class) > 0) {
            callsList = LSCall.find(LSCall.class, "sync_status = ?", SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            Log.d(TAG, "addCallsToServer: count : " + callsList.size());
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

//                    if (responseCode == 200) {
                    JSONObject responseObject = jObj.getJSONObject("response");
                    String id = responseObject.getString("id");
                    String contactNumber = responseObject.getString("contact_number");
                    call.setServerId(id);
                    call.save();
//                    if (!call.getType().equals(LSCall.CALL_TYPE_MISSED)) {
//                        Log.d(TAG, "onResponse: audiopath: " + call.getAudioPath());
//                        LSCallRecording tempRecording = LSCallRecording.getRecordingByAudioPath(call.getAudioPath());
//                        if (tempRecording != null) {
//                            tempRecording.setServerIdOfCall(id);
//                            tempRecording.save();
//                            Log.d(TAG, "onResponse: ServerIDofCAll: " + tempRecording.getServerIdOfCall());
//                        }
//                    }
                    call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_SYNCED);
                    call.save();
                    Log.d(TAG, "onResponse: " + contactNumber);
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, "onErrorResponse: CouldNotSyncAddCall");
//                try {
//                    JSONObject jObj = new JSONObject(new String(error.networkResponse.data));
//                    int responseCode = jObj.getInt("responseCode");
//                    if (responseCode == 409) {
//                        JSONObject responseObject = jObj.getJSONObject("response");
//                        call.setServerId(responseObject.getString("id"));
//                        call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_SYNCED);
//                        call.save();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //because server does not accept 0 duration
                String duration;
                if (call.getDuration() != null && call.getDuration() > 0) {
                    duration = "" + call.getDuration();
                } else {
                    duration = "1";
                }
                params.put("duration", "" + duration);
                params.put("contact_number", "" + call.getContactNumber());
                params.put("call_type", "" + call.getType());
                params.put("date", "" + PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(call.getBeginTime(), "yyyy-MM-dd"));
                params.put("from_time", "" + PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(call.getBeginTime(), "kk:mm:ss"));
//                Log.d(TAG, "getParams: "+PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(call.getBeginTime(), "kk:mm:ss"));
                params.put("api_token", "" + sessionManager.getLoginToken());
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
        if (LSInquiry.count(LSInquiry.class) > 0) {
            inquiriesList = LSInquiry.find(LSInquiry.class, "sync_status = ?", SyncStatus.SYNC_STATUS_INQUIRY_PENDING_NOT_SYNCED);
            Log.d(TAG, "addInquiriesToServer: count : " + inquiriesList.size());
            for (LSInquiry oneInquiry : inquiriesList) {
                Log.d(TAG, "Found Inquiry" + oneInquiry.getContactNumber());
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
//                    if (responseCode == 200) {
                    JSONObject responseObject = jObj.getJSONObject("response");
                    String id = responseObject.getString("id");
                    String contactNumber = responseObject.getString("contact_number");
                    String status = responseObject.getString("status");
                    inquiry.setServerId(id);
                    inquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_PENDING_SYNCED);
                    if (status.equals(LSInquiry.INQUIRY_STATUS_ATTENDED)) {
                        inquiry.delete();
                    } else {
                        inquiry.save();
                    }
                    Log.d(TAG, "onResponse: " + contactNumber);
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, "onErrorResponse: CouldNotSyncAddInquiry");
                try {
                    JSONObject jObj = new JSONObject(new String(error.networkResponse.data));
                    int responseCode = jObj.getInt("responseCode");
                    JSONObject responseObject = jObj.getJSONObject("response");
                    String id = responseObject.getString("id");
                    String contactNumber = responseObject.getString("contact_number");
                    String status = responseObject.getString("status");
                    inquiry.setServerId(id);
                    inquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_PENDING_SYNCED);
                    if (status.equals(LSInquiry.INQUIRY_STATUS_ATTENDED)) {
                        inquiry.delete();
                    } else {
                        inquiry.save();
                    }
                    Log.d(TAG, "onResponse: addInquiryReSync " + contactNumber);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("date", "" + PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(inquiry.getBeginTime(), "yyyy-MM-dd"));
                params.put("time", "" + PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(inquiry.getBeginTime(), "kk:mm:ss"));
                params.put("contact_number", "" + inquiry.getContactNumber());
                params.put("status", "" + inquiry.getStatus());
                params.put("avg_response_time", "" + inquiry.getAverageResponseTime() / 1000);
                params.put("api_token", "" + sessionManager.getLoginToken());
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


    private void updateInquiriesToServer() {

        List<LSInquiry> inquiriesList = null;
        if (LSInquiry.count(LSInquiry.class) > 0) {
            inquiriesList = LSInquiry.find(LSInquiry.class, "sync_status = ?", SyncStatus.SYNC_STATUS_INQUIRY_ATTENDED_NOT_SYNCED);
            Log.d(TAG, "updateInquiriesToServer: count : " + inquiriesList.size());
            for (LSInquiry oneInquiry : inquiriesList) {
                Log.d(TAG, "Found Inquiry");
                updateInquiryToServerSync(oneInquiry);
            }
        }
    }

    public void updateInquiryToServerSync(final LSInquiry inquiry) {

        final int MY_SOCKET_TIMEOUT_MS = 60000;
        RequestQueue queue = Volley.newRequestQueue(mContext);

        final String BASE_URL = MyURLs.UPDATE_INQUIRY;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath("" + inquiry.getServerId())
                .appendQueryParameter("status", "" + inquiry.getStatus())
                .appendQueryParameter("avg_response_time", "" + inquiry.getAverageResponseTime() / 1000)
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();

        StringRequest sr = new StringRequest(Request.Method.PUT, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() Update Inquiry: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
//                    if (responseCode == 200) {
                    JSONObject responseObject = jObj.getJSONObject("response");
                    String contactNumber = responseObject.getString("contact_number");
                    String avg_response_time = responseObject.getString("avg_response_time");
                    inquiry.delete();
                    Log.d(TAG, "onResponse: ContactNum" + contactNumber);
                    Log.d(TAG, "onResponse: AverageResponseTime: " + avg_response_time);
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, "onErrorResponse: CouldNotSyncUpdateInquiry");
            }
        });
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    private void deleteInquiriesFromServer() {
        List<LSInquiry> inquiriesList = null;
        if (LSInquiry.count(LSInquiry.class) > 0) {
            inquiriesList = LSInquiry.find(LSInquiry.class, "sync_status = ? ", SyncStatus.SYNC_STATUS_INQUIRY_DELETE_NOT_SYNCED);
            Log.d(TAG, "deleteInquiriesFromServer: count : " + inquiriesList.size());
            for (LSInquiry oneInquiry : inquiriesList) {
                Log.d(TAG, "Found Contact : " + oneInquiry.getContactName());
                Log.d(TAG, "Server ID : " + oneInquiry.getServerId());
                deleteInquiriesFromServerSync(oneInquiry);
            }
        }
    }

    public void deleteInquiriesFromServerSync(final LSInquiry inquiry) {
//        Log.d(TAG, "deleteContactFromServerSync: DELETE LEAD SERVER ID : "+contact.getServerId());
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        final String BASE_URL = MyURLs.DELETE_INQUIRY;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath("" + inquiry.getServerId())
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        StringRequest sr = new StringRequest(Request.Method.DELETE, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() delete inquiry called with: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
//                    Log.d(TAG, "onResponse: DeleteInquiry: "+jObj.toString());
//                    if (responseCode == 200) {
//                        JSONObject responseObject = jObj.getJSONObject("response");
                    inquiry.delete();
                    InquiryDeletedEventModel mCallEvent = new InquiryDeletedEventModel();
                    TinyBus bus = TinyBus.from(mContext.getApplicationContext());
                    bus.post(mCallEvent);
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, "onErrorResponse: CouldNotSyncDeleteInquiry");
            }
        }) {
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    private void addNotesToServer() {

        List<LSNote> notesList = null;
        if (LSNote.count(LSNote.class) > 0) {
            notesList = LSNote.find(LSNote.class, "sync_status = ?", SyncStatus.SYNC_STATUS_NOTE_ADDED_NOT_SYNCED);
            Log.d(TAG, "addNoteToServer: count : " + notesList.size());
            for (LSNote oneNote : notesList) {
                Log.d(TAG, "Found Notes");
                addNoteToServerSync(oneNote);
            }
        }
    }

    public void addNoteToServerSync(final LSNote note) {
//        Log.d(TAG, "LeadLocalIdOfNote: " + note.getContactOfNote().getId());
//        Log.d(TAG, "LeadServerIdOfNote: " + note.getContactOfNote().getServerId());
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest sr = new StringRequest(Request.Method.POST, MyURLs.ADD_NOTE + note.getContactOfNote().getServerId() + "/notes", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() called with addNoteToServerSync: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
//                    Toast.makeText(getApplicationContext(), "response: "+response.toString(), Toast.LENGTH_LONG).show();
//                    if (responseCode == 200) {
                    JSONObject responseObject = jObj.getJSONObject("response");
                    String description = responseObject.getString("description");
                    String id = responseObject.getString("id");
                    note.setServerId(id);
                    note.setSyncStatus(SyncStatus.SYNC_STATUS_NOTE_ADDED_SYNCED);
                    note.save();
                    Log.d(TAG, "onResponse addNote: " + description + " ServerNoteID : " + id);

//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, "onErrorResponse: CouldNotSyncAddNote");

                try {
                    JSONObject jObj = new JSONObject(new String(error.networkResponse.data));
                    int responseCode = jObj.getInt("responseCode");
//                    Toast.makeText(getApplicationContext(), "response: "+response.toString(), Toast.LENGTH_LONG).show();
//                    if (responseCode == 200) {
                    JSONObject responseObject = jObj.getJSONObject("response");
                    String description = responseObject.getString("description");
                    String id = responseObject.getString("id");
                    note.setServerId(id);
                    note.setSyncStatus(SyncStatus.SYNC_STATUS_NOTE_ADDED_SYNCED);
                    note.save();
                    Log.d(TAG, "onResponse addNoteReSynced: " + description + " ServerNoteID : " + note.getServerId());

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("description", "" + note.getNoteText());
                params.put("api_token", "" + sessionManager.getLoginToken());
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
        if (LSNote.count(LSNote.class) > 0) {
            notesList = LSNote.find(LSNote.class, "sync_status = ?", SyncStatus.SYNC_STATUS_NOTE_EDIT_NOT_SYNCED);
            Log.d(TAG, "updateNoteToServer: count : " + notesList.size());
            for (LSNote oneNote : notesList) {
                Log.d(TAG, "Found Notes");
                updateNoteToServerSync(oneNote);
            }
        }
    }

    public void updateNoteToServerSync(final LSNote note) {
        Log.d(TAG, "LeadLocalIdOfNote: " + note.getContactOfNote().getId());
        Log.d(TAG, "LeadServerIdOfNote: " + note.getContactOfNote().getServerId());
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        final String BASE_URL = MyURLs.UPDATE_NOTE;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath("" + note.getContactOfNote().getServerId())
                .appendPath("notes")
                .appendPath("" + note.getServerId())
//                .appendPath(""+note.getId())
                .appendQueryParameter("description", "" + note.getNoteText())
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
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

//                    if (responseCode == 200) {
                    JSONObject responseObject = jObj.getJSONObject("response");
                    note.setSyncStatus(SyncStatus.SYNC_STATUS_NOTE_EDIT_SYNCED);
                    note.save();
                    Log.d(TAG, "onResponse updateNote : ServerID : " + responseObject.getString("id"));
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, "onErrorResponse: CouldNotSyncUpdateNote");
            }
        }) {
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    private void addFollowupsToServer() {
        List<TempFollowUp> followupsList = null;
        if (TempFollowUp.count(TempFollowUp.class) > 0) {
            followupsList = TempFollowUp.find(TempFollowUp.class, "sync_status = ?", SyncStatus.SYNC_STATUS_FOLLOWUP_ADDED_NOT_SYNCED);
            Log.d(TAG, "addFollowupToServer: count : " + followupsList.size());
            for (TempFollowUp oneFollowup : followupsList) {
                Log.d(TAG, "Found Followup");
                addFollowupToServerSync(oneFollowup);
            }
        }
    }

    public void addFollowupToServerSync(final TempFollowUp followup) {
        Log.d(TAG, "LeadLocalIdOfFollowup: " + followup.getContact().getId());
        Log.d(TAG, "LeadServerIdOfFollowup: " + followup.getContact().getServerId());
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest sr = new StringRequest(Request.Method.POST, MyURLs.ADD_FOLLOWUP + followup.getContact().getServerId() + "/followup", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() called with: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
//                    Toast.makeText(getApplicationContext(), "response: "+response.toString(), Toast.LENGTH_LONG).show();

//                    if (responseCode == 200) {
                    JSONObject responseObject = jObj.getJSONObject("response");
                    String description = responseObject.getString("description");
                    followup.setSyncStatus(SyncStatus.SYNC_STATUS_FOLLOWUP_ADDED_SYNCED);
                    followup.save();
                    Log.d(TAG, "onResponse: " + description);
//                    }
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
                params.put("description", "" + followup.getTitle());
//                params.put("follow_up_date", "2017-12-01 23:33");
                params.put("follow_up_date", "" + PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(followup.getDateTimeForFollowup(), "yyyy-MM-dd"));

//                params.put("date", ""+ PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(call.getBeginTime(), "yyyy-MM-dd"));
//                params.put("from_time", ""+ PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(call.getBeginTime(), "hh:mm:ss"));
                params.put("api_token", "" + sessionManager.getLoginToken());
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
        if (LSContact.count(LSContact.class) > 0) {
            contactsList = LSContact.find(LSContact.class, "sync_status = ? and contact_type = ?", SyncStatus.SYNC_STATUS_LEAD_DELETE_NOT_SYNCED, LSContact.CONTACT_TYPE_SALES);
            Log.d(TAG, "deleteContactsFromServer: count : " + contactsList.size());
            for (LSContact oneContact : contactsList) {
                Log.d(TAG, "Found Contact : " + oneContact.getContactName());
                Log.d(TAG, "Server ID : " + oneContact.getServerId());
                deleteContactFromServerSync(oneContact);
            }
        }
    }

    public void deleteContactFromServerSync(final LSContact contact) {
//        Log.d(TAG, "deleteContactFromServerSync: DELETE LEAD SERVER ID : "+contact.getServerId());
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        final String BASE_URL = MyURLs.DELETE_CONTACT;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath("" + contact.getServerId())
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        StringRequest sr = new StringRequest(Request.Method.DELETE, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() called with: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
//                    Toast.makeText(getApplicationContext(), "response: "+response.toString(), Toast.LENGTH_LONG).show();

//                    if (responseCode == 200) {
//                        JSONObject responseObject = jObj.getJSONObject("response");
                    contact.delete();
                    LeadContactDeletedEventModel mCallEvent = new LeadContactDeletedEventModel();
                    TinyBus bus = TinyBus.from(mContext.getApplicationContext());
                    bus.post(mCallEvent);
//                    }
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
        }) {
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    public void addRecordingToServer() {

        List<LSCallRecording> recordingList = null;
        if (LSCallRecording.count(LSCallRecording.class) > 0) {
            recordingList = LSCallRecording.findWithQuery(LSCallRecording.class, "Select * from LS_CALL_RECORDING where sync_status = 'recording_not_synced' and server_id_of_call IS NOT NULL");
//            recordingList = LSCallRecording.find(LSCallRecording.class, "sync_status = ?", SyncStatus.SYNC_STATUS_RECORDING_NOT_SYNCED);
            Log.d(TAG, "addRecordingsToServer: count : " + recordingList.size());
            for (LSCallRecording oneRecording : recordingList) {
                Log.d(TAG, "Found Call");
                addRecordingToServer(oneRecording);
            }
//        uploadFile(CallsStatesReceiver.mAudio_FilePath + CallsStatesReceiver.mAudio_FileName + "_outgoing_429683239.amr");
        }
    }

    @SuppressWarnings("deprecation")
    private void addRecordingToServer(LSCallRecording recording) {
        Log.d(TAG, "uploadFile: Path: " + recording.getAudioPath());
//        String filePath = "/storage/emulated/0/Pictures/Android File Upload/myrec.amr";
        String responseString = null;
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(MyURLs.FILE_UPLOAD_URL);
        try {
            Log.d(TAG, "uploadFile: 0");
            AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                    new ProgressListener() {
                        @Override
                        public void transferred(long num) {
                            Log.d(TAG, "uploading..." + (int) ((num / (float) totalSize) * 100));
//                            publishProgress((int) ((num / (float) totalSize) * 100));
                        }
                    });
            File sourceFile = new File(recording.getAudioPath());
            // Adding file data to http body
            entity.addPart("recording_file", new FileBody(sourceFile));
            // Extra parameters if you want to pass to server
            entity.addPart("call_id", new StringBody("" + recording.getServerIdOfCall()));
            entity.addPart("api_token", new StringBody(sessionManager.getLoginToken()));
            totalSize = entity.getContentLength();
            httppost.setEntity(entity);
            // Making server call
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity r_entity = response.getEntity();

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                recording.setSyncStatus(SyncStatus.SYNC_STATUS_RECORDING_SYNCED);
                recording.save();
                // Server response
                responseString = EntityUtils.toString(r_entity);
            } else {
                responseString = "Error occurred! Http Status Code: " + statusCode;
            }
            Log.d(TAG, "uploadFile: ResponseCode: " + statusCode);
            Log.d(TAG, "uploadFile: ResponseString: " + responseString);

        } catch (ClientProtocolException e) {
            responseString = e.toString();
            Log.d(TAG, "ClientProtocolException: " + responseString);
        } catch (FileNotFoundException e) {
            responseString = e.toString();
            Log.d(TAG, "FileNotFoundException: " + responseString);
            recording.delete();
        } catch (IOException e) {
            responseString = e.toString();
            Log.d(TAG, "IOException: " + responseString);
        }
    }

//    public void addCallRecordingsToServer(){
//        String path;
//        File[] myFiles = new File[0];
//        File pathToRecordings = new File(Environment.getExternalStorageDirectory() + AudioFilePath.audioFileDirectoryPath);
//        if(pathToRecordings.exists())
//        {
//            myFiles = pathToRecordings.listFiles();
//        }
//        else {
//            Log.d(TAG,  "17)AudioFile: No files found");
//        }
//        Log.d(TAG,  "17)PathToRecordings: "+pathToRecordings);
//        try {
//            if (myFiles.length != 0) {
//                try {
//                    if (myFiles.length !=0){
//                        for (File file : myFiles) {
//                            try {
//                                path = file.getAbsolutePath();
//                                MediaPlayer mediaPlayer = MediaPlayer.create(mContext, Uri.parse(path));
//                                int file_duration = mediaPlayer.getDuration();
//                                Log.d(TAG,  "17)AudioFile: Duration: "+file_duration+" path: "+path);
//                                uploadFile(path, sessionManager.getLoginToken());
//                            }catch (Exception e){
//                                Log.d(TAG, "17)AudioFileSyncing Deleting Corrupted File: "+e.getMessage());
//                                file.delete();
//                            }
//                        }
//                    }
//                }catch (Exception e){
//                    Log.d(TAG, "17)AudioFileSyncing Exception: "+e.getMessage());
//                }
//            }else {
//                pathToRecordings.delete();
//            }
//        }catch (Exception e){
//            Log.d(TAG, "17)AudioFileSyncing Exception: "+e.getMessage());
//        }
//
//    }
//
//    @SuppressWarnings("deprecation")
//    private String uploadFile(String filePath) {
//        String status = "Other";
//
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        String responseString = null;
//        try {
//            File sourceFile = new File(filePath);
//            FileBody sourceFileBody = new FileBody(new File(filePath));
//
//            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
//            multipartEntityBuilder.addPart("status", new StringBody(status));
//
//            multipartEntityBuilder.addBinaryBody("audiofile",sourceFile);
//
//            Log.d(TAG, "17)Audio file Sending...");
//            responseString = Http_Request.getHttpPostFile(MyURLs.AUDIO_FILE_UPLOAD_URL, params, multipartEntityBuilder);
//            Log.d(TAG, "17)Audio file Response: "+responseString);
//            JSONObject jobj = new JSONObject(responseString);
//
//            int error = jobj.getInt("error");
//            if (error == 0){
//                String fileOutputPath;
//                File pathToRecordings = new File(Environment.getExternalStorageDirectory() + AudioFilePath.audioFileDirectoryOutputPath);
//                fileOutputPath = pathToRecordings.getAbsolutePath();
//
//                Log.d(TAG, "17)VoiceRecord: audio deleted from mobile.");
//            }
//        } catch (IOException e) {
//            responseString = e.toString();
//            Log.d(TAG, "17)VoiceRecord: responseStringIOException: " + responseString);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.d(TAG, "17)VoiceRecord: JSONException: " + e.getMessage());
//        }
//
//        return responseString;
//
//    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    //this method is executed when doInBackground function finishes
    @Override
    protected void onPostExecute(Void result) {
    }
}