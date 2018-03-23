package com.example.airport;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.widget.Toast;
import android.util.Log;

import com.estimote.coresdk.cloud.api.CloudCallback;
import com.estimote.coresdk.cloud.api.EstimoteCloud;
import com.estimote.coresdk.cloud.model.BeaconInfo;
import com.estimote.coresdk.common.config.EstimoteSDK;
import com.estimote.coresdk.common.exception.EstimoteCloudException;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

public class MyApplication extends Application {

    private BeaconManager beaconManager;
    private static final String TAG = "RollCall";
    private static final String ApiKey = "456789";
    ArrayList<Class> classes = new ArrayList<Class>();
    ArrayList<Instructor> instructors = new ArrayList<Instructor>();
    String classId;

    @Override
    public void onCreate() {
        super.onCreate();

        //EstimoteSDK.initialize(getApplicationContext(), "<Here goes your application ID>", "<>Here goes your application token");
        EstimoteSDK.initialize(getApplicationContext(), "foote-fvtc-edu-s-your-own--jtm", "5ca18641e78c658c49a2498ba752d5d4");

        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.setMonitoringListener(new BeaconManager.BeaconMonitoringListener() {

            public void onEnteredRegion(BeaconRegion beaconRegion, List<Beacon> beacons) {
                Log.d(TAG, "onEnteredRegion : " + beacons.size());

                try {
                    GetInstructors();
                } catch (JSONException e) {
                    Log.e(TAG, "exception", e);
                    e.printStackTrace();
                }

                for (Beacon b : beacons) {
                    Log.d(TAG, String.format("%d:%d:%s", b.getMajor(), b.getMinor(), b.toString()));

                    EstimoteCloud.getInstance().fetchBeaconDetails(b.getProximityUUID(), b.getMajor(), b.getMinor(), new CloudCallback<BeaconInfo>() {
                        @Override
                        public void success(BeaconInfo beaconInfo) {
                            Log.d(TAG+" ==========", String.valueOf(beaconInfo.toString()));
                           // Log.d(TAG+" ==========", beaconInfo.getClass().getAnnotations());
                            Toast.makeText(getApplicationContext(), "onEnteredRegion:"+ beaconInfo.name, Toast.LENGTH_LONG).show();

                            //classId = beaconInfo.settings[TAG]
                        }

                        @Override
                        public void failure(EstimoteCloudException e) {
                            Log.e(TAG, "BEACON INFO ERROR: " + e);
                        }
                    });
                }


                String response = SubmitRollCall();
                showNotification(
                        "RollCall Submitted.", response);

            }

            @Override
            public void onExitedRegion(BeaconRegion beaconRegion) {
                Log.d(TAG, "onExitedRegion");
                Toast.makeText(getApplicationContext(), "onExitedRegion:", Toast.LENGTH_LONG).show();
                cancelNotification();

            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {  
                Log.d("Airport", "onServiceReady");
                //beaconManager.startMonitoring(new BeaconRegion("monitored region",
                //        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 12988, 22472));
                beaconManager.startMonitoring(new BeaconRegion("monitored region",
                                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null));
            }
        });
    }

    public String SubmitRollCall()
    {
        String studentId = "200175160";
        String classId = "6F4502E2-798D-480A-A473-DD08F31F3BF6";

        String hashtag = "arizona";

        Log.d(TAG, "SubmitRollCall");

        // Add client web service call
        RestClient client = new RestClient("http://rollcallrest.azurewebsites.net/api/RollCall");
        client.AddHeader("X-ApiKey", ApiKey);

        SharedPreferences prefs = getSharedPreferences("RollCall", MODE_PRIVATE);
        studentId = prefs.getString("studentid", null);

        Log.d(TAG, "StudentId : " + studentId);

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("StudentNo", studentId));
            nameValuePairs.add(new BasicNameValuePair("ClassId", classId));
            nameValuePairs.add(new BasicNameValuePair("HashTag", hashtag));
            for(NameValuePair v : nameValuePairs) {
                client.AddParam(v.getName(), v.getValue());
            }
            client.Execute(RestClient.RequestMethod.POST);
            String response = client.getResponse();

            Log.d(TAG, response);
            return response;

        } catch (Exception e) {
            Log.e(TAG, "exception", e);
            return "Error";
        }

    }

    public void GetInstructors() throws JSONException
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Log.d(TAG, "GetInstructors");

        // Add client web service call
        RestClient client = new RestClient("http://rollcallrest.azurewebsites.net/api/Instructor/GetInstructorList");
        client.AddHeader("X-ApiKey", ApiKey);

        try
        {
            client.Execute(RestClient.RequestMethod.GET);

        } catch (Exception e) {
            Log.e(TAG, "exception", e);
            //e.printStackTrace();
        }

        instructors = new ArrayList<Instructor>();

        String response = client.getResponse();
        Log.d("Airport", "Response : " + response);

        JSONArray json = new JSONArray(response);
        Log.d("Airport", "jObject : " + json);

        for (int i=0; i < json.length(); i++)
        {
            try {
                JSONObject oneObject = json.getJSONObject(i);
                // Pulling items from the array
                Instructor instructor = new Instructor(
                        oneObject.getString("Id").toString(),
                        oneObject.getString("Firstname"),
                        oneObject.getString("Lastname"),
                        oneObject.getString("Active"));
                Log.d(TAG, instructor.toString());
                instructors.add((instructor));

            } catch (JSONException e) {
                Log.e("Airport", "exception", e);
            }
        }

    }
    public void cancelNotification() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }

    public void showNotification(String title, String message)  {
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);

    }
}
