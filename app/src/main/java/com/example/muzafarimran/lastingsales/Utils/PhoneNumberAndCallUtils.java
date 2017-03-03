package com.example.muzafarimran.lastingsales.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.apache.commons.lang3.RandomStringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ahmad on 09-Nov-16.
 */

public class PhoneNumberAndCallUtils {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String removeLeadingZeroesFromString(String inputString) {
        String seat;// setNullToString(recElement.getChildText("SEAT"));
        seat = Long.valueOf(inputString).toString();
        return seat;
    }

    public static boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static String numberToInterNationalNumber(String inputString) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        String s = null;
        try {
            Phonenumber.PhoneNumber pkNumberProto = phoneNumberUtil.parse(inputString, "PK");
            s = phoneNumberUtil.format(pkNumberProto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static long secondsFromStartAndEndDates(Date startDate, Date endDate) {
        long diffInMs = endDate.getTime() - startDate.getTime();
        long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);
        return diffInSec;
    }

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static String getDateTimeStringFromMiliseconds(long milliSeconds) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat();

//        String dateFormat = "dd/MM/yyyy hh:mm:ss";
//        // Create a DateFormatter object for displaying date in specified format.
//        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static String getDateTimeStringFromMiliseconds(long milliSeconds, String dateFormat) {
//         = "dd/MM/yyyy hh:mm:ss.SSS";
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


    public static String getTimeAgo(long time, Context ctx) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }
        long now = Calendar.getInstance().getTimeInMillis();
        if (time > now || time <= 0) {
            return null;
        }
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static String currentDateTime() {
        Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat();
        String DateToStr = format.format(curDate);
        return DateToStr;
    }

    public static String generateUniqueFileName(String fname) {
        String filename = fname;
        long millis = System.currentTimeMillis();
        String datetime = new Date().toGMTString();
        datetime = datetime.replace(" ", "");
        datetime = datetime.replace(":", "");
        String rndchars = RandomStringUtils.randomAlphanumeric(16);
        filename = rndchars + "_" + datetime + "_" + millis;
        return filename;
    }

    public static String getContactNameFromLocalPhoneBook(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }

    public static void updateAllCallsOfThisContact(LSContact contact) {
        ArrayList<LSCall> allCalls = LSCall.getCallsFromNumber(contact.getPhoneOne());
        if (allCalls != null) {
            for (LSCall oneCall : allCalls) {
                if (oneCall.getContact() == null) {
                    oneCall.setContact(contact);
                }
                oneCall.setContactName(null);
                oneCall.save();
            }
        }
    }
}
