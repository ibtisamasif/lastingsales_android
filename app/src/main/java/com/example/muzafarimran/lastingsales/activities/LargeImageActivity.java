package com.example.muzafarimran.lastingsales.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.muzafarimran.lastingsales.R;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by ibtisam on 1/4/2018.
 */

public class LargeImageActivity extends AppCompatActivity {
    public static final String IMAGE_URL = "image_url";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_image);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String imageUrl = bundle.getString(LargeImageActivity.IMAGE_URL);
            SimpleDraweeView user_avatar = (SimpleDraweeView) findViewById(R.id.user_avatar);
            imageFunc(user_avatar, imageUrl);
        }
        else {
            finish();
        }
    }

    private void imageFunc(SimpleDraweeView imageView, String url) {
        Uri uri = Uri.parse(url);
        imageView.setImageURI(uri);

//        //Downloading using Glide Library
//        Glide.with(context)
//                .load(url)
////                .override(48, 48)
////                .placeholder(R.drawable.placeholder)
//                .error(R.drawable.ic_account_circle)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(imageView);
    }
}
