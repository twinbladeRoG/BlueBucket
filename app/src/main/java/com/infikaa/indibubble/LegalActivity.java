package com.infikaa.indibubble;


import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;


public class LegalActivity extends Fragment implements CompoundButton.OnCheckedChangeListener{
    TextView productqqtext,productcontext,productdistext,productsertext,productpritext;
    ToggleButton productqqtoggle,productcontoggle,productdistoggle,productsertoggle,productpritoggle;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    FragmentManager fragmentManager;
    public LegalActivity() {
        // Required empty public constructor
    }
    public static LegalActivity newInstance() {
        LegalActivity fragment = new LegalActivity();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_legal, container, false);
        productqqtext=(TextView) view.findViewById(R.id.productqqtext);
        productqqtoggle=(ToggleButton) view.findViewById(R.id.productqqtoggle);

        productcontext=(TextView) view.findViewById(R.id.productcontext);
        productcontoggle=(ToggleButton) view.findViewById(R.id.productcontoggle);

        productdistext=(TextView) view.findViewById(R.id.productdistext);
        productdistoggle=(ToggleButton) view.findViewById(R.id.productdistoggle);

        productsertext=(TextView) view.findViewById(R.id.productsertext);
        productsertoggle=(ToggleButton) view.findViewById(R.id.productsertoggle);

        productpritext=(TextView) view.findViewById(R.id.productpritext);
        productpritoggle=(ToggleButton) view.findViewById(R.id.productpritoggle);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        toolbar=(Toolbar)view.findViewById(R.id.toolbar_legal);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            toolbar.setTitle("Legal");
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
    }

    @Override
    public void onResume() {
        super.onResume();
        //  ((IssueBackButton) getActivity()).issueBackButton(true);
        ((FragmentToActivity) getActivity()).SetNavState(R.id.nav_legal);

    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        productqqtoggle.setChecked(true);
        productcontoggle.setChecked(true);
        productdistoggle.setChecked(true);
        productsertoggle.setChecked(true);
        productpritoggle.setChecked(true);
        //true-plus / false - minus
      //  productqqtoggle.setBackgroundResource(R.drawable.plusicon);
        productqqtext.setText("product quality and quantity\nThird party i.e dealer is all responsible for the quality and quantity of product. As the order is confirmed you will be directed to your specific dealer, that dealer will solely be responsible for both quality and quantity of product.In any case,the XYZ app is NOT responsible for the water quality and quantity.");
        productcontext.setText("Consent \n" +
                "i.>While you provide your personal information either to complete the sign up process or to complete a transaction,its client's responsibility to verify the credentials, place an order,arrange for a delievery or return/purchage, we completly imply that your consent to our collecting it and using it for that specific reason(provided above) only.\n" +
                "ii.>If you have been asked your personal information for a secondary reason,like marketing and publisicing,we will ask you directly for your expressed consent,or provide you with an oppurtunity to say no.\n" +
                "In all other cases user will be sole responsible for his/her act.");
        productdistext.setText("Disclosure\n" +
                "i.> We may disclose your personal information if we are required by law to do so or if you violate our Terms of Service\n" +
                "ii.> We may disclose your feedback if we're required to do so.");

        productsertext.setText("SECURITY\n" +
                "i.> we'll take reasonable steps and precautions to protect yours personal information and privacy.In case your information is lost,misused,disclosed then we're not responsible for that\n" +
                "ii.> While XYZ uses reasonable efforts to include accurate and up-to-date information on the app,XYZ makes no warranties or representations as to the accuracy, correctness, reliability or otherwise with respect to such information and assumes no liability or responsibility for any omission or error (including without limitation typographical errors, virus and technical errors) in the content of the app.Though due care has been taken to make the database completely reliable and error-free, XYZ claims exemption from any liability arising out of any such error in the database\n" +
                "iii.> In the event that XYZ, from time to time, allows for discussions, chats, postings, transmissions, bulletin board and the like on the app, XYZ is under no obligation to monitor or review such transmitted information and assumes no responsibility or liability arising from the content of any such location nor for any error, defamation,slander, omission, falsehood, obscenity, pornography, profanity, danger or inaccuracy of any such information. You are prohibited from posting or transmitting any unlawful, threatening, libelous, defamatory, obscene, scandalous, inflammatory, pornographic or profane material or any material that could constitute or encourage conduct that would be considered a criminal offense, give rise to civil liability or otherwise violate any law. XYZ will fully cooperate with any law enforcement authority or court order requesting or directing XYZ to disclose the identity of anyone posting any such information or material.");
        productpritext.setText("We reserve the right to modify this privacy policy at any time, so please review it frequently. Changes and clarifications will take effect immediately upon their posting on the website. If we make material changes to this policy, we will notify you here that it has been updated, so that you are aware of what information we collect, how we use it, and under what circumstances, if any, we use and/or disclose it.\n" +
                "If our store is acquired or merged with another company, your information may be transferred to the new owners so that we may continue to sell products to you.");
        productqqtoggle.setOnCheckedChangeListener(this);
        productcontoggle.setOnCheckedChangeListener(this);
        productdistoggle.setOnCheckedChangeListener(this);
        productsertoggle.setOnCheckedChangeListener(this);
        productpritoggle.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.productqqtoggle:
                if(isChecked){
                    productqqtext.setVisibility(View.GONE);
                    //  productqqtoggle.setBackgroundResource(R.drawable.plusicon);

                }
                else {
                    productqqtext.setVisibility(View.VISIBLE);
                    //  productqqtoggle.setBackgroundResource(R.drawable.minusicon);
                }

                break;
            case R.id.productcontoggle:
                if(isChecked){
                    productcontext.setVisibility(View.GONE);
                    //  productqqtoggle.setBackgroundResource(R.drawable.plusicon);

                }
                else {
                    productcontext.setVisibility(View.VISIBLE);
                    //  productqqtoggle.setBackgroundResource(R.drawable.minusicon);
                }

                break;
            case R.id.productdistoggle:
                if(isChecked){
                    productdistext.setVisibility(View.GONE);
                    //  productqqtoggle.setBackgroundResource(R.drawable.plusicon);

                }
                else {
                    productdistext.setVisibility(View.VISIBLE);
                    //  productqqtoggle.setBackgroundResource(R.drawable.minusicon);
                }

                break;
            case R.id.productsertoggle:
                if(isChecked){
                    productsertext.setVisibility(View.GONE);
                    //  productqqtoggle.setBackgroundResource(R.drawable.plusicon);

                }
                else {
                    productsertext.setVisibility(View.VISIBLE);
                    //  productqqtoggle.setBackgroundResource(R.drawable.minusicon);
                }

                break;
            case R.id.productpritoggle:
                if(isChecked){
                    productpritext.setVisibility(View.GONE);
                    //  productqqtoggle.setBackgroundResource(R.drawable.plusicon);

                }
                else {
                    productpritext.setVisibility(View.VISIBLE);
                    //  productqqtoggle.setBackgroundResource(R.drawable.minusicon);
                }

                break;
        }
    }
}
