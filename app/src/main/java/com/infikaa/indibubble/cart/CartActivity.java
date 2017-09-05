package com.infikaa.indibubble.cart;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.infikaa.indibubble.FragmentToActivity;
import com.infikaa.indibubble.Home;
import com.infikaa.indibubble.OnCartItemDeleteListener;
import com.infikaa.indibubble.R;
import com.infikaa.indibubble.product.Product;
import com.infikaa.indibubble.product.ProductBill;
import com.infikaa.indibubble.product.ProductDisplay;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartActivity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartActivity extends Fragment {
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    FragmentManager fragmentManager;

    ArrayList<Product> ProductArray;
    ArrayList<String> ImagesArray;
    ListView cartitem;
    int screenWidth;
    int temp;
    FloatingActionButton buyall;

    // TODO: Rename and change types of parameters


    public CartActivity() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CartActivity.
     */
    // TODO: Rename and change types and number of parameters
    public static CartActivity newInstance(int screenWidth) {
        CartActivity fragment = new CartActivity();
        Bundle args = new Bundle();
        args.putInt("screenWidth",screenWidth);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.screenWidth=getArguments().getInt("screenWidth");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    //    ((IssueBackButton) getActivity()).issueBackButton(true);
        ((FragmentToActivity) getActivity()).SetNavState(R.id.nav_cart);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view= inflater.inflate(R.layout.fragment_cart, container, false);
        cartitem=(ListView)view.findViewById(R.id.cartitemlist);
        buyall=(FloatingActionButton) view.findViewById(R.id.cartbuyall);
        return view;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        toolbar=(Toolbar)view.findViewById(R.id.toolbar_cart);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            toolbar.setTitle("Your Orders");
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
        CartDatabaseHelper s = CartDatabaseHelper.getInstance(getActivity());
        ProductArray=s.readCart(getActivity());
        s.close();


        if(!ProductArray.isEmpty()) {
            ImagesArray=new ArrayList<String>();
           // Toast.makeText(getActivity(), String.valueOf(ProductArray.size()), Toast.LENGTH_SHORT).show();
        int size=ProductArray.size();
        String[] name=new String[size];
            String[] brand=new String[size];
            final Double[] price=new Double[size];
            String[] offerDescription=new String[size];
            final String[] productid=new String[size];
           Integer[] id=new Integer[size];
            Product p;
            for(int i=0;i<size;i++){

                p=ProductArray.get(i);
              //  Toast.makeText(getActivity(),p.getPicture().toString(),Toast.LENGTH_SHORT).show();
            //    ImagesArray.add(BitmapFactory.decodeStream( new ByteArrayInputStream(p.getPicture())));

                ImagesArray.add(p.getImagelink());

                name[i]=p.getProductName();
                brand[i]=p.getBrand();
                price[i]=p.getPrice();
                productid[i]=p.getProductid();
                id[i]=i;
                offerDescription[i]=p.getOfferDescription();
            }

            CartItemAdapter cartItemAdapter=new CartItemAdapter(getActivity(), ImagesArray, brand, name, price, id, productid, offerDescription,  new OnCartItemDeleteListener() {
                @Override
                public void onDelete(int id) {
                    temp=id;
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Proceed?");
                    builder.setMessage("Are you sure you want to remove this item ?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CartDatabaseHelper s = CartDatabaseHelper.getInstance(getActivity());
                            if(s.deleteItemByProductid(productid[temp])){
                                s.close();
                                Fragment frg = getFragmentManager().findFragmentByTag("CartActivity");
                                if(frg != null && frg instanceof CartActivity) {

                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.detach(frg);
                                    ft.attach(frg);
                                    ft.commit();
                                }
                            }
                            else{
                                Toast.makeText(getActivity(),"hoeni",Toast.LENGTH_SHORT).show();
                            }





                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.setCancelable(false);
                    builder.show();
                }
            });
            cartitem.setAdapter(cartItemAdapter);
            cartItemAdapter.notifyDataSetChanged();
            cartitem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                    if(isNetworkAvailable()) {
                        Product product = ProductArray.get(position);

                        product.setImagelink(null);
                        ProductDisplay nextFrag = ProductDisplay.newInstance(product,true);
                        getActivity().getFragmentManager().beginTransaction()
                                .replace(R.id.displaycontent, nextFrag)
                                .addToBackStack(null)
                                .commit();

                    }
                    else{
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



            buyall.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Boolean flag=true;
                    int indexpostalcode=ProductArray.get(0).getPostalcode();
                    for(int i=1;i<ProductArray.size();i++){
                        if(ProductArray.get(i).getPostalcode()!=indexpostalcode){
                            flag=false;
                            buyall.setEnabled(false);
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Make sure all your products have the same delivery pincode.")
                                    .setCancelable(false)
                                    .setTitle("Pincode not matching.")
                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                            break;

                        }
                        else if(ProductArray.get(i).getOfferDescription().contains("individual")){
                            flag=false;
                            buyall.setEnabled(false);
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Individual offer is available only on one product.")
                                    .setCancelable(false)
                                    .setTitle("Offer Limitations.")
                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                            break;
                        }
                    }
                    if(flag==true){
                        ProductBill nextFrag = ProductBill.newInstance(ProductArray, screenWidth,false);
                        getActivity().getFragmentManager().beginTransaction()
                                .replace(R.id.displaycontent, nextFrag,"ProductBill")
                                .addToBackStack("ProductBill")
                                .commit();

                    }

                }
            });




        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Empty Cart");
            builder.setMessage("You have an empty cart.");
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getFragmentManager().popBackStack();
                    getFragmentManager().beginTransaction().replace(R.id.displaycontent, Home.newInstance(screenWidth),"Home").addToBackStack("Home").commit();

                    dialog.dismiss();


                }
            });
            builder.setCancelable(false);
            builder.show();

        }
    }


       // Toast.makeText(getActivity(), ProductArray.size(), Toast.LENGTH_SHORT).show();

}
