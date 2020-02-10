package com.example.muzafarimran.lastingsales.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.carditems.LoadingItem;
import com.example.muzafarimran.lastingsales.carditems.SeparatorItem;

/**
 * Created by ibtisam on 11/2/2017.
 */

public class ViewHolderLoadingCard extends RecyclerView.ViewHolder {

//    private ProgressBar progressBar;
    private TextView tvLoading;


    public ViewHolderLoadingCard(View v) {
        super(v);

//        progressBar = v.findViewById(R.id.progressBar);
        tvLoading = v.findViewById(R.id.tvLoading);

    }

    public void bind(Object item, int position, Context mContext) {

        final LoadingItem loadingItem = (LoadingItem) item;
        tvLoading.setText(loadingItem.text);

    }
}
