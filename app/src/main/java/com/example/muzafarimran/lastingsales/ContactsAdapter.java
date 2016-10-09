package com.example.muzafarimran.lastingsales;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by lenovo 1 on 9/21/2016.
 */
public class ContactsAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Contact> mContacts;
    private final static int TYPE_SEPARATOR = 0;
    private final static int TYPE_ITEM = 1;
    private final static int ITEM_TYPES = 2;
    private int prospectCount = 0;
    private int leadCount = 0;
    private CallClickListener callClickListener = null;
    private showContactDetaislsListener showContactDetaislsListener = null;



    public ContactsAdapter(Context c, ArrayList<Contact> contacts)
    {
        this.mContext = c;
        this.mContacts = contacts;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        if (isSeparator(position)){

            seperatorViewHolder seperatorviewHolder = null;
            if (convertView == null){

                convertView = mInflater.inflate(R.layout.section_seperator, parent, false);
                seperatorviewHolder = new seperatorViewHolder();
                seperatorviewHolder.text = (TextView) convertView.findViewById(R.id.section_seperator);

                convertView.setTag(seperatorviewHolder);

            }else {
                seperatorviewHolder = (seperatorViewHolder) convertView.getTag();
            }
            String label = "";
            switch (contact.getName()){

                case "Prospects":
                    label = "Prospects ( "+this.prospectCount+" )";
                    break;

                case "Leads":
                    label = "Leads ( "+this.leadCount+" )";
                    break;

            }
            seperatorviewHolder.text.setText(label);

        }else{
            ViewHolder holder = null;
            if (convertView == null) {

                convertView = mInflater.inflate(R.layout.contacts_text_view, parent, false);

                holder = new ViewHolder();

                holder.name            = (TextView)  convertView.findViewById(R.id.contact_name);
                holder.number          = (TextView)  convertView.findViewById(R.id.contact_number);
                holder.image           = (ImageView) convertView.findViewById(R.id.call_icon);
                holder.messages        = (TextView)  convertView.findViewById(R.id.messages);
                holder.response_time   = (TextView)  convertView.findViewById(R.id.response_time);
                holder.last_contact    = (TextView)  convertView.findViewById(R.id.last_contact);
                holder.number_calls    = (TextView)  convertView.findViewById(R.id.number_calls);
                holder.contact_details = (RelativeLayout) convertView.findViewById(R.id.contact_details);

                convertView.setTag(holder);

                holder.image.setOnClickListener(this.callClickListener);
                holder.name.setOnClickListener(this.showContactDetaislsListener);


            } else {

                holder = (ViewHolder) convertView.getTag();
            }


            holder.name.setText(contact.getName());
            holder.number.setText(contact.getNumber());
            holder.messages.setText(contact.getNumber_messages());
            holder.response_time.setText(contact.getResponse_time());
            holder.last_contact.setText(contact.getLast_contact());
            holder.number_calls.setText(contact.getNumber_calls());

            holder.image.setTag(mContacts.get(position).getNumber());
            holder.name.setTag(holder.contact_details);
        }

        return convertView;
    }

    /*
    * event handler for click on name
    * */
    public class showContactDetaislsListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //System.out.println(((TextView)v).getText().toString());
            RelativeLayout relativeLayout =((RelativeLayout)(v.getTag()));
            relativeLayout.setVisibility(relativeLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        }
    }

    /*
    * Hold references to sub views
    * */
    static class ViewHolder {
        TextView name;
        TextView number;
        ImageView image;
        TextView last_contact;
        TextView response_time;
        TextView messages;
        TextView number_calls;
        RelativeLayout contact_details;
    }

    /*
    * Hold references to seperator tab
    * */
    static class seperatorViewHolder{
        TextView text;
    }

    private boolean isSeparator(int position) {
        return mContacts.get(position).getTag() == "seperator";
    }
}
