package com.vetealinfierno.locus;
//***** 2/18/17 jGAT
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import static com.vetealinfierno.locus.HomeActivity.GROUP_CREATED;
import static com.vetealinfierno.locus.HomeActivity.GROUP_JOINED;
import static com.vetealinfierno.locus.HomeActivity.USER_ID;
import static com.vetealinfierno.locus.JoinActivity.GROUP_ID;

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
                    //disables join and create btns, enables members, leave, map
                    HomeActivity.GROUP_CREATED = true;
                    //creates the group
                    createGroup(TEXT2_QR);
                    QR_GEN = true;
                    ToggleQRGenBtn(false);
                    String temp = "Group ID: "+TEXT2_QR;
                    groupID.setText(temp);
                    closeKeyboard(QRGenActivity.this, gen_btn.getWindowToken());
                }else{
                    print("Error: Enter Text First");
                }
            }
        });
    }

    public void createGroup(final String groupID){
        DatabaseReference dBRef;
        dBRef = FirebaseDatabase.getInstance().getReference(groupID);
        String id = dBRef.push().getKey();
        USER_ID = id;
        LatLng latLng = MapsActivity.mLatLng;
        Double lat = latLng.latitude;
        Double log = latLng.longitude;
        String leadersLocation = lat.toString() +", "+ log.toString();
        UserInfo user = new UserInfo(id, leadersLocation);
        dBRef.child(id).setValue(user).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(), "Group: "+groupID+" Created", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

    public void homeBtnClick(View view){
        startActivity(new Intent(this, HomeActivity.class));
    }

    //switches tot he membersListActivity were we display the list of members int he group
    public void switchToMemActivity(View view){
        if(GROUP_CREATED || GROUP_JOINED) {
            startActivity(new Intent(this, MembersListActivity.class));
        }else{
            Toast.makeText(this, "No Group Created, Use Generate", Toast.LENGTH_LONG).show();
        }
    }

    //switches to mapsActivity were we hope to display the members as markers on the map
    public void switchToMap(View view){
        startActivity(new Intent(this, MapsActivity.class));
    }
}
//finito