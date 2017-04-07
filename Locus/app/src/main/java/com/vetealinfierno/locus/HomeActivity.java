package com.vetealinfierno.locus;
//***** 2/18/17 jGAT
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.vetealinfierno.locus.LoginActivity.LOGIN;
import static com.vetealinfierno.locus.QRGenActivity.QR_GEN;

//this is the home activity the contains the home menu for the user
//consists of the Create_button, Join_button, Leave_button, Map_button, Members_button
//they appear on screen in the order above for the user
public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    public void print(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    //region Class Variables Region #########################################################################################
    public final String NULL = "Null";
    public final String NO = "No";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static boolean GROUP_CREATED = false;
    public static boolean GROUP_JOINED = false;
    public static boolean FIRST_JOIN = true;
    public static String GROUP_ID = "";
    public Button leaveGroupBtn, mapBtn, membersBtn, createGroupBtn, joinGroupBtn, grpIDBtn, logoutBtn;
    public boolean hiddenButtons = false;
    public String userEmail;
    public FirebaseAuth firebaseAuth;
    public FirebaseUser firebaseUser;
    private ProgressDialog pD;
    //endregion

    //region Android Life Cycle Region ######################################################################################
    ///called when activity is launched
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView_Greeting;
        checkLocationPermission();
        setContentView(R.layout.activity_home);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        firebaseAuth =FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        pD = new ProgressDialog(this);
        if(firebaseUser == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        userEmail = firebaseUser.getEmail();
        String greeting = "Welcome " + userEmail;
        leaveGroupBtn = (Button) findViewById(R.id.leave_button);
        mapBtn = (Button) findViewById(R.id.map_button);
        membersBtn = (Button) findViewById(R.id.members_button);
        createGroupBtn = (Button) findViewById(R.id.create_button);
        joinGroupBtn = (Button) findViewById(R.id.join_Button);
        grpIDBtn = (Button) findViewById(R.id.grpId_btn);
        textView_Greeting = (TextView) findViewById(R.id.textView_greeting);
        textView_Greeting.setText(greeting);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // ????  what to do here  ?????
            }
        }
        logoutBtn = (Button) findViewById(R.id.logOut_btn);
        logoutBtn.setOnClickListener(this);
        hideButtons();
        isTeacherLoggedIn();
    }

    @Override
    protected void onStart() {
        super.onStart();
        String status = "Group Created = " + GROUP_CREATED;
        //Toast.makeText(HomeActivity.this, status, Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(GROUP_CREATED || GROUP_JOINED){
            ToggleButtons(true);
        }
    }
    //endregion

    //region ButtonControl Region ###########################################################################################
    ///Enables and Disables leave_button, map_button, members_button
    public void ToggleButtons(boolean status){
        String disabledTxt = "Disabled";
        leaveGroupBtn.setEnabled(status);
        mapBtn.setEnabled(status);
        membersBtn.setEnabled(status);
        grpIDBtn.setVisibility(View.INVISIBLE);
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
        if(hiddenButtons){
            showButtons();
        }
    }

    public void hideButtons(){
        hiddenButtons = true;
        createGroupBtn.setVisibility(View.INVISIBLE);
        joinGroupBtn.setVisibility(View.INVISIBLE);
        leaveGroupBtn.setVisibility(View.INVISIBLE);
        mapBtn.setVisibility(View.INVISIBLE);
        membersBtn.setVisibility(View.INVISIBLE);
        grpIDBtn.setVisibility(View.INVISIBLE);
    }

    public void showButtons(){
        createGroupBtn.setVisibility(View.VISIBLE);
        joinGroupBtn.setVisibility(View.VISIBLE);
        leaveGroupBtn.setVisibility(View.VISIBLE);
        mapBtn.setVisibility(View.VISIBLE);
        membersBtn.setVisibility(View.VISIBLE);
    }
    //endregion

    //region WhoIsLoggedIn? #################################################################################################
    public void isTeacherLoggedIn(){
        final DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference("Teachers");
        dBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isTeacher = false;
                for(DataSnapshot userSnapShot: dataSnapshot.getChildren()){
                    UserInfo teachers = userSnapShot.getValue(UserInfo.class);
                    String teachersEmail = teachers.getEmail();
                    if(teachersEmail.equals(userEmail)){
                        isTeacher = true;
                    }
                }
                if(!isTeacher){
                    doesStudentHaveAGroup();
                }else{
                    doesTeacherHaveAGroup();
                }
                dBRef.removeEventListener(this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadGroup(){
        LOGIN=false;
        pD.setMessage("Group found. Loading...");
        pD.show();
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                pD.dismiss();
                startMapActivity();
            }
        },2000);
    }
    //endregion

    //region ifTeacherLoggedIn Region #######################################################################################
    public void doesTeacherHaveAGroup(){
        final DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference("Teachers");
        dBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean hasAGroup = false;
                for(DataSnapshot teacherSnapShot: dataSnapshot.getChildren()){
                    UserInfo userInfo = teacherSnapShot.getValue(UserInfo.class);
                    if(userEmail.equals(userInfo.getEmail())){
                        if(userInfo.getStatus().equals("Yes")){
                            hasAGroup = true;
                            //print("Teacher has a group");
                            GROUP_ID = userInfo.getGroupID();
                            print(GROUP_ID);
                            if(LOGIN){
                                loadGroup();
                            }
                            break;
                        }
                    }
                }
                if(hasAGroup){
                    GROUP_CREATED = true;
                    ToggleButtons(true);
                }else{
                    ToggleButtons(false);
                    LOGIN = false;
                }
                dBRef.removeEventListener(this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    //endregion

    //region ifStudentLoggedIn Region #######################################################################################
    public void doesStudentHaveAGroup(){
        final DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference("Students");
        dBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean hasAGroup = false;
                for(DataSnapshot studentSnapShot: dataSnapshot.getChildren()){
                    UserInfo userInfo = studentSnapShot.getValue(UserInfo.class);
                    if(userEmail.equals(userInfo.getEmail())){
                        if(userInfo.getStatus().equals("Yes")){
                            hasAGroup = true;
                            //print("Student has a group");
                            GROUP_ID = userInfo.getGroupID();
                            if(LOGIN){
                                loadGroup();
                            }
                            break;
                        }
                    }
                }
                if(hasAGroup){
                    FIRST_JOIN = false;
                    GROUP_JOINED = true;
                    ToggleButtons(true);
                }else{
                    ToggleButtons(false);
                    setStudentButtons();
                    LOGIN = false;
                }
                dBRef.removeEventListener(this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void setStudentButtons(){
        createGroupBtn.setEnabled(false);
        String temp = "Disabled";
        createGroupBtn.setText(temp);
    }
    //endregion

    //region Button OnClickMethods Region ###################################################################################
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
        startMapActivity();
    }

    public void startMapActivity(){
        //finish();
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

    @Override
    public void onClick(View view){
        if(view == logoutBtn){
            firebaseAuth.signOut();
            GROUP_CREATED = false;
            GROUP_JOINED = false;
            finish();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
    //endregion

    //region LocationPermission Methods Region ##############################################################################
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
    //endregion

    //region Destroy/Remove Member Methods Region ###########################################################################
    public void removeMember(){
        print(GROUP_ID);
        final DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference(GROUP_ID);
        dBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot groupSnapShot: dataSnapshot.getChildren()){
                    UserInfo userInfo = groupSnapShot.getValue(UserInfo.class);
                    if(userEmail.equals(userInfo.getEmail())){
                        String id = userInfo.getUserID();
                        dBRef.child(id).removeValue();
                    }
                    dBRef.removeEventListener(this);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void updateStudentInfo(){
        DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference("Students");
        dBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapShot: dataSnapshot.getChildren()){
                    UserInfo user = userSnapShot.getValue(UserInfo.class);
                    if(userEmail.equals(user.getEmail())){
                        String id = user.getUserID();
                        user.setStatus(NO);
                        user.setGroupID(NULL);
                        user.setUserLocation(NULL);
                        DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference("Students");
                        dBRef.child(id).setValue(user).addOnCompleteListener(HomeActivity.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });
                        dBRef.removeEventListener(this);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void updateTeacherInfo(){
        DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference("Teachers");
        dBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapShot: dataSnapshot.getChildren()){
                    UserInfo user = userSnapShot.getValue(UserInfo.class);
                    if(userEmail.equals(user.getEmail())){
                        String id = user.getUserID();
                        user.setStatus(NO);
                        user.setGroupID(NULL);
                        user.setUserLocation(NULL);
                        DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference("Teachers");
                        dBRef.child(id).setValue(user).addOnCompleteListener(HomeActivity.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });
                        dBRef.removeEventListener(this);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void updateGroupInfo(){
        final DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference("Groups");
        dBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot groupSnapShot: dataSnapshot.getChildren()){
                    GroupInfo groupInfo = groupSnapShot.getValue(GroupInfo.class);
                    if(userEmail.equals(groupInfo.getGroupLeader())){
                        String id = groupInfo.getKey();
                        dBRef.child(id).removeValue();
                    }
                    dBRef.removeEventListener(this);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void removeStudentsFromGroup(final String groupID){
        DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference("Students");
        dBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapShot: dataSnapshot.getChildren()){
                    UserInfo user = userSnapShot.getValue(UserInfo.class);
                    print("Inside remove students = " +groupID);
                    if(groupID.equals(user.getGroupID())){
                        String id = user.getUserID();
                        user.setStatus(NO);
                        user.setGroupID(NULL);
                        user.setUserLocation(NULL);
                        DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference("Students");
                        dBRef.child(id).setValue(user).addOnCompleteListener(HomeActivity.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });
                        dBRef.removeEventListener(this);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void destroyGroup(){
        //destroys the group
        DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference();
        dBRef.child(GROUP_ID).removeValue();
    }

    public void removeDestroy() {
        ToggleButtons(false);
        if(GROUP_CREATED){
            removeStudentsFromGroup(GROUP_ID);
            destroyGroup();
            updateTeacherInfo();
            updateGroupInfo();
            GROUP_CREATED = false;
            QR_GEN = false;
            GROUP_ID = "";
            Toast.makeText(this, "You have selected \"Yes\".\nGroup destroyed.", Toast.LENGTH_LONG).show();
        }else if(GROUP_JOINED){
            removeMember();
            updateStudentInfo();
            GROUP_JOINED = false;
            FIRST_JOIN = true;
            GROUP_ID = "";
            setStudentButtons();
            Toast.makeText(this, "You have selected \"Yes\".\nYou have left the group.", Toast.LENGTH_LONG).show();
        }

    }
    //endregion

}
//region finito jGAT
/*....................../´¯/)
....................,/¯../
.................../..../
............./´¯/'...'/´¯¯`·¸
........../'/.../..../......./¨¯\
........('(...´...´.... ¯~/'...')
.........\.................'...../
..........''...\.......... _.·´
............\..............(
..............\.............\...
*/
//endregion








