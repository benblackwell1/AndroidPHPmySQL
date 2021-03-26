package com.example.androidphpmysql;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminSwitchboardActivity extends AppCompatActivity implements View.OnClickListener{

    //buttons for switching activities
//    private Button buttonAppointments, buttonPatients, buttonScreenings, buttonWaitingRoom, buttonGraph;
    private CardView cvAppointments, cvPatients, cvScreenings, cvWaitingRoom, cvGraphReport, cvLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_switchboard);



        cvAppointments = (CardView) findViewById(R.id.cvAppointments);
        cvPatients = (CardView) findViewById(R.id.cvPatients);
        cvScreenings = (CardView) findViewById(R.id.cvScreenings);
        cvWaitingRoom = (CardView) findViewById(R.id.cvWaitingRoom);
        cvGraphReport = (CardView) findViewById(R.id.cvGraphReport);
        cvLogout = (CardView) findViewById(R.id.cvLogout);



        cvAppointments.setOnClickListener(this);
        cvPatients.setOnClickListener(this);
        cvScreenings.setOnClickListener(this);
        cvWaitingRoom.setOnClickListener(this);
        cvGraphReport.setOnClickListener(this);
        cvLogout.setOnClickListener(this);

    }
    @Override
    public void onClick(View view){
        //open the appointments page
        if(view == cvAppointments)
            startActivity(new Intent(this, AppointmentMainActivity.class));
        //open the Patients Page
        if(view == cvPatients)
            startActivity(new Intent(this, AdminActivity.class));
        //open the waiting room Page
        if(view == cvWaitingRoom)
            startActivity(new Intent(this, AdminWaitingRoomActivity.class));
        //open the screening page
        if(view == cvScreenings)
            startActivity(new Intent(this, AdminScreeningActivity.class));
        //open the graph page
        if(view == cvGraphReport)
            startActivity(new Intent(this, BarChartActivity.class));
        //logout of admin
        if(view == cvLogout){
            finish();
            startActivity(new Intent(this, LoginActivity.class));

        }

    }
}
