package com.vetealinfierno.locus;
//***** 2/18/17 jGAT
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.vetealinfierno.locus.Models.GPSModel;

import static com.vetealinfierno.locus.QRGenActivity.QR_GEN;

//this is the home activity the contains the home menu for the user
//consists of the Create_button, Join_button, Leave_button, Map_button, Members_button
//they appear on screen in the order above for the user
public class HomeActivity extends AppCompatActivity {

    public Button leaveGroupBtn, mapBtn, membersBtn, createGroupBtn, joinGroupBtn;
    public static boolean GROUP_CREATED = false;
    public static boolean GROUP_JOINED = false;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    //variables for GPSModel object
    //public static int WAIT_TiME = 1500;
    //public GPSModel gps;
    //public LatLng mLatLng;

    ///called when activity is launched
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkLocationPermission();
        setContentView(R.layout.activity_home);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        leaveGroupBtn = (Button) findViewById(R.id.leave_button);
        mapBtn = (Button) findViewById(R.id.map_button);
        membersBtn = (Button) findViewById(R.id.members_button);
        createGroupBtn = (Button) findViewById(R.id.create_button);
        joinGroupBtn = (Button) findViewById(R.id.join_Button);
        ToggleButtons(false);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //createGPSObject();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
        if(MapsActivity.mLatLng != null){
            Toast.makeText(this, "Location MapAct = " + MapsActivity.mLatLng, Toast.LENGTH_LONG).show();
        }
        */
        if(GROUP_CREATED || GROUP_JOINED){
            ToggleButtons(true);
        }
    }
/*
    //creates the GPS object
    public void  createGPSObject(){
        gps = new GPSModel(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLatLng = gps.getLocation();
                Toast.makeText(HomeActivity.this, "mLatLng GPSMODEL = " +mLatLng, Toast.LENGTH_LONG).show();
            }
        },WAIT_TiME);

    }
*/
    ///Enables and Disables leave_button, map_button, members_button
    public void ToggleButtons(boolean status){
        String disabledTxt = "Disabled";
        leaveGroupBtn.setEnabled(status);
        mapBtn.setEnabled(status);
        membersBtn.setEnabled(status);
        joinGroupBtn.setEnabled(!status);
        createGroupBtn.setEnabled(!status);
        if(!status){
            String create = "Create";
            String join = "Join";
            leaveGroupBtn.setText(disabledTxt);
            mapBtn.setText(disabledTxt);
            membersBtn.setText(disabledTxt);
            createGroupBtn.setText(create);
            joinGroupBtn.setText(join);
        }else if(status){
            joinGroupBtn.setText(disabledTxt);
            createGroupBtn.setText(disabledTxt);
            String leave;
            if(GROUP_CREATED){
                leave = "Destroy Group";
            }else{
                leave = "Leave";
            }
            String members = "Members";
            String map = "Map";
            leaveGroupBtn.setText(leave);
            mapBtn.setText(map);
            membersBtn.setText(members);
        }
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
        GROUP_JOINED = false;
        GROUP_CREATED = false;
        QR_GEN = false;
        ToggleButtons(false);

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

    //returns PackageManager.PERMISSION_GRANTED and the app can proceed with the operation
    //if the app does not have permission, returns PERMISSION_DENIED, and the app has to explicitly ask for permission
    //if the user permission is not granted then the app will proceed with showing explanation to the user
    //shouldShowRequestPermissionRationale method returns true if app has request this permission previously and the user denied request
    //if ^^ returns false then the user has chosen "Don't ask again option when it previously asked for permission
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    //When the user responds to RationalRequest the system invokes app's onRequestPermissionsResult()
    //the app overrides this method to find out whether the permission was granted.
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        ///this is called because the object was not created before permissions where granted
                        //createGPSObject();
                    }
                } else {
                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}
//finito