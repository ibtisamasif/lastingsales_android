package com.example.muzafarimran.lastingsales;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by MUZAFAR IMRAN on 9/19/20
 */
public class CallsAdapter extends BaseAdapter{
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Call> mCalls;

    public CallsAdapter(Context c, ArrayList<Call> call_logs)
    {
        this.mContext = c;
        this.mCalls = call_logs;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        return mCalls.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mCalls.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.calls_text_view, parent, false);

            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.call_name);
            holder.time = (TextView) convertView.findViewById(R.id.call_time);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        Call call = (Call) getItem(position);
        holder.name.setText(call.getName());
        holder.time.setText(call.getTime());

        return convertView;
    }

    static class ViewHolder {
        TextView name;
        TextView time;
    }
}
