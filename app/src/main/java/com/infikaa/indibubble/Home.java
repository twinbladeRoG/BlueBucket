package com.infikaa.indibubble;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.infikaa.indibubble.cart.CardViewCustomItemAdapter;

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
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;


public class Home extends Fragment {
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    FragmentManager fragmentManager;
    EditText postalcodeid;
    ImageButton postalcodebuttonid;
    ListView listView;
    ProgressDialog progressDialog;
    String offerDescription = new String();
    String username;
    String postalcode;
    int screenWidth;
    Boolean loaded = false;
    String[] itemBrand;
    int noofProducts;
    String[] itemName;
    Integer[] itemVolume;
    String itemDistName;
    Double[] itemPrice;
    Integer[] itemQuantity;
    String[] imageLink;
    Integer[] id;
    String[] itemDescription;
    String[] productId;
    Custom_ItemList itemList;
    View view;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static Home newInstance(int screenWidth) {
        Bundle args = new Bundle();
        Home home = new Home();
        args.putInt("screenWidth", screenWidth);
        home.setArguments(args);
        return home;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            screenWidth = getArguments().getInt("screenWidth");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //  ((IssueBackButton) getActivity()).issueBackButton(true);
        ((FragmentToActivity) getActivity()).SetNavState(R.id.nav_home);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home, container, false);

            postalcodeid = (EditText) view.findViewById(R.id.postalcodeid);
            postalcodebuttonid = (ImageButton) view.findViewById(R.id.postalcodebuttonid);
            //listView=(ListView) view.findViewById((R.id.itemlist));
      /*  getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(view.getWindowToken(), 0);
*/

