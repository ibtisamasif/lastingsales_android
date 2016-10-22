package com.example.muzafarimran.lastingsales;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.ImageView;

import android.widget.TextView;

import java.util.List;

/**
 * Created by lenovo 1 on 10/22/2016.
 */
public class IndividualConatactCallAdapter extends BaseAdapter {

        public Context mContext;
        private LayoutInflater mInflater;
        private List<Call> mCalls;
        private String callsType = "";

        public IndividualConatactCallAdapter(Context c, List<Call> call_logs, String callsType)
        {
            this.mContext = c;
            this.mCalls = call_logs;
           // this.callClickListener = new CallClickListener(c);
            this.callsType = callsType;
            this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


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

            Call call = (Call) getItem(position);


            ViewHolder holder = null;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.ind_call_view, parent, false);

                holder = new ViewHolder();
                holder.time_passed = (TextView) convertView.findViewById(R.id.call_time_passed);
                holder.time = (TextView) convertView.findViewById(R.id.call_time);
                holder.call_icon = (ImageView) convertView.findViewById(R.id.ind_call_icon);

                switch (this.callsType){
                    case "missed":
                        holder.call_icon.setImageResource(R.drawable.missed_call_icon_ind);

                        break;

                    case "incoming":
                        holder.call_icon.setImageResource(R.drawable.call_icon_incoming_ind);

                        break;
                }




                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            holder.time.setText(call.getTime());

            //TODO calculate the time difference
            holder.time_passed.setText("04:00");



            return convertView;
        }





static class ViewHolder {
    TextView time_passed;
    TextView time;
    ImageView call_icon;

}


}
