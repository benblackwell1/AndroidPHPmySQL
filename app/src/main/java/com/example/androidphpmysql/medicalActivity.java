package com.example.androidphpmysql;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class medicalActivity extends AppCompatActivity implements View.OnClickListener {
    //defining the textviews
    private TextView textViewPatientID;
    private EditText  editDOB, editWeight, editAlcohol, editSmoking, editUnderlyingConditions, editAllergies, editMedication;
    private RadioGroup editGender;
    private Button buttonUpload;
    private ProgressDialog progressDialog;
    String role = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical);

        //if the user is not logged in it will stop the activity
        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            //this will then start the activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        //if the user has uploaded their medical history already, then they will be checked if the recorded is in the database
//        if(!SharedPrefManager.getInstance(this).isUploaded()){
//            //**add a toast
////            Toast.makeText(getApplicationContext(), "medical uploaded already", Toast.LENGTH_SHORT).show();
//            finish();
//            //this will then start the activity
//            startActivity(new Intent(this, ProfileActivity.class));
//        }


        //initializing the textView that will hold the users ID
        textViewPatientID = (TextView) findViewById(R.id.textViewPatientID);
//        //getting the shared preference ID with the getUserID function ---> received help from Stackoverflow --> 'how to set text an integer and get int without getting error'
        //grabbing this id from shared preferences will help me by inputting it as a foregin key in the related table of the database
        textViewPatientID.setText(String.valueOf(SharedPrefManager.getInstance(this).getUserId()));

        editGender = (RadioGroup)findViewById(R.id.radioGroup);
        editDOB = (EditText) findViewById(R.id.editDOB);
        editWeight = (EditText) findViewById(R.id.editWeight);
        editAlcohol = (EditText) findViewById(R.id.editAlcohol);
        editSmoking = (EditText) findViewById(R.id.editSmoking);
        editUnderlyingConditions = (EditText) findViewById(R.id.editUnderlyingConditions);
        editAllergies = (EditText) findViewById(R.id.editAllergies);
        editMedication = (EditText) findViewById(R.id.editMedication);
        editDOB = (EditText) findViewById(R.id.editDOB);

        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        progressDialog = new ProgressDialog(this);
        buttonUpload.setOnClickListener(this);

    }
    private void registerMedical() {
        final int patientid = Integer.parseInt(textViewPatientID.getText().toString().trim());
//        final String gender = role;
        final String age = editDOB.getText().toString().trim();
        final String weight = editWeight.getText().toString().trim();
        final String alcohol = editAlcohol.getText().toString().trim();
        final String smoking = editSmoking.getText().toString().trim();
        final String underlyingcondition = editUnderlyingConditions.getText().toString().trim();
        final String allergy = editAllergies.getText().toString().trim();
        final String medication = editMedication.getText().toString().trim();
        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        final RadioButton radioMale = (RadioButton) findViewById(R.id.radioMale);
        final RadioButton radioFemale = (RadioButton) findViewById(R.id.radioFemale);

        if (radioGroup.getCheckedRadioButtonId() == radioMale.getId())
        {
            role = "Male";
        }
        else if (radioGroup.getCheckedRadioButtonId() == radioFemale.getId())
        {
            role = "Female";
        }
        progressDialog.setMessage("Registering Medical...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_MEDICAL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
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
                        progressDialog.hide();
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
                params.put("patientid", String.valueOf(patientid));
                params.put("gender", role);
                params.put("age", age);
                params.put("weight", weight);
                params.put("alcohol", alcohol);
                params.put("smoking", smoking);
                params.put("underlyingcondition", underlyingcondition);
                params.put("allergy", allergy);
                params.put("medication", medication);

                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
        @Override
    public void onClick(View v) {
    if (v == buttonUpload)
    {
        registerMedical();
    }
    }
    //override method for logout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //code to include menu item
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    //this method is for when the logout option is selected from the menu
    //the method will log you out
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.menuLogout: // this take the menu id from the menu.xml
                SharedPrefManager.getInstance(this).logout();//this calls the logout method created in shared preference manager
                finish();
                startActivity(new Intent(this, LoginActivity.class));//when logged out it will start the LoginActivity
                break;
            case R.id.menuUpload:
//            Toast.makeText(this,"You clicked settings", Toast.LENGTH_LONG).show();
                finish();
                startActivity(new Intent(this, medicalActivity.class));
                break;
            case R.id.menuProfile:
//            Toast.makeText(this,"You clicked settings", Toast.LENGTH_LONG).show();
                finish();
                startActivity(new Intent(this, ProfileActivity.class));
                break;
        }
        return true;
    }
}
