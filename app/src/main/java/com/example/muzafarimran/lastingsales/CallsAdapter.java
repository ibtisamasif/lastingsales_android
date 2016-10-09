package com.example.muzafarimran.lastingsales;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MUZAFAR IMRAN on 9/19/20
 */
public class CallsAdapter extends BaseAdapter{

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Call> mCalls;
    private CallClickListener callClickListener = null;
    private final static int TYPE_SEPARATOR = 0;
    private final static int TYPE_ITEM = 1;
    private final static int ITEM_TYPES = 2;


    public CallsAdapter(Context c, List<Call> call_logs)
    {
        this.mContext = c;
        this.mCalls = call_logs;
        this.callClickListener = new CallClickListener(c);
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getViewTypeCount() { return ITEM_TYPES; }

    @Override
    public int getItemViewType(int position) { return isSeparator(position) ? TYPE_SEPARATOR : TYPE_ITEM; }

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

        Call call = (Call) getItem(position);

        if (isSeparator(position)){

            seperatorHolder seperatorHolder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.section_seperator, parent, false);
                seperatorHolder = new seperatorHolder();
                seperatorHolder.text = (TextView) convertView.findViewById(R.id.section_seperator);

                convertView.setTag(seperatorHolder);

            }else{
                seperatorHolder = (seperatorHolder) convertView.getTag();
            }

            seperatorHolder.text.setText(mCalls.get(position).getName());


        }

        else {

            ViewHolder holder = null;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.calls_text_view, parent, false);

                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.call_name);
                holder.time = (TextView) convertView.findViewById(R.id.call_time);
                holder.call_icon = (ImageView) convertView.findViewById(R.id.call_icon);

                holder.call_icon.setOnClickListener(this.callClickListener);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.name.setText(call.getName());
            holder.time.setText(call.getTime());

            holder.call_icon.setTag(mCalls.get(position).getNumber());

        }

        return convertView;
    }

    static class ViewHolder {
        TextView name;
        TextView time;
        ImageView call_icon;
    }

    static class seperatorHolder{
        TextView text;
    }

    private boolean isSeparator(int position) {
        return mCalls.get(position).getType() == "seperator";
    }
}
