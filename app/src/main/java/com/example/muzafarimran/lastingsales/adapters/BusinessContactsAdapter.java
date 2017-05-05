package com.example.muzafarimran.lastingsales.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.CallClickListener;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.AddEditLeadActivity;
import com.example.muzafarimran.lastingsales.fragments.ColleagueContactDeleteBottomSheetDialogFragment;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

/**
 * Created by lenovo 1 on 9/21/2016.
 */

public class BusinessContactsAdapter extends BaseAdapter implements Filterable {
    private final static int TYPE_SEPARATOR = 0;
    private final static int TYPE_ITEM = 1;
    private final static int ITEM_TYPES = 2;
    View contact_details = null;
    boolean deleteFlow = false;
    Boolean expanded = false;
    private Context mContext;
    private LayoutInflater mInflater;
    private List<LSContact> mContacts;
    private List<LSContact> filteredData;
    private int prospectCount = 0;
    private int leadCount = 0;
    private CallClickListener callClickListener = null;
    private showContactDetaislsListener showContactDetaislsListener = null;
    private String contactType;
    private RelativeLayout noteDetails;
    private FragmentManager supportFragmentManager;


    public BusinessContactsAdapter(Context c, List<LSContact> contacts, String type) {
        this.mContext = c;
        this.mContacts = contacts;
        if (mContacts == null) {
            mContacts = new ArrayList<>();
        }
        this.filteredData = mContacts;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.callClickListener = new CallClickListener(c);
        this.contactType = type;
        //TODO: correct the counting mechanism
//        this.prospectCount = contacts.indexOf(new LSContact("Leads", null, "separator", null, null, null, null, null, null)) - 1;
        this.leadCount = mContacts.size() - this.prospectCount - 2;
    }

    public boolean isDeleteFlow() {
        return deleteFlow;
    }

    public void setDeleteFlow(boolean deleteFlow) {
        this.deleteFlow = deleteFlow;
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_TYPES;
    }

    @Override
    public int getItemViewType(int position) {
        return isSeparator(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getCount() {
        return this.filteredData.size();
    }

    @Override
    public Object getItem(int position) {
        return this.filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final LSContact contact = (LSContact) getItem(position);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.contact_row_view_business, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.contact_name);
            holder.number = (TextView) convertView.findViewById(R.id.contactNumber);
            holder.call_icon = (ImageView) convertView.findViewById(R.id.call_icon);
            holder.user_details_wrapper = (RelativeLayout) convertView.findViewById(R.id.user_call_group_wrapper);
            holder.contactTagDropDownLayout = (RelativeLayout) convertView.findViewById(R.id.contactTagDropDownLayout);
            holder.deleteButton = (ImageButton) convertView.findViewById(R.id.deleteButtonContactRow);
            holder.bIgnore = (Button) convertView.findViewById(R.id.contactDropDownIgnoreButton);
            holder.bSales = (Button) convertView.findViewById(R.id.contactDropDownAddAsLeadButton);
            holder.contactTagDropDownLayout.setVisibility(GONE);
            convertView.setTag(holder);
            holder.call_icon.setOnClickListener(this.callClickListener);
        } else {
            holder = (ViewHolder) convertView.getTag();
            ((ViewGroup) holder.user_details_wrapper.getParent()).removeView(contact_details);
        }
        holder.contactTagDropDownLayout.setVisibility(GONE);
        if (contact.getContactName().equals("null")) {
            holder.name.setText("");
        } else {
            holder.name.setText(contact.getContactName());
        }
        holder.user_details_wrapper.setTag(position);
        holder.number.setText(contact.getPhoneOne());
        holder.user_details_wrapper.setOnClickListener(new showContactDetaislsListener(contact, holder.contactTagDropDownLayout));
        if (!deleteFlow) {
            holder.deleteButton.setVisibility(GONE);
        } else {
            holder.deleteButton.setVisibility(View.VISIBLE);
        }
        holder.user_details_wrapper.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
//                deleteFlow = true;
//                setList(LSContact.getContactsByType(contactType));
                ColleagueContactDeleteBottomSheetDialogFragment colleagueContactDeleteBottomSheetDialogFragment = new ColleagueContactDeleteBottomSheetDialogFragment();
                colleagueContactDeleteBottomSheetDialogFragment.setPosition(position);
                colleagueContactDeleteBottomSheetDialogFragment.show(getSupportFragmentManager(), colleagueContactDeleteBottomSheetDialogFragment.getTag());
                colleagueContactDeleteBottomSheetDialogFragment.setBusinessContactsAdapter(BusinessContactsAdapter.this);

                return true;
            }
        });
        holder.call_icon.setTag(mContacts.get(position).getPhoneOne());
