package com.example.muzafarimran.lastingsales.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.CallClickListener;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo 1 on 9/21/2016.
 */
public class ContactsAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<LSContact> mContacts;
    private List<LSContact> filteredData;
    private final static int TYPE_SEPARATOR = 0;
    private final static int TYPE_ITEM = 1;
    private final static int ITEM_TYPES = 2;
    private int prospectCount = 0;
    private int leadCount = 0;
    private CallClickListener callClickListener = null;
    private showContactDetaislsListener showContactDetaislsListener = null;

    View contact_details = null;

    public ContactsAdapter(Context c, List<LSContact> contacts) {
        this.mContext = c;
        this.mContacts = contacts;
        this.filteredData = contacts;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.callClickListener = new CallClickListener(c);
        this.showContactDetaislsListener = new showContactDetaislsListener();
        //TODO: correct the counting mechanism
//        this.prospectCount = contacts.indexOf(new LSContact("Leads", null, "separator", null, null, null, null, null, null)) - 1;
        this.leadCount = contacts.size() - this.prospectCount - 2;
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_TYPES;
    }

    @Override
    public int getItemViewType(int position) {
        return isSeparator(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }


    @Override
    public int getCount() {
        return this.filteredData.size();
    }

    @Override
    public Object getItem(int position) {
        return this.filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LSContact contact = (LSContact) getItem(position);

        if (isSeparator(position)) {
            //Toast.makeText(mContext,"sup", Toast.LENGTH_LONG ).show();

            separatorViewHolder separatorviewHolder = null;
            if (convertView == null) {

                convertView = mInflater.inflate(R.layout.section_separator_two_text_views, parent, false);
                separatorviewHolder = new separatorViewHolder();

                separatorviewHolder.salesType = (TextView) convertView.findViewById(R.id.section_separator_first_text);
                separatorviewHolder.salesTypeCount = (TextView) convertView.findViewById(R.id.section_separator_second_text);

                convertView.setTag(separatorviewHolder);

            } else {
                separatorviewHolder = (separatorViewHolder) convertView.getTag();
            }

            separatorviewHolder.salesType.setText(contact.getContactName());
            switch (contact.getContactName()) {
                case "Prospects":
                    separatorviewHolder.salesTypeCount.setText(Integer.toString(this.prospectCount));
                    break;

                case "Leads":
                    separatorviewHolder.salesTypeCount.setText(Integer.toString(this.leadCount));
                    break;
            }

        } else {
            ViewHolder holder = null;
            if (convertView == null) {

                convertView = mInflater.inflate(R.layout.contact_row_view, parent, false);

                holder = new ViewHolder();

                holder.name = (TextView) convertView.findViewById(R.id.contact_name);
                holder.number = (TextView) convertView.findViewById(R.id.contactNumber);
                holder.call_icon = (ImageView) convertView.findViewById(R.id.call_icon);
                holder.user_details_wrapper = (RelativeLayout) convertView.findViewById(R.id.user_call_group_wrapper);


                convertView.setTag(holder);

                holder.call_icon.setOnClickListener(this.callClickListener);
                holder.user_details_wrapper.setOnClickListener(this.showContactDetaislsListener);

            } else {

                holder = (ViewHolder) convertView.getTag();
                ((ViewGroup) holder.user_details_wrapper.getParent()).removeView(contact_details);
            }


            holder.name.setText(contact.getContactName());
            holder.user_details_wrapper.setTag(position);
            holder.number.setText(contact.getPhoneOne());

            holder.call_icon.setTag(mContacts.get(position).getPhoneOne());

        }

        return convertView;
    }


    // for searching
    //TODO this method needs to be moved from here

    @Override
    public Filter getFilter() {

        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new Filter.FilterResults();

                //If there's nothing to filter on, return the original data for list
                if (charSequence == null || charSequence.length() == 0) {

                    results.values = mContacts;
                    results.count = mContacts.size();

                } else {

                    List<LSContact> filterResultsData = new ArrayList<>();
                    //int length = charSequence.length();
                    for (int i = 0; i < mContacts.size(); i++) {
                        if (mContacts.get(i).getContactType().toLowerCase() != "separator" && mContacts.get(i).getContactName().toLowerCase().startsWith(((String) charSequence).toLowerCase())) {
                            filterResultsData.add(mContacts.get(i));
                        }
                    }

                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredData = ((List<LSContact>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }


    /*
    * event handler for click on name
    * */
    public class showContactDetaislsListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (contact_details == null) {
                contact_details = mInflater.inflate(R.layout.contact_detail_drop_down, null);

            } else {
                if (contact_details.getParent() != null) {
                    ((ViewGroup) contact_details.getParent()).removeView(contact_details);
                }

            }

            String number = mContacts.get((int) v.getTag()).getPhoneOne();
            // fill in any details dynamically here
            TextView lastContactText = (TextView) contact_details.findViewById(R.id.last_contact_text);
            TextView responseTimeText = (TextView) contact_details.findViewById(R.id.response_time_text);
            TextView messagesText = (TextView) contact_details.findViewById(R.id.messages_text);
            TextView numberCallsText = (TextView) contact_details.findViewById(R.id.calls_text);

            //TODO get details from the database
            lastContactText.setText("2 Days ago");
            responseTimeText.setText("2 hours");
            messagesText.setText("7");
            numberCallsText.setText("4");

            // insert into main view row
            ViewGroup insertPoint = (ViewGroup) ((ViewGroup) v.getParent()).findViewById(R.id.contact_row);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            params.topMargin = 140;
            insertPoint.addView(contact_details, params);
        }
    }

    /*
    * Hold references to sub views
    * */
    static class ViewHolder {
        TextView name;
        TextView number;
        ImageView call_icon;
        RelativeLayout user_details_wrapper;
    }

    public void setList(List<LSContact> contacts) {
        mContacts = contacts;
        filteredData = contacts;
        notifyDataSetChanged();
    }

    /*
    * Hold references to separator tab
    * */
    static class separatorViewHolder {
        TextView salesType;
        TextView salesTypeCount;
    }

    private boolean isSeparator(int position) {
        return filteredData.get(position).getContactType() == "separator";
    }
}
