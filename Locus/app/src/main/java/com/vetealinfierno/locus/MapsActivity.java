package com.vetealinfierno.locus;
//***** 2/18/17 jGAT
import android.app.Activity;
import android.content.Intent;
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
import com.vetealinfierno.locus.Models.GPSModel;

import static com.vetealinfierno.locus.HomeActivity.GROUP_CREATED;
import static com.vetealinfierno.locus.HomeActivity.GROUP_JOINED;

//this is the activity that will display the map and hopefully display location icons soon
//the map only displays the current location of the user at this moment
//TODO:make this application upload the map according to the group leaders or users location
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    public GoogleApiClient mGoogleApiClient;
    public Marker mCurrLocationMarker;
    public static LatLng mLatLng;
    public Button qr_btn;
    public Button mem_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //gps = new GPSModel(this);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        qr_btn = (Button) findViewById(R.id.qrCodeBtn);
        mem_btn = (Button) findViewById(R.id.mem2_btn);
    }

    //Building the apiClient used for updating location services
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)  //used to configure client
                .addConnectionCallbacks(this)                 //provides callbacks that are called when client is connected or disconnected
                .addOnConnectionFailedListener(this)          //covers scenarios of failed attempt to connect client to service
                .addApi(LocationServices.API)                 //adds the LocationServices API endpoint from GooglePLayServices
                .build();
        mGoogleApiClient.connect();                           //A client must be connected before executing any operation
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(GROUP_JOINED){
            ToggleButtons(true);
        }

    }

    ///disables QR generation button because user has joined a group.
    public void ToggleButtons(boolean status){
        if(status){
            qr_btn.setVisibility(View.GONE);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
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
            //Place current location marker, getting coordinates for current location
            //mLatLng = gps.getLocation();
            mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(mLatLng);
            markerOptions.title("ME!!");
            if(GROUP_CREATED){
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            }else if(GROUP_JOINED){
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
            }else{
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            }

            mCurrLocationMarker = mMap.addMarker(markerOptions);
            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
            //camera zoom into map
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
            Toast.makeText(MapsActivity.this, "location = " + mLatLng,Toast.LENGTH_LONG).show();
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

    //switches the activity to the QR code generator activity, this might not be necessary
    //to have the generator in a new class but it will do for now. this occurs when the user
    //presses the create_button on the home screen.
    public void switchToQRActivity(View view){
        //this starts a new activity if needed for generating QR code
        Intent intent = new Intent(this, QRGenActivity.class);
        startActivity(intent);
    }

    public void switchToHomeActivity(View view){
        //this starts a new activity if needed for generating QR code
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    //switches tot he membersListActivity were we display the list of members int he group
    public void switchToMemActivity(View view){
        if(GROUP_CREATED || GROUP_JOINED) {
            Intent intent = new Intent(this, MembersListActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, "No Group Created, Use Group ID", Toast.LENGTH_LONG).show();
        }
    }

}
//finito