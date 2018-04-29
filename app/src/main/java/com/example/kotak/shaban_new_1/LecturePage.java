package com.example.kotak.shaban_new_1;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by Daren Liu on 7/25/2016.
 */
public class LecturePage extends AppCompatActivity {
    Course course;
    User user;
    Lecture lecture;
    String url="https://shaban.rit.albany.edu/";
    ArrayList<Document> documentList;
    String transcriptURL;
    String lectureId;
    String courseId;
    ListView courseListView;
    FrameLayout frameLayout;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutoriallist);
        frameLayout=(FrameLayout) findViewById(R.id.content_main_fragment);
         Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        lecture = (Lecture) intent.getSerializableExtra("lecture");
        course = (Course) intent.getSerializableExtra("course");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        //adding button : https://stackoverflow.com/questions/44218313/android-adding-buttons-to-toolbar-programatically?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa

        //now get Editor
        //SharedPreferences.Editor editor = sharedPref.edit();
        //put your value
        if(lecture!=null)
        {
/*
            editor.putString("lectureId", lecture.getLectureId());
            editor.putString("lectureTitle", lecture.getLectureDescription());
            toolbar.setTitle(lecture.getLectureDescription());
            lectureId=lecture.getLectureId();
        */    LocalPersistence.witeObjectToFile(LecturePage.this,lecture,"lecture");
            //commits your edits

        }
        else
        {
            lecture=(Lecture)LocalPersistence.readObjectFromFile(LecturePage.this,"lecture");
            //lectureId = sharedPref.getString("lectureId", "Not Available");
            //toolbar.setTitle(sharedPref.getString("lectureTitle", "Not Available"));
        }
        toolbar.setTitle(lecture.getLectureDescription());
        lectureId=lecture.getLectureId();
        if (course!=null)
        {
            //editor.putString("courseId",course.getCourseId());
            LocalPersistence.witeObjectToFile(LecturePage.this,course,"course");
            //courseId=course.getCourseId();
        }
        else
        {
            course= (Course) LocalPersistence.readObjectFromFile(LecturePage.this,"course");
            //courseId=sharedPref.getString("courseId","not available");
        }
        courseId=course.getCourseId();
        if (user!=null)
        {
            LocalPersistence.witeObjectToFile(LecturePage.this,user,"user");
        }
        else
        {
            user= (User) LocalPersistence.readObjectFromFile(LecturePage.this,"user");
        }

        //editor.commit();
        Button chatButton = new Button(this);
        Toolbar.LayoutParams l1= new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        l1.gravity= Gravity.RIGHT;
        chatButton.setBackgroundColor(Color.TRANSPARENT);
        chatButton.setTextColor(Color.WHITE);
        chatButton.setLayoutParams(l1);
        chatButton.setText("Chat");
        toolbar.addView(chatButton);
        chatButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
             //   Log.d("Button:","clicked");
               // intent = new Intent(LecturePage.this.getBaseContext(), Chat.class);
                /*intent.putExtra("course", course);
                intent.putExtra("user", user);
                intent.putExtra("lecture", lecture);
                startActivity(intent);*/
                Intent intent = new Intent(LecturePage.this.getBaseContext(), ChatActivity.class);
                intent.putExtra("course", course);
                intent.putExtra("user", user);
                intent.putExtra("lecture", lecture);
                startActivity(intent);
                
            }
        });


        setSupportActionBar(toolbar);
        //  getActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ConnectivityManager cm =
                (ConnectivityManager)LecturePage.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(!isConnected)
        {
            showAlertDialog(LecturePage.this, "No Internet Connection",
                    "You don't have internet connection.", false);

        }
        else
            new GetTutorialList().execute();
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class GetTutorialList extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress loading dialog
            progressDialog = new ProgressDialog(LecturePage.this);
            progressDialog.setMessage("Loading all lectures...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Making a request to url and getting response
            String jsonStr = null;
            String jsonStr2 = null;

            if( documentList== null) {
                try {
                    jsonStr = JsonReader.readJsonFromUrl(url + "lecture?course=" + courseId);
                  //  jsonStr2=JsonReader.readJsonFromUrl(url+"lecture?id=" + lectureId);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

               // Log.d("docstring:",jsonStr);
                documentList = JsonReader.parseJsonTutorial(jsonStr);
                transcriptURL=JsonReader.parseJsonTranscript(jsonStr);
          //     Log.d("transcriptURL",""+transcriptURL);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            /**
             * Updating received data from JSON into ListView
             */
            //final ArrayList<String> listOfDocumentNames = new ArrayList<String>();
            final ArrayList<String> listOfFileNames = new ArrayList<String>();
            ArrayList<Integer> imgid=new ArrayList<Integer>();
            int i=0;
            if (transcriptURL!=null && !transcriptURL.equals("null")) {
                listOfFileNames.add(transcriptURL.split("/")[2]);
                imgid.add(R.drawable.ic_receipt_black_24dp);
            }
             for(Document document: documentList){
                listOfFileNames.add(document.getUrl().split("/")[2]);
                 imgid.add(R.drawable.ic_assignment_black_24dp);
                // Log.d("file:",""+document.getUrl().split("/")[2]);
             }
            CustomListAdapter adapter=new CustomListAdapter(LecturePage.this,listOfFileNames,imgid);

            View v = LayoutInflater.from(LecturePage.this).inflate(R.layout.course_fragment_layout, null);
        //    Log.d("adapet:",""+adapter.getCount());


            courseListView = (ListView)  v.findViewById(R.id.course_list_view);
            //   courseListView.removeAllViews();
            //  courseListView.removeAllViews();
            if(courseListView.getParent()!=null)
                ((ViewGroup)courseListView.getParent()).removeView(courseListView);

            frameLayout.addView(courseListView);

            courseListView = (ListView) findViewById(R.id.course_list_view);
            courseListView.setAdapter(adapter);
            courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Object item = adapterView.getItemAtPosition(i);
            //        Log.d("urlselected:",""+(String)item);
                    //File myFile = new File("https://shaban.rit.albany.edu/files/"+(String)item);
                    try {
                        Uri uri = Uri.parse((String)item);
                 //now get Editor
                Intent intent;
                if (uri.toString().contains(".mov") || uri.toString().contains(".3gp") || uri.toString().contains(".mpg") || uri.toString().contains(".mpeg") || uri.toString().contains(".mpe") || uri.toString().contains(".mp4") || uri.toString().contains(".avi"))
                {
                  //  Log.d("vid:",""+url);
                    intent = new Intent(LecturePage.this.getBaseContext(), VideoPlayerActivity.class);
                }
                else
                {
                    intent = new Intent(LecturePage.this.getBaseContext(), WebViewActivity.class);
                 }
                 intent.putExtra("url", "" + (String) item);
                 startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }



    /*
     * Author: Hồng Thái
     * Date: 5/25/2015
     * Availability: http://www.devexchanges.info/2015/05/android-tip-combining-multiple.html
     */
    public void setHeight(ListView listView){
        ListAdapter listAdapter = listView.getAdapter();

        int height = 0;
        int width = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        for(int x = 0; x < listAdapter.getCount(); x++){
            View item = listAdapter.getView(x, null, listView);
            item.measure(width, View.MeasureSpec.UNSPECIFIED);
            height += item.getMeasuredHeight();
        }
        ViewGroup.LayoutParams newLayoutParams = listView.getLayoutParams();
        newLayoutParams.height = height + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(newLayoutParams);
        listView.requestLayout();
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
                LecturePage.this.moveTaskToBack(true);
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
                Intent intent = new Intent(LecturePage.this.getBaseContext(), LecturePage.class);
                intent.putExtra("lecture", lecture);
                intent.putExtra("course", course);
                LecturePage.this.startActivity(intent);

                //   getActivity().finish();
                //System.exit(0);
            }
        });
        alertDialog.show();
    }

}

