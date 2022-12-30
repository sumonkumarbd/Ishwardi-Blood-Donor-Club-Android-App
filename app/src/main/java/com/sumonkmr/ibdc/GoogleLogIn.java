package com.sumonkmr.ibdc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
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
    RelativeLayout edit_tabs;
    ImageView imageView,logo,headerImg,bloodDrop,bloodRise;
    TextView google_signIn;
    LottieAnimationView loadingAim,heartAnim;
    TextView g_signUp;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    GoogleSignInAccount account;
    FirebaseAuth mAuth;
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference dbReference;
    Dialog dialog;
    TextView textView7;
    SwipeRefreshLayout loginReload;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_log_in);

        //For Sounds
        SoundManager soundManager = new SoundManager(this);
        soundManager.national_aunt.start();

        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("bn");
        ProcessRequest();
        OneTapClientProcess();
        //for initialize
        Init();
        //for start Animations
        PlayAnim();

        loginReload.setOnRefreshListener(() -> {
            soundManager.national_aunt.start();
            PlayAnim();
            loginReload.setRefreshing(false);
            heartAnim.playAnimation();
        });

        google_signIn.setOnClickListener(v -> {
            LoginProcess();
        });


    }//onCreate

    private void Init() {
        loginReload = findViewById(R.id.loginReload);
        google_signIn = findViewById(R.id.google_signIn);
        loadingAim = findViewById(R.id.loadingAim);
        edit_tabs = findViewById(R.id.edit_tabs);
        imageView = findViewById(R.id.imageView);
        logo = findViewById(R.id.logo);
        heartAnim = findViewById(R.id.heartAnim);
        headerImg = findViewById(R.id.headerImg);
        bloodDrop = findViewById(R.id.bloodDrop);
        bloodRise = findViewById(R.id.bloodRise);
    }

    private void PlayAnim() {
        //For Animations
        YoYo.with(Techniques.FadeIn).delay(0).repeat(0).duration(1300).playOn(edit_tabs);
        YoYo.with(Techniques.FadeInDown).delay(0).repeat(0).duration(1300).playOn(headerImg);
        YoYo.with(Techniques.FadeInRight).delay(0).repeat(0).duration(1300).playOn(bloodRise);
        YoYo.with(Techniques.FadeInLeft).delay(0).repeat(0).duration(1300).playOn(bloodDrop);
        YoYo.with(Techniques.FadeIn).delay(0).repeat(0).duration(1500).playOn(imageView);
        YoYo.with(Techniques.SlideInLeft).delay(0).repeat(0).duration(500).playOn(heartAnim);
        YoYo.with(Techniques.FadeIn).delay(0).repeat(0).duration(1500).playOn(logo);
        YoYo.with(Techniques.SlideInUp).delay(0).repeat(0).duration(2000).playOn(google_signIn);
        YoYo.with(Techniques.Shake).delay(8000).repeat(0).duration(1000).playOn(google_signIn);
    }


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
                Toast.makeText(this, "সফল হয় নি। দয়া করে আবার চেষ্টা করুন!", Toast.LENGTH_SHORT).show();


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




}//Root