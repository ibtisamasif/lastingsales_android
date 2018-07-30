package com.example.muzafarimran.lastingsales.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.carditems.HomeItem;

/**
 * Created by ibtisam on 11/2/2017.
 */

public class ViewHolderHomeCard extends RecyclerView.ViewHolder {

    private final ImageView ivDrawable;
    private TextView tvValue;
    private TextView tvHome;


    public ViewHolderHomeCard(View v) {
        super(v);
        tvValue = v.findViewById(R.id.tvValue);
        tvHome = v.findViewById(R.id.tvHome);
        ivDrawable = v.findViewById(R.id.ivDrawable);

    }

    public void bind(Object item, int position, Context mContext) {
        final HomeItem homeItem = (HomeItem) item;
        tvValue.setText(homeItem.value);
        tvHome.setText(homeItem.text);
        ivDrawable.setImageResource(homeItem.drawable);
    }
}
