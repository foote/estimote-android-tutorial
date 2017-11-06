package com.example.airport;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final Map<String, List<String>> PLACES_BY_BEACONS;
    private static final String TAG = "RollCall";


    // TODO: replace "<major>:<minor>" strings to match your own beacons.
    static {
        Map<String, List<String>> placesByBeacons = new HashMap<>();

        placesByBeacons.put("46576:29665", new ArrayList<String>() {{
            add("mallard");
            // read as: "Heavenly Sandwiches" is closest
            // to the beacon with major 22504 and minor 48827
            add("pintail");
            // "Green & Green Salads" is the next closest
            add("bears");
            // "Mini Panini" is the furthest away
        }});
        placesByBeacons.put("12988:22472", new ArrayList<String>() {{
            add("pintail");
            add("bears");
            add("mallard");
        }});

        placesByBeacons.put("5736:57752", new ArrayList<String>() {{
            add("bears");
            add("pintail");
            add("mallard");
        }});

        PLACES_BY_BEACONS = Collections.unmodifiableMap(placesByBeacons);
    }

    private List<String> placesNearBeacon(Beacon beacon) {
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        if (PLACES_BY_BEACONS.containsKey(beaconKey)) {
            return PLACES_BY_BEACONS.get(beaconKey);
        }
        return Collections.emptyList();
    }

    private BeaconManager beaconManager;
    private BeaconRegion region;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // If they student is not set, show the correct acitvity.
        SharedPreferences prefs = getSharedPreferences("RollCall", MODE_PRIVATE);
        String studentid = prefs.getString("studentid", null);

        Log.d(TAG, "StudentId : " + studentid);

        Button btnSetStudent = (Button)findViewById(R.id.btnSetStudent);
        btnSetStudent.setOnClickListener(new View.OnClickListener() {


            public void onClick(View view) {
                Intent intent2 = new Intent(MainActivity.this, SetStudentActivity.class);
                startActivity(intent2);
            }

        });


        Button btnClear = (Button)findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Log.d(TAG+" ==========", "Clearing StudentId...");
                SharedPreferences.Editor editor = getSharedPreferences("RollCall", MODE_PRIVATE).edit();
                editor.putString("studentid", null);
                editor.commit();

                SharedPreferences prefs = getSharedPreferences("RollCall", MODE_PRIVATE);
                String studentid = prefs.getString("studentid", null);
                Log.d(TAG+" ==========", "Getting : " + studentid);
                TextView tv = (TextView)findViewById(R.id.txtStudentId);
                tv.setText("StudentId: " + studentid);


            }
        });

        if (studentid == null)
        {
            Intent intent = new Intent(MainActivity.this, SetStudentActivity.class);
            startActivity(intent);
        }
        else
        {
            TextView tv = (TextView)findViewById(R.id.txtStudentId);
            tv.setText("StudentId: " + studentid);
        }

        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {

            @Override
            public void onBeaconsDiscovered(BeaconRegion beaconRegion, List<Beacon> beacons) {
                if (!beacons.isEmpty()) {
                    Beacon nearestBeacon = beacons.get(0);
                    List<String> places = placesNearBeacon(nearestBeacon);

                    //Log.d("Airport", "Nearest places: " + places);
                    // TODO: update the UI here
                    TextView et = (TextView)findViewById(R.id.txtInfo);
                    et.setText("Nearest places: " + places);
                }

            }
        });

        region = new BeaconRegion("ranged region", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
