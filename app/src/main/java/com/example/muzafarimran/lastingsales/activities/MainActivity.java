package com.example.muzafarimran.lastingsales.activities;

import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.SampleFragmentPagerAdapter;
import com.example.muzafarimran.lastingsales.providers.LastingSalesDatabaseHelper;

public class MainActivity extends AppCompatActivity {

    // database helper
    LastingSalesDatabaseHelper dbh;

        @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

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


        tabLayout.getTabAt(0).setIcon(R.drawable.menu_icon_home);
        tabLayout.getTabAt(1).setIcon(R.drawable.menu_icon_phone);
        tabLayout.getTabAt(2).setIcon(R.drawable.menu_icon_contact);
        tabLayout.getTabAt(3).setIcon(R.drawable.menu_icon_menu);

    }
}
