package com.infikaa.indibubble.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.floor;

/**
 * Created by Sudipta Saha on 2/6/2017.
 */

public class Utility {
    public String offerInWords(String offerDescription,Boolean flag){

        if(offerDescription==null ||offerDescription.equals("null") || offerDescription.equals("") || offerDescription.isEmpty()){
            return "";
        }
        else {


            StringBuffer offer=new StringBuffer("");
            String[] seg = offerDescription.split("/");
            if(flag==true){
                if(seg[0].contains("individual"))
                    offer.append("Specially for you, ");
                else
                    offer.append("Offer ");
            }

            else
                offer.append("Offer : ");

            if(seg[2].contains("-") && seg[2].contains("%")){
                offer.append(seg[2].substring(1)+ " discount");
            }
            else if(seg[2].contains("-")){
                offer.append("INR "+seg[2].substring(1)+ " off");
            }
            else if(seg[2].contains("+")){
                Pattern pattern;
                Matcher buy,get;
                pattern = Pattern.compile("\\d+$");
                get = pattern.matcher(seg[2]);

                pattern = Pattern.compile("^\\d+");
                buy = pattern.matcher(seg[2]);
                if(get.find() && buy.find())
                    offer.append("Buy "+buy.group(0)+" get "+get.group(0)+" free");

                else
                    offer.append("-");
            }
            return offer.toString();
        }



    }

    public Double getOffer(double price, String offerDescription){
        String[] seg = offerDescription.split("/");
        Pattern pattern;
        Matcher get;

        if(seg[2].contains("-") && seg[2].contains("%")){

            pattern = Pattern.compile("\\d+[.]{0,1}[\\d]*");
            get = pattern.matcher(seg[2]);
            if(get.find())
                price=price*(1-(Double.parseDouble(get.group(0))/100));

        }
        else if(seg[2].contains("-")){
            pattern = Pattern.compile("\\d+[.]{0,1}[\\d]*");
            get = pattern.matcher(seg[2]);
            if(get.find())
                price=price-Double.parseDouble(get.group(0));
        }
        return price;
    }

    public int getOffer(int quantity, String offerDescription){
        String[] seg = offerDescription.split("/");
        Pattern pattern;
        Matcher get,buy;
        pattern = Pattern.compile("\\d+$");
        get = pattern.matcher(seg[2]);
        pattern = Pattern.compile("^\\d+");
        buy = pattern.matcher(seg[2]);
        int oget;
        int obuy,extraquantity;
        if(get.find() && buy.find()){
            oget=Integer.parseInt(get.group(0));
            obuy=Integer.parseInt(buy.group(0));
            extraquantity=(int)(oget*(floor(quantity/obuy)));
        }
        else{
            extraquantity=0;
        }


        return extraquantity;
    }

    public boolean checkPostalcode(String address, int postalcode){
        if(address.equals(""))
            return false;
        String[] seg=address.split(",");
        if(seg[seg.length-1].trim().contains(String.valueOf(postalcode))){
            return true;
        }
        return false;
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
