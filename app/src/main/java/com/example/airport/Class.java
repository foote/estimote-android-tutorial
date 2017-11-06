package com.example.airport;

/**
 * Created by foote on 11/4/2017.
 */

import android.util.Log;

public class Class
{
    public String id;
    public String name;
    public String section;
    public String term;

    private static final String TAG = "Airport";

    public Class(String _id, String _name)
    {
        id= _id;
        name = _name;

        Log.println(Log.DEBUG, TAG, "Constructor : " + _id + ":" + _name);
    }

    public String toString()
    {
        return( name );
    }

}