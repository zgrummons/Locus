package com.vetealinfierno.locus;
//***** 2/18/17 jGAT
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
///this activity will display a list of the members in the group
///needs a method that receives data from the DB table
public class MembersListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    //TODO: add code that will update a list with data received from the DB table
}
//finito