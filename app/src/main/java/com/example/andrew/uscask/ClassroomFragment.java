package com.example.andrew.uscask;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ClassroomFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClassroomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClassroomFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 0;
    Context mContext;
    private ListView mClassListView;
    private Location mLocation;
    private GoogleSignInAccount mGoogleSignInAccount;
    private JSONArray mJsonArray;
    private JSONObject mJsonObject;
    private LocationManager locationManager;
    private LocationListener locationListener;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ClassroomFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClassroomFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClassroomFragment newInstance(String param1, String param2) {
        ClassroomFragment fragment = new ClassroomFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mContext = this.getContext();
        //GETTING LOCATION

        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //makeUseOfNewLocation(location);
                //Toast.makeText(mContext, "Updated location",Toast.LENGTH_SHORT).show();
                mLocation = location;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
        } catch(SecurityException e){
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_classroom, container, false);
        mClassListView = v.findViewById(R.id.classList);
        mGoogleSignInAccount = getArguments().getParcelable("profile");
        //GET LIST OF CLASSES FROM SERVLET
        //Make Request to Servlet
        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String url ="https://fierce-savannah-23542.herokuapp.com/Classes";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //mTextView.setText("Response is: "+ response.substring(0,500));
                        System.out.println("Response: " + response);
                        try {
                            mJsonArray = new JSONArray(response);
                            if(mJsonArray != null) {
                                ArrayList<JSONObject> classes = new ArrayList<JSONObject>();
                                for(int i = 0; i < mJsonArray.length(); i++) {
                                    try {
                                        classes.add(mJsonArray.getJSONObject(i));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                                JsonAdapter adapter = new JsonAdapter(classes, mContext);

                                mClassListView.setAdapter(adapter);
                                mClassListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        try {
                                            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
                                        } catch (SecurityException e) {
                                            e.printStackTrace();
                                        }
                                        JSONObject o = (JSONObject) mClassListView.getItemAtPosition(position);
                                        System.out.println(mLocation);
                                        if(o != null && mLocation != null) {
                                            try {
                                                SimpleDateFormat parser = new SimpleDateFormat("HH:mm:ss");
                                                Date date = new Date();
                                                String currentTimeString = parser.format(date);
                                                Date currentTimeDate = parser.parse(currentTimeString);
                                                Date startTime = parser.parse(o.getString("startTime"));
                                                Date endTime = parser.parse(o.getString("endTime"));
                                                //Date endTime = parser.parse("12:15:00");
                                                System.out.println("Class Location: " + o.getString("latitude") + ", " + o.getString("longitude"));
                                                System.out.println("Class Start Time: " + o.getString("startTime") + " End Time: " + o.getString("endTime"));
                                                System.out.println("Before Class Ends: " + currentTimeDate.before(endTime) + " After Class Starts: " + currentTimeDate.after(startTime));
                                                double classLatitude = Double.parseDouble(o.getString("latitude"));
                                                double classLongitude = Double.parseDouble(o.getString("longitude"));
                                                double currentLatitude = mLocation.getLatitude();
                                                double currentLongitude = mLocation.getLongitude();
                                                float[] distance = new float[1];
                                                Location.distanceBetween(classLatitude, classLongitude, currentLatitude, currentLongitude, distance);
                                                Intent intent = new Intent(getActivity(), QuestionActivity.class);
                                                Bundle b = new Bundle();
                                                b.putString("lectureID", o.getString("id"));
                                                b.putString("studentID", mGoogleSignInAccount.getId());
                                                b.putString("lectureName", o.getString("department") + o.getString("classNumber"));
                                                intent.putExtras(b);
                                                startActivity(intent);
                                                if(currentTimeDate.after(startTime) && currentTimeDate.before(endTime)) {
                                                    System.out.println(distance[0]);
                                                    if (distance[0] < 500) {
                                                        System.out.println("Student is inside the classroom");
                                                        //Forward to question activity

                                                        checkIn(o.getString("id"));
                                                    } else {
                                                        Toast.makeText(mContext, "You are not in class.",Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Toast.makeText(mContext, "Class is not in session.",Toast.LENGTH_SHORT).show();
                                                }
                                            } catch(JSONException e) {
                                                e.printStackTrace();
                                            } catch(ParseException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            Toast.makeText(mContext, "Can't find location.",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                params.put("studentID", mGoogleSignInAccount.getId());
                params.put("requestType", "getClasses");
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        //SETTING THE LIST VALUES

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private void checkIn(String lectureID) {
        final String id = lectureID;
        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String url ="https://fierce-savannah-23542.herokuapp.com/Attendance";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //mTextView.setText("Response is: "+ response.substring(0,500));
                        System.out.println("Response: " + response);

                        if(response.equals("Success")) {
                            Toast.makeText(mContext, "You have been checked in.",Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
                System.out.println("hit an error: " + error.getMessage());
            }
        } ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("studentID", mGoogleSignInAccount.getId());
                params.put("requestType", "checkIn");
                params.put("lectureID", id);
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
