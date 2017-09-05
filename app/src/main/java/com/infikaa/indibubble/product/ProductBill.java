package com.infikaa.indibubble.product;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.infikaa.indibubble.FragmentToActivity;
import com.infikaa.indibubble.PaymentOptionActivity;
import com.infikaa.indibubble.R;
import com.infikaa.indibubble.cart.FavcartDatabaseHelper;
import com.infikaa.indibubble.utility.Utility;

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
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductBill#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductBill extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    FragmentManager fragmentManager;

    private static ArrayList<Product> ProductsArray;
    private static int screenWidth;
    ProgressDialog progressDialog;
    ListView productlist;
    LinearLayout purchaseDetails,buyBillingDetails;
    EditText locality,city,state,billingpostalcode;
    Button checkout;
    TextView netprice;
    Double[] price;
    Integer[] purchaseQuantity;
    private ArrayList<String> ImagesArray;
    String address;
    String billingaddress;
    Boolean fixedQuantity;
    public ProductBill() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProductBill.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductBill newInstance(ArrayList<Product> ProductsArray, int screenWidth,Boolean fixedQuantity) {
        Bundle args = new Bundle();
        ProductBill fragment = new ProductBill();
        args.putSerializable("products", ProductsArray);
        args.putInt("screenWidth",screenWidth);
        args.putBoolean("fixedQuantity",fixedQuantity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            ProductsArray = (ArrayList<Product>) getArguments().getSerializable("products");
            screenWidth = getArguments().getInt("screenWidth");
            fixedQuantity=getArguments().getBoolean("fixedQuantity");
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
        View view= inflater.inflate(R.layout.fragment_product_bill, container, false);
        productlist=(ListView) view.findViewById(R.id.productlist);

        netprice=(TextView) view.findViewById(R.id.buyproductnetprice);
        locality=(EditText) view.findViewById(R.id.billinglocality);
        city=(EditText) view.findViewById(R.id.billingcity);
        state=(EditText) view.findViewById(R.id.billingstate);
        billingpostalcode=(EditText) view.findViewById(R.id.billingpostalcode);
        checkout=(Button) view.findViewById(R.id.billcheckoutbut);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        toolbar=(Toolbar) view.findViewById(R.id.toolbar_bill);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Product Bill");

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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(isNetworkAvailable()){
            new generateBill(this,fixedQuantity).execute();
           // Toast.makeText(getActivity(),String.valueOf(ProductsArray.size()),Toast.LENGTH_SHORT).show();
            SharedPreferences pref = getActivity().getSharedPreferences("Bluebucket", Context.MODE_PRIVATE);
            address=pref.getString("address","");

            pref=null;
            if(new Utility().checkPostalcode(address,ProductsArray.get(0).getPostalcode())){
                billingpostalcode.setText(String.valueOf(ProductsArray.get(0).getPostalcode()));
                String[] decodedAddress=address.split(",");
                locality.setText(decodedAddress[0].trim());
                city.setText(decodedAddress[1].trim());
                state.setText(decodedAddress[2].trim());
                decodedAddress=null;

            }
            else
                billingpostalcode.setText(String.valueOf(ProductsArray.get(0).getPostalcode()));

            billingpostalcode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("To change pincode, please re visit home and order again.")
                            .setCancelable(false)
                            .setTitle("Option unavailable")
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        }



        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Please ensure that you have active network connection.")
                    .setCancelable(false)
                    .setTitle("No Network Connectivity")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check address validity
                if(isNetworkAvailable()){
                   billingaddress=String.valueOf(locality.getText())+","+String.valueOf(locality.getText())+","+String.valueOf(city.getText())+","+String.valueOf(state.getText())+",India,"+String.valueOf(billingpostalcode.getText());
                   if(!billingaddress.equalsIgnoreCase(address)) {
                       address=billingaddress;
                       SharedPreferences pref = getActivity().getSharedPreferences("Bluebucket", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=pref.edit();
                        editor.putString("address",address);

                       pref = null;
                   }

                    new checkOrder(purchaseQuantity).execute();


                    }
                }

            }
        );



    }

    public boolean updateFavProduct(int position, boolean isChecked) {
        FavcartDatabaseHelper f=FavcartDatabaseHelper.getInstance(getActivity());

        if(isChecked){
            Product p=ProductsArray.get(position);

            p.setQuantity(purchaseQuantity[position]);
            Boolean flag=f.writeCart(p);
            f.close();
            return flag;
        }
        else {
            Boolean flag=f.deleteItemByProductid(ProductsArray.get(position).getProductid(),purchaseQuantity[position]);
            return flag;
        }
    }


    class generateBill extends AsyncTask<String,String,Void>{
        Boolean fixedQuantity;
        String[] s;
        String test="";
        int index;
        ProductBill productBill;
        generateBill(ProductBill productBill,Boolean fixedQuantity){
            this.productBill=productBill;
            this.fixedQuantity=fixedQuantity;
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
            s=new String[ProductsArray.size()];
            index=0;



        }

        @Override
        protected Void doInBackground(String... params) {
            URL url = null;
            String response="";
            try {
                url = new URL(getActivity().getString(R.string.host) + "/cus.checkOrder.waterp.php");

                HttpURLConnection conn;
                Uri.Builder builder;
                String query;
                OutputStream os;
                BufferedWriter writer;
                int responseCode;
                Product product;


                for (Product p : ProductsArray) {
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(1000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    builder = new Uri.Builder()
                            .appendQueryParameter("productid", String.valueOf(p.getProductid()))
                            .appendQueryParameter("username", "subho040995@gmail.com")
                            .appendQueryParameter("postalcode", String.valueOf(p.getPostalcode()))
                            .appendQueryParameter("productprice", String.valueOf(p.getPrice()));
                    if(fixedQuantity)
                            builder.appendQueryParameter("quantity", String.valueOf(p.getQuantity()));
                    else
                        builder.appendQueryParameter("quantity", "1");

                    builder.appendQueryParameter("offerdescription", p.getOfferDescription());

                    query = builder.build().getEncodedQuery();


                    os = conn.getOutputStream();
                    writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(query);
                    writer.flush();
                    writer.close();
                    os.close();
                    response="";
                    responseCode = conn.getResponseCode();
                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        while ((line = br.readLine()) != null) {
                            response += line;
                        }
                    } else {

                        response = "sdsgsdgsg";

                    }
                    conn.disconnect();
                    publishProgress(response);
                }










                    // index++;
                } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            } catch (ProtocolException e1) {
                e1.printStackTrace();
            } catch (SocketTimeoutException e){
                e.printStackTrace();
            }catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }








        /*    String[] result=new String[ProductsArray.size()];
            String response="";
            URL url = null;

            try {
                url = new URL(getActivity().getString(R.string.host)+"/cus.checkOrder.waterp.php");

                HttpURLConnection conn;
                Uri.Builder builder;
                String query;
                OutputStream os;
                BufferedWriter writer;
                int responseCode;
                Product product;
                for(int i=0;i<ProductsArray.size();i++){

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(1000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);


                    product=ProductsArray.get(i);
                    builder = new Uri.Builder()
                            .appendQueryParameter("productid", String.valueOf(product.getProductid()))
                            .appendQueryParameter("username", "subho040995@gmail.com")
                            .appendQueryParameter("postalcode", String.valueOf(product.getPostalcode()))
                            .appendQueryParameter("productprice", String.valueOf(product.getPrice()))
                            .appendQueryParameter("quantity","1")
                            .appendQueryParameter("offerdescription", product.getOfferDescription());

                    query = builder.build().getEncodedQuery();


                    os = conn.getOutputStream();
                    writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(query);
                    writer.flush();
                    writer.close();
                    os.close();

                    responseCode=conn.getResponseCode();
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
                    result[i]=response;

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

            return result;*/
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
         //   name[index]=values[0];
            s[index]=values[0];
            index++;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);




            Boolean flag=false;
            JSONObject jbj=null;
            Product product=null;
            int noofProducts=s.length;
            ImagesArray=new ArrayList<String>();
            String[] brand=new String[noofProducts];

            String[] name=new String[noofProducts];
            Integer[] volume=new Integer[noofProducts];
            String[] offerDescription=new String[noofProducts];
         //   final String itemDistName= jbj.getString("companyname");
            Double[] amount=new Double[noofProducts];
            price=new Double[noofProducts];
            Integer[] extraQuantity=new Integer[noofProducts];
            Integer[] quantity=new Integer[noofProducts];
            Integer[] id=new Integer[noofProducts];
            purchaseQuantity=new Integer[noofProducts];

            try {
                for(int i=0;i<noofProducts;i++){

                    jbj=new JSONObject(s[i]);
              //      Toast.makeText(getActivity(),jbj.toString(),Toast.LENGTH_SHORT).show();

                  product=ProductsArray.get(i);
                    brand[i]=product.getBrand();
                    name[i]=product.getProductName();

                    volume[i]=product.getVolume();


                 //   ImagesArray.add(BitmapFactory.decodeByteArray(product.getPicture(),0,product.getPicture().length));
                 //   some.setImageBitmap(ImagesArray.get(0));
                    ImagesArray.add(product.getImagelink());


                   if(!s[i].equals("") && jbj.getString("success").equals("true") && jbj.getString("errorCode").equals("null")){
                       flag=true;
                   //    Toast.makeText(getActivity(),"Amount : "+jbj.getString("price"),Toast.LENGTH_SHORT).show();
                       if(!(product.getOfferDescription().equals(jbj.getString("offerdescription")))){
                            product.setOfferDescription(jbj.getString("offerdescription"));

                        }
                        if(!(product.getPrice()==jbj.getDouble("price"))){
                            product.setPrice(jbj.getDouble("price"));
                        }

                        amount[i]=product.getPrice();
                       if(!fixedQuantity)
                            product.setQuantity(jbj.getInt("leftquantity"));
                        quantity[i]=product.getQuantity();


                        if(product.getOfferDescription().equals("null")){
                            product.setOfferDescription("null");
                            price[i]=product.getPrice();
                            extraQuantity[i]=0;
                        }
                        else if(product.getOfferDescription().contains("-")){
                            price[i]=new Utility().getOffer(product.getPrice(),product.getOfferDescription());
                            extraQuantity[i]=0;
                        }
                        else if(product.getOfferDescription().contains("+")){
                            price[i]=product.getPrice();
                            if(!fixedQuantity)
                                extraQuantity[i]= new Utility().getOffer(1,product.getOfferDescription());
                            else
                                extraQuantity[i]= new Utility().getOffer(product.getQuantity(),product.getOfferDescription());
                        }
                        if(!fixedQuantity)
                            purchaseQuantity[i]=1+extraQuantity[i];
                        else
                            purchaseQuantity[i]= product.getQuantity()+extraQuantity[i];
                       offerDescription[i]=product.getOfferDescription();


                    }
                    else{
                       flag=false;
                        Toast.makeText(getActivity(),"ERROR RETRIVING DATA",Toast.LENGTH_SHORT).show();
                       break;
                    }
                }

         //       Toast.makeText(getActivity(),"Amount : "+ProductsArray.get(0).getPrice()+" - "+product.getPrice(),Toast.LENGTH_SHORT).show();
                if(flag) {
                    if (!fixedQuantity)
                        updateCost(0, price[0], 1);
                    else
                        updateCost(0, price[0], purchaseQuantity[0]);
                    ProductBillAdapter productBillAdapter = new ProductBillAdapter(productBill, getActivity(), ImagesArray, name, brand, offerDescription, volume, quantity, amount, price, extraQuantity, fixedQuantity);
                    // productBillAdapter.notifyDataSetChanged();
                    productlist.setAdapter(productBillAdapter);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialog.dismiss();
          //  Toast.makeText(getActivity(),s,Toast.LENGTH_SHORT).show();
        }
    }







    public void updateCost(int position,double finalprice,int purchaseQuantity){
        double totalprice=0;
        this.price[position]=finalprice;
        this.purchaseQuantity[position]=purchaseQuantity;
        for(int i=0;i<ProductsArray.size();i++){
            totalprice+=price[i];
        }

        netprice.setText(String.format("INR %.02f",totalprice));
    }









    class checkOrder extends AsyncTask<String,Void,String[]>{
        Integer[] purchaseQuantity;
        public checkOrder(Integer[] purchaseQuantity){
            this.purchaseQuantity=purchaseQuantity;
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
        protected String[] doInBackground(String... params) {
            String[] result=new String[ProductsArray.size()];
            String response="";
            URL url = null;

            try {
                url = new URL(getActivity().getString(R.string.host)+"/cus.checkOrder.waterp.php");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(1000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                Uri.Builder builder;
                String query;
                OutputStream os;
                BufferedWriter writer;
                int responseCode;
                Product product;
                for(int i=0;i<ProductsArray.size();i++){
                    product=ProductsArray.get(i);
                    builder = new Uri.Builder()
                            .appendQueryParameter("productid", String.valueOf(product.getProductid()))
                            .appendQueryParameter("username", "subho040995@gmail.com")
                            .appendQueryParameter("postalcode", String.valueOf(product.getPostalcode()))
                            .appendQueryParameter("productprice", String.valueOf(product.getPrice()))
                            .appendQueryParameter("quantity",String.valueOf(purchaseQuantity[i]))
                            .appendQueryParameter("offerdescription", product.getOfferDescription());

                    query = builder.build().getEncodedQuery();


                    os = conn.getOutputStream();
                    writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(query);
                    writer.flush();
                    writer.close();
                    os.close();

                    responseCode=conn.getResponseCode();
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
                    result[i]=response;

                }





            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e){
                e.printStackTrace();
            }
            catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }



        @Override
        protected void onPostExecute(String[] s) {
            super.onPostExecute(s);
            JSONObject jbj=null;
            Product product;
            int noofProducts=s.length;
          //  ImagesArray=new ArrayList<String>();
            Boolean success=true;



            String[] offerDescription=new String[noofProducts];
            //   final String itemDistName= jbj.getString("companyname");
            Double[] amount=new Double[noofProducts];
            price=new Double[noofProducts];
            Integer[] extraQuantity=new Integer[noofProducts];
            Integer[] quantity=new Integer[noofProducts];
            Integer[] id=new Integer[noofProducts];

            try {
                for(int i=0;i<s.length;i++){
                    jbj=new JSONObject(s[i]);
                    product=ProductsArray.get(i);

                    if(!s[i].equals("") && jbj.getString("success").equals("true") && jbj.getString("errorCode").equals("null")) {

                        if (!(product.getOfferDescription().equals(jbj.getString("offerdescription")))) {
                            product.setOfferDescription(jbj.getString("offerdescription"));

                        }
                        if (!(product.getPrice() == jbj.getDouble("price"))) {
                            product.setPrice(jbj.getDouble("price"));
                        }

                        amount[i] = product.getPrice();
                        if (!(jbj.getInt("leftquantity") >= purchaseQuantity[i])) {
                            Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
                            success=false;
                            break;
                        } else {
                            product.setQuantity(purchaseQuantity[i]);
                        }
                //cusid,username,phoneno, postalcode, brand, productname, quantity, productprice,offerdescription

                    }
                    else{
                        Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
                        //alert heres
                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialog.dismiss();
            if(!success){
                Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
            }
            else{
                PaymentOptionActivity nextFrag=PaymentOptionActivity.newInstance(ProductsArray,screenWidth);
                getActivity().getFragmentManager().beginTransaction()
                        .replace(R.id.displaycontent, nextFrag)
                        .addToBackStack(null)
                        .commit();

            }

        }
    }


}

