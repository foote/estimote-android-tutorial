package com.example.airport;

import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by foote on 3/26/2018.
 */

class SubmitRollCallTask extends AsyncTask<Void, Void, String> {

    private Exception exception;
    private static final String TAG = "RollCall";
    private static final String ApiKey = "456789";
    private String _studentId;
    private String _classId;
    private String _hashTag;
    public SubmitRollCallTask(String studentId, String classId, String hashTag) {
        _studentId = studentId;
        _classId = classId;
        _hashTag = hashTag;
    }

    protected String doInBackground(Void... voids) {
        try {
            RestClient client = new RestClient("http://rollcallrest.azurewebsites.net/api/RollCall");
            client.AddHeader("X-ApiKey", ApiKey);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("StudentNo", _studentId));
            nameValuePairs.add(new BasicNameValuePair("ClassId", _classId));
            nameValuePairs.add(new BasicNameValuePair("HashTag", _hashTag));

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


