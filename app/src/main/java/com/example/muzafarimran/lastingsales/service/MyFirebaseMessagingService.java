package com.example.muzafarimran.lastingsales.service;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.app.FireBaseConfig;
import com.example.muzafarimran.lastingsales.events.ContactDeletedEventModel;
import com.example.muzafarimran.lastingsales.events.InquiryDeletedEventModel;
import com.example.muzafarimran.lastingsales.events.LeadContactAddedEventModel;
import com.example.muzafarimran.lastingsales.events.NoteAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSDeal;
import com.example.muzafarimran.lastingsales.providers.models.LSDynamicColumns;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.FireBaseNotificationUtils;
import com.example.muzafarimran.lastingsales.utils.FirebaseCustomNotification;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;
import com.example.muzafarimran.lastingsales.utils.TypeManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by Ravi Tamada on 08/08/16.
 * www.androidhive.info
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    String mMsg;
    SessionManager sessionManager;
    private FireBaseNotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "MessageReceived");
        sessionManager = new SessionManager(getApplicationContext());
        Log.d(TAG, "onMessageReceived: Firebase ID: " + sessionManager.getKeyLoginFirebaseRegId());
        try {
            if (sessionManager.isUserSignedIn()) {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private void handleDataMessage(JSONObject json) {

        Log.e(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");
            Log.e(TAG, "DataValues: " + data.toString());

            String tag = data.getString("tag");
            Log.e(TAG, "TagValues: " + tag);

            String action = data.getString("action");
            Log.e(TAG, "ActionValues: " + action);

            JSONObject payload = data.getJSONObject("payload");
            Log.e(TAG, "PayloadValues: " + payload.toString());

            if (tag.equals("Lead")) {
                if (action.equals("post")) {

                    String id = payload.getString("id");
                    String name = payload.getString("name");
                    String email = null;
                    if (payload.has("email") && !payload.isNull("email")) {
                        email = payload.getString("email");
                    }
                    String phone = payload.getString("phone");
                    String address = null;
                    if (payload.has("address")) {
                        address = payload.getString("address");
                    }
                    String status = payload.getString("status");

                    String dynamic_values = null;
                    if (payload.has("dynamic_values")) {
                        dynamic_values = payload.getString("dynamic_values");
                    }
                    int version = -1;
                    if (payload.has("version") && !payload.isNull("version")) {
                        version = payload.getInt("version");
                    }
//                    String created_at = null;
//                    if (payload.has("created_at")) {
//                        created_at = payload.getString("created_at");
//                    }
                    String updated_at = null;
                    if (payload.has("updated_at")) {
                        updated_at = payload.getString("updated_at");
                    }

                    int created_by = 0;
                    if (payload.has("created_by")) {
                        created_by = payload.getInt("created_by");
                    }

                    int user_id = 0;
                    if (payload.has("user_id")) {
                        user_id = payload.getInt("user_id");
                    }
                    String src = null;
                    if (payload.has("src")) {
                        src = payload.getString("src");
                    }

                    mMsg = name;
                    Log.e(TAG, "handleDataMessageName: " + name);

                    String intlNum = PhoneNumberAndCallUtils.numberToInterNationalNumber(getApplicationContext(), phone);
                    LSContact tempContact = LSContact.getContactFromNumber(phone);
                    if (tempContact != null) {
                        tempContact.setServerId(id);
                        tempContact.setContactName(name);
                        if (email != null) {
                            tempContact.setContactEmail(email);
                        }
                        if (address != null) {
                            tempContact.setContactAddress(address);
                        }
                        tempContact.setPhoneOne(intlNum);
                        tempContact.setContactType(LSContact.CONTACT_TYPE_SALES);
                        tempContact.setContactSalesStatus(status);
//                        tempContact.setContactUpdated_at(Calendar.getInstance().getTimeInMillis()+"");
                        if (dynamic_values != null) {
                            tempContact.setDynamic(dynamic_values);
                        }
                        if (version != -1) {
                            tempContact.setVersion(version);
                        }
                        tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED);
//                        if(created_at != null){
//                            tempContact.setContactCreated_at(created_at);
//                        }
                        if (updated_at != null) {
                            tempContact.setUpdatedAt(PhoneNumberAndCallUtils.getMillisFromSqlFormattedDate(updated_at));
                        }
                        if (created_by != 0) {
                            tempContact.setCreatedBy(created_by);
                        }
                        if (user_id != 0) {
                            tempContact.setUserId(user_id);
                        }
                        if (src != null) {
                            tempContact.setSrc(src);
                        }
                        tempContact.save();
                        if (src.equals("facebook")) {
                            Log.d(TAG, "handleDataMessage: Notification Message: Lead from FB: " + name);
                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            mNotificationManager.notify(FirebaseCustomNotification.NOTIFICATION_ID, FirebaseCustomNotification.createFirebaseFacebookLeadNotification(getApplicationContext(), name));
                        }
                        if (src.equals("assigned")) {
                            Log.d(TAG, "handleDataMessage: Notification Message: Lead from assigned: " + name);
                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            mNotificationManager.notify(FirebaseCustomNotification.NOTIFICATION_ID, FirebaseCustomNotification.createFirebaseAssignedLeadNotification(getApplicationContext(), name));
                        }
                    } else {
                        LSContact contact = new LSContact();
                        contact.setServerId(id);
                        contact.setContactName(name);
                        if (email != null) {
                            contact.setContactEmail(email);
                        }
                        contact.setPhoneOne(phone);
                        if (address != null) {
                            contact.setContactAddress(address);
                        }
                        contact.setContactType(LSContact.CONTACT_TYPE_SALES);
                        contact.setContactSalesStatus(status);
//                        if(created_at != null){
//                            tempContact.setContactCreated_at(created_at);
//                        }
                        if (dynamic_values != null) {
                            contact.setDynamic(dynamic_values);
                        }
                        if (version != -1) {
                            contact.setVersion(version);
                        }
                        contact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED);
                        if (updated_at != null) {
                            contact.setUpdatedAt(PhoneNumberAndCallUtils.getMillisFromSqlFormattedDate(updated_at));
                        }
                        if (created_by != 0) {
                            contact.setCreatedBy(created_by);
                        }
                        if (user_id != 0) {
                            contact.setUserId(user_id);
                        }
                        if (src != null) {
                            contact.setSrc(src);
                        }
                        contact.save();
                        if (src.equals("facebook")) {
                            Log.d(TAG, "handleDataMessage: Notification Message: Lead from FB: " + name);
                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            mNotificationManager.notify(FirebaseCustomNotification.NOTIFICATION_ID, FirebaseCustomNotification.createFirebaseFacebookLeadNotification(getApplicationContext(), name));
                        }
                        if (src.equals("assigned")) {
                            Log.d(TAG, "handleDataMessage: Notification Message: Lead from assigned: " + name);
                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            mNotificationManager.notify(FirebaseCustomNotification.NOTIFICATION_ID, FirebaseCustomNotification.createFirebaseAssignedLeadNotification(getApplicationContext(), name));
                        }
                        Log.e(TAG, "Post From Local DB: " + contact.getContactName());
                        LeadContactAddedEventModel mCallEvent = new LeadContactAddedEventModel();
                        TinyBus bus = TinyBus.from(getApplicationContext());
                        bus.post(mCallEvent);
                    }
                } else if (action.equals("put")) {
                    String id = payload.getString("id");
                    String name = payload.getString("name");
                    String email = null;
                    if (payload.has("email")) {
                        email = payload.getString("email");
                    }
                    String phone = payload.getString("phone");
                    String intlNum = PhoneNumberAndCallUtils.numberToInterNationalNumber(getApplicationContext(), phone);
                    String address = payload.getString("address");
                    String status = payload.getString("status");
                    String lead_type = payload.getString("lead_type");
                    String created_at = null;
//                    if(payload.has("created_at")){
//                        created_at = payload.getString("created_at");
//                    }
                    String updated_at = null;
                    if (payload.has("updated_at")) {
                        updated_at = payload.getString("updated_at");
                    }
                    int created_by = 0;
                    if (payload.has("created_by")) {
                        created_by = payload.getInt("created_by");
                    }

                    int user_id = 0;
                    if (payload.has("user_id")) {
                        user_id = payload.getInt("user_id");
                    }
                    String src = null;
                    if (payload.has("src")) {
                        src = payload.getString("src");
                    }
                    String dynamic_values = null;
                    if (payload.has("dynamic_values")) {
                        dynamic_values = payload.getString("dynamic_values");
                    }
                    int version = -1;
                    if (payload.has("version") && !payload.isNull("version")) {
                        version = payload.getInt("version");
                    }
                    mMsg = name;
                    Log.e(TAG, "handleDataMessageName: " + name);
                    LSContact contact = LSContact.getContactFromServerId(id);
                    String oldType = contact.getContactType();
                    contact.setContactName(name);
                    contact.setContactEmail(email);
                    contact.setPhoneOne(intlNum);
                    contact.setContactAddress(address);
                    contact.setContactSalesStatus(status);
                    if (dynamic_values != null) {
                        contact.setDynamic(dynamic_values);
                    }
                    if (version != -1) {
                        contact.setVersion(version);
                    }
                    contact.setContactType(lead_type);
                    if (updated_at != null) {
                        contact.setUpdatedAt(PhoneNumberAndCallUtils.getMillisFromSqlFormattedDate(updated_at));
                    }
                    if (created_by != 0) {
                        contact.setCreatedBy(created_by);
                    }
                    if (user_id != 0) {
                        contact.setUserId(user_id);
                    }
                    if (src != null) {
                        contact.setSrc(src);
                    }
                    contact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED);
                    contact.save();
                    String newType = contact.getContactType();
                    TypeManager.ConvertTo(getApplicationContext(), contact, oldType, newType);
                    if (src.equals("facebook")) {
                        Log.d(TAG, "handleDataMessage: Notification Message: Lead from FB: " + name);
                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(FirebaseCustomNotification.NOTIFICATION_ID, FirebaseCustomNotification.createFirebaseFacebookLeadNotification(getApplicationContext(), name));
                    }
                    if (src.equals("assigned")) {
                        Log.d(TAG, "handleDataMessage: Notification Message: Lead from assigned: " + name);
                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(FirebaseCustomNotification.NOTIFICATION_ID, FirebaseCustomNotification.createFirebaseAssignedLeadNotification(getApplicationContext(), name));
                    }
                    ContactDeletedEventModel mCallEvent = new ContactDeletedEventModel();
                    TinyBus bus = TinyBus.from(getApplicationContext());
                    bus.post(mCallEvent);

                } else if (action.equals("delete")) {
                    String id = payload.getString("id");
                    LSContact contact = LSContact.getContactFromServerId(id);
                    if (contact != null) {
                        Log.e(TAG, "handleDataMessage: contact: " + contact.toString());
                        contact.delete();
                        LeadContactAddedEventModel mCallEvent = new LeadContactAddedEventModel();
                        TinyBus bus = TinyBus.from(getApplicationContext());
                        bus.post(mCallEvent);
                    }
                }
            }

            if (tag.equals("Note")) {
                if (action.equals("post")) {
                    String id = payload.getString("id");
                    String lead_id = payload.getString("lead_id");
                    String description = payload.getString("description");
                    mMsg = description;
                    LSNote tempNote = new LSNote();
                    tempNote.setServerId(id);
                    tempNote.setNoteText(description);
                    tempNote.setContactOfNote(LSContact.getContactFromServerId(lead_id));
                    tempNote.setSyncStatus(SyncStatus.SYNC_STATUS_NOTE_ADDED_SYNCED);
                    tempNote.setCreatedAt(PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(Calendar.getInstance().getTimeInMillis()));
                    tempNote.save();
                    NoteAddedEventModel mNoteAdded = new NoteAddedEventModel();
                    TinyBus bus = TinyBus.from(getApplicationContext());
                    bus.post(mNoteAdded);

                } else if (action.equals("put")) {
                    Log.d(TAG, "handleDataMessage: NOTE PUT");
                    String id = payload.getString("id");
                    String description = payload.getString("description");
                    LSNote note = LSNote.getNoteByServerId(id);
                    note.setNoteText(description);
                    note.save();
                    NoteAddedEventModel mNoteAdded = new NoteAddedEventModel();
                    TinyBus bus = TinyBus.from(getApplicationContext());
                    bus.post(mNoteAdded);

                } else if (action.equals("delete")) {
                    Log.d(TAG, "handleDataMessage: NOTE delete");
                    String id = payload.getString("id");
                    LSNote note = LSNote.getNoteByServerId(id);
                    note.delete();
                    NoteAddedEventModel mNoteAdded = new NoteAddedEventModel();
                    TinyBus bus = TinyBus.from(getApplicationContext());
                    bus.post(mNoteAdded);
                }
            }

            if (tag.equals("Column")) {
                if (action.equals("post")) {
                    String id = "";
                    if (payload.has("id")) {
                        id = payload.getString("id");
                    }
                    String column_type = "";
                    if (payload.has("column_type")) {
                        column_type = payload.getString("column_type");
                    }
                    String name = "";
                    if (payload.has("name")) {
                        name = payload.getString("name");
                    }
                    String default_value_options = "";
                    if (payload.has("default_value_options")) {
                        default_value_options = payload.getString("default_value_options");
                    }
                    String range = "";
                    if (payload.has("range")) {
                        range = payload.getString("range");
                    }
                    String created_by = "";
                    if (payload.has("created_by")) {
                        created_by = payload.getString("created_by");
                    }
                    String updated_by = "";
                    if (payload.has("updated_by")) {
                        updated_by = payload.getString("updated_by");
                    }
                    String created_at = "";
                    if (payload.has("created_at")) {
                        created_at = payload.getString("created_at");
                    }
                    String updated_at = "";
                    if (payload.has("updated_at")) {
                        updated_at = payload.getString("updated_at");
                    }
                    String company_id = "";
                    if (payload.has("company_id")) {
                        company_id = payload.getString("company_id");
                    }
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

                } else if (action.equals("put")) {
                    String id = "";
                    if (payload.has("id")) {
                        id = payload.getString("id");
                    }
                    String column_type = "";
                    if (payload.has("column_type")) {
                        column_type = payload.getString("column_type");
                    }
                    String name = "";
                    if (payload.has("name")) {
                        name = payload.getString("name");
                    }
                    String default_value_options = "";
                    if (payload.has("default_value_options")) {
                        default_value_options = payload.getString("default_value_options");
                    }
                    String range = "";
                    if (payload.has("range")) {
                        range = payload.getString("range");
                    }
                    String created_by = "";
                    if (payload.has("created_by")) {
                        created_by = payload.getString("created_by");
                    }
                    String updated_by = "";
                    if (payload.has("updated_by")) {
                        updated_by = payload.getString("updated_by");
                    }
                    String created_at = "";
                    if (payload.has("created_at")) {
                        created_at = payload.getString("created_at");
                    }
                    String updated_at = "";
                    if (payload.has("updated_at")) {
                        updated_at = payload.getString("updated_at");
                    }
                    String company_id = "";
                    if (payload.has("company_id")) {
                        company_id = payload.getString("company_id");
                    }

                    LSDynamicColumns tempColumn = LSDynamicColumns.getColumnFromServerId(id);
                    if (tempColumn != null) {
                        tempColumn.setColumnType(column_type);
                        tempColumn.setName(name);
                        tempColumn.setDefaultValueOption(default_value_options);
                        tempColumn.setRange(range);
                        tempColumn.setCreated_by(created_by);
                        tempColumn.setUpdated_by(updated_by);
                        tempColumn.setCreated_at(created_at);
                        tempColumn.setUpdated_at(updated_at);
                        tempColumn.setCompanyId(company_id);
                        tempColumn.save();
                    }

                } else if (action.equals("delete")) {
                    String id = payload.getString("id");
                    LSDynamicColumns tempColumn = LSDynamicColumns.getColumnFromServerId(id);
                    if (tempColumn != null) {
                        tempColumn.delete();
                    }
                }
            }
            if (tag.equals("Notification")) {
                if (action.equals("inquiries")) {
                    String message = "";
                    if (payload.has("message")) {
                        message = payload.getString("message");
                        Log.d(TAG, "handleDataMessage: Notification Message: " + message);
                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(FirebaseCustomNotification.NOTIFICATION_ID, FirebaseCustomNotification.createFirebaseInquiriesNotification(getApplicationContext(), message));
                    }
                } else if (action.equals("unlabeled")) {
                    String message = "";
                    if (payload.has("message")) {
                        message = payload.getString("message");
                        Log.d(TAG, "handleDataMessage: Notification Message: " + message);
                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(FirebaseCustomNotification.NOTIFICATION_ID, FirebaseCustomNotification.createFirebaseUnlabeledNotification(getApplicationContext(), message));
                    }
                }
//                else if (action.equals("homescreen")) {
//                    String message = "";
//                    if (payload.has("message")) {
//                        message = payload.getString("message");
//                        Log.d(TAG, "handleDataMessage: Notification Message: " + message);
//                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                        mNotificationManager.notify(FirebaseCustomNotification.NOTIFICATION_ID, FirebaseCustomNotification.createFirebaseHomescreenNotification(getApplicationContext(), message));
//                    }
//                }
            }

            if (tag.equals("Inquiries")) {
                if (action.equals("delete")) {
                    String contact_number = "";
                    if (payload.has("contact_number")) {
                        contact_number = payload.getString("contact_number");
                        LSInquiry lsInquiry = LSInquiry.getPendingInquiryByNumberIfExists(contact_number);
                        lsInquiry.delete();
                        InquiryDeletedEventModel mCallEvent = new InquiryDeletedEventModel();
                        TinyBus bus = TinyBus.from(getApplicationContext());
                        bus.post(mCallEvent);
                    }
                } else if (action.equals("put")) { // not needed yet hence not implemented
                    String contact_number = "";
                    if (payload.has("contact_number")) {
                        contact_number = payload.getString("contact_number");
                        LSInquiry lsInquiry = LSInquiry.getPendingInquiryByNumberIfExists(contact_number);
                        Log.d(TAG, "handleDataMessage: : " + lsInquiry);
                    }
                }
            }

            if (tag.equals("Deal")) {
                if (action.equals("post")) {
                    String id = payload.getString("id");
                    int user_id = 0;
                    if (payload.has("user_id")) {
                        user_id = payload.getInt("user_id");
                    }
                    int company_id = 0;
                    if (payload.has("company_id")) {
                        company_id = payload.getInt("company_id");
                    }
                    String workflow_id = payload.getString("workflow_id");
                    String workflow_stage_id = payload.getString("workflow_stage_id");
                    int lead_id = 0;
                    if (payload.has("lead_id")) {
                        lead_id = payload.getInt("lead_id");
                    }
                    String name = payload.getString("name");
                    String status = payload.getString("status");
                    int created_by = 0;
                    if (payload.has("created_by")) {
                        created_by = payload.getInt("created_by");
                    }
                    String created_at = payload.getString("created_at");
                    String updated_at = payload.getString("updated_at");


                    // ignore if task already exists
                    LSDeal lsDeal = LSDeal.getDealFromServerId(id);
                    if (lsDeal == null) {
                        // check if lead still exists of which the deal is
                        LSContact lsContact = LSContact.getContactFromServerId(Integer.toString(lead_id));
                        if (lsContact != null) {
                            lsDeal = new LSDeal();
                            lsDeal.setServerId(id);
                            lsDeal.setUserId(Integer.toString(user_id));
                            lsDeal.setCompanyId(Integer.toString(company_id));
                            lsDeal.setWorkflowId(workflow_id);
                            lsDeal.setLeadId(Integer.toString(lead_id));
                            lsDeal.setName(name);
                            lsDeal.setStatus(status);
                            lsDeal.setCreatedBy(Integer.toString(created_by));
                            lsDeal.setCreatedAt(created_at);
                            Date updated_atDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(updated_at);
                            lsDeal.setUpdatedAt(updated_atDate);
                            lsDeal.save();

//                            TaskAddedEventModel mCallEvent = new TaskAddedEventModel();
//                            TinyBus bus = TinyBus.from(getApplicationContext());
//                            bus.post(mCallEvent);
                        }
                    }
                }
            }

            if (tag.equals("Comment")) {
                if (action.equals("post")) {
                    String id = payload.getString("id");
                    int lead_id = 0;
                    if (payload.has("lead_id")) {
                        lead_id = payload.getInt("lead_id");
                    }
                    String comment = payload.getString("comment");
                    String status = payload.getString("status");
                    int created_by = 0;
                    if (payload.has("created_by")) {
                        created_by = payload.getInt("created_by");
                    }
                    String created_at = payload.getString("created_at");
                    String updated_at = payload.getString("updated_at");

                    Toast.makeText(getApplicationContext(), "Comment: " + comment, Toast.LENGTH_SHORT).show();

//                    // ignore if task already exists
//                    LSDeal lsDeal = LSDeal.getDealFromServerId(id);
//                    if (lsDeal == null) {
//                        // check if lead still exists of which the deal is
//                        LSContact lsContact = LSContact.getContactFromServerId(Integer.toString(lead_id));
//                        if (lsContact != null) {
//                            lsDeal = new LSDeal();
//                            lsDeal.setServerId(id);
//                            lsDeal.setUserId(Integer.toString(user_id));
//                            lsDeal.setCompanyId(Integer.toString(company_id));
//                            lsDeal.setWorkflowId(workflow_id);
//                            lsDeal.setLeadId(Integer.toString(lead_id));
//                            lsDeal.setName(name);
//                            lsDeal.setStatus(status);
//                            lsDeal.setCreatedBy(Integer.toString(created_by));
//                            lsDeal.setCreatedAt(created_at);
//                            Date updated_atDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(updated_at);
//                            lsDeal.setUpdatedAt(updated_atDate);
//                            lsDeal.save();
//
////                            TaskAddedEventModel mCallEvent = new TaskAddedEventModel();
////                            TinyBus bus = TinyBus.from(getApplicationContext());
////                            bus.post(mCallEvent);
//                        }
//                    }
                }
            }


//            if (tag.equals("Task")) {
//                if (action.equals("post")) {
//                    String id = payload.getString("id");
//                    String user_id = payload.getString("user_id");
//                    String company_id = payload.getString("company_id");
//                    String workflow_id = payload.getString("workflow_id");
//                    String step_id = payload.getString("step_id");
//                    String lead_id = payload.getString("lead_id");
//                    String name = payload.getString("name");
//                    String description = payload.getString("description");
////                    String type = payload.getString("type");
//                    String status = payload.getString("status");
//                    String created_by = payload.getString("created_by");
//                    String created_at = payload.getString("created_at");
//                    String updated_at = payload.getString("updated_at");
////                    String assigned_at = payload.getString("assigned_at");
////                    String completed_at = payload.getString("completed_at");
////                    String remarks = payload.getString("remarks");
//
//                    // ignore if task already exists
//                    LSTask lsTask = LSTask.getTaskFromServerId(id);
//                    if (lsTask == null) {
//                        // check if lead still exists of which the task is
//                        LSContact lsContact = LSContact.getContactFromServerId(lead_id);
//                        if (lsContact != null) {
//                            LSTask newlsTask = new LSTask();
//                            newlsTask.setServerId(id);
//                            newlsTask.setUserId(user_id);
//                            newlsTask.setCompanyId(company_id);
//                            newlsTask.setWorkflowId(workflow_id);
//                            newlsTask.setStepId(step_id);
//                            newlsTask.setLeadId(lead_id);
//                            newlsTask.setName(name);
//                            newlsTask.setDescription(description);
////                    newlsTask.setType(type);
//                            newlsTask.setStatus(status);
//                            newlsTask.setCreatedBy(created_by);
//                            newlsTask.setCreatedAt(created_at);
//                            Date updated_atDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(updated_at);
//                            newlsTask.setUpdatedAt(updated_atDate);
////                    newlsTask.setAssignedAt(assigned_at);
////                    newlsTask.setCompletedAt(completed_at);
////                    newlsTask.setRemarks(remarks);
//                            newlsTask.save();
//                            TaskAddedEventModel mCallEvent = new TaskAddedEventModel();
//                            TinyBus bus = TinyBus.from(getApplicationContext());
//                            bus.post(mCallEvent);
//                        }
//                    }
//                }
//            }

            Intent pushNotification = new Intent(FireBaseConfig.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", mMsg);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

////             play notification sound
//            FireBaseNotificationUtils notificationUtils = new FireBaseNotificationUtils(getApplicationContext());
//            notificationUtils.playNotificationSound();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }
}
