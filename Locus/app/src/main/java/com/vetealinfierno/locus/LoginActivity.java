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
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public void print(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    //region Class Variables Region ##########################################################################################################
    private Button signInBtn;
    private EditText EditText_email, EditText_password;
    private TextView TextView_signUp;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    public static boolean LOGIN = false;
    //endregion

    //region Android Life Cycle Region #######################################################################################################
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            //start home activity
            switchToHomeActivity();
        }
        signInBtn = (Button) findViewById(R.id.signIn_btn_login);
        EditText_email = (EditText) findViewById(R.id.editText_email_login);
        EditText_password = (EditText) findViewById(R.id.editText_password_login);
        TextView_signUp = (TextView) findViewById(R.id.textView_SignUp_login);

        signInBtn.setOnClickListener(this);
        TextView_signUp.setOnClickListener(this);
    }
    //endregion

    //region UserLogin Method Region #########################################################################################################
    private void userLogin(){
        String email = EditText_email.getText().toString().trim();
        String password = EditText_password.getText().toString().trim();
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            //fields are empty
            print("Please Enter Email or Password");
            return;
        }
        progressDialog.setMessage("Logging in...");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    print("Successful Login...");
                    //start the profile activity
                    switchToHomeActivity();
                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                print(e.getMessage());
            }
        });
    }
    //endregion

    //region Button Control Region ###########################################################################################################
    @Override
    public void onClick(View view){
        if(view == signInBtn){
            userLogin();
        }
        if(view == TextView_signUp){
            finish();
            startActivity(new Intent(this, AccountCreationActivity.class));
        }
    }

    public void switchToHomeActivity(){
        finish();
        LOGIN = true;
        startActivity(new Intent(this, HomeActivity.class));
    }
    //endregion
}
//finito jGAT
