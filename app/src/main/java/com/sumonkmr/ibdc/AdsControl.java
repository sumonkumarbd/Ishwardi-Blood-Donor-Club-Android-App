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

import java.io.Console;
import java.util.Collections;
import java.util.List;

public class AdsControl {
    AdView mAdView;
    AdRequest adRequest;
    @SuppressLint("StaticFieldLeak")
    Activity activity;


    AdsControl() {
    }

    AdsControl(Activity activity) {
        adRequest = new AdRequest.Builder().build();
        this.activity = activity;
        getAdsFirebase();
        getAdsStartIo();
    }//para constructor


    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    static boolean isVal;

    protected boolean getAdsFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("admob");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String settings = snapshot.child("settings").getValue(String.class);
                String appUnitID = snapshot.child("AppUnitID").getValue(String.class);
                String device_id = snapshot.child("device_id").getValue(String.class);
                String interstitials = snapshot.child("interstitials").getValue(String.class);
//                for admob ads
                assert settings != null;
                if (settings.contains("ON")) {
                    isVal = true;
                    assert device_id != null;
                    TestingDevice(device_id);
                    SetAppUnitId(appUnitID);
                    loadFullscreenAd(interstitials);
                    Log.d("DataBase Mode", "ON");

                } else if (settings.contains("OFF")) {
                    isVal = false;
                    Log.d("DataBase Mode", "OFF");
                } else {
                    isVal = false;
                    Log.d("DataBase Mode", "OFF");
                }
            }//onDataChange

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return isVal;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    public void SetAppUnitId(String appUnitId) {
        try {

            ApplicationInfo ai = activity.getPackageManager().getApplicationInfo("com.sumonkmr.ibdc", PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            String myApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
            ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", appUnitId);//you can replace your key APPLICATION_ID here
            String ApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
            System.out.println(ApiKey);
        } catch (PackageManager.NameNotFoundException | NullPointerException e) {
            System.out.println(e);
        }
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    static int BANNER_AD_CLICK_COUNT = 0;

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    protected void loadBannerAd(LinearLayout layout) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("admob");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String settings = snapshot.child("settings").getValue(String.class);
                String banner = snapshot.child("banner").getValue(String.class);
                assert settings != null;
                if (settings.contains("ON")) {
                    layout.setVisibility(View.VISIBLE);
                    mAdView = new AdView(activity);
                    mAdView.setAdSize(AdSize.BANNER);
                    if (banner != null) {
                        mAdView.setAdUnitId(banner);
//                        Toast.makeText(activity, "Its From Database", Toast.LENGTH_SHORT).show();
                    } else {
                        mAdView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
                        Toast.makeText(activity, "Its From Manual", Toast.LENGTH_SHORT).show();
                    }
                    mAdView.loadAd(adRequest);
                    layout.addView(mAdView);
                    mAdView.setAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded() {
                            // Code to be executed when an ad finishes loading.
//                Log.d("Banner Ads","Banner Ads is successfully Loaded!!");
//                Toast.makeText(mAdView.getContext(), "Banner Ads is successfully Loaded!!", Toast.LENGTH_SHORT).show();

                            if (BANNER_AD_CLICK_COUNT >= 3) {
                                if (mAdView != null) mAdView.setVisibility(View.GONE);
                            } else {
                                if (mAdView != null) mAdView.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onAdFailedToLoad(LoadAdError adError) {
                            // Code to be executed when an ad request fails.
                        }

                        @Override
                        public void onAdOpened() {
                            // Code to be executed when an ad opens an overlay that
                            // covers the screen.
                        }

                        @Override
                        public void onAdClicked() {
                            // Code to be executed when the user clicks on an ad.
                            BANNER_AD_CLICK_COUNT++;

                            if (BANNER_AD_CLICK_COUNT >= 3) {
                                if (mAdView != null) mAdView.setVisibility(View.GONE);
                            }

                        }

                        @Override
                        public void onAdClosed() {
                            // Code to be executed when the user is about to return
                            // to the app after tapping on an ad.
                        }
                    });
                    Log.d("DataBase Mode", "ON");
                } else if (settings.contains("OFF")) {
                    Log.d("DataBase Mode", "OFF");
//                    Toast.makeText(activity, "OFF", Toast.LENGTH_SHORT).show();
                    layout.setVisibility(View.INVISIBLE);
                } else {
                    Log.d("DataBase Mode", "OFF");
                }
            }//onDataChange

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // loadFullscreenAd method starts here.....
    int FULLSCREEN_AD_LOAD_COUNT = 0;
    static InterstitialAd mInterstitialAd;

    protected void loadFullscreenAd(String url) {
        //Requesting for a fullscreen Ad
        InterstitialAd.load(activity.getApplicationContext(), url, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
//                Toast.makeText(activity.getApplicationContext(), "Full Screen Ads is Loaded Successfully!", Toast.LENGTH_SHORT).show();

                //Fullscreen callback || Requesting again when an ad is shown already
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        //User dismissed the previous ad. So we are requesting a new ad here
                        FULLSCREEN_AD_LOAD_COUNT++;
                        loadFullscreenAd(url);
                        Log.d("FULLSCREEN_AD_LOAD_COUNT", "" + FULLSCREEN_AD_LOAD_COUNT);


                    }

                }); // FullScreen Callback Ends here


            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                mInterstitialAd = null;
