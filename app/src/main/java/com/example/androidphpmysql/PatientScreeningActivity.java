package com.example.androidphpmysql;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class PatientScreeningActivity extends AppCompatActivity {
    private Switch switch1, switch2, switch3, switch4, switch5, switch6;
    private Button submitCovidScreen;
    private CheckBox checkboxAgree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_screening);

        Intent intent = getIntent();
        final String appID = intent.getStringExtra("appID");

        //initialising switches
        switch1 = (Switch) findViewById(R.id.switch1);
        switch2 = (Switch) findViewById(R.id.switch2);
        switch3 = (Switch) findViewById(R.id.switch3);
        switch4 = (Switch) findViewById(R.id.switch4);
        switch5 = (Switch) findViewById(R.id.switch5);
        switch6 = (Switch) findViewById(R.id.switch6);
        checkboxAgree = (CheckBox) findViewById(R.id.checkboxAgree);

        submitCovidScreen = (Button) findViewById(R.id.submitCovidScreen);
        submitCovidScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switch1.isChecked() == true || switch2.isChecked() == true || switch3.isChecked() == true || switch4.isChecked() == true || switch5.isChecked() == true || switch6.isChecked() == true) {
                    Toast.makeText(getApplicationContext(), "Contact surgery immediately", Toast.LENGTH_LONG).show();
                    View view = View.inflate(PatientScreeningActivity.this, R.layout.link, null);
                    TextView textView = (TextView) view.findViewById(R.id.message);
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                    textView.setText(Html.fromHtml("<a href ='https://donalblackwell.com/contact-us/'>Click Here</a>"));
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(PatientScreeningActivity.this);
                    builder1.setTitle("Alert!");
                    builder1.setView(view);
                    builder1.setMessage("Please contact the dental surgery IMMEDIATELY and inform the staff of your Covid-19 status");
                    builder1.setCancelable(false);
                    builder1.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    AlertDialog Alert1 = builder1.create();
                    Alert1.show();

                }else if(switch1.isChecked() == false && switch2.isChecked() == false && switch3.isChecked() == false && switch4.isChecked() == false && switch5.isChecked() == false && switch6.isChecked() == false && checkboxAgree.isChecked()==true) {
                    //put in the insert to the database here
                    Toast.makeText(PatientScreeningActivity.this, "submitted", Toast.LENGTH_SHORT).show();

                    insertScreening();
                    finish();
                }else{
                    Toast.makeText(PatientScreeningActivity.this, "Agree to the terms please", Toast.LENGTH_SHORT).show();
                }
                
            }
        });
        //stackoverflow resource for link in the dialog
        //https://stackoverflow.com/questions/1997328/how-can-i-get-clickable-hyperlinks-in-alertdialog-from-a-string-resource
    }

    private void insertScreening(){

        Intent intent = getIntent();
        final String appID = intent.getStringExtra("appID");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String appointmentid = appID;
        final Date date = new Date();
        final String screeningDate = sdf.format(date);
        final String screenPassed = "Passed";



        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_SUBMIT_SCREENING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                getApplicationContext(),
                                error.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("appointmentid", String.valueOf(appointmentid));
                params.put("screeningdate", screeningDate);
                params.put("status", screenPassed);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}
