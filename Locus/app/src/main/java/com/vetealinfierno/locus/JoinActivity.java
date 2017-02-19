package com.vetealinfierno.locus;
//***** 2/18/17 jGAT
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

//this is the joinActivity serves as the join screen for the user
//this screen will need the scanQRcode_button (pulls up camera to scan QRcode)
// and a textField for manually entering GroupID
//it will also need a joinButton for the manual entry of groupID
public class JoinActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
    }



    public void UpdateNewMemMethod(View view){
        //TODO: add code that receives the input form the manual GROUPID input field then update DB with new member
    }

    public void ScanQRCodeMethod(View view){
        //TODO: add code that access the devices camera for scanning of a QR code
    }

}
//finito