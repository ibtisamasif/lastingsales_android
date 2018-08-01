package com.example.muzafarimran.lastingsales.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
    private TextView call_time;
    private ImageView call_icon;


    public ViewHolderCallCard(View view) {
        super(view);
        call_type = (TextView) view.findViewById(R.id.call_type);
        tvDuration = (TextView) view.findViewById(R.id.tvDuration);
        time_passed = (TextView) view.findViewById(R.id.call_time_passed);
        call_time = (TextView) view.findViewById(R.id.call_time);
        call_icon = (ImageView) view.findViewById(R.id.ind_call_icon);
    }

    public static Long roundDown5(Long d) {
        return (Long) (d * 100000) / 100000;
    }

    public void bind(Object item, int position, Context mContext) {
        LSCall call = (LSCall) item;
        if (call.getType().equals("unanswered") || call.getType().equals("outgoing") || call.getType().equals("incoming")) {
            String duration = String.format("%s", call.getDuration());
            tvDuration.setText(duration + "s");
        }

//        String date = PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(call.getBeginTime(), "yyyy-MM-dd");
//        String time = PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(call.getBeginTime(), "HH:mm:ss");
////        long dateMillis = PhoneNumberAndCallUtils.getMillisFromSqlFormattedDate(date, "yyyy-MM-dd");
////        long timeMillis = PhoneNumberAndCallUtils.getMillisFromSqlFormattedDate(time, "HH:mm:ss");
//        String dateTime = date + " " + time;

        call_time.setText(PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(call.getBeginTime(), "yyyy-MM-dd HH:mm:ss"));

//        Log.d(TAG, "MILLIS: call.getBegi(): " + roundDown5(call.getBeginTime()));
//        Log.d(TAG, "date: " + date);
//        Log.d(TAG, "time: " + time);
////        Log.d(TAG, "MILLIS: MillisFromDate: " + dateMillis);
////        Log.d(TAG, "MILLIS: MillisFromTime: " + timeMillis);
//        Log.d(TAG, "Date   +   time:        " + dateTime);
//        Log.d(TAG, "SQL:   Date   +   time: " + PhoneNumberAndCallUtils.getMillisFromSqlFormattedDateAndTime(dateTime));
//        Log.d(TAG, "dateMillis +timeMillis: " + PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(PhoneNumberAndCallUtils.getMillisFromSqlFormattedDateAndTime(date + " " + time), "yyyy-MM-dd HH:mm:ss"));
//        if(PhoneNumberAndCallUtils.compareDateTimeInMillis(call.getBeginTime(), PhoneNumberAndCallUtils.getMillisFromSqlFormattedDateAndTime(dateTime))){
//            Log.d(TAG, "bind: HURRRAYYYYY MATCHED");
//        }else {
//            Log.d(TAG, "bind: NOT MATCHED");
//        }
//        Log.d(TAG, "+++++++++++++++++++++++++++++++++++++++++");

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
