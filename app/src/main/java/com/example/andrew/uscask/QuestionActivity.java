package com.example.andrew.uscask;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


public class QuestionActivity extends AppCompatActivity {

    private OkHttpClient client;
    private ArrayList<String> questionsArray = new ArrayList<String>();
    private EditText mEditText;
    private WebSocket ws;

    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        @Override
        public void onOpen(WebSocket webSocket, Response response) {

        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            questionsArray.add(text);
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
        //-------------Setting up toolbar-------------//
        Toolbar toolbar = findViewById(R.id.questionToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        client = new OkHttpClient();

        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.questions_text, questionsArray);
        ListView listView = findViewById(R.id.questionsList);
        listView.setAdapter(adapter);



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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem  item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void startWebSocket() {
        Request request = new Request.Builder().url("http://10.10.1.96:8080/FinalProject/ss").build();
        EchoWebSocketListener listener = new EchoWebSocketListener();
         ws = client.newWebSocket(request, listener);

        client.dispatcher().executorService().shutdown();
    }
    private void sendQuestion() {
        if(ws != null) {
            ws.send(mEditText.getText().toString());
        }
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        mEditText.setText("");
    }
}
