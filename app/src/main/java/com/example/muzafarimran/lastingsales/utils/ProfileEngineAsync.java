package com.example.muzafarimran.lastingsales.utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSContactProfile;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.sync.MyURLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by ibtisam on 9/22/2017.
 */

public class ProfileEngineAsync extends AsyncTask<Object, Void, Void> {
    public static final String TAG = "ProfileFetch";
    Context mContext;
    private SessionManager sessionManager;

    public ProfileEngineAsync(Context context) {
        super.onPreExecute();
        this.mContext = context;
        sessionManager = new SessionManager(context);
        Log.d(TAG, "ProfileEngineAsync onPreExecute:");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Object... params) {
        getBatchContacts();
        return null;
    }

    private void getBatchContacts() {
        List<LSContact> contactsList = null;
        if (LSContact.count(LSContact.class) > 0) {
            contactsList = LSContact.getContactsByTypeInDescOrder(LSContact.CONTACT_TYPE_UNLABELED);
            Log.d(TAG, "getBatchContacts: count : " + contactsList.size());
            for (LSContact oneContact : contactsList) {
                Log.d(TAG, "Found Contacts " + oneContact.getPhoneOne());
                String number = oneContact.getPhoneOne();
                if (number != null) {
                    String formatedNumber = "";
                    if (number.charAt(0) == '+') {
                        formatedNumber = number.substring(1);
                        formatedNumber = formatedNumber.replaceAll("\\s","").trim();
                    }
                    fetchProfile(formatedNumber);
                } else {
                    Log.d(TAG, "getBatchContacts: NUMBER IS NULL CHECK STORED NUMBER");
                }
            }
        }
    }

    private void fetchProfile(final String contactNumber) {
        Log.d(TAG, "fetchProfile: Fetching Data...");
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        RequestQueue queue = Volley.newRequestQueue(mContext);
        final String BASE_URL = MyURLs.GET_PROFILE;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter("phone", "" + contactNumber)
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        StringRequest sr = new StringRequest(Request.Method.GET, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() getProfile: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    String responseCode = jObj.getString("response");
                    JSONObject responseObject = jObj.getJSONObject("response");
//                    if (responseCode.equals("200")){
                    String id = responseObject.getString("id");
                    String fname = responseObject.getString("fname");
                    String lname = responseObject.getString("lname");
                    String dob = responseObject.getString("dob");
                    String phone = responseObject.getString("phone");
                    String interest = responseObject.getString("interest");
                    String address = responseObject.getString("address");
                    String city = responseObject.getString("city");
                    String country = responseObject.getString("country");
                    String email = responseObject.getString("email");
                    String fb = responseObject.getString("fb");
                    String linkd = responseObject.getString("linkd");
                    String tweet = responseObject.getString("tweet");
                    String insta = responseObject.getString("insta");
                    String whatsapp = responseObject.getString("whatsapp");
                    String mature = responseObject.getString("mature");
                    String company = responseObject.getString("company");
                    String work = responseObject.getString("work");
                    String social_image = responseObject.getString("social_image");
                    String created_at = responseObject.getString("created_at");
                    String comp_link = responseObject.getString("comp_link");

                    Log.d(TAG, "onResponse: id: " + id);
                    Log.d(TAG, "onResponse: fname: " + fname);
                    Log.d(TAG, "onResponse: lname: " + lname);
                    Log.d(TAG, "onResponse: dob: " + dob);
                    Log.d(TAG, "onResponse: phone: " + phone);
//                    Log.d(TAG, "onResponse: interest: " + interest);
                    Log.d(TAG, "onResponse: address: " + address);
                    Log.d(TAG, "onResponse: city: " + city);
                    Log.d(TAG, "onResponse: country: " + country);
                    Log.d(TAG, "onResponse: email: " + email);
                    Log.d(TAG, "onResponse: linkd: " + linkd);
                    Log.d(TAG, "onResponse: tweet: " + tweet);
                    Log.d(TAG, "onResponse: insta: " + insta);
                    Log.d(TAG, "onResponse: whatsapp: " + whatsapp);
//                    Log.d(TAG, "onResponse: mature: " + mature);
                    Log.d(TAG, "onResponse: company: " + company);
                    Log.d(TAG, "onResponse: work: " + work);
                    Log.d(TAG, "onResponse: social_image: " + social_image);
//                    Log.d(TAG, "onResponse: created_at: " + created_at);
                    Log.d(TAG, "onResponse: comp_link: " + comp_link);

                    String intlNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(mContext, contactNumber);
                    LSContact lsContact = LSContact.getContactFromNumber(intlNumber);
                    LSInquiry lsInquiry = LSInquiry.getPendingInquiryByNumberIfExists(intlNumber);

                    LSContactProfile lsContactProfile = LSContactProfile.getProfileFromNumber(intlNumber);
                    if (lsContactProfile == null) {
                        Log.d(TAG, "onResponse: Profile Doesnt Exist Creating new");
                        LSContactProfile newProfile = new LSContactProfile();
                        newProfile.setFirstName(fname);
                        newProfile.setLastName(lname);
                        newProfile.setDob(dob);
                        newProfile.setPhone(phone);
                        newProfile.setAddress(address);
                        newProfile.setCity(city);
                        newProfile.setCountry(country);
                        newProfile.setEmail(email);
                        newProfile.setLinkd(linkd);
                        newProfile.setTweet(tweet);
                        newProfile.setInsta(insta);
                        newProfile.setWhatsapp(whatsapp);
                        newProfile.setCompany(company);
                        newProfile.setWork(work);
                        newProfile.setSocial_image(social_image);
                        newProfile.setComp_link(comp_link);
                        newProfile.save();
                        lsContact.setContactProfile(newProfile);
                        lsContact.save();
                        if(lsInquiry != null ) {
                            Log.d(TAG, "onResponse: Profile == null lsInquiry: " + lsInquiry.toString());
                            lsInquiry.setContactProfile(newProfile);
                            lsInquiry.save();
                        }
                    }else {
                        Log.d(TAG, "onResponse: Already Exists: " + lsContactProfile);
                        if(lsContact.getContactProfile() == null){
                            lsContact.setContactProfile(lsContactProfile);
                            lsContact.save();
                        }
                        Log.d(TAG, "onResponse: lsContact: " + lsContact.toString());
                        if(lsInquiry != null ){
                            if(lsInquiry.getContactProfile() == null){
                                lsInquiry.setContactProfile(lsContactProfile);
                                lsInquiry.save();
                            }
                            Log.d(TAG, "onResponse: lsInquiry: " + lsInquiry.toString());
                        }
                    }
//                    }
//                    LeadContactAddedEventModel mCallEvent = new LeadContactAddedEventModel();
//                    TinyBus bus = TinyBus.from(mContext);
//                    bus.post(mCallEvent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e(TAG, "onErrorResponse: CouldNotFetchProfile of :" + contactNumber);
                try {
                    if (error.networkResponse != null) {
                        Log.e(TAG, "onErrorResponse: statusCode: " + error.networkResponse.statusCode);
                        if (error.networkResponse.statusCode == 404) {
                            Log.e(TAG, "onErrorResponse: ProfileNotFound");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
