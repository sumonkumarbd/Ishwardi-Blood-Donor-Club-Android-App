package com.sumonkmr.ibdc;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

public class GoogleLogIn extends AppCompatActivity {

    // Constant for the request code for Google Sign-In
    private static final int REQUEST_CODE = 2;

    // UI Elements
    RelativeLayout edit_tabs;
    ImageView imageView, logo, headerImg, bloodDrop, bloodRise;
    TextView google_signIn;
    LottieAnimationView loadingAim, heartAnim;
    TextView g_signUp;
    SwipeRefreshLayout loginReload;

    // Google Sign-In variables
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    GoogleSignInAccount account;

    // Firebase Authentication instance
    FirebaseAuth mAuth;

    // Firebase Database reference
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference dbReference;

    // Dialog for additional UI interaction
    Dialog dialog;
    TextView textView7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_log_in);

        // Play introductory sound using SoundManager
        SoundManager soundManager = new SoundManager(this);
        soundManager.national_aunt.start();

        // Initialize Firebase Authentication and set language code
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("bn");

        // Initialize Google Sign-In process and UI elements
        ProcessRequest();
        OneTapClientProcess();
        Init();

        // Start animations on screen load
        PlayAnim();

        // Swipe to refresh login process
        loginReload.setOnRefreshListener(() -> {
            soundManager.national_aunt.start();
            PlayAnim();
            loginReload.setRefreshing(false);
            heartAnim.playAnimation();
        });

        // Google Sign-In button click handler
        google_signIn.setOnClickListener(v -> {
            LoginProcess();
        });

    }//onCreate

    /**
     * Initialize UI elements and views
     */
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

    /**
     * Play animation on various UI elements to enhance user experience
     */
    private void PlayAnim() {
        // Apply animations to UI components using YoYo library for smooth transitions
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

    /**
     * Set up Google Sign-In client configuration
     */
    private void ProcessRequest() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Set the server-side web client ID
                .requestEmail() // Request email address during sign-in
                .build();
        gsc = GoogleSignIn.getClient(this, gso); // Initialize GoogleSignInClient
    }

    /**
     * Start the Google Sign-In process by triggering the sign-in intent
     */
    private void LoginProcess() {
        loadingAim.setVisibility(View.VISIBLE); // Show loading animation
        Intent signInIntent = gsc.getSignInIntent(); // Get the sign-in intent
        startActivityForResult(signInIntent, REQUEST_CODE); // Start the activity for result
    }

    /**
     * Handle the result from the Google Sign-In activity
     * @param requestCode the request code
     * @param resultCode the result code
     * @param data the intent data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the request code matches
        if (requestCode == REQUEST_CODE) {
            // Retrieve the signed-in account from the intent
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class); // Get account details
                // Authenticate with Firebase using the Google ID token
                GoogleFirebaseAuth(account.getIdToken());

            } catch (ApiException e) {
                // Handle error during sign-in
                Toast.makeText(this, "টেকনিক্যাল সমস্যা!! অনুগ্রহপূর্বক কিচ্ছুক্ষন পরে আবার চেষ্টা করুন।", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Authenticate with Firebase using the Google ID token
     * @param idToken the Google ID token
     */
    private void GoogleFirebaseAuth(String idToken) {
        // Create Firebase credential using Google ID token
        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
        // Sign in with Firebase using the Google credentials
        mAuth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign-in successful
                        Toast.makeText(GoogleLogIn.this, "লগ ইন সফল!", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        startActivity(new Intent(getApplicationContext(), SplashScreen.class)); // Navigate to splash screen
                        loadingAim.setVisibility(View.INVISIBLE); // Hide loading animation
                    } else {
                        // Sign-in failed
                        Toast.makeText(GoogleLogIn.this, "লগ ইন ফেল!", Toast.LENGTH_SHORT).show();
                        loadingAim.setVisibility(View.INVISIBLE); // Hide loading animation
                    }
                });
    }

    /**
     * Configure and initiate the OneTap sign-in process (optional feature)
     */
    private void OneTapClientProcess() {
        // Initialize OneTap client for password and Google ID token sign-in options
        SignInClient oneTapClient = Identity.getSignInClient(this);
        // Your server's client ID for Google authentication
        BeginSignInRequest signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.default_web_client_id)) // Set server client ID
                        .setFilterByAuthorizedAccounts(true) // Filter by previously authorized accounts
                        .build())
                .setAutoSelectEnabled(true) // Automatically select when one credential is retrieved
                .build();
        // Optionally, proceed with one-tap sign-in logic here (not implemented in this example)
    }

}//GoogleLogIn Activity
