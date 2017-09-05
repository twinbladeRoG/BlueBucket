package com.infikaa.indibubble.cart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.infikaa.indibubble.R;
import com.infikaa.indibubble.product.Product;
import com.infikaa.indibubble.product.ProductDisplay;
import com.infikaa.indibubble.utility.RecyclerAnimationUtil;
import com.infikaa.indibubble.utility.Utility;

/**
 * Created by Sohan on 14-Apr-17.
 */

public class CardViewCustomItemAdapter extends RecyclerView.Adapter<CardViewCustomItemAdapter.ViewHolder> {

    int prevPos=0;
    private String[] itemBrand;
    private String[] itemName;
    private Double[] itemPrice;
    private Integer[] itemVolume;
    private String itemDistName;
    private String offerDescription;
    private String[] imagelink;
    private Integer[] itemQuantity;
    private Integer[] id;
    private String[] itemDescription;
    private String postalcode;
    private String[] productId;
    private android.app.Fragment fragment;
    public CardViewCustomItemAdapter(String[] imagelink, String[] itemBrand, String[] itemName,
                                     Double[] itemPrice, String itemDistName, Integer[] itemVolume,
                                     String offerDescription, String[] itemDescription, Integer[] itemQuantity, Integer[] id,
                                     String postalcode, String[] productId, android.app.Fragment fragment) {
        this.fragment = fragment;
        this.itemBrand = itemBrand;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemDistName = itemDistName;
        this.itemVolume = itemVolume;
        this.imagelink=imagelink;
        this.offerDescription=offerDescription;
        this.itemDescription=itemDescription;
        this.itemQuantity = itemQuantity;
        this.id = id;
        this.postalcode = postalcode;
        this.productId = productId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_itemlist, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Glide
                .with(fragment.getActivity())
                .load(fragment.getActivity().getString(R.string.host)+imagelink[position])
                .centerCrop()
                //.placeholder(R.drawable.loading)
                .crossFade()
                .into(holder.itemPic);
        holder.itemBrand.setText(this.itemBrand[position]);
        holder.itemName.setText(this.itemName[position]);
        if(itemVolume[position]>=1000)
            holder.itemVolume.setText("Volume : "+Double.toString(itemVolume[position]/1000)+" L");
        else
            holder.itemVolume.setText("Volume : "+Integer.toString(this.itemVolume[position])+" ml");
        holder.itemPrice.setText("INR "+String.format("%.2f",this.itemPrice[position])+" "+new Utility().offerInWords(offerDescription,false));
        holder.itemDistName.setText("by "+this.itemDistName);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utility.isNetworkAvailable(fragment.getActivity())) {
                    Product product = new Product();
                        product.setBrand(itemBrand[position]);
                        product.setProductName(itemName[position]);
                        product.setVolume(itemVolume[position]);
                        product.setPrice(itemPrice[position]);
                        product.setQuantity(itemQuantity[position]);
                        product.setDescription(itemDescription[position]);
                        product.setOfferDescription(offerDescription);
                        product.setPostalcode(Integer.parseInt(postalcode));
                        product.setDistName(itemDistName);
                        product.setProductid(productId[position]);
                        product.setImagelink(null);
                    //  Toast.makeText(getActivity(),position+"",Toast.LENGTH_SHORT).show();

                    ProductDisplay nextFrag = ProductDisplay.newInstance(product,false);
                    fragment.getFragmentManager().beginTransaction()
                            .replace(R.id.displaycontent, nextFrag,"ProductDisplay")
                            .addToBackStack("ProductDisplay")
                            .commit();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getActivity());
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
        if (position >= prevPos) {
            RecyclerAnimationUtil.animate(holder, true);
        }
        else {
            RecyclerAnimationUtil.animate(holder, false);
        }
        prevPos=position;
    }

    @Override
    public int getItemCount() {
        if (id != null)
            return id.length;
        else
            return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView itemPic;
        TextView itemBrand;
        TextView itemName;
        TextView itemVolume;
        TextView itemPrice;
        TextView itemDistName;
        CardView cardView;

        public ViewHolder(final View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.product_item_card);
            itemPic = (ImageView) view.findViewById(R.id.itemPic);
            itemBrand = (TextView) view.findViewById(R.id.itembrand);
            itemName = (TextView) view.findViewById(R.id.itemname);
            itemVolume = (TextView) view.findViewById(R.id.itemvolume);
            itemPrice = (TextView) view.findViewById(R.id.itemprice);
            itemDistName = (TextView) view.findViewById(R.id.itemdistname);
        }
    }
}
