package com.example.muzafarimran.lastingsales.fragments;

import android.content.Context;

import com.example.muzafarimran.lastingsales.listeners.SearchCallback;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

/**
 * Created by ahmad on 02-Dec-16.
 */
@Deprecated
public abstract class SearchFragment extends TabFragment {
    protected MaterialSearchView materialSearchView;
    protected MaterialSearchView.OnQueryTextListener mSearchListener = new MaterialSearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            onSearch(query);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            onSearch(newText);
            return false;
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        materialSearchView = ((SearchCallback) context).getSearchView();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            if (materialSearchView != null && materialSearchView.isSearchOpen()) {
                materialSearchView.closeSearch();
            }
        }
        if (isVisibleToUser && isResumed()) {
            if (materialSearchView != null) {
                materialSearchView.setOnQueryTextListener(mSearchListener);
            }
        }
    }

    protected abstract void onSearch(String query);
}