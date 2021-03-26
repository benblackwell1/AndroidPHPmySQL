package com.example.androidphpmysql;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AppointmentHistoryActivity extends AppCompatActivity {
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    ListView listView;
    List<Appointment> appointmentList;
    private ArrayList appDates = new ArrayList<>();
    List<Patient> patientList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_history);

        listView = (ListView) findViewById(R.id.listViewPastAppointments);
        appointmentList = new ArrayList<>();
        patientList = new ArrayList<>();


        //back button code
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        readPatients();
        readAppointments();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }


    private void readAppointments(){
        PerformNetworkRequest request = new PerformNetworkRequest(Constants.URL_APPOINTMENTS_RETRIEVE, null, CODE_GET_REQUEST);
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
                    //refreshing the appointmentList after every operation
                    //so we get an updated list
                    refreshAppointmentList(object.getJSONArray("appointments"));
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
    private void refreshAppointmentList(JSONArray appointments) throws JSONException {
        //clearing previous patients
        appointmentList.clear();

        //traversing through all the items in the json array
        //the json we got from the response
        for (int i = 0; i < appointments.length(); i++) {
            //getting each patient object
            JSONObject obj = appointments.getJSONObject(i);

            //adding the patient to the list
            appointmentList.add(new Appointment(
                    obj.getInt("id"),
                    obj.getInt("patientid"),
                    obj.getString("appdate"),
                    obj.getString("appdesc")
            ));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date today = new Date();
        for(int i = 0; i < appointmentList.size(); i++) {
            for (int y = 0; y < patientList.size(); y++) {
                if (patientList.get(y).getId() == appointmentList.get(i).getPatientid()) {
                    try {
                        if (today.after(sdf.parse(appointmentList.get(i).getAppdate()))) {
                            appDates.add(appointmentList.get(i).getAppdate() + " - " + patientList.get(y).getFname() + " " + patientList.get(y).getLname());
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //creating the adapter and setting it to the listview

        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, appDates);
        listView.setAdapter(adapter);
        listView.setBackgroundColor(Color.WHITE);

        //comparing the dates to the current date
        // https://stackoverflow.com/questions/37734063/how-to-compare-datetime-in-android/37734446
        //code for adapter for the listview
        //https://stackoverflow.com/questions/19079400/arrayadapter-in-android-to-create-simple-listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String app = parent.getItemAtPosition(position).toString();
                    for(int i = 0; i < appointmentList.size(); i++) {
                        for (int y = 0; y < patientList.size(); y++) {
                            if(app.equals(appointmentList.get(i).getAppdate() + " - " + patientList.get(y).getFname() + " " + patientList.get(y).getLname())){
                                String appDesc = appointmentList.get(i).getAppdesc();
                                displayDesc(appDesc);
                            }
                        }
                    }
            }
        });
    }
    //Alert Dialog with appointment description in it
    //https://stackoverflow.com/questions/22645107/android-intent-in-dialog-box
    public void displayDesc(String appointDesc) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Appointment Description");
        alert.setMessage(appointDesc);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.show();
    }


        private void readPatients(){
        PerformNetworkRequest2 request = new PerformNetworkRequest2(Constants.URL_PATIENTS_RETRIEVE, null, CODE_GET_REQUEST);
        request.execute();
    }
    private class PerformNetworkRequest2 extends AsyncTask<Void, Void, String> {

        //the url where we need to send the request
        String url;

        //the parameters
        HashMap<String, String> params;

        //the request code to define whether it is a GET or POST
        int requestCode;

        //constructor to initialize values
        PerformNetworkRequest2(String url, HashMap<String, String> params, int requestCode) {
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

    }
}
