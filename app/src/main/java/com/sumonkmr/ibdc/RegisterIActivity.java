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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class RegisterIActivity extends AppCompatActivity {

    EditText fName,lName,email,pass;
    AutoCompleteTextView birthDate_reg;
    int d,m,y;
    Button nextButtonII;
    com.airbnb.lottie.LottieAnimationView loadingAim1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_i);
        initializeComponents();
        birthDate_reg.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            y = calendar.get(Calendar.YEAR);
            m = calendar.get(Calendar.MONTH);
            d = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterIActivity.this, new DatePickerDialog.OnDateSetListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    birthDate_reg.setText(dayOfMonth+ "/"+(month+1)+"/"+year);
                }
            },y,m,d);
            datePickerDialog.show();
        });
    }

    private void initializeComponents() {
        fName = findViewById(R.id.fNameInput);
        lName = findViewById(R.id.lNameInput);
        email = findViewById(R.id.emailInput);
        pass = findViewById(R.id.passInput);
        birthDate_reg = findViewById(R.id.birthDate_reg);
        nextButtonII = findViewById(R.id.nextButtonII);
        loadingAim1 = findViewById(R.id.loadingAim1);

    }

    public void nextRegisterPage(View view) {
        String f_name,l_name,emailText,passText,birthdateText;
        f_name = fName.getText().toString();
        l_name = lName.getText().toString();
        emailText = email.getText().toString().toLowerCase();
        passText = pass.getText().toString();
        birthdateText = birthDate_reg.getText().toString();

        if(f_name.isEmpty()){
            fName.setError("Fill this field.");
        }
        if(emailText.isEmpty()){
            email.setError("Fill this field.");
        }
        if (l_name.isEmpty()){
            lName.setError("Fill this field.");
        }
        if(passText.isEmpty()){
            pass.setError("Fill this field.");
        }
        if(birthdateText.isEmpty()){
            pass.setError("Fill this field.");
        }

        if(! f_name.isEmpty() && ! l_name.isEmpty() && ! emailText.isEmpty() && ! passText.isEmpty()){
            RegisterUser(f_name,l_name,emailText,passText,birthdateText);
        }

    }



    private void RegisterUser(String f_name, String l_name, String emailText, String passText, String birthdateText) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailText,passText)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        addToDatabase(task.getResult().getUser().getUid(),f_name,l_name,emailText,birthdateText);
                    }else {
                        Toast.makeText(RegisterIActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void addToDatabase(String uid, String f_name, String l_name, String emailText, String birthdate) {

        loadingAim1.setVisibility(View.VISIBLE);
        HashMap<String,String>values = new HashMap<>();
        values.put("FName",f_name);
        values.put("LName",l_name);
        values.put("Email",emailText);
        values.put("UID",uid);
        values.put("Step","1");
        values.put("Visible","False");
        values.put("RequestBlood","False");
        values.put("State","None");
        values.put("District","None");
        values.put("Upazila","None");
        values.put("Village","None");
        values.put("Mobile","None");
        values.put("BloodGroup","None");
        values.put("lastDonateDate","None");
        values.put("birthdate",birthdate);
        values.put("bloodImg_url","None");

        FirebaseDatabase.getInstance().getReference("Donors")
                .child(uid).setValue(values).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        loadingAim1.setVisibility(View.GONE);
                        startActivity(new Intent(RegisterIActivity.this,RegisterIIActivity.class));
                    }
                });


    }



}