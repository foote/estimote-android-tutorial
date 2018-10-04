package com.example.airport;

import android.os.AsyncTask;
import android.util.*;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by foote on 3/26/2018.
 */

class MessageTask extends AsyncTask<Void, Void, String> {

    private Exception exception;
    private static final String TAG = "RollCall";
    private static final String ApiKey = "456789";
    private String _message;
    public MessageTask(String message) {
        _message = message;
    }

    protected String doInBackground(Void... voids) {
        try {
            RestClient client = new RestClient("http://rollcallrest.azurewebsites.net/api/Message");
            client.AddHeader("X-ApiKey", ApiKey);

            String updatedtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("name", "Android"));
            nameValuePairs.add(new BasicNameValuePair("updatetime", updatedtime));
            nameValuePairs.add(new BasicNameValuePair("text", _message));

            for(NameValuePair v : nameValuePairs) {
                client.AddParam(v.getName(), v.getValue());
            }

            client.Execute(RestClient.RequestMethod.POST);
            String response = client.getResponse();

            return response;


        } catch (Exception e) {
            this.exception = e;
            return "false";

        } finally {
        }
    }

    protected void onPostExecute(String feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }


}


