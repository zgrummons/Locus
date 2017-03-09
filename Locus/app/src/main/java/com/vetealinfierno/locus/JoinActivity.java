package com.vetealinfierno.locus;
//***** 2/18/17 jGAT
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
//this is the joinActivity serves as the join screen for the user
//this screen will need the scanQRcode_button (pulls up camera to scan QRcode)
// and a textField for manually entering GroupID
//it will also need a joinButton for the manual entry of groupID
public class JoinActivity extends AppCompatActivity {
    EditText groupID;
    Button joinBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        groupID = (EditText) findViewById(R.id.GroupID_editText);
        joinBtn = (Button) findViewById(R.id.join_Button);
    }

    ///enters the group id manually and button is clicked
    public void UpdateNewMemMethod(View view){
        String groupIDString = groupID.getText().toString();
        if(!groupIDString.equals("") && groupIDString.length()<5) {
            //TODO: add update DB table with groupIDInt (manual input)
            //TODO: use a method so both manual input and scanner can use it ^^^^
            int groupIDInt = Integer.parseInt(groupIDString);
            Toast.makeText(this, "Group ID = " + groupIDInt, Toast.LENGTH_LONG).show();
            //enables the buttons for the students if the successfully joined a group
            HomeActivity.GROUP_JOINED = true;
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(this, "ERROR: Enter a GroupID", Toast.LENGTH_LONG).show();
        }
    }

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
                Toast.makeText(this, "You Cancelled the scanning", Toast.LENGTH_LONG).show();
            }else{
                //TODO: add code that takes the data from result.getContents() and adds it to DB table
                //TODO: use a method so both manual input and scanner can use it ^^^^
                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                //enables the buttons for the students if the successfully joined a group
                //and disables join and create btns
                HomeActivity.GROUP_JOINED = true;
                Intent intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void switchToHomeActivity(View view){
        //this starts a new activity if needed for generating QR code
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

}
//finito