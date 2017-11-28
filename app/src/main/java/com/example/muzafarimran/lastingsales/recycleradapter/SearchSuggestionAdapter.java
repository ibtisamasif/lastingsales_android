package com.example.muzafarimran.lastingsales.recycleradapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.ContactDetailsTabActivity;
import com.example.muzafarimran.lastingsales.activities.NavigationBottomMainActivity;
import com.example.muzafarimran.lastingsales.app.ClassManager;

import java.util.List;

/**
 * Created by ibtisam on 11/20/2017.
 */

public class SearchSuggestionAdapter extends CursorAdapter {

    private static final String TAG = "SearchSuggestionAdapter";
    private List<Object> items;

    private ConstraintLayout cl;

    private TextView tvText;

    private ImageView ivAvatar;

    public SearchSuggestionAdapter(Context context, Cursor cursor, List<Object> items) {

        super(context, cursor, false);

        this.items = items;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        tvText.setText(cursor.getString(cursor.getColumnIndex("text")));

        ivAvatar.setImageResource(cursor.getInt(cursor.getColumnIndex("drawable")));

        cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String className = cursor.getString(cursor.getColumnIndex("class"));

                    if (className.equals(ClassManager.CONTACT_DETAILS_TAB_ACTIVITY)) {
                        String type = cursor.getString(cursor.getColumnIndex("type"));
                        if (type.equals("type_note")) {

                            long intentPutId = cursor.getLong(cursor.getColumnIndex("intentPutId"));
                            Intent detailsActivityIntent = new Intent(context, ClassManager.getClass(className));
                            long contactId = intentPutId;
                            detailsActivityIntent.putExtra(ContactDetailsTabActivity.KEY_CONTACT_ID, contactId + "");
                            detailsActivityIntent.putExtra(ContactDetailsTabActivity.KEY_SET_SELECTED_TAB, "1");
                            context.startActivity(detailsActivityIntent);

                        } else {
                            long intentPutId = cursor.getLong(cursor.getColumnIndex("intentPutId"));
                            Intent detailsActivityIntent = new Intent(context, ClassManager.getClass(className));
                            long contactId = intentPutId;
                            detailsActivityIntent.putExtra(ContactDetailsTabActivity.KEY_CONTACT_ID, contactId + "");
                            context.startActivity(detailsActivityIntent);
                        }
                    } else if (className.equals(ClassManager.CONTACT_CALL_DETAILS_BOTTOM_SHEET_FRAGMENT)) {
                        long intentPutId = cursor.getLong(cursor.getColumnIndex("intentPutId"));
                        NavigationBottomMainActivity navigationBottomMainActivity = (NavigationBottomMainActivity) context;
                        navigationBottomMainActivity.onClickUnlabeled(intentPutId);

                    } else if (className.equals(ClassManager.INQUIRY_CALL_DETAILS_BOTTOM_SHEET_FRAGMENT)) {
                        String intentPutNumber = cursor.getString(cursor.getColumnIndex("intentPutNumber"));
                        NavigationBottomMainActivity navigationBottomMainActivity = (NavigationBottomMainActivity) context;
                        navigationBottomMainActivity.onClickInquiry(intentPutNumber);
                    } else {
                        Toast.makeText(context, "Details not found", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "onClick: Exception unhandled Intent" + e);
                }
            }
        });
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.card_search_item, parent, false);

        cl = (ConstraintLayout) view.findViewById(R.id.cl);

        tvText = (TextView) view.findViewById(R.id.tvText);

        ivAvatar = (ImageView) view.findViewById(R.id.ivAvatar);

        return view;

    }

}
