package com.infikaa.indibubble.product;

import android.app.Activity;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.infikaa.indibubble.R;

import java.util.ArrayList;

/**
 * Created by Sudipta Saha on 2/6/2017.
 */

public class ProductGalleryAdapter extends PagerAdapter {
    private ArrayList<String> ImagesArray;
    private LayoutInflater inflater;
    private Activity context;

    public ProductGalleryAdapter(Activity context,ArrayList<String> ImagesArray) {
        this.context = context;
        this.ImagesArray=ImagesArray;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return ImagesArray.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.product_gallery, view, false);

        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout
                .findViewById(R.id.productimages);

        Glide
                .with(context)
                .load(context.getString(R.string.host)+ImagesArray.get(position))
                .centerCrop()
                .crossFade()
                .into(imageView);

    //    imageView.setImageBitmap(IMAGES.get(position));

        view.addView(imageLayout, 0);

        return imageLayout;
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
