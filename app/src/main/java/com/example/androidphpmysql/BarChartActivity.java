package com.example.androidphpmysql;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BarChartActivity extends AppCompatActivity {
    List<Appointment> appointmentList;
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    // variable for our bar chart
    BarChart barChart;

    // variable for our bar data.
    BarData barData;

    // variable for our bar data set.
    BarDataSet barDataSet;

    // array list for storing entries.
    ArrayList barEntriesArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);
        //back button code
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //gathering the appointments
        appointmentList = new ArrayList<>();
        //get appointment list
        readAppointments();

            // initializing variable for bar chart.
            barChart = (BarChart) findViewById(R.id.bargraph);

            // calling method to get bar entries.
            getBarEntries();


            // creating a new bar data set.
            barDataSet = new BarDataSet(barEntriesArrayList, "Appointments per week");
            final ArrayList<String> labels = new ArrayList<String>();
            labels.add("January");
            labels.add("February");
            labels.add("March");
            labels.add("April");
            labels.add("May");
            // creating a new bar data and
            // passing our bar data set.
            barData = new BarData(barDataSet);

            // below line is to set data
            // to our bar chart.
            barChart.setData(barData);

            // adding color to our bar data set.
            barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

            // setting text color.
            barDataSet.setValueTextColor(Color.BLACK);

            // setting text size
            barDataSet.setValueTextSize(16f);
            barChart.getDescription().setEnabled(false);
            barChart.setBackgroundColor(Color.WHITE);
        }
        
    private void getBarEntries() {
        // creating a new array list
        barEntriesArrayList = new ArrayList<>();



            // adding new entry to our array list with bar
            // entry and passing x and y axis value to it
            barEntriesArrayList.add(new BarEntry(1f, 4));
            barEntriesArrayList.add(new BarEntry(2f, 6));
            barEntriesArrayList.add(new BarEntry(3f, 8));
            barEntriesArrayList.add(new BarEntry(4f, 2));
            barEntriesArrayList.add(new BarEntry(5f, 4));
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

    }

//    private void populateBarChart() throws ParseException {
//
////        barChart = (BarChart) findViewById(R.id.bargraph);
////
////        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
////        String MarchStart = "2021-03-01 00:00:01";
////        Date marchStart = sdf.parse(MarchStart);
////
////        String MarchEnd = "2021-03-31 23:59:59";
////        Date marchEnd = sdf.parse(MarchEnd);
////        int count;
////        ArrayList<String> dates = new ArrayList<>();
//////        ArrayList<BarEntry> values = new ArrayList<>();
//
////        Date today = new Date();
////
////        for (int i = 0; i < appointmentList.size(); i++) {
////            if (marchStart.after(sdf.parse(appointmentList.get(i).getAppdate()))) {
////                dates.add(appointmentList.get(i).getAppdate());
////            }
//////            BarDataSet barDataSet = new BarDataSet(values,"Dates");
//////
//////
//////            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
//////            dataSets.add(barDataSet);
//////
//////            BarData data = new BarData(dataSets);
//////            data.setBarWidth(0.9f);
//////            barChart.setData(data);
//////            barChart.setTouchEnabled(true);
//////            barChart.setDragEnabled(true);
//////            barChart.setScaleEnabled(true);
//////            barChart.setFitBars(true);
//////            barChart.invalidate();
//////        }
//////        count = dates.size();
//////        Toast.makeText(getApplicationContext(),count, Toast.LENGTH_LONG).show();
////
//////        BarDataSet barDataSet = new BarDataSet(values,"Dates");
////            ArrayList<BarEntry> barEntries = new ArrayList<>();
////
////            for (float y = 0; y < dates.size(); y++) {
////                float fsize = y;
////                barEntries.add(new BarEntry(1f, fsize));
////
////            }
////            barEntries.add(new BarEntry(4f, 0));
////            barEntries.add(new BarEntry(8f, 1));
////            barEntries.add(new BarEntry(6f, 2));
////            barEntries.add(new BarEntry(12f, 3));
////            barEntries.add(new BarEntry(18f, 4));
////            barEntries.add(new BarEntry(9f, 5));
////            barEntries.add(new BarEntry(12f, 6));
////            barEntries.add(new BarEntry(10f, 7));
//////        barEntries.add(new BarEntry(1f, fsize));
////            final ArrayList<String> labels = new ArrayList<String>();
////            labels.add("January");
////            labels.add("February");
////            labels.add("March");
////            labels.add("April");
////            labels.add("May");
////            labels.add("June");
////            labels.add("July");
////            labels.add("August");
////            barChart = new BarChart(this);
////
////            ArrayList<BarEntry> entries = new ArrayList<>();
////            entries.add(new BarEntry(0, 3f));
////            entries.add(new BarEntry(1, 8f));
////            entries.add(new BarEntry(2, 6f));
////            entries.add(new BarEntry(3, 11f));
////            entries.add(new BarEntry(4, 5f));
////            entries.add(new BarEntry(5, 14f));
////
////            BarDataSet dataSet = new BarDataSet(entries,"Horizontal Bar");
////
////            BarData data = new BarData(dataSet);
////        barChart.setData(data);
////        barChart.animateXY(2000, 2000);
////        barChart.invalidate();
////
////
////            ArrayList<String> xLabels = new ArrayList<>();
////            xLabels.add("January");
////            xLabels.add("February");
////            xLabels.add("March");
////            xLabels.add("April");
////            xLabels.add("May");
////            xLabels.add("June");
////        XAxis xAxis = barChart.getXAxis();
////            xAxis.setValueFormatter(new IAxisValueFormatter() {
////                @Override
////                public String getFormattedValue(float value, AxisBase axis) {
////                    Print.e(value);
////                    return xLabels.get((int) value);
////                }
////
////            });
////
////            BarDataSet barDataSet = new BarDataSet(barEntries, "Dates");
////
////            BarData theData = new BarData(labels, barDataSet);
////            theData.setBarWidth(0.9f);
////            barChart.setData(theData);
////            barChart.setTouchEnabled(true);
////            barChart.setDragEnabled(true);
////            barChart.setScaleEnabled(true);
////            barChart.setFitBars(true);
////            barChart.invalidate();
//
//
//    }
}
