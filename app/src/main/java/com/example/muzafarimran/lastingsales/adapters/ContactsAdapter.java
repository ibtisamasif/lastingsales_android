package com.example.muzafarimran.lastingsales.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.CallClickListener;
import com.example.muzafarimran.lastingsales.Contact;
import com.example.muzafarimran.lastingsales.R;


import java.util.List;

/**
 * Created by lenovo 1 on 9/21/2016.
 */
public class ContactsAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Contact> mContacts;
    private List<Contact> filteredData;
    private final static int TYPE_SEPARATOR = 0;
    private final static int TYPE_ITEM = 1;
    private final static int ITEM_TYPES = 2;
    private int prospectCount = 0;
    private int leadCount = 0;
    private CallClickListener callClickListener = null;
    private showContactDetaislsListener showContactDetaislsListener = null;

    View contact_details = null;





    public ContactsAdapter(Context c, List<Contact> contacts)
    {
        this.mContext = c;
        this.mContacts = contacts;
        this.filteredData = contacts;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.callClickListener = new CallClickListener(c);
        this.showContactDetaislsListener = new showContactDetaislsListener();
        this.prospectCount = contacts.indexOf(new Contact("Leads", null, "seperator", null, null, null, null)) - 1;
        this.leadCount     = contacts.size() - this.prospectCount - 2;


    }

    @Override
    public int getViewTypeCount() { return ITEM_TYPES; }

    @Override
    public int getItemViewType(int position) { return isSeparator(position) ? TYPE_SEPARATOR : TYPE_ITEM; }


    @Override
    public int getCount()
    {
        return this.filteredData.size();
    }

    @Override
    public Object getItem(int position)
    {
        return this.filteredData.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Contact contact = (Contact) getItem(position);

        if (isSeparator(position)){

            seperatorViewHolder seperatorviewHolder = null;
            if (convertView == null){

                convertView = mInflater.inflate(R.layout.section_seperator_two_text_views, parent, false);
                seperatorviewHolder = new seperatorViewHolder();

                seperatorviewHolder.salesType = (TextView) convertView.findViewById(R.id.section_seperator_first_text);
                seperatorviewHolder.salesTypeCount = (TextView) convertView.findViewById(R.id.section_seperator_second_text);

                convertView.setTag(seperatorviewHolder);

            }else {
                seperatorviewHolder = (seperatorViewHolder) convertView.getTag();
            }

            seperatorviewHolder.salesType.setText(contact.getName());
            switch (contact.getName()){
                case "Prospects":
                       seperatorviewHolder.salesTypeCount.setText(Integer.toString(this.prospectCount));
                        break;

                case "Leads":
                        seperatorviewHolder.salesTypeCount.setText(Integer.toString(this.leadCount));
                    break;
            }

        }else{
            ViewHolder holder = null;
            if (convertView == null) {

                convertView = mInflater.inflate(R.layout.contact_row_view, parent, false);

                holder = new ViewHolder();

                holder.name      = (TextView)  convertView.findViewById(R.id.contact_name);
                holder.number    = (TextView)  convertView.findViewById(R.id.contact_number);
                holder.call_icon = (ImageView) convertView.findViewById(R.id.call_icon);


                convertView.setTag(holder);

                holder.call_icon.setOnClickListener(this.callClickListener);
                holder.name.setOnClickListener(this.showContactDetaislsListener);


            } else {

                holder = (ViewHolder) convertView.getTag();
            }


            holder.name.setText(contact.getName());
            holder.name.setTag(position);
            holder.number.setText(contact.getNumber());

            holder.call_icon.setTag(mContacts.get(position).getNumber());

        }

        return convertView;
    }


    // for searching
    //TODO this method needs to be moved from here

    @Override
    public Filter getFilter() {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence)
            {
                FilterResults results = new Filter.FilterResults();

                //If there's nothing to filter on, return the original data for your list
                if(charSequence == null || charSequence.length() == 0) {

                    results.values = mContacts;
                    results.count = mContacts.size();
                }
                else
                {
                    //Toast.makeText(mContext,"else", Toast.LENGTH_LONG ).show();
                    List<Contact> filterResultsData = null;
                    //int length = charSequence.length();
                    for (int i = 0; i < mContacts.size(); i++){
                        if (mContacts.get(i).getName().startsWith(((String) charSequence))){
                            filterResultsData.add(mContacts.get(i));
                        }
                    }

                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults)
            {
                filteredData = ((List<Contact>)filterResults.values);
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
            if (contact_details == null){
                contact_details = mInflater.inflate(R.layout.contact_detail_drop_down, null);

            }else {
                if (contact_details.getParent() != null){((ViewGroup) contact_details.getParent()).removeView(contact_details);}

            }

            String number = mContacts.get((int) v.getTag()).getNumber();
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
            ViewGroup insertPoint = (ViewGroup) ((ViewGroup)v.getParent().getParent().getParent()).findViewById(R.id.contact_row);
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
    }

    /*
    * Hold references to seperator tab
    * */
    static class seperatorViewHolder{
        TextView salesType;
        TextView salesTypeCount;
    }

    private boolean isSeparator(int position) {
        return mContacts.get(position).getTag() == "seperator";
    }
}
