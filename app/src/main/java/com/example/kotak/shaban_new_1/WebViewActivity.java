package com.example.kotak.shaban_new_1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
        if(!isConnected)
        {
            showAlertDialog(WebViewActivity.this, "No Internet Connection",
                    "You don't have internet connection.", false);

        }
        else {
            Intent intent = getIntent();
            url = (String) intent.getSerializableExtra("url");
            web_view = (WebView) findViewById(R.id.web_view);
            web_view.getSettings().setJavaScriptEnabled(true);

            web_view.setWebViewClient(new WebViewClient());
            web_view.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + "https://shaban.rit.albany.edu/files/" + url);
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
