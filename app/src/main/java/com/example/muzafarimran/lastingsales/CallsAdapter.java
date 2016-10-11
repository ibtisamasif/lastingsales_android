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

    private showCallDetailsListener showcalldetailslistener = null;


    public CallsAdapter(Context c, List<Call> call_logs)
    {
        this.mContext = c;
        this.mCalls = call_logs;
        this.callClickListener = new CallClickListener(c);
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.showcalldetailslistener = new showCallDetailsListener();
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

            seperatorHolder seperatorholder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.section_seperator, parent, false);
                seperatorholder = new seperatorHolder();
                seperatorholder.text = (TextView) convertView.findViewById(R.id.section_seperator);

                convertView.setTag(seperatorholder);

            }else{
                seperatorholder = (seperatorHolder) convertView.getTag();
            }

            seperatorholder.text.setText(mCalls.get(position).getName());


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
                holder.name.setOnClickListener(this.showcalldetailslistener);

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

    /*
    * event handler for click on name
    * */
    public class showCallDetailsListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {


            View view = mInflater.inflate(R.layout.call_detail_drop_down, null);

            // fill in any details dynamically here
            TextView textView = (TextView) view.findViewById(R.id.call_number);
            textView.setText("03234433108");

            // insert into main view
            ViewGroup insertPoint = (ViewGroup) ((ViewGroup)v.getParent()).findViewById(R.id.call_row);
            insertPoint.addView(view, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        }
    }

    static class ViewHolder {
        TextView name;
        TextView time;
        ImageView call_icon;
        ImageView missed_call_icon;
    }

    static class seperatorHolder{
        TextView text;
    }

    private boolean isSeparator(int position) {
        return mCalls.get(position).getType() == "seperator";
    }
}
