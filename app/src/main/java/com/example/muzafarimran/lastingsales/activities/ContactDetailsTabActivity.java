package com.example.muzafarimran.lastingsales.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.ContactDetailsFragmentPagerAdapter;
import com.example.muzafarimran.lastingsales.events.BackPressedEventModel;
import com.example.muzafarimran.lastingsales.listeners.SearchCallback;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 12/29/2016.
 */

public class ContactDetailsTabActivity extends AppCompatActivity implements SearchCallback{

    public static final String KEY_CONTACT_ID = "contact_id";
    private String contactIdString = "0";
    private LSContact selectedContact;
    private TextView tvName;
    private TextView tvNumberOne;
    ViewPager viewPager;
    private CollapsingToolbarLayout collapsingToolbarLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details_tab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbarCollapse);
        collapsingToolbarLayout.setTitle("Lorem Ipsum");
        dynamicToolbarColor();
        toolbarTextAppernce();

//        tvName = (TextView) findViewById(R.id.tvNameOfUserContactDetailsScreen);
//        tvNumberOne = (TextView) findViewById(R.id.tvPhoneOneOfUserContactDetailsScreen);

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
                collapsingToolbarLayout.setTitle(selectedContact.getContactName());
            }
            if (selectedContact.getPhoneOne() == null || selectedContact.getPhoneOne().equals("")) {
//                tvNumberOne.setVisibility(View.GONE);
            } else {
//                tvNumberOne.setText(selectedContact.getPhoneOne());
            }
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new ContactDetailsFragmentPagerAdapter(getSupportFragmentManager() , selectedContact.getPhoneOne()));
        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.menu_icon_phone_selected);
        tabLayout.getTabAt(1).setIcon(R.drawable.menu_icon_details);
        tabLayout.getTabAt(2).setIcon(R.drawable.menu_icon_contact);
        tabLayout.getTabAt(3).setIcon(R.drawable.add_contact_notes_field_icon_unselected);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        tab.setIcon(R.drawable.menu_icon_phone_selected);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.menu_icon_details_selected);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.menu_icon_contact_selected);
                        break;
                    case 3:
                        tab.setIcon(R.drawable.add_contact_notes_field_icon);
                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        tab.setIcon(R.drawable.menu_icon_phone);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.menu_icon_details);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.menu_icon_contact);
                        break;
                    case 3:
                        tab.setIcon(R.drawable.add_contact_notes_field_icon_unselected);
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void dynamicToolbarColor() {

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.temp_avatar);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.lightBlue));
                collapsingToolbarLayout.setStatusBarScrimColor(getResources().getColor(R.color.darkBlue));
            }
        });
    }

        private void toolbarTextAppernce() {
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);
    }

    public void onBackPressed() {
        super.onBackPressed();
        BackPressedEventModel model = new BackPressedEventModel();
        TinyBus.from(getApplicationContext()).post(model);
        if (!model.backPressHandled) {
            super.onBackPressed();
        }
    }

    @Override
    public MaterialSearchView getSearchView() {
        return null;
    }

}
