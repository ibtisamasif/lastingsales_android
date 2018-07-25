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
import com.example.muzafarimran.lastingsales.activities.DealDetailsTabActivity;
import com.example.muzafarimran.lastingsales.activities.NavigationBottomMainActivity;
import com.example.muzafarimran.lastingsales.activities.OrganizationDetailsTabActivity;
import com.example.muzafarimran.lastingsales.app.ClassManager;

/**
 * Created by ibtisam on 11/20/2017.
 */

public class SearchSuggestionAdapter extends CursorAdapter {

    private static final String TAG = "SearchSuggestionAdapter";

    private ConstraintLayout cl;

    private TextView tvText;

    private ImageView ivAvatar;

    public SearchSuggestionAdapter(Context context, Cursor cursor) {

        super(context, cursor, false);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String INTENT_CLASSNAME = cursor.getString(cursor.getColumnIndex("class"));

        String INTENT_TYPE = cursor.getString(cursor.getColumnIndex("type"));

        long INTENT_PUT_ID = 0;
        try {
            INTENT_PUT_ID = cursor.getLong(cursor.getColumnIndex("intentPutId"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String INTENT_PUT_NUMBER = cursor.getString(cursor.getColumnIndex("intentPutNumber"));

        Log.d(TAG, "bindView: INTENT_PUT_ID: " + INTENT_PUT_ID);

        tvText.setText(cursor.getString(cursor.getColumnIndex("text")));

        ivAvatar.setImageResource(cursor.getInt(cursor.getColumnIndex("drawable")));

        long finalINTENT_PUT_ID = INTENT_PUT_ID;
        cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
//                    String className = cursor.getString(cursor.getColumnIndex("class"));

                    if (INTENT_CLASSNAME.equals(ClassManager.CONTACT_DETAILS_TAB_ACTIVITY)) {
//                        String type = cursor.getString(cursor.getColumnIndex("type"));
                        if (INTENT_TYPE.equals("type_note")) {
//                            long intentPutId = cursor.getLong(cursor.getColumnIndex("intentPutId"));
                            Intent i = new Intent(context, ClassManager.getClass(INTENT_CLASSNAME));
                            long contactId = finalINTENT_PUT_ID;
                            i.putExtra(ContactDetailsTabActivity.KEY_CONTACT_ID, contactId + "");
                            i.putExtra(ContactDetailsTabActivity.KEY_SET_SELECTED_TAB, "1");
                            context.startActivity(i);

                        } else {
//                            long intentPutId = cursor.getLong(cursor.getColumnIndex("intentPutId"));
                            Intent i = new Intent(context, ClassManager.getClass(INTENT_CLASSNAME));
                            long contactId = finalINTENT_PUT_ID;
                            i.putExtra(ContactDetailsTabActivity.KEY_CONTACT_ID, contactId + "");
                            context.startActivity(i);
                        }
                    } else if (INTENT_CLASSNAME.equals(ClassManager.DEAL_DETAILS_TAB_ACTIVITY)) {
                        if (INTENT_TYPE.equals("type_deal")) {
                            //                            long intentPutId = cursor.getLong(cursor.getColumnIndex("intentPutId"));
                            Intent i = new Intent(context, ClassManager.getClass(INTENT_CLASSNAME));
                            long dealId = finalINTENT_PUT_ID;
                            i.putExtra(DealDetailsTabActivity.KEY_DEAL_ID, dealId + "");
                            context.startActivity(i);
                        }
                    } else if (INTENT_CLASSNAME.equals(ClassManager.ORG_DETAILS_BOTTOM_SHEET_FRAGMENT)) {
                        if (INTENT_TYPE.equals("type_org")) {
                            //Toast.makeText(context, "true", Toast.LENGTH_SHORT).show();
                            //                            long intentPutId = cursor.getLong(cursor.getColumnIndex("intentPutId"));
                            Intent detailsActivityIntent = new Intent(context, OrganizationDetailsTabActivity.class);
                            long organizationId = finalINTENT_PUT_ID;
                            detailsActivityIntent.putExtra(OrganizationDetailsTabActivity.KEY_ORGANIZATION_ID, organizationId + "");
                            context.startActivity(detailsActivityIntent);
                        }
                    } else if (INTENT_CLASSNAME.equals(ClassManager.INQUIRY_CALL_DETAILS_BOTTOM_SHEET_FRAGMENT)) {
                        String intentPutNumber = cursor.getString(cursor.getColumnIndex("intentPutNumber"));
                        NavigationBottomMainActivity navigationBottomMainActivity = (NavigationBottomMainActivity) context;
                        navigationBottomMainActivity.openInquiryBottomSheetCallback(INTENT_PUT_NUMBER);
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
