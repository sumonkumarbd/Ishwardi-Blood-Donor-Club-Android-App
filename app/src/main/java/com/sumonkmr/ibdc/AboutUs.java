package com.sumonkmr.ibdc;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Random;

public class AboutUs extends AppCompatActivity {

    // Declaring views used in the layout
    LinearLayout sds, sadequl, rakibul, harun, mamun;
    TextView dev_name, aftEmail, moreApps, sadequl_phn, rakibul_phn, harun_phn, mamun_phn;
    ImageView dev_img,linkedin,upwork,fiverr,freelancer;
    private static final int Request_call = 1; // Request code for call permission
    // Phone numbers for the team members
    String sadequl_num = "tel:+8801764942671";
    String rakibul_num = "tel:+8801726641227";
    String harun_num = "tel:+8801849696823";
    String mamun_num = "tel:+8801821897961";
    private LinearLayout bannerLayout; // Layout for displaying ads

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us); // Setting the content view

        // Initializing the ad banner
        bannerLayout = findViewById(R.id.bannerLayout);
        AdsControl ads = new AdsControl(this);
        ads.loadBannerAd(bannerLayout); // Load the banner ad

        Innit(); // Initialize the views

        // Set up click listeners for phone number links
        sadequl_phn.setOnClickListener(v -> {
            callActions(sadequl_num); // Call the sadequl number
        });

        rakibul_phn.setOnClickListener(v -> {
            callActions(rakibul_num); // Call the rakibul number
        });

        harun_phn.setOnClickListener(v -> {
            callActions(harun_num); // Call the harun number
        });

        mamun_phn.setOnClickListener(v -> {
            callActions(mamun_num); // Call the mamun number
        });

        // Set up click listener for developer's profile link
        dev_img.setOnClickListener(v -> {
            gotoUrl("https://www.facebook.com/developer.sumonkmr"); // Open developer's Facebook profile
        });

        dev_name.setOnClickListener(v -> {
            gotoUrl("https://www.facebook.com/developer.sumonkmr"); // Open developer's Facebook profile
        });

        // Set up click listener for "SDS" link
        sds.setOnClickListener(v -> {
            gotoUrl("https://www.appsformation.com/"); // Open SDS website
        });

        // Set up click listener for email contact
        aftEmail.setOnClickListener(v -> {
            // Open email client with predefined subject and body
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@appsformation.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Im Sending Email From IBDC App");
            intent.putExtra(Intent.EXTRA_TEXT, "Please Type Your Massage Here : \n");
            intent.setType("message/rfc822");
            startActivity(intent);
        });

//        // Set up click listener for "More Apps" link
//        moreApps.setOnClickListener(v -> {
//            gotoUrl("https://play.google.com/store/apps/dev?id=6877143126125387449"); // Open the developer's other apps
//        });

        linkedin.setOnClickListener(view -> gotoUrl("https://www.linkedin.com/in/sumonkmr/"));
        upwork.setOnClickListener(view -> gotoUrl("https://www.upwork.com/freelancers/~01aca51f9768f64e7f/"));
        fiverr.setOnClickListener(view -> gotoUrl("https://www.fiverr.com/sumonkmr_bd/"));
        freelancer.setOnClickListener(view -> gotoUrl("https://www.freelancer.com/u/sumonkmrbd/"));

        // Directors' onClick listeners for their Facebook profiles
        sadequl.setOnClickListener(v -> {
            gotoUrl("https://www.facebook.com/sadequl71");
        });

        rakibul.setOnClickListener(v -> {
            gotoUrl("https://www.facebook.com/rakibul.shakil.7");
        });

        harun.setOnClickListener(v -> {
            gotoUrl("https://www.facebook.com/harunmehedi70");
        });

        mamun.setOnClickListener(v -> {
            gotoUrl("https://www.facebook.com/jsjs.hshsh.522");
        });

    }// Finished onCreate

    // Initialize the views
    private void Innit() {
        sds = findViewById(R.id.devInfo);
        aftEmail = findViewById(R.id.sdsEmail);
        dev_name = findViewById(R.id.dev_name);
        dev_img = findViewById(R.id.dev_img);
//        moreApps = findViewById(R.id.moreApps);
        sadequl = findViewById(R.id.sadequl);
        rakibul = findViewById(R.id.rakibul);
        harun = findViewById(R.id.harun);
        mamun = findViewById(R.id.mamun);
        mamun_phn = findViewById(R.id.mamun_phn);
        harun_phn = findViewById(R.id.harun_phn);
        sadequl_phn = findViewById(R.id.sadequl_phn);
        rakibul_phn = findViewById(R.id.rakibul_phn);
        linkedin = findViewById(R.id.linkedin);
        upwork = findViewById(R.id.upwork);
        fiverr = findViewById(R.id.fiverr);
        freelancer = findViewById(R.id.freelancer);
    }

    // Helper method to open a URL in the browser
    private void gotoUrl(String url) {
        Uri uri = Uri.parse(url); // Convert string to Uri
        startActivity(new Intent(Intent.ACTION_VIEW, uri)); // Open the URL in a browser
    }

    // Helper method to make a phone call
    private void callActions(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(number)); // Set the phone number
        // Check if permission is granted for making calls
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, Request_call);
        } else {
            startActivity(intent); // Start the phone call
        }
    }

    // Override the onBackPressed method to show an ad when the user presses the back button
    @Override
    public void onBackPressed() {
        AdsControl adsControl = new AdsControl(this);
        Random random = new Random(); // Generate a random number
        int myCount = random.nextInt(100 - 5) + 5; // Generate a random number between 5 and 100
        if (adsControl.passCondition() && myCount % 2 == 0) {
            AdsControl.mInterstitialAd.show(this); // Show interstitial ad
            super.onBackPressed(); // Proceed with the back press
        } else if (!adsControl.passCondition() && myCount % 2 == 0) {
            adsControl.StartIoInnit(AdsControl.isValStartIo); // Initialize Start.io ads if condition is met
            super.onBackPressed(); // Proceed with the back press
            Log.d("llaa", "onBackPressed: " + adsControl.passCondition() + " and " + myCount);
        } else {
            super.onBackPressed(); // Proceed with the back press without showing ads
        }
    } // onBackPressed

} // End of AboutUs class
