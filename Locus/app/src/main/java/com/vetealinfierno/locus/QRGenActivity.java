package com.vetealinfierno.locus;
//***** 2/18/17 jGAT
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

///this is the QR code generator activity, this class/Activity might not be necessary for the
//generating of a QR code but im new to this so im going to try it this way.
public class QRGenActivity extends AppCompatActivity {
    public static boolean GROUP_CREATED = false;
    EditText text;
    TextView groupID;
    Button gen_btn;
    ImageView image;
    String text2Qr;


    public void print(String s){
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
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
                text2Qr = text.getText().toString().trim();
                if (!text2Qr.equals("")) {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    try {
                        BitMatrix bitMatrix = multiFormatWriter.encode(text2Qr, BarcodeFormat.QR_CODE, 400, 400);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        image.setImageBitmap(bitmap);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                    //TODO: GROUP_CREATED only should be set true if successfully added group ID to DB table
                    GROUP_CREATED = true;
                    //TODO: the groupID should only be displayed if successfully added group ID to DB table
                    text2Qr = "Group ID: "+text2Qr;
                    groupID.setText(text2Qr);
                }else{
                    print("Error: Enter Text First");
                }
            }

        });

    }


    //TODO: add code to send the generated groupID to the data base to create group table
}
//finito