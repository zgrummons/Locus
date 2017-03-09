package com.vetealinfierno.locus;
//***** 2/18/17 jGAT
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

//this is the SplashActivity it is the first activity that is called when the application is launched
//this activity serves as the welcome screen to the user then calls the homeScreen activity after 4 seconds
public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //handler keeps track of the count down to SPlashTimeOut
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                //intent is telling android what we want to do (switchFrom.this, to something.class)
                Intent homeIntent = new Intent(SplashActivity.this, HomeActivity.class);
                ///starting the activity
                startActivity(homeIntent);
                //finish deletes the welcome screen from the "back stack mon"
                finish();
            }
        },SPLASH_TIME_OUT);
    }

    @Override
    protected void onStop(){
        super.onStop();
        //saveConfig();
    }
}
//finito
