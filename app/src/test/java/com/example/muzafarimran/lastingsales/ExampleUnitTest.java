package com.example.muzafarimran.lastingsales;

import com.example.muzafarimran.lastingsales.utils.DynamicColumnBuilderVersion2;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        DynamicColumnBuilderVersion2 dynamicColumnBuilderVersion2 = new DynamicColumnBuilderVersion2();
        System.out.println(dynamicColumnBuilderVersion2.buildJSONversion2());
        assertEquals("{\"firstName\":\"ibtisam\",\"lastName\":\"asif\"}", dynamicColumnBuilderVersion2.buildJSONversion2());
    }
}