package com.example.androidphpmysql;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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

public class ProfileActivity extends AppCompatActivity {
    ListView listView;
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    //defining the textviews
    private TextView textViewUsername, textViewUserEmail;

    List<Appointment> appointmentList;
    private ArrayList appDates = new ArrayList<>();
    List<Patient> patientList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //if the user is not logged in it will stop the activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            //this will then start the activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        //initialising the text views
        textViewUsername = (TextView) findViewById(R.id.textViewUsername);
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);

        textViewUserEmail.setText(SharedPrefManager.getInstance(this).getUserEmail());
        textViewUsername.setText(SharedPrefManager.getInstance(this).getUsername());

        listView = (ListView) findViewById(R.id.listViewPatientAppointments);
        appointmentList = new ArrayList<>();
        patientList = new ArrayList<>();

        getPatients();
        readAppointments();
    }

    private void readAppointments() {
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


        for (int i = 0; i < appointmentList.size(); i++) {
            for (int y = 0; y < patientList.size(); y++) {
                if (SharedPrefManager.getInstance(this).getUserId() == patientList.get(y).getUserid()) {
                    if (patientList.get(y).getId() == appointmentList.get(i).getPatientid()) {
                        try {
                            if (today.before(sdf.parse(appointmentList.get(i).getAppdate()))) {
                                appDates.add(appointmentList.get(i).getAppdate());
                                CustomAdapter adapter = new CustomAdapter(appDates);
                                listView.setAdapter(adapter);
                            } else {
                               String [] noApp = {"No Appointments Upcoming"};
                               ArrayAdapter<String> noAppointment= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, noApp);
                               listView.setAdapter(noAppointment);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        //creating the adapter and setting it to the listview
        //AppointmentAdapter adapter = new AppointmentAdapter(appDates);
        //listView.setAdapter(adapter);
//        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, appDates);
//        CustomAdapter adapter = new CustomAdapter(appDates);
//        listView.setAdapter(adapter);

        //comparing the dates to the current date
        // https://stackoverflow.com/questions/37734063/how-to-compare-datetime-in-android/37734446
        //code for adapter for the listview
        //https://stackoverflow.com/questions/19079400/arrayadapter-in-android-to-create-simple-listview
    }

    class CustomAdapter extends ArrayAdapter<String> {
        ArrayList<String> appDates;

        public CustomAdapter(ArrayList<String> appDates) {
            super(ProfileActivity.this, R.layout.layout_custom_listview, appDates);
            this.appDates = appDates;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View view = getLayoutInflater().inflate(R.layout.layout_custom_listview, null);
            ImageView mImageView = (ImageView) view.findViewById(R.id.imageView);
            TextView mTextView = view.findViewById(R.id.textViewAppointmentDate);
//            mTextView.setText(appDates.get(position));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            final long HOUR = 3600*1000; // in milli-seconds.
            Date today = new Date();
            Date plus2Day = new Date(today.getTime()+48*HOUR);


            //showing the screening image if the date is within 48hrs of today
            try {
                if(today.before(sdf.parse(appDates.get(position))) && plus2Day.after(sdf.parse(appDates.get(position)))){
                    mTextView.setText(appDates.get(position));
                    mImageView.setVisibility(View.VISIBLE);
                }
                else {
                    mTextView.setText(appDates.get(position));
                    mImageView.setVisibility(View.GONE);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //when the the image for screening is clicked
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < appointmentList.size(); i++) {
                        if(appointmentList.get(i).getAppdate() == appDates.get(position)){
                            String appointmentID = String.valueOf(appointmentList.get(i).getId());
                            Intent intent = new Intent(ProfileActivity.this, PatientScreeningActivity.class);
                            intent.putExtra("appID", appointmentID);
                            startActivity(intent);
                        }
                    }
                }
            });

            //resources
            //https://stackoverflow.com/questions/883060/how-can-i-determine-if-a-date-is-between-two-dates-in-java
            //https://stackoverflow.com/questions/7348150/android-why-setvisibilityview-gone-or-setvisibilityview-invisible-do-not
            //https://stackoverflow.com/questions/3581258/adding-n-hours-to-a-date-in-java
            return view;
        }
    }
        private void getPatients() {
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


        //override method for logout
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            //code to include menu item
            getMenuInflater().inflate(R.menu.menu, menu);
            return true;
        }

        //this method is for when the logout oprion is selected from the menu
        //the method will log you out
        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
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
                case R.id.menuUpdate:
//            Toast.makeText(this,"You clicked settings", Toast.LENGTH_LONG).show();
                    finish();
                    startActivity(new Intent(this, UpdateMedical.class));
                    break;
            }
            return true;
        }
    }
