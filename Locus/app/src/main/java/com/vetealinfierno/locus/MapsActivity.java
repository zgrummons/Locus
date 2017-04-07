package com.vetealinfierno.locus;
//***** 2/18/17 jGAT
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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

import java.util.ArrayList;
import java.util.List;

import static com.vetealinfierno.locus.HomeActivity.FIRST_JOIN;
import static com.vetealinfierno.locus.HomeActivity.GROUP_CREATED;
import static com.vetealinfierno.locus.HomeActivity.GROUP_ID;
import static com.vetealinfierno.locus.HomeActivity.GROUP_JOINED;

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
    public Button qr_btn;
    public Button mem_btn;
    List<UserInfo> userList;
    public FirebaseAuth firebaseAuth;
    public FirebaseUser firebaseUser;
    public String USER_EMAIL;
    //endregion

    //region Android Life Cycle  Methods Region #################################################################################################
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //gps = new GPSModel(this);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        qr_btn = (Button) findViewById(R.id.qrCodeBtn);
        mem_btn = (Button) findViewById(R.id.mem2_btn);
        userList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        USER_EMAIL = firebaseUser.getEmail();

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
    public void joinGroup(final String groupID, LatLng latLng){
        DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference(groupID);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String email = user.getEmail();
        String id = dBRef.push().getKey();
        String membersLocation = getLocationString(latLng);
        UserInfo userInfo = new UserInfo(id, membersLocation, email, "Member", groupID);
        dBRef.child(id).setValue(userInfo).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(MapsActivity.this, "You Have Joined Group: "+groupID, Toast.LENGTH_SHORT).show();
            }
        });
        updateUserStatus(groupID, membersLocation);

    }

    public void updateUserStatus(final String groupID, final String membersLocation){
        DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference("Students");
        dBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot userSnapShot: dataSnapshot.getChildren()){
                    UserInfo user = userSnapShot.getValue(UserInfo.class);
                    userList.add(user);
                    if(USER_EMAIL.equals(user.getEmail())){
                        String id = user.getUserID();
                        user.setStatus("Yes");
                        user.setGroupID(groupID);
                        user.setUserLocation(membersLocation);
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
                for(DataSnapshot locationSnapShot: dataSnapshot.getChildren()){
                    UserInfo user = locationSnapShot.getValue(UserInfo.class);
                    if(USER_EMAIL.equals(user.getEmail())){
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
        LocationRequest mLocationRequest;
        mLocationRequest = new LocationRequest();       //get quality of service for location updates from FusedLocationProvider API using requestLocationUpdates
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
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
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(mLatLng);
            markerOptions.title("ME!!");
            if(GROUP_CREATED){
                //TODO: add GetLocationFromDBToUpdateMarkers() to generate the MEMBERS markers on the map!!!!!!
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            }else if(GROUP_JOINED){
                if(FIRST_JOIN){
                    FIRST_JOIN = false;
                    joinGroup(GROUP_ID, mLatLng);
                }
                //TODO: add GetLocationFromDBToUpdateMarkers() to generate the LEADER's marker on the map!!!!!!
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
            }else{
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            }
            //TODO: add UpdateLocationsToDB() to update the group MEMBER's and LEADER's locations to DB!!!!!!
            updateMemberLocationToDB(mLatLng);
            mCurrLocationMarker = mMap.addMarker(markerOptions);
            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
            //camera zoom into map
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
            //Toast.makeText(MapsActivity.this, "location = " + mLatLng, Toast.LENGTH_LONG).show();
            if (mGoogleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
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