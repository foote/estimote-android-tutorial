package com.example.airport;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.*;

/**
 * Created by foote on 11/4/2017.
 */

public class SetStudentActivity extends Activity {
    private static final String TAG = "RollCall";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setstudent);

        Button btnSave = (Button)findViewById(R.id.btnSave);

        SharedPreferences prefs = getSharedPreferences("RollCall", MODE_PRIVATE);
        String studentid = prefs.getString("studentid", null);
        EditText txtStudent = (EditText)findViewById(R.id.txtStudentId);
        txtStudent.setText(studentid);

        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                EditText txtStudent = (EditText)findViewById(R.id.txtStudentId);
                Log.d(TAG+" ==========", "Saving : " + txtStudent.getText().toString());
                SharedPreferences.Editor editor = getSharedPreferences("RollCall", MODE_PRIVATE).edit();
                editor.putString("studentid", txtStudent.getText().toString());
                editor.commit();

                SharedPreferences prefs = getSharedPreferences("RollCall", MODE_PRIVATE);
                String studentid = prefs.getString("studentid", null);
                Log.d(TAG+" ==========", "Getting : " + studentid);
                finish();
            }
        });
        Button btnBack = (Button)findViewById(R.id.Back);

        btnBack.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                finish();
            }

        });
    }
}
