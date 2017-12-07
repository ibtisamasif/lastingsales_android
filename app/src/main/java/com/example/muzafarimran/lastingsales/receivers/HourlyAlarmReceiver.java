package com.example.muzafarimran.lastingsales.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.NavigationBottomMainActivity;
import com.example.muzafarimran.lastingsales.providers.models.LSContactProfile;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ibtisam on 6/20/2017.
 */

public class HourlyAlarmReceiver extends WakefulBroadcastReceiver {
    public static final String TAG = "myAlarmLog";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive:Hourly Alarm Fired");
        Log.d("Inquiry", "onReceive:Hourly Alarm Fired");

        long countLong = intent.getLongExtra(Intent.EXTRA_ALARM_COUNT, 0L);
        Log.d(TAG, "onReceive: CountLong: " + countLong);

        int count = intent.getIntExtra(Intent.EXTRA_ALARM_COUNT, 0);
        Log.d(TAG, "onReceive: CountInt: " + count);
//        if (count < 2) {
        new sendNotification(context).execute();
//        }
////        Refresh Service once daily.
//        context.startService(new Intent(context, CallDetectionService.class));  // TODO is it still needed here as well ?
    }

    private class sendNotification extends AsyncTask<Void, Void, String> {

        Context context;
        List<LSInquiry> lsInquiry;
        List<String> messageList = new ArrayList<>();


        public sendNotification(Context context) {
            super();
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {

            InputStream in;
            try {
                lsInquiry = LSInquiry.getAllPendingInquiriesInDescendingOrder();
                if (lsInquiry != null && lsInquiry.size() > 0) {
                    List<String> nameFromApp = new ArrayList<>();
                    List<String> nameFromPhonebook = new ArrayList<>();
                    List<String> numberFromApp = new ArrayList<>();
                    List<String> nameOrNumber = new ArrayList<>();
                    List<String> timeAgo = new ArrayList<>();

                    Log.d(TAG, "onReceive: lsinquiry.size(): " + lsInquiry.size());
                    for (int i = 0; i < lsInquiry.size(); i++) {
                        nameFromApp.add(lsInquiry.get(i).getContactName());
                        nameFromPhonebook.add(PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(context, lsInquiry.get(i).getContactNumber()));
                        numberFromApp.add(lsInquiry.get(i).getContactNumber());
                        if (nameFromApp.get(i) != null) {
                            nameOrNumber.add(nameFromApp.get(i));
                        } else if (nameFromPhonebook.get(i) != null) {
                            nameOrNumber.add(nameFromPhonebook.get(i));
                        } else {
                            nameOrNumber.add(numberFromApp.get(i));
                        }
                        messageList.add(nameOrNumber.get(i));
                    }
                    if (lsInquiry.size() == 1) {
                        LSContactProfile lsContactProfile = LSContactProfile.getProfileFromNumber(numberFromApp.get(0));
                        if (lsContactProfile != null) {
                            if (lsContactProfile.getSocial_image() != null) {
                                return lsContactProfile.getSocial_image();
                            }
                        }
//                        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                        mNotificationManager.notify(HourlyAlarmNotification.NOTIFICATION_ID, HourlyAlarmNotification.createHourlyAlarmNotificationForSingle(context, "Lets Callback", messageList.get(0), numberFromApp.get(0)));

                    } else if (lsInquiry.size() > 1) {
                        String numberOfContactHavingImage = "";
                        for (int i = 0; i < lsInquiry.size(); i++) {
                            if (lsInquiry.get(i).getContactProfile() != null)
                                if (lsInquiry.get(i).getContactProfile().getSocial_image() != null && !lsInquiry.get(i).getContactProfile().getSocial_image().equals("")) {
                                    numberOfContactHavingImage = lsInquiry.get(i).getContactNumber();
                                    break;
                                }
                        }
                        if (numberOfContactHavingImage != null && !numberOfContactHavingImage.equals("")) {
                            LSContactProfile lsContactProfile = LSContactProfile.getProfileFromNumber(numberOfContactHavingImage);
                            if (lsContactProfile != null) {
                                if (lsContactProfile.getSocial_image() != null && !lsContactProfile.getSocial_image().equals("")) {
                                    return lsContactProfile.getSocial_image();
                                }
                            }
                        }
                    }
                }
                Log.d(TAG, "doInBackground: message: " + messageList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "onPostExecute: SocialImageLink: " + result);
            if (lsInquiry != null && lsInquiry.size() > 0) {
                if (lsInquiry.size() == 1) {
                    Log.d(TAG, "onPostExecute: lsInquiry.size() == 1");
                    if (result != null) {
                        Glide.with(context).load(result).asBitmap().into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                                Intent inquiriesIntent = new Intent(context, NavigationBottomMainActivity.class);
                                inquiriesIntent.putExtra(NavigationBottomMainActivity.KEY_SELECTED_TAB, NavigationBottomMainActivity.INQUIRIES_TAB);
                                PendingIntent pContentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), inquiriesIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel: " + lsInquiry.get(0).getContactNumber()));
                                PendingIntent pCallBackIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), callIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                Notification.Builder notificationBuilder = new Notification.Builder(context)
                                        .setContentIntent(pContentIntent)
                                        .addAction(R.drawable.call_icon, "Call Back", pCallBackIntent)
                                        .addAction(R.drawable.call_icon, "Others", pContentIntent)
                                        .setSmallIcon(R.drawable.ic_notification_small)
                                        .setPriority(Notification.PRIORITY_MAX)
                                        .setLargeIcon(resource)
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notification_small))
                                        .setWhen(System.currentTimeMillis())
                                        .setContentTitle("Lets Callback")
                                        .setTicker("Missed Inquiries")
                                        .setStyle(new Notification.BigTextStyle().bigText(messageList.get(0)))
                                        .setAutoCancel(true)
                                        .setDefaults(Notification.DEFAULT_SOUND)
                                        .setDefaults(Notification.DEFAULT_VIBRATE)
                                        .setContentText(messageList.get(0));

                                notificationBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
                                notificationManager.notify(0, notificationBuilder.build());
                            }
                        });
                    } else {
                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                        Intent inquiriesIntent = new Intent(context, NavigationBottomMainActivity.class);
                        inquiriesIntent.putExtra(NavigationBottomMainActivity.KEY_SELECTED_TAB, NavigationBottomMainActivity.INQUIRIES_TAB);
                        PendingIntent pContentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), inquiriesIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel: " + lsInquiry.get(0).getContactNumber()));
                        PendingIntent pCallBackIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), callIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        Notification.Builder notificationBuilder = new Notification.Builder(context)
                                .setContentIntent(pContentIntent)
                                .addAction(R.drawable.call_icon, "Call Back", pCallBackIntent)
                                .addAction(R.drawable.call_icon, "Others", pContentIntent)
                                .setSmallIcon(R.drawable.ic_notification_small)
                                .setPriority(Notification.PRIORITY_MAX)
