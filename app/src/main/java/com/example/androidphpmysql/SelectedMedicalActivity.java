package com.example.androidphpmysql;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SelectedMedicalActivity extends AppCompatActivity {
    private TextView textViewMedicalID, textViewGender,textViewAge,textViewWeight,textViewAlcohol,textViewSmoking,textViewUnderlyingCondition,textViewAllergy,textViewMedication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_medical);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String MedID = extras.getString("MedID");
        String PatID = extras.getString("PatID");
        String Gender = extras.getString("Gender");
        String Age = extras.getString("Age");
        String Weight = extras.getString("Weight");
        String Alcohol = extras.getString("Alcohol");
        String Smoking = extras.getString("Smoking");
        String UnderlyingCondition = extras.getString("UnderlyingCondition");
        String Allergy = extras.getString("Allergy");
        String Medication = extras.getString("Medication");

        textViewMedicalID = (TextView) findViewById(R.id.textViewMedicalID);
        textViewMedicalID.setText(MedID);

        textViewGender = (TextView) findViewById(R.id.textViewGender);
        textViewGender.setText(Gender);

        textViewAge = (TextView) findViewById(R.id.textViewAge);
        textViewAge.setText(Age);

        textViewWeight = (TextView) findViewById(R.id.textViewWeight);
        textViewWeight.setText(Weight);

        textViewAlcohol = (TextView) findViewById(R.id.textViewAlcohol);
        textViewAlcohol.setText(Alcohol);

        textViewSmoking = (TextView) findViewById(R.id.textViewSmoking);
        textViewSmoking.setText(Smoking);

        textViewUnderlyingCondition = (TextView) findViewById(R.id.textViewUnderlyingCondition);
        textViewUnderlyingCondition.setText(UnderlyingCondition);

        textViewAllergy = (TextView) findViewById(R.id.textViewAllergy);
        textViewAllergy.setText(Allergy);

        textViewMedication = (TextView) findViewById(R.id.textViewMedication);
        textViewMedication.setText(Medication);

//Code to pass a bundle to the next activity
//        https://stackoverflow.com/questions/8452526/android-can-i-use-putextra-to-pass-multiple-values


        //back button code
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

}
