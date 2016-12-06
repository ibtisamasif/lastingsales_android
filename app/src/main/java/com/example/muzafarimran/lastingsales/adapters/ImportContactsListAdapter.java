package com.example.muzafarimran.lastingsales.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by ahmad on 01-Nov-16.
 */

public class ImportContactsListAdapter extends ArrayAdapter<LSContact> {
    LayoutInflater inflater;
    ArrayList<LSContact> contactsList;
    Context context;
    Activity activity;
    private int listItemLayout;

    public ImportContactsListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public ImportContactsListAdapter(Context context, int layoutId, ArrayList<LSContact> contactsList, Activity activity) {
        super(context, layoutId, contactsList);
        listItemLayout = layoutId;
        inflater = LayoutInflater.from(getContext());
        this.contactsList = contactsList;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data tvContactName for this position
        final LSContact oneContact = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(listItemLayout, parent, false);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "name: " + oneContact.getContactName(), Toast.LENGTH_SHORT).show();
                    Intent data = new Intent();
//                    String text = oneContact.getId() + "";
                    String text = position + "";
//                    data.setData(Uri.parse(text));
                    data.setData(Uri.parse(text));
                    activity.setResult(RESULT_OK, data);
                    activity.finish();
                }
            });
            viewHolder.contactName = (TextView) convertView.findViewById(R.id.list_contacts_single_item_contact_name);
            viewHolder.contactNumber = (TextView) convertView.findViewById(R.id.list_contacts_single_item_contact_number);
            convertView.setTag(viewHolder); // view lookup cache stored in tag
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.contactName.setText(oneContact.getContactName());
        viewHolder.contactNumber.setText(oneContact.getPhoneOne());
        // Return the completed view to render on screen
        return convertView;
    }

    // The ViewHolder, only one tvContactName for simplicity and demonstration purposes, you can put all the views inside a row of the list into this ViewHolder
    private static class ViewHolder {
        TextView contactName;
        TextView contactNumber;
    }
}