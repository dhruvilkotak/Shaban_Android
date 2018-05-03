package com.example.kotak.shaban_new_1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayerActivity extends AppCompatActivity {

    VideoView video_view;
     @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("orine:",newConfig.orientation+"");

    }
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        url = (String) intent.getSerializableExtra("url");

        if(url!=null) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            //now get Editor
            SharedPreferences.Editor editor = sharedPref.edit();
            //put your value
            editor.putString("url", url);
            //commits your edits
            editor.commit();
        }
        else {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            url = sharedPref.getString("url", "Not Available");
        }

        ConnectivityManager cm =
                (ConnectivityManager)VideoPlayerActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(!isConnected)
        {
            showAlertDialog(VideoPlayerActivity.this, "No Internet Connection",
                    "You don't have internet connection.", false);

        }
        else {
            video_view = (VideoView) findViewById(R.id.video_view);
            //video_view.setVideoPath("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
            if (url.contains(".mov")) {
                video_view.setVideoPath("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
            } else
                video_view.setVideoPath("https://shaban.rit.albany.edu/files/" + url);

            //http://www.techotopia.com/index.php/An_Android_Studio_VideoView_and_MediaController_Tutorial
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(video_view);
            video_view.setMediaController(mediaController);
            video_view.start();
        }
    }
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
                VideoPlayerActivity.this.moveTaskToBack(true);
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
                Intent intent = new Intent(VideoPlayerActivity.this.getBaseContext(), VideoPlayerActivity.class);
                intent.putExtra("url", "" + url);

                VideoPlayerActivity.this.startActivity(intent);
        }
        });
        alertDialog.show();
    }

}
