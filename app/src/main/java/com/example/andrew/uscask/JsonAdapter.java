package com.example.andrew.uscask;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonAdapter extends ArrayAdapter<JSONObject> implements View.OnClickListener{
    private ArrayList<JSONObject> classList;
    Context mContext;

    public JsonAdapter(ArrayList<JSONObject> jsonObjects, Context context) {
        super(context, R.layout.row_item, jsonObjects);
        this.classList = jsonObjects;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {
        System.out.println("Click has been called");
        int position = (Integer) v.getTag();
        Object object = getItem(position);
        JSONObject jsonObject = (JSONObject) object;
        try {
            System.out.println(jsonObject.get("classDescription"));
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        JSONObject jsonObject = (JSONObject) getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_item, parent, false);
        }
        TextView department = (TextView) convertView.findViewById(R.id.department);
        TextView classNumber = (TextView) convertView.findViewById(R.id.classNumber);
        TextView instructor = (TextView) convertView.findViewById(R.id.instructor);
        TextView description = (TextView) convertView.findViewById(R.id.description);
        try {
            department.setText(jsonObject.getString("department"));
            classNumber.setText(jsonObject.getString("classNumber"));
            instructor.setText(jsonObject.getString("instructor"));
            description.setText(jsonObject.getString("classDescription"));
        } catch(JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
