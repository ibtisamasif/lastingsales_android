package com.example.muzafarimran.lastingsales.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.CallClickListener;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.Utils.PhoneNumberAndCallUtils;
import com.example.muzafarimran.lastingsales.activities.ContactCallDetails;
import com.example.muzafarimran.lastingsales.activities.TagNumberAndAddFollowupActivity;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.view.View.GONE;

/**
 * Created by MUZAFAR IMRAN on 9/19/20
 */
public class UntaggedContactsAdapter extends BaseAdapter implements Filterable {

    private final static int TYPE_SEPARATOR = 0;
    private final static int TYPE_ITEM = 1;
    private final static int ITEM_TYPES = 2;
    public Context mContext;
    public ShowContactCallDetails detailsListener = null;
    Boolean expanded = false;
    RelativeLayout noteDetails = null;
    View call_details = null; //TODO move to handler class below
    private LayoutInflater mInflater;
    private List<LSContact> mContacts;
    private CallClickListener callClickListener = null;
    private ShowDetailsDropDown showcalldetailslistener = null;
    private List<LSContact> filteredData;

    public UntaggedContactsAdapter(Context c) {
        this.mContext = c;
        if (mContacts == null) {
            mContacts = new ArrayList<>();
            filteredData = new ArrayList<>();
        }
        this.callClickListener = new CallClickListener(c);
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.detailsListener = new ShowContactCallDetails();
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_TYPES;
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LSContact contact = (LSContact) getItem(position);
        final String number = contact.getPhoneOne();

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.untagged_contacts_list_item, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.call_name);
            holder.time = (TextView) convertView.findViewById(R.id.call_time);
            holder.call_icon = (ImageView) convertView.findViewById(R.id.call_icon);
            holder.call_name_time = (RelativeLayout) convertView.findViewById(R.id.user_call_group_wrapper);
            holder.numberDetailTextView = (TextView) convertView.findViewById(R.id.call_number);
            holder.tvConnections = (TextView) convertView.findViewById(R.id.tvConnections);
            holder.tvLastContact = (TextView) convertView.findViewById(R.id.tvLastContact);
            holder.bNonbusiness = (Button) convertView.findViewById(R.id.bNonBusinessUntaggedItem);
            holder.contactCallDetails = (RelativeLayout) convertView.findViewById(R.id.rl_calls_details);
            this.showcalldetailslistener = new ShowDetailsDropDown(contact, holder.contactCallDetails);
            holder.bSales = (Button) convertView.findViewById(R.id.bSalesUtaggedItem);
            holder.bColleague = (Button) convertView.findViewById(R.id.bColleagueUntaggedItem);
            holder.bNonbusiness = (Button) convertView.findViewById(R.id.bNonBusinessUntaggedItem);

            holder.call_icon.setOnClickListener(this.callClickListener);
            holder.call_name_time.setOnClickListener(this.showcalldetailslistener);
//            if (contact.getContact() != null) {
//                holder.bSales.setVisibility(GONE);
//            }
            holder.contactCallDetails.setVisibility(GONE);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            ((ViewGroup) holder.call_name_time.getParent().getParent()).removeView(call_details);
        }
        if (contact.getContactName() == null) {
            if (contact.getContactName() != null) {
                holder.name.setText(contact.getContactName());
            } else {
                holder.name.setText(contact.getPhoneOne());
            }
        } else {
            holder.name.setText(contact.getContactName());
        }
        holder.bNonbusiness.setTag(number);
        holder.bNonbusiness.setOnClickListener(detailsListener);
        holder.numberDetailTextView.setText(number);
        holder.call_name_time.setTag(position);
