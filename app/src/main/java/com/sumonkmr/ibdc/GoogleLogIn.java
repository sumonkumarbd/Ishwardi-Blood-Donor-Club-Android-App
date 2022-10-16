package com.sumonkmr.ibdc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

public class GoogleLogIn extends AppCompatActivity {

    private static final int REQUEST_CODE = 2;
    ImageButton google_signIn;
    LottieAnimationView loadingAim;
    TextView g_signUp;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    GoogleSignInAccount account;
    FirebaseAuth mAuth;
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference dbReference;
    Dialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_log_in);

        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("bn");
        ProcessRequest();
        OneTapClientProcess();

        google_signIn = findViewById(R.id.google_signIn);
        loadingAim = findViewById(R.id.loadingAim);


        google_signIn.setOnClickListener(v -> {
            LoginProcess();
        });

    }//onCreate


    private void ProcessRequest() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this,gso);
    }

    private void LoginProcess() {
        loadingAim.setVisibility(View.VISIBLE);
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent,REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            Task<GoogleSignInAccount>task =GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                    // Got an ID token from Google. Use it to authenticate
                    // with Firebase.
                    GoogleFirebaseAuth(account.getIdToken());

            } catch (ApiException e) {
                // ...
                Toast.makeText(this, "গুগল একাউন্ট সঠিক নয়!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void GoogleFirebaseAuth(String idToken) {
        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(GoogleLogIn.this, "লগ ইন সফল!", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(getApplicationContext(),SplashScreen.class));
                            loadingAim.setVisibility(View.INVISIBLE);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(GoogleLogIn.this, "লগ ইন ফেল!", Toast.LENGTH_SHORT).show();
                            loadingAim.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    private void OneTapClientProcess() {

        // ...
        SignInClient oneTapClient = Identity.getSignInClient(this);
        // Your server's client ID, not your Android client ID.
        // Only show accounts previously used to sign in.
        // Automatically sign in when exactly one credential is retrieved.
        BeginSignInRequest signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();
        // ...
    }


    private void DialogForSignUp(){
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.signupdialog);
        Button ok_Btn,cBtn;
        EditText gName,gEmail;
        String uEmail;

        account = GoogleSignIn.getLastSignedInAccount(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_background));
        }

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

        ok_Btn = dialog.findViewById(R.id.ok_Btn);
        cBtn = dialog.findViewById(R.id.cBtn);
        gEmail = dialog.findViewById(R.id.gEmail);
        gName = dialog.findViewById(R.id.gName);
        uEmail = account.getEmail();
        Uri photoUrl = account.getPhotoUrl();
        gEmail.setHint(uEmail);
        gName.setText(account.getDisplayName());
        gEmail.setText(uEmail);





        ok_Btn.setOnClickListener(v -> {
            startActivity(new Intent(this,DashBoard.class));
        });

        cBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });



    }

}//Root