package com.vetealinfierno.locus;
//***** 3/24/17 jGAT
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.vetealinfierno.locus.HomeActivity.FIRST_JOIN;
import static com.vetealinfierno.locus.HomeActivity.GROUP_CREATED;
import static com.vetealinfierno.locus.HomeActivity.GROUP_ID;
import static com.vetealinfierno.locus.HomeActivity.GROUP_JOINED;
import static com.vetealinfierno.locus.QRGenActivity.QR_GEN;

public class AccountCreationActivity extends AppCompatActivity implements View.OnClickListener{

    public void print(String s){
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    //region Class Variables Region ##########################################################################################
    private String userType;
    private Spinner spinnerUSERTYPE;
    private Button submitBtn;
    private EditText EditText_email, EditText_cEmail, EditText_password, EditText_cPassword;
    private TextView linkText;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    //endregion

    //region Android Life Cycle Region #######################################################################################
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_account_creation);

        spinnerUSERTYPE = (Spinner) findViewById(R.id.spinner_usertype);
        firebaseAuth = FirebaseAuth.getInstance();
        submitBtn = (Button) findViewById(R.id.submit_btn);
        EditText_email = (EditText) findViewById(R.id.editText_email);
        EditText_cEmail = (EditText) findViewById(R.id.editText_cEmail);
        EditText_password = (EditText) findViewById(R.id.editText_password);
        EditText_cPassword = (EditText) findViewById(R.id.editText_cPassword);
        progressDialog = new ProgressDialog(this);
        linkText = (TextView) findViewById(R.id.textView_message);
        submitBtn.setOnClickListener(this);
        linkText.setOnClickListener(this);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    //Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    //Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }
    //endregion

    //region Register New User Methods Region ################################################################################
    private void registerUser(){
        String email = EditText_email.getText().toString().trim();
        String cEmail = EditText_cEmail.getText().toString().trim();
        String password = EditText_password.getText().toString().trim();
        String cPassword = EditText_cPassword.getText().toString().trim();
        userType = spinnerUSERTYPE.getSelectedItem().toString();
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(cEmail) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(cPassword)){
            //fields are empty
            print("Please Enter Email or Password");
        }else if(userType.contains("Choose")){
            print("Please answer the final question");
        }else if(email.equals(cEmail) && password.equals(cPassword)){
            progressDialog.setMessage("Registering User...");
            progressDialog.show();
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if(task.isSuccessful()){
                                print("Successful Registration...");
                                if(userType.equals("Yes")){
                                    addTeacher();
                                }else{
                                    addStudent();
                                }
                            }
                        }
                    }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    print(e.getMessage());
                }
            });
        }else if(!email.equals(cEmail)){
            print("Emails do NOT match...");
        }else if(!password.equals(cPassword)){
            print("Passwords do NOT match...");
        }
    }

    public void addStudent(){
        GROUP_JOINED = false;
        GROUP_ID = "";
        FIRST_JOIN = true;
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String email = user.getEmail();
        DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference("Students");
        String id = dBRef.push().getKey();
        UserInfo userInfo = new UserInfo(id, "Null", email, "No", "Null", "Null", "Null");
        dBRef.child(id).setValue(userInfo).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    public void addTeacher(){
        GROUP_CREATED = false;
        GROUP_ID = "";
        QR_GEN = false;
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String email = user.getEmail();
        DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference("Teachers");
        String id = dBRef.push().getKey();
        UserInfo userInfo = new UserInfo(id, "Null", email, "No", "Null", "Null", "Null");
        dBRef.child(id).setValue(userInfo).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });

    }
    //endregion

    //region Button OnClick Method Region ####################################################################################
    @Override
    public void onClick(View view){
        if(view == submitBtn){
            registerUser();
        }
        if(view == linkText){
            //open login activity
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
    //endregion

}
//finito jGAT