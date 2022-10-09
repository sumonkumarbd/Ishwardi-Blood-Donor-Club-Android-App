package com.sumonkmr.ibdc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GoogleSignInAndSignUP extends AppCompatActivity {

    private static final int REQUEST_CODE = 1000;
    SignInButton google_signIn;
    TextView g_signUp;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    FirebaseAuth mAuth;
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference dbReference;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_sign_in_and_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("bn");
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        google_signIn = findViewById(R.id.google_signIn);
        g_signUp = findViewById(R.id.g_signUp);
        DialogForSignUp();

        google_signIn.setOnClickListener(v -> {
            GoogleSignIn();
        });



    }

    private void GoogleSignUp() {

    }

    private void GoogleSignIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                dialog.show();
            } catch (ApiException e) {
                Toast.makeText(this, "গুগল সাইনাপ ব্যার্থ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void DashBoardPass() {
        finish();
        Intent intent = new Intent(new Intent(this, DashBoard.class));
        startActivity(intent);
    }



    private void DialogForSignUp(){
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.signupdialog);
        Button ok_Btn,cBtn;
        EditText form_Email;
        String uEmail;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_background));
        }

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

        ok_Btn = dialog.findViewById(R.id.ok_Btn);
        cBtn = dialog.findViewById(R.id.cBtn);
        form_Email = dialog.findViewById(R.id.form_Email);

        GoogleSignInAccount userAcc = GoogleSignIn.getLastSignedInAccount(this);
        if (userAcc != null){
            uEmail = userAcc.getEmail();
            form_Email.setText(uEmail);

        }




        ok_Btn.setOnClickListener(v -> {

        });

        cBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });



    }
}