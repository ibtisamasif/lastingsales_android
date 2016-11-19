package com.example.muzafarimran.lastingsales.providers.models;

import android.annotation.TargetApi;
import android.os.Build;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by ahmad on 08-Nov-16.
 */

public class LSContact extends SugarRecord
{
//    private int contactId;
    private String contactName;
    private String contactEmail;
    private String contactType;
    private String phoneOne;
    private String phone2;
    private String contactDescription;
    private String contactCompany;
    private String contactAddress;
    private String contactCreated_at;
    private String contactUpdated_at;
    private String contactDeleted_at;
    private String contactSales_status;

    @Ignore
    public static final String CONTACT_TYPE_SALES = "type_sales";
    @Ignore
    public static final String CONTACT_TYPE_COLLEAGUE = "type_colleague";
    @Ignore
    public static final String CONTACT_TYPE_PERSONAL = "type_personal";



    public LSContact()
    {
    }

    public LSContact(String contactName, String contactEmail, String contactType, String phoneOne, String phone2, String contactDescription, String contactCompany, String contactAddress)
    {
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        this.contactType = contactType;
        this.phoneOne = phoneOne;
        this.phone2 = phone2;
        this.contactDescription = contactDescription;
        this.contactCompany = contactCompany;
        this.contactAddress = contactAddress;
    }

    public static List<LSContact> getContactsByType(String type) {
        return LSContact.find(LSContact.class, "contact_type = ? ", type );
    }


    public static LSContact getContactFromNumber(String number) {


        ArrayList<LSContact> list = null;

        try{
            list = (ArrayList<LSContact>) LSContact.find(LSContact.class, "phone_one = ? ", number);
        }catch (IllegalArgumentException e)
        {
            return null;
        }

        if (list.size() > 0) {

            return list.get(0);
        } else {
            return null;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object obj)
    {
        LSContact c = (LSContact) obj;

        return (

                        Objects.equals(c.getContactName(), this.contactName) &&
                        Objects.equals(c.getContactEmail(), this.contactEmail) &&
                        Objects.equals(c.getContactType(), this.contactType) &&
                        Objects.equals(c.getPhoneOne(), this.phoneOne) &&
                        Objects.equals(c.getPhone2(), this.phone2) &&
                        Objects.equals(c.getContactCompany(), this.contactCompany) &&
                        Objects.equals(c.getContactDescription(), this.contactDescription) &&
                        Objects.equals(c.getContactAddress(), this.contactAddress) &&
                        Objects.equals(c.getContactCreated_at(), this.contactCreated_at) &&
                        Objects.equals(c.getContactUpdated_at(), this.contactUpdated_at) &&
                        Objects.equals(c.getContactDeleted_at(), this.contactDeleted_at) &&
                        Objects.equals(c.getContactSales_status(), this.contactSales_status)
        );
    }

    @Override
    public int hashCode() {
        return
                (this.contactName != null ? this.contactName.hashCode() : 0) +
                (this.contactEmail != null ? this.contactEmail.hashCode() : 0) +
                (this.contactType != null ? this.contactType.hashCode() : 0) +
                (this.phoneOne != null ? this.phoneOne.hashCode() : 0) +
                (this.phone2 != null ? this.phone2.hashCode() : 0) +
                (this.contactCompany != null ? this.contactCompany.hashCode() : 0) +
                (this.contactDescription != null ? this.contactDescription.hashCode() : 0) +
                (this.contactAddress != null ? this.contactAddress.hashCode() : 0) +
                (this.contactCreated_at != null ? this.contactCreated_at.hashCode() : 0) +
                (this.contactUpdated_at != null ? this.contactUpdated_at.hashCode() : 0) +
                (this.contactDeleted_at != null ? this.contactDeleted_at.hashCode() : 0) +
                (this.contactSales_status != null ? this.contactSales_status.hashCode() : 0)
                ;
    }




    public String getContactName()
    {
        return contactName;
    }

    public void setContactName(String contactName)
    {
        this.contactName = contactName;
    }

    public String getContactEmail()
    {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail)
    {
        this.contactEmail = contactEmail;
    }

    public String getContactType()
    {
        return contactType;
    }

    public void setContactType(String contactType)
    {
        this.contactType = contactType;
    }

    public String getPhoneOne()
    {
        return phoneOne;
    }

    public void setPhoneOne(String phoneOne)
    {
        this.phoneOne = phoneOne;
    }

    public String getPhone2()
    {
        return phone2;
    }

    public void setPhone2(String phone2)
    {
        this.phone2 = phone2;
    }

    public String getContactDescription()
    {
        return contactDescription;
    }

    public void setContactDescription(String contactDescription)
    {
        this.contactDescription = contactDescription;
    }

    public String getContactCompany()
    {
        return contactCompany;
    }

    public void setContactCompany(String contactCompany)
    {
        this.contactCompany = contactCompany;
    }

    public String getContactAddress()
    {
        return contactAddress;
    }

    public void setContactAddress(String contactAddress)
    {
        this.contactAddress = contactAddress;
    }

    public String getContactCreated_at()
    {
        return contactCreated_at;
    }

    public void setContactCreated_at(String contactCreated_at)
    {
        this.contactCreated_at = contactCreated_at;
    }

    public String getContactUpdated_at()
    {
        return contactUpdated_at;
    }

    public void setContactUpdated_at(String contactUpdated_at)
    {
        this.contactUpdated_at = contactUpdated_at;
    }

    public String getContactDeleted_at()
    {
        return contactDeleted_at;
    }

    public void setContactDeleted_at(String contactDeleted_at)
    {
        this.contactDeleted_at = contactDeleted_at;
    }

    public String getContactSales_status()
    {
        return contactSales_status;
    }

    public void setContactSales_status(String contactSales_status)
    {
        this.contactSales_status = contactSales_status;
    }
}
