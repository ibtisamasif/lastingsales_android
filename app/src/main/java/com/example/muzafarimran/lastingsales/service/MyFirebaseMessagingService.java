package com.example.muzafarimran.lastingsales.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.activities.MainActivity;
import com.example.muzafarimran.lastingsales.app.FireBaseConfig;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.FireBaseNotificationUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

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

//        Log.e(TAG, "From: " + remoteMessage.getFrom());
//        Log.e(TAG, "From2: " + remoteMessage.getTo());
//        Log.e(TAG, "From3: " + remoteMessage.getNotification());
//        Log.e(TAG, "From4: " + remoteMessage.getData());
//        Log.e(TAG, "From5: " + remoteMessage.getMessageType());
//        Log.e(TAG, "From6: " + remoteMessage.getCollapseKey());


//        if (remoteMessage == null)
//            return;
//
//        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
//            handleNotification(remoteMessage.getNotification().getBody());
//        }
//
//        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

        try {
//                JSONObject json = new JSONObject(remoteMessage.getNotification().getBody().toString());
            JSONObject json = new JSONObject(remoteMessage.getData().toString());
            handleDataMessage(json);
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
//        }
    }

    private void handleNotification(String message) {
        Log.d(TAG, "handleNotification: CHECK 1");
        if (!FireBaseNotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(FireBaseConfig.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            FireBaseNotificationUtils notificationUtils = new FireBaseNotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        } else {
            // If the app is in background, firebase itself handles the notification
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
//                    String email = data.getString("email");  // Json Exception: No value for email
                    String phone = payload.getString("phone");
//                    String address = data.getString("address");
                    String status = payload.getString("status");
                    mMsg = name;
                    Log.e(TAG, "handleDataMessageName: " + name);
                    LSContact contact = new LSContact();
                    contact.setServerId(id);
                    contact.setContactName(name);
//                  contact.setContactEmail(email);
                    contact.setPhoneOne(phone);
//                  contact.setContactAddress(address);
                    contact.setContactType(LSContact.CONTACT_TYPE_SALES);
                    contact.setContactSalesStatus(status);
                    contact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED);
                    contact.save();
                    Log.e(TAG, "Post From Local DB: " + contact.getContactName());
//                  ColleagueContactAddedEventModel mCallEvent = new ColleagueContactAddedEventModel();
//                  TinyBus bus = TinyBus.from(getApplicationContext());
//                  bus.register(mCallEvent);
//                  bus.post(mCallEvent);

                } else if (action.equals("put")) {
                    String id = payload.getString("id");
                    String name = payload.getString("name");
                    String email = payload.getString("email");
                    String phone = payload.getString("phone");
                    String address = payload.getString("address");
                    String status = payload.getString("status");
                    mMsg = name;
                    Log.e(TAG, "handleDataMessageName: " + name);
                    LSContact contact = LSContact.getContactFromServerId(id);
                    contact.setContactName(name);
                    contact.setContactEmail(email);
                    contact.setPhoneOne(phone);
                    contact.setContactAddress(address);
                    contact.setContactSalesStatus(status);
                    contact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED);
                    contact.save();
                    Log.e(TAG, "Put From Local DB: " + contact.getContactName());
                } else if (action.equals("delete")) {
                    //TODO
                    String id = payload.getString("id");
                    LSContact contact = LSContact.getContactFromServerId(id);
                    Log.e(TAG, "handleDataMessage: contact: " + contact.toString());
                    contact.delete();
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
                    tempNote.save();

                } else if (action.equals("put")) {
                    // TODO Notes Update
                    Log.d(TAG, "handleDataMessage: NOTE PUT");
                    String id = payload.getString("id");
                    String description = payload.getString("description");
                    LSNote note = LSNote.getNoteByServerId(id);
                    note.setNoteText(description);
                    note.save();

                } else if (action.equals("delete")) {
                    // TODO Notes Delete
                    Log.d(TAG, "handleDataMessage: NOTE delete");
                    String id = payload.getString("id");
                    LSNote note = LSNote.getNoteByServerId(id);
                    note.delete();
                }
            }


//            JSONObject data = json.getJSONObject("data");
//            String title = data.getString("title");
//            String message = data.getString("message");
//            boolean isBackground = data.getBoolean("is_background");
//            String imageUrl = data.getString("image");
//            String timestamp = data.getString("timestamp");
//            JSONObject payload = data.getJSONObject("payload");
//
//            Log.e(TAG, "title: " + title);
//            Log.e(TAG, "message: " + message);
//            Log.e(TAG, "isBackground: " + isBackground);
//            Log.e(TAG, "payload: " + payload.toString());
//            Log.e(TAG, "imageUrl: " + imageUrl);
//            Log.e(TAG, "timestamp: " + timestamp);

//            if (!FireBaseNotificationUtils.isAppIsInBackground(getApplicationContext())) {
//            Log.d(TAG, "handleNotification: CHECK 2");
//                 app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(FireBaseConfig.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", mMsg);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            FireBaseNotificationUtils notificationUtils = new FireBaseNotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
//            } else {
//            Log.d(TAG, "handleNotification: CHECK 3");
            // app is in background, show the notification in notification tray
            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
            resultIntent.putExtra("message", mMsg);

//                // check for image attachment
//                if (TextUtils.isEmpty(imageUrl)) {
//                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
//                } else {
//                    // image is present, show notification with image
//                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
//                }
//            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new FireBaseNotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new FireBaseNotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
