package com.infikaa.indibubble.cart;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
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
import com.infikaa.indibubble.OnFavCartItemDeleteListener;
import com.infikaa.indibubble.product.Product;
import com.infikaa.indibubble.product.ProductBill;
import com.infikaa.indibubble.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavouriteActivty#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavouriteActivty extends Fragment {
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    FragmentManager fragmentManager;
    ArrayList<Product> ProductArray;
    ArrayList<String> ImagesArray;
    ListView favcartitem;
    int screenWidth;
    int temp;
    public FavouriteActivty() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment FavouriteActivty.
     */
    // TODO: Rename and change types and number of parameters
    public static FavouriteActivty newInstance(int screenWidth) {
        FavouriteActivty fragment = new FavouriteActivty();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_favourite_activty, container, false);
        favcartitem=(ListView) view.findViewById(R.id.favcartitemlist);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        toolbar=(Toolbar)view.findViewById(R.id.toolbar_fav);
        toolbar.setTitle("Favourite Cart");
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            toolbar.setTitle("Favourite Cart");
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
    public void onResume() {
        super.onResume();
        ((FragmentToActivity) getActivity()).SetNavState(R.id.nav_favourite);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FavcartDatabaseHelper f = FavcartDatabaseHelper.getInstance(getActivity());
        ProductArray=f.readCart(getActivity());
        f.close();

        if(!ProductArray.isEmpty()) {
            ImagesArray = new ArrayList<String>();
            int size = ProductArray.size();
            String[] name = new String[size];
            String[] brand = new String[size];
            final Double[] price = new Double[size];
            final Integer[] quantity=new Integer[size];
            final String[] productid = new String[size];
            final String[] offerDescription = new String[size];
            Integer[] id = new Integer[size];
            Product p;
            for (int i = 0; i < size; i++) {

                p = ProductArray.get(i);

                ImagesArray.add(p.getImagelink());

                name[i]=p.getProductName();
                brand[i]=p.getBrand();
                price[i]=p.getPrice();
                productid[i]=p.getProductid();
                quantity[i]=p.getQuantity();
                offerDescription[i]=p.getOfferDescription();
                id[i]=i;
            }
            FavCartItemAdapter adapter=new FavCartItemAdapter(getActivity(), ImagesArray, brand, name, price, quantity, id, productid, offerDescription, new OnFavCartItemDeleteListener() {
                @Override
                public void onDelete(int id) {
                    temp=id;
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Proceed?");
                    builder.setMessage("Are you sure you want to remove this item ?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FavcartDatabaseHelper f = FavcartDatabaseHelper.getInstance(getActivity());
                            if(f.deleteItemByProductid(productid[temp],quantity[temp])){
                                f.close();
                                Fragment frg = getFragmentManager().findFragmentByTag("FavouriteActivity");
                                if(frg != null && frg instanceof FavouriteActivty) {

                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.detach(frg);
                                    ft.attach(frg);
                                    ft.commit();
                                }
                            }
                            else{
                                Toast.makeText(getActivity(),"hoeni",Toast.LENGTH_SHORT).show();
                            }



//Toast.makeText(getActivity(),"delete item : "+temp,Toast.LENGTH_SHORT).show();

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

            favcartitem.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            favcartitem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ArrayList<Product> TempProductArray=new ArrayList<Product>();
                    TempProductArray.add(ProductArray.get(position));
                    ProductBill productBill=ProductBill.newInstance(TempProductArray,screenWidth,true);
                    getActivity().getFragmentManager().beginTransaction()
                            .replace(R.id.displaycontent, productBill,"ProductBill")
                            .addToBackStack("ProductBill")
                            .commit();

                Toast.makeText(getActivity(),"po : "+position,Toast.LENGTH_SHORT).show();
                }
            });





        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Empty Favourite");
            builder.setMessage("You have an favourite list.");
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
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
