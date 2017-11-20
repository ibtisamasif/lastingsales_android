package com.example.muzafarimran.lastingsales.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.NavigationBottomMainActivity;
import com.example.muzafarimran.lastingsales.carditems.ChipItem;
import com.example.muzafarimran.lastingsales.carditems.SeparatorItem;
import com.example.muzafarimran.lastingsales.listeners.ChipClickListener;

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

        bAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chipClickListener.onChipClick("All");
//                Toast.makeText(mContext, "bAll Leads", Toast.LENGTH_SHORT).show();
            }
        });

        bInProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chipClickListener.onChipClick("InProgress");
//                Toast.makeText(mContext, "bInProgress Leads", Toast.LENGTH_SHORT).show();
            }
        });

        bWon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chipClickListener.onChipClick("Won");
//                Toast.makeText(mContext, "bWon Leads", Toast.LENGTH_SHORT).show();
            }
        });

        bLost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chipClickListener.onChipClick("Lost");
//                Toast.makeText(mContext, "bLost Leads", Toast.LENGTH_SHORT).show();
            }
        });

        bInActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chipClickListener.onChipClick("InActive");
//                Toast.makeText(mContext, "bInActive Leads", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
