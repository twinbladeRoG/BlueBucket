package com.infikaa.indibubble.order;

import android.app.Fragment;
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

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Sohan on 24-Oct-16.
 */
public class ProcessFragment extends Fragment {
    View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    Order processOrders[];

    String emailId;

    public static ProcessFragment newInstance() {
        
        Bundle args = new Bundle();
        
        ProcessFragment fragment = new ProcessFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view==null) {
            view = inflater.inflate(R.layout.fragment_process, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.refresh_process);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.process_recycler_view);
        mLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("Bluebucket", MODE_PRIVATE);
        emailId = pref.getString("emailId", "");

        GetProcess go = new GetProcess();
        go.execute(emailId, "atprocess");

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetProcess go = new GetProcess();
                go.execute(emailId, "atprocess");
            }
        });
    }

    public class GetProcess extends AsyncTask<String, Void, Order[]> {

        public final String LOG_TAG = GetProcess.class.getSimpleName();

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
                    processOrders = new Order[n];
                    for (int i=0; i<n ; i++) {
                        JSONObject list = new JSONObject(pJson.getString(String.valueOf(i+1)));

                        processOrders[i]=new Order();
                        processOrders[i].setProductName(list.getString("productname"));
                        processOrders[i].setQuantity(Integer.parseInt(list.getString("quantity")));
                        processOrders[i].setProductPrice(Integer.parseInt(list.getString("productprice")));
                        processOrders[i].setOfferDescription(list.getString("offerdescription"));
                        processOrders[i].setTotalPrice(Integer.parseInt(list.getString("totalprice")));
                        /*processOrders[i].setAtHalt();
                        processOrders[i].setAtProcess();
                        processOrders[i].setAtComplete();*/
                        processOrders[i].setOrderID(list.getString("orderid"));
                        processOrders[i].setPaymentType(list.getString("paymenttype"));
                        processOrders[i].setAddress(list.getString("address"));
                        processOrders[i].setImageLink(list.getString("imagelink"));
                    }
                }
                else {
                    return null;
                }
            }

            return processOrders;
        }

        @Override
        protected void onPostExecute(Order[] orders) {
            swipeRefreshLayout.setRefreshing(false);

            mAdapter = new CardViewAdapter(orders, ProcessFragment.this);
            mRecyclerView.setAdapter(mAdapter);
        }
    }
}
