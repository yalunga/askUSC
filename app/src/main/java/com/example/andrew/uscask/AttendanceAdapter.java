package com.example.andrew.uscask;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.MyViewHolder> {

    private ArrayList<JSONObject> mJsonArray;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ImageView mImageView;
        public MyViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.attendanceDate);
            mImageView = v.findViewById(R.id.attendanceImage);
        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public AttendanceAdapter(ArrayList<JSONObject> JsonArray) {
        mJsonArray = JsonArray;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public AttendanceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try {
            JSONObject o = mJsonArray.get(position);
            holder.mTextView.setText(o.getString("date"));
            if(o.getInt("attended") == 0) {
                holder.mImageView.setImageResource(R.drawable.ic_close_red_24dp);
            } else {
                holder.mImageView.setImageResource(R.drawable.ic_check_green_24dp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {
        return mJsonArray.size();
    }


}
