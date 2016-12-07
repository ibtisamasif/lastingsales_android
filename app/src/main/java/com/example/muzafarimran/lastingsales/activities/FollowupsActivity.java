package com.example.muzafarimran.lastingsales.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.PopUpWindowAddNewFollowUp;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.FollowupsListAdapter;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;

import java.util.ArrayList;

public class FollowupsActivity extends AppCompatActivity {
    static ArrayList<TempFollowUp> allFollowups;
    ListView listView = null;
    FloatingActionButton floatingActionButton;
    PopUpWindowAddNewFollowUp addNewFollowUpPopUp;
    FollowupsListAdapter followupsListAdapter;

    public static ArrayList<TempFollowUp> getAllFollowups() {
        allFollowups = (ArrayList<TempFollowUp>) TempFollowUp.listAll(TempFollowUp.class);
        return allFollowups;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followups);
        listView = (ListView) findViewById(R.id.followups_list_view);
        followupsListAdapter = new FollowupsListAdapter(getApplicationContext(), R.layout.folowup_list_item, getAllFollowups(), this);
        listView.setAdapter(followupsListAdapter);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_add_followup);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addNewFollowUpPopUp = new PopUpWindowAddNewFollowUp(FollowupsActivity.this, followupsListAdapter);
                addNewFollowUpPopUp.displayPopUpWindow();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PopUpWindowAddNewFollowUp.CONTACT_CHOOSER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String returnedResult = data.getData().toString();
                if (addNewFollowUpPopUp != null) {
                    LSContact oneContact = LSContactChooserActivity.getAllContacts().get(Integer.parseInt(returnedResult));
                    addNewFollowUpPopUp.setContactName(oneContact.getContactName());
                    addNewFollowUpPopUp.setContactNumber(oneContact.getPhoneOne());
                    addNewFollowUpPopUp.setSelectedLSContact(oneContact);
                }
            }
        }
    }
}