package com.example.muzafarimran.lastingsales.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.carditems.ChipItem;
import com.example.muzafarimran.lastingsales.listeners.ChipClickListener;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import java.util.Collection;
import java.util.List;

/**
 * Created by ibtisam on 11/2/2017.
 */

public class ViewHolderChipCard extends RecyclerView.ViewHolder {

    private Button bAll;
    private Button bInProgress;
    private Button bWon;
    private Button bLost;
    private Button bInActive;

    ChipClickListener chipClickListener;

    public ViewHolderChipCard(View v) {
        super(v);
        bAll = v.findViewById(R.id.bAll);
        bInProgress = v.findViewById(R.id.bInProgress);
        bWon = v.findViewById(R.id.bWon);
        bLost = v.findViewById(R.id.bLost);
        bInActive = v.findViewById(R.id.bInActive);
    }

    public void bind(Object item, int position, Context mContext) {
        final ChipItem chipItem = (ChipItem) item;
        chipClickListener = (ChipClickListener) mContext;

        Collection<LSContact> contacts = LSContact.getDateArrangedSalesContacts();
        if (contacts != null) {
            bAll.setText("ALL" + "(" + contacts.size() + ")");
        }
        bAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                chipItem.selected = 1;
                bAll.setBackground(mContext.getResources().getDrawable(R.drawable.shape_chip_selected));
                bInProgress.setBackground(mContext.getResources().getDrawable(R.drawable.shape_chip_normal));
                bWon.setBackground(mContext.getResources().getDrawable(R.drawable.shape_chip_normal));
                bLost.setBackground(mContext.getResources().getDrawable(R.drawable.shape_chip_normal));
                bAll.setBackground(mContext.getResources().getDrawable(R.drawable.shape_chip_normal));
                bInActive.setBackground(mContext.getResources().getDrawable(R.drawable.shape_chip_normal));
                chipClickListener.onChipClick("All");
//                Toast.makeText(mContext, "bAll Leads", Toast.LENGTH_SHORT).show();
            }
        });

        List<LSContact> contactsInprogress = LSContact.getDateArrangedSalesContactsByLeadSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
        if (contactsInprogress != null) {
            bInProgress.setText("INPROGRESS" + "(" + contactsInprogress.size() + ")");
        }
        bInProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                chipItem.selected = 2;
                bAll.setBackground(mContext.getResources().getDrawable(R.drawable.shape_chip_normal));
                bInProgress.setBackground(mContext.getResources().getDrawable(R.drawable.shape_chip_selected));
                bWon.setBackground(mContext.getResources().getDrawable(R.drawable.shape_chip_normal));
                bLost.setBackground(mContext.getResources().getDrawable(R.drawable.shape_chip_normal));
                bAll.setBackground(mContext.getResources().getDrawable(R.drawable.shape_chip_normal));
                bInActive.setBackground(mContext.getResources().getDrawable(R.drawable.shape_chip_normal));
                chipClickListener.onChipClick("InProgress");
//                Toast.makeText(mContext, "bInProgress Leads", Toast.LENGTH_SHORT).show();
            }
        });

        List<LSContact> contactsWon = LSContact.getDateArrangedSalesContactsByLeadSalesStatus(LSContact.SALES_STATUS_CLOSED_WON);
        if (contactsWon != null) {
            bWon.setText("WON" + "(" + contactsWon.size() + ")");
        }
        bWon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                chipItem.selected = 3;
                bAll.setBackground(mContext.getResources().getDrawable(R.drawable.shape_chip_normal));
                bInProgress.setBackground(mContext.getResources().getDrawable(R.drawable.shape_chip_normal));
                bWon.setBackground(mContext.getResources().getDrawable(R.drawable.shape_chip_selected));
                bLost.setBackground(mContext.getResources().getDrawable(R.drawable.shape_chip_normal));
                bInActive.setBackground(mContext.getResources().getDrawable(R.drawable.shape_chip_normal));
                chipClickListener.onChipClick("Won");
//                Toast.makeText(mContext, "bWon Leads", Toast.LENGTH_SHORT).show();
            }
        });

        List<LSContact> contactsLost = LSContact.getDateArrangedSalesContactsByLeadSalesStatus(LSContact.SALES_STATUS_CLOSED_LOST);
        if (contactsLost != null) {
            bLost.setText("LOST" + "(" + contactsLost.size() + ")");
        }
        bLost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                chipItem.selected = 4;
                bAll.setBackground(mContext.getResources().getDrawable(R.drawable.shape_chip_normal));
                bInProgress.setBackground(mContext.getResources().getDrawable(R.drawable.shape_chip_normal));
                bWon.setBackground(mContext.getResources().getDrawable(R.drawable.shape_chip_normal));
                bLost.setBackground(mContext.getResources().getDrawable(R.drawable.shape_chip_selected));
                bInActive.setBackground(mContext.getResources().getDrawable(R.drawable.shape_chip_normal));
                chipClickListener.onChipClick("Lost");
//                Toast.makeText(mContext, "bLost Leads", Toast.LENGTH_SHORT).show();
            }
        });

        Collection<LSContact> contactsInactive = LSContact.getAllInactiveLeadContacts();
        if (contactsInactive != null) {
            bInActive.setText("INACTIVE" + "(" + contactsInactive.size() + ")");
        }
        bInActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                chipItem.selected = 5;
                bAll.setBackground(mContext.getResources().getDrawable(R.drawable.shape_chip_normal));
                bInProgress.setBackground(mContext.getResources().getDrawable(R.drawable.shape_chip_normal));
                bWon.setBackground(mContext.getResources().getDrawable(R.drawable.shape_chip_normal));
                bLost.setBackground(mContext.getResources().getDrawable(R.drawable.shape_chip_normal));
                bInActive.setBackground(mContext.getResources().getDrawable(R.drawable.shape_chip_selected));
                chipClickListener.onChipClick("InActive");
//                Toast.makeText(mContext, "bInActive Leads", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
