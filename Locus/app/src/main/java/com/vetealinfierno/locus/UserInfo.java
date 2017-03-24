package com.vetealinfierno.locus;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by johntoland on 3/22/17.
 */

public class UserInfo {
    String userID;
    String userLocation;

    public UserInfo(){

    }

    public UserInfo(String userID, String userLocation) {
        this.userID = userID;
        this.userLocation = userLocation;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserLocation() {
        return userLocation;
    }
}
