package com.example.androidphpmysql;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class AppointmentMainActivity extends AppCompatActivity implements View.OnClickListener{
//buttton for adding appointments
    private Button buttonAddAppointment;
    private Button buttonAppointmentHistory;

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    ListView listView;
    List<Appointment> appointmentList;
    private ArrayList appDates = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_main);

        listView = (ListView) findViewById(R.id.listViewAppointments);
        appointmentList = new ArrayList<>();

        buttonAddAppointment = (Button) findViewById(R.id.buttonAddAppointment);
        buttonAddAppointment.setOnClickListener(this);

        buttonAppointmentHistory = (Button) findViewById(R.id.buttonAppointmentHistory);
        buttonAppointmentHistory.setOnClickListener(this);

        //back button code
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        readAppointments();
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
        for(int i = 0; i < appointmentList.size(); i++){
            try {
                if(today.before(sdf.parse(appointmentList.get(i).getAppdate()))){
                    appDates.add(appointmentList.get(i).getAppdate());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //**need to create a way to display what patients name is.

        //creating the adapter and setting it to the listview
        //AppointmentAdapter adapter = new AppointmentAdapter(appDates);
        //listView.setAdapter(adapter);
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, appDates);
        listView.setAdapter(adapter);

        //comparing the dates to the current date
        // https://stackoverflow.com/questions/37734063/how-to-compare-datetime-in-android/37734446
        //code for adapter for the listview
        //https://stackoverflow.com/questions/19079400/arrayadapter-in-android-to-create-simple-listview
    }

    @Override
    public void onClick(View view){
        if(view == buttonAddAppointment)
            startActivity(new Intent(this, CreateAppointmentActivity.class));
//        if(view == buttonAppointmentHistory)
//            startActivity(new Intent(this, AppointmentHistoryActivity.class));
    }
    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

}
