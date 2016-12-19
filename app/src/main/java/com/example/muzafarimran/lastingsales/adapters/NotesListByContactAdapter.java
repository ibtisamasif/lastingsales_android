package com.example.muzafarimran.lastingsales.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.CallClickListener;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.NotesByContactsActivity;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ibtisam on 12/14/2016.
 */

public class NotesListByContactAdapter extends BaseAdapter  implements Filterable{
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


    public NotesListByContactAdapter(Context c, List<LSContact> contacts) {
        this.mContext = c;
        this.mContacts = contacts;
        if (mContacts == null) {
            mContacts = new ArrayList<>();
        }
        this.filteredData = mContacts;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.callClickListener = new CallClickListener(c);
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
    public View getView(int position, View convertView, ViewGroup parent) {

        final LSContact contact = (LSContact) getItem(position);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.note_by_contacts_item, parent, false);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent detailsActivityIntent = new Intent(mContext, NotesByContactsActivity.class);
                    long contactId = contact.getId();
                    detailsActivityIntent.putExtra(NotesByContactsActivity.KEY_CONTACT_ID, contactId + "");
//                    bundle.putString(FrameActivity.ACTIVITY_TITLE, "Notes List");
//                    bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, false);
                    mContext.startActivity(detailsActivityIntent);
                }
            });
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.contact_name);
            holder.number = (TextView) convertView.findViewById(R.id.contactNumber);
            holder.call_icon = (ImageView) convertView.findViewById(R.id.call_icon);
            convertView.setTag(holder);
            holder.call_icon.setOnClickListener(this.callClickListener);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(contact.getContactName());
        holder.number.setText(contact.getPhoneOne());
        holder.call_icon.setTag(mContacts.get(position).getPhoneOne());

        return convertView;
    }

    // for searching
    //TODO this method needs to be moved from here


    public void setList(List<LSContact> contacts) {
        mContacts = contacts;
        filteredData = contacts;
        notifyDataSetChanged();
    }

    private boolean isSeparator(int position) {
        return filteredData.get(position).getContactType() == "separator";
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
        ImageView call_icon;
    }
}
