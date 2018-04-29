package com.example.kotak.shaban_new_1;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import io.socket.client.Ack;
import io.socket.emitter.Emitter;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private Button chatSendButton;
    private EditText inputMessageView;
    private ArrayList<Message> messages = new ArrayList<Message>();
   // private RecyclerView.Adapter messageAdapter;
    private String nameOfUser;
    private SailsIOClient socket;
    private User user;
    private Course course;
    private Lecture lecture;
    String lectureName;
    String lectureId;
    String firstname;
    android.os.Handler customHandler;
    private Timer myTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Intent intent=getIntent();
        user = (User) intent.getSerializableExtra("user");
        course=(Course)intent.getSerializableExtra("course");
        lecture=(Lecture) intent.getSerializableExtra("lecture");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = sharedPref.edit();
        //put your value
        if(lecture!=null)
        {

            editor.putString("lectureId", lecture.getLectureId());
            editor.putString("lectureTitle", lecture.getLectureDescription());
            toolbar.setTitle(" Chat : "+lecture.getLectureDescription());
            lectureId=lecture.getLectureId();
            //commits your edits

        }
        else
        {
            lectureId = sharedPref.getString("lectureId", "Not Available");
            toolbar.setTitle(" Chat : "+sharedPref.getString("lectureTitle", "Not Available"));
        }
        if(user!=null)
        {
            nameOfUser = user.getName();
            editor.putString("userName",nameOfUser);
        }
        else
        {
            nameOfUser=sharedPref.getString("userName","Not Available");
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ConnectivityManager cm =
                (ConnectivityManager)ChatActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(!isConnected)
        {
            showAlertDialog(ChatActivity.this, "No Internet Connection",
                    "You don't have internet connection.", false);

        }
        else
        {
            try {
                new GetAllMessages().execute().get();
                mMessageAdapter = new MessageListAdapter(this, messages, nameOfUser);

                mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mMessageRecycler.setLayoutManager(mLayoutManager);
                mMessageRecycler.setItemAnimator(new DefaultItemAnimator());
                mMessageRecycler.setAdapter(mMessageAdapter);
                inputMessageView = (EditText) findViewById(R.id.edittext_chatbox);
                scrollToBottom();

           customHandler = new android.os.Handler();
           customHandler.postDelayed(updateTimerThread, 10000);

           chatSendButton=(Button)findViewById(R.id.button_chatbox_send);
            chatSendButton.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v) {
                    try {
                        attemptSend();
                        //relodeMessages();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            inputMessageView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                    if (id == R.id.button_chatbox_send || id == EditorInfo.IME_NULL) {
                        try {
                            attemptSend();
                          //  relodeMessages();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                    return false;
                }
            });

            inputMessageView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (null == nameOfUser) return;
                    if (!socket.socket.isConnected()) return;
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            socket = new SailsIOClient("https://shaban.rit.albany.edu", lecture.getLectureId());
            socket.socket.on("message", onNewMessage);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
         }

        }
    }


    private Runnable updateTimerThread = new Runnable()

    {
        public void run()
        {
         try {
             new GetAllMessages().execute().get();
         }catch (Exception e)
         {

         }
           if(mMessageAdapter.getItemCount()!=messages.size() && messages.size()>0)
            {
                Log.d("messages","cominng");

                try {

                    mMessageAdapter.notifyItemInserted(messages.size() - 1);
                    mMessageAdapter = new MessageListAdapter(ChatActivity.this, messages, nameOfUser);
                    mMessageRecycler.setAdapter(mMessageAdapter);

                    scrollToBottom();
                }catch (Exception e)
                {

                }
            }

            //write here whaterver you want to repeat
            customHandler.postDelayed(this, 10000);
        }
    };


    private void attemptSend() throws Exception {
        if (null == nameOfUser) return;
        if (!socket.socket.isConnected()) return;

        final String message = inputMessageView.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            inputMessageView.requestFocus();
            return;
        }

        inputMessageView.setText("");
        addMessage(user.getFirstName()+" "+user.getLastName(), message); // user.getFirtsName
        JSONObject jsonObject = emitMessage(message);
        Log.d("sensingmsg:",jsonObject.toString());
        // perform the sending message attempt.
        socket.socket.post("/messages", jsonObject, new Ack() {
            @Override
            public void call(Object... args) {
                System.out.println("Message is sent"+message);
            }
        });
    }

    public JSONObject emitMessage(String message) throws Exception{
        JSONObject data = new JSONObject();
        JSONObject author = new JSONObject();
        JSONObject group = new JSONObject();

        author.put("firstName", user.getFirstName());
        author.put("lastName", user.getLastName());
        author.put("phone", user.getPhone());
        group.put("serial_number", lecture.getSerialNumber());
        group.put("description", lecture.getLectureDescription());
        group.put("transcript_url", lecture.getTranscriptUrl());
        group.put("id", lecture.getLectureId());
        group.put("course", course.getCourseId());
        data.put("author", author);
        data.put("group", group);
        data.put("content", message);
        return data;
    }



    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Are you being reached");
                    JSONObject data = (JSONObject) args[0];
                    JSONObject author;
                    String username;
                    String message;
                    try {
                        author = data.getJSONObject("author");
                        username = author.getString("firstName") + " " + author.getString("lastName");
                        message = data.getString("content");
                    } catch (JSONException e) {
                        return;
                    }
                    if(!username.equals(user.getName()))
                        try {
                            addMessage(username, message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
            });
        }
    };

    private void addMessage(String username, String message) throws JSONException {
        Message newMsg = new Message(username, message);
        messages.add(newMsg);
        writeToFile(username, newMsg);
        mMessageAdapter.notifyItemInserted(messages.size() - 1);
        scrollToBottom();
    }
    private void scrollToBottom() {
        mMessageRecycler.scrollToPosition(mMessageAdapter.getItemCount() - 1);
    }


    private void readMessagesFromFile(String url) throws IOException, JSONException, ExecutionException, InterruptedException {
        String uri = ChatActivity.this.getFilesDir().toString();
        final File file = new File(uri, url);
        if(!file.exists()) {
            file.createNewFile();
        }

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        JSONObject jsonObject;
        String line;
        Message message;
        while ((line = bufferedReader.readLine()) != null) {
            jsonObject = new JSONObject(line);
            message = new Message((String) jsonObject.get("user"), (String) jsonObject.get("message"));
            messages.add(message);
        }
        bufferedReader.close();
    }


    private void writeToFile(String username, Message message) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("user", username);
        json.put("message", message.getContent());
        String jsonToFile = json.toString();
        String uri = ChatActivity.this.getFilesDir().toString();
        final File file = new File(uri, "lecture" + lectureId);
        try {
            FileOutputStream fileOutputStream;
            OutputStreamWriter outputStreamWriter;
            fileOutputStream = this.openFileOutput(file.getName(), Context.MODE_PRIVATE);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(jsonToFile + '\n');
//            if(file.exists()) {
//                fileOutputStream = getContext().openFileOutput(file.getName(), Context.MODE_APPEND);
//                outputStreamWriter = new OutputStreamWriter(fileOutputStream);
//                outputStreamWriter.append(jsonToFile + '\n');
//            }
//            else {
//                fileOutputStream = getContext().openFileOutput(file.getName(), Context.MODE_PRIVATE);
//                outputStreamWriter = new OutputStreamWriter(fileOutputStream);
//                outputStreamWriter.write(jsonToFile + '\n');
//            }
            outputStreamWriter.close();

            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class GetAllMessages extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;
        private String url = "https://shaban.rit.albany.edu/messages?limit=1000&group=" + lectureId + "&sort=id%20DESC";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            // Showing progress loading dialog
//            progressDialog = new ProgressDialog(getActivity());
//            progressDialog.setMessage("Loading all messages...");
//            progressDialog.setCancelable(false);
//            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Making a request to url and getting response
            String jsonStr = null;
            HashMap<Integer, Message> messageMap;
            try {
                jsonStr = JsonReader.readJsonFromUrl(url);
                jsonStr = JsonReader.readJsonFromUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("messagessize:",messages.size()+"");
            messages = JsonReader.ParseJSONMessage(jsonStr, lectureId);
          //  Log.d("jsonstr",jsonStr);

            for(int x = 0; x < messages.size() - 1; x++){
                int min = x;
                for(int y = x+1; y < messages.size(); y++){
                    if(messages.get(y).getId() < messages.get(min).getId()) {
                        min = y;
                    }

                }
                Message temp = messages.get(x);
                messages.set(x, messages.get(min));
                messages.set(min, temp);
            }
            for (int x = messages.size() - 1; x >= 0; x--) {//
                Message msg = messages.get(x);
                try {

               //     Log.d("msg:",msg.getContent());
                    writeToFile(msg.getUsername(), msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            /*
            for (Message m:messages) {

                Log.d("messages:", "" + m.getContent());
            }*/
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // Dismiss the progress dialog
//            if (progressDialog.isShowing())
//                progressDialog.dismiss();

        }

    }



    @Override
    public void onDestroy() {

        socket.socket.Disconnect();
        socket.socket.off("message", onNewMessage);
        customHandler.removeCallbacks(updateTimerThread);
        super.onDestroy();


    }
    public void showAlertDialog(Context context, String title, String message, Boolean status) {

        AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);

        //AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon(R.drawable.ic_not_interested_black_24dp);

        alertDialog.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ChatActivity.this.moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                //getActivity().finish();
                System.exit(1);
            }
        });
        // Setting OK Button
        alertDialog.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                Intent intent = new Intent(ChatActivity.this.getBaseContext(), ChatActivity.class);
                intent.putExtra("course", course);
                intent.putExtra("user", user);
                intent.putExtra("lecture", lecture);
                ChatActivity.this.startActivity(intent);

                //   getActivity().finish();
                //System.exit(0);
            }
        });
        alertDialog.show();
    }

}
