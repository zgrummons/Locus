package com.vetealinfierno.locus;
//***** 3/24/17 jGAT
public class UserInfo {
    String userID;
    String userLocation;
    String email;
    String status;
    String groupID;
    String safeZone;
    String time;


    public UserInfo() {

    }

    public UserInfo(String userID, String userLocation, String email, String status, String groupID, String safeZone, String time) {
        this.userID = userID;
        this.userLocation = userLocation;
        this.email = email;
        this.status = status;
        this.groupID = groupID;
        this.safeZone = safeZone;
        this.time = time;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getSafeZone(){
        return safeZone;
    }

    public void setSafeZone(String safeZone){
        this.safeZone = safeZone;
    }

    public String getTime(){
        return this.time;
    }

    public void setTime(String time){
        this.time = time;
    }


}
//finito jGAT
