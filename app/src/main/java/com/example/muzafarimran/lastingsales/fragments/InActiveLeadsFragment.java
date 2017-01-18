package com.example.muzafarimran.lastingsales.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.TagNumberAndAddFollowupActivity;
import com.example.muzafarimran.lastingsales.adapters.InActiveLeadsAdapter;
import com.example.muzafarimran.lastingsales.events.BackPressedEventModel;
import com.example.muzafarimran.lastingsales.events.ColleagueContactAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 12/17/2016.
 */
public class InActiveLeadsFragment extends  TabFragment{

    public static final String TAG = "InActiveLeadsFragment";
    ListView listView = null;
    InActiveLeadsAdapter inActiveLeadsAdapter;
    MaterialSearchView searchView;
    private TinyBus bus;
    FloatingActionButton floatingActionButtonAdd, floatingActionButtonImport;
    FloatingActionMenu floatingActionMenu;
    public static InActiveLeadsFragment newInstance(int page, String title) {
        InActiveLeadsFragment fragmentFirst = new InActiveLeadsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public void setList(List<LSContact> contacts) {
        if (inActiveLeadsAdapter != null) {
            inActiveLeadsAdapter.setList(contacts);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        inActiveLeadsAdapter = new InActiveLeadsAdapter(getContext(), LSContact.getAllInactiveLeadContacts());
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        bus = TinyBus.from(getActivity().getApplicationContext());
        bus.register(this);
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onColleagueContactAddedEventModel(ColleagueContactAddedEventModel event) {
        List<LSContact> contacts = LSContact.getAllInactiveLeadContacts();
        setList(contacts);
        TinyBus.from(getActivity().getApplicationContext()).unregister(event);
    }

    @Subscribe
    public void onBackPressedEventModel(BackPressedEventModel event) {
        if (!event.backPressHandled && inActiveLeadsAdapter.isDeleteFlow()) {
            event.backPressHandled = true;
            inActiveLeadsAdapter.setDeleteFlow(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        List<LSContact> contacts = LSContact.getAllInactiveLeadContacts();
        setList(contacts);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leads, container, false);
        listView = (ListView) view.findViewById(R.id.leads_contacts_list);
        listView.setAdapter(inActiveLeadsAdapter);
        searchView = (MaterialSearchView) getActivity().findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                inActiveLeadsAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                inActiveLeadsAdapter.getFilter().filter(newText);
                return false;
            }
        });
        floatingActionMenu = (FloatingActionMenu) view.findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionButtonAdd = (FloatingActionButton) view.findViewById(R.id.material_design_floating_action_menu_add);
        floatingActionButtonImport = (FloatingActionButton) view.findViewById(R.id.material_design_floating_action_menu_import);
        floatingActionMenu.setClosedOnTouchOutside(true);

        floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionMenu.close(true);
                Intent intent = new Intent(getContext(), TagNumberAndAddFollowupActivity.class);
                intent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE, TagNumberAndAddFollowupActivity.LAUNCH_MODE_ADD_NEW_CONTACT);
                intent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_TYPE , LSContact.CONTACT_TYPE_SALES);
                startActivity(intent);
            }
        });
        floatingActionButtonImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionMenu.close(true);
                Intent intent = new Intent(getContext(), TagNumberAndAddFollowupActivity.class);
                intent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE, TagNumberAndAddFollowupActivity.LAUNCH_MODE_IMPORT_CONTACT);
                intent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_TYPE , LSContact.CONTACT_TYPE_SALES);
                startActivity(intent);

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

}
