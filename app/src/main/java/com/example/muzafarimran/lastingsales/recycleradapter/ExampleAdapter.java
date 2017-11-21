package com.example.muzafarimran.lastingsales.recycleradapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import android.media.Image;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.app.ClassManager;

import java.util.List;

/**
 * Created by ibtisam on 11/20/2017.
 */

public class ExampleAdapter extends CursorAdapter {

    private static final String TAG = "ExampleAdapter";
    private List<Object> items;

    private ConstraintLayout cl;

    private TextView tvText;

    private ImageView ivAvatar;

    public ExampleAdapter(Context context, Cursor cursor, List<Object> items) {

        super(context, cursor, false);

        this.items = items;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        tvText.setText(cursor.getString(cursor.getColumnIndex("text")));

        ivAvatar.setBackgroundResource(cursor.getInt(cursor.getColumnIndex("drawable")));

        cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String className = cursor.getString(cursor.getColumnIndex("class"));
                    context.startActivity(new Intent(context, ClassManager.getClass(className)));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "onClick: Exception unhandled Intent" + e);
                }
            }
        });


//        int position = cursor.getPosition();
//
//        final HomeItem homeItem  = (HomeItem) items.get(position);
//
//        tvText.setText(homeItem.value);

//        int position = cursor.getPosition();
//
//        LSContact lsContact =  (LSContact) items.get(position);
//
//        tvText.setText(lsContact.getContactName());

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item, parent, false);

        cl = (ConstraintLayout) view.findViewById(R.id.cl);

        tvText = (TextView) view.findViewById(R.id.tvText);

        ivAvatar = (ImageView) view.findViewById(R.id.ivAvatar);

        return view;

    }

}