//        holder.time.setText(PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(contact.getBeginTime()));
        holder.call_icon.setTag(mContacts.get(position).getPhoneOne());
        holder.bSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(mContext, TagNumberAndAddFollowupActivity.class);
                myIntent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE, TagNumberAndAddFollowupActivity.LAUNCH_MODE_TAG_UNTAGGED_CONTACT);
                myIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_TYPE, LSContact.CONTACT_TYPE_SALES);
                myIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_ID, contact.getId() + "");
                mContext.startActivity(myIntent);
            }
        });
        holder.bColleague.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(mContext, TagNumberAndAddFollowupActivity.class);
                myIntent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE, TagNumberAndAddFollowupActivity.LAUNCH_MODE_TAG_UNTAGGED_CONTACT);
                myIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_TYPE, LSContact.CONTACT_TYPE_COLLEAGUE);
                myIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_ID, contact.getId() + "");
                mContext.startActivity(myIntent);
            }
        });
        holder.bNonbusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String intlNum = PhoneNumberAndCallUtils.numberToInterNationalNumber(number);
                contact.setContactType(LSContact.CONTACT_TYPE_PERSONAL);
                contact.save();
                ArrayList<LSContact> allUntaggedContacts = (ArrayList<LSContact>) LSContact.getContactsByType(LSContact.CONTACT_TYPE_UNTAGGED);
                setList(allUntaggedContacts);
                Toast.makeText(mContext, "Added as Non-Business Contact!", Toast.LENGTH_SHORT).show();
            }
        });

        ArrayList<LSCall> allCallsForThisNumber = LSCall.getCallsFromNumber(PhoneNumberAndCallUtils.numberToInterNationalNumber(contact.getPhoneOne()));
        if (allCallsForThisNumber != null && allCallsForThisNumber.size() > 0) {
            holder.tvConnections.setText("" + allCallsForThisNumber.size() + "");
        } else {
            holder.tvConnections.setText("" + 0 + "");
        }
        if (allCallsForThisNumber != null && allCallsForThisNumber.size() > 0) {
            LSCall latestCall = allCallsForThisNumber.get(allCallsForThisNumber.size() - 1);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(latestCall.getBeginTime());
            String lastContact = calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-"
                    + calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE);
            holder.tvLastContact.setText("" + lastContact + "");
        } else {
            holder.tvLastContact.setText("" + "Never+" + "");
        }
        return convertView;
    }

    public void setList(List<LSContact> mCalls) {
        this.mContacts = mCalls;
        filteredData = mCalls;
        notifyDataSetChanged();
    }

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
                        if (mContacts.get(i).getContactType().toLowerCase() != "separator" && mContacts.get(i).getContactName().toLowerCase().contains(((String) charSequence).toLowerCase())) {
                            filterResultsData.add(mContacts.get(i));
                            continue;
                        }
                        if (mContacts.get(i).getContactType().toLowerCase() != "separator" && mContacts.get(i).getPhoneOne().toLowerCase().contains(((String) charSequence).toLowerCase())) {
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

    static class ViewHolder {
        TextView name;
        TextView time;
        ImageView call_icon;
        ImageView missed_call_icon;
        RelativeLayout call_name_time;
        RelativeLayout contactCallDetails;
        TextView numberDetailTextView;
        TextView tvConnections;
        TextView tvLastContact;
        Button bNonbusiness;
        Button bColleague;
        Button bSales;
    }

    static class separatorHolder {
        TextView text;
    }

    /*
    * event handler for click on Name Wrapper layout
    * */
    public class ShowDetailsDropDown implements View.OnClickListener {

        RelativeLayout detailsLayout;
        LSContact contact;

        public ShowDetailsDropDown(LSContact contact, RelativeLayout layout) {
            this.contact = contact;
            this.detailsLayout = layout;
        }

        @Override
        public void onClick(View v) {

            if (expanded && noteDetails != null) {
                noteDetails.setVisibility(View.GONE);
                noteDetails = null;
                expanded = false;
            } else {
                noteDetails = detailsLayout;
                detailsLayout.setVisibility(View.VISIBLE);
                expanded = true;
            }
        }
    }

    /*
    * event handler for click on name
    * */
    public class ShowContactCallDetails implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent myIntent = new Intent(mContext, ContactCallDetails.class);
            myIntent.putExtra("number", (String) v.getTag());
            mContext.startActivity(myIntent);
        }
    }

    /*
    * event handler for click on name
    * */
    public class TagAContactClickListener implements View.OnClickListener {
        String number;

        public TagAContactClickListener(String number) {
            this.number = number;
        }

        @Override
        public void onClick(View v) {
            String intlNum = PhoneNumberAndCallUtils.numberToInterNationalNumber(number);
            Intent myIntent = new Intent(mContext, TagNumberAndAddFollowupActivity.class);
            myIntent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE, TagNumberAndAddFollowupActivity.LAUNCH_MODE_TAG_PHONE_NUMBER);
            myIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_TYPE, LSContact.CONTACT_TYPE_SALES);
            myIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_PHONE_NUMBER, number);
            mContext.startActivity(myIntent);
        }
    }
}