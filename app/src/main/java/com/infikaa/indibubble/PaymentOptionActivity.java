package com.infikaa.indibubble;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.infikaa.indibubble.product.Product;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the

 * to handle interaction events.
 * Use the {@link PaymentOptionActivity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaymentOptionActivity extends Fragment {
    int screenWidth;
    ArrayList<Product> ProductArray=new ArrayList<Product>();
    LinearLayout paymentLayout;
    Button proceed;
    RadioGroup radioGroup;
    ProgressDialog progressDialog;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PaymentOptionActivity.
     */
    // TODO: Rename and change types and number of parameters
    public static PaymentOptionActivity newInstance(ArrayList<Product> ProductArray,int screenWidth) {
        PaymentOptionActivity fragment = new PaymentOptionActivity();
        Bundle args = new Bundle();
        args.putSerializable("products",ProductArray);
        args.putInt("screenWidth",screenWidth);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ProductArray=(ArrayList<Product>)getArguments().getSerializable("products");
            screenWidth=getArguments().getInt("screenWidth");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
     //   ((IssueBackButton) getActivity()).issueBackButton(false);
        ((FragmentToActivity) getActivity()).SetNavState(R.id.nav_home);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_payment_option, container, false);
        paymentLayout=(LinearLayout) view.findViewById(R.id.paymentlayout);
        radioGroup=(RadioGroup) view.findViewById(R.id.paymentoption);
        proceed=(Button) view.findViewById(R.id.paymentproceedbut);
        return view;
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        getFragmentManager().beginTransaction().remove(this).commit();
        getActivity().getFragmentManager().popBackStack();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //cusid,username,phoneno, postalcode, brand, productname, quantity, productprice,offerdescription
        DateTime dateTime=new DateTime();
        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

        //Toast.makeText(getActivity(),dtf.print(dateTime),Toast.LENGTH_SHORT).show();
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.displaycontent, PurchaseDetailActivity.newInstance(),"PurchaseDetailActivity").addToBackStack("PurchaseDetailActivity").commit();
            }
        });
    }


    class registerOrder extends AsyncTask<String,Void,String[]>{
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
        protected String[] doInBackground(String... params) {
            return new String[0];
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            progressDialog.dismiss();

        }
    }

}
