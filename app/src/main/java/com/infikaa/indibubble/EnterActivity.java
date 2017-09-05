package com.infikaa.indibubble;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class EnterActivity extends AppCompatActivity {
    Button login;
    Button register;
    ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }*/
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);
        login=(Button) findViewById(R.id.login);
        register=(Button) findViewById(R.id.register);
        logo=(ImageView) findViewById(R.id.enter_logo);

        FrameLayout frameLayout=(FrameLayout)findViewById(R.id.activity_login);
        frameLayout.setBackground(getResources().getDrawable(R.drawable.background_waves));

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(EnterActivity.this, LoginActivity.class);
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Pair[] pairs = new Pair[1];
                    pairs[0] = new Pair<View, String>(logo, "logoImage");
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                            EnterActivity.this, pairs);
                    startActivity(myIntent, options.toBundle());
                }
                else {
                    startActivity(myIntent);
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(EnterActivity.this, RegisterActivity.class);

                EnterActivity.this.startActivity(myIntent);
            }
        });
    }
}
