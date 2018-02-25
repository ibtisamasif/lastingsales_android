package com.example.muzafarimran.lastingsales;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import junit.framework.TestResult;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    @Override
    public TestResult run() {
        assertEquals(LSContact.getContactFromNumber("+923214454445"), LSContact.getContactFromNumber("+923214454445"));
        return super.run();
    }
}