package com.example.muzafarimran.lastingsales.activities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.health.SystemHealthManager;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.SampleFragmentPagerAdapter;
import com.example.muzafarimran.lastingsales.providers.LastingSalesDatabaseHelper;
import com.example.muzafarimran.lastingsales.providers.models.Contact;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    private tabSelectedListener tabselectedlistener = new tabSelectedListener();
    private Context context = this;
/*
    Toolbar myToolbar = null;
*/
    // database helper
    LastingSalesDatabaseHelper dbh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.setTitle("LastingSales");

        setContentView(R.layout.activity_main);

       /* this.myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(this.myToolbar);*/
        //this.myToolbar.setMinimumHeight(350);

        // create a new database instance and open connection
        dbh = LastingSalesDatabaseHelper.getInstance(getApplicationContext());

        List<Contact> contacts = dbh.searchContacts("Kashif");
        System.out.println("Id is: " + contacts.get(0).getId());
        System.out.println("Email is: " + contacts.get(0).getEmail());
//        dbh.createContact(new Contact("Salman Bukhari", "sbukhari828@gmail.com", "sales", "0323-4433108", null, null, null, null, "prospect"));
//
//        dbh.createContact(new Contact("Leads", null, "separator", null, null, null, null, null, null));
//        dbh.createContact(new Contact("Raza Ahmad", "sra0nasir@gmail.com", "sales", "0332-5404943", null, null, null, null, "lead"));
//
//        dbh.createContact(new Contact("Malcom X", "malcolmx@yahoo.com", "nonbusiness", "650-540-9865", null, null, null, null, null));
//        dbh.createContact(new Contact("Courtney Cox", "courtney_cox@live.com", "nonbusiness", "0332-5404943", null, null, null, null, null));
//
//        dbh.createContact(new Contact("John Snow", "johnsnow@yahoo.com", "colleague", "546-654-7135", null, null, null, null, null));
//        dbh.createContact(new Contact("Alastar Cook", "alastar_cook@ymail.com", "colleague", "615-736-5445", null, null, null, null, null));
//
//        dbh.createContact(new Contact("Rachel Greene", "rachel_greene@gmail.com", "untagged", "654-857-9332", null, null, null, null, null));
//        dbh.createContact(new Contact("Ted Mosby", "tedmosby1@yahoo.com", "untagged", "141-785-1233", null, null, null, null, null));
//        dbh.createContact(new Contact("Garfield Sobers", "garysobers@gmail.com", "untagged", "691-337-1285", null, null, null, null, null));

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager(), MainActivity.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(this.tabselectedlistener);


        tabLayout.getTabAt(0).setIcon(R.drawable.menu_icon_home_selected_aqua);
        tabLayout.getTabAt(1).setIcon(R.drawable.menu_icon_phone);
        tabLayout.getTabAt(2).setIcon(R.drawable.menu_icon_contact);
        tabLayout.getTabAt(3).setIcon(R.drawable.menu_icon_menu);

    }

    public class tabSelectedListener implements TabLayout.OnTabSelectedListener {


        @Override
        public void onTabSelected(TabLayout.Tab tab) {


            switch (tab.getPosition()){

                case 0:
                    tab.setIcon(R.drawable.menu_icon_home_selected_aqua);
                    break;

                case 1:
                    tab.setIcon(R.drawable.menu_icon_phone_selected_aqua);
                    //((TextView)(myToolbar.findViewById(R.id.title))).setText("CALL LOGS");
                    break;

                case 2:
                    tab.setIcon(R.drawable.menu_icon_contact_selected_aqua);
                   // ((TextView)(myToolbar.findViewById(R.id.title))).setText("CONTACTS");
                    break;


                case 3:
                    tab.setIcon(R.drawable.menu_icon_menu_selected_aqua);
                   // ((TextView)(myToolbar.findViewById(R.id.title))).setText("MENU");
                    break;

            }

           /* if (tab.getPosition() != 0){
                myToolbar.findViewById(R.id.title).setVisibility(View.VISIBLE);
                myToolbar.findViewById(R.id.lasting_sales_logo).setVisibility(View.GONE);
                myToolbar.setMinimumHeight(100);


            }else {
                myToolbar.findViewById(R.id.lasting_sales_logo).setVisibility(View.VISIBLE);
                myToolbar.findViewById(R.id.title).setVisibility(View.GONE);
                myToolbar.setMinimumHeight(350);
            }*/

        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

            switch (tab.getPosition()){

                case 0:
                    tab.setIcon(R.drawable.menu_icon_home);
                    break;

                case 1:
                    tab.setIcon(R.drawable.menu_icon_phone);
                    break;

                case 2:
                    tab.setIcon(R.drawable.menu_icon_contact);
                    break;


                case 3:
                    tab.setIcon(R.drawable.menu_icon_menu);
                    break;

            }
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
           //int position = tab.getPosition();
        }
    }
}

