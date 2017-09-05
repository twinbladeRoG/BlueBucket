package com.infikaa.indibubble.cart;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.infikaa.indibubble.OnFavCartItemDeleteListener;
import com.infikaa.indibubble.R;
import com.infikaa.indibubble.utility.Utility;

import java.util.ArrayList;

/**
 * Created by Sudipta Saha on 2/26/2017.
 */

public class FavCartItemAdapter extends ArrayAdapter {
    private OnFavCartItemDeleteListener listener;
    private ArrayList<String> ImagesArray;
    private Activity context;
    private String[] brand;
    private String[] name;
    private Double[] price;
    private Integer[] id;
    private Integer[] quantity;
    private String[] offerDescription;
    public FavCartItemAdapter(Activity context, ArrayList<String> ImagesArray,String[] brand,String[] name, Double[] price,Integer[] quantity,Integer[] id,String[] productid, String[] offerDescription,OnFavCartItemDeleteListener listener){
        super(context, R.layout.custom_favcartproductlist,name);
        this.context=context;
        this.ImagesArray=ImagesArray;
        this.name=name;
        this.brand=brand;
        this.price=price;
        this.quantity=quantity;
        this.id=id;
        this.offerDescription=offerDescription;
        this.listener=listener;

    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_favcartproductlist, null, true);
        ImageView imageView=(ImageView) view.findViewById(R.id.favcartproductpic);
        TextView brand=(TextView) view.findViewById(R.id.favcartproductbrand);
        TextView name=(TextView) view.findViewById(R.id.favcartproductname);
        TextView price=(TextView) view.findViewById(R.id.favcartproductprice);
        TextView quantity=(TextView) view.findViewById(R.id.favcartproductquantity);
        TextView offer=(TextView) view.findViewById(R.id.offer_favcart);
        ImageButton removeproduct=(ImageButton) view.findViewById(R.id.favcartproductremovebut);
      //  view.setLayoutParams(new ListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)(screenWidth*.4)));
       // imageView.setImageBitmap(ImagesArray.get(position));
        Glide
                .with(context)
                .load(context.getString(R.string.host)+this.ImagesArray.get(position))
                .centerCrop()
        //        .placeholder(R.drawable.loading)
                .crossFade()
                .into(imageView);

        if(!offerDescription[position].equals("null"))
            offer.setText("Offer : "+new Utility().offerInWords(offerDescription[position],false));
        else
            offer.setText("");

        brand.setText("By "+this.brand[position]);
        name.setText(""+this.name[position]);
        price.setText("INR "+this.price[position]);
        quantity.setText("Quantity "+this.quantity[position]);
        removeproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null)
                    listener.onDelete(position);
            }
        });
        removeproduct.setFocusable(false);
        view.setId(position);
        return view;
    }
}
