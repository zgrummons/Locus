package com.vetealinfierno.locus;
//***** 2/18/17 jGAT
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
///this is the QR code generator activity, this class/Activity might not be necessary for the
//generating of a QR code but im new to this so im going to try it this way.
public class QRGenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrgen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    //TODO: add code to generate and display the QR code
    //TODO: add code to send the generated group idea to the data base to create group table
}
//finito