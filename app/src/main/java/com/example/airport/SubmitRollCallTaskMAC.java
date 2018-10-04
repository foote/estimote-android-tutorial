package com.example.airport;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by foote on 3/26/2018.
 */

class SubmitRollCallTaskMAC extends AsyncTask<Void, Void, String> {

    private Exception exception;
    private static final String TAG = "RollCall";
    private static final String ApiKey = "456789";
    private String _studentId;
    private String _macAddress;
    private String _hashTag;
    public SubmitRollCallTaskMAC(String studentId, String macAddress) {
        _studentId = studentId;
        _macAddress = macAddress;
    }

    protected String doInBackground(Void... voids) {
        try {
            RestClient client = new RestClient("http://rollcallrest.azurewebsites.net/api/RollCall/Submit");
            client.AddHeader("X-ApiKey", ApiKey);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("studentid", _studentId));
            nameValuePairs.add(new BasicNameValuePair("macaddress", _macAddress));

            for(NameValuePair v : nameValuePairs) {
                client.AddParam(v.getName(), v.getValue());
            }

            client.Execute(RestClient.RequestMethod.POST);
            String response = client.getResponse();
            Log.d(TAG, _macAddress + " : " + _studentId + " : " + response);
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


