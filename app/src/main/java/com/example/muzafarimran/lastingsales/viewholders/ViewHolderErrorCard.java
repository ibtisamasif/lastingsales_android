package com.example.muzafarimran.lastingsales.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.carditems.ErrorItem;

/**
 * Created by ibtisam on 11/2/2017.
 */

public class ViewHolderErrorCard extends RecyclerView.ViewHolder {

    private TextView tvError;
    private ImageView imErrorImage;


    public ViewHolderErrorCard(View v) {
        super(v);

        tvError = v.findViewById(R.id.tvError);
        imErrorImage = v.findViewById(R.id.imErrorImage);

    }

    public void bind(Object item, int position, Context mContext) {

        final ErrorItem errorItem = (ErrorItem) item;
        tvError.setText(errorItem.message);
        imErrorImage.setImageResource(errorItem.drawable);

    }
}
