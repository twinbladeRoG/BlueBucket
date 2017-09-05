package com.infikaa.indibubble.order;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Sohan on 24-Oct-16.
 */
public class HaltFragment extends Fragment {
    View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    Order haltOrders[];

    String emailId;

    public static HaltFragment newInstance() {
        
        Bundle args = new Bundle();
        
        HaltFragment fragment = new HaltFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view==null) {
            view = inflater.inflate(R.layout.fragment_halt, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.refresh_halt);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CardViewAdapter(haltOrders, HaltFragment.this);
        mRecyclerView.setAdapter(mAdapter);

        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("Bluebucket", MODE_PRIVATE);
        emailId = pref.getString("emailId", "");

        GetOrders go = new GetOrders();
        go.execute(emailId, "athalt");

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetOrders go = new GetOrders();
                go.execute(emailId, "athalt");
            }
        });
    }

    public class GetOrders extends AsyncTask<String, Void, Order[]> {

        public final String LOG_TAG = GetOrders.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Order[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String requestJSONString = "";

            final String BASE_URL = getActivity().getString(R.string.host)+"/cus.getOrderTable.waterp.php";
            final String USERNAME_PARAM = "username";
            final String STATE_PARAM = "state";

            try {
                Uri.Builder builtUri = new Uri.Builder()
                        .appendQueryParameter(USERNAME_PARAM, params[0])
                        .appendQueryParameter(STATE_PARAM, params[1]);


                Log.v(LOG_TAG, "Order URL: "+builtUri.toString());
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
                        requestJSONString+=line;
                    }
                }
                else {
                    requestJSONString="";
                }
                Log.v(LOG_TAG, "Order JSON: " + requestJSONString);
            }
            catch (Exception e ) {
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

        private Order[] getRequestResult(String jsonStr) throws JSONException {

            JSONObject pJson = new JSONObject(jsonStr);
            String resultStr[][] = null;

            final String OWN_SUCCESS = "success";
            final String OWN_NOPRODUCT = "noofProducts";
            final String OWN_ERROR = "errorCode";

            if (pJson.getString(OWN_SUCCESS).equals("true")) {
                int n = Integer.parseInt(pJson.getString(OWN_NOPRODUCT));

                if (n>0) {
                    resultStr = new String[n][19];
                    haltOrders = new Order[n];
                    Log.v(LOG_TAG, "Order Array: " + haltOrders.length+", N: "+ n);
                    for (int i=0; i<n ; i++) {
                        JSONObject list = new JSONObject(pJson.getString(String.valueOf(i+1)));

                        Log.v(LOG_TAG, "I: " + i + " "+list.getString("productname"));
                        haltOrders[i]=new Order();
                        haltOrders[i].setProductName(list.getString("productname"));
                        haltOrders[i].setQuantity(Integer.parseInt(list.getString("quantity")));
                        haltOrders[i].setProductPrice(Integer.parseInt(list.getString("productprice")));
                        haltOrders[i].setOfferDescription(list.getString("offerdescription"));
                        haltOrders[i].setTotalPrice(Integer.parseInt(list.getString("totalprice")));
                        /*haltOrders[i].setAtHalt();
                        haltOrders[i].setAtProcess();
                        haltOrders[i].setAtComplete();*/
                        haltOrders[i].setOrderID(list.getString("orderid"));
                        haltOrders[i].setPaymentType(list.getString("paymenttype"));
                        haltOrders[i].setAddress(list.getString("address"));
                        haltOrders[i].setImageLink(list.getString("imagelink"));
                    }
                }
                else {return null;}
            }

            return haltOrders;
        }

        @Override
        protected void onPostExecute(Order order[]) {
            swipeRefreshLayout.setRefreshing(false);


            mAdapter = new CardViewAdapter(order, HaltFragment.this);
            mRecyclerView.setAdapter(mAdapter);

            /*
            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
            if (strings[1].equals("5")) {
                Toast.makeText(getApplicationContext(), "Cannot read database!", Toast.LENGTH_LONG).show();
            }
            else if (strings[1].equals("3")) {
                Toast.makeText(getApplicationContext(), "Cannot connect to database!", Toast.LENGTH_LONG).show();
            }
            else if (strings[1].equals("1")) {
                Toast.makeText(getApplicationContext(), "Invalid or empty data fields!!", Toast.LENGTH_LONG).show();
            }
            */
        }
    }

    public void cancelOrder(String orderId) {
        CancelOrder cancelOrder = new CancelOrder();
        cancelOrder.execute(
                emailId,
                orderId
        );
    }

    public class CancelOrder extends AsyncTask<String, Void, String[]> {

        public final String LOG_TAG = CancelOrder.class.getSimpleName();
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(view.getContext(), "Loading", "loading cart", true);
        }

        @Override
        protected String[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String requestJSONString = "";

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String time=df.format(date);

            final String BASE_URL = "http://waterp.hol.es/cancelOrder.waterp.php";
            final String USERNAME_PARAM = "username";
            final String CUS_PARAM = "user";
            final String ORDER_ID_PARAM = "orderid";
            final String CANCEL_PARAM = "atcancel";

            try {
                Uri.Builder builtUri = new Uri.Builder()
                        .appendQueryParameter(USERNAME_PARAM, params[0])
                        .appendQueryParameter(ORDER_ID_PARAM, params[1])
                        .appendQueryParameter(CANCEL_PARAM, time)
                        .appendQueryParameter(CUS_PARAM, "cus");


                Log.v(LOG_TAG, "Order URL: "+builtUri.toString());
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
                        requestJSONString+=line;
                    }
                }
                else {
                    requestJSONString="";
                }
                Log.v(LOG_TAG, "Order JSON: " + requestJSONString);
            }
            catch (Exception e ) {
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
            progressDialog.dismiss();
            if (strings[0].equals("true")) {
                Toast.makeText(view.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                //GetOrders go = new GetOrders();
                //go.execute("subho040995@gmail.com", "athalt");
            }
            else {
                Toast.makeText(view.getContext(), "Failed to delete!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
