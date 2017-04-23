package com.vetealinfierno.locus;
//***** 2/18/17 jGAT
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import static com.vetealinfierno.locus.HomeActivity.GROUP_ID;
import static com.vetealinfierno.locus.HomeActivity.GROUP_JOINED;
import static com.vetealinfierno.locus.HomeActivity.SAFE_ZONE;

//this is the joinActivity serves as the join screen for the user
//this screen will need the scanQRcode_button (pulls up camera to scan QRcode)
// and a textField for manually entering GroupID
//it will also need a joinButton for the manual entry of groupID
public class JoinActivity extends AppCompatActivity implements View.OnClickListener{

    public void print(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    //region Class Variables Region #########################################################################################################
    private EditText groupID;
    private Button joinBtn;
    public boolean GROUP_ID_EXISTS = false;
    private ProgressDialog pD;
    //endregion

    //region Android Life Cycle Methods Region ##############################################################################################
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        groupID = (EditText) findViewById(R.id.GroupID_editText);
        joinBtn = (Button) findViewById(R.id.manJoin_Button);
        pD = new ProgressDialog(this);

        joinBtn.setOnClickListener(this);
    }
    //endregion

    //region Scanning QRCode Methods Region #################################################################################################
    ///the scanning of the QR code
    public void ScanQRCodeMethod(View view){
        final Activity activity = this;
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan QR Code");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    //method for the result of the QR code scan
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents() == null){
                print("You Cancelled the scanning");
            }else{
                GROUP_ID = result.getContents();
                //enables the buttons for the students if the successfully joined a group
                //and disables join and create btns
                setGroupJoined();
                switchToMaps();
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    //endregion

    //region Manual Input Methods Region ####################################################################################################
    ///enters the group id manually and button is clicked
    public void UpdateNewMemMethod(){
        final String groupIDString = groupID.getText().toString();
        doesGroupIDExist(groupIDString);
        pD.setMessage("Searching for GroupID...");
        pD.show();
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                if(!groupIDString.equals("") && groupIDString.length()<8 && GROUP_ID_EXISTS) {
                    GROUP_ID = groupIDString;
                    //enables the buttons for the students if the successfully joined a group
                    setGroupJoined();
                    switchToMaps();
                }else{
                    print("ERROR: Enter a VALID GroupID");
                }
                pD.dismiss();
            }
        },2000);
    }

    public void doesGroupIDExist(final String groupID){
        DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference("Groups");
        dBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference("Groups");
                for(DataSnapshot groupSnapShot: dataSnapshot.getChildren()){
                    GroupInfo groupInfo = groupSnapShot.getValue(GroupInfo.class);
                    if(groupID.equals(groupInfo.getGroupID())){
                        SAFE_ZONE = Integer.parseInt(groupInfo.getSafeZone());
                        dBRef.removeEventListener(this);
                        setGroupExists(true);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //endregion

    //region Setters Methods Region #########################################################################################################
    public void setGroupJoined(){
        GROUP_JOINED = true;
    }

    public void setGroupExists(boolean in){
        GROUP_ID_EXISTS = in;
        //print(""+GROUP_ID_EXISTS);
    }
    //endregion

    //region Button Controls/OnClick Methods Region #########################################################################################
    @Override
    public void onClick(View view){
        if(view == joinBtn){
            UpdateNewMemMethod();
        }
    }

    public void switchToMaps(){
        startActivity(new Intent(this, MapsActivity.class));
        finish();
    }

    public void switchToHomeActivity(View view){
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
    //endregion

}
//finito jGAT