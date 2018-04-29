package com.example.kotak.shaban_new_1;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;


public class LectureView extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    public Course course;
    public User user;
    public String courseId;
    public String courseName;
    ArrayList<Lecture> lectureList;
    ArrayList<Course> courseList;
    private static String url = "https://shaban.rit.albany.edu/lecture";
        private static String url2="https://shaban.rit.albany.edu/course";
    ListView courseListView;
FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_view);
        frameLayout=(FrameLayout) findViewById(R.id.content_main_fragment);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        //String phoneNumber = (String)user.getPhone();
        course=(Course)intent.getSerializableExtra("course");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        //  getActionBar().setHomeButtonEnabled(true);


        if(course!=null) {
            courseId = course.getCourseId();
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            //now get Editor
            SharedPreferences.Editor editor = sharedPref.edit();
            //put your value
            editor.putString("courseId", courseId);
            editor.putString("courseName", course.getCourseName());

            //commits your edits
            editor.commit();
            toolbar.setTitle(course.getCourseName());
        }
        else {
            //course=(Course)intent.getSerializableExtra("course");
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            courseId = sharedPref.getString("courseId", "Not Available");
            courseName=sharedPref.getString("courseName", "Not Available");
            toolbar.setTitle(courseName);
            // Create object of SharedPreferences.
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onStart() {
        super.onStart();

        ConnectivityManager cm =
                (ConnectivityManager)LectureView.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(!isConnected)
        {
            showAlertDialog(LectureView.this, "No Internet Connection",
                    "You don't have internet connection.", false);

        }
        else
            new GetLecture().execute();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }/*
        return super.onOptionsItemSelected(item);*/
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    private class GetLecture extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress loading dialog
            progressDialog = new ProgressDialog(LectureView.this);
            progressDialog.setMessage("Loading all Folders...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Making a request to url and getting response
            String jsonStr = null;
            String jsonStr2=null;
            if(lectureList == null) {
                try {
                    jsonStr = JsonReader.readJsonFromUrl(url + "?course=" + courseId);
                    jsonStr2=JsonReader.readJsonFromUrl(url2+"?parent="+courseId);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                courseList=JsonReader.ParseJSONCourse(jsonStr2,1);
                lectureList = JsonReader.ParseJSONLecture(jsonStr);
              //  Log.d("courseList:",""+courseList.size());
               // Log.d("lectureList",""+lectureList.size());
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
            final ArrayList<String> listOfLectureCourse = new ArrayList<String>();
          //  final ArrayList<String> listOfCourseNames = new ArrayList<String>();
            ArrayList<Integer> imgid=new ArrayList<Integer>();
            int i=0;
            final int lectureTotal=lectureList.size();
            final int courseTotal=courseList.size();
            for(Lecture lecture: lectureList){
                listOfLectureCourse.add(lecture.getLectureDescription());
                imgid.add(R.drawable.ic_folder_black_24dp);
            }
            for (Course courseObj: courseList)
            {
                listOfLectureCourse.add(courseObj.getCourseName());
                imgid.add(R.drawable.ic_class_black_24dp);
            }
         //   Log.d("lecturesize:",""+listOfLectureCourse.size());


            CustomListAdapter adapter=new CustomListAdapter(LectureView.this,listOfLectureCourse,imgid);
            /*LayoutInflater inflater = (LayoutInflater)getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.course_fragment_layout, null);
            */
            View v = LayoutInflater.from(LectureView.this).inflate(R.layout.course_fragment_layout, null);
           // Log.d("adapet:",""+adapter.getCount());


            courseListView = (ListView)  v.findViewById(R.id.course_list_view);
         //   courseListView.removeAllViews();
          //  courseListView.removeAllViews();
            if(courseListView.getParent()!=null)
                ((ViewGroup)courseListView.getParent()).removeView(courseListView);

          frameLayout.addView(courseListView);

            courseListView.setAdapter(adapter);

            courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Object item = adapterView.getItemAtPosition(i);
                    int indexOfLectureCourse = listOfLectureCourse.indexOf((String) item);
                    Intent intent=null;
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(LectureView.this);
                    //now get Editor
                    SharedPreferences.Editor editor = sharedPref.edit();

                    if (indexOfLectureCourse<lectureTotal)
                    {//lecture List
                        intent = new Intent(LectureView.this.getBaseContext(), LecturePage.class);
                        Lecture lecture = lectureList.get(indexOfLectureCourse);
                        intent.putExtra("lecture", lecture);
                        intent.putExtra("course", course);
                      /*  editor.putString("courseId", courseId);
                        editor.putString("courseName", courseName);
*/
                   }
                    if (indexOfLectureCourse>=lectureTotal && indexOfLectureCourse<(lectureTotal+courseTotal))
                    {//courseList
                        intent = new Intent(LectureView.this.getBaseContext(), LectureView.class);
                        Course courseObj2 = courseList.get(indexOfLectureCourse-lectureTotal);
                        intent.putExtra("course", courseObj2);
                //        Log.d("courseName",courseObj2.getParent().getCourseName());
/*
                        editor.putString("courseName", courseObj2.getParent().getCourseName());
                        editor.putString("courseId", courseObj2.getParent().getCourseId());
*/

                    }


                    //put your value

                    //commits your edits
                    editor.commit();

                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            });
        }

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
                LectureView.this.moveTaskToBack(true);
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
                Intent intent = new Intent(LectureView.this.getBaseContext(), LectureView.class);
                intent.putExtra("course", course);
                intent.putExtra("user", user);
                LectureView.this.startActivity(intent);


            }
        });
        alertDialog.show();
    }


}
