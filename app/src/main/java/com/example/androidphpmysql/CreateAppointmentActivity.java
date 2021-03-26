package com.example.androidphpmysql;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.layout.simple_spinner_item;

public class CreateAppointmentActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    EditText date_time_in;
    Spinner spinnerPatient;
    Button buttonCreateAppointment;
    EditText appointmentDesc;
    TextView textViewPatID;
    //will use this list to display in the spinner
    List<Patient> patientList;

    private ArrayList names = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_appointment);
        date_time_in = findViewById(R.id.date_time_input);

        //disabling the keyboard when the datetime edittext is clicked
        date_time_in.setInputType(InputType.TYPE_NULL);

        //on click listener to create dialog for the date-time
        date_time_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(date_time_in);
            }
        });

        appointmentDesc = (EditText) findViewById(R.id.editAppointmentFor);
        spinnerPatient = (Spinner) findViewById(R.id.spinnerPatient);
        textViewPatID = (TextView) findViewById(R.id.textViewPatID);
        patientList = new ArrayList<>();
        readPatients();

        //onClick listener
        buttonCreateAppointment = (Button) findViewById(R.id.buttonCreateAppointment);
        buttonCreateAppointment.setOnClickListener(this);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
    //function to show the dialog
    private void showDateTimeDialog(final EditText date_time_in){
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute ){
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        //formatting the date time
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        //setting the value to the editText
                        date_time_in.setText(simpleDateFormat.format(calendar.getTime()));

                    }
                };
                //creating the time dialog after the date has been picked
                new TimePickerDialog(CreateAppointmentActivity.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),false).show();

            }
        };
        //disabling previous dates so they cant be selected

        //creating date dialog for date to be picked.
        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateAppointmentActivity.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
        datePickerDialog.show();
    }


    private void readPatients(){
        CreateAppointmentActivity.PerformNetworkRequest request = new CreateAppointmentActivity.PerformNetworkRequest(Constants.URL_PATIENTS_RETRIEVE, null, CODE_GET_REQUEST);
        request.execute();
    }
    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {

        //the url where we need to send the request
        String url;

        //the parameters
        HashMap<String, String> params;

        //the request code to define whether it is a GET or POST
        int requestCode;

        //constructor to initialize values
        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        //when the task started displaying a progressbar
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressBar.setVisibility(View.VISIBLE);
        }


        //this method will give the response from the request
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            progressBar.setVisibility(View.INVISIBLE);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    //refreshing the patientList after every operation
                    //so we get an updated list
                    //we will create this method right now it is commented
                    //because we haven't created it yet
                    refreshPatientList(object.getJSONArray("patients"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //the network operation will be performed in background
        @Override
        protected String doInBackground(Void... voids) {
            PatientRequestHandler requestHandler = new PatientRequestHandler();

            if (requestCode == CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);


            if (requestCode == CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }

    private void refreshPatientList(JSONArray patients) throws JSONException {
        //clearing previous patients
        patientList.clear();

        //traversing through all the items in the json array
        //the json we got from the response
        for (int i = 0; i < patients.length(); i++) {
            //getting each patient object
            JSONObject obj = patients.getJSONObject(i);

            //adding the patient to the list
            patientList.add(new Patient(
                    obj.getInt("id"),
                    obj.getInt("userid"),
                    obj.getString("fname"),
                    obj.getString("lname"),
                    obj.getString("street"),
                    obj.getString("city"),
                    obj.getString("zip"),
                    obj.getString("phone"),
                    obj.getString("prsi")
            ));
        }

        //traversing the array of patients and putting their names in another array //https://demonuts.com/android-populate-spinner-from-json/
        for (int i = 0; i < patientList.size(); i++){
            names.add(patientList.get(i).getId() + " " + patientList.get(i).getFname() + " " + patientList.get(i).getLname());
        }
        names.add(0, "Choose Patient");

        //adapter to put the patients names in the spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(CreateAppointmentActivity.this, simple_spinner_item, names);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinnerPatient.setAdapter(spinnerArrayAdapter);
//
        //**need to get the id of the patient with an onItemSelectedListener
        //adjust the hashmap accordingly.
        spinnerPatient.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String nameSelected = spinnerPatient.getItemAtPosition(position).toString();
                int nameid;
                for(int i = 0; i <patientList.size(); i++){
                    if (nameSelected.equals("Choose Patient")){
                        textViewPatID.setText("Choose Patient");
                    }
                    if(nameSelected.equals(patientList.get(i).getId() + " " + patientList.get(i).getFname() + " " + patientList.get(i).getLname())) {
                        nameid = patientList.get(i).getId();
                        textViewPatID.setText(Integer.toString(nameid));

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
}


    @Override
    public void onClick(View v) {
        if (v == buttonCreateAppointment)
        {
            addAppointment();
        }
    }

    private void addAppointment(){


        final String patID = textViewPatID.getText().toString().trim();
        final String appDesc = appointmentDesc.getText().toString().trim();
        final String appDate = date_time_in.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_CREATE_APPOINTMENT,
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
                params.put("patientid", patID);
                params.put("appdate", appDate);
                params.put("appdesc", appDesc);

                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

        //***Need to have a response after the record has been added
        //also need to hide the patientID

        AppointmentCreated();

    }
    private void AppointmentCreated(){
        //go back to the appointment main page
        startActivity(new Intent(this, AppointmentMainActivity.class));
        finish();
    }
}

