package com.example.muzafarimran.lastingsales;

import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(PhoneNumberAndCallUtils.getMillisFromSqlFormattedDate("2017-01-02"), PhoneNumberAndCallUtils.getMillisFromSqlFormattedDate("2017-01-02 01:00:00"));
    }
}