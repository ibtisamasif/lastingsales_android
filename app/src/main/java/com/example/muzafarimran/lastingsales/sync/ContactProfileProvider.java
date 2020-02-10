package com.example.muzafarimran.lastingsales.sync;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.events.LeadContactAddedEventModel;
import com.example.muzafarimran.lastingsales.listeners.LSContactProfileCallback;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSContactProfile;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

import org.json.JSONException;
import org.json.JSONObject;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 10/20/2017.
 */

public class ContactProfileProvider {
    public static final String TAG = "ContactProfileProvider";
    Context mContext;
    private SessionManager sessionManager;
    private static RequestQueue queue;

    public ContactProfileProvider(Context context) {
        this.mContext = context;
        sessionManager = new SessionManager(context);
        queue = Volley.newRequestQueue(mContext);
    }

    public void getContactProfile(String number, final LSContactProfileCallback callback) {
        if (number != null) {
            LSContactProfile lsContactProfile = LSContactProfile.getProfileFromNumber(number);
            if (lsContactProfile != null) {
                Log.d(TAG, "getContactProfile: from LsContactProfileTable");
                // Search in LsContactProfile table and return
                callback.onSuccess(lsContactProfile);
            } else {
                Log.d(TAG, "getContactProfile: from Server");
                // Try to fetch from server and return
                fetchProfileFromServer(number, callback);
//                fetchProfileFromServer(number);
            }
        }
    }


    private void fetchProfileFromServer(final String contactNumber, final LSContactProfileCallback callback) {
        final LSContactProfile[] contactProfile = {null};
        String formatedNumber = "";
        if (contactNumber.charAt(0) == '+') {
            formatedNumber = contactNumber.substring(1);
            formatedNumber = formatedNumber.replaceAll("\\s", "").trim();
        }

        Log.d(TAG, "fetchProfileFromServer: Fetching Data... " + contactNumber);

        final int MY_SOCKET_TIMEOUT_MS = 60000;
        final String BASE_URL = MyURLs.GET_PROFILE;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter("phone", "" + formatedNumber)
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
                    Log.d(TAG, "onResponse: fb: " + fb);
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
                        newProfile.setFb(fb);
                        newProfile.setLinkd(linkd);
                        newProfile.setTweet(tweet);
                        newProfile.setInsta(insta);
                        newProfile.setWhatsapp(whatsapp);
                        newProfile.setCompany(company);
                        newProfile.setWork(work);
                        newProfile.setSocial_image(social_image);
                        newProfile.setComp_link(comp_link);
                        newProfile.save();
                        if (lsContact != null) {
                            lsContact.setContactProfile(newProfile);
                            lsContact.save();
                            Log.d(TAG, "onResponse: save Profile in Contact: " + lsContact.toString());
                        }
                        LSInquiry lsInquiry = LSInquiry.getPendingInquiryByNumberIfExists(intlNumber);
                        if (lsInquiry != null) {
                            lsInquiry.setContactProfile(newProfile);
                            lsInquiry.save();
                            Log.d(TAG, "onResponse: save Profile in PendingInquiry: " + lsInquiry.toString());
                            Log.d(TAG, "onResponse: GET FROM INQUIRIES AGAIN: " + LSInquiry.getPendingInquiryByNumberIfExists(intlNumber).toString());
                        }
                        callback.onSuccess(newProfile);
                        Log.d(TAG, "onResponse: ");
                    } else {
                        Log.d(TAG, "onResponse: Already Exists: " + lsContactProfile);

                        lsContactProfile.setFirstName(fname);
                        lsContactProfile.setLastName(lname);
                        lsContactProfile.setDob(dob);
                        lsContactProfile.setPhone(phone);
                        lsContactProfile.setAddress(address);
                        lsContactProfile.setCity(city);
                        lsContactProfile.setCountry(country);
                        lsContactProfile.setEmail(email);
                        lsContactProfile.setFb(fb);
                        lsContactProfile.setLinkd(linkd);
                        lsContactProfile.setTweet(tweet);
                        lsContactProfile.setInsta(insta);
                        lsContactProfile.setWhatsapp(whatsapp);
                        lsContactProfile.setCompany(company);
                        lsContactProfile.setWork(work);
                        lsContactProfile.setSocial_image(social_image);
                        lsContactProfile.setComp_link(comp_link);
                        lsContactProfile.save();

                        if (lsContact.getContactProfile() == null) {
                            lsContact.setContactProfile(lsContactProfile);
                            lsContact.save();
                            Log.d(TAG, "onResponse: lsContact: " + lsContact.toString());
                        }
                        LSInquiry lsInquiry = LSInquiry.getPendingInquiryByNumberIfExists(intlNumber);
                        if (lsInquiry != null) {
                            if (lsInquiry.getContactProfile() == null) {
                                lsInquiry.setContactProfile(lsContactProfile);
                                lsInquiry.save();
                                Log.d(TAG, "onResponse: lsInquiry: " + lsInquiry.toString());
                            }
                        }
                        callback.onSuccess(lsContactProfile);
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
}