//                Toast.makeText(activity.getApplicationContext(), "Full Screen Ads is Loading is failed!", Toast.LENGTH_SHORT).show();
            }

        });

    }
    // loadFullscreenAd method ENDS  here..... >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    protected void TestingDevice(String id) {

        if (id.length() > 12) {
            //Adding your device id -- to avoid invalid activity from your device
            List<String> testDeviceIds = Collections.singletonList(id);
            RequestConfiguration configuration =
                    new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
            MobileAds.setRequestConfiguration(configuration);
        }

        //Init Admob Ads
        MobileAds.initialize(activity.getApplicationContext(), initializationStatus -> {
//            Toast.makeText(activity.getApplicationContext(), "অ্যাড লোড হতে প্রস্তুত!", Toast.LENGTH_SHORT).show();
        });
    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    boolean mod;

    protected boolean passCondition() {
        if (getAdsFirebase() && mInterstitialAd != null) {
            mod = true;
//            Toast.makeText(activity.getApplicationContext(), "সব ঠিকঠাক", Toast.LENGTH_SHORT).show();
        } else {
            mod = false;
//            Toast.makeText(activity.getApplicationContext(), String.valueOf(getAdsFirebase())+isVal+mod, Toast.LENGTH_SHORT).show();
        }
        return mod;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    static boolean isValStartIo;

    protected boolean getAdsStartIo() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("startIoAds");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String settings = snapshot.child("Settings").getValue(String.class);
                String appUnitID = snapshot.child("appUnitID").getValue(String.class);

//                for StartIO ads
                assert settings != null;
                if (settings.contains("ON")) {
                    isValStartIo = true;
                    StartIoApUnitId(appUnitID);
                    Log.d("DataBase Mode", "ON");

                } else if (settings.contains("OFF")) {
                    isValStartIo = false;
                    Log.d("DataBase Mode", "OFF");
                } else {
                    isValStartIo = false;
                    Log.d("DataBase Mode", "OFF");
                }
            }//onDataChange

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return isValStartIo;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    protected void StartIoInnit(Boolean bol) {

        if (bol) {
            StartAppAd.onBackPressed(activity);
            StartAppAd.showAd(activity);
        } else {
            Log.d("llaa", "StartIoInnit: " + "false");

        }
    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    public void StartIoApUnitId(String appUnitId) {
        try {

            ApplicationInfo ai = activity.getPackageManager().getApplicationInfo("com.sumonkmr.ibdc", PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            String myApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
            ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", appUnitId);//you can replace your key APPLICATION_ID here
            String ApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
            System.out.println(ApiKey);
        } catch (PackageManager.NameNotFoundException | NullPointerException e) {
            System.out.println(e);
        }
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


}
