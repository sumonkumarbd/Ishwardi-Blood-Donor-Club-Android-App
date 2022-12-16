package com.sumonkmr.ibdc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.errorprone.annotations.ForOverride;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AboutUs extends AppCompatActivity {

    LinearLayout sds,sadequl,rakibul,harun,mamun;
    TextView dev_name,sdsEmail,moreApps,sadequl_phn,rakibul_phn,harun_phn,mamun_phn;
    ImageView dev_img;
    private static final int Request_call = 1;
    String sadequl_num = "tel:+8801764942671";
    String rakibul_num = "tel:+8801726641227";
    String harun_num = "tel:+8801849696823";
    String mamun_num = "tel:+8801821897961";
    Random random;
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);

        Innit();
        sadequl_phn.setOnClickListener(v-> {
            callActions(sadequl_num);
        });

        rakibul_phn.setOnClickListener(v -> {
            callActions(rakibul_num);
        });

        harun_phn.setOnClickListener(v -> {
            callActions(harun_num);
        });

        mamun_phn.setOnClickListener(v -> {
            callActions(mamun_num);
        });

        dev_img.setOnClickListener(v -> {
            gotoUrl("https://www.facebook.com/developer.sumonkmr");
        });

        dev_name.setOnClickListener(v -> {
            gotoUrl("https://www.facebook.com/developer.sumonkmr");
        });

        sds.setOnClickListener(v -> {
            gotoUrl("https://www.facebook.com/sumonkmr.studio/");
        });

        sdsEmail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL,new String[]{"sumonkmrofficial@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT,"Im Sending Email From IBDC App");
            intent.putExtra(Intent.EXTRA_TEXT,"Please Type Your Massage Here : \n");
            intent.setType("massage/rfc822");
            startActivity(intent);
        });

        moreApps.setOnClickListener(v -> {
            gotoUrl("https://play.google.com/store/apps/dev?id=6877143126125387449");
        });


//        Directors onClick
        sadequl.setOnClickListener(v-> {
            gotoUrl("https://www.facebook.com/sadequl71");
        });

        rakibul.setOnClickListener(v-> {
            gotoUrl("https://www.facebook.com/rakibul.shakil.7");
        });

        harun.setOnClickListener(v-> {
            gotoUrl("https://www.facebook.com/harunmehedi70");
        });

        mamun.setOnClickListener(v-> {
            gotoUrl("https://www.facebook.com/jsjs.hshsh.522");
        });

    }//Finished On create!

    private void Innit(){
        sds = findViewById(R.id.sds);
        sdsEmail = findViewById(R.id.sdsEmail);
        dev_name = findViewById(R.id.dev_name);
        dev_img = findViewById(R.id.dev_img);
        moreApps = findViewById(R.id.moreApps);
        sadequl = findViewById(R.id.sadequl);
        rakibul = findViewById(R.id.rakibul);
        harun = findViewById(R.id.harun);
        mamun = findViewById(R.id.mamun);
        mamun_phn = findViewById(R.id.mamun_phn);
        harun_phn = findViewById(R.id.harun_phn);
        sadequl_phn = findViewById(R.id.sadequl_phn);
        rakibul_phn = findViewById(R.id.rakibul_phn);
    }

    private void gotoUrl(String url){
        Uri uri = Uri.parse(url);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }


    public void callActions(String number){
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CALL_PHONE},Request_call);
            }else {
                startActivity(intent);
            }
    }




    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    boolean isVal = false;
    private boolean getAdsFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("admob");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String settings = snapshot.child("settings").getValue(String.class);
                String device_id = snapshot.child("device_id").getValue(String.class);
                String banner = snapshot.child("banner").getValue(String.class);
                String interstitials = snapshot.child("interstitials").getValue(String.class);
                assert settings != null;
                if (settings.contains("ON")){
                    isVal = true;
                    assert device_id != null;
                    initAdmobAd(device_id);
                    loadFullscreenAd(interstitials);

                }else if (settings.contains("OFF")){
                    isVal = false;
                    Toast.makeText(AboutUs.this, "ads No Coming!", Toast.LENGTH_SHORT).show();
                }
                else {
                    isVal = false;
                    Toast.makeText(AboutUs.this, "Nothing!", Toast.LENGTH_SHORT).show();
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
    int BANNER_AD_CLICK_COUNT =0;
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    private void loadBannerAd(){
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                if (BANNER_AD_CLICK_COUNT >=3){
                    if(mAdView!=null) mAdView.setVisibility(View.GONE);
                }else{
                    if(mAdView!=null) mAdView.setVisibility(View.VISIBLE);
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

                if (BANNER_AD_CLICK_COUNT >=3){
                    if(mAdView!=null) mAdView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // loadFullscreenAd method starts here.....
    InterstitialAd mInterstitialAd;
    int FULLSCREEN_AD_LOAD_COUNT=0;
    private void loadFullscreenAd(String url){

        //Requesting for a fullscreen Ad
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(AboutUs.this,url, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;

                //Fullscreen callback || Requesting again when an ad is shown already
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        //User dismissed the previous ad. So we are requesting a new ad here
                        FULLSCREEN_AD_LOAD_COUNT++;
                        loadFullscreenAd(url);
                        Log.d("FULLSCREEN_AD_LOAD_COUNT", ""+FULLSCREEN_AD_LOAD_COUNT);


                    }

                }); // FullScreen Callback Ends here


            }
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                mInterstitialAd = null;
            }

        });

    }
    // loadFullscreenAd method ENDS  here..... >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    private void initAdmobAd(String id){

        if (id.length()>12){
            //Adding your device id -- to avoid invalid activity from your device
            List<String> testDeviceIds = Collections.singletonList(id);
            RequestConfiguration configuration =
                    new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
            MobileAds.setRequestConfiguration(configuration);
        }

        //Init Admob Ads
        MobileAds.initialize(AboutUs.this, initializationStatus -> {
            Toast.makeText(AboutUs.this, "coming", Toast.LENGTH_SHORT).show();
        });


    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        random = new Random();
        int myCount = random.nextInt(100 - 5) + 5;
        if (getAdsFirebase() && mInterstitialAd != null) {
            if (myCount % 2 == 0) {
                //Show Fullscreen ad
                mInterstitialAd.show(AboutUs.this);
                super.onBackPressed();
            }else {
                Toast.makeText(AboutUs.this, String.valueOf(myCount), Toast.LENGTH_SHORT).show();
            }

        }else {
            Toast.makeText(AboutUs.this, String.valueOf(getAdsFirebase()), Toast.LENGTH_SHORT).show();
        }
        new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }
}