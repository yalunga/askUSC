package com.example.andrew.uscask;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.UUID;

import okhttp3.WebSocket;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.MyViewHolder> {
    private ArrayList<Message> messageArrayList;
    private WebSocket ws;
    private String studentID;
    private String classID;
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView questionContent;
        public ImageView mImageView;
        public TextView upvotes;
        public MyViewHolder(View v) {
            super(v);
            questionContent = v.findViewById(R.id.questionContent);
            mImageView = v.findViewById(R.id.upvoteImage);
            upvotes = (TextView) v.findViewById(R.id.upvotes);
        }
    }

    public QuestionsAdapter(ArrayList<Message> messageArrayList, WebSocket ws, String studentID, String classID) {
        this.messageArrayList = messageArrayList;
        this.ws = ws;
        this.studentID = studentID;
        this.classID = classID;
    }

    @Override
    public QuestionsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.question_item, parent, false);

        QuestionsAdapter.MyViewHolder vh = new QuestionsAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Message m = messageArrayList.get(position);
        holder.upvotes.setText(Integer.toString(m.getVotes()));
        holder.questionContent.setText(m.getData());
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Message upvote = new Message(classID, m.getMessageID(), studentID);
                String jsonMessage = gson.toJson(upvote);
                ws.send(jsonMessage);
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }
}
