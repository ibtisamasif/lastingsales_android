package com.example.muzafarimran.lastingsales.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by ibtisam on 11/9/2016.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String TAG = "MyRecyclerViewAdapter";
    private List<?> mItem;
    private Context mContext;

    public MyRecyclerViewAdapter(Context context, List<?> item) {
        this.mItem = item;
        this.mContext = context;
    }

    @Override
    public int getItemViewType(int position) {

        Object item = mItem.get(position);
        String className = item.getClass().getName();
        switch (className) {
            case ClassNames.LSCONTACTS_CLASS_NAME:
                return ClassNames.LSCONTACTS_CLASS_TYPE;

            case ClassNames.LSINQUIRY_CLASS_NAME:
                return ClassNames.LSINQUIRY_CLASS_TYPE;

            case ClassNames.SEPARATOR_CLASS_NAME:
                return ClassNames.SEPARATOR_CLASS_TYPE;

            default:
                return -1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Log.d(TAG, "onCreateViewHolder: viewType = " + viewType);
        switch (viewType) {
            case ClassNames.LSCONTACTS_CLASS_TYPE:
                return new ViewHolderUnlabeledCard(LayoutInflater.from(mContext).inflate(ClassNames.LSCONTACTS_CLASS_RESOURCE, viewGroup, false));

            case ClassNames.LSINQUIRY_CLASS_TYPE:
                return new ViewHolderInquiryCard(LayoutInflater.from(mContext).inflate(ClassNames.LSINQUIRY_CLASS_RESOURCE, viewGroup, false));

            case ClassNames.SEPARATOR_CLASS_TYPE:
                return new ViewHolderSeparatorCard(LayoutInflater.from(mContext).inflate(ClassNames.SEPARATOR_CLASS_RESOURCE, viewGroup, false));

            case ClassNames.ERROR_CLASS_TYPE:
                return new ViewHolderErrorCard(LayoutInflater.from(mContext).inflate(ClassNames.ERROR_CLASS_RESOURCE, viewGroup, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Object item = mItem.get(position);

//        int type = item.type;
        switch (holder.getItemViewType()) {
            case ClassNames.LSCONTACTS_CLASS_TYPE:
                ViewHolderUnlabeledCard viewHolderUnlabeledCard = (ViewHolderUnlabeledCard) holder;
                viewHolderUnlabeledCard.bind(item, position, mContext);
                break;
            case ClassNames.LSINQUIRY_CLASS_TYPE:
                ViewHolderInquiryCard viewHolderInquiryCard = (ViewHolderInquiryCard) holder;
                viewHolderInquiryCard.bind(item, position, mContext);
                break;
            case ClassNames.SEPARATOR_CLASS_TYPE:
                ViewHolderSeparatorCard viewHolderSeparatorCard = (ViewHolderSeparatorCard) holder;
                viewHolderSeparatorCard.bind(item, position, mContext);
                break;
            case ClassNames.ERROR_CLASS_TYPE:
                ViewHolderErrorCard viewHolderErrorCard = (ViewHolderErrorCard) holder;
                viewHolderErrorCard.bind(item, position, mContext);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mItem.size();
    }
}