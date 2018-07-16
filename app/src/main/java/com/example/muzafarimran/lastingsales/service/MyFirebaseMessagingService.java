package com.example.muzafarimran.lastingsales.service;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.app.FireBaseConfig;
import com.example.muzafarimran.lastingsales.events.CommentEventModel;
import com.example.muzafarimran.lastingsales.events.ContactDeletedEventModel;
import com.example.muzafarimran.lastingsales.events.DealAddedEventModel;
import com.example.muzafarimran.lastingsales.events.InquiryDeletedEventModel;
import com.example.muzafarimran.lastingsales.events.LeadContactAddedEventModel;
import com.example.muzafarimran.lastingsales.events.NoteAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSDeal;
import com.example.muzafarimran.lastingsales.providers.models.LSDynamicColumns;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.providers.models.LSOrganization;
import com.example.muzafarimran.lastingsales.providers.models.LSStage;
import com.example.muzafarimran.lastingsales.providers.models.LSWorkflow;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.FireBaseNotificationUtils;
import com.example.muzafarimran.lastingsales.utils.FirebaseCustomNotification;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;
import com.example.muzafarimran.lastingsales.utils.TypeManager;
import com.example.muzafarimran.lastingsales.utilscallprocessing.DeleteManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by Ravi Tamada on 08/08/16.
 * www.androidhive.info
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private final static AtomicInteger c = new AtomicInteger(0);
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
                        tempContact.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
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
                            mNotificationManager.notify(c.incrementAndGet(), FirebaseCustomNotification.createFirebaseAssignedLeadNotification(getApplicationContext(), name, id));
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
                        contact.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
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
                            mNotificationManager.notify(c.incrementAndGet(), FirebaseCustomNotification.createFirebaseAssignedLeadNotification(getApplicationContext(), name, id));
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
                    contact.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
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
                        mNotificationManager.notify(c.incrementAndGet(), FirebaseCustomNotification.createFirebaseAssignedLeadNotification(getApplicationContext(), name, id));
                    }
                    ContactDeletedEventModel mCallEvent = new ContactDeletedEventModel();
                    TinyBus bus = TinyBus.from(getApplicationContext());
                    bus.post(mCallEvent);

                } else if (action.equals("delete")) {
                    String id = payload.getString("id");
                    LSContact contact = LSContact.getContactFromServerId(id);
                    if (contact != null) {
                        Log.e(TAG, "handleDataMessage: contact: " + contact.toString());
                        DeleteManager.deleteContact(getApplicationContext(), contact);
//                        contact.delete();
                        LeadContactAddedEventModel mCallEvent = new LeadContactAddedEventModel();
                        TinyBus bus = TinyBus.from(getApplicationContext());
                        bus.post(mCallEvent);
                    }
                }
            }

            if (tag.equals("Organization")) {
                if (action.equals("post")) {

                    String id = payload.getString("id");
                    String name = payload.getString("name");
                    String email = null;
                    if (payload.has("email") && !payload.isNull("email")) {
                        email = payload.getString("email");
                    }
                    String phone = null;
                    if (payload.has("phone") && !payload.isNull("phone")) {
                        phone = payload.getString("phone");
                    }
                    String address = null;
                    if (payload.has("address")) {
                        address = payload.getString("address");
                    }
                    String status = payload.getString("status");
                    String dynamic_values = null;
                    if (payload.has("dynamic_values")) {
                        dynamic_values = payload.getString("dynamic_values");
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

                    LSOrganization tempOrganization = LSOrganization.getOrganizationFromServerId(id);
                    if (tempOrganization != null) {
                        tempOrganization.setServerId(id);
                        tempOrganization.setName(name);
                        if (email != null) {
                            tempOrganization.setEmail(email);
                        }
                        if (address != null) {
                            tempOrganization.setAddress(address);
                        }
                        if (phone != null) {
                            tempOrganization.setPhone(phone);
                        }
                        tempOrganization.setUpdatedAt(Calendar.getInstance().getTime());
                        if (dynamic_values != null) {
                            tempOrganization.setDynamicValues(dynamic_values);
                        }
                        tempOrganization.setSyncStatus(SyncStatus.SYNC_STATUS_ORGANIZATION_ADD_SYNCED);
                        tempOrganization.setUpdatedAt(Calendar.getInstance().getTime());
//                        if (created_by != 0) {
//                            tempOrganization.setCreatedBy(created_by);
//                        }
                        if (user_id != 0) {
                            tempOrganization.setUserId(user_id + "");
                        }
                        if (src != null) {
                            tempOrganization.setSrc(src);
                        }
                        tempOrganization.save();
//                        if (src.equals("facebook")) {
//                            Log.d(TAG, "handleDataMessage: Notification Message: Lead from FB: " + name);
//                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                            mNotificationManager.notify(FirebaseCustomNotification.NOTIFICATION_ID, FirebaseCustomNotification.createFirebaseFacebookLeadNotification(getApplicationContext(), name));
//                        }
//                        if (src.equals("assigned")) {
//                            Log.d(TAG, "handleDataMessage: Notification Message: Lead from assigned: " + name);
//                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                            mNotificationManager.notify(c.incrementAndGet(), FirebaseCustomNotification.createFirebaseAssignedLeadNotification(getApplicationContext(), name, id));
//                        }
                    } else {
                        LSOrganization organization = new LSOrganization();
                        organization.setServerId(id);
                        organization.setName(name);
                        if (email != null) {
                            organization.setEmail(email);
                        }
                        if (phone != null) {
                            organization.setPhone(phone);
                        }
                        if (address != null) {
                            organization.setAddress(address);
                        }
                        if (dynamic_values != null) {
                            organization.setDynamicValues(dynamic_values);
                        }
                        organization.setSyncStatus(SyncStatus.SYNC_STATUS_ORGANIZATION_ADD_SYNCED);
                        organization.setUpdatedAt(Calendar.getInstance().getTime());
//                        if (created_by != 0) {
//                            organization.setCreatedBy(created_by);
//                        }
                        if (user_id != 0) {
                            organization.setUserId(user_id + "");
                        }
                        if (src != null) {
                            organization.setSrc(src);
                        }
                        organization.save();
//                        if (src.equals("facebook")) {
//                            Log.d(TAG, "handleDataMessage: Notification Message: Lead from FB: " + name);
//                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                            mNotificationManager.notify(FirebaseCustomNotification.NOTIFICATION_ID, FirebaseCustomNotification.createFirebaseFacebookLeadNotification(getApplicationContext(), name));
//                        }
//                        if (src.equals("assigned")) {
//                            Log.d(TAG, "handleDataMessage: Notification Message: Lead from assigned: " + name);
//                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                            mNotificationManager.notify(c.incrementAndGet(), FirebaseCustomNotification.createFirebaseAssignedLeadNotification(getApplicationContext(), name, id));
//                        }
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
                    String phone = null;
                    if (payload.has("phone")) {
                        phone = payload.getString("phone");
                    }
//                    String intlNum = PhoneNumberAndCallUtils.numberToInterNationalNumber(getApplicationContext(), phone);
                    String address = null;
                    if (payload.has("address")) {
                        address = payload.getString("address");
                    }
//                    String status = payload.getString("status");
//                    String lead_type = payload.getString("lead_type");
//                    String created_at = null;
//                    if(payload.has("created_at")){
//                        created_at = payload.getString("created_at");
//                    }
//                    String updated_at = null;
//                    if (payload.has("updated_at")) {
//                        updated_at = payload.getString("updated_at");
//                    }
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
                    mMsg = name;
                    Log.e(TAG, "handleDataMessageName: " + name);
                    LSOrganization organization = LSOrganization.getOrganizationFromServerId(id);
                    organization.setName(name);
                    organization.setEmail(email);
                    if (phone != null) {
                        organization.setPhone(phone);
                    }
                    organization.setAddress(address);
                    if (dynamic_values != null) {
                        organization.setDynamicValues(dynamic_values);
                    }
                    organization.setUpdatedAt(Calendar.getInstance().getTime());
//                    if (created_by != 0) {
//                        organization.setCreatedBy(created_by);
//                    }
                    if (user_id != 0) {
                        organization.setUserId(user_id + "");
                    }
                    if (src != null) {
                        organization.setSrc(src);
                    }
                    organization.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED);
                    organization.save();
//                    if (src.equals("facebook")) {
//                        Log.d(TAG, "handleDataMessage: Notification Message: Lead from FB: " + name);
//                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                        mNotificationManager.notify(FirebaseCustomNotification.NOTIFICATION_ID, FirebaseCustomNotification.createFirebaseFacebookLeadNotification(getApplicationContext(), name));
//                    }
//                    if (src.equals("assigned")) {
//                        Log.d(TAG, "handleDataMessage: Notification Message: Lead from assigned: " + name);
//                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                        mNotificationManager.notify(c.incrementAndGet(), FirebaseCustomNotification.createFirebaseAssignedLeadNotification(getApplicationContext(), name, id));
//                    }
//                    ContactDeletedEventModel mCallEvent = new ContactDeletedEventModel();
//                    TinyBus bus = TinyBus.from(getApplicationContext());
//                    bus.post(mCallEvent);

                } else if (action.equals("delete")) {
                    String id = payload.getString("id");
                    LSOrganization organization = LSOrganization.getOrganizationFromServerId(id);
                    if (organization != null) {
                        Log.e(TAG, "handleDataMessage: organization: " + organization.toString());
                        DeleteManager.deleteOrganization(getApplicationContext(), organization);
//                        contact.delete();
//                        LeadContactAddedEventModel mCallEvent = new LeadContactAddedEventModel();
//                        TinyBus bus = TinyBus.from(getApplicationContext());
//                        bus.post(mCallEvent);
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
                    note.setCreatedAt(PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(Calendar.getInstance().getTimeInMillis()));
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

            if (tag.equals("Stage")) {
                if (action.equals("post")) {
                    String id = payload.getString("id");
                    int company_id = 0;
                    if (payload.has("company_id")) {
                        company_id = payload.getInt("company_id");
                    }
                    String workflow_id = payload.getString("workflow_id");
                    String name = payload.getString("name");
                    int created_by = 0;
                    if (payload.has("created_by")) {
                        created_by = payload.getInt("created_by");
                    }
                    String created_at = payload.getString("created_at");
                    String updated_at = payload.getString("updated_at");
                    String position = payload.getString("position");
                    LSStage lsStage = LSStage.getStageFromServerId(id);
                    if (lsStage == null) {
                        // check if lead still exists of which the deal is
                        LSWorkflow lsWorkflow = LSWorkflow.getWorkflowFromServerId(workflow_id);
                        if (lsWorkflow != null) {
                            lsStage = new LSStage();
                            lsStage.setServerId(id);
                            lsStage.setCompanyId(Integer.toString(company_id));
                            lsStage.setWorkflowId(workflow_id);
                            lsStage.setName(name);
                            lsStage.setCreatedBy(Integer.toString(created_by));
//                            lsStage.setCreatedAt(created_at);
                            Date updated_atDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(updated_at);
                            lsStage.setUpdatedAt(updated_atDate);
                            lsStage.setWorkflow(lsWorkflow);
                            lsStage.setPosition(position);
                            lsStage.save();

                        }
                    }
                }

                if (action.equals("put")) {
                    String id = payload.getString("id");
                    int company_id = 0;
                    if (payload.has("company_id")) {
                        company_id = payload.getInt("company_id");
                    }
                    String workflow_id = payload.getString("workflow_id");
                    String name = payload.getString("name");
                    int created_by = 0;
                    if (payload.has("created_by")) {
                        created_by = payload.getInt("created_by");
                    }
                    String created_at = payload.getString("created_at");
                    String updated_at = payload.getString("updated_at");
                    String position = payload.getString("position");
                    LSStage lsStage = LSStage.getStageFromServerId(id);
                    if (lsStage != null) {
                        // check if lead still exists of which the deal is
                        LSWorkflow lsWorkflow = LSWorkflow.getWorkflowFromServerId(workflow_id);
                        if (lsWorkflow != null) {
                            lsStage.setServerId(id);
                            lsStage.setCompanyId(Integer.toString(company_id));
                            lsStage.setWorkflowId(workflow_id);
                            lsStage.setName(name);
                            lsStage.setCreatedBy(Integer.toString(created_by));
//                            lsStage.setCreatedAt(created_at);
                            Date updated_atDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(updated_at);
                            lsStage.setUpdatedAt(updated_atDate);
                            lsStage.setWorkflow(lsWorkflow);
                            lsStage.setPosition(position);
                            lsStage.save();

                        }
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
                    int organization_id = 0;
                    if (payload.has("organization_id")) {
                        organization_id = payload.getInt("organization_id");
                    }
                    String name = payload.getString("name");
//                    String status = payload.getString("status");
                    int created_by = 0;
                    if (payload.has("created_by")) {
                        created_by = payload.getInt("created_by");
                    }
//                    String created_at = payload.getString("created_at");
                    String dynamic_values = "";
                    if (payload.has("dynamic_values")) {
                        dynamic_values = payload.getString("dynamic_values");
                    }
                    String is_private = "";
                    if (payload.has("is_private")) {
                        is_private = payload.getString("is_private");
                    }
                    // ignore if task already exists
                    LSDeal lsDeal = LSDeal.getDealFromServerId(id);
                    if (lsDeal == null) {
                        // check if lead or organization still exists of which the deal is
                        LSOrganization lsOrganization = LSOrganization.getOrganizationFromServerId(Integer.toString(organization_id));
                        LSContact lsContact = LSContact.getContactFromServerId(Integer.toString(lead_id));
                        if (lsContact != null || lsOrganization != null) {
                            lsDeal = new LSDeal();
                            lsDeal.setServerId(id);
                            lsDeal.setUserId(Integer.toString(user_id));
                            lsDeal.setCompanyId(Integer.toString(company_id));
                            lsDeal.setWorkflowId(workflow_id);
                            lsDeal.setWorkflowStageId(workflow_stage_id);
                            lsDeal.setName(name);
//                            lsDeal.setStatus(status);
                            lsDeal.setCreatedBy(Integer.toString(created_by));
//                            lsDeal.setCreatedAt(created_at);
                            lsDeal.setUpdatedAt(Calendar.getInstance().getTime());
                            lsDeal.setDynamic(dynamic_values);
                            lsDeal.setIsPrivate(is_private);
                            if (lsContact != null) {
                                lsDeal.setContact(lsContact);
                            }
                            if (lsOrganization != null) {
                                lsDeal.setOrganization(lsOrganization);
                            }
                            lsDeal.setStatus(SyncStatus.SYNC_STATUS_DEAL_ADD_SYNCED);
                            lsDeal.save();
                            TinyBus.from(getApplicationContext()).post(new DealAddedEventModel());
                        }
                    }
                }

                if (action.equals("put")) {
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
                    int organization_id = 0;
                    if (payload.has("organization_id")) {
                        organization_id = payload.getInt("organization_id");
                    }
                    String name = payload.getString("name");
                    String status = payload.getString("status");
                    int created_by = 0;
                    if (payload.has("created_by")) {
                        created_by = payload.getInt("created_by");
                    }
                    String created_at = payload.getString("created_at");
                    String updated_at = payload.getString("updated_at");
                    String dynamic_values = payload.getString("dynamic_values");
                    String is_private = "";
                    if (payload.has("is_private")) {
                        is_private = payload.getString("is_private");
                    }

                    // ignore if task already exists
                    LSDeal lsDeal = LSDeal.getDealFromServerId(id);
                    if (lsDeal != null) {
                        // check if lead or organization still exists of which the deal is
                        LSOrganization lsOrganization = LSOrganization.getOrganizationFromServerId(Integer.toString(organization_id));
                        LSContact lsContact = LSContact.getContactFromServerId(Integer.toString(lead_id));
                        if (lsContact != null || lsOrganization != null) {
                            lsDeal.setServerId(id);
                            lsDeal.setUserId(Integer.toString(user_id));
                            lsDeal.setCompanyId(Integer.toString(company_id));
                            lsDeal.setWorkflowId(workflow_id);
                            lsDeal.setWorkflowStageId(workflow_stage_id);
                            lsDeal.setName(name);
                            lsDeal.setStatus(status);
                            lsDeal.setCreatedBy(Integer.toString(created_by));
                            lsDeal.setCreatedAt(created_at);
                            lsDeal.setUpdatedAt(Calendar.getInstance().getTime());
                            lsDeal.setDynamic(dynamic_values);
                            lsDeal.setIsPrivate(is_private);
                            if (lsContact != null) {
                                lsDeal.setContact(lsContact);
                            }
                            if (lsOrganization != null) {
                                lsDeal.setOrganization(lsOrganization);
                            }
                            lsDeal.setStatus(SyncStatus.SYNC_STATUS_DEAL_UPDATE_SYNCED);
                            lsDeal.save();
                            TinyBus.from(getApplicationContext()).post(new DealAddedEventModel());
                        } else {
                            // may be to aggressive to delete contact
                            lsDeal.delete();
                        }
                    }
                }

                if (action.equals("delete")) {
                    String id = payload.getString("id");
                    // ignore if task already exists
                    LSDeal lsDeal = LSDeal.getDealFromServerId(id);
                    if (lsDeal != null) {
                        lsDeal.delete();
                        TinyBus.from(getApplicationContext()).post(new DealAddedEventModel());
                    }
                }
            }

            if (tag.equals("Comment")) {
                if (action.equals("post")) {
                    String id = payload.getString("id");
                    String lead_id = "";
                    if (payload.has("lead_id")) {
                        lead_id = payload.getString("lead_id");
                    }
                    String comment = payload.getString("comment");
                    int user_id = 0;
                    if (payload.has("user_id")) {
                        user_id = payload.getInt("user_id");
                    }
                    int created_by = 0;
                    if (payload.has("created_by")) {
                        created_by = payload.getInt("created_by");
                    }
                    String created_at = payload.getString("created_at");
                    String updated_at = payload.getString("updated_at");

                    JSONObject objectUser = payload.getJSONObject("user");
                    String firstUsername = objectUser.getString("firstname");
                    String lastUsername = objectUser.getString("lastname");

                    JSONObject objectLead = payload.getJSONObject("lead");
                    String lead_name = objectLead.getString("name");

                    Log.d(TAG, "handleDataMessage: firstname: " + firstUsername);
                    Log.d(TAG, "handleDataMessage: lastname: " + lastUsername);

//                    LSContact lsContact = LSContact.getContactFromServerId(lead_id);
//                    if (lsContact != null) { // Don't show notification is user doesnt have lead.
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(c.incrementAndGet(), FirebaseCustomNotification.createFirebaseCommentNotification(getApplicationContext(), firstUsername + " " + lastUsername, comment, FirebaseCustomNotification.NOTIFICATION_TYPE_COMMENT, lead_id, lead_name));
                    TinyBus.from(getApplicationContext()).post(new CommentEventModel());
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
//                            newlsTask.setStageId(step_id);
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

            if (tag.equals("Custom")) {
                if (action.equals("custom")) {

                    String id = payload.getString("id");
                    String type = payload.getString("type");
                    String title = payload.getString("title");
                    Log.d(TAG, "handleDataMessage: id: " + id);
                    Log.d(TAG, "handleDataMessage: type: " + type);
                    Log.d(TAG, "handleDataMessage: title: " + title);

                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(FirebaseCustomNotification.NOTIFICATION_ID, FirebaseCustomNotification.createFirebaseCustomNotification(getApplicationContext(), title, type, id));
                }
            }

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
