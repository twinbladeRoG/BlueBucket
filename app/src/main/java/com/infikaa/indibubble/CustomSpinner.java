package com.infikaa.indibubble;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AdapterView;
import android.widget.Spinner;

/**
 * Created by Sudipta Saha on 2/9/2017.
 */

public class CustomSpinner extends Spinner {
    AdapterView.OnItemSelectedListener listener;
    public CustomSpinner(Context context) {
        super(context);
    }
    public CustomSpinner(Context context, AttributeSet attrs)
    { super(context, attrs); }


    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        if (listener != null)
            listener.onItemSelected(this, getChildAt(position), position, 0);

    }
    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener)
    {
        this.listener = listener;
    }


    public void setOnItemSelectedEvenIfUnchangedListener(
            AdapterView.OnItemSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void setSelection(int position, boolean animate) {
        super.setSelection(position, animate);
        if (listener != null)
            listener.onItemSelected(this, getChildAt(position), position, 0);

    }
}
