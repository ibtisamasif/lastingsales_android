package com.example.muzafarimran.lastingsales.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;

import java.util.List;

/**
 * Created by lenovo 1 on 10/22/2016.
 */
public class IndividualContactCallAdapter extends BaseAdapter {

    public Context mContext;
    private LayoutInflater mInflater;
    private List<LSCall> mCalls;

    public IndividualContactCallAdapter(Context c, List<LSCall> call_logs) {
        this.mContext = c;
        this.mCalls = call_logs;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setList(List<LSCall> mCalls) {
        this.mCalls = mCalls;
        notifyDataSetChanged();
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
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.ind_call_view, parent, false);
            holder = new ViewHolder();
            holder.time_passed = (TextView) convertView.findViewById(R.id.call_time_passed);
            holder.time = (TextView) convertView.findViewById(R.id.call_time);
            holder.call_icon = (ImageView) convertView.findViewById(R.id.ind_call_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.time.setText(PhoneNumberAndCallUtils.getTimeAgo(call.getBeginTime(), mContext));
        //TODO calculate the time difference
        holder.time_passed.setText(PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(call.getBeginTime()));
        switch (call.getType()) {
            case "missed":
                holder.call_icon.setImageResource(R.drawable.missed_call_icon_ind);
                break;
            case "incoming":
                holder.call_icon.setImageResource(R.drawable.call_icon_incoming_ind);
                break;
            case "outgoing":
                holder.call_icon.setImageResource(R.drawable.call_icon_out_going_ind);
                break;
        }
        return convertView;
    }

    static class ViewHolder {
        TextView time_passed;
        TextView time;
        ImageView call_icon;
    }
}