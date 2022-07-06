package com.sumonkmr.ibdc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginScreenActivity extends AppCompatActivity {


    EditText Email,Pass;
    Button login;
    LottieAnimationView loadingAim;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        
        Email = findViewById(R.id.emailLogin);
        Pass = findViewById(R.id.passLogin);
        login = findViewById(R.id.logon_btn);
        loadingAim = findViewById(R.id.loadingAim);

        validExpressions();

    }

    private void validExpressions() {

        login.setOnClickListener(v -> {
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


        });
    }

    public void OpenRegisterActivity(View view) {
        startActivity(new Intent(LoginScreenActivity.this,RegisterIActivity.class));
    }


    public void LoginNow(View view) {
//        if(!Email.getText().toString().isEmpty() && !Pass.getText().toString().isEmpty()){
//            FirebaseAuth.getInstance().signInWithEmailAndPassword(Email.getText().toString(),Pass.getText().toString())
//                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if(task.isSuccessful()){
//                                startActivity(new Intent(LoginScreenActivity.this,SplashScreen.class));
//                            }else {
//                                Toast.makeText(LoginScreenActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//        }
    }
}