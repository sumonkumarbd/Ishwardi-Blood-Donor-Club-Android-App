package com.sumonkmr.ibdc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetHost;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sumonkmr.ibdc.model.User;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LoginScreenActivity extends AppCompatActivity {


    EditText Email,Pass,phn_logIn,passLogin_p;
    Button login,logon_btn_p,login_submit;
    LottieAnimationView loadingAim;
    androidx.appcompat.widget.SwitchCompat logSwitch;
    LinearLayout emailLgn,phnLgn;
    FirebaseAuth mAuth;
    boolean isVerified = false, isSubmit = false;
    String s_phn_logIn,s_passLogin_p,id;
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference dbReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        mAuth = FirebaseAuth.getInstance();
        
        Email = findViewById(R.id.emailLogin);
        Pass = findViewById(R.id.passLogin);
        login = findViewById(R.id.logon_btn);
        logon_btn_p = findViewById(R.id.logon_btn_p);
        login_submit = findViewById(R.id.login_submit);
        loadingAim = findViewById(R.id.loadingAim);
        logSwitch = findViewById(R.id.logSwitch);
        emailLgn = findViewById(R.id.emailLgn);
        phnLgn = findViewById(R.id.phnLgn);
        phn_logIn = findViewById(R.id.phn_logIn);
        passLogin_p = findViewById(R.id.passLogin_p);
        s_phn_logIn = phn_logIn.getText().toString();
        s_passLogin_p = passLogin_p.getText().toString();
        loadingAim.setVisibility(View.GONE);

        logSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                emailLgn.setVisibility(View.GONE);
                phnLgn.setVisibility(View.VISIBLE);
                login.setVisibility(View.GONE);
                logon_btn_p.setVisibility(View.VISIBLE);
            }else{
                phnLgn.setVisibility(View.GONE);
                emailLgn.setVisibility(View.VISIBLE);
                login.setVisibility(View.VISIBLE);
                logon_btn_p.setVisibility(View.GONE);
                login_submit.setVisibility(View.GONE);
            }
        });

        login.setOnClickListener(v-> {
            LoginWithEmail();
        });

        logon_btn_p.setOnClickListener(v-> {
            initOpt();
        });


        login_submit.setOnClickListener(view -> {

            if(passLogin_p.getText().toString().isEmpty())
                Toast.makeText(getApplicationContext(),"Blank Field can not be processed",Toast.LENGTH_LONG).show();
            else if(passLogin_p.getText().toString().length()!=6)
                Toast.makeText(getApplicationContext(),"INvalid OTP",Toast.LENGTH_LONG).show();
            else
            {
                PhoneAuthCredential credential=PhoneAuthProvider.getCredential(id,passLogin_p.getText().toString());
                signInWithPhoneAuthCredential(credential);
            }

        });




    }//OnCreate

    public void OpenRegisterActivity(View view) {
        startActivity(new Intent(LoginScreenActivity.this,RegisterIActivity.class));
    }


    public void LoginWithEmail() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String s_email = Email.getText().toString();
        String s_pass = Pass.getText().toString();
        if (!s_email.matches(emailPattern)){
            Toast.makeText(this, "Please Enter a Valid Email...", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(s_pass)){
            Toast.makeText(this, "Enter a Valid Password!!", Toast.LENGTH_SHORT).show();
        }

        if(!s_email.isEmpty() && !s_pass.isEmpty()){
            FirebaseAuth.getInstance().signInWithEmailAndPassword(Email.getText().toString(),Pass.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                loadingAim.setVisibility(View.VISIBLE);
                                startActivity(new Intent(LoginScreenActivity.this,SplashScreen.class));
                            }else {
                                Toast.makeText(LoginScreenActivity.this, "Incorrect Email or Password!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }



    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                passLogin_p.setHint("অসমাপ্ত!");
            } else if (e instanceof FirebaseTooManyRequestsException) {
                passLogin_p.setHint("কিছুক্ষন পর আবার চেষ্টা করুন...");
            }

            phn_logIn.setEnabled(true);

        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {

            passLogin_p.setHint(R.string.enter_Otp);
            logon_btn_p.setText(R.string.submit);
            id = verificationId;
            isSubmit = true;

        }

    };


    private void initOpt(){
        logon_btn_p.setVisibility(View.GONE);
        login_submit.setVisibility(View.VISIBLE);
        if(!phn_logIn.getText().toString().isEmpty()  && passLogin_p.getText().toString().isEmpty() && phn_logIn.getText().toString().length() == 11){
                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(mAuth)
                                    .setPhoneNumber("+88" + phn_logIn.getText().toString())       // Phone number to verify
                                    .setTimeout(15L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(this)                 // Activity (for callback binding)
                                    .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                    loadingAim.setVisibility(View.VISIBLE);

        }else {
            Toast.makeText(this, "ভ্যালিড মোবাইল নাম্বার দিন...", Toast.LENGTH_SHORT).show();
            logon_btn_p.setVisibility(View.VISIBLE);
            login_submit.setVisibility(View.GONE);
        }


    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = task.getResult().getUser();
                            assert user != null;
                            if (user.getEmail() != null){
                                startActivity(new Intent(LoginScreenActivity.this,SplashScreen.class));
                                finish();
                            }else {
                                user.delete();
                                Toast.makeText(LoginScreenActivity.this, "এই নম্বর টি নিবন্ধিত নয়!", Toast.LENGTH_SHORT).show();
                                Handler handler = new Handler();
                                Runnable runnable = () -> {
                                    startActivity(new Intent(LoginScreenActivity.this,RegisterIActivity.class));
                                };
                                handler.postDelayed(runnable,4000);
                            }

                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(LoginScreenActivity.this, "Please Give Us Valid Information's!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }//signInWithPhoneAuthCredential

}//root