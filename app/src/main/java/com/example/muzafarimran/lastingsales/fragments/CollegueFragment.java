package com.example.muzafarimran.lastingsales.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.events.BackPressedEventModel;
import com.example.muzafarimran.lastingsales.events.ColleagueContactAddedEventModel;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.TagNumberAndAddFollowupActivity;
import com.example.muzafarimran.lastingsales.adapters.ColleagueContactsAdapter;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;


/**
 * A simple {@link Fragment} subclass.
 */
@Deprecated
public class CollegueFragment extends TabFragment {

    private static final String TAG = "ColleagueContactFrag";
    ListView listView = null;
    ColleagueContactsAdapter colleagueContactsAdapter;
    MaterialSearchView searchView;
    ShowAddContactForm showaddcontactform = new ShowAddContactForm();
    FloatingActionButton floatingActionButtonAdd, floatingActionButtonImport;
    FloatingActionMenu floatingActionMenu;
    private TinyBus bus;

    public static CollegueFragment newInstance(int page, String title) {
        CollegueFragment fragmentFirst = new CollegueFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public void setList(List<LSContact> contacts) {
        if (colleagueContactsAdapter != null) {
            colleagueContactsAdapter.setList(contacts);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        colleagueContactsAdapter = new ColleagueContactsAdapter(getContext(), null, LSContact.CONTACT_TYPE_COLLEAGUE);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
        bus = TinyBus.from(getActivity().getApplicationContext());
        bus.register(this);
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        Log.d(TAG, "onStop() called");
        super.onStop();
    }

    @Subscribe
    public void onColleagueContactAddedEventModel(ColleagueContactAddedEventModel event) {
        Log.d(TAG, "onColleagueContactEvent() called with: event = [" + event + "]");
        List<LSContact> contacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_COLLEAGUE);
        setList(contacts);
//        TinyBus.from(getActivity().getApplicationContext()).unregister(event);
    }

    @Subscribe
    public void onBackPressedEventModel(BackPressedEventModel event) {
        if (!event.backPressHandled && colleagueContactsAdapter.isDeleteFlow()) {
            event.backPressHandled = true;
            colleagueContactsAdapter.setDeleteFlow(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        List<LSContact> contacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_COLLEAGUE);
        setList(contacts);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_collegue, container, false);

        floatingActionMenu = (FloatingActionMenu) view.findViewById(R.id.material_design_android_floating_action_menu_collegue);
        floatingActionButtonAdd = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.material_design_floating_action_menu_add_collegue);
        floatingActionButtonImport = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.material_design_floating_action_menu_import_collegue);
        colleagueContactsAdapter.setSupportFragmentManager(getFragmentManager());
        floatingActionMenu.setClosedOnTouchOutside(true);

        floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), TagNumberAndAddFollowupActivity.class);
                intent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE, TagNumberAndAddFollowupActivity.LAUNCH_MODE_ADD_NEW_CONTACT);
                intent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_TYPE , LSContact.CONTACT_TYPE_COLLEAGUE);
                startActivity(intent);
            }
        });
        floatingActionButtonImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), TagNumberAndAddFollowupActivity.class);
                intent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE, TagNumberAndAddFollowupActivity.LAUNCH_MODE_IMPORT_CONTACT);
                intent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_TYPE , LSContact.CONTACT_TYPE_COLLEAGUE);
                startActivity(intent);
            }
        });

        listView = (ListView) view.findViewById(R.id.collegue_contacts_list);
        listView.setAdapter(colleagueContactsAdapter);
//        this.inputSearch.addTextChangedListener(new CollegueFragment.addListenerOnTextChange());
        searchView = (MaterialSearchView) getActivity().findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                colleagueContactsAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                colleagueContactsAdapter.getFilter().filter(newText);
                return false;
            }
        });
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listView = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    * event handler for click on add contact cta
    * */
    public class ShowAddContactForm implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent myIntent = new Intent(getActivity(), TagNumberAndAddFollowupActivity.class);
            myIntent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE , TagNumberAndAddFollowupActivity.LAUNCH_MODE_ADD_NEW_CONTACT);
            myIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_TYPE , LSContact.CONTACT_TYPE_COLLEAGUE);
            getActivity().startActivity(myIntent);
        }
    }
}