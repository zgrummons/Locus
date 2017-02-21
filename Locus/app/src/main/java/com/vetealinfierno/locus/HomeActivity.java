package com.vetealinfierno.locus;
//***** 2/18/17 jGAT
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

//this is the home activity the contains the home menu for the user
//consists of the Create_button, Join_button, Leave_button, Map_button, Members_button
//they appear on screen in the order above for the user
public class HomeActivity extends AppCompatActivity {

    public Button leaveGroupBtn;
    public Button mapBtn;
    public Button membersBtn;
    public static boolean GROUP_CREATED = false;
    public static boolean GROUP_JOINED = false;

    ///called when activity is launched
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        leaveGroupBtn = (Button) findViewById(R.id.leave_button);
        mapBtn = (Button) findViewById(R.id.map_button);
        membersBtn = (Button) findViewById(R.id.members_button);
        ToggleButtons(false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(GROUP_CREATED||GROUP_JOINED){
            ToggleButtons(true);
        }
    }

    ///Enables and Disables leave_button, map_button, members_button
    public void ToggleButtons(boolean status){
        leaveGroupBtn.setEnabled(status);
        mapBtn.setEnabled(status);
        membersBtn.setEnabled(status);
    }

    //switches the activity to the QR code generator activity, this might not be necessary
    //to have the generator in a new class but it will do for now. this occurs when the user
    //presses the create_button on the home screen.
    public void switchToQRActivity(View view){
        //this starts a new activity if needed for generating QR code
        Intent intent = new Intent(this, QRGenActivity.class);
        startActivity(intent);
    }

    //this switches the activity to the joinActivity, the join screen for the user.
    //this occurs when the user presses the join_button on the home screen.
    public void switchToJoinActivity(View view){
        //intent is telling android what we want to do which is (swithFrom.this, to something.class)
        Intent intent = new Intent(this, JoinActivity.class);
        ///starting the activity
        startActivity(intent);
    }

    //TODO: add code that removes member from the database table
    public void LeaveGroupMethod(View view){
        String message = "//TODO: add code that removes member from the database table";
        //code to leave group goes here
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

    }

    //switches to mapsActivity were we hope to display the members as markers on the map
    public void switchToMapActivity(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    //switches tot he membersListActivity were we display the list of members int he group
    public void switchToMemActivity(View view){
        Intent intent  = new Intent(this, MembersListActivity.class);
        startActivity(intent);
    }



}
//finito