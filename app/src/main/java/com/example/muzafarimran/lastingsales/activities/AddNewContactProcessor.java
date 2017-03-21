package com.example.muzafarimran.lastingsales.activities;

import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

/**
 * Created by ibtisam on 3/21/2017.
 */

class AddNewContactProcessor {
    public static void Process(AddEditLeadActivity addEditLeadActivity, EditText etContactName, EditText etContactPhone, boolean editingMode, String selectedContactType) {
        etContactName.setError(null);
        etContactPhone.setError(null);
        boolean validation = true;
        String contactName = etContactName.getText().toString();
        String contactPhone = etContactPhone.getText().toString();
        if (contactName.equals("") || contactName.length() < 3) {
            validation = false;
            etContactName.setError("Invalid Name!");
        }
        if (contactPhone.equals("") || contactPhone.length() < 3) {
            validation = false;
            etContactPhone.setError("Invalid Number!");
        }
        if (validation && !editingMode) {
            String intlNum = PhoneNumberAndCallUtils.numberToInterNationalNumber(contactPhone);
            LSContact checkContact;
            checkContact = LSContact.getContactFromNumber(intlNum);
            if (checkContact != null) {
                if (checkContact.getContactType().equals(LSContact.CONTACT_TYPE_UNLABELED)) {
                    checkContact.setContactName(contactName);
                    checkContact.setPhoneOne(intlNum);
                    checkContact.setContactType(selectedContactType);
                    checkContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
                    checkContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                    checkContact.save();
                    if (!selectedContactType.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
                        moveToContactDetailScreen(addEditLeadActivity, checkContact);
                    } else {
                        //update inquiry as well if exists
                        LSInquiry tempInquiry = LSInquiry.getInquiryByNumberIfExists(intlNum);
                        if (tempInquiry != null) {
                            tempInquiry.setContact(checkContact);
                            tempInquiry.save();
                            tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_DELETE_NOT_SYNCED);
                            tempInquiry.save();
                            DataSenderAsync dataSenderAsync = new DataSenderAsync(addEditLeadActivity.getApplicationContext());
                            dataSenderAsync.execute();
                        }
                    }
                    Toast.makeText(addEditLeadActivity, "Contact Saved", Toast.LENGTH_SHORT).show();
                    addEditLeadActivity.finish();
                } else {
                    Toast.makeText(addEditLeadActivity, "Already Exists", Toast.LENGTH_SHORT).show();
                }
            } else {
                LSContact tempContact = new LSContact();
                tempContact.setContactName(contactName);
                tempContact.setPhoneOne(intlNum);
                tempContact.setContactType(selectedContactType);
                tempContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
                tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                tempContact.save();
                if (!selectedContactType.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
                    moveToContactDetailScreen(addEditLeadActivity, tempContact);
                } else {
                    //update inquiry as well if exists
                    LSInquiry tempInquiry = LSInquiry.getInquiryByNumberIfExists(intlNum);
                    if (tempInquiry != null) {
                        tempInquiry.setContact(checkContact);
                        tempInquiry.save();
                        tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_DELETE_NOT_SYNCED);
                        tempInquiry.save();
                        DataSenderAsync dataSenderAsync = new DataSenderAsync(addEditLeadActivity.getApplicationContext());
                        dataSenderAsync.execute();
                    }
                }
                Toast.makeText(addEditLeadActivity, "Contact Saved", Toast.LENGTH_SHORT).show();
                addEditLeadActivity.finish();
            }
        }
    }
    private static void moveToContactDetailScreen(Context context, LSContact contact) {
        Intent detailsActivityIntent = new Intent(context, ContactDetailsTabActivity.class);
        long contactId = contact.getId();
        detailsActivityIntent.putExtra(ContactDetailsTabActivity.KEY_CONTACT_ID, contactId + "");
        context.startActivity(detailsActivityIntent);
    }
}
