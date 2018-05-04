package com.example.muzafarimran.lastingsales.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.muzafarimran.lastingsales.NavigationBottomFragments.BlankFragment3_1;
import com.example.muzafarimran.lastingsales.NavigationBottomFragments.BlankFragment3_2;
import com.example.muzafarimran.lastingsales.NavigationBottomFragments.BlankFragment3_3;
import com.example.muzafarimran.lastingsales.NavigationBottomFragments.BlankFragment3_4;
import com.example.muzafarimran.lastingsales.NavigationBottomFragments.BlankFragment3_5;
import com.example.muzafarimran.lastingsales.fragments.TabFragment;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import java.util.Collection;

/**
 * Created by ibtisam on 12/29/2016.
 */

public class MyLeadPagerAdapter extends FragmentPagerAdapter {
    private final int TAB_COUNT = 5;
//    private Long id;
//    private String number;

    public MyLeadPagerAdapter(FragmentManager fm) {
        super(fm);
//        this.id = id;
//        this.number = number;
    }

    @Override
    public Fragment getItem(int position) {
        TabFragment fragment = null;
        switch (position) {
            case 0:
                fragment = BlankFragment3_1.newInstance();
                break;
            case 1:
                fragment = BlankFragment3_2.newInstance();
                break;
            case 2:
                fragment = BlankFragment3_3.newInstance();
                break;
            case 3:
                fragment = BlankFragment3_4.newInstance();
                break;
            case 4:
                fragment = BlankFragment3_5.newInstance();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                Collection<LSContact> allContacts = LSContact.getDateArrangedSalesContacts();
                if (allContacts != null && allContacts.size() > 0) {
                    return "All" + "(" + allContacts.size() + ")";
                } else {
                    return "All(0)";
                }
            case 1:
                Collection<LSContact> inProgressContacts = LSContact.getDateArrangedSalesContactsByLeadSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
                if (inProgressContacts != null && inProgressContacts.size() > 0) {
                    return "InProgress" + "(" + inProgressContacts.size() + ")";
                } else {
                    return "InProgress(0)";
                }
            case 2:
                Collection<LSContact> wonContacts = LSContact.getDateArrangedSalesContactsByLeadSalesStatus(LSContact.SALES_STATUS_CLOSED_WON);
                if (wonContacts != null && wonContacts.size() > 0) {
                    return "Won" + "(" + wonContacts.size() + ")";
                } else {
                    return "Won(0)";
                }
            case 3:
                Collection<LSContact> lostContacts = LSContact.getDateArrangedSalesContactsByLeadSalesStatus(LSContact.SALES_STATUS_CLOSED_LOST);
                if (lostContacts != null && lostContacts.size() > 0) {
                    return "Lost" + "(" + lostContacts.size() + ")";
                } else {
                    return "Lost(0)";
                }
            case 4:
                Collection<LSContact> inActiveContacts = LSContact.getAllInactiveLeadContacts();
                if (inActiveContacts != null && inActiveContacts.size() > 0) {
                    return "InActive" + "(" + inActiveContacts.size() + ")";
                } else {
                    return "InActive(0)";
                }
            default:
                return null;
        }
    }
}
