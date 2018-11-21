package com.example.andrew.uscask;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


public class QuestionActivity extends AppCompatActivity {

    private OkHttpClient client;
    private ArrayList<Message> questionsArray = new ArrayList<Message>();
    private EditText mEditText;
    private WebSocket ws;
    private String studentID;
    private String lectureID;
    private String lectureName;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Gson gson = new Gson();

    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            System.out.println("Connected!");
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            //questionsArray.add(text);
            System.out.println("Recieved message: " + text);
            Message m = gson.fromJson(text, Message.class);
            if(m.getType().equals("Vote")) {
                for(int i = 0; i < questionsArray.size(); i++) {
                    if(questionsArray.get(i).getMessageID().equals(m.getMessageID())) {
                        System.out.println("Vote message recieved");
                        Boolean vote = questionsArray.get(i).vote(m.getSender());
                        mAdapter.notifyItemChanged(i);
                        if(vote) {
                            if(i != 0 && questionsArray.get(i).getVotes() > questionsArray.get(i-1).getVotes()) {
                                Message temp = questionsArray.get(i);
                                questionsArray.set(i, questionsArray.get(i-1));
                                questionsArray.set(i-1, temp);
                                //mAdapter.notifyItemRangeChanged(i-1,2);
                                mAdapter.notifyItemMoved(i, i-1);
                            } else {
                                mAdapter.notifyItemChanged(i);
                            }
                        } else {
                            if(i != questionsArray.size()-1 && questionsArray.get(i).getVotes() < questionsArray.get(i+1).getVotes()) {
                                Message temp = questionsArray.get(i+1);
                                questionsArray.set(i+1, questionsArray.get(i));
                                questionsArray.set(i, temp);
                                mAdapter.notifyItemMoved(i+1, i);
                                System.out.println("Swapping: " + gson.toJson(questionsArray.get(i)) + " with " + gson.toJson(questionsArray.get(i+1)));
                                System.out.println(questionsArray.get(i+1).getVotes());
                            } else {
                                mAdapter.notifyItemChanged(i);
                            }
                        }
                    }
                }
            } else {
                questionsArray.add(m);
                System.out.println("Questions Array: " + questionsArray);
                mAdapter.notifyItemInserted(questionsArray.size()-1);
            }
        }


        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);

        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        studentID = getIntent().getExtras().getString("studentID");
        lectureID = getIntent().getExtras().getString("lectureID");
        lectureName = getIntent().getExtras().getString("lectureName");
        //-------------Setting up toolbar-------------//
        Toolbar toolbar = findViewById(R.id.questionToolbar);
        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title_question);
        toolbarTitle.setText(lectureName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mRecyclerView = (RecyclerView) findViewById(R.id.questionsList);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        client = new OkHttpClient();




        mEditText = findViewById(R.id.askText);
        mEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendQuestion();
                    return true;
                }
                return false;
            }
        });

        startWebSocket();

        mAdapter = new QuestionsAdapter(questionsArray, ws, studentID, lectureID);
        mRecyclerView.setAdapter(mAdapter);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem  item) {
        if(item.getItemId() == android.R.id.home) {
            ws.close(1000, null);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void startWebSocket() {
       // Request request = new Request.Builder().url("ws://fierce-savannah-23542.herokuapp.com/chatWS?userID="+studentID+"&classID="+lectureID).build();
        Request request = new Request.Builder().url("ws://fierce-savannah-23542.herokuapp.com/chatWS/"+studentID+"/"+lectureID).build();
        EchoWebSocketListener listener = new EchoWebSocketListener();
        ws = client.newWebSocket(request, listener);
        System.out.println("Ws: " + ws);

        client.dispatcher().executorService().shutdown();
    }
    private void sendQuestion() {
        if(ws != null) {
            Message m = new Message(mEditText.getText().toString(), lectureID, UUID.randomUUID().toString(), studentID);
            ws.send(gson.toJson(m));
        }
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        mEditText.setText("");
    }
}
