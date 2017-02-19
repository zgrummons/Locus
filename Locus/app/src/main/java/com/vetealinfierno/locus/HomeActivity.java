package com.vetealinfierno.locus;
//***** 2/18/17 jGAT
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
//this is the home activity the contains the home menu for the user
//consists of the Create_button, Join_button, Leave_button, Map_button, Members_button
//they appear on screen in the order above for the user
public class HomeActivity extends AppCompatActivity {
    ///called when activity is launched
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

    }

    //this switches the activity to the joinActivity, the join screen for the user.
    //this occurs when the user presses the join_button on the home screen.
    public void switchToJoinActivity(View view){
        //intent is telling android what we want to do which is (swithFrom.this, to something.class)
        Intent intent = new Intent(this, JoinActivity.class);
        ///starting the activity
        startActivity(intent);
    }

    //switches the activity to the QR code generator activity, this might not be necessary
    //to have the generator in a new class but it will do for now. this occurs when the user
    //presses the create_button on the home screen.
    public void switchToQRActivity(View view){
        Intent intent = new Intent(this, QRGenActivity.class);
        startActivity(intent);
    }

    //TODO: add code that removes member from the database table
    public void LeaveGroupMethod(View view){
        //code to leave group goes here

    }
    //TODO: get the google api key for the mapsActivity
    //TODO: the instructions for getting the key are in google_maps_api.xml file in the values folder of this project
    public void switchToMapActivity(View view){
        //Intent intent = new Intent(this, MapsActivity.class);
        //startActivity(intent);
    }

    //switches tot he membersListActivity were we display the list of members int he group
    public void switchToMemActivity(View view){
        Intent intent  = new Intent(this, MembersListActivity.class);
        startActivity(intent);
    }









}
//finito