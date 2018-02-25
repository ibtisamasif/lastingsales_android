package com.example.muzafarimran.lastingsales;

import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(LSContact.getContactFromNumber("+923214454445"), LSContact.getContactFromNumber("+923214454445"));
    }
}