            toolbar = (Toolbar) view.findViewById(R.id.toolbar_home);
            if (toolbar != null) {
                ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
                toolbar.setTitle(R.string.app_name);
            }
            fragmentManager = getActivity().getFragmentManager();
            drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
            toggle = new ActionBarDrawerToggle(
                    getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fragmentManager.getBackStackEntryCount() > 1) {
                        fragmentManager.popBackStack();
                    } else {
                        drawer.openDrawer(GravityCompat.START);
                    }
                }
            });
            toggle.syncState();

            mRecyclerView = (RecyclerView) view.findViewById(R.id.item_list);

            mLayoutManager = new LinearLayoutManager(view.getContext());
            mRecyclerView.setLayoutManager(mLayoutManager);

            SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("Bluebucket", MODE_PRIVATE);
            username = pref.getString("emailId", "");
            postalcode = pref.getString("postalcode", "");
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        postalcodeid.setText(postalcode);


        //  postalcode=savedInstanceState.getString("postalcode");
        //   itemList=(Custom_ItemList) savedInstanceState.getSerializable("itemList");
        //  Toast.makeText(getActivity(),"here",Toast.LENGTH_SHORT).show();
        // itemList=new Custom_ItemList(getActivity(),itemPics,itemBrand,itemName,itemPrice,itemDistName,itemVolume,offerDescription,itemQuantity,id,screenWidth);

        //   listView.setAdapter(itemList);
               /* listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        if(isNetworkAvailable()) {
                            Product product = new Product();
                            JSONObject obj = null;
                            try {
                                obj = jbj.getJSONObject(view.getId() + "");





                                product.setBrand(obj.getString("brand"));
                                product.setProductName(obj.getString("productname"));
                                product.setVolume(obj.getInt("volume"));
                                product.setPrice(obj.getDouble("price"));
                                product.setQuantity(obj.getInt("leftquantity"));
                                product.setDescription(obj.getString("description"));
                                product.setOfferDescription(offerDescription);
                                product.setPostalcode(Integer.parseInt(postalcode));
                                product.setDistName(itemDistName);
                                product.setProductid(obj.getString("productid"));

                                product.setDescription(obj.getString("description"));
                                product.setPicture(null);
                                //  Toast.makeText(getActivity(),product.getOfferDescription(),Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //  Toast.makeText(getActivity(),position+"",Toast.LENGTH_SHORT).show();

                            ProductDisplay nextFrag = ProductDisplay.newInstance(product, screenWidth,false);
                            getActivity().getFragmentManager().beginTransaction()
                                    .replace(R.id.displaycontent, nextFrag,"ProductDisplay")
                                    .addToBackStack("ProductDisplay")
                                    .commit();
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

                    }
                });*/


        // new SendPostRequest(screenWidth).execute();

        postalcodebuttonid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!postalcodeid.getText().equals("") && String.valueOf(postalcodeid.getText()).matches("[\\d]{6}")) {

                    postalcode = String.valueOf(postalcodeid.getText());
                    InputMethodManager input = (InputMethodManager) getActivity()
                            .getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    input.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

                    if (isNetworkAvailable()) {


                        new SendPostRequest(screenWidth).execute();
                    } else {
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
            /*    String[] itemBrand={"Aquafina"};
                String[] itemName={"Aquafina Hard Rock"};
                String[] itemVolume={"50ml"};
                String[] itemPrice={"INR 150"};
                String[] itemDistName={"Google Inc"};
                Integer[] id={1};
                                Custom_ItemList itemList=new Custom_ItemList(getActivity(),null,itemBrand,itemName,itemPrice,itemDistName,itemVolume,null,null,id,screenWidth);
                listView.setAdapter(itemList);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(getActivity(),view.getId()+" ",Toast.LENGTH_SHORT).show();

                    }
                });
                */


                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Please ensure you have entered a valid 6 digit postal code.")
                            .setCancelable(false)
                            .setTitle("Invalid Postal Code")
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public class SendPostRequest extends AsyncTask<String, Void, String[]> {
        int screenWidth;

        SendPostRequest(int screenWidth) {
            this.screenWidth = screenWidth;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading..");
            progressDialog.setTitle("Checking Network");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String[] doInBackground(String... params) {
            String response = "";
            String[] result = new String[3];
            try {
                URL url = new URL(getActivity().getString(R.string.host) + "/cus.getProducts.waterp.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(1000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("postalcode", postalcode);
                String query = builder.build().getEncodedQuery();


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                } else {
                    response = "";

                }


                result[0] = response;
                url = new URL(getActivity().getString(R.string.host) + "/cus.getOffers.waterp.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(1000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                builder = new Uri.Builder()
                        .appendQueryParameter("postalcode", postalcode)
                        .appendQueryParameter("username", username);
                query = builder.build().getEncodedQuery();

                os = conn.getOutputStream();
                writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                response = "";
                responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                } else {
                    response = "";

                }
                result[1] = response;

             /* url = new URL(getActivity().getString(R.string.host)+"/cus.getProductImages.waterp.php");
              conn = (HttpURLConnection) url.openConnection();
              conn.setReadTimeout(15000);
              conn.setConnectTimeout(1000);
              conn.setRequestMethod("POST");
              conn.setDoInput(true);
              conn.setDoOutput(true);
              builder = new Uri.Builder()
                      .appendQueryParameter("productname", "sample")
                      .appendQueryParameter("count", "1");
              query = builder.build().getEncodedQuery();

              os = conn.getOutputStream();
              writer = new BufferedWriter(
                      new OutputStreamWriter(os, "UTF-8"));
              writer.write(query);
              writer.flush();
              writer.close();
              os.close();
              response="";
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

            result[2]=response;
*/
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String[] s) {
            final JSONObject jbj;


            try {


                jbj = new JSONObject(s[0]);
                JSONObject obj;


                if (!s[0].equals("") && jbj.getString("success").equals("true") && jbj.getString("errorCode").equals("null")) {
                    noofProducts = Integer.parseInt(jbj.getString("noofProducts"));
                    //   Toast.makeText(getActivity(),s[0],Toast.LENGTH_SHORT).show();
                    itemBrand = new String[noofProducts];

                    itemName = new String[noofProducts];
                    itemVolume = new Integer[noofProducts];
                    itemDistName = jbj.getString("companyname");
                    itemPrice = new Double[noofProducts];
                    itemQuantity = new Integer[noofProducts];
                    imageLink = new String[noofProducts];
                    id = new Integer[noofProducts];
                    itemDescription = new String[noofProducts];
                    productId = new String[noofProducts];

                    JSONObject dbj = new JSONObject(s[1]);
                    if (!s[1].equals("") && dbj.getString("success").equals("true") && dbj.getString("errorCode").equals("null")) {


                        if (dbj.getString("noofOffers").equals("0")) {
                            offerDescription = "null";
                        } else {
                            obj = new JSONObject(dbj.getString(dbj.getString("noofOffers")));
                            offerDescription = obj.getString("type") + "/" + obj.getString("scope") + "/" + obj.getString("offer");
                        }

                    }


                    byte[] decodedString;
                    Bitmap decodedByte = null;
/*                  dbj=new JSONObject(s[2]);
                  if (!s[2].equals("") && dbj.getString("success").equals("true") && dbj.getString("errorCode").equals("null")) {
                      decodedString = Base64.decode(dbj.getString("1"), Base64.DEFAULT);
                      decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                  }
                  Bitmap[] itemPics=new Bitmap[noofProducts];
*/
                    for (int i = 0; i < noofProducts; i++) {
                        obj = new JSONObject(jbj.getString(String.valueOf(i + 1)));
                        // itemPics[i]=decodedByte;
                        itemBrand[i] = obj.getString("brand");
                        itemName[i] = obj.getString("productname");
                        itemPrice[i] = obj.getDouble("price");
                        itemVolume[i] = obj.getInt("volume");
                        imageLink[i] = obj.getString("imagelink");
                        Toast.makeText(getActivity(), imageLink[i] + " j", Toast.LENGTH_SHORT).show();
                        itemQuantity[i] = obj.getInt("leftquantity");
                        id[i] = (i + 1);
                        itemDescription[i] = obj.getString("description");
                        productId[i] = obj.getString("productid");
                    }

                    mRecyclerView = (RecyclerView) view.findViewById(R.id.item_list);

                    mLayoutManager = new LinearLayoutManager(view.getContext());
                    mRecyclerView.setLayoutManager(mLayoutManager);

                    mAdapter = new CardViewCustomItemAdapter(imageLink, itemBrand, itemName, itemPrice, itemDistName,
                            itemVolume, offerDescription, itemDescription, itemQuantity, id, postalcode, productId, Home.this);
                    mRecyclerView.setAdapter(mAdapter);

                  /*itemList=new Custom_ItemList(getActivity(),imageLink,itemBrand,itemName,itemPrice,itemDistName,itemVolume,offerDescription,itemQuantity,id,screenWidth);

                  listView.setAdapter(itemList);
                  listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                      @Override
                      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                          if(isNetworkAvailable()) {
                              Product product = new Product();
                              JSONObject obj = null;
                              try {
                                  obj = jbj.getJSONObject(view.getId() + "");





                                  product.setBrand(obj.getString("brand"));
                                  product.setProductName(obj.getString("productname"));
                                  product.setVolume(obj.getInt("volume"));
                                  product.setPrice(obj.getDouble("price"));
                                  product.setQuantity(obj.getInt("leftquantity"));
                                  product.setDescription(obj.getString("description"));
                                  product.setOfferDescription(offerDescription);
                                  product.setPostalcode(Integer.parseInt(postalcode));
                                  product.setDistName(itemDistName);
                                  product.setProductid(obj.getString("productid"));

                                  product.setDescription(obj.getString("description"));
                                  product.setImagelink(null);
                                  //  Toast.makeText(getActivity(),product.getOfferDescription(),Toast.LENGTH_SHORT).show();
                              } catch (JSONException e) {
                                  e.printStackTrace();
                              }
                              //  Toast.makeText(getActivity(),position+"",Toast.LENGTH_SHORT).show();

                              ProductDisplay nextFrag = ProductDisplay.newInstance(product, screenWidth,false);
                              getActivity().getFragmentManager().beginTransaction()
                                      .replace(R.id.displaycontent, nextFrag,"ProductDisplay")
                                      .addToBackStack("ProductDisplay")
                                      .commit();
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

                      }
                  });*/

                } else if (!s[0].equals("") && jbj.getString("success").equals("false") && jbj.getString("errorCode").equals("5")) {
                    Toast.makeText(getActivity(), "Mysql query error", Toast.LENGTH_SHORT).show();
                } else if (!s[0].equals("") && jbj.getString("success").equals("false") && jbj.getString("errorCode").equals("4")) {
                    Toast.makeText(getActivity(), "No service in this area", Toast.LENGTH_SHORT).show();
                } else if (!s[0].equals("") && jbj.getString("success").equals("false") && jbj.getString("errorCode").equals("3")) {
                    Toast.makeText(getActivity(), "Failed Database connectivity", Toast.LENGTH_SHORT).show();
                } else if (!s[0].equals("") && jbj.getString("success").equals("false") && jbj.getString("errorCode").equals("1")) {
                    Toast.makeText(getActivity(), "Invalid data", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Undefined error", Toast.LENGTH_SHORT).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialog.dismiss();


        }

    }


}
