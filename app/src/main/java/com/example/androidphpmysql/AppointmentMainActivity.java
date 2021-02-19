package com.example.androidphpmysql;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AppointmentMainActivity extends AppCompatActivity implements View.OnClickListener{
//buttton for adding appointments
    private Button buttonAddAppointment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_main);

        buttonAddAppointment = (Button) findViewById(R.id.buttonAddAppointment);
        buttonAddAppointment.setOnClickListener(this);
    }
    @Override
    public void onClick(View view){
        if(view == buttonAddAppointment)
            startActivity(new Intent(this, CreateAppointmentActivity.class));
    }
}
