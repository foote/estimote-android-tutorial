package com.example.airport;

import android.util.Log;
import android.util.Log;

public class Response {



        public String id;
        public String responsemessage;
        public String isError;

        private static final String TAG = "RollCall";

        public Response(String _id, String _msg, String _isError)
        {
            id= _id;
            responsemessage = _msg;
            isError = _isError;

            android.util.Log.println(Log.DEBUG, TAG, "Constructor : " + _id + ":" + _msg);
        }

        public String toString()
        {
            return( responsemessage );
        }

    }
