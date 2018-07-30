package com.example.muzafarimran.lastingsales.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.DealDetailsFragmentPagerAdapter;
import com.example.muzafarimran.lastingsales.events.DealEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSDeal;
import com.example.muzafarimran.lastingsales.utilscallprocessing.DeleteManager;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 12/29/2016.
 */

public class DealDetailsTabActivity extends AppCompatActivity {
    public static final String TAG = "ContactDetailsTab";
    public static final String KEY_DEAL_ID = "deal_id";
    public static final String KEY_SET_SELECTED_TAB = "key_set_selected_tab";

    ViewPager viewPager;
    Toolbar toolbar;
    ActionBar actionBar;
    FloatingActionButton floatingActionButton;
    private String dealIdString = "0";
    private String selectedTab = "";
    private LSDeal selectedDeal;
    private TinyBus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details_tab);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        Long dealIDLong;
        if (extras != null) {
            dealIdString = extras.getString(DealDetailsTabActivity.KEY_DEAL_ID);
            if (dealIdString != null) {
                dealIDLong = Long.parseLong(dealIdString);
                selectedDeal = LSDeal.findById(LSDeal.class, dealIDLong);
            }
        }
        if (selectedDeal != null) {
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            viewPager.setAdapter(new DealDetailsFragmentPagerAdapter(getSupportFragmentManager(), selectedDeal.getId())); //TODO crash getId was null
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
                    }
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });

            floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_add);
            floatingActionButton.hide();
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedDeal != null) {
                        Intent intent = new Intent(getApplicationContext(), AddEditNoteActivity.class);
                        intent.putExtra(AddEditNoteActivity.ACTIVITY_LAUNCH_MODE, AddEditNoteActivity.LAUNCH_MODE_ADD_NEW_NOTE);
                        intent.putExtra(AddEditNoteActivity.TAG_LAUNCH_MODE_DEAL_ID, selectedDeal.getId());
                        startActivity(intent);
                    }
                }
            });
        } else {
            finish();
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
                String nameTextOnDialog = "";
                if (selectedDeal.getName() != null) {
                    nameTextOnDialog = selectedDeal.getName();
                }
                AlertDialog.Builder alert = new AlertDialog.Builder(DealDetailsTabActivity.this);
                alert.setTitle("Delete");
                alert.setMessage("Are you sure to delete " + nameTextOnDialog);
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        selectedDeal.delete();
                        DeleteManager.deleteDeal(DealDetailsTabActivity.this, selectedDeal);
//                        Snackbar.make(toolbar, "Lead deleted!", Snackbar.LENGTH_SHORT).show();
                        dialog.dismiss();
                        finish();
                        TinyBus.from(DealDetailsTabActivity.this.getApplicationContext()).post(new DealEventModel());
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
                Intent addContactScreenIntent = new Intent(getApplicationContext(), EditDealActivity.class);
                addContactScreenIntent.putExtra(EditDealActivity.TAG_LAUNCH_MODE_DEAL_ID, dealIdString);
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
        bus.unregister(this);
        super.onStop();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Bundle extras = getIntent().getExtras();
        Long dealIDLong;
        if (extras != null) {
            dealIdString = extras.getString(DealDetailsTabActivity.KEY_DEAL_ID);
            if (dealIdString != null) {
                dealIDLong = Long.parseLong(dealIdString);
                selectedDeal = LSDeal.findById(LSDeal.class, dealIDLong);
            }
        }

        if (selectedDeal != null) {
            if (selectedDeal.getName() == null || selectedDeal.getName().equals("")) {
//                tvName.setVisibility(View.GONE);
            } else {
                if (selectedDeal.getContact() != null && selectedDeal.getContact().getContactName() != null && !selectedDeal.getContact().getContactName().equals("")) {
                    toolbar.setTitle(selectedDeal.getName() + " (" + selectedDeal.getContact().getContactName() + ") ");
                    setSupportActionBar(toolbar);
//                collapsingToolbarLayout.setTitle(selectedDeal.getContactName());
                } else {
                    toolbar.setTitle(selectedDeal.getName());
                    setSupportActionBar(toolbar);
                }
            }
//            if (selectedDeal.getPhoneOne() == null || selectedDeal.getPhoneOne().equals("")) {
////                tvNumberOne.setVisibility(View.GONE);
//            } else {
////                tvNumberOne.setText(selectedDeal.getPhoneOne());
//            }
        }
        setSupportActionBar(toolbar);
        if (extras != null) {
            selectedTab = extras.getString(DealDetailsTabActivity.KEY_SET_SELECTED_TAB);
            if (selectedTab != null && selectedTab != "") {
                viewPager.setCurrentItem(1, true);
            }
        }
    }
}
