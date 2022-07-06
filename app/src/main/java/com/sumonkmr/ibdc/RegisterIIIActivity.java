package com.sumonkmr.ibdc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class RegisterIIIActivity extends AppCompatActivity {


    AutoCompleteTextView bloodgrp;

    EditText mobile,textVerification;
    Button submit;
    String lastDonateDate;
    ImageView bloodImg;


    boolean isVerified = false, isSubmit = false;

    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_i_i_i);

        initializeComponents();

        String[] bloodGroups = getResources().getStringArray(R.array.blood_groups);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,bloodGroups);

        bloodgrp.setAdapter(adapter);
    }

    private void initializeComponents() {
        bloodgrp = findViewById(R.id.bloodGrpDropDown);
        mobile = findViewById(R.id.mobileEditText);
        textVerification = findViewById(R.id.verificationText);
        submit = findViewById(R.id.btnVerifySubmit);
        lastDonateDate = getString(R.string.last_donate_date);
        bloodImg = findViewById(R.id.bloodImg);
    }


    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            textVerification.setText(R.string.verified);
            addToDatabase();
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
               textVerification.setText("Failed!");
            } else if (e instanceof FirebaseTooManyRequestsException) {
                textVerification.setText("Message Quota Exceeded!\nTry Again After few Hours!");
            }

           mobile.setEnabled(true);

        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {

            textVerification.setHint("Enter Otp!!");
            submit.setText(R.string.submit);
            id = verificationId;
            isSubmit = true;

        }

    };

    private void addToDatabase() {

        HashMap<String,Object> values = new HashMap<>();
        values.put("Step","Done");
        values.put("Mobile",mobile.getText().toString());
        values.put("BloodGroup",bloodgrp.getText().toString());
        values.put("lastDonateDate",lastDonateDate);
        values.put("bloodImg",bloodImg);
        values.put("Visible","True");
        FirebaseDatabase.getInstance().getReference("Donors/"+FirebaseAuth.getInstance().getUid())
                .updateChildren(values)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(RegisterIIIActivity.this,DashBoard.class));
                            RegisterIIIActivity.this.finish();
                        }else {
                            Toast.makeText(RegisterIIIActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void verifyAndSubmit(View view) {

        mobile.setEnabled(false);
        if(!isSubmit) {
            if (!isVerified && !mobile.getText().toString().isEmpty() && !bloodgrp.getText().toString().isEmpty()) {
                PhoneAuthOptions options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                        .setPhoneNumber("+88" + mobile.getText().toString())
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(RegisterIIIActivity.this)
                        .setCallbacks(mCallbacks)
                        .build();

                PhoneAuthProvider.verifyPhoneNumber(options);
                textVerification.setText(R.string.verifying);
            }
            if (mobile.getText().toString().isEmpty()) {
                mobile.setError("Enter Mobile Number!");
            }
        }else {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id,textVerification.getText().toString());
            FirebaseAuth.getInstance().getCurrentUser().linkWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                addToDatabase();
                            }else {
                                Toast.makeText(RegisterIIIActivity.this, "Error!\n"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                submit.setText(R.string.verify_txt);
                                mobile.setEnabled(true);
                                textVerification.setText(R.string.not_verified);
                                isVerified = false;
                                isSubmit = false;
                            }
                        }
                    });
        }


    }
}