package com.vetealinfierno.locus;
//***** 2/18/17 jGAT
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

//this is the SplashActivity it is the first activity that is called when the application is launched
//this activity serves as the welcome screen to the user then calls the homeScreen activity after 4 seconds
public class SplashActivity extends AppCompatActivity {

    //region Android Life Cycle Methods Region ##########################################################################################
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final int SPLASH_TIME_OUT = 1000;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //handler keeps track of the count down to SPlashTimeOut
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        },SPLASH_TIME_OUT);
    }

    @Override
    protected void onStop(){
        super.onStop();
        //saveConfig();
    }
    //endregion
}
//finito
