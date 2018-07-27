package com.example.muzafarimran.lastingsales.activities;

import android.app.Dialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.OrganizationDetailsFragmentPagerAdapter;
import com.example.muzafarimran.lastingsales.providers.models.LSOrganization;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utilscallprocessing.DeleteManager;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 12/29/2016.
 */

public class OrganizationDetailsTabActivity extends AppCompatActivity {
    public static final String TAG = "OrganizationDetailsTab";
    public static final String KEY_SOURCE = "key_source";
    public static final String KEY_SOURCE_NOTIFICATION = "key_source_notification";
    public static final String KEY_ORGANIZATION_ID = "organization_id";
    public static final String KEY_SET_SELECTED_TAB = "key_set_selected_tab";

    ViewPager viewPager;
    FloatingActionButton floatingActionButton;
    private String organizationIdString = "0";
    private String selectedTab = "";
    private LSOrganization selectedOrganization;
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
                intent.putExtra(AddEditNoteActivity.TAG_LAUNCH_MODE_ORGANIZATION_ID, selectedOrganization.getId());
                startActivity(intent);
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            organizationIdString = extras.getString(OrganizationDetailsTabActivity.KEY_ORGANIZATION_ID);
            if (organizationIdString != null) {
                Long organizationIDLong = Long.parseLong(organizationIdString);
                selectedOrganization = LSOrganization.findById(LSOrganization.class, organizationIDLong);
            }
        }

        if (selectedOrganization != null) {
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            OrganizationDetailsFragmentPagerAdapter organizationDetailsFragmentPagerAdapter = new OrganizationDetailsFragmentPagerAdapter(getSupportFragmentManager(), selectedOrganization.getId());
            viewPager.setAdapter(organizationDetailsFragmentPagerAdapter);
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
                selectedTab = extras.getString(OrganizationDetailsTabActivity.KEY_SET_SELECTED_TAB);
                if (selectedTab != null && !selectedTab.equals("")) {
                    if (selectedTab.equals("3")) {
                        viewPager.setCurrentItem(3, true);
                        organizationDetailsFragmentPagerAdapter.getItem(3).setArguments(extras);
                    } else if (selectedTab.equals("2")) {
                        viewPager.setCurrentItem(2, true);
                        organizationDetailsFragmentPagerAdapter.getItem(2).setArguments(extras);
                    } else if (selectedTab.equals("1")) {
                        viewPager.setCurrentItem(1, true);
                        organizationDetailsFragmentPagerAdapter.getItem(1).setArguments(extras);
                    } else {
                        viewPager.setCurrentItem(0, true);
                        organizationDetailsFragmentPagerAdapter.getItem(0).setArguments(extras);
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
            String source = extras.getString(OrganizationDetailsTabActivity.KEY_SOURCE);
            String newContactId = extras.getString(OrganizationDetailsTabActivity.KEY_ORGANIZATION_ID);
            String selectedTab = extras.getString(OrganizationDetailsTabActivity.KEY_SET_SELECTED_TAB);
            if (source != null) {
                if (source.equalsIgnoreCase(OrganizationDetailsTabActivity.KEY_SOURCE_NOTIFICATION)) {
                    finish();
                    Intent intent = new Intent(this, OrganizationDetailsTabActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(OrganizationDetailsTabActivity.KEY_ORGANIZATION_ID, newContactId);
                    intent.putExtra(OrganizationDetailsTabActivity.KEY_SET_SELECTED_TAB, selectedTab);
                    startActivity(intent);
                }
            }
        }

        if (organizationIdString != null) {
            Long organizationIDLong = Long.parseLong(organizationIdString);
            selectedOrganization = LSOrganization.findById(LSOrganization.class, organizationIDLong);
        }

        if (selectedOrganization != null) {
            if (selectedOrganization.getName() == null || selectedOrganization.getName().equals("")) {
                toolbar.setTitle(selectedOrganization.getPhone());
                setSupportActionBar(toolbar);
            } else {
                toolbar.setTitle(selectedOrganization.getName());
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
//                intent.putExtra("address", selectedContact.getPhone()());
//                intent.putExtra(intent.EXTRA_TEXT, "Welcome to lastingSales");
//                intent.putExtra("sms_body", "Welcome to lastingSales");
//                startActivity(intent);
////                Toast.makeText(this, "Opening Message window", Toast.LENGTH_SHORT).show();
//                break;
            case R.id.ic_action_delete:
                String nameTextOnDialog;
                if (selectedOrganization.getName() != null) {
                    nameTextOnDialog = selectedOrganization.getName();
                } else {
                    nameTextOnDialog = selectedOrganization.getPhone();
                }
                AlertDialog.Builder alert = new AlertDialog.Builder(OrganizationDetailsTabActivity.this);
                alert.setTitle("Delete");
                alert.setMessage("Are you sure to delete " + nameTextOnDialog);
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteManager.deleteOrganization(OrganizationDetailsTabActivity.this, selectedOrganization);
                        Snackbar.make(toolbar, "Organization deleted!", Snackbar.LENGTH_SHORT).show();
                        dialog.dismiss();
                        finish();
//                        InquiryDeletedEventModel mCallEvent = new InquiryDeletedEventModel();
//                        TinyBus bus = TinyBus.from(OrganizationDetailsTabActivity.this);
//                        bus.post(mCallEvent);
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
                editOrganizationDialogBox(selectedOrganization);
                break;

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void editOrganizationDialogBox(LSOrganization tempOrganization) {
        if (tempOrganization != null) {

            Dialog addOrgDialog = new Dialog(OrganizationDetailsTabActivity.this);
            addOrgDialog.setContentView(R.layout.edit_organization);
            addOrgDialog.setCancelable(true);
            addOrgDialog.show();

            Button bSave = (Button) addOrgDialog.findViewById(R.id.bSaveAddOrg);
            Button bCancel = (Button) addOrgDialog.findViewById(R.id.bCancelAddOrg);
            EditText nameAddOrg = addOrgDialog.findViewById(R.id.etNameAddOrg);
            EditText emailAddOrg = addOrgDialog.findViewById(R.id.etEmailAddOrg);
            EditText phoneAddOrg = addOrgDialog.findViewById(R.id.etPhoneAddOrg);

            nameAddOrg.setText(tempOrganization.getName());
            emailAddOrg.setText(tempOrganization.getEmail());
            phoneAddOrg.setText(tempOrganization.getPhone());
            bCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addOrgDialog.dismiss();
                }
            });

            bSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (nameAddOrg.getText().toString().isEmpty()) {
                        nameAddOrg.setError("Please enter  Name!");
//                    Toast.makeText(getActivity(), "Please enter  Name!", Toast.LENGTH_SHORT).show();
                    } else {

                        tempOrganization.setName(nameAddOrg.getText().toString());
                        tempOrganization.setEmail(emailAddOrg.getText().toString());
                        tempOrganization.setPhone(phoneAddOrg.getText().toString());

                        if (tempOrganization.getSyncStatus().equals(SyncStatus.SYNC_STATUS_ORGANIZATION_ADD_SYNCED) || tempOrganization.getSyncStatus().equals(SyncStatus.SYNC_STATUS_ORGANIZATION_UPDATE_SYNCED)) {
                            tempOrganization.setSyncStatus(SyncStatus.SYNC_STATUS_ORGANIZATION_UPDATE_NOT_SYNCED);
                        }

                        if (tempOrganization.save() > 0) {
                            Toast.makeText(OrganizationDetailsTabActivity.this, "Organization Modified", Toast.LENGTH_SHORT).show();
                            addOrgDialog.dismiss();
                            DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(OrganizationDetailsTabActivity.this);
                            dataSenderAsync.run();
                        } else {
                            Toast.makeText(OrganizationDetailsTabActivity.this, "Error could not modify something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }
}
