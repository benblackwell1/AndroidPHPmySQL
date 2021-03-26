package com.example.androidphpmysql;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminScreeningActivity extends AppCompatActivity {
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    ListView listView;
    List<Screening> screeningList;
    List<Appointment> appointmentList;
    List<Patient> patientList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_screening);
        //back button code
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (ListView) findViewById(R.id.listViewScreenings);

        screeningList = new ArrayList<>();
        appointmentList = new ArrayList<>();
        patientList = new ArrayList<>();
        getPatients();
        getAppointments();
        getScreenings();

    }

    private void getPatients() {
        PerformNetworkRequest3 request = new PerformNetworkRequest3(Constants.URL_PATIENTS_RETRIEVE, null, CODE_GET_REQUEST);
        request.execute();
    }
    private class PerformNetworkRequest3 extends AsyncTask<Void, Void, String> {

        //the url where we need to send the request
        String url;

        //the parameters
        HashMap<String, String> params;

        //the request code to define whether it is a GET or POST
        int requestCode;

        //constructor to initialize values
        PerformNetworkRequest3(String url, HashMap<String, String> params, int requestCode) {
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

                    refreshPatientsList(object.getJSONArray("patients"));
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

    private void refreshPatientsList(JSONArray patients) throws JSONException {

        patientList.clear();

        //traversing through all the items in the json array
        //the json we got from the response
        for (int i = 0; i < patients.length(); i++) {
            //getting each patient object
            JSONObject obj = patients.getJSONObject(i);

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
    private void getAppointments() {
        PerformNetworkRequest2 request = new PerformNetworkRequest2(Constants.URL_APPOINTMENTS_RETRIEVE, null, CODE_GET_REQUEST);
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

        appointmentList.clear();

        //traversing through all the items in the json array
        //the json we got from the response
        for (int i = 0; i < appointments.length(); i++) {
            //getting each patient object
            JSONObject obj = appointments.getJSONObject(i);

            appointmentList.add(new Appointment(
                    obj.getInt("id"),
                    obj.getInt("patientid"),
                    obj.getString("appdate"),
                    obj.getString("appdesc")

            ));
        }
    }
    private void getScreenings() {
        PerformNetworkRequest request = new PerformNetworkRequest(Constants.URL_RETRIEVE_SCREENING, null, CODE_GET_REQUEST);
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

                    refreshScreeningList(object.getJSONArray("screenings"));
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

    private void refreshScreeningList(JSONArray screenings) throws JSONException {

        screeningList.clear();

        //traversing through all the items in the json array
        //the json we got from the response
        for (int i = 0; i < screenings.length(); i++) {
            //getting each patient object
            JSONObject obj = screenings.getJSONObject(i);

            screeningList.add(new Screening(
                    obj.getInt("id"),
                    obj.getInt("appointmentid"),
                    obj.getString("screeningdate"),
                    obj.getString("status")

            ));
        }

        //creating the adapter and setting it to the listview
        ScreeningAdapter adapter = new ScreeningAdapter(screeningList);
        listView.setAdapter(adapter);
    }

    class ScreeningAdapter extends ArrayAdapter<Screening> {

        //our patient list
        List<Screening> screeningList;

        //constructor to get the list
        public ScreeningAdapter(List<Screening> screeningList) {
            super(AdminScreeningActivity.this, R.layout.custom_screening_listview, screeningList);
            this.screeningList = screeningList;
        }

        //method returning listview
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.custom_screening_listview, null, true);
            listViewItem.setBackgroundColor(Color.WHITE);
            //getting textview for displaying name
            TextView textViewName = listViewItem.findViewById(R.id.textViewName);
            TextView textViewScreeningDate = listViewItem.findViewById(R.id.textViewScreeningDate);

            final Screening screening = screeningList.get(position);

            textViewScreeningDate.setText(screening.getScreeningdate());
//            for (int i =0; i<patientList.size();i++){
//                for (int y =0; y<appointmentList.size();y++){
//                    if (screening.getAppointmentid() == appointmentList.get(y).getId() && appointmentList.get(y).getPatientid()==patientList.get(i).getId()){
//                            textViewName.setText(patientList.get(i).getFname() + " " + patientList.get(i).getLname());
//                        }
//                    }
//            }
//            int screeningid = screening.getAppointmentid();
//            for (int y =0; y<appointmentList.size();y++){
//                if (screeningid == appointmentList.get(y).getId()){
//                    textViewName.setText(appointmentList.get(y).getPatientid());
//                }
//            }

            for (int y =0; y<appointmentList.size();y++) {
                for (int i = 0; i < patientList.size(); i++) {
                    if (appointmentList.get(y).getId() == screening.getAppointmentid()){
                        if (patientList.get(i).getId() == appointmentList.get(y).getPatientid()){
                            textViewName.setText(patientList.get(i).getFname() + " " + patientList.get(i).getLname());
                        }
                    }
                }
            }

            return listViewItem;
        }
    }
}
