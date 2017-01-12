//package com.example.muzafarimran.lastingsales.sync;
//
//import android.content.ContentResolver;
//import android.content.Context;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Handler;
//import android.util.Log;
//
//import com.example.muzafarimran.lastingsales.SessionManager;
//import com.example.muzafarimran.lastingsales.providers.models.MyURLs;
//import com.example.muzafarimran.lastingsales.utils.NetworkAccess;
//import com.vivid.app.controllers.LoginActivity;
//import com.vivid.app.models.Calls;
//import com.vivid.app.models.ContactType;
//import com.vivid.app.models.Contacts;
//import com.vivid.app.models.Inquiry;
//import com.vivid.app.models.LastSyncDetails;
//import com.vivid.app.models.Lead;
//import com.vivid.app.models.LeadStatus;
//import com.vivid.app.models.LoginInfo;
//import com.vivid.app.models.Message;
//import com.vivid.app.models.MyAlarms;
//import com.vivid.app.models.MyLogTags;
//import com.vivid.app.models.Notes;
//import com.vivid.app.models.SyncAttributes;
//import com.vivid.app.utils.Http_Request;
//import com.vivid.app.utils.MyDateTimeStamp;
//import com.vivid.app.utils.MySyncFetching;
//import com.vivid.app.utils.PersistanceLRT;
//import com.vivid.app.utils.PrefManager;
//
//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class DataSenderNew extends AsyncTask<Object, Void, Void> {
//    public static final String TAG = "DataSenderNew";
//    String INCOMING_ZERO_DURATION = "0";
//
//    String leadResponse = "";
//    String inquiryResponse = "";
//    String outgoingResponse = "";
//    String incomingResponse = "";
//    String leadUpdateResponse = "";
//    String contactUpdateResponse = "";
//    String leadServerToAppResponse = "";
//    String privateDurationResponse = "";
//    String incomingCallsServerToAppResponse = "";
//    String outgoingCallsServerToAppResponse = "";
//    String persistanceLrtUpdateResponse = "";
//    String privateContactResponse = "";
//    String privateContactServerToAppResponse = "";
//
//    Context mContext;
//    SessionManager sessionManager;
//
//    PrefManager pref;
//
//    public DataSenderNew(Context context1) {
//        mContext = context1;
//    }
//
//    //this method is called before sending data
//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//        pref = new PrefManager(mContext);
//    }
//
//    @Override
//    protected Void doInBackground(Object... params) {
//
//        try {
//            if (NetworkAccess.isNetworkAvailable(mContext)) {
//                if (!sessionManager.isUserSignedIn()) {
//
//                    createLeadMethod();
//                    createBusinessContactMethod();
//                    privateContact();
//                    leadUpdateMethod();
//                    leadsFetchingHTTP();
//                    leadsDeleteFetchingHTTP();
//                    contactsFetchingHTTP();
//                    contactsDeleteFetchingHTTP();
//                    contactUpdateMethod();
//
//                }
//            } else {
//                Log.d(TAG, "SyncingNoInternetConnection");
//            }
//        } catch (Exception e) {
//            Log.d(TAG, "SyncingException: " + e.getMessage());
//        }
//
//        return null;
//    }
//
//
//    private void privateContact() { // for private contacts only
//        List<Contacts> contactsList = null;
//        if (Contacts.count(Contacts.class) > 0) {
//            contactsList = Contacts.find(Contacts.class,
//                    "sync_status = ? and type = ?",
//                    SyncStatus.SYNC_STATUS_NOT_SYNCED, ContactType.Private);
//            if (contactsList.size() != 0) {
//                for (Contacts contacts : contactsList) {
//                    privateContactHTTP(contacts.getName(), contacts.getNumber(), contacts.getDateTimeStamp(), contacts);
//                }
//            } else {
//                Log.d(TAG, "8) No Private Contact to sync");
//            }
//        }
//    }
//
//    private void createBusinessContactMethod() { // for business contacts only
//        List<Contacts> contactsList = null;
//        if (Contacts.count(Contacts.class) > 0) {
//            contactsList = Contacts.find(Contacts.class,
//                    "( sync_status = ? or sync_status = ? ) and type = ?",
//                    SyncStatus.SYNC_STATUS_NOT_SYNCED, SyncStatus.SYNC_STATUS_UPDATE_REQUIRE,
//                    ContactType.Business);
//            if (contactsList.size() != 0) {
//                for (Contacts contacts : contactsList) {
//                    List<Lead> leadList = Lead.find(Lead.class, "contact_id = ?", "" + contacts.getId());
//                    if (leadList.size() == 0) {
//                        businessContactHTTP(contacts.getName(), contacts.getNumber(), contacts.getDateTimeStamp(), contacts);
//                    } else {
//                        //do nothing
//                    }
//                }
//            } else {
//                Log.d(TAG, "7) No Business Contact to sync");
//            }
//        }
//    }
//
//    private void privateContactHTTP(final String name, final String client_num, final String dateTimeStamp, final Contacts contacts) {
//
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("agent_num", "" + pref.getKEY_MOBILE()));
//        params.add(new BasicNameValuePair("hash", "" + pref.getHashKey()));
//        params.add(new BasicNameValuePair("mobile", "" + client_num));
//        params.add(new BasicNameValuePair("name", "" + name));
//        params.add(new BasicNameValuePair("email", ""));
//        params.add(new BasicNameValuePair("company", ""));
//        params.add(new BasicNameValuePair("address", ""));
//        params.add(new BasicNameValuePair("notes", ""));
//
//        Log.d(TAG, "8)PrivateContact Sending...");
//        String resultServer = Http_Request.getHttpPost(MyURLs.PRIVATE_CONTACT_URL, params);
//        Log.d(TAG, "8)PrivateContact: " + resultServer);
//        JSONObject jobj;
//        try {
//            // Create Apache HttpClient
//            jobj = new JSONObject(resultServer);
//            // JSONObject jobj = new JSONObject(response);
//            int error = jobj.getInt("error");
//            privateContactResponse = "" + error;
//            if (error == 0) {
//                contacts.setSyncStatus(SyncStatus.SYNC_STATUS_SYNCED);
//                contacts.save();
////                Toast.makeText(mContext, "private Saved success.", Toast.LENGTH_SHORT).show();
//            } else if (error == 1) {
//                contacts.setSyncStatus(SyncStatus.SYNC_STATUS_SYNCED);
//                contacts.save();
////                Toast.makeText(mContext, "private Contact Already Exist with this Agent.", Toast.LENGTH_LONG).show();
//            } else if (error == 2) {
////                Toast.makeText(mContext, "private No Agent Found.", Toast.LENGTH_LONG).show();
//            }
//        } catch (Exception e) {
//            Log.d(TAG, "8)PrivateContactResponse Exception: " + e.getMessage());
//        }
//    }
//
//    private void businessContactHTTP(final String name, final String client_num, final String dateTimeStamp, final Contacts contacts) {
//
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("agent_num", "" + pref.getKEY_MOBILE()));
//        params.add(new BasicNameValuePair("hash", "" + pref.getHashKey()));
//        params.add(new BasicNameValuePair("mobile", "" + client_num));
//        params.add(new BasicNameValuePair("name", "" + name));
//        params.add(new BasicNameValuePair("email", ""));
//        params.add(new BasicNameValuePair("company", ""));
//        params.add(new BasicNameValuePair("address", ""));
//        params.add(new BasicNameValuePair("notes", ""));
//
//        Log.d(TAG, "7)BusinessContact sending...");
//        String resultServer = Http_Request.getHttpPost(MyURLs.BUSINESS_CONTACT_URL, params);
//        Log.d(TAG, "7)BusinessContact: " + resultServer);
//        JSONObject jobj;
//        try {
//            // Create Apache HttpClient
//            jobj = new JSONObject(resultServer);
//            // JSONObject jobj = new JSONObject(response);
//            int error = jobj.getInt("error");
//            if (error == 0) {
//                contacts.setSyncStatus(SyncStatus.SYNC_STATUS_SYNCED);
//                contacts.save();
////                Toast.makeText(mContext, "business Saved success.", Toast.LENGTH_SHORT).show();
//            } else if (error == 1) {
//                contacts.setName(jobj.getString("name"));
//                contacts.setSyncStatus(SyncStatus.SYNC_STATUS_SYNCED);
//                contacts.save();
////                Toast.makeText(mContext, "business Contact Already Exist with this Agent.", Toast.LENGTH_LONG).show();
//            } else if (error == 2) {
////                Toast.makeText(mContext, "business No Agent Found.", Toast.LENGTH_LONG).show();
//            }
//        } catch (Exception e) {
//            Log.d(TAG, "7)BusinessContactResponse Exception: " + e.getMessage());
//        }
//    }
//
//    private void leadUpdateMethod() {
//        List<Lead> leadUpdateListSugar = null;
//        if (Lead.count(Lead.class) > 0) {
////            leadUpdateListSugar = Lead.find(Lead.class,"sync_status = ?",SyncStatus.SYNC_STATUS_LEAD_UPDATE_REQUIRE);
//            leadUpdateListSugar = Lead.listAll(Lead.class);
//            String follow_up_datetime = MyDateTimeStamp.emptyTimeStamp;
//            List<MyAlarms> myAlarmsList = null;
//            if (leadUpdateListSugar.size() != 0) {
//                Log.d(TAG, "19)LeadUpdate Sending...");
//                for (Lead lead : leadUpdateListSugar) {
//
//                    Contacts contacts = Contacts.findById(Contacts.class, lead.getContactID());
//                    if (contacts != null) {
//                        String contactNumber = contacts.getNumber();
//                        String contactName = contacts.getName();
//
//                        myAlarmsList = MyAlarms.find(MyAlarms.class,
//                                "number = ? and contact_id = ?", contactNumber, "" + contacts.getId());
//                        if (myAlarmsList.size() != 0) {
//                            for (MyAlarms alarms : myAlarmsList) {
//                                follow_up_datetime = alarms.alarmTime;
//                            }
//                        } else {
//                            follow_up_datetime = MyDateTimeStamp.emptyTimeStamp;
//                        }
//                        String leadStatus = LeadStatus.getLeadStatusNumber(lead.getLeadStatus());
//                        List<Notes> notesList = Notes.find(Notes.class, "contact_id = ? ", "" + lead.getContactID());
//                        String note = "";
//                        for (Notes notes : notesList) {
//                            if (notes.getNote() != null) {
//                                note = notes.note;
//                            }
//                        }
//                        leadUpdateHTTP(mContext, "" + contactNumber, contactName, leadStatus,
//                                note, "" + lead.getId(), lead, follow_up_datetime, lead.getDateClosed());
//                    }
//                }
//                Log.d(TAG, "19)LeadUpdate Complete");
//            }
//        } else {
//            Log.d(TAG, "19)No Lead to Update.");
//        }
//    }
//
//    private void contactUpdateMethod() {
//        List<Contacts> contactUpdateListSugar = null;
//        if (Contacts.count(Contacts.class) > 0) {
//            contactUpdateListSugar = Contacts.find(Contacts.class,
//                    "type = ? and sync_status = ?", ContactType.Business,
//                    SyncStatus.SYNC_STATUS_UPDATE_REQUIRE);
////            contactUpdateListSugar = Contacts.find(Contacts.class,
////                    "type = ?",ContactType.Business);
//
//            if (contactUpdateListSugar.size() != 0) {
//                Log.d(TAG, "18)ContactUpdate Sending...");
//
//                for (Contacts contacts : contactUpdateListSugar) {
//                    List<Notes> notesList = Notes.find(Notes.class, "contact_id = ? ", "" + contacts.getId());
//                    String note = "";
//                    for (Notes notes : notesList) {
//                        if (notes.note != null) {
//                            note = notes.note;
//                        }
//                    }
//                    contactUpdateHTTP(mContext, contacts.getNumber(), contacts.getName(), contacts, note);
//                }
//                Log.d(TAG, "18)ContactUpdate complete");
//            }
//        } else {
//            Log.d(TAG, "18) No Contact to Update.");
//        }
//    }
//
//    private void createLeadMethod() {
//        List<Lead> leadList = null;
//        if (Lead.count(Lead.class) > 0) {
//            leadList = Lead.find(Lead.class, "sync_status = ?", SyncStatus.SYNC_STATUS_NOT_SYNCED);
//            if (leadList.size() != 0) {
//                for (Lead lead : leadList) {
//                    Contacts contacts = Contacts.findById(Contacts.class, lead.getContactID());
//                    if (contacts != null) {
//                        addLeadRequestHTTP(contacts.getAgentID(), pref.getClientId(), pref.getHashKey(),
//                                contacts.getName(), contacts.getNumber(), "", "", "", "",
//                                contacts.getDateTimeStamp(), "" + lead.getId(), lead);
//                    } else {
//                        Log.d(TAG, "5)LeadsContact is null");
//                    }
//                }
//            } else {
//                Log.d(TAG, "5) No Lead to sync");
//            }
//        }
//    }
//
//    private void addLeadRequestHTTP(final String agent_id, final String client_id, final String hash_key,
//                                    final String name, final String client_num, final String email,
//                                    final String company, final String address, final String notes,
//                                    final String dateTimeStamp, final String leadID, final Lead lead) {
//
//        String agent_num = pref.getKEY_MOBILE();
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("agent_num", "" + agent_num));
//        params.add(new BasicNameValuePair("hash", "" + hash_key));
//        params.add(new BasicNameValuePair("lead_num", "" + client_num));
//        params.add(new BasicNameValuePair("lead_name", "" + name));
//        params.add(new BasicNameValuePair("timestamp", "" + dateTimeStamp));
//
//        Log.d(TAG, "5)LeadCreate sending...");
//        String resultServer = Http_Request.getHttpPost(MyURLs.LEAD_URL, params);
//        Log.d(TAG, "5)LeadCreateResponse: " + resultServer);
//
//        JSONObject jobj;
//        try {
//            // Create Apache HttpClient
//            jobj = new JSONObject(resultServer);
//            // JSONObject jobj = new JSONObject(response);
//            int error = jobj.getInt("error");
//
//            leadResponse = "" + error;
//
//            if (error == 0) {
//
//                lead.setSyncStatus(SyncStatus.SYNC_STATUS_SYNCED);
//                lead.save();
//
////                DataSenderNew dataSenderNew = new DataSenderNew(mContext);
////                dataSenderNew.execute();
////                Toast.makeText(mContext, "lead Saved.", Toast.LENGTH_SHORT).show();
//            } else if (error == 1) {
////                Toast.makeText(mContext, "Contact Already Exist with this Agent.", Toast.LENGTH_LONG).show();
//            } else if (error == 2) {
////                Toast.makeText(mContext, "lead No Agent Found.", Toast.LENGTH_LONG).show();
//            }
//
//        } catch (Exception e) {
//            Log.d(TAG, "5)LeadCreateResponse Exception: " + e.getMessage());
//        }
//    }
//
//    private void leadUpdateHTTP(final Context context, final String mobile, final String name,
//                                final String leadStatus, final String notes,
//                                final String leadID, final Lead lead,
//                                final String follow_up_datetime, final String date_closed) {
//
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("agent_num", "" + pref.getKEY_MOBILE()));
////        params.add(new BasicNameValuePair("hash", "" + pref.getHashKey()));
////        params.add(new BasicNameValuePair("client_id", "" + pref.getClientId()));
//        params.add(new BasicNameValuePair("user_id", "" + pref.getAgentId()));
//        params.add(new BasicNameValuePair("lead_num", "" + mobile));
//        params.add(new BasicNameValuePair("lead_name", "" + name));
//        params.add(new BasicNameValuePair("status", "" + leadStatus));
//        params.add(new BasicNameValuePair("product", "" + notes));
//        params.add(new BasicNameValuePair("notes_created_date", "" + MyDateTimeStamp.getDateTimeStamp()));
//        params.add(new BasicNameValuePair("lead_persistence", "" + PersistanceLRT.getLeadPersistance(mobile, 21)));
//        params.add(new BasicNameValuePair("lead_persistence_7days", "" + PersistanceLRT.getLeadPersistance(mobile, 7)));
//        params.add(new BasicNameValuePair("lead_lrt", "" + PersistanceLRT.getLeadLRT(mobile)));
//        params.add(new BasicNameValuePair("follow_up", "" + follow_up_datetime));
//        params.add(new BasicNameValuePair("date_closed", date_closed));
//
////        Log.d(TAG, "19)LeadUpdate Sending...");
//        String resultServer = Http_Request.getHttpPost(MyURLs.LEAD_UPDATE_URL, params);
////        Log.d(TAG, "19)LeadUpdateResponse: "+resultServer);
//
////        Log.d(TAG, "leadUpdateResponse: "+resultServer);
//        JSONObject jobj;
//        try {
//            // Create Apache HttpClient
//            jobj = new JSONObject(resultServer);
//            // JSONObject jobj = new JSONObject(response);
//            int error = jobj.getInt("error");
//            leadUpdateResponse = "" + error;
//
//            if (error == 0) {
//
//                lead.setSyncStatus(SyncStatus.SYNC_STATUS_SYNCED);
//                lead.save();
//
//            } else if (error == 1) {
////                Toast.makeText(context, "lead update no agent found.", Toast.LENGTH_LONG).show();
//            } else if (error == 3) {
////                lead.delete();
//
//                List<Contacts> contactsList = Contacts.find(Contacts.class, "number = ?", mobile);
//                String contactID = null;
//                for (Contacts contacts : contactsList) {
//                    contactID = "" + contacts.getId();
////                    contacts.delete();
//                }
//
//                List<Lead> leadList = Lead.find(Lead.class,
//                        "contact_id = ? and sync_status != ?", contactID, SyncStatus.SYNC_STATUS_NOT_SYNCED);
//                for (Lead lead1 : leadList) {
//                    lead1.delete();
//                }
//
//                List<Calls> callList = Calls.find(Calls.class, "number = ? or contact_id = ?", mobile, contactID);
//                for (Calls call : callList) {
//                    call.delete();
//                }
////                List<Inquiry> inquiryList = Inquiry.find(Inquiry.class,"number = ?",mobile);
////                for (Inquiry inquiry : inquiryList){
////                    inquiry.delete();
////                }
////                List<MyAlarms> myAlarmsList = MyAlarms.find(MyAlarms.class,"number = ? or contact_id = ?",mobile,contactID);
////                for (MyAlarms myAlarms: myAlarmsList){
////                    myAlarms.delete();
////                }
//                List<Message> messageList = Message.find(Message.class, "number = ? or contact_id = ?", mobile, contactID);
//                for (Message message : messageList) {
//                    message.delete();
//                }
//
//            }
//
//        } catch (Exception e) {
//            Log.d(TAG, "19)LeadUpdateResponse Exception: " + e.getMessage());
////            Toast.makeText(context, "catch:"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void contactUpdateHTTP(final Context context, final String mobile, final String name,
//                                   Contacts contacts, String note) {
//
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("agent_num", "" + pref.getKEY_MOBILE()));
////        params.add(new BasicNameValuePair("hash", "" + pref.getHashKey()));
////        params.add(new BasicNameValuePair("client_id", "" + pref.getClientId()));
//        params.add(new BasicNameValuePair("user_id", "" + pref.getAgentId()));
//        params.add(new BasicNameValuePair("contact_num", "" + mobile));
//        params.add(new BasicNameValuePair("contact_name", "" + name));
//        params.add(new BasicNameValuePair("notes", "" + note));
//
////        Log.d(TAG, "18)ContactUpdate Sending...");
//        String resultServer = Http_Request.getHttpPost(MyURLs.CONTACT_UPDATE_URL, params);
////        Log.d(TAG, "18)ContactUpdateResponse: "+resultServer);
//
//        JSONObject jobj;
//        try {
//            // Create Apache HttpClient
//            jobj = new JSONObject(resultServer);
//            // JSONObject jobj = new JSONObject(response);
//            int error = jobj.getInt("error");
//            contactUpdateResponse = "" + error;
//
//            if (error == 0) {
//                contacts.setSyncStatus(SyncStatus.SYNC_STATUS_SYNCED);
//                contacts.save();
//            } else if (error == 1) {
////                Toast.makeText(context, "contact update no agent found.", Toast.LENGTH_LONG).show();
//            } else if (error == 2) {
//                if (Lead.find(Lead.class, "contact_id = ?", "" + contacts.getId()).size() == 0) {
//                    contacts.delete();
//                    Log.d(TAG, "contact " + name + "(" + mobile + ") Deleted.");
//                }
////                Toast.makeText(context, "contact update No contact of the given number exists", Toast.LENGTH_LONG).show();
//            } else if (error == 3) {
////                Toast.makeText(context, "contact update name is not null", Toast.LENGTH_LONG).show();
//            }
//
//        } catch (Exception e) {
//            Log.d(TAG, "18)ContactUpdateResponse Exception: " + e.getMessage());
////            Toast.makeText(context, "catch:"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void persistenceLrtUpdateHTTP(final Context context, final String persistance,
//                                          final String persistance7days,
//                                          final String avglrt/*, final LoginInfo loginInfo*/) {
//
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("agent_num", "" + pref.getKEY_MOBILE()));
////        params.add(new BasicNameValuePair("hash", "" + pref.getHashKey()));
////        params.add(new BasicNameValuePair("client_id", "" + pref.getClientId()));
//        params.add(new BasicNameValuePair("persistance", "" + persistance));
//        params.add(new BasicNameValuePair("persistance7days", "" + persistance7days));
//        params.add(new BasicNameValuePair("lrt", "" + avglrt));
//
//        Log.d(TAG, "6)PersistenceLRT sending...");
//        String resultServer = Http_Request.getHttpPost(MyURLs.PERSISTENCE_LRT_URL, params);
//        Log.d(TAG, "6)PersistenceLRTResponse: " + resultServer);
//        JSONObject jobj;
//        try {
//            // Create Apache HttpClient
//            jobj = new JSONObject(resultServer);
//            // JSONObject jobj = new JSONObject(response);
//            int error = jobj.getInt("error");
//            persistanceLrtUpdateResponse = "" + error;
//
//            if (error == 0) {
//
////                loginInfo.syncStatus = SyncStatus.SYNC_STATUS_SYNCED;
////                loginInfo.save();
//
//            } else if (error == 1) {
////                Toast.makeText(context, "lead update no agent found.", Toast.LENGTH_LONG).show();
//            }
//
//        } catch (Exception e) {
//            Log.d(TAG, "6)PersistenceLRTResponse Exception: " + e.getMessage());
////            Toast.makeText(context, "catch:"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void outgoingCallsFetchingHTTP() {
//        List<LastSyncDetails> syncDetailses = LastSyncDetails.find(
//                LastSyncDetails.class, "attribute = ?", SyncAttributes.outgoing_call);
//        String lastSync = syncDetailses.get(0).getLastUpdated();
//
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("agent_num", "" + pref.getKEY_MOBILE()));
//        params.add(new BasicNameValuePair("hash", "" + pref.getHashKey()));
//        params.add(new BasicNameValuePair("last_sync", "" + lastSync));
//
//        Log.d(TAG, "13)OutgoingCallsFetching Requesting...");
//        String resultServer = Http_Request.getHttpPost(MyURLs.OUTGOING_CALLS_FETCHING_URL, params);
//        Log.d(TAG, "13)OutgoingCallsFetching Response: " + resultServer);
//        JSONObject jobj;
//        try {
//            // Create Apache HttpClient
//            jobj = new JSONObject(resultServer);
//            // JSONObject jobj = new JSONObject(response);
//            int error = jobj.getInt("error");
//
//            JSONArray outgoingcalls = jobj.getJSONArray("outgoingcalls");
//            Log.d(MyLogTags.lastSync, "outgoingJsonListSize : " + outgoingcalls.length());
//
//            JSONObject leadObj;
//
//            String contact_num, outgoing_duration, outgoing_datetime, outgoing_type, audio_file_name;
//
//            outgoingCallsServerToAppResponse = "" + error;
//
//            if (error == 0) {
//                for (int i = 0; i < outgoingcalls.length(); i++) {
//                    leadObj = outgoingcalls.getJSONObject(i);
//                    contact_num = leadObj.getString("contact_num");
//                    outgoing_duration = leadObj.getString("duration");
//                    outgoing_datetime = leadObj.getString("datetime");
//                    outgoing_type = leadObj.getString("status");
//                    audio_file_name = leadObj.getString("audio_file_name");
//
//                    insertOutgoing(contact_num, outgoing_duration,
//                            outgoing_datetime, outgoing_type, audio_file_name);
//                    if ((i + 1) == outgoingcalls.length()) {
//                        MySyncFetching.setLastSyncDate(SyncAttributes.outgoing_call);
//                    }
//                }
//
//            } else if (error == 1) {
////                Toast.makeText(context, "lead sync no agent found.", Toast.LENGTH_LONG).show();
//            }
//
//        } catch (Exception e) {
//            Log.d(TAG, "13)OutgoingCallsFetchingResponse Exception: " + e.getMessage());
////            Toast.makeText(context, "lead sync catch:"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void insertOutgoing(String contact_num, String outgoing_duration,
//                                String outgoing_datetime, String outgoing_type, String audio_file_name) {
//        String datee = outgoing_datetime.substring(0, 8);
//        String timee = outgoing_datetime.substring(9);
//        List<Contacts> contactsList = Contacts.find(Contacts.class, "number = ?", contact_num);
//        Long leadID = null;
//        Long contactID = null;
//        String contact_name = "";
//        for (Contacts contacts : contactsList) {
//            contactID = contacts.getId();
//            if (contacts.getName() != null) {
//                contact_name = contacts.getName();
//            }
//        }
//        List<Lead> leadList = Lead.find(Lead.class, "contact_id = ? ", "" + contactID);
//        for (Lead lead : leadList) {
//            leadID = lead.getId();
//        }
//
////        List<Calls> callList = Calls.listAll(Calls.class);
//        List<Calls> callList = Calls.find(Calls.class, "number = ? and date_time = ?",
//                contact_num, outgoing_datetime);
//        if (callList.size() == 0 && contactID != null && leadID != null) {
//            Calls call = new Calls(contactID, leadID,
//                    contact_name, contact_num, outgoing_datetime, outgoing_duration,
//                    outgoing_type, audio_file_name, SyncStatus.SYNC_STATUS_SYNCED);
//            call.save();
//            String o = "";
//        }/*else {
//            for (Calls call : callList){
//                if (!call.number.equals(contact_name) && !call.dateTime.equals(outgoing_datetime)
//                        && !call.type.equals(outgoing_type)){
//                    //save call records from server
//                    call = new Calls(contactID, leadID,
//                            contact_name, contact_num, outgoing_datetime, outgoing_duration,
//                            outgoing_type, SyncStatus.SYNC_STATUS_SYNCED);
//                    call.save();
//                }else {
//                    //do nothing
//                }
//            }
//        }
//*/
//    }
//
//    private void incomingCallsFetchingHTTP() {
//        List<LastSyncDetails> syncDetailses = LastSyncDetails.find(
//                LastSyncDetails.class, "attribute = ?", SyncAttributes.incoming_call);
//        String lastSync = syncDetailses.get(0).getLastUpdated();
//
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("agent_num", "" + pref.getKEY_MOBILE()));
//        params.add(new BasicNameValuePair("hash", "" + pref.getHashKey()));
//        params.add(new BasicNameValuePair("last_sync", "" + lastSync));
//
//        Log.d(TAG, "12)IncomingCallsFetching Requesting...");
//        String resultServer = Http_Request.getHttpPost(MyURLs.INCOMING_CALLS_FETCHING_URL, params);
//        Log.d(TAG, "12)IncomingCallsFetching Response: " + resultServer);
//        JSONObject jobj;
//        try {
//            // Create Apache HttpClient
//            jobj = new JSONObject(resultServer);
//            // JSONObject jobj = new JSONObject(response);
//            int error = jobj.getInt("error");
//
//            JSONArray incomingCallsArray;
//            JSONArray inquiriesCallsArray;
//            JSONObject leadObj;
//
//            String contact_num, contact_name, incoming_duration, incoming_datetime,
//                    incoming_end_datetime, incoming_type, inquiry, audio_file_name;
//
//            incomingCallsServerToAppResponse = "" + error;
//
//            if (error == 0) {
//                incomingCallsArray = jobj.getJSONArray("incomingcalls");
//                inquiriesCallsArray = jobj.getJSONArray("inquiries");
//                Log.d(MyLogTags.lastSync, "incomingJsonListSize : " + incomingCallsArray.length());
//                Log.d(MyLogTags.lastSync, "inquiriesJsonListSize : " + inquiriesCallsArray.length());
//
//                for (int i = 0; i < incomingCallsArray.length(); i++) {
//                    leadObj = incomingCallsArray.getJSONObject(i);
//                    contact_num = leadObj.getString("contact_num");
//                    contact_name = leadObj.getString("contact_name");
//                    incoming_duration = leadObj.getString("duration");
//                    incoming_datetime = leadObj.getString("datetime");
//                    incoming_end_datetime = leadObj.getString("end_datetime");
//                    incoming_type = leadObj.getString("status");
//                    audio_file_name = leadObj.getString("audio_file_name");
//                    inquiry = leadObj.getString("inquiry");
//                    if (/*inquiry.equals("1") || inquiry.equals(1) ||*/
//                            ((inquiry.equals("0") || inquiry.equals(0))
//                                    && !incoming_end_datetime.equals(MyDateTimeStamp.emptyTimeStamp)
//                                    && !incoming_end_datetime.equals(incoming_datetime))) {
//                        insertInquiry(contact_num, contact_name, incoming_datetime,
//                                incoming_end_datetime, incoming_type);//resolved inquiries
//                    } else {
//                        insertIncoming(contact_num, incoming_duration, incoming_datetime,
//                                incoming_type, audio_file_name);
//                    }
//                    if ((i + 1) == incomingCallsArray.length()) {
//                        MySyncFetching.setLastSyncDate(SyncAttributes.incoming_call);
//                    }
//                }
//                for (int i = 0; i < inquiriesCallsArray.length(); i++) {//pending inquiries
//                    leadObj = inquiriesCallsArray.getJSONObject(i);
//                    contact_num = leadObj.getString("contact_num");
//                    contact_name = leadObj.getString("contact_name");
//                    incoming_datetime = leadObj.getString("datetime");
//                    incoming_end_datetime = leadObj.getString("end_datetime");
//                    incoming_type = leadObj.getString("status");
//                    insertInquiry(contact_num, contact_name, incoming_datetime,
//                            incoming_end_datetime, incoming_type);
//                    if ((i + 1) == inquiriesCallsArray.length()) {
//                        MySyncFetching.setLastSyncDate(SyncAttributes.inquiry);
//                    }
//                }
//            } else if (error == 1) {
////                Toast.makeText(context, "lead sync no agent found.", Toast.LENGTH_LONG).show();
//            }
//
//        } catch (Exception e) {
//            Log.d(TAG, "12)IncomingCallsFetchingResponse Exception: " + e.getMessage());
////            Toast.makeText(context, "lead sync catch:"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void inquiryDeleteFetchingHTTP() {
//        List<LastSyncDetails> syncDetailses = LastSyncDetails.find(
//                LastSyncDetails.class, "attribute = ?", SyncAttributes.incoming_call);
//        String lastSync = syncDetailses.get(0).getLastUpdated();
//
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("agent_num", "" + pref.getKEY_MOBILE()));
//        params.add(new BasicNameValuePair("hash", "" + pref.getHashKey()));
//        params.add(new BasicNameValuePair("last_sync", "" + lastSync));
//
//        Log.d(TAG, "11)InquiriesDelete Requesting...");
//        String resultServer = Http_Request.getHttpPost(MyURLs.INQUIRIES_DELETE_FETCHING_URL, params);
//        Log.d(TAG, "11)InquiriesDeleteResponse: " + resultServer);
//        JSONObject jobj;
//        try {
//            // Create Apache HttpClient
//            jobj = new JSONObject(resultServer);
//            // JSONObject jobj = new JSONObject(response);
//            int error = jobj.getInt("error");
//
//            JSONArray resolved_inquiries;
//            JSONObject leadObj;
//
//            String contact_num, contact_name, incoming_duration, incoming_datetime,
//                    incoming_end_datetime, inquiry_status, incoming_type, inquiry, audio_file_name;
//
//            incomingCallsServerToAppResponse = "" + error;
//
//            if (error == 0) {
//                resolved_inquiries = jobj.getJSONArray("resolvedinquiries");
//
//                for (int i = 0; i < resolved_inquiries.length(); i++) {//pending inquiries
//                    leadObj = resolved_inquiries.getJSONObject(i);
//                    contact_num = leadObj.getString("contact_num");
//                    incoming_datetime = leadObj.getString("datetime");
//                    incoming_end_datetime = leadObj.getString("end_datetime");
//                    inquiry_status = leadObj.getString("inquiry");
//                    incoming_type = leadObj.getString("status");
//                    deleteInquiry(contact_num, incoming_datetime, incoming_type);
//                    if ((i + 1) == resolved_inquiries.length()) {
//                        MySyncFetching.setLastSyncDate(SyncAttributes.delete_inquiry);
//                    }
//                }
//
//            } else if (error == 1) {
////                Toast.makeText(context, "lead sync no agent found.", Toast.LENGTH_LONG).show();
//            }
//
//        } catch (Exception e) {
//            Log.d(TAG, "11)InquiriesDeleteResponse Exception: " + e.getMessage());
////            Toast.makeText(context, "lead sync catch:"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void insertIncoming(String contact_num, String incoming_duration,
//                                String incoming_datetime, String incoming_type, String audio_file_name) {
//        String datee = incoming_datetime.substring(0, 8);
//        String timee = incoming_datetime.substring(9);
//        List<Contacts> contactsList = Contacts.find(Contacts.class, "number = ?", contact_num);
//        Long leadID = null;
//        Long contactID = null;
//        String contact_name = "";
//        for (Contacts contacts : contactsList) {
//            contactID = contacts.getId();
//            if (contacts.getName() != null) {
//                contact_name = contacts.getName();
//            }
//        }
//        List<Lead> leadList = Lead.find(Lead.class, "contact_id = ? ", "" + contactID);
//        for (Lead lead : leadList) {
//            leadID = lead.getId();
//        }
//
////        List<Calls> callList = Calls.listAll(Calls.class);
//        List<Calls> callList = Calls.find(Calls.class, "number = ? and date_time = ?",
//                contact_num, incoming_datetime);
//        if (callList.size() == 0 && contactID != null && leadID != null) {
//            Calls call = new Calls(contactID, leadID,
//                    contact_name, contact_num, incoming_datetime, incoming_duration,
//                    incoming_type, audio_file_name, SyncStatus.SYNC_STATUS_SYNCED);
//            call.save();
//        }
///*
//        else {
//            for (Calls call : callList){
//                if (!call.number.equals(contact_name) && !call.date.equals(datee)
//                        && !call.time.equals(timee) && !call.type.equals(incoming_type)){
//                    //save call records from server
//                    call = new Calls(contactID, leadID,
//                            contact_name, contact_num, datee, timee, incoming_duration,
//                            incoming_type, SyncStatus.SYNC_STATUS_SYNCED);
//                    call.save();
//                }else {
//                    //do nothing
//                }
//            }
//        }
//*/
//
//    }
//
//    private void insertInquiry(String contact_num, String contact_name, String incoming_datetime,
//                               String incomingEndDateTime, String incoming_type) {
//        List<Contacts> contactsList = Contacts.find(Contacts.class, "number = ?", contact_num);
////        List<Inquiry> inquiryList = Inquiry.listAll(Inquiry.class);
//        List<Inquiry> inquiryList = Inquiry.find(Inquiry.class,
//                "number = ? and start_date_time = ?", contact_num, incoming_datetime);
//
//        if (inquiryList.size() == 0) {
//
//            if (contactsList.size() == 0) { // if no contact or lead is created against inquiry
//                if (incomingEndDateTime.equals("0000-00-00 00:00:00")
//                        || incomingEndDateTime.equals(incoming_datetime)) {
//                    Inquiry inquiry1 = new Inquiry(
//                            contact_name, contact_num, incoming_datetime, incomingEndDateTime,
//                            incoming_type, SyncStatus.SYNC_STATUS_SYNCED);
//                    inquiry1.save();
//                    insertIncoming(contact_num, INCOMING_ZERO_DURATION,
//                            incoming_datetime, incoming_type, ""); // in calls
//                } else { // inquiry is resolved
//                    Inquiry inquiry1 = new Inquiry(
//                            contact_name, contact_num, incoming_datetime, incomingEndDateTime,
//                            incoming_type, SyncStatus.SYNC_STATUS_INQUIRY_RESOLVED_SYNCED);
//                    inquiry1.save();
//                }
//            } else { // if inquiry is against any lead or contact
//                for (Contacts contacts : contactsList) {
//                    if (contacts.getName() != null || !contacts.getName().equals("")) {
//                        if (incomingEndDateTime.equals("0000-00-00 00:00:00")
//                                || incomingEndDateTime.equals(incoming_datetime)) {
//                            Inquiry inquiry1 = new Inquiry(
//                                    contacts.getName(), contact_num, incoming_datetime,
//                                    incomingEndDateTime, incoming_type, SyncStatus.SYNC_STATUS_SYNCED);
//                            inquiry1.save();
//                            insertIncoming(contact_num, INCOMING_ZERO_DURATION, incoming_datetime, incoming_type, ""); // in calls
//                        } else { // inquiry is resolved
//                            Inquiry inquiry1 = new Inquiry(
//                                    contacts.getName(), contact_num, incoming_datetime,
//                                    incomingEndDateTime, incoming_type, SyncStatus.SYNC_STATUS_INQUIRY_RESOLVED_SYNCED);
//                            inquiry1.save();
//                        }
//                    } else {
//                        if (incomingEndDateTime.equals("0000-00-00 00:00:00")
//                                || incomingEndDateTime.equals(incoming_datetime)) {
//                            Inquiry inquiry1 = new Inquiry(
//                                    contact_name, contact_num, incoming_datetime, incomingEndDateTime,
//                                    incoming_type, SyncStatus.SYNC_STATUS_SYNCED);
//                            inquiry1.save();
//                            insertIncoming(contact_num, INCOMING_ZERO_DURATION, incoming_datetime, incoming_type, ""); // in calls
//                        } else { // inquiry is resolved
//                            Inquiry inquiry1 = new Inquiry(
//                                    contact_name, contact_num, incoming_datetime, incomingEndDateTime,
//                                    incoming_type, SyncStatus.SYNC_STATUS_INQUIRY_RESOLVED_SYNCED);
//                            inquiry1.save();
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private void deleteInquiry(String contact_num, String incoming_datetime,
//                               String incoming_type) {
//        List<Inquiry> inquiryList = Inquiry.find(Inquiry.class,
//                "number = ? and start_date_time = ?", contact_num, incoming_datetime);
//        if (inquiryList.size() != 0) {
//            for (Inquiry inquiry : inquiryList) {
//                inquiry.delete();
//                Log.d(TAG, "inquiry deleted: " + contact_num);
//            }
//        }
//        List<Calls> calls = Calls.find(Calls.class,
//                "number = ? and type = ?", contact_num, incoming_type);
//        if (calls.size() != 0) {
//            for (Calls call : calls) {
//                call.delete();
//            }
//        }
//    }
//
//    private void leadsFetchingHTTP() {
//        List<LastSyncDetails> syncDetailses = LastSyncDetails.find(
//                LastSyncDetails.class, "attribute = ?", SyncAttributes.lead);
//        String lastSync = syncDetailses.get(0).getLastUpdated();
//
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("agent_num", "" + pref.getKEY_MOBILE()));
//        params.add(new BasicNameValuePair("hash", "" + pref.getHashKey()));
//        params.add(new BasicNameValuePair("client_id", "" + pref.getClientId()));
//        params.add(new BasicNameValuePair("agent_id", "" + pref.getAgentId()));
//        params.add(new BasicNameValuePair("last_sync", "" + lastSync));
//
//        Log.d(TAG, "9)LeadServerToAppFetching Requesting...");
//        String resultServer = Http_Request.getHttpPost(MyURLs.LEAD_FETCHING_URL, params);
//        Log.d(TAG, "9)LeadServerToAppFetchingResponse: " + resultServer);
//        JSONObject jobj;
//        try {
//            // Create Apache HttpClient
//            jobj = new JSONObject(resultServer);
//            // JSONObject jobj = new JSONObject(response);
//            int error = jobj.getInt("error");
//
//            JSONArray leads;
//            JSONObject leadObj;
//
//            String title, lead_status, notes, timestamp, product_interest;
//            String persistence, lrt, email, number, name, follow_up;
//
//            leadServerToAppResponse = "" + error;
//
//            if (error == 0) {
//                leads = jobj.getJSONArray("leads");
//                Log.d(MyLogTags.lastSync, "leadJsonListSize : " + leads.length());
//
//                for (int i = 0; i < leads.length(); i++) {
//                    leadObj = leads.getJSONObject(i);
//                    title = leadObj.getString("Title");
//                    lead_status = leadObj.getString("lead_status");
//                    notes = leadObj.getString("Notes");
//                    persistence = leadObj.getString("persistance");
//                    lrt = leadObj.getString("lrt");
//                    email = leadObj.getString("email");
//                    number = leadObj.getString("phone");
//                    name = leadObj.getString("name");
//                    timestamp = leadObj.getString("timestamp");
//                    product_interest = leadObj.getString("Product_Interest");
//                    follow_up = leadObj.getString("follow_up");
//
//                    lead_status = LeadStatus.getLeadStatus(lead_status);
//
//                    insertLeadContact(number, name, lead_status,
//                            product_interest, timestamp, email, follow_up);
//
//                    if ((i + 1) == leads.length()) {
//                        MySyncFetching.setLastSyncDate(SyncAttributes.lead);
//                    }
//
//                }
//
//
//            } else if (error == 1) {
////                Toast.makeText(context, "lead sync no agent found.", Toast.LENGTH_LONG).show();
//            }
//
//        } catch (Exception e) {
//            Log.d(TAG, "9)LeadServerToAppFetchingResponse Exception: " + e.getMessage());
////            Toast.makeText(context, "lead sync catch:"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void leadsDeleteFetchingHTTP() {
//        List<LastSyncDetails> syncDetailses = LastSyncDetails.find(
//                LastSyncDetails.class, "attribute = ?", SyncAttributes.lead_delete);
//        String lastSync = syncDetailses.get(0).getLastUpdated();
//
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("agent_num", "" + pref.getKEY_MOBILE()));
//        params.add(new BasicNameValuePair("hash", "" + pref.getHashKey()));
//        params.add(new BasicNameValuePair("client_id", "" + pref.getClientId()));
//        params.add(new BasicNameValuePair("agent_id", "" + pref.getAgentId()));
//        params.add(new BasicNameValuePair("last_sync", "" + lastSync));
//
//        Log.d(TAG, "14)LeadsDeleteFetching Requesting...");
//        String resultServer = Http_Request.getHttpPost(MyURLs.LEAD_DELETE_URL, params);
//        Log.d(TAG, "14)LeadsDeleteFetching Response: " + resultServer);
//
//        JSONObject jobj;
//        try {
//            // Create Apache HttpClient
//            jobj = new JSONObject(resultServer);
//            // JSONObject jobj = new JSONObject(response);
//            int error = jobj.getInt("error");
//
//            JSONArray leads = jobj.getJSONArray("leads");
//            Log.d(MyLogTags.lastSync, "leadsDeleteJsonListSize : " + leads.length());
//
//            JSONObject leadObj;
//
//            String number;
//
//            leadServerToAppResponse = "" + error;
//
//            if (error == 0) {
////                leads = jobj.getJSONArray("leads");
//                for (int i = 0; i < leads.length(); i++) {
//                    leadObj = leads.getJSONObject(i);
//
//                    number = leadObj.getString("phone");
//
//                    if (number != null) {
//                        deleteLeadContact(number);
//                        if ((i + 1) == leads.length()) {
//                            MySyncFetching.setLastSyncDate(SyncAttributes.lead_delete);
//                        }
//                    } else {//do nothing
//                    }
//                }
//
//            } else if (error == 1) {
////                Toast.makeText(context, "lead sync no agent found.", Toast.LENGTH_LONG).show();
//            }
//
//        } catch (Exception e) {
//            Log.d(TAG, "14)LeadsDeleteFetchingResponse Exception: " + e.getMessage());
////            Toast.makeText(context, "lead sync catch:"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void contactsDeleteFetchingHTTP() {
//        List<LastSyncDetails> syncDetailses = LastSyncDetails.find(
//                LastSyncDetails.class, "attribute = ?", SyncAttributes.contact_delete);
//        String lastSync = syncDetailses.get(0).getLastUpdated();
//
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("agent_num", "" + pref.getKEY_MOBILE()));
//        params.add(new BasicNameValuePair("hash", "" + pref.getHashKey()));
//        params.add(new BasicNameValuePair("client_id", "" + pref.getClientId()));
//        params.add(new BasicNameValuePair("agent_id", "" + pref.getAgentId()));
//        params.add(new BasicNameValuePair("last_sync", "" + lastSync));
//
//        Log.d(TAG, "15)ContactsDeleteFetching Requesting From: " + lastSync + " ...");
//        String resultServer = Http_Request.getHttpPost(MyURLs.CONTACT_DELETE_URL, params);
//        Log.d(TAG, "15)ContactsDeleteFetching Response: " + resultServer);
//
//        JSONObject jobj;
//        try {
//            // Create Apache HttpClient
//            jobj = new JSONObject(resultServer);
//            // JSONObject jobj = new JSONObject(response);
//            int error = jobj.getInt("error");
//
//            JSONArray contactsJsonArray = jobj.getJSONArray("contacts");
//
//            JSONObject leadObj;
//
//            String number;
//
//            if (error == 0) {
//                for (int i = 0; i < contactsJsonArray.length(); i++) {
//                    leadObj = contactsJsonArray.getJSONObject(i);
//
//                    number = leadObj.getString("phone");
//
//                    if (number != null) {
//                        deleteContact(number);
//                        if ((i + 1) == contactsJsonArray.length()) {
//                            MySyncFetching.setLastSyncDate(SyncAttributes.contact_delete);
//                        }
//                    } else {
//                        //do nothing
//                    }
//                }
//
//            }
//
//        } catch (Exception e) {
//            Log.d(TAG, "15)ContactsDeleteFetchingResponse Exception: " + e.getMessage());
//        }
//    }
//
//    private void contactsFetchingHTTP() {
//        List<LastSyncDetails> syncDetailses = LastSyncDetails.find(
//                LastSyncDetails.class, "attribute = ?", SyncAttributes.contacts);
//        String lastSync = syncDetailses.get(0).getLastUpdated();
//
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("agent_num", "" + pref.getKEY_MOBILE()));
//        params.add(new BasicNameValuePair("hash", "" + pref.getHashKey()));
//        params.add(new BasicNameValuePair("client_id", "" + pref.getClientId()));
//        params.add(new BasicNameValuePair("agent_id", "" + pref.getAgentId()));
//        params.add(new BasicNameValuePair("last_sync", "" + lastSync));
//
//        Log.d(TAG, "10)ContactsFetchingToApp Requesting...");
//        String resultServer = Http_Request.getHttpPost(MyURLs.CONTACTS_FETCHING_URL, params);
//        Log.d(TAG, "10)ContactsFetchingToApp Response: " + resultServer);
//        JSONObject jobj;
//        try {
//            // Create Apache HttpClient
//            jobj = new JSONObject(resultServer);
//            // JSONObject jobj = new JSONObject(response);
//            int error = jobj.getInt("error");
//
//            JSONArray contactsJsonArray;
//            JSONObject contactObj;
//
//            String number, name, contact_type, created_at;
//
//            privateContactServerToAppResponse = "" + error;
//
//            if (error == 0) {
//                contactsJsonArray = jobj.getJSONArray("contacts");
//                Log.d(MyLogTags.lastSync, "contactsJsonListSize : " + contactsJsonArray.length());
//                for (int i = 0; i < contactsJsonArray.length(); i++) {
//                    contactObj = contactsJsonArray.getJSONObject(i);
//                    number = contactObj.getString("phone");
//                    name = contactObj.getString("name");
//                    contact_type = contactObj.getString("contact_type");
//                    created_at = contactObj.getString("created_at");
//
//                    insertContact(mContext, number, name, created_at, contact_type);
//
//                    if ((i + 1) == contactsJsonArray.length()) {
//                        MySyncFetching.setLastSyncDate(SyncAttributes.contacts);
//                    }
//
//                }
//
//            } else if (error == 1) {
////                Toast.makeText(context, "lead sync no agent found.", Toast.LENGTH_LONG).show();
//            }
//
//        } catch (Exception e) {
//            Log.d(TAG, "10)ContactsFetchingToAppResponse Exception: " + e.getMessage());
////            Toast.makeText(context, "lead sync catch:"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void insertLeadContact(String number, String name,
//                                   String lead_status, String product, String time_created,
//                                   String email, String follow_up) {
//
//        List<MyAlarms> myAlarmsList = MyAlarms.find(MyAlarms.class, "number = ?", number);
//        List<MyAlarms> alarmsList = MyAlarms.find(MyAlarms.class, "alarm_time = ?", follow_up);
//        List<Contacts> contactsList = Contacts.find(Contacts.class, "number = ?", number);
//        if (contactsList.size() == 0 || contactsList.equals(null)) {
//
//            List<LoginInfo> loginInfoList = LoginInfo.find(LoginInfo.class, "agent_mobile = ? ", pref.getKEY_MOBILE());
//            String agentID = null;
//            for (LoginInfo loginInfo : loginInfoList) {
//                agentID = "" + loginInfo.getId();
//            }
//
//            Contacts contacts1 = new Contacts(agentID, name, number, ContactType.Business,
//                    time_created, SyncStatus.SYNC_STATUS_SYNCED, email);
//            contacts1.save();
//
//            Lead lead = new Lead(contacts1.getId(), time_created, lead_status,
//                    MyDateTimeStamp.emptyTimeStamp, product, SyncStatus.SYNC_STATUS_SYNCED);
//            lead.save();
//
//            List<Notes> notesList = Notes.find(Notes.class, "contact_id = ? ", "" + lead.getContactID());
//            if (notesList.size() == 0) {// create new notes in notes table
//                Notes notes = new Notes("" + lead.getContactID(), product, time_created);
//                notes.save();
//            } else {// update previous notes
//                for (Notes notes : notesList) {
//                    notes.note = product;
//                    notes.setCreated_at(time_created);
//                    notes.save();
//                }
//            }
//
//            if (myAlarmsList.size() == 0 && !follow_up.equals(MyDateTimeStamp.emptyTimeStamp)
//                    && alarmsList.size() == 0) {
//                MyAlarms myAlarms = new MyAlarms("" + contacts1.getId(),
//                        name, number, follow_up, "Follow up",
//                        SyncStatus.SYNC_STATUS_SYNCED);
//                myAlarms.save();
//            }
////            long contactID = contacts1.getId()
////            long leadID = lead.getId();
//
////            Toast.makeText(context, "Synced from Server to App",
////                    Toast.LENGTH_LONG).show();
//        } else {
//            String contactID = null;
//            for (Contacts contacts : contactsList) {
//                contactID = "" + contacts.getId();
//            }
//            List<Lead> leadlist = Lead.find(Lead.class, "contact_id = ?", contactID);
//            for (Lead lead : leadlist) {
//                lead.setLeadStatus(lead_status);
//                lead.setProduct(product);
//                lead.save();
//            }
//            List<Notes> notesList = Notes.find(Notes.class, "contact_id = ? ", contactID);
//            if (notesList.size() == 0) {// create new notes in notes table
//                Notes notes = new Notes(contactID, product, time_created);
//                notes.save();
//            } else {// update previous notes
//                for (Notes notes : notesList) {
//                    notes.note = product;
//                    notes.setCreated_at(time_created);
//                    notes.save();
//                }
//            }
//            if (myAlarmsList.size() == 0 && !follow_up.equals(MyDateTimeStamp.emptyTimeStamp)
//                    && alarmsList.size() == 0) {
//                MyAlarms myAlarms = new MyAlarms("" + contactID,
//                        name, number, follow_up, "Follow up",
//                        SyncStatus.SYNC_STATUS_SYNCED);
//                myAlarms.save();
//            }
//
//        }
//    }
//
//    private void deleteLeadContact(String number) {
//
//        List<Contacts> contactsList = Contacts.find(Contacts.class,
//                "number = ?", number);
//
//        if (contactsList.size() != 0) {
//
//            String contactID = null;
//            for (Contacts contacts : contactsList) {
//                contactID = "" + contacts.getId();
////                contacts.delete();
//            }
//
//            List<Lead> leadList = Lead.find(Lead.class,
//                    "contact_id = ?", contactID);
//            for (Lead lead : leadList) {
//                lead.delete();
//            }
//
//            List<Calls> callList = Calls.find(Calls.class,
//                    "contact_id = ? or number = ?", contactID, number);
//            for (Calls call : callList) {
//                call.delete();
//            }
//        }
//    }
//
//    private void deleteContact(String number) {
//
//        List<Contacts> contactsList = Contacts.find(Contacts.class,
//                "number = ?", number);
//
//        if (contactsList.size() != 0) {
//
//            String contactID = null;
//            String contactName = "";
//            for (Contacts contacts : contactsList) {
//                contactID = "" + contacts.getId();
//                contactName = "" + contacts.getName();
//                contacts.delete();
//            }
//
//            List<Lead> leadList = Lead.find(Lead.class,
//                    "contact_id = ?", contactID);
//            for (Lead lead : leadList) {
//                lead.delete();
//            }
//
//            List<Calls> callList = Calls.find(Calls.class,
//                    "contact_id = ? or number = ?", contactID, number);
//            for (Calls call : callList) {
//                call.delete();
//            }
//
//            Log.d(TAG, "15)ContactDelete: " + contactName + "(" + number + ") Deleted");
//        }
//    }
//
//    private void insertContact(Context context, String number, String name, String time_created, String contact_type) {
//
//        List<Contacts> contactsList = Contacts.find(Contacts.class, "number = ?", number);
//
//        if (contactsList.size() == 0 || contactsList.equals(null)) {
//
//            List<LoginInfo> loginInfoList = LoginInfo.find(LoginInfo.class, "agent_mobile = ? ", pref.getKEY_MOBILE());
//            String agentID = null;
//            for (LoginInfo loginInfo : loginInfoList) {
//                agentID = "" + loginInfo.getId();
//            }
//
//            if (contact_type.equals("private")) {
//                Contacts contacts1 = new Contacts(agentID, name, number, ContactType.Private,
//                        time_created, SyncStatus.SYNC_STATUS_SYNCED, "");
//                contacts1.save();
//            } else {
//                Contacts contacts1 = new Contacts(agentID, name, number, ContactType.Business,
//                        time_created, SyncStatus.SYNC_STATUS_SYNCED, "");
//                contacts1.save();
//            }
////            Toast.makeText(context, "Synced from Server to App",
////                    Toast.LENGTH_LONG).show();
//        } else {
////            String contactID = null;
////            for (Contacts contacts: contactsList){
////                contacts.type = ContactType.Private;
////            }
//
//        }
//    }
//
//    @Override
//    protected void onProgressUpdate(Void... values) {
//        // TODO Auto-generated method stub
//        super.onProgressUpdate(values);
//    }
//
//    //this method is executed when doInBackground function finishes
//    @Override
//    protected void onPostExecute(Void result) {
//        // TODO Auto-generated method stub
//    }
//}