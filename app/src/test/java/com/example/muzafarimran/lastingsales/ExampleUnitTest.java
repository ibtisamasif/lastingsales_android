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
        assertEquals("+92 322 8899906", PhoneNumberAndCallUtils.numberToInterNationalNumber("03228899906"));
    }
}