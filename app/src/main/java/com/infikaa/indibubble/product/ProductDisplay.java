package com.infikaa.indibubble.product;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.infikaa.indibubble.FragmentToActivity;
import com.infikaa.indibubble.R;
import com.infikaa.indibubble.cart.CartDatabaseHelper;
import com.infikaa.indibubble.utility.Utility;
import com.viewpagerindicator.CirclePageIndicator;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductDisplay#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductDisplay extends Fragment {
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FragmentManager fragmentManager;
    private static Product product;
    private static ViewPager productpicgallery;
    private static CirclePageIndicator indicator;
    private static LinearLayout linearLayout;
    private static int screenWidth;
    private static boolean isCartItem;
    Button buynow;
    Button addtocart;
    ProgressDialog progressDialog;
    TextView brand, name, description, volume, price, offer, offerprice, productready, distname;
    View view;
    private ArrayList<String> ImagesArray;
    private ArrayList<Product> ProductsArray;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    Comment[] comment;
    CardView rateView;

    RatingBar ratingBar;
    EditText commentText;
    Button commentSubmit;

    String emailId, cus_name;
    DateTime currentTime;

    public static ProductDisplay newInstance(Product product, boolean isCartItem) {
        Bundle args = new Bundle();
        ProductDisplay fragment = new ProductDisplay();
        args.putSerializable("product", product);
        args.putBoolean("isCartItem", isCartItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product = (Product) getArguments().getSerializable("product");
            screenWidth = getArguments().getInt("screenWidth");
            isCartItem = getArguments().getBoolean("isCartItem");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        //   ((IssueBackButton) getActivity()).issueBackButton(false);
        ((FragmentToActivity) getActivity()).SetNavState(R.id.nav_home);
        //  ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_display_product, container, false);
            productpicgallery = (ViewPager) view.findViewById(R.id.productpicgallery);
            indicator = (CirclePageIndicator) view.findViewById(R.id.productpicindicator);
            linearLayout = (LinearLayout) view.findViewById(R.id.productdisplay);
            //linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 350));
            buynow = (Button) view.findViewById(R.id.productdisplaybuy);
            addtocart = (Button) view.findViewById(R.id.productdisplayaddtocart);
            brand = (TextView) view.findViewById(R.id.productbrand);
            name = (TextView) view.findViewById(R.id.productname);
            description = (TextView) view.findViewById(R.id.productdescription);
            volume = (TextView) view.findViewById(R.id.productvolume);
            price = (TextView) view.findViewById(R.id.productprice);
            offer = (TextView) view.findViewById(R.id.productofferdescription);
            offerprice = (TextView) view.findViewById(R.id.productpricewithoffer);
            productready = (TextView) view.findViewById(R.id.productready);
            distname = (TextView) view.findViewById(R.id.productdistname);
            rateView = (CardView) view.findViewById(R.id.user_comment);
            ratingBar = (RatingBar) view.findViewById(R.id.user_rating_bar);
            commentSubmit = (Button) view.findViewById(R.id.user_comment_submit_button);
            commentText = (EditText) view.findViewById(R.id.user_comment_text);
            toolbar = (Toolbar) view.findViewById(R.id.product_tootbar);
        }

        return view;

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        toolbar = (Toolbar) view.findViewById(R.id.product_tootbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.product_collapsing_toolbar);

        fragmentManager=getActivity().getFragmentManager();
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

        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("Bluebucket", MODE_PRIVATE);
        emailId = pref.getString("emailId", "");
        cus_name = pref.getString("username", "");

        currentTime=new DateTime();
        final DateTimeFormatter dateTimeFormatter = DateTimeFormat
                .forPattern("yyyy-MM-dd HH:MM:SS");


        commentSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetComment setComment = new SetComment();
                setComment.execute(
                        product.getProductid(),
                        emailId,
                        cus_name,
                        Float.toString(ratingBar.getRating()),
                        commentText.getText().toString(),
                        dateTimeFormatter.print(currentTime)
                        );
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rating_recycler);
        mLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        comment=null;
        mAdapter = new CommentViewAdapter(comment, ProductDisplay.this, emailId);
        mRecyclerView.setAdapter(mAdapter);



        GetComments getComments = new GetComments();
        getComments.execute(product.getProductid());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //  Toast.makeText(getActivity(),product.getBrand(),Toast.LENGTH_SHORT).show();


        new onPostRequest().execute();
        collapsingToolbarLayout.setTitle(product.getProductName());
        brand.setText("By " + product.getBrand());
        name.setText(product.getProductName());
        description.setText(product.getDescription());
        distname.setText("Distributed by " + product.getDistName());
        if (product.getVolume() >= 1000)
            volume.setText("Volume : " + String.format("%d", product.getVolume() / 1000) + " L");
        else
            volume.setText("Volume : " + product.getVolume() + " mL");
        price.setText("Price : INR " + String.format("%.02f", product.getPrice()));

        if (product.getOfferDescription().equals("null")) {
            offer.setVisibility(View.GONE);
            offerprice.setVisibility(View.GONE);
        } else {
            if (product.getOfferDescription().contains("+")) {
                offerprice.setVisibility(View.GONE);
            } else {
                offerprice.setText("INR " + String.format("%.2f", new Utility().getOffer(product.getPrice(), product.getOfferDescription())));
                price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

            offer.setText(new Utility().offerInWords(product.getOfferDescription(), true));

        }
        if (product.getQuantity() == 0) {
            productready.setText("Product out of stock!");
            productready.setTextColor(Color.RED);
            buynow.setEnabled(false);
        }
        if (!isCartItem) {
            addtocart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Proceed?");
                    builder.setMessage("Are you sure to put this order in your cart ?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CartDatabaseHelper s = CartDatabaseHelper.getInstance(getActivity());
                            Boolean flag = s.writeCart(product);
                            s.close();
                            if (flag)
                                Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), " You cancelled ", Toast.LENGTH_SHORT).show();

                        }
                    });
                    builder.setCancelable(false);
                    builder.show();
                }
            });
        } else {
            addtocart.setEnabled(false);


        }
        buynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    //  product.setPicture(ImagesArray.get(1));
                 /*   int size = ImagesArray.get(1).getRowBytes() * ImagesArray.get(1).getHeight();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(size);
                    ImagesArray.get(1).copyPixelsToBuffer(byteBuffer);
                    product.setPicture(byteBuffer.array());*/
                    ProductsArray = new ArrayList<Product>();
                    ProductsArray.add(product);


                    ProductBill nextFrag = ProductBill.newInstance(ProductsArray, screenWidth, false);

                    getActivity().getFragmentManager().beginTransaction()
                            .replace(R.id.displaycontent, nextFrag, "ProductBill")
                            .addToBackStack("ProductBill")
                            .commit();

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
            }
        });


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    class onPostRequest extends AsyncTask<String, Void, String> {
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
            String response = "";
            URL url = null;
            try {
                url = new URL(getActivity().getString(R.string.host) + "/cus.getProductImages.waterp.php");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(1000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("productid", product.getProductid());
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


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
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

            try {
                JSONObject jbj = new JSONObject(s);
                byte[] decodedString;
                Bitmap decodedByte = null;
                ImagesArray = new ArrayList<String>();
                if (!s.equals("") && jbj.getString("success").equals("true") && jbj.getString("errorCode").equals("null")) {
                    int noofImages = jbj.getInt("noofImages");
                    // ImagesArray = new String[noofImages];

                    Toast.makeText(getActivity(), noofImages + "", Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < noofImages; i++) {
                        ImagesArray.add(jbj.getString(String.valueOf(i + 1)));
                    }
                } else {
                    ImagesArray.add("/productimages/fail/1.jpg");

                }

                product.setImagelink(ImagesArray.get(0));
               /* Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                imageView.setImageBitmap(bmp);

                product.setPicture(stream.toByteArray());
*/
                productpicgallery.setAdapter(new ProductGalleryAdapter(getActivity(), ImagesArray));
                DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
                //  int dp=Math.round(linearLayout.getWidth() / (displaymetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));


                indicator.setViewPager(productpicgallery);
                indicator.setRadius(3 * displaymetrics.density);


            } catch (JSONException e) {
                e.printStackTrace();
            }


            progressDialog.dismiss();

        }
    }


    class updateCartItem extends AsyncTask<String, Void, String> {
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
            String response = "";
            URL url = null;
            try {
                url = new URL(getActivity().getString(R.string.host) + "/cus.checkOrder.waterp.php");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(1000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("productid", String.valueOf(product.getProductid()))
                        .appendQueryParameter("username", "subho040995@gmail.com")
                        .appendQueryParameter("postalcode", String.valueOf(product.getPostalcode()))
                        .appendQueryParameter("productprice", String.valueOf(product.getPrice()))
                        .appendQueryParameter("quantity", "1")
                        .appendQueryParameter("offerdescription", product.getOfferDescription());

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


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
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
            JSONObject jbj = null;
            //  ImagesArray = new ArrayList<Bitmap>();
            try {
                jbj = new JSONObject(s);
                if (!s.equals("") && jbj.getString("success").equals("true") && jbj.getString("errorCode").equals("null")) {
                    if (!(product.getOfferDescription().equals(jbj.getString("offerdescription")))) {
                        product.setOfferDescription(jbj.getString("offerdescription"));

                    }
                    if (!(product.getPrice() == jbj.getDouble("price"))) {
                        product.setPrice(jbj.getDouble("price"));
                    }


                } else {
                    Toast.makeText(getActivity(), jbj.getString("errorCode"), Toast.LENGTH_SHORT).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();

        }
    }


    public class GetComments extends AsyncTask<String, Void, Comment[]> {
        public final String LOG_TAG = "Comments";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Comment[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String requestJSONString = "";

            final String BASE_URL = getActivity().getString(R.string.host)+"/cus.updateProductReview.waterp.php";
            final String PRODUCT_ID_PARAM = "productid";
            final String OPTION_PARAM = "option";

            try {
                Uri.Builder builtUri = new Uri.Builder()
                        .appendQueryParameter(OPTION_PARAM, "get")
                        .appendQueryParameter(PRODUCT_ID_PARAM, params[0]);

                Log.v(LOG_TAG, "Comment URL: "+builtUri.toString());
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
                Log.v(LOG_TAG, "REQUEST JSON: " + requestJSONString);
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

        public Comment[] getRequestResult(String json) throws JSONException {
            JSONObject pJson = new JSONObject(json);

            final String OWN_SUCCESS = "success";
            final String OWN_NOCOMMENTS = "noofReview";
            final String OWN_ERROR = "errorCode";
            comment=null;
            if (pJson.getString(OWN_SUCCESS).equals("true")) {
                int n = Integer.parseInt(pJson.getString(OWN_NOCOMMENTS));
                comment=new Comment[n];
                if (n>0) {
                     comment = new Comment[n];
                    Log.v(LOG_TAG, "Order Array: " + comment.length+", N: "+ n);
                    for (int i=0; i<n ; i++) {
                        JSONObject list = new JSONObject(pJson.getString(String.valueOf(i+1)));

                        comment[i]=new Comment();
                        comment[i].setUsername(list.getString("name"));
                        comment[i].setRating(Float.parseFloat(list.getString("rating")));
                        comment[i].setComment(list.getString("comment"));
                        comment[i].setDate(list.getString("time"));
                        comment[i].setEmail(list.getString("username"));
                    }
                }
                else {return null;}
            }

            return comment;
        }

        @Override
        protected void onPostExecute(Comment[] comments) {
            if (comments != null && comments.length > 0) {
                rateView.setVisibility(View.VISIBLE);
                for (Comment comment1 : comments) {
                    if (comment1.getEmail().contains(emailId)) {
                        rateView.setVisibility(View.GONE);
                    }
                }
            }
            mAdapter = new CommentViewAdapter(comments, ProductDisplay.this, emailId);
            mRecyclerView.setAdapter(mAdapter);


        }
    }

    public class SetComment extends AsyncTask<String, Void, Void> {

        public final String LOG_TAG = "Comments";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String requestJSONString = "";

            final String BASE_URL = getActivity().getString(R.string.host)+"/cus.updateProductReview.waterp.php";
            final String PRODUCT_ID_PARAM = "productid";
            final String OPTION_PARAM = "option";
            final String USERNAME_PARAM = "username";
            final String NAME_PARAM = "name";
            final String RATING_PARAM = "rating";
            final String COMMENT_PARAM = "comment";
            final String TIME_PARAM = "time";

            try {

                Uri.Builder builtUri = new Uri.Builder()
                        .appendQueryParameter(OPTION_PARAM, "set")
                        .appendQueryParameter(PRODUCT_ID_PARAM, params[0])
                        .appendQueryParameter(USERNAME_PARAM, params[1])
                        .appendQueryParameter(NAME_PARAM, params[2])
                        .appendQueryParameter(RATING_PARAM, params[3])
                        .appendQueryParameter(COMMENT_PARAM, params[4])
                        .appendQueryParameter(TIME_PARAM, params[5]);

                Log.v(LOG_TAG, "Comment URL: "+builtUri.toString());
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
                Log.v(LOG_TAG, "REQUEST JSON: " + requestJSONString);
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
                getRequestResult(requestJSONString);
            }
            catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        String success, error;
        public void getRequestResult(String json) throws JSONException {
            JSONObject pJson = new JSONObject(json);

            final String OWN_SUCCESS = "success";
            final String OWN_ERROR = "errorCode";
            success=pJson.getString(OWN_SUCCESS);
            error=pJson.getString(OWN_ERROR);

        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (success.equals("true")) {
                Toast.makeText(getContext(), "Sumbitted", Toast.LENGTH_SHORT).show();
                GetComments getComments = new GetComments();
                getComments.execute(product.getProductid());
            }

            else
                Toast.makeText(getContext(), "Failed "+success+" "+error, Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteComment(String username) {
        DeleteComment deleteComment = new DeleteComment();
        deleteComment.execute(product.getProductid(), username);
    }

    public class DeleteComment extends AsyncTask<String, Void, Void> {

        public final String LOG_TAG = "Comments";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String requestJSONString = "";

            final String BASE_URL = getActivity().getString(R.string.host)+"/cus.updateProductReview.waterp.php";
            final String PRODUCT_ID_PARAM = "productid";
            final String OPTION_PARAM = "option";
            final String USERNAME_PARAM = "username";

            try {

                Uri.Builder builtUri = new Uri.Builder()
                        .appendQueryParameter(OPTION_PARAM, "remove")
                        .appendQueryParameter(PRODUCT_ID_PARAM, params[0])
                        .appendQueryParameter(USERNAME_PARAM, params[1]);

                Log.v(LOG_TAG, "Comment URL: "+builtUri.toString());
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
                Log.v(LOG_TAG, "REQUEST JSON: " + requestJSONString);
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
                getRequestResult(requestJSONString);
            }
            catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        String success, error;
        public void getRequestResult(String json) throws JSONException {
            JSONObject pJson = new JSONObject(json);

            final String OWN_SUCCESS = "success";
            final String OWN_ERROR = "errorCode";
            success=pJson.getString(OWN_SUCCESS);
            error=pJson.getString(OWN_ERROR);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (success.equals("true")) {
                Toast.makeText(getContext(),"Deleted",Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getContext(),"Failed "+error,Toast.LENGTH_SHORT).show();

            GetComments getComments = new GetComments();
            getComments.execute(product.getProductid());
        }
    }

}
