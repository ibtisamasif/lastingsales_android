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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.ContactDetailsFragmentPagerAdapter;
import com.example.muzafarimran.lastingsales.events.InquiryDeletedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.utilscallprocessing.DeleteManager;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 12/29/2016.
 */

public class ContactDetailsTabActivity extends AppCompatActivity {
    public static final String TAG = "ContactDetailsTab";
    public static final String KEY_SOURCE = "key_source";
    public static final String KEY_SOURCE_NOTIFICATION = "key_source_notification";
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
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_contact_details_tab);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_add);
        floatingActionButton.hide();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddEditNoteActivity.class);
                intent.putExtra(AddEditNoteActivity.ACTIVITY_LAUNCH_MODE, AddEditNoteActivity.LAUNCH_MODE_ADD_NEW_NOTE);
                intent.putExtra(AddEditNoteActivity.TAG_LAUNCH_MODE_CONTACT_ID, selectedContact.getId());
                startActivity(intent);
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            contactIdString = extras.getString(ContactDetailsTabActivity.KEY_CONTACT_ID);
            if (contactIdString != null) {
                Long contactIDLong = Long.parseLong(contactIdString);
                selectedContact = LSContact.findById(LSContact.class, contactIDLong);
            }
        }
        if (selectedContact != null) {
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            ContactDetailsFragmentPagerAdapter contactDetailsFragmentPagerAdapter = new ContactDetailsFragmentPagerAdapter(getSupportFragmentManager(), selectedContact.getId());
            viewPager.setAdapter(contactDetailsFragmentPagerAdapter); //TODO crash getId was null
//        viewPager.getAdapter().notifyDataSetChanged();

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

            if (extras != null) {
                selectedTab = extras.getString(ContactDetailsTabActivity.KEY_SET_SELECTED_TAB);
                if (selectedTab != null && !selectedTab.equals("")) {
                    if (selectedTab.equals("3")) {
                        viewPager.setCurrentItem(3, true);
                        contactDetailsFragmentPagerAdapter.getItem(3).setArguments(extras);
                    } else if (selectedTab.equals("2")) {
                        viewPager.setCurrentItem(2, true);
                        contactDetailsFragmentPagerAdapter.getItem(2).setArguments(extras);
                    } else if (selectedTab.equals("1")) {
                        viewPager.setCurrentItem(1, true);
                        contactDetailsFragmentPagerAdapter.getItem(1).setArguments(extras);
                    } else {
                        viewPager.setCurrentItem(0, true);
                        contactDetailsFragmentPagerAdapter.getItem(0).setArguments(extras);
                    }
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        try {
            bus = TinyBus.from(this.getApplicationContext());
            bus.register(this);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String source = extras.getString(ContactDetailsTabActivity.KEY_SOURCE);
            String newContactId = extras.getString(ContactDetailsTabActivity.KEY_CONTACT_ID);
            String selectedTab = extras.getString(ContactDetailsTabActivity.KEY_SET_SELECTED_TAB);
            if (source != null) {
                if (source.equalsIgnoreCase(ContactDetailsTabActivity.KEY_SOURCE_NOTIFICATION)) {
                    finish();
                    Intent intent = new Intent(this, ContactDetailsTabActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(ContactDetailsTabActivity.KEY_CONTACT_ID, newContactId);
                    intent.putExtra(ContactDetailsTabActivity.KEY_SET_SELECTED_TAB, selectedTab);
                    startActivity(intent);
                }
            }
        }

        if (contactIdString != null) {
            Long contactIDLong = Long.parseLong(contactIdString);
            selectedContact = LSContact.findById(LSContact.class, contactIDLong);
        }

        if (selectedContact != null) {
            if (selectedContact.getContactName() == null || selectedContact.getContactName().equals("")) {
                toolbar.setTitle(selectedContact.getPhoneOne());
                setSupportActionBar(toolbar);
            } else {
                toolbar.setTitle(selectedContact.getContactName());
                setSupportActionBar(toolbar);
            }
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        bus.unregister(this);
        super.onStop();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent: ");
        if (intent != null)
            setIntent(intent);
    }

    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed: ");
//        BackPressedEventModel model = new BackPressedEventModel();
//        TinyBus.from(getApplicationContext()).post(model);
//        if (!model.backPressHandled) {
//            super.onBackPressed();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lead_options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.ic_action_message:
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("sms:"));
//                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("app.lastingsales.com"));
////                intent.setType("image/png");
//                intent.putExtra("subject", "Greetings");
//                intent.putExtra("address", selectedContact.getPhoneOne());
//                intent.putExtra(intent.EXTRA_TEXT, "Welcome to lastingSales");
//                intent.putExtra("sms_body", "Welcome to lastingSales");
//                startActivity(intent);
////                Toast.makeText(this, "Opening Message window", Toast.LENGTH_SHORT).show();
//                break;
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
                        DeleteManager.deleteContact(ContactDetailsTabActivity.this, selectedContact);
                        Snackbar.make(toolbar, "Lead deleted!", Snackbar.LENGTH_SHORT).show();
                        dialog.dismiss();
                        finish();
                        InquiryDeletedEventModel mCallEvent = new InquiryDeletedEventModel();
                        TinyBus bus = TinyBus.from(ContactDetailsTabActivity.this);
                        bus.post(mCallEvent);
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
                Log.d("duplicate", "onOptionsItemSelected: ");
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
}
