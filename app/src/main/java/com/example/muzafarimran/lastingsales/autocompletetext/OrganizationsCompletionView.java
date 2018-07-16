package com.example.muzafarimran.lastingsales.autocompletetext;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.providers.models.LSOrganization;
import com.tokenautocomplete.TokenCompleteTextView;

/**
 * Sample token completion view for basic contact info
 *
 * Created on 9/12/13.
 * @author mgod
 */
public class OrganizationsCompletionView extends TokenCompleteTextView<LSOrganization> {

    InputConnection testAccessibleInputConnection;

    public OrganizationsCompletionView(Context context) {
        super(context);
    }

    public OrganizationsCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OrganizationsCompletionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View getViewForObject(LSOrganization lsOrganization) {
        LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        TokenTextView token = (TokenTextView) l.inflate(R.layout.contact_token, (ViewGroup) getParent(), false);
        token.setText(lsOrganization.getName());
        return token;
    }

    @Override
    protected LSOrganization defaultObject(String completionText) {
//        //Stupid simple example of guessing if we have an email or not
//        int index = completionText.indexOf('@');
//        if (index == -1) {
//            return new LSOrganization(completionText, completionText.replace(" ", "") + "@example.com");
//        } else {
//            return new LSOrganization(completionText.substring(0, index), completionText);
//        }
        return null;
    }

    @Override
    public InputConnection onCreateInputConnection(@NonNull EditorInfo outAttrs) {
        testAccessibleInputConnection = super.onCreateInputConnection(outAttrs);
        return testAccessibleInputConnection;
    }
}
