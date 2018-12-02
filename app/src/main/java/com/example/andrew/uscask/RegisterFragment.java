package com.example.andrew.uscask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static EditText mClassID;
    private Button mEnrollButton;
    private GoogleSignInAccount mGoogleSignInAccount;
    private Activity mActivity;
    private int isGuest;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        mClassID = v.findViewById(R.id.classID);
        mEnrollButton = v.findViewById(R.id.enrollButton);
        mGoogleSignInAccount = getArguments().getParcelable("profile");
        isGuest = getArguments().getInt("guest");

        mEnrollButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                enroll(v);
            }
        });

        mActivity = this.getActivity();
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

    //ENROLL IN CLASS FUNCTION
    public void enroll(View view) {
        //Make Request to Servlet
        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        if(isGuest != 1) {
            String url = "http://fierce-savannah-23542.herokuapp.com/Classes";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            //mTextView.setText("Response is: "+ response.substring(0,500));
                            System.out.println("register response: " + response);
                            if (response.equals("Added")) {
                                //Load the Classroom fragment
                                Fragment fragment = null;
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("profile", mGoogleSignInAccount);
                                try {
                                    fragment = ClassroomFragment.class.newInstance();
                                    fragment.setArguments(bundle);
                                    Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
                                    TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
                                    toolbarTitle.setText("Enter Classroom");
                                    NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                                    navigationView.getMenu().getItem(1).setChecked(true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                // Insert the fragment by replacing any existing fragment
                                FragmentManager fragmentManager = getFragmentManager();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.flContent, fragment)
                                        .commit();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //mTextView.setText("That didn't work!");
                    System.out.println("hit an error: " + error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    String enrollID = mClassID.getText().toString();
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("studentID", mGoogleSignInAccount.getId());
                    params.put("lectureID", enrollID);
                    params.put("requestType", "registerClass");
                    return params;
                }
            };

            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        } else {
            String url = "http://fierce-savannah-23542.herokuapp.com/Guest";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            //mTextView.setText("Response is: "+ response.substring(0,500));
                            System.out.println("register response: " + response);
                           if(response != "" && response != null) {
                               try {
                                   JSONObject o = new JSONObject(response);
                                   Intent intent = new Intent(getContext(), QuestionActivity.class);
                                   Bundle b = new Bundle();
                                   b.putString("lectureID", o.getString("id"));
                                   b.putString("studentID", "guest");
                                   b.putString("lectureName", o.getString("department") + o.getString("classNumber"));
                                   intent.putExtras(b);
                                   startActivity(intent);
                               } catch (JSONException e) {
                                   e.printStackTrace();
                               }
                           }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //mTextView.setText("That didn't work!");
                    System.out.println("hit an error: " + error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    String enrollID = mClassID.getText().toString();
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lectureID", enrollID);
                    return params;
                }
            };

            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }
    }
}
