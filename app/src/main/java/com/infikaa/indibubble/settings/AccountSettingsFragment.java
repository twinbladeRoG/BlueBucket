package com.infikaa.indibubble.settings;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.infikaa.indibubble.R;

/**
 * Created by Sohan on 24-Oct-16.
 */
public class AccountSettingsFragment extends Fragment {
    Toolbar toolbar;
    public static AccountSettingsFragment newInstance() {

        Bundle args = new Bundle();

        AccountSettingsFragment fragment = new AccountSettingsFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.fragment_account_settings, container, false);
        return root;
    }

    EditText name,add,ph;
    Button update;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar=(Toolbar)view.findViewById(R.id.toolbar_acc);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            toolbar.setTitle("Address Change");
        }
        name=(EditText)view.findViewById(R.id.name);
        add= (EditText)view.findViewById(R.id.address);
        ph=  (EditText)view.findViewById(R.id.phone);
        update=(Button)view.findViewById(R.id.updateAcc);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
