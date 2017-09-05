package com.infikaa.indibubble.product;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.infikaa.indibubble.CustomSpinner;
import com.infikaa.indibubble.R;
import com.infikaa.indibubble.utility.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Created by Sudipta Saha on 2/8/2017.
 */

public class ProductBillAdapter extends ArrayAdapter<String> implements AdapterView.OnItemSelectedListener, DialogInterface.OnClickListener{
    ProductBill productBill;
     Activity context;
     ArrayList<String> image;
    String[] name;
     String[] brand;
     public String[] offerDescription;
     Integer[] volume;
     Integer[] quantity;
     Double[] amount;
     Double[] finalprice;
     Integer[] extraQuantity;
    Integer[]selector;
    Integer[]finalquantity;
    int viewPosition;
    int r;
    TextView modiamount;
    TextView modiprice;
    TextView modiquantity;
    CustomSpinner customSpinner;
    Boolean fixedQuantity;
    public ProductBillAdapter(ProductBill productBill,Activity context,ArrayList<String> image,String[] name,String[] brand,String[] offerDescription,Integer[] volume,Integer[] quantity,Double[] amount,Double[] finalprice,Integer[] extraQuantity,Boolean fixedQuantity){
        super(context, R.layout.custom_buyproductlist,name);
        this.productBill=productBill;
        this.image=image;
        this.context=context;
        this.name=name;
        this.brand=brand;
        this.offerDescription=offerDescription;
        this.volume=volume;
        this.quantity=quantity;
        this.amount=amount;
        this.finalprice=finalprice;
        this.extraQuantity=extraQuantity;
        this.fixedQuantity=fixedQuantity;
        selector=new Integer[brand.length];
        Arrays.fill(selector,0);
        finalquantity=new Integer[brand.length];
        if(!fixedQuantity) {
            for (int i = 0; i < quantity.length; i++) {
                finalquantity[i] = 1 + extraQuantity[i];
            }
        }
        else{
            for (int i = 0; i < quantity.length; i++) {
                finalquantity[i] = quantity[i] + extraQuantity[i];
            }
        }

    }


    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.custom_buyproductlist, null, true);
        int i;
        ImageView image=(ImageView) listViewItem.findViewById(R.id.buyproductpic);

        TextView brand = (TextView) listViewItem.findViewById(R.id.buyproductbrand);

        TextView name = (TextView) listViewItem.findViewById(R.id.buyproductname);

        TextView offerDescription = (TextView) listViewItem.findViewById(R.id.buyproductoffer);

        TextView volume = (TextView) listViewItem.findViewById(R.id.buyproductvolume);

        TextView amount = (TextView) listViewItem.findViewById(R.id.buyproductamount);

        TextView price = (TextView) listViewItem.findViewById(R.id.buyproductfinalprice);

        TextView finalquantity = (TextView) listViewItem.findViewById(R.id.buyproductfinalquantity);

        CheckBox favproduct=(CheckBox) listViewItem.findViewById(R.id.productfavcheck);

        final CustomSpinner buyproductquantity=(CustomSpinner) listViewItem.findViewById(R.id.buyproductquantity);
        if(!fixedQuantity) {
            favproduct.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // FavcartDatabaseHelper f=FavcartDatabaseHelper.getInstance(context);

                    Boolean success = productBill.updateFavProduct(position, isChecked);
                    if (success == false)
                        buttonView.setChecked(false);
                    Toast.makeText(context, "Return : " + success, Toast.LENGTH_SHORT).show();


                    // Toast.makeText(context,"situation : "+position,Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
            favproduct.setVisibility(View.GONE);

      //  image.setImageBitmap(this.image.get(position));
        Glide
                .with(context)
                .load(context.getString(R.string.host)+this.image.get(position))
                .centerCrop()
                .crossFade()
                .into(image);

        brand.setText(this.brand[position]);
        name.setText(this.name[position]);
        if(this.volume[position]>=1000)
            volume.setText("Volume : "+this.volume[position]/1000+" L");
        else
            volume.setText("Volume : "+this.volume[position]+" mL");

        offerDescription.setText(new Utility().offerInWords(this.offerDescription[position],false));
        ArrayList<String> options=new ArrayList<String>();
        if(!fixedQuantity) {

            if (this.quantity[position] >= 5) {
                for (i = 1; i <= 5; i++) {
                    options.add(i + "");
                }
                options.add("more");
            } else {
                for (i = 1; i <= this.quantity[position]; i++) {
                    options.add(i + "");
                }

            }
        }
        else {
            options.add(String.valueOf(this.quantity[position]));

        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,options);

        buyproductquantity.setAdapter(adapter);
      //  adapter.notifyDataSetChanged();
        buyproductquantity.setSelection(selector[position], false);

        buyproductquantity.setTag(position);

        if(!fixedQuantity) {
            buyproductquantity.setOnItemSelectedEvenIfUnchangedListener(this);
            price.setText(String.format("%.02f",this.finalprice[position]));
        }
            else {
            Double finalPrice=0.0;
            buyproductquantity.setEnabled(false);
            if (this.offerDescription[position].equals("")) {
                finalPrice=this.amount[position] * this.quantity[position];
                price.setText(String.format("INR %.02f",finalPrice ));
                finalquantity.setText(String.valueOf(quantity[position]));
            } else if (this.offerDescription[viewPosition].contains("-")) {
                finalPrice=new Utility().getOffer((double) (this.amount[position] * this.quantity[position]), this.offerDescription[position]);
                price.setText(String.format("INR %.02f", finalPrice));
                finalquantity.setText(String.valueOf(quantity[position]));
            } else if (this.offerDescription[viewPosition].contains("+")) {
                finalPrice=this.amount[position] * this.quantity[position];
                price.setText(String.format("INR %.02f", finalPrice));

                finalquantity.setText(String.valueOf(this.quantity[position] + new Utility().getOffer(this.quantity[position], this.offerDescription[position])));

            }



        productBill.updateCost(position,finalPrice,this.quantity[position]);



        }

        amount.setText(String.format("%.02f",this.amount[position]));
     /*   if(!fixedQuantity)
            i=this.extraQuantity[position]+1;
        else
            i=this.extraQuantity[position]+quantity[position];
*/
        finalquantity.setText(String.valueOf(this.finalquantity[position]));
        listViewItem.setId(position);

        return listViewItem;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
      //  parent.setSelection(position,false);
        viewPosition=(int) parent.getTag();
        int quantity=0;

        double finalprice=0;
        modiamount=(TextView) parent.getRootView().findViewById(R.id.buyproductamount);
        modiprice=(TextView) parent.getRootView().findViewById(R.id.buyproductfinalprice);
        modiquantity=(TextView) parent.getRootView().findViewById(R.id.buyproductfinalquantity);
      if(!parent.getItemAtPosition(position).equals("more")) {
          if (this.offerDescription[viewPosition].equals("")) {
              finalprice = this.amount[(int) parent.getTag()] * Integer.parseInt(String.valueOf(parent.getItemAtPosition(position)));
              modiprice.setText(String.format("INR %.02f", finalprice));
              modiquantity.setText(String.valueOf(parent.getItemAtPosition(position)));
              finalquantity[viewPosition]=Integer.parseInt(String.valueOf(parent.getItemAtPosition(position)));
          } else if (this.offerDescription[viewPosition].contains("-")) {
              finalprice = new Utility().getOffer((double) (this.amount[(int) parent.getTag()] * Integer.parseInt(String.valueOf(parent.getItemAtPosition(position)))), this.offerDescription[viewPosition]);
              modiprice.setText(String.format("INR %.02f", finalprice));
              modiquantity.setText(String.valueOf(parent.getItemAtPosition(position)));
              finalquantity[viewPosition]=Integer.parseInt(String.valueOf(parent.getItemAtPosition(position)));
          } else if (this.offerDescription[viewPosition].contains("+")) {
              finalprice = this.amount[(int) parent.getTag()] * Integer.parseInt(String.valueOf(parent.getItemAtPosition(position)));
              modiprice.setText(String.format("INR %.02f", finalprice));
              quantity = Integer.parseInt(String.valueOf(parent.getItemAtPosition(position))) + new Utility().getOffer(Integer.parseInt(String.valueOf(parent.getItemAtPosition(position))), this.offerDescription[viewPosition]);
              modiquantity.setText(String.valueOf(quantity));
              finalquantity[viewPosition]=quantity;
          }
          productBill.updateCost(viewPosition,finalprice,Integer.parseInt(String.valueOf(parent.getItemAtPosition(position))));
      }
      else if(parent.getItemAtPosition(position).equals("more")){
            customSpinner=(CustomSpinner) parent;
          LayoutInflater li = LayoutInflater.from(context);
       //   View promptsView = li.inflate(R.layout.custom_prompt_dialog, null);
        EditText ed=new EditText(context);

         r=View.generateViewId();
            ed.setId(r);
          AlertDialog.Builder builder = new AlertDialog.Builder(context);
          builder.setView(ed)
                  .setCancelable(false)
                  .setTitle("Bulk Order")
                  .setMessage("Enter quantity greater than 5")
                  .setPositiveButton("Okay", this)
                  .setNegativeButton("Cancel", this);
          AlertDialog alert = builder.create();
          alert.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE  | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
          alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
          alert.show();

      }
       // notifyDataSetChanged();
        selector[viewPosition]=position;

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        return;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        double finalprice=0;
        int quantity=0;

        EditText ed = (EditText) ((AlertDialog) dialog).findViewById(r);
        switch (which){
            case DialogInterface.BUTTON_POSITIVE :
                if(ed.getText().toString().trim().length()==0 || !(Pattern.compile("^\\d+$").matcher(String.valueOf(ed.getText())).matches()) || Integer.parseInt(String.valueOf(ed.getText())) <=5 ) {
                    customSpinner.setSelection(0);

                }

                else if(Integer.parseInt(String.valueOf(ed.getText()))<=this.quantity[viewPosition]) {
                    int value=Integer.parseInt(String.valueOf(ed.getText()));
                    if (this.offerDescription[viewPosition].equals("")) {
                        finalprice = this.amount[(int) viewPosition] * value;

                        modiprice.setText(String.format("INR %.02f", finalprice));

                        modiquantity.setText(String.valueOf(value));
                        finalquantity[viewPosition]=value;
                    } else if (this.offerDescription[viewPosition].contains("-")) {
                        finalprice = new Utility().getOffer((double) (this.amount[(int) viewPosition] *value), this.offerDescription[viewPosition]);
                        modiprice.setText(String.format("INR %.02f", finalprice));
                        modiquantity.setText(String.valueOf(value));
                        finalquantity[viewPosition]=value;
                    }

                    else if (this.offerDescription[viewPosition].contains("+")) {
                        finalprice = this.amount[(int) viewPosition] * value;
                        modiprice.setText(String.format("INR %.02f", finalprice));
                        quantity = value + new Utility().getOffer(value, this.offerDescription[viewPosition]);
                        modiquantity.setText(String.valueOf(quantity));
                        finalquantity[viewPosition]=quantity;
                    }
                    productBill.updateCost(viewPosition,finalprice,this.quantity[viewPosition]);

                }

                else if(Integer.parseInt(String.valueOf(ed.getText()))>this.quantity[viewPosition]){
                    if (this.offerDescription[viewPosition].equals("")) {
                        finalprice = this.amount[(int) viewPosition] * this.quantity[viewPosition];

                        modiprice.setText(String.format("INR %.02f", finalprice));

                        modiquantity.setText(String.valueOf(this.quantity[viewPosition]));
                        finalquantity[viewPosition]=this.quantity[viewPosition];
                    } else if (this.offerDescription[viewPosition].contains("-")) {
                        finalprice = new Utility().getOffer((double) (this.amount[(int) viewPosition] *this.quantity[viewPosition]), this.offerDescription[viewPosition]);
                        modiprice.setText(String.format("INR %.02f", finalprice));
                        modiquantity.setText(String.valueOf(this.quantity[viewPosition]));
                        finalquantity[viewPosition]=this.quantity[viewPosition];
                    }

                    else if (this.offerDescription[viewPosition].contains("+")) {
                        finalprice = this.amount[(int) viewPosition] * this.quantity[viewPosition];
                        modiprice.setText(String.format("INR %.02f", finalprice));
                        quantity = this.quantity[viewPosition] + new Utility().getOffer(this.quantity[viewPosition], this.offerDescription[viewPosition]);
                        modiquantity.setText(String.valueOf(quantity));
                        finalquantity[viewPosition]=quantity;
                    }
                    productBill.updateCost(viewPosition,finalprice,this.quantity[viewPosition]);





                }
              dialog.dismiss();
                break;
            case DialogInterface.BUTTON_NEGATIVE :
                customSpinner.setSelection(4);
                dialog.dismiss();
                break;
        }
    }
}

