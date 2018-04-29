package com.example.kotak.shaban_new_1;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private AutoCompleteTextView phonenumberView;
    //private EditText mPasswordView;
    //private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = LoginActivity.this.getFilesDir().toString();
        ConnectivityManager cm =
                (ConnectivityManager)LoginActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(!isConnected)
        {
            showAlertDialog(LoginActivity.this, "No Internet Connection",
                    "You don't have internet connection.", false);

        }



        Log.d("LoginUrl",url);
        File file = new File(url, "userInfo");
        if(file.exists()){
            try {
                FileInputStream inputStream = new FileInputStream(file);
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                inputStream.close();
                JSONObject json = new JSONObject(new String(buffer, "UTF-8"));
                user = new User(json.getString("firstName"), json.getString("lastName"), json.getString("phone"));
                Intent intent = new Intent(LoginActivity.this.getBaseContext(), MainActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("url",url);
                LoginActivity.this.startActivity(intent);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            setContentView(R.layout.activity_login);

            Button btn = (Button) findViewById(R.id.sign_in_button);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText phone = (EditText) findViewById(R.id.phonenumber);
                    if (phone.getText().toString() == "") {
                        Toast.makeText(getApplicationContext(), "Enter your phone number", Toast.LENGTH_LONG);
                    } else {
                        new ConfirmUser(phone.getText().toString()).execute();
                    }
                }
            });
        }
        //mLoginFormView = findViewById(R.id.login_form);
        //mProgressView = findViewById(R.id.login_progress);
    }
User user;
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class ConfirmUser extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;
        String phoneNumber;

        public ConfirmUser(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Logging in...");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Making a request to url and getting response
            String jsonStr = null;
            try {
                jsonStr = JsonReader.readJsonFromUrl("https://shaban.rit.albany.edu/users/" + phoneNumber);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            user = JsonReader.ParseJSONUser(jsonStr);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            if (user != null) {
                writeToFile(user);
                Intent intent = new Intent(LoginActivity.this.getBaseContext(), MainActivity.class);
                intent.putExtra("user", user);
                LoginActivity.this.startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "User not registered", Toast.LENGTH_LONG);
            }
        }

        private void writeToFile(User user) {
            JSONObject json = new JSONObject();
            try {
                json.put("firstName", user.getFirstName());
                json.put("lastName", user.getLastName());
                json.put("phone", user.getPhone());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String jsonToFile = json.toString();
            String uri = LoginActivity.this.getFilesDir().toString();
             Log.d("urilol:", uri);
            final File file = new File(uri, "userInfo");
            try {
                FileOutputStream fileOutputStream;
                OutputStreamWriter outputStreamWriter;
                fileOutputStream = LoginActivity.this.getBaseContext().openFileOutput(file.getName(), Context.MODE_PRIVATE);
                   Log.d("filename:",file.getName());
                outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                outputStreamWriter.write(jsonToFile + '\n');
                outputStreamWriter.close();

                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                LoginActivity.this.moveTaskToBack(true);
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
                Intent intent = new Intent(LoginActivity.this.getBaseContext(), LoginActivity.class);
                LoginActivity.this.startActivity(intent);

                //   getActivity().finish();
                //System.exit(0);
            }
        });
        alertDialog.show();
    }

}

