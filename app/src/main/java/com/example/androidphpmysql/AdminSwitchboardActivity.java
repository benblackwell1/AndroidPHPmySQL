package com.example.androidphpmysql;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminSwitchboardActivity extends AppCompatActivity implements View.OnClickListener{

    //buttons for switching activities
    private Button buttonAppointments, buttonPatients, buttonScreenings, buttonWaitingRoom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_switchboard);

        buttonAppointments = (Button) findViewById(R.id.buttonAppointments);
        buttonPatients = (Button) findViewById(R.id.buttonPatients);
        buttonScreenings = (Button) findViewById(R.id.buttonScreenings);
        buttonWaitingRoom = (Button) findViewById(R.id.buttonWaitingRoom);

        buttonAppointments.setOnClickListener(this);
        buttonPatients.setOnClickListener(this);
        buttonScreenings.setOnClickListener(this);
        buttonWaitingRoom.setOnClickListener(this);

    }
    @Override
    public void onClick(View view){
        //open the appointments page
        if(view == buttonAppointments)
            startActivity(new Intent(this, AppointmentMainActivity.class));
        //open the Patients Page
        if(view == buttonPatients)
            startActivity(new Intent(this, AdminActivity.class));
        //open the screening page
        //need to create a screening page
        //open the Patients Page
        if(view == buttonWaitingRoom)
            startActivity(new Intent(this, AdminWaitingRoomActivity.class));
    }
}
