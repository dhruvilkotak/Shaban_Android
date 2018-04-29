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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.app.FragmentManager;

import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    User user;
    String phoneNumber;
    String url;
    Bundle bundle = new Bundle();


    private TextView userName;
    private TextView phoneNumberText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // toolbar.setTitle("Courses - Shaban ");
        setSupportActionBar(toolbar);

        ConnectivityManager cm =
                (ConnectivityManager)MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(!isConnected)
        {
            showAlertDialog(MainActivity.this, "No Internet Connection",
                    "You don't have internet connection.", false);

        }
        url = MainActivity.this.getFilesDir().toString();
        File file = new File(url, "userInfo");
        Intent intent = getIntent();

        if(file.exists()){
            try {
                FileInputStream inputStream = new FileInputStream(file);
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                inputStream.close();
                JSONObject json = new JSONObject(new String(buffer, "UTF-8"));
                user = new User(json.getString("firstName"), json.getString("lastName"), json.getString("phone"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            user = (User) intent.getSerializableExtra("user");
            phoneNumber = user.getPhone();
        }

        url=(String)intent.getSerializableExtra("url");
        bundle.putSerializable("user",user);

     //   new GetCourse().execute();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);

        userName=(TextView)hView.findViewById(R.id.userNameTextView);
      //  Log.d("userName",""+userName);
        phoneNumberText=(TextView) hView.findViewById(R.id.phoneNumberTextView);
        if (userName!=null)
            userName.setText(user.getFirstName()+" "+user.getLastName());
        if (phoneNumberText!=null)
            phoneNumberText.setText(user.getPhone());

        onNavigationItemSelected(navigationView.getMenu().getItem(0));
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(MainActivity.this);

            //AlertDialog alertDialog = new AlertDialog.Builder(context).create();

            // Setting Dialog Title
            alertDialog.setTitle("Exit Application");

            // Setting Dialog Message
            alertDialog.setMessage("Do you want to exit application ? ");

            // Setting alert dialog icon
            alertDialog.setIcon(R.drawable.ic_exit_to_app_black_24dp);

            alertDialog.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.this.moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    //getActivity().finish();
                    System.exit(1);
                }
            });
            // Setting OK Button
            alertDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.show();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        FragmentManager fragmentManager = getFragmentManager();

        if (id == R.id.courses) {
            fragment =new CourseFragment();
            // Handle the camera action
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.content_main_fragment, fragment).commit();

        } else if (id == R.id.about) {
            fragment =new AboutFragment();

            fragmentManager.beginTransaction().replace(R.id.content_main_fragment, fragment).commit();

        } else if (id == R.id.signout) {
            File file = new File(url+"/userInfo" +
                    "");
        //    Log.d("action:","signout");
            boolean deleted = file.delete();
          //  Log.d("url:",url);
            //Log.d("file:",""+deleted);
            Intent intent = new Intent(MainActivity.this.getBaseContext(), LoginActivity.class);
            MainActivity.this.startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                MainActivity.this.moveTaskToBack(true);
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
                Intent intent = new Intent(MainActivity.this.getBaseContext(), MainActivity.class);
                MainActivity.this.startActivity(intent);

                //   getActivity().finish();
                //System.exit(0);
            }
        });
        alertDialog.show();
    }

}
