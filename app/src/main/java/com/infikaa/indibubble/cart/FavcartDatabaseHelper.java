package com.infikaa.indibubble.cart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.infikaa.indibubble.product.Product;

import java.util.ArrayList;

/**
 * Created by Sudipta Saha on 2/26/2017.
 */

public class FavcartDatabaseHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "Bluebucket.sqlite";
    private static final String DATABASE_CARTTABLE = "favcart";
    private static final int DATABASE_VERSION = 2;

    private static FavcartDatabaseHelper mInstance = null;

    public static FavcartDatabaseHelper getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            Toast.makeText(ctx,"Not null",Toast.LENGTH_SHORT).show();
            mInstance = new FavcartDatabaseHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    private FavcartDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createCart(sqLiteDatabase);
    }

    private void createCart(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("create table "+DATABASE_CARTTABLE+"( id integer primary key, picture text, brand text, name text, volume text, price text, offerdescription text, quantity text, postalcode text, description text,  distName text,  productid text, unique(productid,quantity) )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+DATABASE_CARTTABLE+";");
        onCreate(sqLiteDatabase);


    }
    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+DATABASE_CARTTABLE+";");
        onCreate(sqLiteDatabase);

    }

    public boolean writeCart(Product p) {
        boolean success=false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v=new ContentValues();

      //  db.execSQL("insert or replace into "+DATABASE_CARTTABLE+" (picture, brand, TypeID, Level, Seen) values ((select ID from Book where Name = \"SearchName\"), \"SearchName\", ...);");



        try {
            v.put("picture",p.getImagelink());


            v.put("brand",p.getBrand());
            v.put("name",p.getProductName());
            v.put("volume",p.getVolume());
            v.put("price",p.getPrice());
            v.put("offerdescription",p.getOfferDescription());
            v.put("quantity",p.getQuantity());
            v.put("postalcode",p.getPostalcode());
            v.put("description",p.getDescription());
            v.put("distName",p.getDistName());
            v.put("productid",p.getProductid());
            //sqLiteDatabase=this.getWritableDatabase();
            success= db.insert(DATABASE_CARTTABLE,null,v) >0;
        }
        finally {


            v.clear();
            db.close();
            this.close();
        }
        return success;
    }

    public ArrayList<Product> readCart(Context context){

        SQLiteDatabase db=this.getReadableDatabase();
        Cursor rs = db.rawQuery("select * from "+DATABASE_CARTTABLE, null);
        ArrayList<Product> ProductArray=new ArrayList<Product>();
        Product product;


        try {
            if (rs.moveToFirst()) {

                while (!rs.isAfterLast()) {

                    product = new Product();
                    product.setImagelink(rs.getString(1));

                    product.setBrand(rs.getString(2));
                    product.setProductName(rs.getString(3));
                    product.setVolume(rs.getInt(4));
                    product.setPrice(rs.getDouble(5));
                    product.setOfferDescription(rs.getString(6));
                    product.setQuantity(rs.getInt(7));
                    product.setPostalcode(rs.getInt(8));
                    product.setDescription(rs.getString(9));
                    product.setDistName(rs.getString(10));

                    product.setProductid(rs.getString(11));
                    ProductArray.add(product);
                    //  arrayList.add(" id : "+rs.getString(0)+" itemName : " + rs.getString(1) + " itemQuan : " + rs.getString(2) + " itemVolume : " + rs.getString(3) + " itemPrice : " + rs.getString(4)+ " totalPrice : " + rs.getString(5) + " distName : " + rs.getString(6) + " itemOrderType : " + rs.getString(7) + " itemOffer : " + rs.getString(8));
                    rs.moveToNext();
                }
            }

        }
        finally {
            rs.close();
            db.close();
            this.close();
        }


        return ProductArray;

    }

    public boolean deleteItemByProductid(String productid,int quantity){
        SQLiteDatabase db = this.getWritableDatabase();
        boolean success;
        success = db.delete(DATABASE_CARTTABLE,"productid = '"+productid+"' AND quantity='"+quantity+"'",null)>0;
        db.close();
        this.close();
        return success;
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }


}
