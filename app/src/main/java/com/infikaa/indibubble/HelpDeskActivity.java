package com.infikaa.indibubble;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HelpDeskActivity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HelpDeskActivity extends Fragment {
    String username;
    CheckBox anon;
    Spinner type;
    EditText message;
    Button submit;
    ArrayList<String> options;
    ArrayAdapter<String> adapter;
    ProgressDialog progressDialog;
    String[] typeString;
    android.support.v7.widget.Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    FragmentManager fragmentManager;

    public static HelpDeskActivity newInstance(String username) {
        HelpDeskActivity fragment = new HelpDeskActivity();
        Bundle args = new Bundle();
        args.putString("username",username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString("username");
        }


    }

    @Override
    public void onResume() {
        super.onResume();
   //     ((IssueBackButton) getActivity()).issueBackButton(true);
        ((FragmentToActivity) getActivity()).SetNavState(R.id.nav_help);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_help_desk, container, false);
        anon=(CheckBox) view.findViewById(R.id.helpbeanon);
        message=(EditText) view.findViewById(R.id.helpmessage);
        type=(Spinner) view.findViewById(R.id.helptype);
        submit=(Button) view.findViewById(R.id.helpsubmitbut);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        toolbar=(android.support.v7.widget.Toolbar)view.findViewById(R.id.toolbar_help);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            toolbar.setTitle("Help");
        }
        fragmentManager=getActivity().getFragmentManager();
        drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragmentManager.getBackStackEntryCount() > 1){
                    fragmentManager.popBackStack();
                }else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        toggle.syncState();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        options=new ArrayList<String>();
        typeString=new String[]{"suggestion","help"};
        options.add("Suggestion");
        options.add(1,"Help");
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,options);
        type.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        type.setSelection(0);
        anon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    options.remove(1);
                    adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,options);
                    type.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    type.setSelection(0);
                }
                else{
                    options.add(1,"Help");
                    adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,options);
                    type.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    type.setSelection(0);
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(message.getText().toString().matches("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Empty Field");
                    builder.setMessage("Please make sure you don't have empty fields.");
                    builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                             dialog.dismiss();


                        }
                    });
                    builder.setCancelable(false);
                    builder.show();

                }
                else{
                    new OnPostRequest("subho040995@gmail.com",message.getText().toString(),typeString[type.getSelectedItemPosition()]).execute();
                }
            }
        });



    }

    class OnPostRequest extends AsyncTask<String,Void,String> {
        String typeString,message,username;
        public OnPostRequest(String username, String message, String typeString){
            this.typeString=typeString;
            this.username=username;
            this.message=message;
        }



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading..");
            progressDialog.setTitle("Checking Network");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        @Override
        protected String doInBackground(String... params) {
            String response="";
            URL url = null;

            try {
                url = new URL(getActivity().getString(R.string.host)+"/helpdesk.waterp.php");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(1000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("type", typeString )
                        .appendQueryParameter("username", username)
                        .appendQueryParameter("message", message)
                        .appendQueryParameter("user", "cus");


                String query = builder.build().getEncodedQuery();


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                int responseCode=conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        response+=line;
                    }
                }
                else {
                    response="";

                }





            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            progressDialog.dismiss();
            Toast.makeText(getActivity(),s,Toast.LENGTH_SHORT).show();
            JSONObject jbj=null;
            try {
                jbj=new JSONObject(s);

                if(!s.equals("") && jbj.getString("success").equals("true") && jbj.getString("errorCode").equals("null") ) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Complain Registered.");
                    if(typeString.equals("help")) {
                        builder.setMessage("We have successully registered your complain. Please check your mail. Token number : #"+String.format("%06s",jbj.getString("token")));
                    }
                   else {
                        builder.setMessage("We have successully registered your suggestion.");
                    }
                    builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getFragmentManager().popBackStack();
                            getFragmentManager().beginTransaction().replace(R.id.displaycontent, new Home(),"Home").addToBackStack("Home").commit();

                            dialog.dismiss();


                        }
                    });
                    builder.setCancelable(false);
                    builder.show();

                }
                else if(!s.equals("") && jbj.getString("success").equals("false") && jbj.getString("errorCode").equals("5") ){
                    Toast.makeText(getActivity(),"Mysql query error",Toast.LENGTH_SHORT).show();
                }

                else if(!s.equals("") && jbj.getString("success").equals("false") && jbj.getString("errorCode").equals("7") ){
                    Toast.makeText(getActivity(),"Mail failed",Toast.LENGTH_SHORT).show();
                }
                else if(!s.equals("") && jbj.getString("success").equals("false") && jbj.getString("errorCode").equals("8") ){
                    Toast.makeText(getActivity(),"Invalid user type",Toast.LENGTH_SHORT).show();
                }
                else if(!s.equals("") && jbj.getString("success").equals("false") && jbj.getString("errorCode").equals("4") ){
                    Toast.makeText(getActivity(),"Invalid user",Toast.LENGTH_SHORT).show();
                }
                else if(!s.equals("") && jbj.getString("success").equals("false") && jbj.getString("errorCode").equals("3") ){
                    Toast.makeText(getActivity(),"Failed Database connectivity",Toast.LENGTH_SHORT).show();
                }
                else if(!s.equals("") && jbj.getString("success").equals("false") && jbj.getString("errorCode").equals("1") ){
                    Toast.makeText(getActivity(),"Invalid data",Toast.LENGTH_SHORT).show();
                }

                else{
                    Toast.makeText(getActivity(),"Undefined error",Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }






        }
    }
}
