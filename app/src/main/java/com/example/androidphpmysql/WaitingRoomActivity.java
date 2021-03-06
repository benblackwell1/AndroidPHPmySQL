package com.example.androidphpmysql;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaitingRoomActivity extends AppCompatActivity {
    ListView listViewWaitingRoom;
    TextView textViewUserID;
    TextView textViewUserName;
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    List<WaitingRoom> waitingRoomList;
    Button btnRefreshWaitingRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        textViewUserID = (TextView) findViewById(R.id.textViewUserID);
        textViewUserName = (TextView) findViewById(R.id.textViewUserName);
        textViewUserID.setText(String.valueOf(SharedPrefManager.getInstance(this).getUserId()));
        textViewUserName.setText(String.valueOf(SharedPrefManager.getInstance(this).getUsername()));
        waitingRoomList = new ArrayList<>();
        btnRefreshWaitingRoom = (Button) findViewById(R.id.btnRefreshWaitingRoom);

        btnRefreshWaitingRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDeletion();
            }
        });

        //channel for the notification
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("Enter Surgery", "Enter Surgery", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        //back button code
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadIntoWaitingRoom();
//        getAllWaitingRoom();
    }


    private void loadIntoWaitingRoom() {
        final String userid = String.valueOf(SharedPrefManager.getInstance(this).getUserId());
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_ADD_WAITING_ROOM,
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
                params.put("userid", userid);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void getAllWaitingRoom() {
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


        if (names.length() == 0) {
            shownotification();
        } else {
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

        }
    }

    private void checkDeletion() {
        getAllWaitingRoom();
    //checking if the arraylist is empty
    for (int i = 0; i < waitingRoomList.size(); i++) {
            if (!String.valueOf(waitingRoomList.get(i).getUserid()).equals(String.valueOf(SharedPrefManager.getInstance(this).getUserId()))) {
                shownotification();
            } else if (waitingRoomList.contains(null)){
                shownotification();
            } else if (String.valueOf(waitingRoomList.get(i).getUserid()).equals(String.valueOf(SharedPrefManager.getInstance(this).getUserId()))) {
                Toast.makeText(getApplicationContext(), "Still waiting", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void shownotification() {
        Toast.makeText(getApplicationContext(), "Please enter the surgery", Toast.LENGTH_SHORT).show();
        new AlertDialog.Builder(WaitingRoomActivity.this)
                .setTitle("Enter Surgery")
                .setMessage("Please enter the surgery, the dentist is ready to see you.")
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(new Intent(WaitingRoomActivity.this, ProfileActivity.class));
                    }
                }).show();
        //notification to indicate to enter the surgery
        NotificationCompat.Builder builder = new NotificationCompat.Builder(WaitingRoomActivity.this, "Enter Surgery");
        builder.setContentTitle("Enter the surgery");
        builder.setContentText("Please enter the surgery. The dentist is ready to see you.");
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(WaitingRoomActivity.this);
        managerCompat.notify(1,builder.build());
    }

}


