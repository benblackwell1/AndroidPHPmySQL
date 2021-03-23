package com.example.androidphpmysql;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CompleteRegistrationActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText etFName, etLName, etAddress1, etAddress2, etEirCode, etPhoneNum, etPRSI;
    private Button btnComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_registration);

//        initialising the editTexts
        etFName = (EditText) findViewById(R.id.etFName);
        etLName = (EditText) findViewById(R.id.etLName);
        etAddress1 = (EditText) findViewById(R.id.etAddress1);
        etAddress2 = (EditText) findViewById(R.id.etAddress2);
        etEirCode = (EditText) findViewById(R.id.etEirCode);
        etPhoneNum = (EditText) findViewById(R.id.etPhoneNum);
        etPRSI = (EditText) findViewById(R.id.etPRSI);

        btnComplete = (Button) findViewById(R.id.btnComplete);

        btnComplete.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == btnComplete)
            completeRegistration();
    }

    private void completeRegistration() {

        final int userid =SharedPrefManager.getInstance(this).getUserId();
        final String fname = etFName.getText().toString().trim();
        final String lname = etLName.getText().toString().trim();
        final String street = etAddress1.getText().toString().trim();
        final String city = etAddress2.getText().toString().trim();
        final String zip = etEirCode.getText().toString().trim();
        final String phone = etPhoneNum.getText().toString().trim();
        final String prsi = etPRSI.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_REGISTER_PATIENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                            finish();
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
                params.put("userid", String.valueOf(userid));
                params.put("fname", fname);
                params.put("lname", lname);
                params.put("street", street);
                params.put("city", city);
                params.put("zip", zip);
                params.put("phone", phone);
                params.put("prsi", prsi);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}
