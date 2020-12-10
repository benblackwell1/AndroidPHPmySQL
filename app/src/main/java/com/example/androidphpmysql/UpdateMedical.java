package com.example.androidphpmysql;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

public class UpdateMedical extends AppCompatActivity {
    //defining the text views
    private TextView textViewPatientID,textviewGender, textviewDOB;
    private EditText  editWeight, editAlcohol, editSmoking, editUnderlyingConditions, editAllergies, editMedication;
    private Button buttonUpdate;
    private ProgressDialog progressDialog;
    String role = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_medical);
        //if the user is not logged in it will stop the activity
        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            //this will then start the activity
            startActivity(new Intent(this, LoginActivity.class));
        }
        //initializing the textView that will hold the users ID
        textViewPatientID = (TextView) findViewById(R.id.textViewPatientID);
        textViewPatientID.setText(String.valueOf(SharedPrefManager.getInstance(this).getUserId()));

        //setting the text view that wont be able to change in the update
        textviewGender = (TextView) findViewById(R.id.textViewGender);
        textviewDOB = (TextView) findViewById(R.id.textViewDOB);

        //setting the edit texts fields so that they can display the data that is retrieved from the database
        editWeight = (EditText) findViewById(R.id.editWeightUpdate);
        editAlcohol = (EditText) findViewById(R.id.editAlcoholUpdate);
        editSmoking = (EditText) findViewById(R.id.editSmokingUpdate);
        editUnderlyingConditions = (EditText) findViewById(R.id.editUnderlyingConditionsUpdate);
        editAllergies = (EditText) findViewById(R.id.editAllergiesUpdate);
        editMedication = (EditText) findViewById(R.id.editMedicationUpdate);

        //must create a constants class for the url path to the php file that will update the database record.
        //e.g select ... from ... where id=id

            //retrieve medical record associated to the patientid
            sendGetRequest();
    }
    private void sendGetRequest(){
        final String patientid = String.valueOf(SharedPrefManager.getInstance(this).getUserId());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                Constants.URL_MEDICAL_RETRIEVE + "?patientid=" + patientid , new Response.Listener<String>() {
            @Override
            //successful response
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    int id = obj.getInt("medical id");
                    int patientid = obj.getInt("patientid");
                    String gender = obj.getString("gender");
                    String age = obj.getString("age");
                    String weight = obj.getString("weight");
                    String alcohol = obj.getString("alcohol");
                    String smoking = obj.getString("smoking");
                    String underlyingcondition = obj.getString("underlyingcondition");
                    String allergy = obj.getString("allergy");
                    String medication = obj.getString("medication");

                    textviewGender.append(gender);
                    textviewDOB.append(age);
                    editWeight.append(weight);
                    editAlcohol.append(alcohol);
                    editSmoking.append(smoking);
                    editUnderlyingConditions.append(underlyingcondition);
                    editAllergies.append(allergy);
                    editMedication.append(medication);

                }
                catch (JSONException e){
                    e.printStackTrace();
                }
//                get_response_text.setText("Data: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            //failed response
            public void onErrorResponse(VolleyError error) {
                textviewGender.setText("Data : response failed");
                textviewDOB.setText("Data : response failed");

            }
        });
                RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
//received help from this video -> https://www.youtube.com/watch?v=y2xtLqP8dSQ
    }
//    private void updateMedical(){
//        final int patientid = Integer.parseInt(textViewPatientID.getText().toString().trim());
//        final String gender = textViewGender.getText().toString().trim();
//        final String age = textViewDOB.getText().toString().trim();
//
//    }
}