//                                .setLargeIcon(resource)
                                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notification_small))
                                .setWhen(System.currentTimeMillis())
                                .setContentTitle("Lets Callback")
                                .setTicker("Missed Inquiries")
                                .setStyle(new Notification.BigTextStyle().bigText(messageList.get(0)))
                                .setAutoCancel(true)
                                .setDefaults(Notification.DEFAULT_SOUND)
                                .setDefaults(Notification.DEFAULT_VIBRATE)
                                .setContentText(messageList.get(0));

                        notificationBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
                        notificationManager.notify(0, notificationBuilder.build());
                    }

                } else if (lsInquiry.size() > 1) {
                    Log.d(TAG, "onPostExecute: lsInquiry.size() > 1");
                    if (result != null) {
                        Glide.with(context).load(result).asBitmap().into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                Intent inquiriesIntent = new Intent(context, NavigationBottomMainActivity.class);
                                inquiriesIntent.putExtra(NavigationBottomMainActivity.KEY_SELECTED_TAB, NavigationBottomMainActivity.INQUIRIES_TAB);
                                PendingIntent pContentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), inquiriesIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                                        .setContentIntent(pContentIntent)
                                        .setSmallIcon(R.drawable.ic_notification_small)
                                        .setPriority(Notification.PRIORITY_MAX)
                                        .setLargeIcon(resource)
                                        .setWhen(System.currentTimeMillis())
                                        .setContentTitle("Lets Callback")
                                        .setContentText(messageList.get(0) + " and others")
                                        .setTicker("Missed Inquiries")
                                        .setAutoCancel(true)
                                        .setDefaults(Notification.DEFAULT_SOUND)
                                        .setDefaults(Notification.DEFAULT_VIBRATE);

                                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                                inboxStyle.setBigContentTitle("Lets Callback");
                                inboxStyle.setBuilder(notificationBuilder);
                                int count = 0;
                                for (String oneMessage : messageList) {
                                    inboxStyle.addLine(oneMessage);
                                    if (messageList.size() > 5 && count == 4) {
                                        inboxStyle.setSummaryText("+" + (messageList.size() - 5) + " more");
                                        break;
                                    }
                                    count++;
                                }
                                notificationBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
                                notificationManager.notify(0, notificationBuilder.build());
                            }
                        });
                    } else {
                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        Intent inquiriesIntent = new Intent(context, NavigationBottomMainActivity.class);
                        inquiriesIntent.putExtra(NavigationBottomMainActivity.KEY_SELECTED_TAB, NavigationBottomMainActivity.INQUIRIES_TAB);
                        PendingIntent pContentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), inquiriesIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                                .setContentIntent(pContentIntent)
                                .setSmallIcon(R.drawable.ic_notification_small)
                                .setPriority(Notification.PRIORITY_MAX)
//                                .setLargeIcon(resource)
                                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notification_small))
                                .setWhen(System.currentTimeMillis())
                                .setContentTitle("Lets Callback")
                                .setContentText(messageList.get(0) + " and others")
                                .setTicker("Missed Inquiries")
                                .setAutoCancel(true)
                                .setDefaults(Notification.DEFAULT_SOUND)
                                .setDefaults(Notification.DEFAULT_VIBRATE);

                        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                        inboxStyle.setBigContentTitle("Lets Callback");
                        inboxStyle.setBuilder(notificationBuilder);
                        int count = 0;
                        for (String oneMessage : messageList) {
                            inboxStyle.addLine(oneMessage);
                            if (messageList.size() > 5 && count == 4) {
                                inboxStyle.setSummaryText("+" + (messageList.size() - 5) + " more");
                                break;
                            }
                            count++;
                        }
                        notificationBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
                        notificationManager.notify(0, notificationBuilder.build());

                    }
                }
            }
        }
    }
}

