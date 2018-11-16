package com.example.andrew.uscask;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AttendanceActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private JSONArray mJsonArray;
    private String studentID;
    private String lectureID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        studentID = getIntent().getExtras().getString("studentID");
        lectureID = getIntent().getExtras().getString("lectureID");
        //-------------Setting up toolbar-------------//
        Toolbar toolbar = findViewById(R.id.attendanceToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mRecyclerView = (RecyclerView) findViewById(R.id.attendanceList);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://fierce-savannah-23542.herokuapp.com/Attendance";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //mTextView.setText("Response is: "+ response.substring(0,500));
                        System.out.println("Response: " + response);
                        try {
                            mJsonArray = new JSONArray(response);
                            ArrayList<JSONObject> dates = new ArrayList<>();
                            if(mJsonArray != null) {
                                for(int i = 0; i < mJsonArray.length(); i++) {
                                    dates.add(mJsonArray.getJSONObject(i));
                                }
                                System.out.println("Dates size: " + dates.size());
                               mAdapter = new AttendanceAdapter(dates);
                               mRecyclerView.setAdapter(mAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(response == "Added") {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
                System.out.println("hit an error: " + error.getMessage());
                error.printStackTrace();
            }
        } ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("studentID", studentID);
                params.put("lectureID", lectureID);
                params.put("requestType", "getStudentHistory");
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
