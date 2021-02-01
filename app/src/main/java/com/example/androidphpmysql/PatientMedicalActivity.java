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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientMedicalActivity extends AppCompatActivity implements CommentDialog.CommentDialogListener {
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    private TextView textViewUserID;
    private TextView textViewName;
    private TextView textViewAddress;


    //declaring button for pop up dialog to caputre the comment made by dentist
    private Button buttonAddComment;
    ListView listViewComment;
    List<Comment> commentList = new ArrayList<>();

    //listview for medicals
    ListView listView;


    //will use this list to display in the listview
    //    https://stackoverflow.com/questions/31368814/attempt-to-invoke-interface-method-boolean-java-util-list-addjava-lang-object
    List<Medical> medicalList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_medical);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String userid = extras.getString("userid");
        String fname = extras.getString("fname");
        String lname = extras.getString("lname");
        String street = extras.getString("street");
        String city = extras.getString("city");
        String zip = extras.getString("zip");
        String patientid = extras.getString("patientid");

        textViewUserID = (TextView) findViewById(R.id.textViewUserID);
        textViewUserID.setText(userid);

        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewName.setText(fname + " " + lname);

        textViewAddress = (TextView) findViewById(R.id.textViewAddress);
        textViewAddress.setText(street + ", " + city + ", " + zip);
        //code to pass the integer as a string through the intent
        //https://stackoverflow.com/questions/37627328/passing-array-data-using-bundle-to-another-activity-on-listview-click

        listView = (ListView) findViewById(R.id.listViewPatients);
        listViewComment =(ListView) findViewById(R.id.listViewComments);

        buttonAddComment = (Button) findViewById(R.id.buttonAddComment);
        //adding onclick listener for when button is clicked. It will open the dialog for input. https://www.youtube.com/watch?v=ARezg1D9Zd0&pbjreload=101
        buttonAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        readMedicals(userid);

        readComments(patientid);

        //back button code
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //method to open dialog
    public void openDialog(){
        CommentDialog commentDialog = new CommentDialog();
        commentDialog.show(getSupportFragmentManager(), "comment dialog");
    }

    @Override
    public void applyText(String comment) {
        //set the listview to the comment
        String commentOnPatient = comment;
        //add comment to the database
        addComment(commentOnPatient);
    }

    //function to add comment to the database
    private void addComment(final String commentmade){

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        final String patientid = extras.getString("patientid");
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_ADD_COMMENT,
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
                params.put("patientid", String.valueOf(patientid));
                params.put("commentmade", commentmade);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
        //have to add the retrieve here to show in the comment listview.
        onRestart();
    }
    private void readComments( String patientid){
        PatientMedicalActivity.PerformNetworkRequestComment request = new PatientMedicalActivity.PerformNetworkRequestComment(Constants.URL_PATIENTS_COMMENT_RETRIEVE + "?patientid=" +  patientid, null, CODE_GET_REQUEST);
        request.execute();
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    class MedicalAdapter extends ArrayAdapter<Medical> {
        //our medical list
        List<Medical> medicalList;

        //constructor to get the list
        public MedicalAdapter(List<Medical> medicalList) {
            super(PatientMedicalActivity.this, R.layout.layout_medical_list, medicalList);
            this.medicalList = medicalList;
        }

        //method returning listview
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.layout_medical_list, null, true);

            //getting textview for displaying the timestamp of the medical record
            TextView textViewName = listViewItem.findViewById(R.id.textViewName);

            final Medical medical = medicalList.get(position);

            textViewName.setText(medical.getTimestamp().toString());

            //attaching a click listener to the name textview
            //passing the values of the specific medical record to the next activity
            //code from https://stackoverflow.com/questions/51578807/how-do-i-pass-id-of-listview-item-i-got-from-server-to-another-activity-onclick
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Medical obj = getItem(position);
                    int medid = obj.getId();
                    int patid = obj.getPatientid();
                    String gender = obj.getGender();
                    String age = obj.getAge();
                    String weight = obj.getWeight();
                    String alcohol = obj.getAlcohol();
                    String smoking = obj.getSmoking();
                    String underlyingcondition = obj.getUnderlyingcondition();
                    String allergy = obj.getAllergy();
                    String medication = obj.getMedication();
                    Intent intent = new Intent(PatientMedicalActivity.this, SelectedMedicalActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("MedID", String.valueOf(medid));
                    extras.putString("PatID", String.valueOf(patid));
                    extras.putString("Gender", gender);
                    extras.putString("Age", age);
                    extras.putString("Weight", weight);
                    extras.putString("Alcohol", alcohol);
                    extras.putString("Smoking", smoking);
                    extras.putString("UndelyingCondition", underlyingcondition);
                    extras.putString("Allergy", allergy);
                    extras.putString("Medication", medication);

                    intent.putExtras(extras);
                    finish();
                    startActivity(intent);
                }
            });

            return listViewItem;
        }
    }

    private void readMedicals( String userid){
        PatientMedicalActivity.PerformNetworkRequest request = new PatientMedicalActivity.PerformNetworkRequest(Constants.URL_PATIENTS_MEDICAL_RETRIEVE + "?patientid=" +  userid, null, CODE_GET_REQUEST);
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
                    //refreshing the medicalList after every operation
                    //so we get an updated list
                    //we will create this method right now it is commented
                    //because we haven't created it yet
                    refreshMedicalList(object.getJSONArray("medicals"));
//                    refreshCommentList(object.getJSONArray("comments"));
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
    private void refreshMedicalList(JSONArray medicals) throws JSONException {
        //clearing previous medicals
        medicalList.clear();

        //traversing through all the items in the json array
        //the json we got from the response
        for (int i = 0; i < medicals.length(); i++) {
            //getting each medical object
            JSONObject obj = medicals.getJSONObject(i);

            //adding the medicals to the list
            medicalList.add(new Medical(
                    obj.getInt("id"),
                    obj.getInt("patientid"),
                    obj.getString("gender"),
                    obj.getString("age"),
                    obj.getString("weight"),
                    obj.getString("alcohol"),
                    obj.getString("smoking"),
                    obj.getString("underlyingcondition"),
                    obj.getString("allergy"),
                    obj.getString("medication"),
                    obj.getString("timestamp")
                    ));
        }

        //creating the adapter and setting it to the listview
        PatientMedicalActivity.MedicalAdapter adapter = new PatientMedicalActivity.MedicalAdapter(medicalList);
        listView.setAdapter(adapter);
    }

    private void refreshCommentList(JSONArray comments) throws JSONException {
        //clearing previous medicals
        commentList.clear();

        //traversing through all the items in the json array
        //the json we got from the response
        for (int i = 0; i < comments.length(); i++) {
            //getting each medical object
            JSONObject obj = comments.getJSONObject(i);

            //adding the medicals to the list
            commentList.add(new Comment(
                    obj.getInt("id"),
                    obj.getInt("patientid"),
                    obj.getString("commentmade"),
                    obj.getString("timestamp")
            ));
        }

        //creating the adapter and setting it to the listview
        PatientMedicalActivity.CommentAdapter adapter = new PatientMedicalActivity.CommentAdapter(commentList);
        listViewComment.setAdapter(adapter);
    }

    //Adapter class for the comments
    class CommentAdapter extends ArrayAdapter<Comment> {
        //our medical list
        List<Comment> commentList;

        //constructor to get the list
        public CommentAdapter(List<Comment> commentList) {
            super(PatientMedicalActivity.this, R.layout.layout_comment_list, commentList);
            this.commentList = commentList;
        }

        //method returning listview
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater2 = getLayoutInflater();
            View listViewItemComment = inflater2.inflate(R.layout.layout_comment_list, null, true);

            //getting textview for displaying the timestamp of the medical record
            TextView textViewComment = listViewItemComment.findViewById(R.id.textViewComment);

            final Comment comment = commentList.get(position);

            textViewComment.setText(comment.getCommentmade());

            return listViewItemComment;
        }
    }


    //***Network Request for the Comments

    private class PerformNetworkRequestComment extends AsyncTask<Void, Void, String> {

        //the url where we need to send the request
        String url;

        //the parameters
        HashMap<String, String> params;

        //the request code to define whether it is a GET or POST
        int requestCode;

        //constructor to initialize values
        PerformNetworkRequestComment(String url, HashMap<String, String> params, int requestCode) {
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
                    //refreshing the medicalList after every operation
                    //so we get an updated list
                    //we will create this method right now it is commented
                    //because we haven't created it yet
//                    refreshMedicalList(object.getJSONArray("medicals"));
                    refreshCommentList(object.getJSONArray("comments"));
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
}
