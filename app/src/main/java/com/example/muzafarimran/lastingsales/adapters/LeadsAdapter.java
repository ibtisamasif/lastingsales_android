package com.example.muzafarimran.lastingsales.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.CallClickListener;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.ContactDetailsTabActivity;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

/**
 * Created by ibtisam on 12/17/2016.
 */

public class LeadsAdapter extends BaseAdapter implements Filterable{

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
    private String contactLeadType;
    private LinearLayout noteDetails;


    public LeadsAdapter(Context c, List<LSContact> contacts, String type) {
        this.mContext = c;
        this.mContacts = contacts;
        if (mContacts == null) {
            mContacts = new ArrayList<>();
        }
        this.filteredData = mContacts;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.callClickListener = new CallClickListener(c);
        //Filtering out colleagues from list
        if(type!=LSContact.CONTACT_TYPE_COLLEAGUE) {
            this.contactLeadType = type;
        }
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
        return TYPE_ITEM;
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
    public View getView(int position, View convertView, ViewGroup parent) {

        final LSContact contact = (LSContact) getItem(position);
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.contact_row_view, parent, false);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.contact_name);
                holder.number = (TextView) convertView.findViewById(R.id.contactNumber);
                holder.call_icon = (ImageView) convertView.findViewById(R.id.call_icon);

                holder.user_details_wrapper = (RelativeLayout) convertView.findViewById(R.id.user_call_group_wrapper);

                holder.deleteButton = (ImageButton) convertView.findViewById(R.id.deleteButtonContactRow);
                holder.lastContactText = (TextView) convertView.findViewById(R.id.last_contact_text);
                holder.numberCallsText = (TextView) convertView.findViewById(R.id.calls_text);
                holder.contactDetailsDopDownLayout = (LinearLayout) convertView.findViewById(R.id.contactDetailsDropDownLayout);

//                holder.detailsButton = (Button) convertView.findViewById(R.id.contactDetailsDropDownDetailsButton);

                holder.moreButton = (ImageView) convertView.findViewById(R.id.ivMoreButtonContactsDetailsDropDown);

                holder.salesLeadStatus = (TextView) convertView.findViewById(R.id.status_text);
                holder.statusRow = (RelativeLayout) convertView.findViewById(R.id.status_row);
                holder.contactDetailsDopDownLayout.setVisibility(GONE);
                convertView.setTag(holder);
                holder.call_icon.setOnClickListener(this.callClickListener);
            } else {
                holder = (ViewHolder) convertView.getTag();
                ((ViewGroup) holder.user_details_wrapper.getParent()).removeView(contact_details);
            }
            holder.contactDetailsDopDownLayout.setVisibility(GONE);
            String timeAgoString;
            String numberOfCalls;
            ArrayList<LSCall> allCalls = LSCall.getCallsFromNumber(contact.getPhoneOne());
            if (allCalls == null || allCalls.size() == 0) {
                timeAgoString = "Never";
                numberOfCalls = 0 + "";
            } else {
                LSCall latestCall = allCalls.get(allCalls.size() - 1);
                timeAgoString = PhoneNumberAndCallUtils.getTimeAgo(latestCall.getBeginTime(), mContext);
                numberOfCalls = allCalls.size() + "";
            }
            holder.lastContactText.setText(timeAgoString);
            holder.numberCallsText.setText(numberOfCalls);
            holder.name.setText(contact.getContactName());
            holder.user_details_wrapper.setTag(position);
            holder.number.setText(contact.getPhoneOne());
            holder.user_details_wrapper.setOnClickListener(new showContactDetaislsListener(contact, holder.contactDetailsDopDownLayout));
            if (!deleteFlow) {
                holder.deleteButton.setVisibility(GONE);
            } else {
                holder.deleteButton.setVisibility(View.VISIBLE);
            }
            holder.user_details_wrapper.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    deleteFlow = true;
                    setList(LSContact.getContactsByLeadSalesStatus(contactLeadType));
                    return true;
                }
            });
            holder.call_icon.setTag(mContacts.get(position).getPhoneOne());
//              Deletes the contact, queries db and updates local list plus notifies adapter
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Flushing Notes Of lead
                    List<LSNote> allNotesOfThisContact = LSNote.getNotesByContactId(contact.getId());
                    if(allNotesOfThisContact!=null && allNotesOfThisContact.size()>0){
                        for(LSNote oneNote : allNotesOfThisContact){
                            oneNote.delete();
                        }
                    }
                    //Flushing Followup Of lead
                    List<TempFollowUp> allFollowupsOfThisContact = TempFollowUp.getFollowupsByContactId(contact.getId());
                    if(allFollowupsOfThisContact!=null && allFollowupsOfThisContact.size()>0){
                        for(TempFollowUp oneFollowup : allFollowupsOfThisContact){
                            oneFollowup.delete();
                        }
                    }
                    contact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_DELETE_NOT_SYNCED);
                    contact.save();
//                    contact.delete();
                    DataSenderAsync dataSenderAsync = new DataSenderAsync(mContext);
                    dataSenderAsync.execute();
                    setList(LSContact.getContactsByLeadSalesStatus(contactLeadType));
                    Toast.makeText(mContext, "Contact Deleted!", Toast.LENGTH_SHORT).show();
                }
            });

