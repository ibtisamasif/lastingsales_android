package com.example.muzafarimran.lastingsales.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.example.muzafarimran.lastingsales.activities.NavigationBottomMainActivity;

import org.apache.commons.net.time.TimeTCPClient;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by ibtisam on 1/17/2018.
 */

public class GetTimeFromInternetASync extends AsyncTask<Void, Void, Void> {
    public static final String TAG = "GetTimeFromInternetASyn";

    public GetTimeFromInternetASync(NavigationBottomMainActivity navigationBottomMainActivity) {

    }

    @Override
    protected Void doInBackground(Void... voids) {
//        getTime();
        getAppacheTime();
        return null;
    }

    public void getAppacheTime() {
        try {
            TimeTCPClient client = new TimeTCPClient();
            try {
                // Set timeout of 60 seconds
                client.setDefaultTimeout(60000);
                // Connecting to time server
                // Other time servers can be found at : http://tf.nist.gov/tf-cgi/servers.cgi#
                // Make sure that your program NEVER queries a server more frequently than once every 4 seconds
                client.connect("nist.time.nosc.us");
                Log.d(TAG, "onReceive: client.getDate(): " + client.getDate());
            } finally {
                client.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getTime() {
        try {
            //Make the Http connection so we can retrieve the time
            HttpClient httpclient = new DefaultHttpClient();
            // I am using yahoos api to get the time
            HttpResponse response = httpclient.execute(new
                    HttpGet("http://developer.yahooapis.com/TimeService/V1/getTime?appid=YahooDemo"));
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                // The response is an xml file and i have stored it in a string
                String responseString = out.toString();
                Log.d(TAG, responseString);
                //We have to parse the xml file using any parser, but since i have to
                //take just one value i have deviced a shortcut to retrieve it
                int x = responseString.indexOf("<Timestamp>");
                int y = responseString.indexOf("</Timestamp>");
                //I am using the x + "<Timestamp>" because x alone gives only the start value
                Log.d(TAG, responseString.substring(x + "<Timestamp>".length(), y));
                String timestamp = responseString.substring(x + "<Timestamp>".length(), y);
                // The time returned is in UNIX format so i need to multiply it by 1000 to use it
                Date d = new Date(Long.parseLong(timestamp) * 1000);
                Log.d(TAG, d.toString());
                return d.toString();
            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            Log.d(TAG, e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
        return null;
    }
}
