package com.vetealinfierno.locus;
//***** 2/18/17 jGAT
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import static com.vetealinfierno.locus.HomeActivity.GROUP_CREATED;
import static com.vetealinfierno.locus.HomeActivity.GROUP_JOINED;

///this is the QR code generator activity, this class/Activity might not be necessary for the
//generating of a QR code but im new to this so im going to try it this way.
public class QRGenActivity extends AppCompatActivity {
    EditText text;
    TextView groupID;
    Button gen_btn;
    ImageView image;
    public static boolean QR_GEN =false;
    public static Bitmap QR_CODE;
    public static String TEXT2_QR;

    public void print(String s){
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
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

    //disables the creator of the group from creating more than one group at a time
    public void ToggleQRGenBtn(boolean status){
        String disabledTxt = "Disabled";
        gen_btn.setEnabled(status);
        gen_btn.setText(disabledTxt);
        image.setImageBitmap(QR_CODE);
        groupID.setText(TEXT2_QR);
        if(status){
            String generate = "Generate";
            String na = "";
            gen_btn.setEnabled(true);
            gen_btn.setText(generate);
            groupID.setText(na);
            image.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrgen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        groupID = (TextView) findViewById(R.id.groupID_text);
        text = (EditText) findViewById(R.id.text);
        gen_btn = (Button) findViewById(R.id.gen_btn);
        image = (ImageView) findViewById(R.id.image);
        gen_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                image.setVisibility(View.VISIBLE);
                TEXT2_QR = text.getText().toString().trim();
                if (!TEXT2_QR.equals("")) {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    try {
                        BitMatrix bitMatrix = multiFormatWriter.encode(TEXT2_QR, BarcodeFormat.QR_CODE, 400, 400);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        QR_CODE = barcodeEncoder.createBitmap(bitMatrix);
                        image.setImageBitmap(QR_CODE);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                    //TODO: add code to send the generated groupID to the data base to create group table
                    //TODO: GROUP_CREATED only should be set true if successfully added group ID to DB table
                    //disables join and create btns, enables members, leave, map
                    HomeActivity.GROUP_CREATED = true;
                    QR_GEN = true;
                    ToggleQRGenBtn(false);
                    //TODO: the groupID should only be displayed if successfully added group ID to DB table
                    TEXT2_QR = "Group ID: "+TEXT2_QR;
                    groupID.setText(TEXT2_QR);
                    closeKeyboard(QRGenActivity.this, gen_btn.getWindowToken());
                }else{
                    print("Error: Enter Text First");
                }
            }
        });
    }

    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

    public void homeBtnClick(View view){
        //intent is telling android what we want to do which is (swithFrom.this, to something.class)
        Intent intent = new Intent(this, HomeActivity.class);
        ///starting the activity
        startActivity(intent);
    }

    //switches tot he membersListActivity were we display the list of members int he group
    public void switchToMemActivity(View view){
        if(GROUP_CREATED || GROUP_JOINED) {
            Intent intent = new Intent(this, MembersListActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, "No Group Created, Use Generate", Toast.LENGTH_LONG).show();
        }
    }
}
//finito