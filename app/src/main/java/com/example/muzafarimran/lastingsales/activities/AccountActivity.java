package com.example.muzafarimran.lastingsales.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.carditems.SettingItem;
import com.example.muzafarimran.lastingsales.recycleradapter.MyRecyclerViewAdapter;
import com.example.muzafarimran.lastingsales.utils.MyDateTimeStamp;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by ibtisam on 11/8/2017.
 */

public class AccountActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter adapter;
    private List<Object> list = new ArrayList<Object>();
    SessionManager sessionManager;
    private SimpleDraweeView ivPic;
    private TextView tvEmail;
    private TextView tvName;
    private TextView tvRole;
    private TextView tvSupportNumber;

    private static Bitmap Image = null;
    private static Bitmap rotateImage = null;
    private static final int GALLERY = 1;

    public static int getOrientation(Context context, Uri photoUri) {
        /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        ivPic = findViewById(R.id.ivPic);
        tvName = findViewById(R.id.tvContactName);
        tvEmail = findViewById(R.id.tvNumber);
        tvRole = findViewById(R.id.tvRole);
        tvSupportNumber = findViewById(R.id.tvSupportNumber);
        mRecyclerView = findViewById(R.id.rv_about);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sessionManager = new SessionManager(this);

        MyDateTimeStamp.setFrescoImage(ivPic, sessionManager.getKeyLoginImagePath());

        tvName.setText(sessionManager.getKeyLoginFirstName() + " " + sessionManager.getKeyLoginLastName());
        tvEmail.setText(sessionManager.getKeyLoginEmail());
        tvRole.setText("Account type: " + sessionManager.getKeyLoginRoleName());
        SettingItem settingItemAbout = new SettingItem("About", null, R.drawable.ic_info_outline_24dp);
        SettingItem settingItemSetting = new SettingItem("Settings", null, R.drawable.ic_settings_24dp);
        SettingItem settingItemLogout = new SettingItem("Logout", null, R.drawable.ic_power_settings_new_24dp);
        list.add(settingItemAbout);
        list.add(settingItemSetting);
        list.add(settingItemLogout);

        adapter = new MyRecyclerViewAdapter(this, list);

        mRecyclerView.setAdapter(adapter);

        tvSupportNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = "03111308308";
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + number));
                startActivity(intent);
            }
        });

        ivPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivPic.setImageBitmap(null);
//                if (Image != null)
//                    Image.recycle();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY && resultCode != 0) {
            Uri mImageUri = data.getData();
            try {
                Image = Media.getBitmap(this.getContentResolver(), mImageUri);
                if (getOrientation(getApplicationContext(), mImageUri) != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(getOrientation(getApplicationContext(), mImageUri));
                    if (rotateImage != null)
                        rotateImage.recycle();
                    rotateImage = Bitmap.createBitmap(Image, 0, 0, Image.getWidth(), Image.getHeight(), matrix, true);
                    ivPic.setImageBitmap(rotateImage);
                } else
                    ivPic.setImageBitmap(Image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
