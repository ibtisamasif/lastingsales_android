package com.example.muzafarimran.lastingsales.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.carditems.TryAgainItem;
import com.example.muzafarimran.lastingsales.events.CommentEventModel;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 11/2/2017.
 */

public class ViewHolderTryAgainCard extends RecyclerView.ViewHolder {

    private TextView tvError;
    private ImageView imErrorImage;
    private Button bTryAgain;


    public ViewHolderTryAgainCard(View v) {
        super(v);

        tvError = v.findViewById(R.id.tvError);
        imErrorImage = v.findViewById(R.id.imErrorImage);
        bTryAgain = v.findViewById(R.id.bTryAgain);

    }

    public void bind(Object item, int position, Context mContext) {

        final TryAgainItem errorItem = (TryAgainItem) item;
        tvError.setText(errorItem.message);
        imErrorImage.setImageResource(errorItem.drawable);
        bTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TinyBus.from(mContext.getApplicationContext()).post(new CommentEventModel());
            }
        });

    }
}
