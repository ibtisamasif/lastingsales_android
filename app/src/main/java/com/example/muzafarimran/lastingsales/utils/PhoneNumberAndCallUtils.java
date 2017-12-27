package com.example.muzafarimran.lastingsales.utils;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by ahmad on 09-Nov-16.
 */

public class PhoneNumberAndCallUtils {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

//    public static String removeLeadingZeroesFromString(String inputString) {
//        String seat;// setNullToString(recElement.getChildText("SEAT"));
//        seat = Long.valueOf(inputString).toString();
//        return seat;
//    }
//
//    public static boolean isValidPassword(final String password) {
//        Pattern pattern;
//        Matcher matcher;
//        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
//        pattern = Pattern.compile(PASSWORD_PATTERN);
//        matcher = pattern.matcher(password);
//        return matcher.matches();
//    }

    @Deprecated
    public static String numberToInterNationalNumber(String inputString) {
        if (inputString != null) {
            PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
            String s = null;
            try {
                Phonenumber.PhoneNumber pkNumberProto = phoneNumberUtil.parse(inputString, "PK");
                s = phoneNumberUtil.format(pkNumberProto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            } catch (NumberParseException e) {
                e.printStackTrace();
            }
            return s;
        } else {
            return null;
        }
    }

    public static String numberToInterNationalNumber(Context context, String inputString) {
        if (inputString != null) {
            PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
            String s = null;
            try {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String countryCodeValue = tm.getNetworkCountryIso();
                Phonenumber.PhoneNumber pkNumberProto = phoneNumberUtil.parse(inputString, countryCodeValue.toUpperCase());
                s = phoneNumberUtil.format(pkNumberProto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            } catch (NumberParseException e) {
                e.printStackTrace();
            }
            return s;
        } else {
            return null;
        }
    }

//    public static String numberToInterNationalE164FormatNumber(String inputString) { // Will review in future
//        String s = PhoneNumberUtils.formatNumberToE164(inputString, "PK");
//        return s;
//    }

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
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+5"));
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static String getDaysAgo(long time, Context ctx) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }
        long now = Calendar.getInstance().getTimeInMillis();
        if (time > now || time <= 0) {
            return null;
        }
        final long diff = now - time;
        if (diff < 24 * HOUR_MILLIS) {
            return " Today ";
        } else if (diff < 48 * HOUR_MILLIS) {
            return " Yesterday ";
        } else {
            return diff / DAY_MILLIS + " Days ago ";
        }
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
        }
//        else if (diff < 48 * HOUR_MILLIS) {
//            return "yesterday";
//        }
        else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

//    public static String getTimeDuration(long time, Context ctx) {
//        if (time < 1000000000000L) {
//            // if timestamp given in seconds, convert to millis
//            time *= 1000;
//        }
//        long now = Calendar.getInstance().getTimeInMillis();
//        if (time > now || time <= 0) {
//            return null;
//        }
//        final long diff = now - time;
//        if (diff < MINUTE_MILLIS) {
//            return diff / 1000 + "s";
//        } else if (diff < 60 * MINUTE_MILLIS) {
//            return (diff / 1000) / 60 + "m";
//        } else if (diff < 90 * MINUTE_MILLIS) {
//            return "an hour ago";
//        } else if (diff < 24 * HOUR_MILLIS) {
//            return diff / HOUR_MILLIS + " hours ago";
//        } else if (diff < 48 * HOUR_MILLIS) {
//            return "yesterday";
//        } else {
//            return diff / DAY_MILLIS + " days ago";
//        }
//    }
//
//    public static String currentDateTime() {
//        Date curDate = new Date();
//        SimpleDateFormat format = new SimpleDateFormat();
//        String DateToStr = format.format(curDate);
//        return DateToStr;
//    }
//
//    public static String generateUniqueFileName(String fname) {
//        String filename = fname;
//        long millis = System.currentTimeMillis();
//        String datetime = new Date().toGMTString();
//        datetime = datetime.replace(" ", "");
//        datetime = datetime.replace(":", "");
//        String rndchars = RandomStringUtils.randomAlphanumeric(16);
//        filename = rndchars + "_" + datetime + "_" + millis;
//        return filename;
//    }

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
//        if (cursor != null && !cursor.isClosed()) {
//            cursor.close();
//        }
        cursor.close();
        return contactName;
    }

    public static void addContactInNativePhonebook(Context context, String name, String number) {
        String DisplayName = name;
        String MobileNumber = number;
        String HomeNumber = "";
        String WorkNumber = "";
        String emailID = "";
        String company = "";
        String jobTitle = "";

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //------------------------------------------------------ Names
        if (DisplayName != null) {
            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            DisplayName).build());
        }

        //------------------------------------------------------ Mobile Number
        if (MobileNumber != null) {
            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }

        //------------------------------------------------------ Home Numbers
        if (HomeNumber != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, HomeNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                    .build());
        }

        //------------------------------------------------------ Work Numbers
        if (WorkNumber != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, WorkNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                    .build());
        }

        //------------------------------------------------------ Email
        if (emailID != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailID)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());
        }

        //------------------------------------------------------ Organization
        if (!company.equals("") && !jobTitle.equals("")) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, jobTitle)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .build());
        }

        // Asking the Contact provider to create a new contact
        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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

    public static long getMillisFromSqlFormattedDateAndTime(String last_call1) {
        long millis1 = 0;
        if (last_call1 != null) {
            String myDate1 = last_call1;
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date1 = sdf1.parse(myDate1);
                millis1 = date1.getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return millis1;
    }

    public static long getMillisFromSqlFormattedDate(String last_call1) {
        long millis1 = 0;
        if (last_call1 != null) {
            String myDate1 = last_call1;
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
//            sdf1.setTimeZone(TimeZone.getTimeZone("GMT+5"));
            try {
                Date date1 = sdf1.parse(myDate1);
                millis1 = date1.getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return millis1;
    }

    public static long getMillisFromSqlFormattedDate(String last_call1, String part) {
        long millis1 = 0;
        if (last_call1 != null) {
            String myDate1 = last_call1;
            SimpleDateFormat sdf1 = new SimpleDateFormat(part);
//            sdf1.setTimeZone(TimeZone.getTimeZone("GMT+5"));
            try {
                Date date1 = sdf1.parse(myDate1);
                millis1 = date1.getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return millis1;
    }


    public static boolean compareDateTimeInMillis(long milliSeconds1, long milliSeconds2) {
        // this function should compare time in milliseconds upto 10 characters ignoring last three characters i.e 1513587790755 and 1513587790000 are equal.
        milliSeconds1 = Long.parseLong(Long.toString(milliSeconds1).substring(0, 10));
        milliSeconds2 = Long.parseLong(Long.toString(milliSeconds2).substring(0, 10));

        if (milliSeconds1 == milliSeconds2) {
            return true;
        }
        return false;
    }
}
