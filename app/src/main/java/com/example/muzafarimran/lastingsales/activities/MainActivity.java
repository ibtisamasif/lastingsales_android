package com.example.muzafarimran.lastingsales.activities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
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

        this.setTitle("");

        setContentView(R.layout.activity_main);

       /* this.myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(this.myToolbar);*/
        //this.myToolbar.setMinimumHeight(350);

        // create a new database instance and open connection
        dbh = LastingSalesDatabaseHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = dbh.getWritableDatabase();

//        // Create a new map of values, where column names are the keys
//        ContentValues values = new ContentValues();
//        values.put(LastingSalesContract.User.COLUMN_NAME_NAME, "salman");
//        values.put(LastingSalesContract.User.COLUMN_NAME_EMAIL, "sbukhari577@gmail.com");
//        values.put(LastingSalesContract.User.COLUMN_NAME_PHONE, "6789");
//        values.put(LastingSalesContract.User.COLUMN_NAME_PASSWORD, "abcd");
//        values.put(LastingSalesContract.User.COLUMN_NAME_CREATED_AT, "now");
//        values.put(LastingSalesContract.User.COLUMN_NAME_CLIENT_ID, "1");
//
//        // Insert the new row, returning the primary key value of the new row
//        long newRowId = db.insert(LastingSalesContract.User.TABLE_NAME, null, values);
//        Log.d("newRowId", Long.toString(newRowId));

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

