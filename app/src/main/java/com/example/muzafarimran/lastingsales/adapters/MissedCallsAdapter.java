package com.example.muzafarimran.lastingsales.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import com.example.muzafarimran.lastingsales.CallClickListener;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.ContactCallDetails;
import com.example.muzafarimran.lastingsales.activities.TagNumberAndAddFollowupActivity;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.view.View.GONE;

/**
 * Created by MUZAFAR IMRAN on 9/19/20
 */
public class MissedCallsAdapter extends BaseAdapter implements Filterable {

    private final static int TYPE_SEPARATOR = 0;
    private final static int TYPE_ITEM = 1;
    private final static int ITEM_TYPES = 1;
    public Context mContext;
    public ShowContactCallDetails detailsListener = null;
    Boolean expanded = false;
    RelativeLayout noteDetails = null;
    View call_details = null; //TODO move to handler class below
    private LayoutInflater mInflater;
    private List<LSCall> mCalls;
    private CallClickListener callClickListener = null;
    private ShowDetailsDropDown showcalldetailslistener = null;
    private List<LSCall> filteredData;

    public MissedCallsAdapter(Context c) {
        this.mContext = c;
        if (mCalls == null) {
            mCalls = new ArrayList<>();
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
    public int getItemViewType(int position) {
        return TYPE_ITEM;
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
        LSCall call = (LSCall) getItem(position);
        String number = call.getContactNumber();
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.missed_calls_list_item, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.call_name);
            holder.time = (TextView) convertView.findViewById(R.id.call_time);
            holder.call_icon = (ImageView) convertView.findViewById(R.id.call_icon);
            holder.call_name_time = (RelativeLayout) convertView.findViewById(R.id.user_call_group_wrapper);
            holder.numberDetailTextView = (TextView) convertView.findViewById(R.id.call_number);
            holder.bContactCallsdetails = (Button) convertView.findViewById(R.id.bNonBusinessUntaggedItem);
            holder.contactCallDetails = (RelativeLayout) convertView.findViewById(R.id.rl_calls_details);
            holder.inquireyCount = (TextView) convertView.findViewById(R.id.inquireyCount);
            this.showcalldetailslistener = new ShowDetailsDropDown(call, holder.contactCallDetails);
            holder.bTag = (Button) convertView.findViewById(R.id.call_tag_btn);
            holder.call_icon.setOnClickListener(this.callClickListener);
            holder.call_name_time.setOnClickListener(this.showcalldetailslistener);
            if (call.getContact() != null) {
                holder.bTag.setVisibility(GONE);
            }
            holder.contactCallDetails.setVisibility(GONE);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            ((ViewGroup) holder.call_name_time.getParent().getParent()).removeView(call_details);
        }
        if (call.getContact() == null) {
            if (call.getContactName() != null) {
                holder.name.setText(call.getContactName());
            } else {
                holder.name.setText(call.getContactNumber());
            }
        } else {
            if (call.getContact().getContactName() == null) {
                holder.name.setText(call.getContactNumber());
            } else {
                holder.name.setText(call.getContact().getContactName());
            }
        }
        holder.bContactCallsdetails.setTag(number);
        holder.bContactCallsdetails.setOnClickListener(detailsListener);
        holder.numberDetailTextView.setText(number);
        holder.call_name_time.setTag(position);

        long callTimeMillis = call.getBeginTime();
        long now = Calendar.getInstance().getTimeInMillis();
        long agoTimestamp = now - callTimeMillis;

        if (agoTimestamp > 300000) {
            holder.time.setTextColor(Color.parseColor("#ff0000"));
        }
        else
        {
            holder.time.setTextColor(Color.parseColor("#d0898989"));
        }
        holder.time.setText(PhoneNumberAndCallUtils.getTimeAgo(call.getBeginTime(), mContext));
        holder.call_icon.setTag(mCalls.get(position).getContactNumber());
        holder.bTag.setOnClickListener(new TagAContactClickListener(number));
        if (call.getCountOfInquiries() > 0) {
            holder.inquireyCount.setText(call.getCountOfInquiries()+"");
        }

        return convertView;
    }

    public void setList(List<LSCall> mCalls) {
        this.mCalls = mCalls;
        filteredData = mCalls;
        notifyDataSetChanged();
    }

    // for searching
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                //If there's nothing to filter on, return the original data for list
                if (charSequence == null || charSequence.length() == 0) {
                    results.values = mCalls;
                    results.count = mCalls.size();
                } else {
                    List<LSCall> filterResultsData = new ArrayList<>();
                    //int length = charSequence.length();
                    for (int i = 0; i < mCalls.size(); i++) {
                        LSCall oneCall = mCalls.get(i);
//                        if (mCalls.get(i).getContactName().toLowerCase().startsWith(((String) charSequence).toLowerCase()) && mCalls.get(i).getContact().getContactName().toLowerCase().startsWith(((String) charSequence).toLowerCase())) {
                        if (oneCall.getContactName() != null) {
                            if (oneCall.getContactName().toLowerCase().contains(((String) charSequence).toLowerCase())) {
                                filterResultsData.add(mCalls.get(i));
                                continue;
                            }
                        }
                        if (oneCall.getContactNumber().contains(((String) charSequence).toLowerCase())) {
                            filterResultsData.add(mCalls.get(i));
                            continue;
                        }
                        if (oneCall.getContact() != null) {
                            if (oneCall.getContact().getContactName().contains(((String) charSequence).toLowerCase())) {
                                filterResultsData.add(mCalls.get(i));
                                continue;
                            }
                        }
                    }
                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                if (((List<LSCall>) filterResults.values) != null) {
                    filteredData = ((List<LSCall>) filterResults.values);
                }
//                filteredData = ((List<LSCall>) filterResults.values);
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
        Button bContactCallsdetails;
        Button bTag;
        TextView inquireyCount;
    }

    /*
    * event handler for click on Name Wrapper layout
    * */
    public class ShowDetailsDropDown implements View.OnClickListener {

        RelativeLayout detailsLayout;
        LSCall call;

        public ShowDetailsDropDown(LSCall call, RelativeLayout layout) {
            this.call = call;
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