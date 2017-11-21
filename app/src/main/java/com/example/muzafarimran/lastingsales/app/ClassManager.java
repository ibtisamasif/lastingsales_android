package com.example.muzafarimran.lastingsales.app;

import android.content.Intent;

import com.example.muzafarimran.lastingsales.activities.AboutActivity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by ibtisam on 11/20/2017.
 */

public class ClassManager {

    public static final String ABOUT_ACTIVITY = "about_activity";

    public static Class getClass(String name) {
        HashMap<String, Class> hashMap = new HashMap<>();
        hashMap.put(ABOUT_ACTIVITY, AboutActivity.class);
        return hashMap.get(name);
    }
}
