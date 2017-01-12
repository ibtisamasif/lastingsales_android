package com.example.muzafarimran.lastingsales.utils;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by ahmad on 06-May-16.
 */
public class VolleyRequestQueue
{
    private static RequestQueue requestQueue;

    public static RequestQueue getQueue(Context _context)
    {
        if (requestQueue == null)
        {
            requestQueue = Volley.newRequestQueue(_context);

        }
        return requestQueue;
    }

}
