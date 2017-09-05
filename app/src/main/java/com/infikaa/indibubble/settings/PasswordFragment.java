package com.infikaa.indibubble.settings;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.infikaa.indibubble.R;

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

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Sohan on 24-Oct-16.
 */
public class PasswordFragment extends Fragment {
    Toolbar toolbar;
    EditText old,newp,conf;
    Button up;

    public static PasswordFragment newInstance() {

        Bundle args = new Bundle();

        PasswordFragment fragment = new PasswordFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_password, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar=(Toolbar)view.findViewById(R.id.toolbar_password);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            toolbar.setTitle("Change Password");
        }

        old=(EditText)view.findViewById(R.id.old);
        newp=(EditText)view.findViewById(R.id.newp);
        conf=(EditText)view.findViewById(R.id.conf);
        up=(Button)view.findViewById(R.id.update);

        old.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isPassValid(old)){
                    old.setTextColor(Color.GREEN);
                    //Toast.makeText(getActivity(), ""+old.getText().toString(),Toast.LENGTH_SHORT).show();
                }

                else {
                    old.setTextColor(Color.RED);
                    //Toast.makeText(getActivity(), "" + old.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("Bluebucket", MODE_PRIVATE);
                String emailId = pref.getString("emailId", "");
                String oldPass = old.getText().toString();
                String newPass = newp.getText().toString();

                ChangePassword password=new ChangePassword();
                password.execute(emailId,oldPass,newPass,"cus");
                /*if (isPassValid(old)) {
                    if (isPassValid(newp)) {
                        if (newp.getText().toString().equals(conf.getText().toString())) {


                        }
                        else {
                            Toast.makeText(getActivity(),"Password doesnt match", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                        Toast.makeText(getActivity(),"New password invalid!", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getActivity(),"Old password invalid!", Toast.LENGTH_SHORT).show();*/
            }
        });
    }
    public final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public boolean isEmailIDValid(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find() && emailStr.length()!=0;
    }
    public boolean isPassValid(EditText pass) {
        return pass.getText().toString().length() >= 4;
    }

    public class ChangePassword extends AsyncTask<String, Void, String[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        public final String LOG_TAG = "Password";
        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String requestJSONString = "";

            final String BASE_URL = getActivity().getString(R.string.host) + "/changePassword.waterp.php";
            final String USERNAME_PARAM = "username";
            final String OLD_PASS_PARAM = "oldpassword";
            final String NEW_PASS_PARAM = "newpassword";
            final String USER_PARAM = "user ";

            try {
                Uri.Builder builtUri = new Uri.Builder()
                        .appendQueryParameter(USERNAME_PARAM, params[0])
                        .appendQueryParameter(OLD_PASS_PARAM, params[1])
                        .appendQueryParameter(NEW_PASS_PARAM, params[2])
                        .appendQueryParameter(USER_PARAM, params[3]);


                Log.v(LOG_TAG, "Order URL: " + builtUri.toString());

                URL url = new URL(BASE_URL);
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
                        requestJSONString += line;
                    }
                } else {
                    requestJSONString = "";
                }
                Log.v(LOG_TAG, "Order JSON: " + requestJSONString);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getRequestResult(requestJSONString);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        private String[] getRequestResult(String jsonStr) throws JSONException {

            JSONObject pJson = new JSONObject(jsonStr);
            String resultStr[] = new String[2];

            final String OWN_SUCCESS = "success";
            final String OWN_ERROR = "errorCode";

            resultStr[0]=pJson.getString(OWN_SUCCESS);
            resultStr[1]=pJson.getString(OWN_ERROR);

            return resultStr;
        }
        @Override
        protected void onPostExecute(String[] strings) {
            Toast.makeText(getContext(),">>>>>>>>"+strings[0]+"|||"+strings[1],Toast.LENGTH_SHORT).show();
        }
    }
}
