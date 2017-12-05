package com.example.muzafarimran.lastingsales.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.ContactDetailsFragmentPagerAdapter;
import com.example.muzafarimran.lastingsales.events.BackPressedEventModel;
import com.example.muzafarimran.lastingsales.events.ContactDeletedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;

import java.util.List;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 12/29/2016.
 */

public class ContactDetailsTabActivity extends AppCompatActivity {
    public static final String TAG = "ContactDetailsTab";
    public static final String KEY_CONTACT_ID = "contact_id";
    public static final String KEY_SET_SELECTED_TAB = "key_set_selected_tab";

    ViewPager viewPager;
    FloatingActionButton floatingActionButton;
    private String contactIdString = "0";
    private String selectedTab = "";
    private LSContact selectedContact;
    private TinyBus bus;
    Toolbar toolbar;
    ActionBar actionBar;
//    private CollapsingToolbarLayout collapsingToolbarLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details_tab);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        Long contactIDLong;
        if (extras != null) {
            contactIdString = extras.getString(ContactDetailsTabActivity.KEY_CONTACT_ID);
            if (contactIdString != null) {
                contactIDLong = Long.parseLong(contactIdString);
                selectedContact = LSContact.findById(LSContact.class, contactIDLong);
            }
        }
        if (selectedContact != null) {
            if (selectedContact.getContactName() == null || selectedContact.getContactName().equals("")) {
//                tvName.setVisibility(View.GONE);
            } else {
                toolbar.setTitle(selectedContact.getContactName());
                setSupportActionBar(toolbar);
//                collapsingToolbarLayout.setTitle(selectedContact.getContactName());
            }
            if (selectedContact.getPhoneOne() == null || selectedContact.getPhoneOne().equals("")) {
//                tvNumberOne.setVisibility(View.GONE);
            } else {
//                tvNumberOne.setText(selectedContact.getPhoneOne());
            }
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new ContactDetailsFragmentPagerAdapter(getSupportFragmentManager(), selectedContact.getId(), selectedContact.getPhoneOne()));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        floatingActionButton.hide();
//                        tab.setIcon(R.drawable.menu_icon_details_selected);
                        break;
                    case 1:
                        floatingActionButton.show();
//                        tab.setIcon(R.drawable.menu_icon_phone_selected);
                        break;
                    case 2:
                        floatingActionButton.hide();
//                        tab.setIcon(R.drawable.menu_icon_contact_selected);
                        break;
                    case 3:
                        floatingActionButton.hide();
//                        tab.setIcon(R.drawable.add_contact_notes_field_icon);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
//                        tab.setIcon(R.drawable.menu_icon_details);
                        break;
                    case 1:
//                        tab.setIcon(R.drawable.menu_icon_phone);
                        break;
                    case 2:
//                        tab.setIcon(R.drawable.menu_icon_contact);
                        break;
                    case 3:
//                        tab.setIcon(R.drawable.add_contact_notes_field_icon_unselected);
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_add_note);
        floatingActionButton.hide();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), AddEditNoteActivity.class);
                intent.putExtra(AddEditNoteActivity.ACTIVITY_LAUNCH_MODE, AddEditNoteActivity.LAUNCH_MODE_ADD_NEW_NOTE);
                intent.putExtra(AddEditNoteActivity.TAG_LAUNCH_MODE_CONTACT_NUMBER, selectedContact.getPhoneOne());
                startActivity(intent);
            }
        });

        if (extras != null) {
            selectedTab = extras.getString(ContactDetailsTabActivity.KEY_SET_SELECTED_TAB);
            if (selectedTab != null && selectedTab != "") {
                viewPager.setCurrentItem(1, true);
            }
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
//        BackPressedEventModel model = new BackPressedEventModel();
//        TinyBus.from(getApplicationContext()).post(model);
//        if (!model.backPressHandled) {
//            super.onBackPressed();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_action_delete:


                String nameTextOnDialog;
                if (selectedContact.getContactName() != null) {
                    nameTextOnDialog = selectedContact.getContactName();
                } else {
                    nameTextOnDialog = selectedContact.getPhoneOne();
                }
                AlertDialog.Builder alert = new AlertDialog.Builder(ContactDetailsTabActivity.this);
                alert.setTitle("Delete");
                alert.setMessage("Are you sure to delete " + nameTextOnDialog);
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LSInquiry checkInquiry = LSInquiry.getInquiryByNumberIfExists(selectedContact.getPhoneOne());
                        if (checkInquiry != null) {
                            checkInquiry.delete();
                        }
//                    if (checkInquiry == null) {
                        //Flushing Notes Of lead
                        List<LSNote> allNotesOfThisContact = LSNote.getNotesByContactId(selectedContact.getId());
                        if (allNotesOfThisContact != null && allNotesOfThisContact.size() > 0) {
                            for (LSNote oneNote : allNotesOfThisContact) {
                                oneNote.delete();
                            }
                        }
                        //Flushing Followup Of lead
                        List<TempFollowUp> allFollowupsOfThisContact = TempFollowUp.getFollowupsByContactId(selectedContact.getId());
                        if (allFollowupsOfThisContact != null && allFollowupsOfThisContact.size() > 0) {
                            for (TempFollowUp oneFollowup : allFollowupsOfThisContact) {
                                oneFollowup.delete();
                            }
                        }
                        //contact is deleted and will be hard deleted on syncing.
                        selectedContact.setLeadDeleted(true);
                        selectedContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_DELETE_NOT_SYNCED);
                        selectedContact.save();
//                    selectedContact.delete();
                        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(ContactDetailsTabActivity.this);
                        dataSenderAsync.run();
                        // FIRE EVENT TO REFRESH LIST
                        Snackbar.make(toolbar, "Lead deleted!", Snackbar.LENGTH_SHORT).show();
//                    }else {
//                        Toast.makeText(mContext, "Please Handle Inquiry First", Toast.LENGTH_SHORT).show();
//                    }
                        ContactDeletedEventModel mCallEvent = new ContactDeletedEventModel();
                        TinyBus bus = TinyBus.from(ContactDetailsTabActivity.this);
                        bus.post(mCallEvent);
                        dialog.dismiss();
                        finish();
                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
                break;

            case R.id.ic_action_edit:
                Intent addContactScreenIntent = new Intent(getApplicationContext(), AddEditLeadActivity.class);
                addContactScreenIntent.putExtra(AddEditLeadActivity.ACTIVITY_LAUNCH_MODE, AddEditLeadActivity.LAUNCH_MODE_EDIT_EXISTING_CONTACT);
                addContactScreenIntent.putExtra(AddEditLeadActivity.TAG_LAUNCH_MODE_CONTACT_ID, contactIdString);
                startActivity(addContactScreenIntent);
                break;

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bus = TinyBus.from(this.getApplicationContext());
        bus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Bundle extras = getIntent().getExtras();
        Long contactIDLong;
        if (extras != null) {
            contactIdString = extras.getString(ContactDetailsTabActivity.KEY_CONTACT_ID);
            if (contactIdString != null) {
                contactIDLong = Long.parseLong(contactIdString);
                selectedContact = LSContact.findById(LSContact.class, contactIDLong);
            }
        }
        toolbar.setTitle(selectedContact.getContactName());
        setSupportActionBar(toolbar);
    }
}
