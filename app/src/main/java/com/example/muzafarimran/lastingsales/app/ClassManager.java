package com.example.muzafarimran.lastingsales.app;

import android.content.Intent;

import com.example.muzafarimran.lastingsales.activities.AboutActivity;
import com.example.muzafarimran.lastingsales.activities.ContactDetailsTabActivity;
import com.example.muzafarimran.lastingsales.fragments.ContactCallDetailsBottomSheetFragmentNew;
import com.example.muzafarimran.lastingsales.fragments.InquiryCallDetailsBottomSheetFragmentNew;

import java.util.HashMap;

/**
 * Created by ibtisam on 11/20/2017.
 */

public class ClassManager {

    public static final String ABOUT_ACTIVITY = "about_activity";
    public static final String CONTACT_DETAILS_TAB_ACTIVITY = "contact_details_tab_activity";
    public static final String CONTACT_CALL_DETAILS_BOTTOM_SHEET_FRAGMENT = "contact_call_details_bottom_sheet_fragment";
    public static final String INQUIRY_CALL_DETAILS_BOTTOM_SHEET_FRAGMENT = "inquiry_call_details_bottom_sheet_fragment";

    public static Class getClass(String name) {
        HashMap<String, Class> hashMap = new HashMap<>();
        hashMap.put(ABOUT_ACTIVITY, AboutActivity.class);
        hashMap.put(CONTACT_DETAILS_TAB_ACTIVITY, ContactDetailsTabActivity.class);
        hashMap.put(CONTACT_CALL_DETAILS_BOTTOM_SHEET_FRAGMENT, ContactCallDetailsBottomSheetFragmentNew.class);
        hashMap.put(INQUIRY_CALL_DETAILS_BOTTOM_SHEET_FRAGMENT, InquiryCallDetailsBottomSheetFragmentNew.class);
        return hashMap.get(name);
    }
}
