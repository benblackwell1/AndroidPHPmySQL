package com.example.androidphpmysql;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AdminLoginActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText editTextUsername, editTextPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(this);

    }
    @Override
    public void onClick(View view) {
        if (view == btnLogin) {
            adminlogin();
        }
    }

    private void adminlogin() {

        String username = String.valueOf(editTextUsername.getText());
        String password = String.valueOf(editTextPassword.getText());


        if (username.equals("admin") && password.equals("1234")){
            startActivity(new Intent(getApplicationContext(), AdminSwitchboardActivity.class));
            finish();
        }else{
            Toast.makeText(getApplicationContext(), "Incorrect details", Toast.LENGTH_SHORT).show();
        }
    }
}
