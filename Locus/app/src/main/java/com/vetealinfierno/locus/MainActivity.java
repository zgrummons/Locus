package com.vetealinfierno.locus;
//***** 2/18/17 jGAT
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.common.data.DataBufferUtils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import android.widget.Toast;

import static android.R.attr.duration;

//this is the MainActivity it is the first activity that is called when the application is launched
//this activity serves as the welcome screen to the user then calls the homeScreen activity after 4 seconds
public class MainActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 4000;
    public static final String PREFS_NAME = "LOCUS_Config";
    private static int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //load config
        //SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        //if (!settings.contains("UserId"))
        //    userId = settings.getInt("UserId", -1);

        //if (userId == -1)
            getNewUserId();
        //else
            //get group id

        //handler keeps track of the count down to SPlashTimeOut
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                //intent is telling android what we want to do (switchFrom.this, to something.class)
                Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
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
        saveConfig();
    }

    protected void getNewUserId()
    {
        String connString = "jdbc:microsoft:sqlserver://99.64.48.184:1433;DatabaseName=locus";
        Connection conn = null;
        CallableStatement call = null;

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
            conn = DriverManager.getConnection(connString, "locus", "locus");
            String query = "EXEC getNewUserId NULL";
            call = conn.prepareCall(query);

            if (call.execute()) {
                try (ResultSet rs = call.getResultSet()){
                    userId = rs.getInt(0);

                }
            }
            call.close();
            conn.close();
        } catch (Exception e){
            Toast.makeText(this, e.getStackTrace().toString(), Toast.LENGTH_LONG);
        }

        saveConfig();
    }

    protected void saveConfig() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("UserId", userId);
        editor.commit();
    }
}
//finito
