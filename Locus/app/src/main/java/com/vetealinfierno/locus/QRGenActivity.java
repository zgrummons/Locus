package com.vetealinfierno.locus;
//***** 2/18/17 jGAT
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.vetealinfierno.locus.HomeActivity.GROUP_CREATED;
import static com.vetealinfierno.locus.HomeActivity.GROUP_ID;
import static com.vetealinfierno.locus.HomeActivity.GROUP_JOINED;
import static com.vetealinfierno.locus.HomeActivity.SAFE_ZONE;
import static com.vetealinfierno.locus.MapsActivity.mLatLng;

///this is the QR code generator activity, this class/Activity might not be necessary for the
//generating of a QR code but im new to this so im going to try it this way.
public class QRGenActivity extends AppCompatActivity {

    public void print(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    //region Class Variables Region ########################################################################################################
    TextView groupID;
    ImageView image;
    public static boolean QR_GEN =false;
    public static Bitmap QR_CODE;
    List<UserInfo> userList;
    public FirebaseAuth firebaseAuth;
    public FirebaseUser firebaseUser;
    public String USER_EMAIL;
    //endregion

    //region Android Life Cycle Methods Region #############################################################################################
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrgen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        groupID = (TextView) findViewById(R.id.groupID_text);
        image = (ImageView) findViewById(R.id.image);
        userList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        USER_EMAIL = firebaseUser.getEmail();
        if(GROUP_CREATED) {
            generateGroupID();
        }else{
            GROUP_ID = GenerateRandomNumber(3);
            //print(GROUP_ID);
            generateGroupID();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(QR_GEN){
            ToggleQRGenBtn(false);
        }else{
            ToggleQRGenBtn(true);
        }
    }
    //endregion

    //region Generate QR Code Region #######################################################################################################
    public void generateGroupID(){
        image.setVisibility(View.VISIBLE);
        if (!GROUP_ID.equals("")) {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try {
                BitMatrix bitMatrix = multiFormatWriter.encode(GROUP_ID, BarcodeFormat.QR_CODE, 400, 400);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                QR_CODE = barcodeEncoder.createBitmap(bitMatrix);
                image.setImageBitmap(QR_CODE);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            //disables join and create btns, enables members, leave, map
            if(!GROUP_CREATED){
                createGroup(GROUP_ID);
                GROUP_CREATED = true;
            }
            QR_GEN = true;
            ToggleQRGenBtn(false);
            String temp = "Group ID: "+GROUP_ID;
            groupID.setText(temp);
        }else{
            print("Error: Enter Text First");
        }
    }
    //endregion

    //region GenRanNum/LatLng -> String Methods Region #####################################################################################
    String GenerateRandomNumber(int charLength) {
        return String.valueOf(charLength < 1 ? 0 : new Random()
                .nextInt((9 * (int) Math.pow(10, charLength - 1)) - 1)
                + (int) Math.pow(10, charLength - 1));
    }

    public String setLocationString(LatLng latLng){
        String location;
        Double lat = latLng.latitude;
        Double log = latLng.longitude;
        location = lat.toString() +", "+ log.toString();
        return location;
    }
    //endregion

    //region CreagteGroup/Update Teach Info Methods Region #################################################################################
    public void createGroup(final String groupID){
        DatabaseReference dBRef;
        String leaderEmail = USER_EMAIL;
        String leadersLocation = setLocationString(mLatLng);
        dBRef = FirebaseDatabase.getInstance().getReference(groupID);
        String id = dBRef.push().getKey();
        UserInfo userInfo = new UserInfo(id, leadersLocation, leaderEmail, "LEADER", groupID ,Integer.toString(SAFE_ZONE), getTime());
        dBRef.child(id).setValue(userInfo).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               print("Group: "+groupID+" Created");
            }
        });
        dBRef = FirebaseDatabase.getInstance().getReference("Groups");
        String id2 = dBRef.push().getKey();
        GroupInfo groupInfo = new GroupInfo(id2, groupID,leaderEmail, Integer.toString(SAFE_ZONE));
        dBRef.child(id2).setValue(groupInfo).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
        updateTeachersData(groupID, leadersLocation);
    }

    public String getTime(){
        Date d = new Date();
        CharSequence s = DateFormat.format("yyyy-MM-dd hh:mm:ss", d.getTime());
        return s.toString();
    }

    public void updateTeachersData(final String groupID, final String leadersLocation){
        DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference("Teachers");
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
                        user.setUserLocation(leadersLocation);
                        user.setSafeZone(Integer.toString(SAFE_ZONE));
                        final DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference("Teachers");
                        dBRef.child(id).setValue(user).addOnCompleteListener(QRGenActivity.this, new OnCompleteListener<Void>() {
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

    //region Button Control Methods Region #################################################################################################
    //disables the creator of the group from creating more than one group at a time
    public void ToggleQRGenBtn(boolean status){
        image.setImageBitmap(QR_CODE);
        groupID.setText(GROUP_ID);
        if(status){
            String na = "";
            groupID.setText(na);
            image.setVisibility(View.GONE);
        }
    }

    public void homeBtnClick(View view){
        startActivity(new Intent(this, HomeActivity.class));
    }

    //switches tot he membersListActivity were we display the list of members int he group
    public void switchToMemActivity(View view){
        if(GROUP_CREATED || GROUP_JOINED) {
            startActivity(new Intent(this, MembersListActivity.class));
        }else{
            print("No Group Created, Use Generate");
        }
    }

    //switches to mapsActivity were we hope to display the members as markers on the map
    public void switchToMap(View view){
        startActivity(new Intent(this, MapsActivity.class));
    }
    //endregion
}
//finito jGAT