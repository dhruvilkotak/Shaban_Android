package com.example.kotak.shaban_new_1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {

    WebView web_view;

    @Override
    public void onBackPressed()
    {
        if(web_view.canGoBack())
        {
            web_view.goBack();
        }
        else
        {
            super.onBackPressed();
        }
    }
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ConnectivityManager cm =
                (ConnectivityManager)WebViewActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
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

        if(!isConnected)
        {
            showAlertDialog(WebViewActivity.this, "No Internet Connection",
                    "You don't have internet connection.", false);

        }
        else {
            web_view = (WebView) findViewById(R.id.web_view);
            web_view.getSettings().setJavaScriptEnabled(true);
            Log.d("url",url);
            web_view.setWebViewClient(new WebViewClient());
            web_view.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + "https://shaban.rit.albany.edu/files/" + url);
        }
    }
    public void showAlertDialog(Context context, String title, String message, Boolean status) {

        AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);

        //AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setCancelable(false);
        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon(R.drawable.ic_not_interested_black_24dp);

        alertDialog.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                WebViewActivity.this.moveTaskToBack(true);
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
                Intent intent = new Intent(WebViewActivity.this.getBaseContext(), WebViewActivity.class);
                intent.putExtra("url", "" + url);

                WebViewActivity.this.startActivity(intent);

                //   getActivity().finish();
                //System.exit(0);
            }
        });
        alertDialog.show();
    }

}
