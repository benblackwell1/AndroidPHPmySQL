package com.example.androidphpmysql;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editTextUsername, editTextPassword, editTextEmail;
    private Button buttonRegister;
    private Button buttonAdminTest;
    private ProgressDialog progressDialog;

    private TextView textViewLogin;

    //code for this login has been aided with the help of this video series. I has refactored it to fit my project -> https://www.youtube.com/playlist?list=PLk7v1Z2rk4hjQaV062aE_CW68xgXdYFpV

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
            return;
        }

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        textViewLogin = (TextView) findViewById(R.id.textViewLogin);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        buttonAdminTest = (Button) findViewById(R.id.buttonAdminTest);


        progressDialog = new ProgressDialog(this);

        buttonRegister.setOnClickListener(this);
        buttonAdminTest.setOnClickListener(this);
        textViewLogin.setOnClickListener(this);
    }

    private void registerUser(){
        final String email = editTextEmail.getText().toString().trim();
        final String username = editTextUsername.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject  jsonObject = new JSONObject(response);
                            Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                            userLogin();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username",username);
                params.put("email",email);
                params.put("password",password);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
    @Override
    public void onClick(View view){
        if (view == buttonRegister)
            registerUser();
        if(view == textViewLogin)
            startActivity(new Intent(this, LoginActivity.class));
        if(view == buttonAdminTest)
            startActivity(new Intent(this, AdminSwitchboardActivity.class));
    }

    private void userLogin() {
            final String username = editTextUsername.getText().toString().trim();
            final String password = editTextPassword.getText().toString().trim();

            progressDialog.show();

            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST,
                    Constants.URL_LOGIN,
                    new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response){
                            progressDialog.dismiss();
                            //will determine weather the user is successfully logged in or weather the parameters are incorrect
                            try {
                                JSONObject obj = new JSONObject(response.substring(response.indexOf("{"),response.lastIndexOf("}") +1));
                                if(!obj.getBoolean("error")){
                                    SharedPrefManager.getInstance(getApplicationContext())
                                            .userLogin(
                                                    obj.getInt("id"),
                                                    obj.getString("username"),
                                                    obj.getString("email")
                                            );
                                    startActivity(new Intent(getApplicationContext(), CompleteRegistrationActivity.class));
                                    finish();
                                }else{
                                    Toast.makeText(
                                            getApplicationContext(),
                                            obj.getString("message"),
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            progressDialog.dismiss();
                            Toast.makeText(
                                    getApplicationContext(),
                                    error.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("username",username);
                    params.put("password", password);
                    return params;
                }
            };
            RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
        }
    }