//            holder.detailsButton.setOnClickListener(new DetailsButtonClickListener(contact));

            final View moreView = holder.moreButton;
            holder.moreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new android.widget.PopupMenu(mContext, moreView);
                    popupMenu.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
//                                case R.id.lead_type_prospect:
//                                    contact.setContactSalesStatus(LSContact.SALES_STATUS_PROSTPECT);
//                                    contact.save();
//                                    notifyDataSetChanged();
//                                    break;
                                case R.id.lead_type_lead:
                                    contact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
                                    contact.save();
                                    notifyDataSetChanged();
                                    break;
                                case R.id.lead_type_closed_won:
                                    contact.setContactSalesStatus(LSContact.SALES_STATUS_CLOSED_WON);
                                    contact.save();
                                    notifyDataSetChanged();
                                    break;
                                case R.id.lead_type_closed_lost:
                                    contact.setContactSalesStatus(LSContact.SALES_STATUS_CLOSED_LOST);
                                    contact.save();
                                    notifyDataSetChanged();
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.inflate(R.menu.sales_tab_contact_lead_types);
                    popupMenu.show();
                }
            });

        if (contact.getContactType() != null) {
            if (contact.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
                if (contact.getContactSalesStatus() != null && !contact.getContactSalesStatus().equals("")) {
                    switch (contact.getContactSalesStatus()) {
//                        case LSContact.SALES_STATUS_PROSTPECT:
//                            holder.salesLeadStatus.setText("Prospect");
//                            break;
                        case LSContact.SALES_STATUS_INPROGRESS:
                            holder.salesLeadStatus.setText("InProgress");
                            break;
                        case LSContact.SALES_STATUS_CLOSED_WON:
                            holder.salesLeadStatus.setText("Closed Won");
                            break;
                        case LSContact.SALES_STATUS_CLOSED_LOST:
                            holder.salesLeadStatus.setText("Closed Lost");
                            break;
                    }
                }
            } else {
                holder.statusRow.setVisibility(GONE);
            }
        }
        return convertView;
    }


    // for searching
    //TODO this method needs to be moved from here

    public void setList(List<LSContact> contacts) {
        mContacts = contacts;
        filteredData = contacts;
        notifyDataSetChanged();
    }

    public String getContactType() {
        return contactLeadType;
    }

    public void setContactType(String contactLeadType) {
        this.contactLeadType = contactLeadType;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new Filter.FilterResults();
                //If there's nothing to filter on, return the original data for list
                if (charSequence == null || charSequence.length() == 0) {
                    results.values = mContacts;
                    results.count = mContacts.size();
                } else {
                    List<LSContact> filterResultsData = new ArrayList<>();
                    //int length = charSequence.length();
                    for (int i = 0; i < mContacts.size(); i++) {
                        if (mContacts.get(i).getContactType().toLowerCase() != "separator" && mContacts.get(i).getContactName().toLowerCase().contains(((String) charSequence).toLowerCase())) {
                            filterResultsData.add(mContacts.get(i));
                            continue;
                        }
                        if (mContacts.get(i).getContactType().toLowerCase() != "separator" && mContacts.get(i).getPhoneOne().toLowerCase().contains(((String) charSequence).toLowerCase())) {
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
    /*
    * Hold references to sub views
    * */
    static class ViewHolder {
        TextView name;
        TextView number;
        TextView lastContactText;
        TextView numberCallsText;
        ImageView call_icon;
        RelativeLayout user_details_wrapper;
        ImageButton deleteButton;
        LinearLayout contactDetailsDopDownLayout;
//        Button detailsButton;
        ImageView moreButton;
        TextView salesLeadStatus;
        RelativeLayout statusRow;
    }

    /*
    * event handler for click on name
    * */
    public class showContactDetaislsListener implements View.OnClickListener {
        LSContact contact;
        LinearLayout detailsLayout;

        public showContactDetaislsListener(LSContact contact, LinearLayout layout) {
            this.contact = contact;
            this.detailsLayout = layout;
        }

        @Override
        public void onClick(View v) {
            Intent detailsActivityIntent = new Intent(mContext, ContactDetailsTabActivity.class);
            long contactId = contact.getId();
            detailsActivityIntent.putExtra(ContactDetailsTabActivity.KEY_CONTACT_ID, contactId + "");
            mContext.startActivity(detailsActivityIntent);
        }
    }

//    private class DetailsButtonClickListener implements View.OnClickListener {
//        LSContact contact;
//
//        public DetailsButtonClickListener(LSContact contact) {
//            this.contact = contact;
//        }
//
//        @Override
//        public void onClick(View view) {
//            Intent detailsActivityIntent = new Intent(mContext, ContactDetailsTabActivity.class);
//            long contactId = contact.getId();
//            detailsActivityIntent.putExtra(ContactDetailsTabActivity.KEY_CONTACT_ID, contactId + "");
//            mContext.startActivity(detailsActivityIntent);
//        }
//    }
}
