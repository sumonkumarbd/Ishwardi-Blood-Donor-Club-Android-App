package com.sumonkmr.ibdc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RegisterIIIActivity extends AppCompatActivity {


    AutoCompleteTextView bloodgrp,lastDonateDate_reg;
    de.hdodenhof.circleimageview.CircleImageView profile_image_reg,p_image_shade_reg;
    EditText mobile,textVerification;
    Button submit;
    com.airbnb.lottie.LottieAnimationView loadingAim3;
    final Context  context = RegisterIIIActivity.this;
    boolean isVerified = false, isSubmit = false;
    TextView profile_image_hint;
    CheckBox lastDonate_check;
    Uri filepath;
    Bitmap bitmap;
    String userId,otpid;
    Uri profile_uri;
    int d,m,y;
    private StorageReference storageReference;
    private DatabaseReference dbReference;
    private FirebaseDatabase db;
    private ProgressBar progressbar;
    String id;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_i_i_i);
        initializeComponents();
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("bn");
        storageReference = FirebaseStorage.getInstance().getReference();
        p_image_shade_reg.setOnClickListener(v -> {
            Dexter.withContext(context)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Browse For Image"), 101);
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).check();
        });

        lastDonate_check.setOnCheckedChangeListener((buttonView, isChecked) ->
                lastDonateDate_reg.setEnabled(!isChecked)

        );


        final Calendar calendar = Calendar.getInstance();
        lastDonateDate_reg.setOnClickListener(v -> {
            y = calendar.get(Calendar.YEAR);
            m = calendar.get(Calendar.MONTH);
            d = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterIIIActivity.this, new DatePickerDialog.OnDateSetListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    lastDonateDate_reg.setText(dayOfMonth+ "/"+(month+1)+"/"+year);
                }
            },y,m,d);
            datePickerDialog.show();
        });

        String[] bloodGroups = getResources().getStringArray(R.array.blood_groups);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,bloodGroups);

        bloodgrp.setAdapter(adapter);
    }//onCreate

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 101 && resultCode == -1) {
            assert data != null;
            filepath = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(filepath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                profile_image_reg.setImageBitmap(bitmap);
                processImageUpload();
            } catch (Exception ex) {
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private String getExtention(){
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(context.getContentResolver().getType(filepath));
    }

    private void processImageUpload(){
        final StorageReference uploader = storageReference.child(String.format("profile_image/User Id : %s/profile_picture.%s",userId,getExtention()));
        uploader.putFile(filepath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(context, R.string.Updated_img, Toast.LENGTH_SHORT).show();
                        uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                if (!filepath.toString().isEmpty()){
                                    profileImg(uri);
                                }
                            }
                        });
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        long per = (100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                        progressbar.setProgress((int) per);
                        progressbar.setMax(100);
                        Toast.makeText(context, R.string.updating, Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void profileImg(Uri uri){
        HashMap<String,Object> values = new HashMap<>();
        values.put("bloodImg_url",uri.toString());
        FirebaseDatabase.getInstance().getReference("Donors/"+mAuth.getUid())
                .updateChildren(values)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context, R.string.Updated, Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initializeComponents() {
        bloodgrp = findViewById(R.id.bloodGrpDropDown);
        mobile = findViewById(R.id.mobileEditText);
        textVerification = findViewById(R.id.verificationText);
        submit = findViewById(R.id.btnVerifySubmit);
        lastDonateDate_reg = findViewById(R.id.lastDonateDate_reg);
        profile_image_reg = findViewById(R.id.profile_image_reg);
        p_image_shade_reg = findViewById(R.id.p_image_shade_reg);
        profile_image_hint = findViewById(R.id.profile_image_hint);
        progressbar = findViewById(R.id.progressbar_reg);
        loadingAim3 = findViewById(R.id.loadingAim3);
        lastDonate_check = findViewById(R.id.lastDonate_check);
    }



    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            textVerification.setText(id);
            addToDatabase();
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
               textVerification.setText("অসমাপ্ত!");
            } else if (e instanceof FirebaseTooManyRequestsException) {
                textVerification.setText("কিছুক্ষন পর আবার চেষ্টা করুন...");
            }

           mobile.setEnabled(true);

        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {

            textVerification.setHint(R.string.enter_Otp);
            submit.setText(R.string.submit);
            id = verificationId;
            isSubmit = true;

        }

    };

    private void addToDatabase() {
        loadingAim3.setVisibility(View.VISIBLE);
        HashMap<String,Object> values = new HashMap<>();
        values.put("Step","Done");
        values.put("Mobile",mobile.getText().toString());
        values.put("BloodGroup",bloodgrp.getText().toString());
        if (lastDonate_check.isChecked()){
            values.put("lastDonateDate","পূর্বে করিনি।");
        }else {
            values.put("lastDonateDate",lastDonateDate_reg.getText().toString());
        }
        values.put("Visible","True");
        FirebaseDatabase.getInstance().getReference("Donors/"+mAuth.getUid())
                .updateChildren(values)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            loadingAim3.setVisibility(View.GONE);
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
                PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+88" + mobile.getText().toString())
                        .setTimeout(15L, TimeUnit.SECONDS)
                        .setActivity(RegisterIIIActivity.this)
                        .setCallbacks(mCallbacks)
                        .build();

                PhoneAuthProvider.verifyPhoneNumber(options);
                textVerification.setHint(R.string.verifying);
            }
            if (mobile.getText().toString().isEmpty()) {
                mobile.setError("Enter Mobile Number!");
            }
        }else {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id,textVerification.getText().toString());
            Objects.requireNonNull(mAuth.getCurrentUser()).linkWithCredential(credential)
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