package com.vetealinfierno.locus;
//***** 2/18/17 jGAT

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import static com.vetealinfierno.locus.HomeActivity.FIRST_JOIN;
import static com.vetealinfierno.locus.HomeActivity.GROUP_CREATED;
import static com.vetealinfierno.locus.HomeActivity.GROUP_ID;
import static com.vetealinfierno.locus.HomeActivity.GROUP_JOINED;
import static com.vetealinfierno.locus.HomeActivity.SAFE_ZONE;

//this is the activity that will display the map and hopefully display location icons soon
//the map only displays the current location of the user at this moment
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    public void print(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    //region Class Variables Region #############################################################################################################
    private GoogleMap mMap;
    public GoogleApiClient mGoogleApiClient;
    public Marker mCurrLocationMarker;
    public static LatLng mLatLng;
    private final int MAX_GROUP_SIZE = 10;
    public Marker[] groupMarkers = new Marker[MAX_GROUP_SIZE];
    public LatLng[] groupLatLng = new LatLng[MAX_GROUP_SIZE];
    public String[] memberIDs = new String[MAX_GROUP_SIZE];
    public MarkerOptions[] groupMarkerOptions = new MarkerOptions[MAX_GROUP_SIZE];
    public Button qr_btn;
    public Button mem_btn;
    private LocationRequest mLocationRequest;

    public FirebaseAuth firebaseAuth;
    public FirebaseUser firebaseUser;
    public String USER_EMAIL;
    //endregion

    //region Getting System Time ################################################################################################################
    public String getTime(){
        Date d = new Date();
        CharSequence s = DateFormat.format("yyyy-MM-dd hh:mm:ss", d.getTime());
        return s.toString();
    }
    //endregion###

    //region Android Life Cycle  Methods Region #################################################################################################
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mLocationRequest = new LocationRequest();       //get quality of service for location updates from FusedLocationProvider API using requestLocationUpdates
        mLocationRequest.setInterval(30000);
        mLocationRequest.setFastestInterval(30000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        qr_btn = (Button) findViewById(R.id.qrCodeBtn);
        mem_btn = (Button) findViewById(R.id.mem2_btn);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        USER_EMAIL = firebaseUser.getEmail();
        for(int i =0; i<MAX_GROUP_SIZE; i++){
            groupLatLng[i] = null;
            groupMarkers[i] = null;
            groupMarkerOptions[i] = null;
            memberIDs[i] = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(GROUP_JOINED){
            ToggleButtons(true);
        }else{
            ToggleButtons(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        USER_EMAIL = "";
    }
    //endregion

    //region Convert LatLng -> String Method Region #############################################################################################
    public String getLocationString(LatLng latLng){
        String location;
        Double lat = latLng.latitude;
        Double log = latLng.longitude;
        location = lat.toString() +", "+ log.toString();
        return location;
    }
    //endregion

    //region Joining Group Methods Region #######################################################################################################
    public void joinGroup(final String groupID, final LatLng latLng){
        final DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference(groupID);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String email = user.getEmail();
        String id = dBRef.push().getKey();
        String membersLocation = getLocationString(latLng);
        UserInfo userInfo = new UserInfo(id, membersLocation, email, "Member", groupID, Integer.toString(SAFE_ZONE), getTime());
        dBRef.child(id).setValue(userInfo);

        updateUserStatus(groupID, membersLocation);
        removeMarkers();
        grabGroupMembersLocationFromDB();
        updateMarkers();
        print("You have Joined Group " + groupID);
    }

    public void updateUserStatus(final String groupID, final String membersLocation){
        final DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference("Students");
        dBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapShot: dataSnapshot.getChildren()){
                    UserInfo user = userSnapShot.getValue(UserInfo.class);
                    if(USER_EMAIL.equals(user.getEmail())){
                        String id = user.getUserID();
                        user.setStatus("Yes");
                        user.setGroupID(groupID);
                        user.setUserLocation(membersLocation);
                        user.setSafeZone(Integer.toString(SAFE_ZONE));
                        DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference("Students");
                        dBRef.child(id).setValue(user).addOnCompleteListener(MapsActivity.this, new OnCompleteListener<Void>() {
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
    //endregion

    //region Updating Member Location Methods Region ############################################################################################
    public void updateMemberLocationToDB(final LatLng latLng){
        DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference(GROUP_ID);
        dBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for(DataSnapshot locationSnapShot: dataSnapshot.getChildren()){
                    UserInfo user = locationSnapShot.getValue(UserInfo.class);
                    memberIDs[i] = null;
                    groupLatLng[i] = null;
                    groupMarkers[i] = null;
                    groupMarkerOptions[i] = null;
                    i++;
                    if(USER_EMAIL.equals(user.getEmail())){
                        //print("USER email = " + USER_EMAIL);
                        String id = user.getUserID();
                        String location = getLocationString(latLng);
                        user.setUserLocation(location);
                        DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference(GROUP_ID);
                        dBRef.child(id).setValue(user).addOnCompleteListener(MapsActivity.this, new OnCompleteListener<Void>() {
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
        if(GROUP_CREATED || GROUP_JOINED){
            removeMarkers();
            grabGroupMembersLocationFromDB();
            updateMarkers();
        }
    }
    //endregion

    //region Google Maps/Location Services Methods Region #######################################################################################
    //Building the apiClient used for updating location services
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)  //used to configure client
                .addConnectionCallbacks(this)                 //provides callbacks that are called when client is connected or disconnected
                .addOnConnectionFailedListener(this)          //covers scenarios of failed attempt to connect client to service
                .addApi(LocationServices.API)                 //adds the LocationServices API endpoint from GooglePLayServices
                .build();
        mGoogleApiClient.connect();                           //A client must be connected before executing any operation
    }

    //region Description of onMapReady Region
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    //endregion
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //setting map type to HYBRID
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //used to enable location layer which will allow a user to interact with current user location.
                mMap.setMyLocationEnabled(true);
                buildGoogleApiClient();
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    //getting the coordinates of current location and updating the camera
    @Override
    public void onLocationChanged(Location location) {
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }
            mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            // updates the members location to database
            updateMemberLocationToDB(mLatLng);
            MarkerOptions userMarkerOptions = new MarkerOptions();
            userMarkerOptions.position(mLatLng);
            userMarkerOptions.title("ME!!");
            if(GROUP_CREATED){
                checkDistanceFromMembers(location);
                userMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            }else if(GROUP_JOINED){
                if(FIRST_JOIN){
                    FIRST_JOIN = false;
                    joinGroup(GROUP_ID, mLatLng);
                }else{

                    checkDistanceFromMembers(location);
                }

                userMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            }else{
                userMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            }
            mCurrLocationMarker = mMap.addMarker(userMarkerOptions);
            // move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
            // camera zoom into map
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
/*
            if (mGoogleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
*/
    }

    public void grabGroupMembersLocationFromDB() {
        final DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference(GROUP_ID);
        dBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for(DataSnapshot locationSnapShot: dataSnapshot.getChildren()){
                    UserInfo user = locationSnapShot.getValue(UserInfo.class);
                    //print("User status = "+ user.getStatus());
                    if(user.getStatus().contentEquals("Member") && GROUP_CREATED || !USER_EMAIL.equals(user.getEmail()) && GROUP_JOINED){
                        memberIDs[i] = user.getEmail();
                        //print(" i = " + i);
                        //print("member id = " + memberIDs[i]);
                        addMemberLocationToArray(user.getUserLocation(), i);
                        i++;
                    }
                }
                dBRef.removeEventListener(this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addMemberLocationToArray(String location, int i){
        //print("i = "+ i);
        groupLatLng[i] = StringToLatLng(location);
        //print("groupLatLng["+i+"] = " + groupLatLng[i]);
    }

    public LatLng StringToLatLng(String location){
        LatLng latLng;
        String lat = location.substring(0,location.indexOf(','));
        String lng = location.substring(location.indexOf(' '));
        Double latDub = Double.parseDouble(lat);
        Double lngDub = Double.parseDouble(lng);
        latLng = new LatLng(latDub, lngDub);
        return latLng;
    }

    public void removeMarkers(){
        if(groupMarkers != null) {
            for (Marker marker : groupMarkers) {
                if (marker != null) {
                    marker.remove();
                }
            }
        }
    }

    public void updateMarkers(){
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                for(int i = 0; i<groupLatLng.length; i++){
                    if(groupLatLng[i] != null) {
                        //print("inside placing the markers, i = " + i);
                        groupMarkerOptions[i] = new MarkerOptions();
                        groupMarkerOptions[i].position(groupLatLng[i]);
                        // this will display the user email on the map
                        groupMarkerOptions[i].title(memberIDs[i]);
                        if (GROUP_JOINED) {
                            if (i == 0) {
                                //groupMarkerOptions[i].title("Leader");
                                groupMarkerOptions[i].icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            } else {
                                //groupMarkerOptions[i].title("Member");
                                groupMarkerOptions[i].icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                            }
                            groupMarkers[i] = mMap.addMarker(groupMarkerOptions[i]);
                        }else{
                            //groupMarkerOptions[i].title("Member");
                            groupMarkerOptions[i].icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                            groupMarkers[i] = mMap.addMarker(groupMarkerOptions[i]);
                        }
                    }
                }
            }
        },4000);
    }

    public void checkDistanceFromMembers(Location location) {
        Location memberLocation = new Location("");
        // user is a group leader check distance from each member
        if (GROUP_CREATED) {
            for (int i = 0; i < groupLatLng.length; i++) {
                if (groupLatLng[i] != null) {
                    memberLocation.setLatitude(groupLatLng[i].latitude);
                    memberLocation.setLongitude(groupLatLng[i].longitude);
                    // print("groupLatLng["+i+"] = "+ groupLatLng[i]);
                    // print("distance between = " + location.distanceTo(memberLocation));
                    // print("safe Zone = " + SAFE_ZONE);
                    if (location.distanceTo(memberLocation) > SAFE_ZONE) {
                       // print("member is far from leader");
                        groupMarkerOptions[i].icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                        PushNotification(i, location.distanceTo(memberLocation));
                    }
                }
            }
        }else{
            // user is a member check distance from leader
            for(int i =0; i<1; i++){
                if(groupLatLng[i] != null){
                    memberLocation.setLatitude(groupLatLng[i].latitude);
                    memberLocation.setLongitude(groupLatLng[i].longitude);
                    // print("distance between = " + location.distanceTo(memberLocation));
                    // print("safe zone = " + SAFE_ZONE);
                    if (location.distanceTo(memberLocation) > SAFE_ZONE) {
                        // print("you are far from leader");
                        PushNotification(i , location.distanceTo(memberLocation));
                    }
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
    //endregion

    //region Button Control Methods Region ######################################################################################################
    public void switchToQRActivity(View view){
        startActivity(new Intent(this, QRGenActivity.class));
    }

    public void switchToHomeActivity(View view){
        startActivity(new Intent(this, HomeActivity.class));
    }

    //switches tot he membersListActivity were we display the list of members int he group
    public void switchToMemActivity(View view){
        if(GROUP_CREATED || GROUP_JOINED) {
            startActivity(new Intent(this, MembersListActivity.class));
        }else{
            Toast.makeText(this, "No Group Created, Use Group ID", Toast.LENGTH_LONG).show();
        }
    }

    ///disables QR generation button because user has joined a group.
    public void ToggleButtons(boolean status){
        if(status){
            qr_btn.setVisibility(View.GONE);
        }
    }
    //endregion

    //region PushNotifications ##################################################################################################################
    public void PushNotification(int i, float distanceBetween){
        String title;
        if(GROUP_CREATED){
            title = "Caliente: Member falling behind!";
        }else{
            title = "Caliente: Return to leader!";
        }
        long[] pattern = {500,500,500,500,500,500,500,500,500};
        NotificationManager pushNotify = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MapsActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(memberIDs[i] +" is "+ (Math.floor(distanceBetween * 100) / 100)+ "meters from you.")
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setLights(Color.BLUE, 500, 500)
                .setVibrate(pattern)
                .setContentIntent(pIntent)
                .build();
        pushNotify.notify(0, notification);
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