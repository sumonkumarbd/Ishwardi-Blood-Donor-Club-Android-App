package com.sumonkmr.ibdc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.startapp.sdk.adsbase.StartAppAd;

import java.util.Collections;
import java.util.List;

public class AdsControl {
    AdView mAdView; // AdMob banner ad view
    AdRequest adRequest; // AdMob ad request object
    @SuppressLint("StaticFieldLeak")
    static Activity activity; // Reference to the current activity for ad interactions

    // Default constructor for AdsControl
    AdsControl() {
    }

    // Constructor to initialize AdsControl with the current activity
    AdsControl(Activity activity) {
        adRequest = new AdRequest.Builder().build(); // Create an ad request
        this.activity = activity; // Assign the passed activity
        getAdsFirebase(); // Retrieve ad configuration from Firebase
        getAdsStartIo(); // Retrieve StartApp ad configuration
    }

    // Flag to check if ads are enabled
    static boolean isVal;

    // Method to fetch ad settings from Firebase
    protected boolean getAdsFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("admob"); // Firebase reference for AdMob configuration
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Retrieve values from Firebase
                String settings = snapshot.child("settings").getValue(String.class);
                String appUnitID = snapshot.child("AppUnitID").getValue(String.class);
                String device_id = snapshot.child("device_id").getValue(String.class);
                String interstitials = snapshot.child("interstitials").getValue(String.class);

                assert settings != null;
                if (settings.contains("ON")) {
                    isVal = true; // Enable ads
                    assert device_id != null;
                    TestingDevice(device_id); // Set test device ID for AdMob
                    SetAppUnitId(appUnitID); // Set AdMob App Unit ID
                    loadFullscreenAd(interstitials); // Load interstitial ads
                    Log.d("DataBase Mode", "ON");
                } else {
                    isVal = false; // Disable ads
                    Log.d("DataBase Mode", "OFF");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database errors if any
            }
        });

        return isVal;
    }

    // Method to set the App Unit ID for AdMob
    public void SetAppUnitId(String appUnitId) {
        try {
            if (appUnitId != null) {
                ApplicationInfo ai = activity.getPackageManager().getApplicationInfo("com.sumonkmr.ibdc", PackageManager.GET_META_DATA);
                Bundle bundle = ai.metaData;
                ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", appUnitId); // Set the AdMob App Unit ID
                String ApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID"); // Fetch the App Unit ID
                System.out.println(ApiKey); // Print the App ID for debugging
            }
        } catch (PackageManager.NameNotFoundException | NullPointerException e) {
            Log.e("SetAppUnitId", "Error while setting App ID: " + e.getMessage()); // Handle exceptions
        }
    }

    // Counter to track banner ad clicks
    static int BANNER_AD_CLICK_COUNT = 0;

    // Method to load a banner ad into the passed LinearLayout
    protected void loadBannerAd(LinearLayout layout) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("admob"); // Firebase reference for AdMob banner ads
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String settings = snapshot.child("settings").getValue(String.class);
                String banner = snapshot.child("banner").getValue(String.class); // Retrieve banner ad unit ID

                assert settings != null;
                if (settings.contains("ON")) {
                    layout.setVisibility(View.VISIBLE); // Show the layout if ads are enabled
                    mAdView = new AdView(activity); // Create a new AdView for banner ads
                    mAdView.setAdSize(AdSize.BANNER); // Set the ad size to BANNER
                    if (banner != null) {
                        mAdView.setAdUnitId(banner); // Set the banner ad unit ID from Firebase
                    } else {
                        mAdView.setAdUnitId("ca-app-pub-3940256099942544/6300978111"); // Default test banner ad unit
                        Toast.makeText(activity, "It's From Manual", Toast.LENGTH_SHORT).show();
                    }
                    mAdView.loadAd(adRequest); // Load the banner ad
                    layout.addView(mAdView); // Add the ad view to the layout
                    mAdView.setAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded() {
                            // Hide the banner ad after 3 clicks
                            if (BANNER_AD_CLICK_COUNT >= 3) {
                                if (mAdView != null) mAdView.setVisibility(View.GONE);
                            } else {
                                if (mAdView != null) mAdView.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onAdFailedToLoad(LoadAdError adError) {
                            // Handle ad load failure
                        }

                        @Override
                        public void onAdOpened() {
                            // Handle ad opened event
                        }

                        @Override
                        public void onAdClicked() {
                            BANNER_AD_CLICK_COUNT++; // Increment click count
                            if (BANNER_AD_CLICK_COUNT >= 3) {
                                if (mAdView != null) mAdView.setVisibility(View.GONE); // Hide after 3 clicks
                            }
                        }

                        @Override
                        public void onAdClosed() {
                            // Handle ad closed event
                        }
                    });
                    Log.d("DataBase Mode", "ON");
                } else {
                    layout.setVisibility(View.INVISIBLE); // Hide the layout if ads are disabled
                    Log.d("DataBase Mode", "OFF");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle Firebase read errors
            }
        });
    }

    // Counter for fullscreen ads
    int FULLSCREEN_AD_LOAD_COUNT = 0;
    static InterstitialAd mInterstitialAd; // Interstitial ad object

    // Method to load an interstitial ad
    protected void loadFullscreenAd(String url) {
        // Requesting a fullscreen ad
        InterstitialAd.load(activity.getApplicationContext(), url, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd; // Set the interstitial ad
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        FULLSCREEN_AD_LOAD_COUNT++; // Increment the load count after ad dismissal
                        loadFullscreenAd(url); // Load a new ad
                        Log.d("FULLSCREEN_AD_LOAD_COUNT", "" + FULLSCREEN_AD_LOAD_COUNT);
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mInterstitialAd = null; // Nullify if ad fails to load
            }
        });
    }

    // Method to configure the test device for AdMob
    protected void TestingDevice(String id) {
        if (id != null && id.length() > 12) {
            List<String> testDeviceIds = Collections.singletonList(id); // List of test device IDs
            RequestConfiguration configuration = new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
            MobileAds.setRequestConfiguration(configuration); // Apply the configuration
        }
        MobileAds.initialize(activity.getApplicationContext(), initializationStatus -> {}); // Initialize MobileAds
    }

    boolean mod;

    // Method to check if all conditions are met for ads
    protected boolean passCondition() {
        if (getAdsFirebase() && mInterstitialAd != null) {
            mod = true; // Ads are enabled and interstitial ad is available
        } else {
            mod = false; // Ads are not enabled or interstitial ad is not available
        }
        return mod;
    }

    static boolean isValStartIo; // Flag for StartApp ads

    // Method to fetch StartApp ad settings from Firebase
    static protected boolean getAdsStartIo() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("startIoAds"); // Firebase reference for StartApp ads
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String settings = snapshot.child("settings").getValue(String.class);
                String unitId = snapshot.child("unitId").getValue(String.class); // Retrieve StartApp ad unit ID

                if (settings != null && settings.contains("ON")) {
                    isValStartIo = true; // Enable StartApp ads
                    StartIoInnit(true); // Initialize StartApp ad
                } else {
                    isValStartIo = false; // Disable StartApp ads
                    StartIoInnit(false); // Disable StartApp ad
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle Firebase read errors
            }
        });
        return isValStartIo;
    }

    // Method to initialize and show StartApp ad
    static protected void StartIoInnit(Boolean bol) {
        if (bol) {
            StartAppAd.showAd(activity); // Show StartApp ad
        }
    }
}
