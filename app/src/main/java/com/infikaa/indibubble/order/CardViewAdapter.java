package com.infikaa.indibubble.order;

import android.app.Fragment;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.infikaa.indibubble.R;
import com.infikaa.indibubble.utility.RecyclerAnimationUtil;
import com.infikaa.indibubble.utility.Utility;

/**
 * Created by Sohan on 23-Feb-17.
 */
public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder> {
    private Order order[];
    Fragment fragment;

    int previousPosition = 0;

    public CardViewAdapter(Order order[], Fragment fragment) {
        if (order != null) {
            this.order = new Order[order.length];
            this.order = order.clone();
        } else
            this.order = null;
        this.fragment = fragment;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView pNameVol;
        TextView quantity;
        TextView price;
        TextView offer;
        TextView tprice;
        ImageButton cancel;
        ImageView iv;

        public ViewHolder(final View view) {
            super(view);
            pNameVol = (TextView) view.findViewById(R.id.halt_product_name_volume);
            quantity = (TextView) view.findViewById(R.id.halt_product_quantity);
            price = (TextView) view.findViewById(R.id.halt_product_price);
            offer = (TextView) view.findViewById(R.id.halt_product_offer);
            tprice = (TextView) view.findViewById(R.id.halt_product_total_price);
            cancel = (ImageButton) view.findViewById(R.id.halt_cancel);
            iv = (ImageView) view.findViewById(R.id.order_image);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_card_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.pNameVol.setText(order[position].getProductName());
        holder.quantity.setText("Quantity: " + order[position].getQuantity());
        holder.price.setText("\u20B9" + order[position].getProductPrice());
        holder.offer.setText(new Utility().offerInWords(order[position].getOfferDescription(), false));
        holder.tprice.setText("\u20B9" + order[position].getTotalPrice());
        Glide
                .with(fragment.getActivity())
                .load(fragment.getActivity().getString(R.string.host) + order[position].getImageLink())
                .centerCrop()
                //.placeholder(R.drawable.loading)
                .crossFade()
                .into(holder.iv);

        if (fragment instanceof CompleteFragment) {
            holder.cancel.setVisibility(View.GONE);
        } else {
            holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog dialog = new AlertDialog.Builder(fragment.getView().getContext())
                            .setTitle("Delete entry")
                            .setMessage("Are you sure you want to delete this entry?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //fragment.cancelOrder(mDataset[position][14]);
                                    Toast.makeText(fragment.getView().getContext().getApplicationContext(), "Yes", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(fragment.getView().getContext(), "No", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });
        }


        if (position >= previousPosition) {
            RecyclerAnimationUtil.animate(holder, true);
        } else {
            RecyclerAnimationUtil.animate(holder, false);
        }
        previousPosition = position;

    }

    @Override
    public int getItemCount() {
        if (order != null)
            return order.length;
        else
            return 0;
    }
}