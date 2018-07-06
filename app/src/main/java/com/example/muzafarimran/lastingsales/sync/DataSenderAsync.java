package com.example.muzafarimran.lastingsales.sync;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.events.DealAddedEventModel;
import com.example.muzafarimran.lastingsales.events.InquiryDeletedEventModel;
import com.example.muzafarimran.lastingsales.events.ContactDeletedEventModel;
import com.example.muzafarimran.lastingsales.events.LeadContactAddedEventModel;
import com.example.muzafarimran.lastingsales.events.NoteAddedEventModel;
import com.example.muzafarimran.lastingsales.listeners.PostExecuteListener;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSDeal;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.providers.models.LSOrganization;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;
import com.example.muzafarimran.lastingsales.utils.NetworkAccess;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.halfbit.tinybus.TinyBus;

public class DataSenderAsync {
    public static final String TAG = "DataSenderAsync";
//    private static final String TAG = "AppInitializationTest";

    private static DataSenderAsync instance = null;
    private static int currentState = 1;
    private static final int IDLE = 1;
    private static final int PENDING = 2;
    private static boolean firstThreadIsRunning = false;
    private Context mContext;
    private SessionManager sessionManager;
    private long totalSize = 0;
    private static RequestQueue queue;
    private final int MY_TIMEOUT_MS = 30000;
    private final int MY_MAX_RETRIES = 0;
    private PostExecuteListener DataSenderOnPostExecuteListener = null;

    public PostExecuteListener getDataSenderOnPostExecuteListener() {
        return DataSenderOnPostExecuteListener;
    }

    public void setDataSenderOnPostExecuteListener(PostExecuteListener dataSenderOnPostExecuteListener) {
        this.DataSenderOnPostExecuteListener = dataSenderOnPostExecuteListener;
    }

    protected DataSenderAsync(Context context) {
        Log.d(TAG, "DataSenderAsync: ==========================================================================================================================");
        mContext = context;
        queue = Volley.newRequestQueue(mContext);
        firstThreadIsRunning = false;
    }

    public static DataSenderAsync getInstance(Context context) {
        final Context appContext = context.getApplicationContext();
        if (instance == null) {
//            synchronized ((Object) firstThreadIsRunning){
            if (!firstThreadIsRunning) {
                firstThreadIsRunning = true;
                instance = new DataSenderAsync(appContext);
            }
//            }
        }
        return instance;
    }

