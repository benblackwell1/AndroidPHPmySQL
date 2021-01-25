package com.example.androidphpmysql;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class PatientMedicalActivity extends AppCompatActivity {
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    private TextView textViewUserID;
    ListView listView;
    //will use this list to display in the listview
    List<Medical> medicalList;
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

        listView = (ListView) findViewById(R.id.listViewPatients);

    }
}
