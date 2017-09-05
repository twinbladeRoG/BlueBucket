package com.infikaa.indibubble;

import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.infikaa.indibubble.cart.CartActivity;
import com.infikaa.indibubble.cart.FavouriteActivty;
import com.infikaa.indibubble.order.OrderFragment;
import com.infikaa.indibubble.settings.SettingsFragment;

public class SurfActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        FragmentToActivity, IssueBackButton, FragmentManager.OnBackStackChangedListener {

    //Toolbar toolbar;
    FragmentManager fragmentManager;
    DrawerLayout drawer;
    //ActionBarDrawerToggle toggle;
    android.app.Fragment f;
    String postalcode;
    int screenWidth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surf);

        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        /*toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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
        toggle.syncState();*/

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        // fragmentManager=getFragmentManager();
        postalcode="555444";
        screenWidth=((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        /*SharedPreferences pref = getApplicationContext().getSharedPreferences("Bluebucket", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("username","subho040995@gmail.com");
        editor.putString("phoneno","8013554858");
        editor.putString("cusid","1");
        editor.putString("address","BB-91,Salt Lake City,Kolkata,West Bengal,India,555444");
        editor.putString("postalcode","555444");
        editor.commit();*/
        fragmentManager=getFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);

        android.app.Fragment f=fragmentManager.findFragmentByTag("Home");
        if(f != null && f instanceof Home) {

            fragmentManager.popBackStack("Home", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        fragmentManager.beginTransaction().replace(R.id.displaycontent,Home.newInstance(screenWidth),"Home").addToBackStack("Home").commit();
        /*fragmentManager=getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.displaycontent,new Home()).addToBackStack("home").commit();
*/  }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        if(fragmentManager.getBackStackEntryCount() == 0 ){

            fragmentManager.beginTransaction().replace(R.id.displaycontent,Home.newInstance(screenWidth),"Home").addToBackStack("Home").commit();        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
          fragmentManager.popBackStack();
            fragmentManager.beginTransaction().replace(R.id.displaycontent,Home.newInstance(screenWidth),"Home").addToBackStack("Home").commit();

        } else if (id == R.id.nav_cart) {

            fragmentManager.popBackStack();
            fragmentManager.beginTransaction().replace(R.id.displaycontent, CartActivity.newInstance(screenWidth),"CartActivity").addToBackStack("CartActivity").commit();

        } else if (id == R.id.nav_orders) {
            fragmentManager.popBackStack();
            fragmentManager.beginTransaction( ).replace(R.id.displaycontent, OrderFragment.newInstance(),"OrderFragment").addToBackStack("OrderFragment").commit();

        } else if (id == R.id.nav_qrcode) {
            checkORCodePermission();
        } else if (id == R.id.nav_favourite) {
            fragmentManager.popBackStack();
            fragmentManager.beginTransaction().replace(R.id.displaycontent, FavouriteActivty.newInstance(screenWidth),"FavouriteActivity").addToBackStack("FavouriteActivity").commit();

        } else if (id == R.id.nav_logout) {

        } else if (id == R.id.nav_legal) {
            fragmentManager.popBackStack();
            fragmentManager.beginTransaction().replace(R.id.displaycontent,new LegalActivity(),"Legal").addToBackStack("Legal").commit();

        }
        else if(id==R.id.nav_help){
            fragmentManager.popBackStack();
            f=HelpDeskActivity.newInstance("subho040995@gmail.com");
            fragmentManager.beginTransaction().replace(R.id.displaycontent,f,"HelpDeskActivity").addToBackStack("HelpDeskActivity").commit();
        }
        else if(id==R.id.nav_setting){
            fragmentManager.popBackStack();
            f= SettingsFragment.newInstance();
            fragmentManager.beginTransaction().replace(R.id.displaycontent,f,"SettingsActivity").addToBackStack("SettingsActivity").commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void SetNavState(int id) {
        NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
        nav.setCheckedItem(id);
    }

    @Override
    public boolean onSupportNavigateUp() {
        fragmentManager.popBackStack();
        return true;
    }

    @Override
    public void issueBackButton(Boolean flag) {
       // toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(!flag){
            Toast.makeText(getApplicationContext(),flag+" sdgfsg ",Toast.LENGTH_SHORT).show();
            //toggle.setDrawerIndicatorEnabled(flag);
        }
     /*   if(flag==true){

            toolbar.setNavigationIcon(R.drawable.carticon);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   activity.onBackPressed();
                }
            });
        }
        else{

            toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
        }*/
    }
    @Override
    public void onBackStackChanged() {

        if(fragmentManager.getBackStackEntryCount() > 1){
            if (getSupportActionBar() != null)
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }else {
            if (getSupportActionBar() != null)
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    void checkORCodePermission(){
        int rc = ContextCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.CAMERA);
        if(rc == PackageManager.PERMISSION_GRANTED){
            fragmentManager.popBackStack();
            fragmentManager.beginTransaction().replace(R.id.displaycontent,QRFragment.newInstance(),"QRFragment").addToBackStack("QRFragment").commit();

        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 2);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fragmentManager.popBackStack();
                fragmentManager.beginTransaction().replace(R.id.displaycontent, QRFragment.newInstance(), "QRFragment").addToBackStack("QRFragment").commit();

            } else {

                checkORCodePermission();


            }
        }
        else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }




}
