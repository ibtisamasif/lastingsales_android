package com.example.muzafarimran.lastingsales.recycleradapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.muzafarimran.lastingsales.app.ClassNames;
import com.example.muzafarimran.lastingsales.viewholders.ViewHolderAddLeadCard;
import com.example.muzafarimran.lastingsales.viewholders.ViewHolderCallCard;
import com.example.muzafarimran.lastingsales.viewholders.ViewHolderChipCard;
import com.example.muzafarimran.lastingsales.viewholders.ViewHolderConnectionsCard;
import com.example.muzafarimran.lastingsales.viewholders.ViewHolderContactHeaderBottomsheetCard;
import com.example.muzafarimran.lastingsales.viewholders.ViewHolderSocialProfileCard;
import com.example.muzafarimran.lastingsales.viewholders.ViewHolderErrorCard;
import com.example.muzafarimran.lastingsales.viewholders.ViewHolderHomeCard;
import com.example.muzafarimran.lastingsales.viewholders.ViewHolderInquiryCard;
import com.example.muzafarimran.lastingsales.viewholders.ViewHolderLoadingCard;
import com.example.muzafarimran.lastingsales.viewholders.ViewHolderMoreCard;
import com.example.muzafarimran.lastingsales.viewholders.ViewHolderNoteCard;
import com.example.muzafarimran.lastingsales.viewholders.ViewHolderReminderCard;
import com.example.muzafarimran.lastingsales.viewholders.ViewHolderSeparatorCard;
import com.example.muzafarimran.lastingsales.viewholders.ViewHolderSettingCard;
import com.example.muzafarimran.lastingsales.viewholders.ViewHolderUnlabeledCard;

import java.util.List;

