package com.example.muzafarimran.lastingsales.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.CallClickListener;
import com.example.muzafarimran.lastingsales.Utils.PhoneNumberAndCallUtils;
import com.example.muzafarimran.lastingsales.activities.AddContactActivity;
import com.example.muzafarimran.lastingsales.activities.ContactCallDetails;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;

import java.util.List;

import static android.view.View.GONE;

/**
 * Created by MUZAFAR IMRAN on 9/19/20
 */
public class CallsAdapter extends BaseAdapter {

    public Context mContext;
    private LayoutInflater mInflater;
    private List<LSCall> mCalls;
    private CallClickListener callClickListener = null;
    private final static int TYPE_SEPARATOR = 0;
    private final static int TYPE_ITEM = 1;
    private final static int ITEM_TYPES = 2;
    Boolean expanded = false;
    RelativeLayout noteDetails = null;

    View call_details = null; //TODO move to handler class below

    private ShowDetailsDropDown showcalldetailslistener = null;
    public ShowContactCallDetails detailsListener = null;


    public CallsAdapter(Context c) {
        this.mContext = c;

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
        return isSeparator(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getCount() {
        return mCalls.size();
    }

    @Override
    public Object getItem(int position) {
        return mCalls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LSCall call = (LSCall) getItem(position);
        String number = call.getContactNumber();


        if (isSeparator(position)) {

            separatorHolder separatorholder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.section_separator, parent, false);
                separatorholder = new separatorHolder();
                separatorholder.text = (TextView) convertView.findViewById(R.id.section_separator);

                convertView.setTag(separatorholder);

            } else {
                separatorholder = (separatorHolder) convertView.getTag();
            }

//            separatorholder.text.setText(mCalls.get(position).getName());


        } else {

            ViewHolder holder = null;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.calls_text_view, parent, false);

                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.call_name);
                holder.time = (TextView) convertView.findViewById(R.id.call_time);
                holder.call_icon = (ImageView) convertView.findViewById(R.id.call_icon);
                holder.call_name_time = (RelativeLayout) convertView.findViewById(R.id.user_call_group_wrapper);

                holder.numberDetailTextView = (TextView) convertView.findViewById(R.id.call_number);
                holder.bContactCallsdetails = (Button) convertView.findViewById(R.id.call_details_btn);
                holder.contactCallDetails = (RelativeLayout) convertView.findViewById(R.id.rl_calls_details);
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

                holder.name.setText(call.getContactNumber());
            } else {
                holder.name.setText(call.getContact().getContactName());
            }

            holder.bContactCallsdetails.setTag(number);
            holder.bContactCallsdetails.setOnClickListener(detailsListener);
            holder.numberDetailTextView.setText(number);
            holder.call_name_time.setTag(position);
            holder.time.setText(PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(call.getBeginTime()));
            holder.call_icon.setTag(mCalls.get(position).getContactNumber());
            holder.bTag.setOnClickListener(new TagAContactClickListener(number) );

        }

        return convertView;
    }

    public void setList(List<LSCall> missedCalls) {
        mCalls = missedCalls;
        notifyDataSetChanged();
    }

    /*
    * event handler for click on Name Wrapper layout
    * */
    public class ShowDetailsDropDown implements View.OnClickListener {

        RelativeLayout detailsLayout;
        LSCall call;




        public ShowDetailsDropDown(LSCall call,  RelativeLayout layout) {
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
            Intent myIntent = new Intent(mContext, AddContactActivity.class);
            myIntent.putExtra(ContactCallDetails.NUMBER_EXTRA, number);
            mContext.startActivity(myIntent);
        }

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
    }

    static class separatorHolder {
        TextView text;
    }

    private boolean isSeparator(int position) {
        return mCalls.get(position).getType() == "separator";
    }
}
