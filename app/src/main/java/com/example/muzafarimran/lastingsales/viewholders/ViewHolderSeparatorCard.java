package com.example.muzafarimran.lastingsales.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.carditems.SeparatorItem;

/**
 * Created by ibtisam on 11/2/2017.
 */

public class ViewHolderSeparatorCard extends RecyclerView.ViewHolder {

    private TextView tvSeparator;

    public ViewHolderSeparatorCard(View v) {
        super(v);

        tvSeparator = v.findViewById(R.id.tvSeparator);

    }

    public void bind(Object item, int position, Context mContext) {

        final SeparatorItem separatorItem = (SeparatorItem) item;
        tvSeparator.setText(separatorItem.text);

    }
}