//              Deletes the contact, queries db and updates local list plus nitifies adpater
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LSInquiry checkInquiry = LSInquiry.getInquiryByNumberIfExists(contact.getPhoneOne());
//                if (checkInquiry == null) {
                    contact.setLeadDeleted(true);
                    contact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_DELETE_NOT_SYNCED);
                    contact.delete();
                    setList(LSContact.getContactsByType(contactType));
                    Toast.makeText(mContext, "Contact Deleted!", Toast.LENGTH_SHORT).show();
                    DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(mContext);
                    dataSenderAsync.run();
//                } else {
//                    Toast.makeText(mContext, "Please Handle Inquiry First", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        holder.bSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(mContext, AddEditLeadActivity.class);
                myIntent.putExtra(AddEditLeadActivity.ACTIVITY_LAUNCH_MODE, AddEditLeadActivity.LAUNCH_MODE_EDIT_EXISTING_CONTACT);
                myIntent.putExtra(AddEditLeadActivity.TAG_LAUNCH_MODE_CONTACT_ID, contact.getId() + "");
                myIntent.putExtra(AddEditLeadActivity.MIXPANEL_SOURCE, AddEditLeadActivity.MIXPANEL_SOURCE_COLLEAGUE);
                mContext.startActivity(myIntent);
            }
        });
        holder.bIgnore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contact.setContactType(LSContact.CONTACT_TYPE_IGNORED);
                contact.save();
                ArrayList<LSContact> allIBusinessContacts = (ArrayList<LSContact>) LSContact.getContactsByType(LSContact.CONTACT_TYPE_BUSINESS);
                setList(allIBusinessContacts);
                Toast.makeText(mContext, "Added as Ignored Contact!", Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    public void deleteAtPosition(int position) {
        LSContact contact = mContacts.get(position);
        mContacts.remove(position);
        contact.setLeadDeleted(true);
        contact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_DELETE_NOT_SYNCED);
        contact.save();
//        contact.delete();
        setList(LSContact.getContactsByType(contactType));
        Toast.makeText(mContext, "Contact Deleted", Toast.LENGTH_SHORT).show();
        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(mContext);
        dataSenderAsync.run();
    }
    // for searching
    //TODO this method needs to be moved from here

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                //If there's nothing to filter on, return the original data for list
                if (charSequence == null || charSequence.length() == 0) {
                    results.values = mContacts;
                    results.count = mContacts.size();
                } else {
                    List<LSContact> filterResultsData = new ArrayList<>();
                    //int length = charSequence.length();
                    for (int i = 0; i < mContacts.size(); i++) {
                        if (mContacts.get(i).getContactName() != null && mContacts.get(i).getContactName().toLowerCase().contains(((String) charSequence).toLowerCase())) {
                            filterResultsData.add(mContacts.get(i));
                            continue;
                        }
                        if (mContacts.get(i).getPhoneOne().toLowerCase().contains(((String) charSequence).toLowerCase())) {
                            filterResultsData.add(mContacts.get(i));
                        }
                    }
                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredData = ((List<LSContact>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }

    public void setList(List<LSContact> contacts) {
        mContacts = contacts;
        filteredData = contacts;
        notifyDataSetChanged();
    }

    private boolean isSeparator(int position) {
        return filteredData.get(position).getContactType() == "separator";
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public FragmentManager getSupportFragmentManager() {
        return supportFragmentManager;
    }

    public void setSupportFragmentManager(FragmentManager supportFragmentManager) {
        this.supportFragmentManager = supportFragmentManager;
    }

    /*
    * Hold references to sub views
    * */
    static class ViewHolder {
        TextView name;
        TextView number;
        ImageView call_icon;
        RelativeLayout user_details_wrapper;
        ImageButton deleteButton;
        Button bIgnore;
        Button bSales;
        RelativeLayout contactTagDropDownLayout;
    }

    /*
    * event handler for click on name
    * */
    public class showContactDetaislsListener implements View.OnClickListener {
        LSContact contact;
        RelativeLayout detailsLayout;

        public showContactDetaislsListener(LSContact contact, RelativeLayout layout) {
            this.contact = contact;
            this.detailsLayout = layout;
        }

        @Override
        public void onClick(View v) {
            if (expanded && noteDetails != null) {
                noteDetails.setVisibility(View.GONE);
                noteDetails = null;
                expanded = false;
            } else {
                noteDetails = detailsLayout;
                detailsLayout.setVisibility(View.VISIBLE);
                expanded = true;
            }
        }
    }
}