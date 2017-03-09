package com.vetealinfierno.locus.Models;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
/**
 * Created by zgrum on 2/27/2017.
 */

public class GPSModel extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private LatLng latLng;
    private Activity activity;
    private GoogleApiClient mGoogleApiClient;

    public GPSModel(Activity activity) {
        this.activity = activity;
        if (activity != null) {
            buildGoogleApiClient(activity);
        }
    }

    //Building the apiClient used for updating location services
    protected synchronized void buildGoogleApiClient(Activity activity) {
        mGoogleApiClient = new GoogleApiClient.Builder(activity)  //used to configure client
                .addConnectionCallbacks(this)                 //provides callbacks that are called when client is connected or disconnected
                .addOnConnectionFailedListener(this)          //covers scenarios of failed attempt to connect client to service
                .addApi(LocationServices.API)                 //adds the LocationServices API endpoint from GooglePLayServices
                .build();
        mGoogleApiClient.connect();                           //A client must be connected before executing any operation
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest;
        mLocationRequest = new LocationRequest();       //get quality of service for location updates from FusedLocationProvider API using requestLocationUpdates
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {


    }

    @Override
    public void onLocationChanged(Location location) {
        Location mLastLocation;
        mLastLocation = location;
        latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        //stop location updates
        if (mGoogleApiClient != null) {
            removeUpdates();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


    }

    public LatLng getLocation() {
        return latLng;

    }

    public void removeUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }
}
