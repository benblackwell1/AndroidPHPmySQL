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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AdminWaitingRoomActivity extends AppCompatActivity {
    ListView listViewWaitingRoom;
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    List<WaitingRoom> waitingRoomList;
    List<Patient> patientList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_waiting_room);
        waitingRoomList = new ArrayList<>();
        patientList = new ArrayList<>();
        listViewWaitingRoom = (ListView) findViewById(R.id.listViewWaitingRoom);
        readPatients();
        loadWaitingRoomIntoListView();
    }

    private void loadWaitingRoomIntoListView() {
        PerformNetworkRequest request = new PerformNetworkRequest(Constants.URL_RETRIEVE_WAITING_ROOM, null, CODE_GET_REQUEST);
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
                    refreshWaitingRoomList(object.getJSONArray("names"));
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

    private void refreshWaitingRoomList(JSONArray names) throws JSONException {
        //clearing previous patients
        waitingRoomList.clear();

        //traversing through all the items in the json array
        //the json we got from the response
        for (int i = 0; i < names.length(); i++) {
            //getting each patient object
            JSONObject obj = names.getJSONObject(i);

            //adding the patient to the list
            waitingRoomList.add(new WaitingRoom(
                    obj.getInt("id"),
                    obj.getInt("userid"),
                    obj.getString("timestamp")
            ));
        }

        //creating the adapter and setting it to the listview
        AdminWaitingRoomActivity.WaitingRoomAdapter adapter = new AdminWaitingRoomActivity.WaitingRoomAdapter(waitingRoomList);
        listViewWaitingRoom.setAdapter(adapter);
    }

    class WaitingRoomAdapter extends ArrayAdapter<WaitingRoom> {
        //our medical list
        List<WaitingRoom> waitingRoomList;

        //constructor to get the list
        public WaitingRoomAdapter(List<WaitingRoom> waitingRoomList) {
            super(AdminWaitingRoomActivity.this, R.layout.layout_waiting_room, waitingRoomList);
            this.waitingRoomList = waitingRoomList;
        }

        //method returning listview
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.layout_waiting_room, null, true);

            //getting textview for displaying the timestamp of the medical record
            TextView textViewWaitingRoom = listViewItem.findViewById(R.id.textViewWaitingRoom);
            TextView textViewWaitingRoomName = listViewItem.findViewById(R.id.textViewWaitingRoomName);
            Button btnEnterPremise = listViewItem.findViewById(R.id.btnEnterPremise);

            final WaitingRoom waitingRoom = waitingRoomList.get(position);

            textViewWaitingRoom.setText(String.valueOf(waitingRoom.getUserid()));
            for (int i = 0; i < patientList.size(); i++) {
                if (String.valueOf(patientList.get(i).getUserid()) == textViewWaitingRoom.getText()) {
                    textViewWaitingRoomName.setText(patientList.get(i).getFname() + " " + patientList.get(i).getLname());
                }
            }

            btnEnterPremise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteWaitingRoom(waitingRoom.getUserid());
                }
            });

            return listViewItem;

        }
    }

    private void readPatients() {
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
    }

    private void deleteWaitingRoom(int userid) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                Constants.URL_DELETE_WAITING_ROOM + "?userid=" + userid, new Response.Listener<String>() {
            @Override
            //successful response
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(getIntent());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            //failed response
            public void onErrorResponse(VolleyError error) {


            }
        });
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

    }
}
