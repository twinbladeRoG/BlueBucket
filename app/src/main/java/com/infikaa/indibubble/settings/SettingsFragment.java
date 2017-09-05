package com.infikaa.indibubble.settings;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.infikaa.indibubble.R;

public class SettingsFragment extends android.app.Fragment {
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    FragmentManager fragmentManager;


    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_settings, container, false);
        return root;
    }

    ArrayAdapter<String> aa;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar=(Toolbar)view.findViewById(R.id.toolbar_settings);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            toolbar.setTitle("Settings");
        }
        fragmentManager=getActivity().getFragmentManager();
        drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragmentManager.getBackStackEntryCount() > 1){
                    fragmentManager.popBackStack();
                }else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        toggle.syncState();

        String[] menu={"Password", "Account Details"};

        aa = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_text,
                R.id.list_item_textview,
                menu);

        ListView listView = (ListView) view.findViewById(R.id.listview_settings);
        listView.setAdapter(aa);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==0) {
                    Fragment f=PasswordFragment.newInstance();
                    FragmentManager fm = getFragmentManager();
                    fm.beginTransaction().replace(R.id.displaycontent, f, "PasswordFragment").addToBackStack("SettingsActivity").commit();
                }
                else {
                    Fragment f=AccountSettingsFragment.newInstance();
                    FragmentManager fm = getFragmentManager();
                    fm.beginTransaction().replace(R.id.displaycontent, f, "AccountSettingsFragment").addToBackStack("SettingsActivity").commit();
                }
            }
        });
    }
}
