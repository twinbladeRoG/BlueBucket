package com.infikaa.indibubble;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.infikaa.indibubble.utility.Utility;

/**
 * Created by Sudipta Saha on 2/5/2017.
 */

public class Custom_ItemList extends ArrayAdapter<String>  {
    private Activity context;
    private String[] itemBrand;
    private String[] itemName;
    private Double[] itemPrice;
    private Integer[] itemVolume;
    private String itemDistName;
    private String offerDescription;
    private String[] imagelink;
    private Integer[] itemQuantity;
    private Integer[] id;
    private Integer screenWidth;

    public Custom_ItemList(Activity context,String[] imagelink, String[] itemBrand, String[] itemName, Double[] itemPrice, String itemDistName, Integer[] itemVolume, String offerDescription, Integer[] itemQuantity, Integer[] id,int screenWidth) {
        super(context, R.layout.custom_itemlist, itemName);
        this.context = context;
        this.itemBrand = itemBrand;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemDistName = itemDistName;
        this.itemVolume = itemVolume;
        this.imagelink=imagelink;
        this.offerDescription=offerDescription;
        this.itemQuantity = itemQuantity;
        this.id = id;
        this.screenWidth=screenWidth;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.custom_itemlist, null, true);
       /* DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        double dpHeight = displayMetrics.heightPixels /.displayMetrics.density;
        dpHeight=(1/3)*dpHeight;
        int hei= (int) dpHeight;
        */
       // int screenHeight = ((WindowManager)context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
      //  listViewItem.setLayoutParams(new ListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)(screenHeight*0.25)));

        ImageView itemPic = (ImageView) listViewItem.findViewById(R.id.itemPic);
        TextView itemBrand = (TextView) listViewItem.findViewById(R.id.itembrand);
        TextView itemName = (TextView) listViewItem.findViewById(R.id.itemname);
        TextView itemVolume = (TextView) listViewItem.findViewById(R.id.itemvolume);
        TextView itemPrice = (TextView) listViewItem.findViewById(R.id.itemprice);
        TextView itemDistName = (TextView) listViewItem.findViewById(R.id.itemdistname);
       // int dp=Math.round(listViewItem.getWidth() / (displaymetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
      //  int dp = (int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, listViewItem.getWidth(), displaymetrics );


       listViewItem.setLayoutParams(new ListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)(screenWidth*.4)));
       // itemPic.setImageBitmap(this.itemPic[position]);

        Glide
                .with(context)
                .load(context.getString(R.string.host)+imagelink[position])
                .centerCrop()
                //.placeholder(R.drawable.loading)
                .crossFade()
                .into(itemPic);

        itemBrand.setText(this.itemBrand[position]);
        itemName.setText(this.itemName[position]);
        if(this.itemVolume[position]>=1000)
            itemVolume.setText("Volume : "+Double.toString(this.itemVolume[position]/1000)+" L");
        else
            itemVolume.setText("Volume : "+Integer.toString(this.itemVolume[position])+" ml");
        itemPrice.setText("INR "+String.format("%.2f",this.itemPrice[position])+" "+new Utility().offerInWords(offerDescription,false));



            itemDistName.setText("by "+this.itemDistName);
        listViewItem.setId(this.id[position]);
        return  listViewItem;
    }

}

