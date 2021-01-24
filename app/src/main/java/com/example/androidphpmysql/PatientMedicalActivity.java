package com.example.androidphpmysql;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class PatientMedicalActivity extends AppCompatActivity {

    private TextView textViewUserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_medical);

        Intent intent = getIntent();
        String userid = intent.getStringExtra("Userid");
        textViewUserID = (TextView) findViewById(R.id.textViewUserID);
        textViewUserID.setText(userid);
        //code to pass the integer as a string
        //https://stackoverflow.com/questions/37627328/passing-array-data-using-bundle-to-another-activity-on-listview-click
    }
}