/**
 * Created by ibtisam on 11/9/2016.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
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

            case ClassNames.ERROR_CLASS_NAME:
                return ClassNames.ERROR_CLASS_TYPE;

            case ClassNames.SETTING_CLASS_NAME:
                return ClassNames.SETTING_CLASS_TYPE;

            case ClassNames.LOADING_CLASS_NAME:
                return ClassNames.LOADING_CLASS_TYPE;

            case ClassNames.HOME_CLASS_NAME:
                return ClassNames.HOME_CLASS_TYPE;

            case ClassNames.NOTE_CLASS_NAME:
                return ClassNames.NOTE_CLASS_TYPE;

            case ClassNames.CALL_CLASS_NAME:
                return ClassNames.CALL_CLASS_TYPE;

            case ClassNames.REMINDER_CLASS_NAME:
                return ClassNames.REMINDER_CLASS_TYPE;

            case ClassNames.MORE_CLASS_NAME:
                return ClassNames.MORE_CLASS_TYPE;

            case ClassNames.CHIP_CLASS_NAME:
                return ClassNames.CHIP_CLASS_TYPE;

            case ClassNames.LSCONTACTS_SOCIAL_PROFILE_CLASS_NAME:
                return ClassNames.LSCONTACTS_SOCIAL_PROFILE_CLASS_TYPE;

            case ClassNames.CONNECTIONS_CLASS_NAME:
                return ClassNames.CONNECTIONS_CLASS_TYPE;

            case ClassNames.CONTACT_HEADER_BOTTOMSHEET_CLASS_NAME:
                return ClassNames.CONTACT_HEADER_BOTTOMSHEET_CLASS_TYPE;

            case ClassNames.ADD_lEAD_CLASS_NAME:
                return ClassNames.ADD_lEAD_CLASS_TYPE;

            default:
                Log.e(TAG, "getItemViewType: VIEW TYPE UNHANDLED");
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

            case ClassNames.SETTING_CLASS_TYPE:
                return new ViewHolderSettingCard(LayoutInflater.from(mContext).inflate(ClassNames.SETTING_CLASS_RESOURCE, viewGroup, false));

            case ClassNames.LOADING_CLASS_TYPE:
                return new ViewHolderLoadingCard(LayoutInflater.from(mContext).inflate(ClassNames.LOADING_CLASS_RESOURCE, viewGroup, false));

            case ClassNames.HOME_CLASS_TYPE:
                return new ViewHolderHomeCard(LayoutInflater.from(mContext).inflate(ClassNames.HOME_CLASS_RESOURCE, viewGroup, false)); //Huawei crash here

            case ClassNames.NOTE_CLASS_TYPE:
                return new ViewHolderNoteCard(LayoutInflater.from(mContext).inflate(ClassNames.NOTE_CLASS_RESOURCE, viewGroup, false));

            case ClassNames.CALL_CLASS_TYPE:
                return new ViewHolderCallCard(LayoutInflater.from(mContext).inflate(ClassNames.CALL_CLASS_RESOURCE, viewGroup, false));

            case ClassNames.REMINDER_CLASS_TYPE:
                return new ViewHolderReminderCard(LayoutInflater.from(mContext).inflate(ClassNames.REMINDER_CLASS_RESOURCE, viewGroup, false));

            case ClassNames.MORE_CLASS_TYPE:
                return new ViewHolderMoreCard(LayoutInflater.from(mContext).inflate(ClassNames.MORE_CLASS_RESOURCE, viewGroup, false));

            case ClassNames.CHIP_CLASS_TYPE:
                return new ViewHolderChipCard(LayoutInflater.from(mContext).inflate(ClassNames.CHIP_CLASS_RESOURCE, viewGroup, false));

            case ClassNames.LSCONTACTS_SOCIAL_PROFILE_CLASS_TYPE:
                return new ViewHolderSocialProfileCard(LayoutInflater.from(mContext).inflate(ClassNames.LSCONTACTS_SOCIAL_PROFILE_CLASS_RESOURCE, viewGroup, false));

            case ClassNames.CONNECTIONS_CLASS_TYPE:
                return new ViewHolderConnectionsCard(LayoutInflater.from(mContext).inflate(ClassNames.CONNECTIONS_CLASS_RESOURCE, viewGroup, false));

            case ClassNames.CONTACT_HEADER_BOTTOMSHEET_CLASS_TYPE:
                return new ViewHolderContactHeaderBottomsheetCard(LayoutInflater.from(mContext).inflate(ClassNames.CONTACT_HEADER_BOTTOMSHEET_CLASS_RESOURCE, viewGroup, false));

            case ClassNames.ADD_lEAD_CLASS_TYPE:
                return new ViewHolderAddLeadCard(LayoutInflater.from(mContext).inflate(ClassNames.ADD_lEAD_CLASS_RESOURCE, viewGroup, false));
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
            case ClassNames.SETTING_CLASS_TYPE:
                ViewHolderSettingCard viewHolderSettingCard = (ViewHolderSettingCard) holder;
                viewHolderSettingCard.bind(item, position, mContext);
                break;
            case ClassNames.LOADING_CLASS_TYPE:
                ViewHolderLoadingCard viewHolderLoadingCard = (ViewHolderLoadingCard) holder;
                viewHolderLoadingCard.bind(item, position, mContext);
                break;
            case ClassNames.HOME_CLASS_TYPE:
                ViewHolderHomeCard viewHolderHomeCard = (ViewHolderHomeCard) holder;
                viewHolderHomeCard.bind(item, position, mContext);
                break;
            case ClassNames.NOTE_CLASS_TYPE:
                ViewHolderNoteCard viewHolderNoteCard = (ViewHolderNoteCard) holder;
                viewHolderNoteCard.bind(item, position, mContext);
                break;
            case ClassNames.CALL_CLASS_TYPE:
                ViewHolderCallCard viewHolderCallCard = (ViewHolderCallCard) holder;
                viewHolderCallCard.bind(item, position, mContext);
                break;
            case ClassNames.REMINDER_CLASS_TYPE:
                ViewHolderReminderCard viewHolderReminderCard = (ViewHolderReminderCard) holder;
                viewHolderReminderCard.bind(item, position, mContext);
                break;
            case ClassNames.MORE_CLASS_TYPE:
                ViewHolderMoreCard viewHolderMoreCard = (ViewHolderMoreCard) holder;
                viewHolderMoreCard.bind(item, position, mContext);
                break;
            case ClassNames.CHIP_CLASS_TYPE:
                ViewHolderChipCard viewHolderChipCard = (ViewHolderChipCard) holder;
                viewHolderChipCard.bind(item, position, mContext);
                break;
            case ClassNames.LSCONTACTS_SOCIAL_PROFILE_CLASS_TYPE:
                ViewHolderSocialProfileCard viewHolderSocialProfileCard = (ViewHolderSocialProfileCard) holder;
                viewHolderSocialProfileCard.bind(item, position, mContext);
                break;
            case ClassNames.CONNECTIONS_CLASS_TYPE:
                ViewHolderConnectionsCard viewHolderConnectionsCard = (ViewHolderConnectionsCard) holder;
                viewHolderConnectionsCard.bind(item, position, mContext);
                break;
            case ClassNames.CONTACT_HEADER_BOTTOMSHEET_CLASS_TYPE:
                ViewHolderContactHeaderBottomsheetCard viewHolderContactHeaderBottomsheetCard = (ViewHolderContactHeaderBottomsheetCard) holder;
                viewHolderContactHeaderBottomsheetCard.bind(item, position, mContext);
                break;
            case ClassNames.ADD_lEAD_CLASS_TYPE:
                ViewHolderAddLeadCard viewHolderAddLeadCard = (ViewHolderAddLeadCard) holder;
                viewHolderAddLeadCard.bind(item, position, mContext);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mItem.size();
    }
}