    public void run() {
        Log.d(TAG, "run: ");
        if (currentState == IDLE) {
            currentState = PENDING;
            Log.d(TAG, "run: InsideRUNING" + this.toString());
            queue.setmAllFinishedListener(new RequestQueue.AllFinishedListener() {
                @Override
                public void onAllFinished() {
                    currentState = IDLE;
                    Log.d(TAG, "onRequestFinished: EVERYTHING COMPLETED");
                }
            });
            new AsyncTask<Object, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    sessionManager = new SessionManager(mContext);
                    Log.d(TAG, "DataSenderAsync onPreExecute: ");
                }

                @Override
                protected Void doInBackground(Object... params) {
                    try {
                        if (NetworkAccess.isNetworkAvailable(mContext)) {
                            if (sessionManager.isUserSignedIn() && sessionManager.getCanSync()) {
                                Log.d(TAG, "Syncing");
                                Log.d(TAG, "Token : " + sessionManager.getLoginToken());
                                Log.d(TAG, "user_id : " + sessionManager.getKeyLoginId());
                                addContactsToServer();
                                updateContactsToServer();
                                deleteContactsFromServer();
                                addOrganizationsToServer();
                                updateOrganizationsToServer();
                                deleteOrganizationsFromServer();
                                addDealToServer();
                                updateDealsToServer();
                                deleteDealsFromServer();
                                addCallsToServer();
                                addInquiriesToServer();
                                updateInquiriesToServer();
                                deleteInquiriesFromServer();
                                addNotesToServer();
                                updateNotesToServer();
                                deleteNotesFromServer();
                                addFollowupsToServer();
//                            if (NetworkAccess.isWifiConnected(mContext)) {
//                                addRecordingToServer();
//                            }
                            }else {
                                Toast.makeText(mContext, ".", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "SyncNoInternet");
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "SyncingException: " + e.getMessage());
                    }
                    return null;
                }

                @Override
                protected void onProgressUpdate(Void... values) {
                    super.onProgressUpdate(values);
                }

                //this method is executed when doInBackground function finishes
                @Override
                protected void onPostExecute(Void result) {
                    if (DataSenderOnPostExecuteListener != null) {
                        Log.d(TAG, "DataSenderAsync onPostExecuteListener:");
                        DataSenderOnPostExecuteListener.onPostExecuteListener();
                    }
                    queue.isIdle();
//                    if (currentState != PENDING) {
//                        Log.d(TAG, "onPostExecuteListener: currentState: " + currentState);
//                        currentState = IDLE;
//                    }
                }
            }.execute();
        } else {
            Log.d(TAG, "run: NotRunning");
        }
    }

    private void addContactsToServer() {
        List<LSContact> contactsList = null;
        if (LSContact.count(LSContact.class) > 0) {
            contactsList = LSContact.find(LSContact.class, "sync_status = ? ", SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
            Log.d(TAG, "addContactsToServer: count : " + contactsList.size());
            for (LSContact oneContact : contactsList) {
                Log.d(TAG, "Found Contacts " + oneContact.getPhoneOne());
                addContactToServerSync(oneContact);
            }
        }
    }

    private void addContactToServerSync(final LSContact contact) {
        currentState = PENDING;
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
                Log.d(TAG, "onErrorResponse: CouldNotSyncAddContact");
                try {
                    if (error != null) {
                        if (error.networkResponse != null) {
                            Log.d(TAG, "onErrorResponse: error.networkResponse: " + error.networkResponse);
                            if (error.networkResponse.statusCode == 409) {
                                JSONObject jObj = new JSONObject(new String(error.networkResponse.data));
                                int responseCode = jObj.getInt("responseCode");
                                if (responseCode == 409) {
                                    Log.d(TAG, "onErrorResponse: responseCode == 409");
                                    JSONObject responseObject = jObj.getJSONObject("response");
                                    contact.setServerId(responseObject.getString("id"));
                                    contact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED);
                                    contact.save();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();

                params.put("name", "" + contact.getContactName());

                if (contact.getContactEmail() != null) {
                    params.put("email", "" + contact.getContactEmail());
                }
                if (contact.getContactAddress() != null) {
                    params.put("address", "" + contact.getContactAddress());
                }
                if (contact.getDynamic() != null) {
                    params.put("dynamic_values", "" + contact.getDynamic());
                }
                params.put("phone", "" + contact.getPhoneOne());
                params.put("status", "" + contact.getContactSalesStatus());
                params.put("api_token", "" + sessionManager.getLoginToken());
                params.put("lead_type", "" + contact.getContactType());
                Log.d(TAG, "getParams: addContactToServerSync " + params);
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_TIMEOUT_MS,
                MY_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    private void updateContactsToServer() {
        List<LSContact> contactsList = null;
        if (LSContact.count(LSContact.class) > 0) {
            contactsList = LSContact.find(LSContact.class, "sync_status = ? ", SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
            Log.d(TAG, "updateContactsToServer: count : " + contactsList.size());
            for (LSContact oneContact : contactsList) {
                Log.d(TAG, "Found Contact : " + oneContact.getContactName());
                Log.d(TAG, "Server ID : " + oneContact.getServerId());
                updateContactToServerSync(oneContact);
            }
        }
    }

    private void updateContactToServerSync(final LSContact contact) {
        currentState = PENDING;
        String email = "";
        String address = "";
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
                .appendQueryParameter("lead_type", "" + contact.getContactType())
                .appendQueryParameter("dynamic_values", "" + contact.getDynamic())
                .build();
        final String myUrl = builtUri.toString();
        Log.d(TAG, "updateContactToServerSync: myUrl: " + myUrl);
        StringRequest sr = new StringRequest(Request.Method.PUT, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() updateContact: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
//                    int responseCode = jObj.getInt("responseCode");
//                    if (responseCode == 200) {
                    JSONObject responseObject = jObj.getJSONObject("response");
                    contact.setServerId(responseObject.getString("id"));
                    contact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_SYNCED);
                    contact.save();
                    Log.d(TAG, "onResponse : ServerIDofContact : " + responseObject.getString("id"));
                    LeadContactAddedEventModel mCallEvent = new LeadContactAddedEventModel();
                    TinyBus bus = TinyBus.from(mContext.getApplicationContext());
                    bus.post(mCallEvent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: CouldNotSyncUpdateContact");
            }
        }) {
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_TIMEOUT_MS,
                MY_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    private void deleteContactsFromServer() {
        List<LSContact> contactsList = null;
        if (LSContact.count(LSContact.class) > 0) {
            contactsList = LSContact.find(LSContact.class, "sync_status = ? ", SyncStatus.SYNC_STATUS_LEAD_DELETE_NOT_SYNCED);
            Log.d(TAG, "deleteContactsFromServer: count : " + contactsList.size());
            for (LSContact oneContact : contactsList) {
                Log.d(TAG, "Found Contact : " + oneContact.getContactName());
                Log.d(TAG, "Server ID : " + oneContact.getServerId());
                deleteContactFromServerSync(oneContact);
            }
        }
    }

    private void deleteContactFromServerSync(final LSContact contact) {
        currentState = PENDING;
//        Log.d(TAG, "deleteContactFromServerSync: DELETE LEAD SERVER ID : "+contact.getServerId());
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
                Log.d(TAG, "onResponse() called with: response (deleteContact) = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
//                    Toast.makeText(getApplicationContext(), "response: "+response.toString(), Toast.LENGTH_LONG).show();

//                    if (responseCode == 200) {
//                        JSONObject responseObject = jObj.getJSONObject("response");
                    contact.delete();
                    ContactDeletedEventModel mCallEvent = new ContactDeletedEventModel();
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
                Log.d(TAG, "onErrorResponse: CouldNotSyncDeleteContact");
                try {
                    if (error != null) {
                        if (error.networkResponse != null) {
                            JSONObject jObj = new JSONObject(new String(error.networkResponse.data));
                            int responseCode = jObj.getInt("responseCode");
                            if (responseCode == 259) {
                                Log.d(TAG, "onErrorResponse: responseCode == 259 deleted");
                                contact.delete();
                                ContactDeletedEventModel mCallEvent = new ContactDeletedEventModel();
                                TinyBus bus = TinyBus.from(mContext.getApplicationContext());
                                bus.post(mCallEvent);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_TIMEOUT_MS,
                MY_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    private void addOrganizationsToServer() {
        List<LSOrganization> organizationsList = null;
        if (LSOrganization.count(LSOrganization.class) > 0) {
            organizationsList = LSOrganization.find(LSOrganization.class, "sync_status = ? ", SyncStatus.SYNC_STATUS_ORGANIZATION_ADD_NOT_SYNCED);
            Log.d(TAG, "addOrganizationToServer: count : " + organizationsList.size());
            for (LSOrganization oneOrganization : organizationsList) {
                Log.d(TAG, "Found Organizations " + oneOrganization.getName());
                addOrganizationToServerSync(oneOrganization);
            }
        }
    }

    private void addOrganizationToServerSync(final LSOrganization organization) {
        currentState = PENDING;
        StringRequest sr = new StringRequest(Request.Method.POST, MyURLs.ADD_ORGANIZATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() addOrganization: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONObject responseObject = jObj.getJSONObject("response");
                    organization.setServerId(responseObject.getString("id"));
                    Log.d(TAG, "onResponse: OrganizationServerID : " + responseObject.getString("id"));
                    organization.setSyncStatus(SyncStatus.SYNC_STATUS_ORGANIZATION_ADD_SYNCED);
                    organization.save();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: CouldNotSyncAddOrganization");
                try {
                    if (error != null) {
                        if (error.networkResponse != null) {
                            Log.d(TAG, "onErrorResponse: error.networkResponse: " + error.networkResponse);
                            if (error.networkResponse.statusCode == 409) {
                                JSONObject jObj = new JSONObject(new String(error.networkResponse.data));
                                int responseCode = jObj.getInt("responseCode");
                                if (responseCode == 409) {
                                    Log.d(TAG, "onErrorResponse: responseCode == 409");
                                    JSONObject responseObject = jObj.getJSONObject("response");
                                    organization.setServerId(responseObject.getString("id"));
                                    organization.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED);
                                    organization.save();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();

                params.put("name", "" + organization.getName());

                if (organization.getEmail() != null) {
                    params.put("email", "" + organization.getEmail());
                }
                if (organization.getAddress() != null) {
                    params.put("address", "" + organization.getAddress());
                }
                if (organization.getDynamicValues() != null) {
                    params.put("dynamic_values", "" + organization.getDynamicValues());
                }
                params.put("phone", "" + organization.getPhone());
                params.put("api_token", "" + sessionManager.getLoginToken());
                Log.d(TAG, "getParams: addOrganizationToServerSync " + params);
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_TIMEOUT_MS,
                MY_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    private void updateOrganizationsToServer() {
        List<LSOrganization> organizationsList = null;
        if (LSOrganization.count(LSOrganization.class) > 0) {
            organizationsList = LSOrganization.find(LSOrganization.class, "sync_status = ? ", SyncStatus.SYNC_STATUS_ORGANIZATION_UPDATE_NOT_SYNCED);
            Log.d(TAG, "updateOrganizationsToServer: count : " + organizationsList.size());
            for (LSOrganization oneOrganization : organizationsList) {
                Log.d(TAG, "Found Organization : " + oneOrganization.getName());
                Log.d(TAG, "Server ID : " + oneOrganization.getServerId());
                updateOrganizationToServerSync(oneOrganization);
            }
        }
    }

    private void updateOrganizationToServerSync(final LSOrganization contact) {
        currentState = PENDING;
        String email = "";
        String address = "";
        if (contact.getEmail() != null) {
            email = contact.getEmail();
        }
        if (contact.getAddress() != null) {
            address = contact.getAddress();
        }
        final String BASE_URL = MyURLs.UPDATE_ORGANIZATION;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath("" + contact.getServerId())
                .appendQueryParameter("name", "" + contact.getName())
                .appendQueryParameter("email", "" + email)
                .appendQueryParameter("phone", "" + contact.getPhone())
                .appendQueryParameter("address", "" + address)
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .appendQueryParameter("dynamic_values", "" + contact.getDynamicValues())
                .build();
        final String myUrl = builtUri.toString();
        Log.d(TAG, "updateOrganizationToServerSync: myUrl: " + myUrl);
        StringRequest sr = new StringRequest(Request.Method.PUT, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() updateOrganization: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
//                    int responseCode = jObj.getInt("responseCode");
//                    if (responseCode == 200) {
                    JSONObject responseObject = jObj.getJSONObject("response");
                    contact.setServerId(responseObject.getString("id"));
                    contact.setSyncStatus(SyncStatus.SYNC_STATUS_ORGANIZATION_UPDATE_SYNCED);
                    contact.save();
                    Log.d(TAG, "onResponse : ServerIDofOrganization : " + responseObject.getString("id"));
//                    LeadContactAddedEventModel mCallEvent = new LeadContactAddedEventModel();
//                    TinyBus bus = TinyBus.from(mContext.getApplicationContext());
//                    bus.post(mCallEvent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: CouldNotSyncUpdateOrganization");
            }
        }) {
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_TIMEOUT_MS,
                MY_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    private void deleteOrganizationsFromServer() {
        List<LSOrganization> organizationsList = null;
        if (LSOrganization.count(LSOrganization.class) > 0) {
            organizationsList  = LSOrganization.find(LSOrganization.class, "sync_status = ? ", SyncStatus.SYNC_STATUS_ORGANIZATION_DELETE_NOT_SYNCED);
            Log.d(TAG, "deleteOrganizationsFromServer: count : " + organizationsList  .size());
            for (LSOrganization oneOrganization : organizationsList) {
                Log.d(TAG, "Found Organization : " + oneOrganization.getName());
                Log.d(TAG, "Server ID : " + oneOrganization.getServerId());
                deleteOrganizationFromServerSync(oneOrganization);
            }
        }
    }

    private void deleteOrganizationFromServerSync(final LSOrganization organization) {
        currentState = PENDING;
//        Log.d(TAG, "deleteOrganizationFromServerSync: DELETE ORGANIZATION SERVER ID : "+organization.getServerId());
        final String BASE_URL = MyURLs.DELETE_ORGANIZATION;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath("" + organization.getServerId())
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        StringRequest sr = new StringRequest(Request.Method.DELETE, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() called with: response (deleteOrganization) = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
//                    Toast.makeText(getApplicationContext(), "response: "+response.toString(), Toast.LENGTH_LONG).show();

//                    if (responseCode == 200) {
//                        JSONObject responseObject = jObj.getJSONObject("response");
                    organization.delete();
//                    ContactDeletedEventModel mCallEvent = new ContactDeletedEventModel();
//                    TinyBus bus = TinyBus.from(mContext.getApplicationContext());
//                    bus.post(mCallEvent);
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: CouldNotSyncDeleteOrganization");
                try {
                    if (error != null) {
                        if (error.networkResponse != null) {
                            JSONObject jObj = new JSONObject(new String(error.networkResponse.data));
                            int responseCode = jObj.getInt("responseCode");
                            if (responseCode == 259) {
                                Log.d(TAG, "onErrorResponse: responseCode == 259 deleted");
                                organization.delete();
//                                ContactDeletedEventModel mCallEvent = new ContactDeletedEventModel();
//                                TinyBus bus = TinyBus.from(mContext.getApplicationContext());
//                                bus.post(mCallEvent);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_TIMEOUT_MS,
                MY_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    private void addDealToServer() {
        List<LSDeal> dealsList = null;
        if (LSDeal.count(LSDeal.class) > 0) {
            dealsList = LSDeal.find(LSDeal.class, "sync_status = ? ", SyncStatus.SYNC_STATUS_DEAL_ADD_NOT_SYNCED);
            Log.d(TAG, "addDealToServer: count : " + dealsList.size());
            for (LSDeal oneDeal : dealsList) {
                Log.d(TAG, "Found Deals " + oneDeal.getName());
                addDealToServerSync(oneDeal);
            }
        }
    }

    private void addDealToServerSync(final LSDeal deal) {
        currentState = PENDING;
        StringRequest sr = new StringRequest(Request.Method.POST, MyURLs.ADD_DEAL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() addDeal: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONObject responseObject = jObj.getJSONObject("response");
                    deal.setServerId(responseObject.getString("id"));
                    Log.d(TAG, "onResponse: addDealServerID : " + responseObject.getString("id"));
                    deal.setSyncStatus(SyncStatus.SYNC_STATUS_DEAL_ADD_SYNCED);
                    deal.save();
                    TinyBus.from(mContext.getApplicationContext()).post(new DealAddedEventModel());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: CouldNotSyncAddDeal");
                try {
                    if (error != null) {
                        if (error.networkResponse != null) {
                            Log.d(TAG, "onErrorResponse: error.networkResponse: " + error.networkResponse);
                            if (error.networkResponse.statusCode == 409) {
                                JSONObject jObj = new JSONObject(new String(error.networkResponse.data));
                                int responseCode = jObj.getInt("responseCode");
                                if (responseCode == 409) {
                                    Log.d(TAG, "onErrorResponse: responseCode == 409");
                                    JSONObject responseObject = jObj.getJSONObject("response");
                                    deal.setServerId(responseObject.getString("id"));
                                    deal.setSyncStatus(SyncStatus.SYNC_STATUS_DEAL_ADD_SYNCED);
                                    deal.save();
                                    TinyBus.from(mContext.getApplicationContext()).post(new DealAddedEventModel());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", "" + deal.getName());
                if (deal.getDynamic() != null) {
                    params.put("dynamic_values", "" + deal.getDynamic());
                }
                params.put("status", "" + deal.getStatus());
                params.put("lead_id", "" + deal.getContact().getServerId()); // TODO can crash here
                params.put("workflow_id", "" + deal.getWorkflowId());
                params.put("workflow_stage_id", "" + deal.getWorkflowStageId());
                params.put("is_private", "" + deal.getIsPrivate());
                params.put("api_token", "" + sessionManager.getLoginToken());
                Log.d(TAG, "getParams: addDealToServerSync " + params);
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_TIMEOUT_MS,
                MY_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    private void updateDealsToServer() {
        List<LSDeal> dealsList = null;
        if (LSDeal.count(LSDeal.class) > 0) {
            dealsList = LSDeal.find(LSDeal.class, "sync_status = ? ", SyncStatus.SYNC_STATUS_DEAL_UPDATE_NOT_SYNCED);
            Log.d(TAG, "updateDealsToServer: count : " + dealsList.size());
            for (LSDeal oneDeal : dealsList) {
                Log.d(TAG, "Found Deal : " + oneDeal.getName());
                Log.d(TAG, "Server ID : " + oneDeal.getServerId());
                updateDealToServerSync(oneDeal);
            }
        }
    }

    private void updateDealToServerSync(final LSDeal deal) {
        currentState = PENDING;
        final String BASE_URL = MyURLs.UPDATE_DEAL;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath("" + deal.getServerId())
                .appendQueryParameter("name", "" + deal.getName())
                .appendQueryParameter("status", "" + deal.getStatus())
                .appendQueryParameter("dynamic_values", "" + deal.getDynamic())
                .appendQueryParameter("workflow_id", "" + deal.getWorkflowId())
                .appendQueryParameter("workflow_stage_id", "" + deal.getWorkflowStageId())
                .appendQueryParameter("dynamic_values", "" + deal.getDynamic())
                .appendQueryParameter("is_private", "" + deal.getIsPrivate())
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        Log.d(TAG, "updateDealToServerSync: myUrl: " + myUrl);
        StringRequest sr = new StringRequest(Request.Method.PUT, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() updateDeal: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
//                    int responseCode = jObj.getInt("responseCode");
//                    if (responseCode == 200) {
                    JSONObject responseObject = jObj.getJSONObject("response");
                    deal.setServerId(responseObject.getString("id"));
                    deal.setSyncStatus(SyncStatus.SYNC_STATUS_DEAL_UPDATE_SYNCED);
                    deal.save();
                    Log.d(TAG, "onResponse : updateDealServerId : " + responseObject.getString("id"));
                    TinyBus.from(mContext.getApplicationContext()).post(new DealAddedEventModel());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: CouldNotSyncUpdateDeal");
                TinyBus.from(mContext.getApplicationContext()).post(new DealAddedEventModel());
            }
        }) {
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_TIMEOUT_MS,
                MY_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    private void deleteDealsFromServer() {
        List<LSDeal> dealsList = null;
        if (LSDeal.count(LSDeal.class) > 0) {
            dealsList = LSDeal.find(LSDeal.class, "sync_status = ? ", SyncStatus.SYNC_STATUS_DEAL_DELETE_NOT_SYNCED);
            Log.d(TAG, "deleteDealsFromServer: count : " + dealsList.size());
            for (LSDeal oneDeal : dealsList) {
//                Log.d(TAG, "Found Deal : " + oneDeal.getName());
//                Log.d(TAG, "Server ID : " + oneDeal.getServerId());
                deleteDealFromServerSync(oneDeal);
            }
        }
    }

    private void deleteDealFromServerSync(final LSDeal deal) {
        currentState = PENDING;
//        Log.d(TAG, "deleteDealFromServerSync: DELETE LEAD SERVER ID : "+deal.getServerId());
        final String BASE_URL = MyURLs.DELETE_DEAL;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath("" + deal.getServerId())
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        StringRequest sr = new StringRequest(Request.Method.DELETE, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() called with: response (deleteDeal) = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
//                    int responseCode = jObj.getInt("responseCode");
//                    if (responseCode == 200) {
//                        JSONObject responseObject = jObj.getJSONObject("response");
//                }
                    deal.delete();
                    TinyBus.from(mContext.getApplicationContext()).post(new DealAddedEventModel());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: CouldNotSyncDeleteDeal");
                try {
                    if (error != null) {
                        if (error.networkResponse != null) {
                            if (error.networkResponse.statusCode == 404) {
                                JSONObject jObj = new JSONObject(new String(error.networkResponse.data));
                                int responseCode = jObj.getInt("responseCode");
                                if (responseCode == 450001) {
                                    Log.d(TAG, "onErrorResponse: responseCode == 450001 deal deleted");
                                    deal.delete();
                                    TinyBus.from(mContext.getApplicationContext()).post(new DealAddedEventModel());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_TIMEOUT_MS,
                MY_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    private void addCallsToServer() {

        List<LSCall> callsList = null;
        if (LSCall.count(LSCall.class) > 0) {
            callsList = LSCall.find(LSCall.class, "sync_status = ?", SyncStatus.SYNC_STATUS_CALL_ADD_NOT_SYNCED);
            Log.d(TAG, "addCallsToServer: count : " + callsList.size());
            for (LSCall oneCall : callsList) {
                Log.d(TAG, "Found Call " + oneCall.getContactNumber());
                addCallToServerSync(oneCall);
            }
        }
    }

    private void addCallToServerSync(final LSCall call) {
        currentState = PENDING;
        StringRequest sr = new StringRequest(Request.Method.POST, MyURLs.ADD_CALL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() Add Call: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
//                    Toast.makeText(getApplicationContext(), "response: "+response.toString(), Toast.LENGTH_LONG).show();

//                    if (responseCode == 200) {
                    JSONObject responseObject = jObj.getJSONObject("response"); //crashed here check response properly Caused by java.lang.OutOfMemoryError: Could not allocate JNI Env //onResponse() Add Call: response = [{"responseCode":"200","response":"error while generating message"}]
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
//                Log.d(TAG, "onErrorResponse: error.networkResponse.data " +(new String(error.networkResponse.data)));
                Log.d(TAG, "onErrorResponse: CouldNotSyncAddCall");
                try {
                    if (error.networkResponse != null) {
                        Log.d(TAG, "onErrorResponse: statusCode: " + error.networkResponse.statusCode);
                        if (error.networkResponse.statusCode == 409) {
//                            call.setServerId(responseObject.getString("id")); // Not needed
                            call.setSyncStatus(SyncStatus.SYNC_STATUS_CALL_ADD_SYNCED);
                            call.save();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //because server does not accept 0 duration
//                String duration;
//                if (call.getDuration() != null && call.getDuration() > 0) {
//                    duration = "" + call.getDuration();
//                } else {
//                    duration = "1";
//                }
                params.put("duration", "" + call.getDuration());
//                params.put("duration", "" + duration);
                params.put("contact_number", "" + call.getContactNumber());
                params.put("call_type", "" + call.getType());
                params.put("date", "" + PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(call.getBeginTime(), "yyyy-MM-dd"));
                params.put("from_time", "" + PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(call.getBeginTime(), "kk:mm:ss"));
                params.put("api_token", "" + sessionManager.getLoginToken());
                Log.d(TAG, "getParams: " + params.toString());
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_TIMEOUT_MS,
                MY_MAX_RETRIES,
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

    private void addInquiryToServerSync(final LSInquiry inquiry) {
        currentState = PENDING;
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
                        InquiryDeletedEventModel mCallEvent = new InquiryDeletedEventModel();
                        TinyBus bus = TinyBus.from(mContext.getApplicationContext());
                        bus.post(mCallEvent);
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
                Log.d(TAG, "onErrorResponse: CouldNotSyncAddInquiry");
                try {
                    if (error != null) {
                        if (error.networkResponse != null) {
                            Log.d(TAG, "onErrorResponse: error.networkResponse.data: " + error.networkResponse.data);
                            Log.d(TAG, "onErrorResponse: error.networkResponse.statusCode: " + error.networkResponse.statusCode);
                            JSONObject jObj = new JSONObject(new String(error.networkResponse.data));
//                    int responseCode = jObj.getInt("responseCode");
                            JSONObject responseObject = jObj.getJSONObject("response");
                            String id = responseObject.getString("id");
                            String contactNumber = responseObject.getString("contact_number");
                            String status = responseObject.getString("status");
                            inquiry.setServerId(id);
                            inquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_PENDING_SYNCED);
                            if (status.equals(LSInquiry.INQUIRY_STATUS_ATTENDED)) {
                                inquiry.delete();
                                InquiryDeletedEventModel mCallEvent = new InquiryDeletedEventModel();
                                TinyBus bus = TinyBus.from(mContext.getApplicationContext());
                                bus.post(mCallEvent);
                            } else {
                                inquiry.save();
                            }
                            Log.d(TAG, "onResponse: addInquiryReSync " + contactNumber);
                        }
                    }
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
                Log.d(TAG, "addInquiryToServerSync getParams: " + params);
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_TIMEOUT_MS,
                MY_MAX_RETRIES,
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

    private void updateInquiryToServerSync(final LSInquiry inquiry) {
        currentState = PENDING;
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
                Log.d(TAG, "onErrorResponse: CouldNotSyncUpdateInquiry");
                try {
                    if (error != null) {
                        if (error.networkResponse != null) {
                            Log.d(TAG, "onErrorResponse: error.networkResponse: " + error.networkResponse);
                            Log.d(TAG, "onErrorResponse: error.networkResponse.statusCode: " + error.networkResponse.statusCode);
                            if (error.networkResponse.statusCode == 401) { // unauthorized check token
                                Log.e(TAG, "onErrorResponse: 401");
                            } else if (error.networkResponse.statusCode == 412) { // Inquiry record not found
                                JSONObject jObj = new JSONObject(new String(error.networkResponse.data));
                                int responseCode = jObj.getInt("responseCode");
                                String responseObject = jObj.getString("response");
                                if (responseCode == 236) {
                                    Log.d(TAG, "onErrorResponse: " + responseObject);
                                    inquiry.delete();
                                    InquiryDeletedEventModel mCallEvent = new InquiryDeletedEventModel();
                                    TinyBus bus = TinyBus.from(mContext.getApplicationContext());
                                    bus.post(mCallEvent);
                                    Toast.makeText(mContext, "Inquiry doesn't exist on server", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                }
                InquiryDeletedEventModel mCallEvent = new InquiryDeletedEventModel();
                TinyBus bus = TinyBus.from(mContext.getApplicationContext());
                bus.post(mCallEvent);
            }
        });
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_TIMEOUT_MS,
                MY_MAX_RETRIES,
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

    private void deleteInquiriesFromServerSync(final LSInquiry inquiry) {
        currentState = PENDING;
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
                Log.d(TAG, "onErrorResponse: CouldNotSyncDeleteInquiry");
                try {
                    if (error != null) {
                        if (error.networkResponse != null) {
                            Log.d(TAG, "onErrorResponse: error.networkResponse: " + error.networkResponse);
                            Log.d(TAG, "onErrorResponse: error.networkResponse.statusCode: " + error.networkResponse.statusCode);
                            if (error.networkResponse.statusCode == 412) {
                                JSONObject jObj = new JSONObject(new String(error.networkResponse.data));
                                int responseCode = jObj.getInt("responseCode");
                                String responseObject = jObj.getString("response");
                                if (responseCode == 236) {
                                    Log.d(TAG, "onErrorResponse: " + responseObject);
                                    inquiry.delete();
                                    InquiryDeletedEventModel mCallEvent = new InquiryDeletedEventModel();
                                    TinyBus bus = TinyBus.from(mContext.getApplicationContext());
                                    bus.post(mCallEvent);
                                    Toast.makeText(mContext, "Inquiry doesn't exist on server", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_TIMEOUT_MS,
                MY_MAX_RETRIES,
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
                if (oneNote.getContactOfNote() != null) {
                    addNoteToServerSync(oneNote);
                } else {
                    Log.d(TAG, "addNotesToServer: Contact of note is NULL, Deleting.. " + oneNote.getNoteText());
                    oneNote.delete();
                }
            }
        }
    }

    private void addNoteToServerSync(final LSNote note) {
        currentState = PENDING;
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
                Log.d(TAG, "onErrorResponse: CouldNotSyncAddNote");
                try {
                    if (error.networkResponse != null) {
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
                    } else {
                        Log.d(TAG, "onErrorResponse: no response may be poor internet");
                    }
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
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_TIMEOUT_MS,
                MY_MAX_RETRIES,
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

    private void updateNoteToServerSync(final LSNote note) {
        currentState = PENDING;
        Log.d(TAG, "LeadLocalIdOfNote: " + note.getContactOfNote().getId());
        Log.d(TAG, "LeadServerIdOfNote: " + note.getContactOfNote().getServerId());
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
                Log.d(TAG, "onErrorResponse: CouldNotSyncUpdateNote");
            }
        }) {
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_TIMEOUT_MS,
                MY_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    private void deleteNotesFromServer() {

        List<LSNote> notesList = null;
        if (LSNote.count(LSNote.class) > 0) {
            notesList = LSNote.find(LSNote.class, "sync_status = ?", SyncStatus.SYNC_STATUS_NOTE_DELETE_NOT_SYNCED);
            Log.d(TAG, "deleteNotesFromServer: count : " + notesList.size());
            for (LSNote oneNote : notesList) {
                Log.d(TAG, "Found Notes");
                deleteNoteFromServerSync(oneNote);
            }
        }
    }


    private void deleteNoteFromServerSync(final LSNote note) {
        Log.d(TAG, "deleteNoteFromServerSync: ServerIdOfNote: " + note.getServerId());
        currentState = PENDING;
        final String BASE_URL = MyURLs.DELETE_NOTE;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath("" + note.getContactOfNote().getServerId())
                .appendPath("notes")
                .appendPath("" + note.getServerId())
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        StringRequest sr = new StringRequest(Request.Method.DELETE, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() delete note called with: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    // ignoring responseCode etc because server is sending wrong Json format
//                    int responseCode = jObj.getInt("responseCode");
//                    Log.d(TAG, "onResponse: DeleteInquiry: "+jObj.toString());
//                    if (responseCode == 200) {
//                        JSONObject responseObject = jObj.getJSONObject("response");
                    note.delete();
                    NoteAddedEventModel mNoteAdded = new NoteAddedEventModel();
                    TinyBus bus = TinyBus.from(mContext.getApplicationContext());
                    bus.post(mNoteAdded);
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: CouldNotSyncDeleteNote");
                try {
                    if (error.networkResponse != null) {
                        Log.d(TAG, "onErrorResponse: error.networkResponse.statusCode: " + error.networkResponse.statusCode);
                        if (error.networkResponse.statusCode == 412) {
                            JSONObject jObj = new JSONObject(new String(error.networkResponse.data));
                            int responseCode = jObj.getInt("responseCode");
                            String responseObject = jObj.getString("response");
                            if (responseCode == 236) {
                                Log.d(TAG, "onErrorResponse: " + responseObject);
                                note.delete();
                                NoteAddedEventModel mNoteAdded = new NoteAddedEventModel();
                                TinyBus bus = TinyBus.from(mContext);
                                bus.post(mNoteAdded);
                                Log.d(TAG, "onErrorResponse: Note doesn't exist on server");
//                                Toast.makeText(mContext, "Note doesn't exist on server", Toast.LENGTH_SHORT).show();
                            }
                        } else if (error.networkResponse.statusCode == 404) {
//                        note.delete();
//                        Log.d(TAG, "onErrorResponse: Note doesn't exist on server deleted and from mobile.");
//                        Toast.makeText(mContext, "Note doesn't exist on server deleted and from mobile.", Toast.LENGTH_SHORT).show();
                        } else if (error.networkResponse.statusCode == 401) {
                            // Doesn't Exist on server.
                            note.delete();
                        }
                    } else {
                        Log.d(TAG, "onErrorResponse: no response may be poor internet");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_TIMEOUT_MS,
                MY_MAX_RETRIES,
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

    private void addFollowupToServerSync(final TempFollowUp followup) {
        currentState = PENDING;
        Log.d(TAG, "LeadLocalIdOfFollowup: " + followup.getContact().getId());
        Log.d(TAG, "LeadServerIdOfFollowup: " + followup.getContact().getServerId());
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
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_TIMEOUT_MS,
                MY_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }
}