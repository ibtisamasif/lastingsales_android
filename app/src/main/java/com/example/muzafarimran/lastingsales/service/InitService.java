package com.example.muzafarimran.lastingsales.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.app.MyURLs;
import com.example.muzafarimran.lastingsales.app.SyncStatus;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSDeal;
import com.example.muzafarimran.lastingsales.providers.models.LSDynamicColumns;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.providers.models.LSOrganization;
import com.example.muzafarimran.lastingsales.providers.models.LSProperty;
import com.example.muzafarimran.lastingsales.providers.models.LSStage;
import com.example.muzafarimran.lastingsales.providers.models.LSWorkflow;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;
import com.example.muzafarimran.lastingsales.utilscallprocessing.InquiryManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class InitService extends IntentService {
    public static final String TAG = "InitService";
    public static final String RESULT = "result";
    public static final String NOTIFICATION = "com.lastingsales.agent";
    private static RequestQueue queue;
    AtomicInteger requestsCounter;
    private int result = Activity.RESULT_CANCELED;
    private SessionManager sessionManager;
    private Context mContext;

    public InitService() {
        super("InitService");
    }

    // called asynchronously by Android
    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        mContext = getApplicationContext();
        sessionManager = new SessionManager(mContext);
        queue = Volley.newRequestQueue(mContext);
        requestsCounter = new AtomicInteger(0);

        if (sessionManager.isUserSignedIn()) {
            fetchAgentDataFunc();
            fetchDynamicColumns();
            fetchWorkflow();
        }

        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                Log.d(TAG, "onRequestFinished: " + requestsCounter.get());
                requestsCounter.decrementAndGet();
                if (requestsCounter.get() == 0) {
                    Log.d(TAG, "onRequestFinished: ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                    Log.d(TAG, "onRequestFinished: RESULT: " + result);
                    publishResults(result);
                }
            }
        });
    }

    private void fetchAgentDataFunc() {
        Log.d(TAG, "fetchAgentDataFunc: Fetching Data...");
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        final String BASE_URL = MyURLs.GET_SYNC;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter("populate", "true")
                .appendQueryParameter("per_page", "50000")
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        Log.d(TAG, "fetchAgentLeadsFunc: MYURL: " + myUrl);
        StringRequest sr = new StringRequest(Request.Method.GET, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() getContact: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONObject responseObject = jObj.getJSONObject("response");

                    JSONArray jsonArrayLeads = responseObject.getJSONArray("leads");
                    Log.d(TAG, "onResponse: data jsonArray length : " + jsonArrayLeads.length());
                    for (int i = jsonArrayLeads.length() - 1; i >= 0; i--) {
                        JSONObject jsonObjectOneLead = jsonArrayLeads.getJSONObject(i);
                        String contactId = jsonObjectOneLead.getString("id");
                        String contactName = jsonObjectOneLead.getString("name");
                        String contactNumber = jsonObjectOneLead.getString("phone");
                        String contactStatus = jsonObjectOneLead.getString("status");
                        String lead_type = jsonObjectOneLead.getString("lead_type");
                        String email = jsonObjectOneLead.getString("email");
                        String address = jsonObjectOneLead.getString("address");
                        String dynamic_values = jsonObjectOneLead.getString("dynamic_values");
                        int created_by = 0;
                        if (jsonObjectOneLead.has("created_by")) {
                            created_by = jsonObjectOneLead.getInt("created_by");
                        }
                        String updated_at = "";
                        if (jsonObjectOneLead.has("updated_at")) {
                            updated_at = jsonObjectOneLead.getString("updated_at");
                        }
                        int user_id = jsonObjectOneLead.getInt("user_id");
                        String src = jsonObjectOneLead.getString("src");

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
//                            fetchAgentNotesFunc(tempContact);

                            JSONArray jsonArrayLeadProperties = jsonObjectOneLead.getJSONArray("properties");
                            Log.d(TAG, "onResponse: data jsonArrayLeadProperties length : " + jsonArrayLeadProperties.length());
                            for (int j = jsonArrayLeadProperties.length() - 1; j >= 0; j--) {
                                JSONObject jsonObjectOneProperty = jsonArrayLeadProperties.getJSONObject(j);
                                String property_id = jsonObjectOneProperty.getString("id");
                                String property_user_id = jsonObjectOneProperty.getString("user_id");
                                String property_company_id = jsonObjectOneProperty.getString("company_id");
                                String property_column_id = jsonObjectOneProperty.getString("column_id");
                                String property_storable_id = jsonObjectOneProperty.getString("storable_id");
                                String property_storable_type = jsonObjectOneProperty.getString("storable_type");
                                String property_value = jsonObjectOneProperty.getString("value");
                                String property_created_by = jsonObjectOneProperty.getString("created_by");
                                String property_updated_by = jsonObjectOneProperty.getString("updated_by");

                                LSProperty tempProperty = new LSProperty();
                                tempProperty.setServerId(property_id);
                                tempProperty.setUserId(property_user_id);
                                tempProperty.setCompanyId(property_company_id);
                                tempProperty.setColumnId(property_column_id);
                                tempProperty.setStorableId(property_storable_id);
                                tempProperty.setStorableType(property_storable_type);
                                tempProperty.setValue(property_value);
                                tempProperty.setContactOfProperty(LSContact.getContactFromServerId(property_storable_id));
                                tempProperty.setSyncStatus(SyncStatus.SYNC_STATUS_PROPERTY_ADD_OR_UPDATE_SYNCED);
                                tempProperty.setUpdatedAt(PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(Calendar.getInstance().getTimeInMillis()));
                                tempProperty.save();
                            }

                            JSONArray jsonArrayLeadNotes = jsonObjectOneLead.getJSONArray("notes");
                            Log.d(TAG, "onResponse: data jsonArrayLeadNotes length : " + jsonArrayLeadNotes.length());
                            for (int j = jsonArrayLeadNotes.length() - 1; j >= 0; j--) {
                                JSONObject jsonObjectOneNote = jsonArrayLeadNotes.getJSONObject(j);
                                String note_id = jsonObjectOneNote.getString("id");
                                String note_user_id = jsonObjectOneNote.getString("user_id");
                                String note_company_id = jsonObjectOneNote.getString("company_id");
                                String note_notable_id = jsonObjectOneNote.getString("notable_id");
                                String note_notable_type = jsonObjectOneNote.getString("notable_type");
                                String note_description = jsonObjectOneNote.getString("description");
                                String note_created_by = jsonObjectOneNote.getString("created_by");
                                String note_updated_by = jsonObjectOneNote.getString("updated_by");

                                LSNote tempNote = new LSNote();
                                tempNote.setServerId(note_id);
                                tempNote.setContactOfNote(LSContact.getContactFromServerId(note_notable_id));
                                tempNote.setNotableType(note_notable_type);
                                tempNote.setNoteText(note_description);
                                tempNote.setSyncStatus(SyncStatus.SYNC_STATUS_NOTE_ADDED_SYNCED);
                                tempNote.setCreatedAt(PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(Calendar.getInstance().getTimeInMillis()));
                                tempNote.save();
                            }
                        }

                        JSONArray jsonArrayOrganizations = responseObject.getJSONArray("organizations");
                        Log.d(TAG, "onResponse: data jsonArrayOrganizations length : " + jsonArrayOrganizations.length());
                        for (int k = 0; k < jsonArrayOrganizations.length(); k++) {
                            JSONObject jsonObjectOneOrganization = jsonArrayOrganizations.getJSONObject(k);
                            String organization_id = jsonObjectOneOrganization.getString("id");
                            String organization_name = jsonObjectOneOrganization.getString("name");
                            int organization_created_by = 0;
                            if (jsonObjectOneOrganization.has("created_by")) {
                                organization_created_by = jsonObjectOneOrganization.getInt("created_by");
                            }

                            int organization_user_id = jsonObjectOneOrganization.getInt("user_id");
                            String organization_Status = jsonObjectOneOrganization.getString("status");
                            String organization_dynamic_values = jsonObjectOneOrganization.getString("dynamic_values");
                            int organization_company_id = jsonObjectOneOrganization.getInt("company_id");
                            String organization_src = jsonObjectOneOrganization.getString("src");
                            String organization_src_id = jsonObjectOneOrganization.getString("src_id");
                            String organization_version = jsonObjectOneOrganization.getString("version");

                            String organization_email = jsonObjectOneOrganization.getString("email");
                            String organization_phone = jsonObjectOneOrganization.getString("phone");

                            Log.d(TAG, "onResponse: ID: " + organization_id);
                            Log.d(TAG, "onResponse: Name: " + organization_name);
                            Log.d(TAG, "onResponse: Status: " + organization_Status);
                            Log.d(TAG, "onResponse: dynamic_values: " + organization_dynamic_values);
                            Log.d(TAG, "onResponse: created_by: " + organization_created_by);
//                                Log.d(TAG, "onResponse: created_at: " + organization_created_at);
//                                Log.d(TAG, "onResponse: updated_at: " + organization_updated_at);
                            Log.d(TAG, "onResponse: user_id: " + organization_user_id);
                            Log.d(TAG, "onResponse: company_id: " + organization_company_id);
                            Log.d(TAG, "onResponse: src_id: " + organization_src_id);
                            Log.d(TAG, "onResponse: src: " + organization_src);
                            Log.d(TAG, "onResponse: version: " + organization_version);
                            Log.d(TAG, "onResponse: email: " + organization_email);
                            Log.d(TAG, "onResponse: version: " + organization_phone);

                            if (LSOrganization.getOrganizationFromServerId(organization_id) == null) {
                                LSOrganization tempOrganization = new LSOrganization();
                                tempOrganization.setServerId(organization_id);
                                tempOrganization.setName(organization_name);
                                tempOrganization.setPhone(organization_phone);
                                tempOrganization.setEmail(organization_email);
                                tempOrganization.setStatus(organization_Status);
                                tempOrganization.setDynamicValues(organization_dynamic_values);
                                tempOrganization.setUpdatedAt(Calendar.getInstance().getTime());
                                tempOrganization.setUserId(Integer.toString(user_id));
                                tempOrganization.setCompanyId(Integer.toString(organization_company_id));
                                tempOrganization.setSrcId(organization_src_id);
                                tempOrganization.setSrc(organization_src);
                                tempOrganization.setVersion(organization_version);
                                tempOrganization.setSyncStatus(SyncStatus.SYNC_STATUS_ORGANIZATION_ADD_SYNCED);
                                tempOrganization.save();

                                JSONArray jsonArrayOrganizationProperties = jsonObjectOneOrganization.getJSONArray("properties");
                                Log.d(TAG, "onResponse: data jsonArrayOrganizationProperties length : " + jsonArrayOrganizationProperties.length());
                                for (int j = jsonArrayOrganizationProperties.length() - 1; j >= 0; j--) {
                                    JSONObject jsonObjectOneProperty = jsonArrayOrganizationProperties.getJSONObject(j);
                                    String property_id = jsonObjectOneProperty.getString("id");
                                    String property_user_id = jsonObjectOneProperty.getString("user_id");
                                    String property_company_id = jsonObjectOneProperty.getString("company_id");
                                    String property_column_id = jsonObjectOneProperty.getString("column_id");
                                    String property_storable_id = jsonObjectOneProperty.getString("storable_id");
                                    String property_storable_type = jsonObjectOneProperty.getString("storable_type");
                                    String property_value = jsonObjectOneProperty.getString("value");
                                    String property_created_by = jsonObjectOneProperty.getString("created_by");
                                    String property_updated_by = jsonObjectOneProperty.getString("updated_by");

                                    LSProperty tempProperty = new LSProperty();
                                    tempProperty.setServerId(property_id);
                                    tempProperty.setUserId(property_user_id);
                                    tempProperty.setCompanyId(property_company_id);
                                    tempProperty.setColumnId(property_column_id);
                                    tempProperty.setStorableId(property_storable_id);
                                    tempProperty.setStorableType(property_storable_type);
                                    tempProperty.setValue(property_value);
                                    tempProperty.setOrganizationOfProperty(LSOrganization.getOrganizationFromServerId(property_storable_id));
                                    tempProperty.setSyncStatus(SyncStatus.SYNC_STATUS_PROPERTY_ADD_OR_UPDATE_SYNCED);
                                    tempProperty.setUpdatedAt(PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(Calendar.getInstance().getTimeInMillis()));
                                    tempProperty.save();
                                }

                                JSONArray jsonArrayOrganizationNotes = jsonObjectOneOrganization.getJSONArray("notes");
                                Log.d(TAG, "onResponse: data jsonArrayOrganizationNotes length : " + jsonArrayOrganizationNotes.length());
                                for (int j = jsonArrayOrganizationNotes.length() - 1; j >= 0; j--) {
                                    JSONObject jsonObjectOneNote = jsonArrayOrganizationNotes.getJSONObject(j);
                                    String note_id = jsonObjectOneNote.getString("id");
                                    String note_user_id = jsonObjectOneNote.getString("user_id");
                                    String note_company_id = jsonObjectOneNote.getString("company_id");
                                    String note_notable_id = jsonObjectOneNote.getString("notable_id");
                                    String note_notable_type = jsonObjectOneNote.getString("notable_type");
                                    String note_description = jsonObjectOneNote.getString("description");
                                    String note_created_by = jsonObjectOneNote.getString("created_by");
                                    String note_updated_by = jsonObjectOneNote.getString("updated_by");

                                    LSNote tempNote = new LSNote();
                                    tempNote.setServerId(note_id);
                                    tempNote.setOrganizationOfNote(LSOrganization.getOrganizationFromServerId(note_notable_id));
                                    tempNote.setNotableType(note_notable_type);
                                    tempNote.setNoteText(note_description);
                                    tempNote.setSyncStatus(SyncStatus.SYNC_STATUS_NOTE_ADDED_SYNCED);
                                    tempNote.setCreatedAt(PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(Calendar.getInstance().getTimeInMillis()));
                                    tempNote.save();
                                }
                            }
                        }

                        JSONArray jsonArrayDeals = responseObject.getJSONArray("deals");
                        Log.d(TAG, "onResponse: data jsonArrayNotes length : " + jsonArrayDeals.length());
                        for (int k = 0; k < jsonArrayDeals.length(); k++) {
                            JSONObject jsonObjectOneDeal = jsonArrayDeals.getJSONObject(k);
                            String deal_id = jsonObjectOneDeal.getString("id");
                            String deal_name = jsonObjectOneDeal.getString("name");
                            int deal_created_by = 0;
                            if (jsonObjectOneDeal.has("created_by")) {
                                deal_created_by = jsonObjectOneDeal.getInt("created_by");
                            }
                            String deal_created_at = jsonObjectOneDeal.getString("created_at");
                            String deal_updated_at = jsonObjectOneDeal.getString("updated_at");
                            int deal_user_id = jsonObjectOneDeal.getInt("user_id");
                            int deal_lead_id = jsonObjectOneDeal.getInt("lead_id");
                            int deal_organization_id = jsonObjectOneDeal.getInt("organization_id");
                            int deal_workflow_id = jsonObjectOneDeal.getInt("workflow_id");
                            int deal_workflow_stage_id = jsonObjectOneDeal.getInt("workflow_stage_id");
                            String deal_Status = jsonObjectOneDeal.getString("status");
                            String deal_dynamic_values = jsonObjectOneDeal.getString("dynamic_values");
                            int deal_company_id = jsonObjectOneDeal.getInt("company_id");
                            String deal_src = jsonObjectOneDeal.getString("src");
                            String deal_src_id = jsonObjectOneDeal.getString("src_id");
                            String deal_is_private = jsonObjectOneDeal.getString("is_private");
                            String deal_version = jsonObjectOneDeal.getString("version");
                            String deal_value = jsonObjectOneDeal.getString("value");
                            String deal_currency = jsonObjectOneDeal.getString("currency");
                            String deal_success_rate = jsonObjectOneDeal.getString("success_rate");
                            String deal_success_eta = jsonObjectOneDeal.getString("success_eta");

                            Log.d(TAG, "onResponse: ID: " + deal_id);
                            Log.d(TAG, "onResponse: Name: " + deal_name);
                            Log.d(TAG, "onResponse: Status: " + deal_Status);
                            Log.d(TAG, "onResponse: dynamic_values: " + deal_dynamic_values);
                            Log.d(TAG, "onResponse: created_by: " + deal_created_by);
                            Log.d(TAG, "onResponse: created_at: " + deal_created_at);
                            Log.d(TAG, "onResponse: updated_at: " + deal_updated_at);
                            Log.d(TAG, "onResponse: user_id: " + deal_user_id);
                            Log.d(TAG, "onResponse: company_id: " + deal_company_id);
                            Log.d(TAG, "onResponse: src_id: " + deal_src_id);
                            Log.d(TAG, "onResponse: lead_id: " + deal_lead_id);
                            Log.d(TAG, "onResponse: organization_id: " + deal_organization_id);
                            Log.d(TAG, "onResponse: workflow_id: " + deal_workflow_id);
                            Log.d(TAG, "onResponse: workflow_stage_id: " + deal_workflow_stage_id);
                            Log.d(TAG, "onResponse: src: " + deal_src);
                            Log.d(TAG, "onResponse: is_private: " + deal_is_private);
                            Log.d(TAG, "onResponse: version: " + deal_version);
                            Log.d(TAG, "onResponse: value: " + deal_value);
                            Log.d(TAG, "onResponse: currency: " + deal_currency);
                            Log.d(TAG, "onResponse: success_rate: " + deal_success_rate);
                            Log.d(TAG, "onResponse: success_eta: " + deal_success_eta);

                            if (LSDeal.getDealFromServerId(deal_id) == null) {
                                LSContact lsContact = LSContact.getContactFromServerId(Integer.toString(deal_lead_id));
                                LSOrganization lsOrganization = LSOrganization.getOrganizationFromServerId(Integer.toString(deal_organization_id));
                                if (lsContact != null || lsOrganization != null) {
                                    LSDeal tempDeal = new LSDeal();
                                    tempDeal.setServerId(deal_id);
                                    tempDeal.setName(deal_name);
                                    tempDeal.setStatus(deal_Status);
//                            tempDeal.setEmail(email);
                                    tempDeal.setDynamic(deal_dynamic_values);
                                    if (created_by != 0) {
                                        tempDeal.setCreatedBy(Integer.toString(deal_created_by));
                                    }
                                    tempDeal.setCreatedAt(deal_created_at);
                                    tempDeal.setUpdatedAt(Calendar.getInstance().getTime());
                                    tempDeal.setUserId(Integer.toString(user_id));
                                    tempDeal.setCompanyId(Integer.toString(deal_company_id));
                                    tempDeal.setWorkflowId(Integer.toString(deal_workflow_id));
                                    tempDeal.setWorkflowStageId(Integer.toString(deal_workflow_stage_id));
                                    tempDeal.setIsPrivate(deal_is_private);
                                    if (lsContact != null) {
                                        tempDeal.setContact(lsContact);
                                    }
                                    if (lsOrganization != null) {
                                        tempDeal.setOrganization(lsOrganization);
                                    }
                                    tempDeal.setValue(deal_value);
                                    tempDeal.setCurrency(deal_currency);
                                    tempDeal.setSuccessRate(deal_success_rate);
                                    tempDeal.setSuccessEta(deal_success_eta);
                                    tempDeal.setSyncStatus(SyncStatus.SYNC_STATUS_DEAL_ADD_SYNCED);
                                    tempDeal.save();

                                    JSONArray jsonArrayDealProperties = jsonObjectOneDeal.getJSONArray("properties");
                                    Log.d(TAG, "onResponse: data jsonArrayDealProperties length : " + jsonArrayDealProperties.length());
                                    for (int j = jsonArrayDealProperties.length() - 1; j >= 0; j--) {
                                        JSONObject jsonObjectOneProperty = jsonArrayDealProperties.getJSONObject(j);
                                        String property_id = jsonObjectOneProperty.getString("id");
                                        String property_user_id = jsonObjectOneProperty.getString("user_id");
                                        String property_company_id = jsonObjectOneProperty.getString("company_id");
                                        String property_column_id = jsonObjectOneProperty.getString("column_id");
                                        String property_storable_id = jsonObjectOneProperty.getString("storable_id");
                                        String property_storable_type = jsonObjectOneProperty.getString("storable_type");
                                        String property_value = jsonObjectOneProperty.getString("value");
                                        String property_created_by = jsonObjectOneProperty.getString("created_by");
                                        String property_updated_by = jsonObjectOneProperty.getString("updated_by");

                                        LSProperty tempProperty = new LSProperty();
                                        tempProperty.setServerId(property_id);
                                        tempProperty.setUserId(property_user_id);
                                        tempProperty.setCompanyId(property_company_id);
                                        tempProperty.setColumnId(property_column_id);
                                        tempProperty.setStorableId(property_storable_id);
                                        tempProperty.setStorableType(property_storable_type);
                                        tempProperty.setValue(property_value);
                                        tempProperty.setDealOfProperty(LSDeal.getDealFromServerId(property_storable_id));
                                        tempProperty.setSyncStatus(SyncStatus.SYNC_STATUS_PROPERTY_ADD_OR_UPDATE_SYNCED);
                                        tempProperty.setUpdatedAt(PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(Calendar.getInstance().getTimeInMillis()));
                                        tempProperty.save();
                                    }

                                    JSONArray jsonArrayDealNotes = jsonObjectOneDeal.getJSONArray("notes");
                                    Log.d(TAG, "onResponse: data jsonArrayDealNotes length : " + jsonArrayDealNotes.length());
                                    for (int j = jsonArrayDealNotes.length() - 1; j >= 0; j--) {
                                        JSONObject jsonObjectOneNote = jsonArrayDealNotes.getJSONObject(j);
                                        String note_id = jsonObjectOneNote.getString("id");
                                        String note_user_id = jsonObjectOneNote.getString("user_id");
                                        String note_company_id = jsonObjectOneNote.getString("company_id");
                                        String note_notable_id = jsonObjectOneNote.getString("notable_id");
                                        String note_notable_type = jsonObjectOneNote.getString("notable_type");
                                        String note_description = jsonObjectOneNote.getString("description");
                                        String note_created_by = jsonObjectOneNote.getString("created_by");
                                        String note_updated_by = jsonObjectOneNote.getString("updated_by");

                                        LSNote tempNote = new LSNote();
                                        tempNote.setServerId(note_id);
                                        tempNote.setDealOfNote(LSDeal.getDealFromServerId(note_notable_id));
                                        tempNote.setNotableType(note_notable_type);
                                        tempNote.setNoteText(note_description);
                                        tempNote.setSyncStatus(SyncStatus.SYNC_STATUS_NOTE_ADDED_SYNCED);
                                        tempNote.setCreatedAt(PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(Calendar.getInstance().getTimeInMillis()));
                                        tempNote.save();
                                    }
                                }
                            }
                        }
                    }
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
            }
        });
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
                        String related_to = jsonobject.getString("related_to");
                        String form_visibility = jsonobject.getString("form_visibility");
                        String name = jsonobject.getString("name");
                        String display_name = jsonobject.getString("display_name");
                        String default_value_options = jsonobject.getString("default_value_options");
                        String range = jsonobject.getString("range");
                        String created_by = jsonobject.getString("created_by");
                        String updated_by = jsonobject.getString("updated_by");
                        String created_at = jsonobject.getString("created_at");
                        String updated_at = jsonobject.getString("updated_at");
                        String company_id = jsonobject.getString("company_id");

                        Log.d(TAG, "onResponse: ID: " + id);
                        Log.d(TAG, "onResponse: column_type: " + column_type);
                        Log.d(TAG, "onResponse: related_to: " + related_to);
                        Log.d(TAG, "onResponse: form_visibility: " + form_visibility);
                        Log.d(TAG, "onResponse: name: " + name);
                        Log.d(TAG, "onResponse: display_name: " + display_name);
                        Log.d(TAG, "onResponse: default_value_options: " + default_value_options);
                        Log.d(TAG, "onResponse: range: " + range);
                        Log.d(TAG, "onResponse: created_by: " + created_by);
                        Log.d(TAG, "onResponse: updated_by: " + updated_by);
                        Log.d(TAG, "onResponse: created_at: " + created_at);
                        Log.d(TAG, "onResponse: updated_at: " + updated_at);
                        Log.d(TAG, "onResponse: company_id: " + company_id);

                        LSDynamicColumns checkColumn = LSDynamicColumns.getColumnFromServerId(id);
                        if (checkColumn == null) {
                            LSDynamicColumns newColumn = new LSDynamicColumns();
                            newColumn.setServerId(id);
                            newColumn.setColumnType(column_type);
                            newColumn.setRelatedTo(related_to);
                            newColumn.setFormVisibility(form_visibility);
                            newColumn.setName(name);
                            newColumn.setDisplayName(display_name);
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
                    fetchInquiries();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onResponse: JSONException DynamicColumns");
                }
            }
        }, error -> {
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
        });
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
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onResponse: JSONException fetchWorkflow");
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onResponse: ParseException fetchWorkflow");
                }
            }
        }, error -> {
            Log.e(TAG, "onErrorResponse: CouldNotGETWorkflow");
            if (error != null) {
                if (error.networkResponse != null) {
                    if (error.networkResponse.statusCode == 404) {
                    } else {
                    }
                } else {
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
                Log.d(TAG, "onResponse() getInquiries: response = " + response);
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

                                InquiryManager.createOrUpdate(mContext, inquiry_id, status_of_inquiry, beginTimeFromServer, contactNumber);
                            }
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
        }, error -> Log.e(TAG, "onErrorResponse: CouldNotGETInquiries"));
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