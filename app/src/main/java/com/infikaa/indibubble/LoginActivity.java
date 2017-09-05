package com.infikaa.indibubble;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    EditText username, password;
    Button login;
    ProgressDialog progressDialog;
    private String success, errorcode;
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id.name_field);
        password = (EditText) findViewById(R.id.password_field);
        login = (Button)findViewById(R.id.login_button);

        username.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_person_black_24dp),null,null,null);
        password.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_locked),null,null,null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Slide slide=new Slide();
            slide.setSlideEdge(Gravity.BOTTOM);
            slide.setDuration(1000);
            slide.setInterpolator(new AccelerateDecelerateInterpolator());
            getWindow().setEnterTransition(slide);
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    loginAcc();
            }
        });
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str=s.toString();
                if (isEmailIDValid(str))
                    username.setTextColor(Color.GREEN);
                else
                    username.setTextColor(Color.RED);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void loginAcc() {
        LoginUser lu = new LoginUser();
        String un, ps;
        un = username.getText().toString();
        ps = password.getText().toString();
        lu.execute(un, ps);
    }
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public boolean isEmailIDValid(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        if(emailStr.length()==0) {
            //Toast.makeText(getBaseContext(),"Please enter the username", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (matcher.find()) {
            //Toast.makeText(getBaseContext(),"Please enter a valid username", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public class LoginUser extends AsyncTask<String, Void, String[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(LoginActivity.this);
            progressDialog.setTitle("Logging in");
            progressDialog.setMessage("Authenticating credentials. Please wait.");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        private final String LOG_TAG = LoginUser.class.getSimpleName();
        @Override
        protected String[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String requestJSONString="";

            final String LOGIN_BASE_URL = "http://waterp.hol.es/cus.login.waterp.php";
            final String USERNAME_PARAM = "username";
            final String PASSWORD_PARAM = "password";

            try {
                Uri.Builder builtUri = new Uri.Builder()
                        .appendQueryParameter(USERNAME_PARAM, params[0])
                        .appendQueryParameter(PASSWORD_PARAM, params[1]);

                Log.v(LOG_TAG, builtUri.toString());
                URL url = new URL(LOGIN_BASE_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                String query = builtUri.build().getEncodedQuery();
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == urlConnection.HTTP_OK) {
                    String line;
                    reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    while ((line = reader.readLine()) != null) {
                        requestJSONString+=line;
                    }
                }
                else {
                    requestJSONString="";
                }

                Log.v(LOG_TAG, "Login JSON: " + requestJSONString);
            }
            catch (Exception e) {
                Log.e(LOG_TAG, "Error", e);
                return null;
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getRequestResult(requestJSONString);
            }
            catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }
        private String[] getRequestResult(String jsonStr) throws JSONException {

            final String OWN_SUCCESS = "success";
            final String OWN_ERROR = "errorCode";
            final String OWN_FNAME = "firstname";
            final String OWN_MNAME = "middlename";
            final String OWN_LNAME = "lastname";
            final String OWN_PHONE = "phoneno";
            final String OWN_ADDRESS = "address";

            JSONObject loginJson = new JSONObject(jsonStr);
            String resultStr[] = new String[7];
            resultStr[0] = loginJson.getString(OWN_SUCCESS);
            resultStr[1] = loginJson.getString(OWN_ERROR);
            if (resultStr[0].equals("true")) {
                resultStr[2] = loginJson.getString(OWN_FNAME);
                if (loginJson.getString(OWN_MNAME).equals("null"))
                    resultStr[3] = "";
                else
                    resultStr[3] = loginJson.getString(OWN_MNAME);
                resultStr[4] = loginJson.getString(OWN_LNAME);
                resultStr[5] = loginJson.getString(OWN_PHONE);
                resultStr[6] = loginJson.getString(OWN_ADDRESS);
            }

            return resultStr;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            progressDialog.dismiss();
            if (strings != null) {
                if (strings[0].equals("false")) {
                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                    if (strings[1].equals("5")) {
                        Toast.makeText(getApplicationContext(), "Cannot read database!", Toast.LENGTH_LONG).show();
                    } else if (strings[1].equals("4")) {
                        Toast.makeText(getApplicationContext(), "Invalid password and username!", Toast.LENGTH_LONG).show();
                    } else if (strings[1].equals("3")) {
                        Toast.makeText(getApplicationContext(), "Cannot connect to database!", Toast.LENGTH_LONG).show();
                    } else if (strings[1].equals("2")) {
                        Toast.makeText(getApplicationContext(), "Invalid password or mail ID!", Toast.LENGTH_LONG).show();
                    } else if (strings[1].equals("1")) {
                        Toast.makeText(getApplicationContext(), "Invalid or empty data fields!!", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Login Successfull", Toast.LENGTH_SHORT).show();

                    SharedPreferences pref = getApplicationContext().getSharedPreferences("Bluebucket", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("emailId", username.getText().toString());
                    editor.putString("username", strings[2] + " " + strings[3] + " " + strings[4]);
                    editor.putString("phoneno", strings[5]);
                    editor.putString("cusid","1");
                    editor.putString("address", strings[6]);
                    editor.putString("postalcode","123456");
                    editor.commit();

                    Intent myIntent = new Intent(LoginActivity.this, SurfActivity.class);
                    startActivity(myIntent);
                }
            }
            else
                Toast.makeText(getApplicationContext(), "Null", Toast.LENGTH_SHORT).show();



            /*
            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
            if (strings[1].equals("5")) {
                Toast.makeText(getApplicationContext(), "Cannot read database!", Toast.LENGTH_LONG).show();
            }
            else if (strings[1].equals("4")) {
                Toast.makeText(getApplicationContext(), "Invalid password and username!", Toast.LENGTH_LONG).show();
            }
            else if (strings[1].equals("3")) {
                Toast.makeText(getApplicationContext(), "Cannot connect to database!", Toast.LENGTH_LONG).show();
            }
            else if (strings[1].equals("2")) {
                Toast.makeText(getApplicationContext(), "Invalid password or mail ID!", Toast.LENGTH_LONG).show();
            }
            else if (strings[1].equals("1")) {
                Toast.makeText(getApplicationContext(), "Invalid or empty data fields!!", Toast.LENGTH_LONG).show();
            }
            */
        }
    }
}
