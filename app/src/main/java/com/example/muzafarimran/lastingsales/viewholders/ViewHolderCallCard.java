package com.example.muzafarimran.lastingsales.viewholders;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;

import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

/**
 * Created by ibtisam on 11/1/2017.
 */

public class ViewHolderCallCard extends RecyclerView.ViewHolder {
    public static final String TAG = "ViewHolderCallCard";
    private TextView call_type;
    private TextView tvDuration;
    private TextView time_passed;
    private TextView time;
    private ImageView call_icon;


    public ViewHolderCallCard(View view) {
        super(view);
        call_type = (TextView) view.findViewById(R.id.call_type);
        tvDuration = (TextView) view.findViewById(R.id.tvDuration);
        time_passed = (TextView) view.findViewById(R.id.call_time_passed);
        time = (TextView) view.findViewById(R.id.call_time);
        call_icon = (ImageView) view.findViewById(R.id.ind_call_icon);
    }

    public void bind(Object item, int position, Context mContext) {
        LSCall call = (LSCall) item;
        if (call.getType().equals("unanswered") || call.getType().equals("outgoing") || call.getType().equals("incoming")){
            String duration = String.format("%s",call.getDuration()); // TODO duration is only in seconds yet
            tvDuration.setText(duration+"s");
        }
        time.setText(PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(call.getBeginTime()));
        time_passed.setText(PhoneNumberAndCallUtils.getTimeAgo(call.getBeginTime(), mContext));
        switch (call.getType()) {
            case "missed":
                call_type.setText("Missed");
                call_icon.setImageResource(R.drawable.missed_call_icon_ind);
                break;
            case "rejected":
                call_type.setText("Rejected");
                call_icon.setImageResource(R.drawable.call_icon_incoming_ind);
                break;
            case "incoming":
                call_type.setText("Incoming");
                call_icon.setImageResource(R.drawable.call_icon_incoming_ind);
                break;
            case "outgoing":
                call_type.setText("Outgoing");
                call_icon.setImageResource(R.drawable.call_icon_out_going_ind);
                break;
            case "unanswered":
                call_type.setText("Unanswered");
                call_icon.setImageResource(R.drawable.call_icon_out_going_ind);
                break;
        }
    }
}
