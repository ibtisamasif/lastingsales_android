//package com.example.muzafarimran.lastingsales.receivers;
//
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.support.v4.app.NotificationCompat;
//import android.support.v4.content.WakefulBroadcastReceiver;
//import android.util.Log;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.bumptech.glide.request.animation.GlideAnimation;
//import com.bumptech.glide.request.target.SimpleTarget;
//import com.example.muzafarimran.lastingsales.R;
//import com.example.muzafarimran.lastingsales.activities.NavigationBottomMainActivity;
//import com.example.muzafarimran.lastingsales.providers.models.LSContactProfile;
//import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
//import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;
//
//import java.io.InputStream;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
///**
// * Created by ibtisam on 6/20/2017.
// */
//
//public class HourlyAlarmReceiver extends WakefulBroadcastReceiver {
//    public static final String TAG = "myAlarmLog";
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        Log.d(TAG, "onReceive:Hourly Alarm Fired");
//        Log.d("Inquiry", "onReceive:Hourly Alarm Fired");
//        if (isDay()) {
//            Log.d(TAG, "onReceive: DAY TIME");
////        long countLong = intent.getLongExtra(Intent.EXTRA_ALARM_COUNT, 0L);
////        Log.d(TAG, "onReceive: CountLong: " + countLong);
////
////        int count = intent.getIntExtra(Intent.EXTRA_ALARM_COUNT, 0);
////        Log.d(TAG, "onReceive: CountInt: " + count);
////        if (count < 2) {
//            new sendNotification(context).execute();
////        }
//        }else {
//            Log.d(TAG, "onReceive: NIGHT TIME");
//        }
//
//////        Refresh Service once daily.
////        context.startService(new Intent(context, CallDetectionService.class));  // is it still needed here as well ?
//    }
//
//    private class sendNotification extends AsyncTask<Void, Void, String> {
//
//        Context context;
//        List<LSInquiry> lsInquiry;
//        List<String> messageList = new ArrayList<>();
//
//
//        public sendNotification(Context context) {
//            super();
//            this.context = context;
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//
//            InputStream in;
//            try {
//                lsInquiry = LSInquiry.getAllPendingInquiriesInDescendingOrder();
//                if (lsInquiry != null && lsInquiry.size() > 0) {
//                    List<String> nameFromApp = new ArrayList<>();
//                    List<String> nameFromPhonebook = new ArrayList<>();
//                    List<String> numberFromApp = new ArrayList<>();
//                    List<String> nameOrNumber = new ArrayList<>();
//                    List<String> timeAgo = new ArrayList<>();
//
//                    Log.d(TAG, "onReceive: lsinquiry.size(): " + lsInquiry.size());
//                    for (int i = 0; i < lsInquiry.size(); i++) {
//                        nameFromApp.add(lsInquiry.get(i).getContactName());
//                        nameFromPhonebook.add(PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(context, lsInquiry.get(i).getContactNumber()));
//                        numberFromApp.add(lsInquiry.get(i).getContactNumber());
//                        if (nameFromApp.get(i) != null) {
//                            nameOrNumber.add(nameFromApp.get(i));
//                        } else if (nameFromPhonebook.get(i) != null) {
//                            nameOrNumber.add(nameFromPhonebook.get(i));
//                        } else {
//                            nameOrNumber.add(numberFromApp.get(i));
//                        }
//                        messageList.add(nameOrNumber.get(i));
//                    }
//                    if (lsInquiry.size() == 1) {
//                        LSContactProfile lsContactProfile = LSContactProfile.getProfileFromNumber(numberFromApp.get(0));
//                        if (lsContactProfile != null) {
//                            if (lsContactProfile.getSocial_image() != null) {
//                                return lsContactProfile.getSocial_image();
//                            }
//                        }
////                        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
////                        mNotificationManager.notify(HourlyAlarmNotification.NOTIFICATION_ID, HourlyAlarmNotification.createHourlyAlarmNotificationForSingle(context, "Lets Callback", messageList.get(0), numberFromApp.get(0)));
//
//                    } else if (lsInquiry.size() > 1) {
//                        String numberOfContactHavingImage = "";
//                        for (int i = 0; i < lsInquiry.size(); i++) {
//                            if (lsInquiry.get(i).getContactProfile() != null)
//                                if (lsInquiry.get(i).getContactProfile().getSocial_image() != null && !lsInquiry.get(i).getContactProfile().getSocial_image().equals("")) {
//                                    numberOfContactHavingImage = lsInquiry.get(i).getContactNumber();
//                                    break;
//                                }
//                        }
//                        if (numberOfContactHavingImage != null && !numberOfContactHavingImage.equals("")) {
//                            LSContactProfile lsContactProfile = LSContactProfile.getProfileFromNumber(numberOfContactHavingImage);
//                            if (lsContactProfile != null) {
//                                if (lsContactProfile.getSocial_image() != null && !lsContactProfile.getSocial_image().equals("")) {
//                                    return lsContactProfile.getSocial_image();
//                                }
//                            }
//                        }
//                    }
//                    Log.d(TAG, "doInBackground: message: " + messageList);
//                } else {
//                    Log.d(TAG, "doInBackground: NO INQUIRY TO DISPLAY CALLBACK NOTIFICATION");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            if (lsInquiry != null && lsInquiry.size() > 0) {
//                if (lsInquiry.size() == 1) {
//                    Log.d(TAG, "onPostExecute: lsInquiry.size() == 1");
//                    if (result != null) {
//                        Log.d(TAG, "onPostExecute: 1");
//                        Log.d(TAG, "onPostExecute: SocialImageLink: " + result);
//                        Glide.with(context).load(result).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
//                            @Override
//                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                                Intent inquiriesIntent = new Intent(context, NavigationBottomMainActivity.class);
//                                inquiriesIntent.putExtra(NavigationBottomMainActivity.KEY_SELECTED_TAB, NavigationBottomMainActivity.INQUIRIES_TAB);
//                                PendingIntent pContentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), inquiriesIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                                Intent callIntent = new Intent(Intent.ACTION_CALL);
//                                callIntent.setData(Uri.parse("tel: " + lsInquiry.get(0).getContactNumber()));
//                                PendingIntent pCallBackIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), callIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                                Notification.Builder notificationBuilder = new Notification.Builder(context)
//                                        .setContentIntent(pContentIntent)
//                                        .addAction(R.drawable.ic_call_png, "Call Back", pCallBackIntent)
//                                        .addAction(R.drawable.ic_call_png, "Others", pContentIntent)
//                                        .setSmallIcon(R.drawable.ic_notification_small)
//                                        .setPriority(Notification.PRIORITY_MAX)
//                                        .setLargeIcon(resource)
////                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notification_small))
//                                        .setWhen(System.currentTimeMillis())
//                                        .setContentTitle("Lets Callback")
//                                        .setTicker("Missed Inquiries")
//                                        .setStyle(new Notification.BigTextStyle().bigText(messageList.get(0)))
//                                        .setAutoCancel(true)
//                                        .setDefaults(Notification.DEFAULT_SOUND)
//                                        .setContentText(messageList.get(0));
//
//                                notificationBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
//                                notificationManager.notify(0, notificationBuilder.build());
//                            }
//                        });
//                    } else {
//                        Log.d(TAG, "onPostExecute: 2");
//                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                        Intent inquiriesIntent = new Intent(context, NavigationBottomMainActivity.class);
//                        inquiriesIntent.putExtra(NavigationBottomMainActivity.KEY_SELECTED_TAB, NavigationBottomMainActivity.INQUIRIES_TAB);
//                        PendingIntent pContentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), inquiriesIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                        Intent callIntent = new Intent(Intent.ACTION_CALL);
//                        callIntent.setData(Uri.parse("tel: " + lsInquiry.get(0).getContactNumber()));
//                        PendingIntent pCallBackIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), callIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                        Notification.Builder notificationBuilder = new Notification.Builder(context)
//                                .setContentIntent(pContentIntent)
//                                .addAction(R.drawable.ic_call_png, "Call Back", pCallBackIntent)
//                                .addAction(R.drawable.ic_call_png, "Others", pContentIntent)
//                                .setSmallIcon(R.drawable.ic_notification_small)
//                                .setPriority(Notification.PRIORITY_MAX)
////                                .setLargeIcon(resource)
//                                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notification_small))
//                                .setWhen(System.currentTimeMillis())
//                                .setContentTitle("Lets Callback")
//                                .setTicker("Missed Inquiries")
//                                .setStyle(new Notification.BigTextStyle().bigText(messageList.get(0)))
//                                .setAutoCancel(true)
//                                .setDefaults(Notification.DEFAULT_SOUND)
//                                .setContentText(messageList.get(0));
//
//                        notificationBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
//                        notificationManager.notify(0, notificationBuilder.build());
//                    }
//
//                } else if (lsInquiry.size() > 1) {
//                    Log.d(TAG, "onPostExecute: lsInquiry.size() > 1");
//                    if (result != null) {
//                        Log.d(TAG, "onPostExecute: 3");
//                        Log.d(TAG, "onPostExecute: SocialImageLink: " + result);
//                        Glide.with(context).load(result).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
//                            @Override
//                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                                Intent inquiriesIntent = new Intent(context, NavigationBottomMainActivity.class);
//                                inquiriesIntent.putExtra(NavigationBottomMainActivity.KEY_SELECTED_TAB, NavigationBottomMainActivity.INQUIRIES_TAB);
//                                PendingIntent pContentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), inquiriesIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
//                                        .setContentIntent(pContentIntent)
//                                        .setSmallIcon(R.drawable.ic_notification_small)
//                                        .setPriority(Notification.PRIORITY_MAX)
//                                        .setLargeIcon(resource)
//                                        .setWhen(System.currentTimeMillis())
//                                        .setContentTitle("Lets Callback")
//                                        .setContentText(messageList.get(0) + " and others")
//                                        .setTicker("Missed Inquiries")
//                                        .setAutoCancel(true)
//                                        .setDefaults(Notification.DEFAULT_SOUND);
//
//                                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
//                                inboxStyle.setBigContentTitle("Lets Callback");
//                                inboxStyle.setBuilder(notificationBuilder);
//                                int count = 0;
//                                for (String oneMessage : messageList) {
//                                    inboxStyle.addLine(oneMessage);
//                                    if (messageList.size() > 5 && count == 4) {
//                                        inboxStyle.setSummaryText("+" + (messageList.size() - 5) + " more");
//                                        break;
//                                    }
//                                    count++;
//                                }
//                                notificationBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
//                                notificationManager.notify(0, notificationBuilder.build());
//                            }
//                        });
//                    } else {
//                        Log.d(TAG, "onPostExecute: 4");
//                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                        Intent inquiriesIntent = new Intent(context, NavigationBottomMainActivity.class);
//                        inquiriesIntent.putExtra(NavigationBottomMainActivity.KEY_SELECTED_TAB, NavigationBottomMainActivity.INQUIRIES_TAB);
//                        PendingIntent pContentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), inquiriesIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
//                                .setContentIntent(pContentIntent)
//                                .setSmallIcon(R.drawable.ic_notification_small)
//                                .setPriority(Notification.PRIORITY_MAX)
////                                .setLargeIcon(resource)
//                                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notification_small))
//                                .setWhen(System.currentTimeMillis())
//                                .setContentTitle("Lets Callback")
//                                .setContentText(messageList.get(0) + " and others")
//                                .setTicker("Missed Inquiries")
//                                .setAutoCancel(true)
//                                .setDefaults(Notification.DEFAULT_SOUND);
//
//                        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
//                        inboxStyle.setBigContentTitle("Lets Callback");
//                        inboxStyle.setBuilder(notificationBuilder);
//                        int count = 0;
//                        for (String oneMessage : messageList) {
//                            inboxStyle.addLine(oneMessage);
//                            if (messageList.size() > 5 && count == 4) {
//                                inboxStyle.setSummaryText("+" + (messageList.size() - 5) + " more");
//                                break;
//                            }
//                            count++;
//                        }
//                        notificationBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
//                        notificationManager.notify(0, notificationBuilder.build());
//
//                    }
//                }
//            }
//        }
//    }
//
//    public boolean isDay() {
//        try {
//            boolean flag = true;
//            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
//            Date date_from = null;
//
//            date_from = formatter.parse("22:00");
//            Date date_to = formatter.parse("09:00");
//            Date dateNow = formatter.parse(formatter.format(new Date()));
//
//            System.out.println("date_to: " + date_from);
//            System.out.println("date_to: " + date_to);
//            System.out.println("dateNow: " + dateNow);
//
//            String check;
//            if (date_from.before(date_to)) { // they are on the same day
//                if (dateNow.after(date_from) && dateNow.before(date_to)) {
//                    check = "nightCycle";
//                    flag = false;
//                } else {
//                    check = "dayCycle";
//                    flag = true;
//                }
//            } else { // interval crossing midnight
//                if (dateNow.before(date_to) || dateNow.after(date_from)) {
//                    check = "nightCycle";
//                    flag = false;
//                } else {
//                    check = "dayCycle";
//                    flag = true;
//                }
//            }
//            System.out.println("Cycle is " + check);
//            return flag;
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//}
//
