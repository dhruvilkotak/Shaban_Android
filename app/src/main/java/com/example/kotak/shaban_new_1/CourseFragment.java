package com.example.kotak.shaban_new_1;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by kotak on 21/04/2018.
 */

public class CourseFragment extends Fragment {

    private ListView courseListView;
    User user;


    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);

        //AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);
        alertDialog.setCancelable(false);
        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon(R.drawable.ic_not_interested_black_24dp);

        alertDialog.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                //getActivity().finish();
                System.exit(1);
            }
        });
        // Setting OK Button
        alertDialog.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
                Intent intent = new Intent(getActivity().getBaseContext(), LoginActivity.class);
                getActivity().startActivity(intent);

                //   getActivity().finish();
                //System.exit(0);
            }
        });
        alertDialog.show();

        // Showing Alert Message
       // alertDialog.show();
    }


    ArrayList<Course> courseList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.course_fragment_layout, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        Toolbar toolbar=getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Courses  ");
        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(!isConnected)
        {
            showAlertDialog(getActivity(), "No Internet Connection",
                    "You don't have internet connection.", false);

        }

        user=(User)getArguments().getSerializable("user");

        new GetCourse().execute();

    }

    private class GetCourse extends AsyncTask<Void, Void, Void> {


        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress loading dialog
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Making a request to url and getting response
            String jsonStr = null;
            if(courseList == null) {
                try {
                    jsonStr = JsonReader.readJsonFromUrl("https://shaban.rit.albany.edu/course");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                courseList = JsonReader.ParseJSONCourse(jsonStr,0);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            try {
                // Dismiss the progress dialog
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                /**
                 * Updating received data from JSON into ListView
                 */
                final ArrayList<String> listOfCourseNames = new ArrayList<String>();
                ArrayList<Integer> imgid = new ArrayList<Integer>();
                int i = 0;
                for (Course course : courseList) {

                    if (course.getParent() == null || course.getParent().getCourseId() == null) {
                        //  Log.d("parentId:",course.getParent().getCourseId());
                        listOfCourseNames.add(course.getCourseName());
                    //    Log.d("course", course.getCourseName());
                        imgid.add(R.drawable.ic_class_black_24dp);
                    }
                }

                CustomListAdapter adapter = new CustomListAdapter(getActivity(), listOfCourseNames, imgid);
                courseListView = (ListView) getActivity().findViewById(R.id.course_list_view);
                courseListView.setAdapter(adapter);

                courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Object item = adapterView.getItemAtPosition(i);
                        int indexOfCourse = listOfCourseNames.indexOf((String) item);
                        Intent intent = new Intent(getActivity().getBaseContext(), LectureView.class);
                        Course course = courseList.get(indexOfCourse);
                        intent.putExtra("course", course);
                        intent.putExtra("user", user);
                        startActivity(intent);
                    }
                });
            }catch (Exception e)
            {
//                showAlertDialog(getActivity(), "No Internet Connection",
  //                      "You don't have internet connection.", false);
            }
        }
    }

}
