package com.example.muzafarimran.lastingsales.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.recycleradapter.MyRecyclerViewAdapter;
import com.example.muzafarimran.lastingsales.carditems.SettingItem;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ibtisam on 11/8/2017.
 */

public class AccountActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter adapter;
    private List<Object> list = new ArrayList<Object>();
    SessionManager sessionManager;
    private CircleImageView ivPic;
    private TextView tvEmail;
    private TextView tvName;
    private TextView tvRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        ivPic = (CircleImageView) findViewById(R.id.ivPic);
        tvName = (TextView) findViewById(R.id.tvName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvRole = (TextView) findViewById(R.id.tvRole);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_about);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sessionManager = new SessionManager(this);
        loadImage(ivPic, sessionManager.getKeyLoginImagePath());
        tvName.setText(sessionManager.getKeyLoginFirstName() + " " + sessionManager.getKeyLoginLastName());
        tvEmail.setText(sessionManager.getKeyLoginEmail());
        tvRole.setText("Account type: "+ sessionManager.getKeyLoginRoleName());
        SettingItem settingItemAbout = new SettingItem("About");
        SettingItem settingItemSetting = new SettingItem("Settings");
        SettingItem settingItemLogout = new SettingItem("Logout");

        list.add(settingItemAbout);
        list.add(settingItemSetting);
        list.add(settingItemLogout);

        adapter = new MyRecyclerViewAdapter(this, list);

        mRecyclerView.setAdapter(adapter);

    }

    private void loadImage(CircleImageView imageView, String url) {
        Glide.with(this)
                .load(url)
//                .override(48, 48)
//                .placeholder(R.drawable.placeholder)
                .error(R.drawable.ic_account_circle)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
