package com.example.muzafarimran.lastingsales.receivers;

import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

/**
 * Created by ibtisam on 3/16/2017.
 */

public class IgnoredContact {

    public static void AddAsIgnoredContact(String contactPhone, String contactName) {
        String intlNum = PhoneNumberAndCallUtils.numberToInterNationalNumber(contactPhone);
        LSContact checkContact;
        checkContact = LSContact.getContactFromNumber(intlNum);
        if (checkContact != null) {
            if (checkContact.getContactType().equals(LSContact.CONTACT_TYPE_UNLABELED)) {
                checkContact.setPhoneOne(contactPhone);
                checkContact.setContactName(contactName);
                checkContact.setContactType(LSContact.CONTACT_TYPE_IGNORED);
                checkContact.save();
            }
        } else {
            LSContact tempContact = new LSContact();
            tempContact.setPhoneOne(contactPhone);
            tempContact.setContactName(contactName);
            tempContact.setContactType(LSContact.CONTACT_TYPE_IGNORED);
            tempContact.save();
        }
    }
}
