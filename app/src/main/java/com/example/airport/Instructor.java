package com.example.airport;

import android.util.Log;

public class Instructor
{
    public String id;
    public String firstname;
    public String lastname;
    public String active;

    private static final String TAG = "Airport";

    public Instructor(String _id, String _firstname, String _lastname, String _active)
    {
        id= _id;
        firstname = _firstname;
        lastname = _lastname;
        active = _active;

        Log.println(Log.DEBUG, TAG, "Constructor : " + _id + ":" + _lastname);
    }



    public String toString()
    {
        return( lastname + ", " + firstname );
    }

}
