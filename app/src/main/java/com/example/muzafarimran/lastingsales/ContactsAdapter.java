package com.example.muzafarimran.lastingsales;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by lenovo 1 on 9/21/2016.
 */
public class ContactsAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Contact> mContacts;
    private final int VIEW_TYPE_COUNT = 2;
    private int prospectCount = 0;
    private int leadCount = 0;

    public ContactsAdapter(Context c, ArrayList<Contact> contacts)
    {
        this.mContext = c;
        this.mContacts = contacts;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.prospectCount = contacts.indexOf(new Contact("Leads", null, "seperator")) - 1;
        this.leadCount     = contacts.size() - this.prospectCount - 2;
    }

    @Override
    public int getViewTypeCount() {
        return this.VIEW_TYPE_COUNT;
    }

    @Override
    public int getCount()
    {
        return mContacts.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mContacts.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Contact contact = (Contact) getItem(position);
        if (contact.getTag() == "seperator"){

            String label = "";
            switch (contact.getName()){

                case "Prospects":
                    label = "Prospects ( "+this.prospectCount+" )";
                    break;

                case "Leads":
                    label = "Leads ( "+this.leadCount+" )";
                    break;

            }

            View view = mInflater.inflate(R.layout.section_seperator, parent, false);
            TextView seperatorLabel = (TextView) view.findViewById(R.id.section_seperator);
            seperatorLabel.setText(label);

            return view;
        }
        ViewHolder holder = null;


        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.calls_text_view, parent, false);

            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.call_name);


            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.name.setText(contact.getName());


        return convertView;
    }

    static class ViewHolder {
        TextView name;

    }
}
