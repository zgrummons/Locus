package com.vetealinfierno.locus;
//***** 3/24/17 jGAT
public class GroupInfo {

    String groupID;
    String groupLeader;
    String key;

    public GroupInfo() {
    }

    public GroupInfo(String key, String groupID, String groupLeader) {
        this.key = key;
        this.groupID = groupID;
        this.groupLeader = groupLeader;
    }

    public String getGroupID() {
        return groupID;
    }

    public String getGroupLeader() {
        return groupLeader;
    }

    public String getKey() {
        return key;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public void setGroupLeader(String groupLeader) {
        this.groupLeader = groupLeader;
    }
}
//finito jGAT
