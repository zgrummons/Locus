package com.vetealinfierno.locus;
//***** 2/18/17 jGAT
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.vetealinfierno.locus.JoinActivity.GROUP_ID;
import static com.vetealinfierno.locus.QRGenActivity.QR_GEN;
import static com.vetealinfierno.locus.QRGenActivity.TEXT2_QR;

//this is the home activity the contains the home menu for the user
//consists of the Create_button, Join_button, Leave_button, Map_button, Members_button
//they appear on screen in the order above for the user
public class HomeActivity extends AppCompatActivity {

    public Button leaveGroupBtn, mapBtn, membersBtn, createGroupBtn, joinGroupBtn, grpIDBtn;
    public static boolean GROUP_CREATED = false;
    public static boolean GROUP_JOINED = false;
    public static boolean FIRST_JOIN = false;
    public static String USER_ID = "";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private DatabaseReference dBRef;

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
        grpIDBtn = (Button) findViewById(R.id.grpId_btn);
        ToggleButtons(false);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // ????  what to do here  ?????
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(GROUP_CREATED || GROUP_JOINED){
            ToggleButtons(true);
        }
    }

    ///Enables and Disables leave_button, map_button, members_button
    public void ToggleButtons(boolean status){
        String disabledTxt = "Disabled";
        leaveGroupBtn.setEnabled(status);
        mapBtn.setEnabled(status);
        membersBtn.setEnabled(status);
        grpIDBtn.setVisibility(View.GONE);
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
                grpIDBtn.setVisibility(View.VISIBLE);
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

    //switches the activity to the QR code generator activity, this might not be necessary
    //to have the generator in a new class but it will do for now. this occurs when the user
    //presses the create_button on the home screen.
    public void switchToQRActivity(View view){
        startActivity(new Intent(this, QRGenActivity.class));
    }

    public void LeaveGroupMethod(View view){
        showDialogBox();
    }

    public void showDialogBox(){
        AlertDialog.Builder mBuild = new AlertDialog.Builder(HomeActivity.this);
        mBuild.setTitle("Confirmation:");
        mBuild.setMessage("Are you sure?");
        mBuild.setCancelable(false);
        mBuild.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(HomeActivity.this, "You have selected No.", Toast.LENGTH_SHORT).show();
            }
        });
        mBuild.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeDestroy();
            }
        });
        Dialog dialog = mBuild.create();
        dialog.show();
    }

    //switches to mapsActivity were we hope to display the members as markers on the map
    public void switchToMapActivity(View view){
        startActivity(new Intent(this, MapsActivity.class));
    }

    //switches to mapsActivity were we hope to display the members as markers on the map
    public void switchToJoinActivity(View view){
        startActivity(new Intent(this, JoinActivity.class));
    }

    //switches tot he membersListActivity were we display the list of members int he group
    public void switchToMemActivity(View view){
        startActivity(new Intent(this, MembersListActivity.class));
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
                    }
                } else {
                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void removeMember(){
        dBRef = FirebaseDatabase.getInstance().getReference(GROUP_ID);
        dBRef.child(USER_ID).removeValue();
    }

    public void destroyGroup(){
        dBRef = FirebaseDatabase.getInstance().getReference();
        dBRef.child(TEXT2_QR).removeValue();
    }

    public void removeDestroy() {
        if(GROUP_CREATED){
            destroyGroup();
            GROUP_CREATED = false;
            QR_GEN = false;
            Toast.makeText(this, "You have selected \"Yes\".\nGroup destroyed.", Toast.LENGTH_LONG).show();
        }else if(GROUP_JOINED){
            removeMember();
            GROUP_JOINED = false;
            FIRST_JOIN = false;
            Toast.makeText(this, "You have selected \"Yes\".\nYou have left the group.", Toast.LENGTH_LONG).show();
        }
        ToggleButtons(false);
    }
}
//finito