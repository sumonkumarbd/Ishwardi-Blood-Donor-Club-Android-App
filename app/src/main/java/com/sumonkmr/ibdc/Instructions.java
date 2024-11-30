package com.sumonkmr.ibdc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;

import java.util.Random;

public class Instructions extends AppCompatActivity {
    // AdView for displaying banner ads
    AdView adView;

    // Layout container for the banner ad
    LinearLayout bannerLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view for the activity, using the layout 'activity_instructions'
        setContentView(R.layout.activity_instructions);

        // Bind the banner layout view to the 'bannerLay' variable
        bannerLay = findViewById(R.id.bannerLayout);

        // Initialize AdsControl instance to manage ads
        AdsControl ads = new AdsControl(this);

        // Load a banner ad into the banner layout
        ads.loadBannerAd(bannerLay);
    }

    /**
     * Handles the back button press behavior. This method is customized to show an interstitial ad
     * under specific conditions before the activity is closed.
     */
    @Override
    public void onBackPressed() {
        // Create an instance of AdsControl to manage ads when the back button is pressed
        AdsControl adsControl = new AdsControl(this);

        // Create a random number generator to determine if an ad should be shown
        Random random = new Random();

        // Generate a random number between 5 and 100 to control ad behavior
        int myCount = random.nextInt(100 - 5) + 5;

        // If the ad control passes the condition and the random number is even, show an interstitial ad
        if (adsControl.passCondition() && myCount % 2 == 0) {
            // Show the interstitial ad
            AdsControl.mInterstitialAd.show(this);

            // Proceed with the default back pressed behavior
            super.onBackPressed();
        }
        // If the ad control does not pass the condition but the random number is even, initialize the IoT process
        else if (!adsControl.passCondition() && myCount % 2 == 0) {
            // Initialize the IoT process for ad-related operations (if applicable)
            adsControl.StartIoInnit(AdsControl.isValStartIo);

            // Proceed with the default back pressed behavior
            super.onBackPressed();

            // Log the details for debugging purposes (pass condition and random number)
            Log.d("llaa", "onBackPressed: " + adsControl.passCondition() + " and " + myCount);
        }
        // If none of the conditions match, simply proceed with the default back pressed behavior
        else {
            super.onBackPressed();
        }
    } // onBackPressed
}
