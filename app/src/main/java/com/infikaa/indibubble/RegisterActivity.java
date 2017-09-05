package com.infikaa.indibubble;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.infikaa.indibubble.utility.SendMail;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    EditText name, postalAddress, phoneNumber, otpField, emailID, pinC, pass, cpass;
    TextInputLayout tname, tpostal, tphone, totp, temail, tpinc, tpass, tcpass;
    Spinner phCode, state, city;
    Button send,gsign;
    ArrayAdapter<String> states;
    ArrayAdapter<String> cities;
    ArrayAdapter<String> phCodes;
    ArrayList<String> stateArray = new ArrayList<String>();
    ArrayList<String> citiesArray = new ArrayList<String>();
    ArrayList<String> phCodesArray = new ArrayList<String>();

    public int otp;

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name=(EditText)findViewById(R.id.register_name);
        emailID= (EditText)findViewById(R.id.register_email);
        phoneNumber= (EditText)findViewById(R.id.register_phone);
        otpField= (EditText)findViewById(R.id.register_otp);
        postalAddress=(EditText)findViewById(R.id.register_address);
        pinC=(EditText)findViewById(R.id.register_pincode);
        pass=(EditText)findViewById(R.id.register_password);
        cpass=(EditText)findViewById(R.id.register_confirm_password);

        tname=(TextInputLayout) findViewById(R.id.register_input_name);
        temail= (TextInputLayout)findViewById(R.id.register_input_email);
        tphone= (TextInputLayout)findViewById(R.id.register_input_phone);
        totp= (TextInputLayout)findViewById(R.id.register_input_otp);
        tpostal=(TextInputLayout)findViewById(R.id.register_input_address);
        tpinc=(TextInputLayout)findViewById(R.id.register_input_pincode);
        tpass=(TextInputLayout)findViewById(R.id.register_input_password);
        tcpass=(TextInputLayout)findViewById(R.id.register_input_confirm_password);

        phCode=(Spinner)findViewById(R.id.spinner);
        state=(Spinner)findViewById(R.id.spinner2);
        city=(Spinner)findViewById(R.id.spinner3);

        send=(Button)findViewById(R.id.button);

        phCodes = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, phCodesArray);

        cities = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, citiesArray);

        states = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, stateArray);

        initArr();

        phCode.setAdapter(phCodes);
        city.setAdapter(cities);
        state.setAdapter(states);

        final Drawable rightIcon = getResources().getDrawable(R.drawable.ic_done_24dp);
        final Drawable wrongIcon = getResources().getDrawable(R.drawable.ic_wrong_24dp);
        final Drawable nameIcon  = getResources().getDrawable(R.drawable.ic_person_black_24dp);
        final Drawable phoneIcon = getResources().getDrawable(R.drawable.ic_phone);
        final Drawable emailIcon = getResources().getDrawable(R.drawable.ic_email);
        final Drawable addressIcon = getResources().getDrawable(R.drawable.ic_address);
        final Drawable pinIcon = getResources().getDrawable(R.drawable.ic_location);
        final Drawable passIcon = getResources().getDrawable(R.drawable.ic_locked);

        name.setCompoundDrawablesWithIntrinsicBounds(nameIcon,null,null,null);
        postalAddress.setCompoundDrawablesWithIntrinsicBounds(addressIcon,null,null,null);
        pinC.setCompoundDrawablesWithIntrinsicBounds(pinIcon,null,null,null);
        emailID.setCompoundDrawablesWithIntrinsicBounds(emailIcon,null,null,null);
        phoneNumber.setCompoundDrawablesWithIntrinsicBounds(phoneIcon,null,null,null);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    otpField.setEnabled(true);
                    Toast.makeText(getBaseContext(), "Valid", Toast.LENGTH_SHORT).show();
                    sendMail();
                }
                else {
                    otpField.setEnabled(false);
                    Toast.makeText(getBaseContext(), "Invalid or Empty field", Toast.LENGTH_SHORT).show();
                }
            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isNameValid(name.getText().toString())) {
                    name.setCompoundDrawablesRelativeWithIntrinsicBounds(nameIcon,null,rightIcon,null);
                    tname.setError(null);
                }
                else {
                    name.setCompoundDrawablesRelativeWithIntrinsicBounds(nameIcon,null,wrongIcon,null);
                    tname.setError("Must start with capital and have a title");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isNumberValid(phoneNumber.getText().toString())) {
                    phoneNumber.setCompoundDrawablesRelativeWithIntrinsicBounds(phoneIcon, null, rightIcon, null);
                    tphone.setError(null);
                }
                else {
                    phoneNumber.setCompoundDrawablesRelativeWithIntrinsicBounds(phoneIcon, null, wrongIcon, null);
                    tphone.setError("Must be a 10-digit valid phone number");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        pinC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isPINValid(pinC.getText().toString())) {
                    pinC.setCompoundDrawablesRelativeWithIntrinsicBounds(pinIcon, null, rightIcon, null);
                    tpinc.setError(null);
                }
                else {
                    pinC.setCompoundDrawablesRelativeWithIntrinsicBounds(pinIcon, null, wrongIcon, null);
                    tpinc.setError("Must be a 6-digit valid pincode");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        emailID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isEmailIDValid(emailID.getText().toString())) {
                    emailID.setCompoundDrawablesRelativeWithIntrinsicBounds(emailIcon, null, rightIcon, null);
                    temail.setError(null);
                }
                else {
                    emailID.setCompoundDrawablesRelativeWithIntrinsicBounds(emailIcon, null, wrongIcon, null);
                    temail.setError("Must be a valid email ID");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isPassValid()) {
                    pass.setCompoundDrawablesRelativeWithIntrinsicBounds(rightIcon,null,null,null);
                    tpass.setError(null);
                }
                else {
                    pass.setCompoundDrawablesRelativeWithIntrinsicBounds(wrongIcon,null,null,null);
                    tpass.setError("Must contain 2 capital & small letters & 2 numbers and special charactoers");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        cpass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (cpass.getText().toString().equals(pass.getText().toString())) {
                    cpass.setCompoundDrawablesRelativeWithIntrinsicBounds(rightIcon,null,null,null);
                    tcpass.setError(null);
                }
                else {
                    cpass.setCompoundDrawablesRelativeWithIntrinsicBounds(wrongIcon,null,null,null);
                    tcpass.setError("Doesn't match");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otpField.setEnabled(false);
        otpField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (otpField.getText().toString().equals(String.valueOf(otp))) {
                    register();
                    otpField.setBackgroundColor(Color.TRANSPARENT);
                    otpField.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,rightIcon,null);
                    Toast.makeText(getApplicationContext(),"MATCHED OTP", Toast.LENGTH_SHORT).show();
                }
                else {
                    otpField.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,wrongIcon,null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        cpass.setLongClickable(false);
        pass.setLongClickable(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            updateUI(true,acct);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false, null);
        }
    }
    private void updateUI(boolean signedIn, GoogleSignInAccount acct) {
        if (signedIn) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();

            emailID.setText(personEmail);
            name.setText(personName);
            Toast.makeText(this, "Gmail details recieved!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sign In Failed!", Toast.LENGTH_SHORT).show();
        }
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        emailID.setText(null);
                        name.setText(null);
                        postalAddress.setText(null);
                    }
                });
    }

    public void initArr() {
        stateArray.add("West Bengal");
        citiesArray.add("Kolkata");
        citiesArray.add("Howrah");
        phCodesArray.add("+91");
    }

    private int generateOTP() {
        Random ran = new Random();
        return (100000 + ran.nextInt(900000));
    }

    public final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public final Pattern VALID_PASSWORD_REGEX =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$", Pattern.CASE_INSENSITIVE);

    public boolean isEmailIDValid(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find() && emailStr.length()!=0;
    }

    public boolean isNameValid( String name ) {
        String regex="^[ A-z]+$";
        String names[];
        names=name.toString().trim().split("\\s+");
        return names.length >= 2 && name.matches(regex);
    }

    public boolean isNumberValid (String num) {
        String regexStr = "^[0-9]{10}$";
        return num.matches(regexStr) && num.length()!=0;
    }

    public boolean isPINValid(String p) {
        String regexStr = "^[1-9][0-9]{5}$";
        //Toast.makeText(getBaseContext(),"Len: "+p.length(),Toast.LENGTH_SHORT).show();
        return p.length()==6 && p.length()!=0;

    }

    public boolean isPassValid() {
        Matcher matcher = VALID_PASSWORD_REGEX.matcher(pass.getText().toString());
        return matcher.find();
    }

    public boolean isValid() {

        if (!isNameValid(name.getText().toString()) && name.getText().toString().length()==0)
            return false;
        if (!isNumberValid(phoneNumber.getText().toString()))
            return false;
        if (!isEmailIDValid(emailID.getText().toString()))
            return false;
        if (postalAddress.getText().toString().length()==0)
            return false;
        if (!isPINValid(pinC.getText().toString()))
            return false;
        if (!isPassValid())
            return false;
        return true;
    }

    public void sendMail() {
        //Getting content for email
        String email = emailID.getText().toString();
        String subject = "BlueBucket One-Time OTP";
        otp=generateOTP();
        String message = ""+otp;

        //Creating SendMail object
        SendMail sm = new SendMail(this, email, subject, message);

        //Executing sendmail to send email
        sm.execute();
    }

    public void register() {
        String names[], fname, midname, lname;
        names=name.getText().toString().trim().split("\\s+");
        if (names.length == 2) {
            fname=names[0];
            lname=names[1];
            midname="";
        }
        else {
            fname=names[0];
            midname=names[1];
            lname=names[2];
        }
        RegisterUser registerUser=new RegisterUser();
        registerUser.execute(
                fname,
                midname,
                lname,
                phoneNumber.getText().toString(),
                emailID.getText().toString(),
                pass.getText().toString(),
                postalAddress.getText().toString()+" ! "+
                        city.getSelectedItem().toString()+" ! "+
                        state.getSelectedItem().toString()+" ! India ! "+
                        pinC.getText().toString()
                );
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public class RegisterUser extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = RegisterUser.class.getSimpleName();
        @Override
        protected String[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String requestJSONString="";

            final String REGISTER_BASE_URL = "http://waterp.hol.es/cus.register.waterp.php";
            final String FIRSTNAME_PARAM = "firstname";
            final String MIDDLENAME_PARAM = "middlename";
            final String LASTNAME_PARAM = "lastname";
            final String PHONENO_PARAM = "phoneno";
            final String USERNAME_PARAM = "username";
            final String PASSWORD_PARAM = "password";
            final String ADDRESS_PARAM = "address";

            try {
                Uri.Builder builtUri = new Uri.Builder()
                        .appendQueryParameter(FIRSTNAME_PARAM, params[0])
                        .appendQueryParameter(MIDDLENAME_PARAM, params[1])
                        .appendQueryParameter(LASTNAME_PARAM, params[2])
                        .appendQueryParameter(PHONENO_PARAM, params[3])
                        .appendQueryParameter(USERNAME_PARAM, params[4])
                        .appendQueryParameter(PASSWORD_PARAM, params[5])
                        .appendQueryParameter(ADDRESS_PARAM, params[6]);


                Log.v(LOG_TAG, builtUri.toString());
                URL url = new URL(REGISTER_BASE_URL);
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
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
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
                        e.printStackTrace();
                    }
                }
            }

            try {
                return getRequestResult(requestJSONString);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        private String[] getRequestResult(String jsonStr) throws JSONException {
            JSONObject requestJSON=new JSONObject(jsonStr);
            String success[] = new String[2];
            success[0] = requestJSON.getString("success");
            success[1] = requestJSON.getString("errorCode");
            Log.v(LOG_TAG, "Parsed JSON String: " + success[0]+", "+success[1]);
            return success;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if (strings[0].equals("true")) {
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                if (strings[1].equals("5")) {
                    Toast.makeText(getApplicationContext(), "Cannot read database!", Toast.LENGTH_LONG).show();
                }
                else if (strings[1].equals("4")) {
                    Toast.makeText(getApplicationContext(), "Username already exists!", Toast.LENGTH_LONG).show();
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
            }
        }
    }
}
