package com.example.muzafarimran.lastingsales.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by ahmad on 01-Nov-16.
 */

public class ImportContactsListAdapter2 extends BaseAdapter implements Filterable{
    LayoutInflater inflater;
    ArrayList<LSContact> contactsList;
    Context context;
    Activity activity;
    Context mContext;
    private int listItemLayout;

    public ImportContactsListAdapter2(Context context, int layoutId, ArrayList<LSContact> contactsList, Activity activity) {
        mContext = context;
        listItemLayout = layoutId;
        inflater = LayoutInflater.from(mContext);
        this.contactsList = contactsList;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return contactsList.size();
    }

    @Override
    public Object getItem(int i) {
        return contactsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data tvContactName for this position
        final LSContact oneContact = (LSContact) getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(listItemLayout, parent, false);

            viewHolder.contactName = (TextView) convertView.findViewById(R.id.list_contacts_single_item_contact_name);
            viewHolder.contactNumber = (TextView) convertView.findViewById(R.id.list_contacts_single_item_contact_number);
            convertView.setTag(viewHolder); // view lookup cache stored in tag
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.contactName.setText(oneContact.getContactName());
        viewHolder.contactNumber.setText(oneContact.getPhoneOne());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "name: " + oneContact.getContactName(), Toast.LENGTH_SHORT).show();
                Intent data = new Intent();
//                    String text = oneContact.getId() + "";
                String text = position + "";
//                    data.setData(Uri.parse(text));
                data.setData(Uri.parse(text));
                activity.setResult(RESULT_OK, data);
                activity.finish();
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    // The ViewHolder, only one tvContactName for simplicity and demonstration purposes, you can put all the views inside a row of the list into this ViewHolder
    private static class ViewHolder {
        TextView contactName;
        TextView contactNumber;
    }

}
