package com.example.androidphpmysql;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    //defining the textviews
    private TextView textViewUsername, textViewUserEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //if the user is not logged in it will stop the activity
        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            //this will then start the activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        //initialising the text views
        textViewUsername = (TextView) findViewById(R.id.textViewUsername);
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);

        textViewUserEmail.setText(SharedPrefManager.getInstance(this).getUserEmail());
        textViewUsername.setText(SharedPrefManager.getInstance(this).getUsername());

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
    switch(item.getItemId()){
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
