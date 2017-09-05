package com.infikaa.indibubble.cart;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.infikaa.indibubble.OnCartItemDeleteListener;
import com.infikaa.indibubble.R;
import com.infikaa.indibubble.utility.Utility;

import java.util.ArrayList;

/**
 * Created by Sudipta Saha on 2/10/2017.
 */

public class CartItemAdapter extends ArrayAdapter<String> {
    private OnCartItemDeleteListener listener;
    private ArrayList<String> ImagesArray;
    private Activity context;
    private String[] brand;
    private String[] name;
    private Double[] price;
    private Integer[] id;
    private String[] offerDescription;
    public CartItemAdapter(Activity context, ArrayList<String> ImagesArray,String[] brand,String[] name, Double[] price,Integer[] id,String[] productid, String[] offerDescription, OnCartItemDeleteListener listener){
        super(context, R.layout.custom_cartproductlist,name);
        this.context=context;
        this.ImagesArray=ImagesArray;
        this.name=name;
        this.brand=brand;
        this.price=price;
        this.id=id;

        this.offerDescription=offerDescription;
        this.listener=listener;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_cartproductlist, null, true);

        ImageView imageView=(ImageView) view.findViewById(R.id.cartproductpic);
        TextView brand=(TextView) view.findViewById(R.id.cartproductbrand);
        TextView name=(TextView) view.findViewById(R.id.cartproductname);
        TextView price=(TextView) view.findViewById(R.id.cartproductprice);
        TextView offer=(TextView) view.findViewById(R.id.offer_cart);

        ImageButton removeproduct=(ImageButton) view.findViewById(R.id.cartproductremovebut);
       // view.setLayoutParams(new ListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)(screenWidth*.4)));
       // imageView.setImageBitmap(ImagesArray.get(position));
        Glide
                .with(context)
                .load(context.getString(R.string.host)+this.ImagesArray.get(position))
                .centerCrop()

                .crossFade()
                .into(imageView);
        brand.setText("By "+this.brand[position]);
        name.setText(""+this.name[position]);
        price.setText("INR "+this.price[position]);
        if(!offerDescription[position].equals("null"))
            offer.setText("Offer : "+new Utility().offerInWords(offerDescription[position],false));
        else
            offer.setText("");
        removeproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(listener!=null){
                    listener.onDelete(position);
                }


            }
        });
        removeproduct.setFocusable(false);


        view.setId(position);

        return view;
    }


}
