package com.sumonkmr.ibdc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.ads.AdView;

import java.util.Random;

public class ProfitOfBloodDonation extends AppCompatActivity {

    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profit_of_donation);
        adView = findViewById(R.id.adMob_ben);
        AdsControl ads = new AdsControl(this,adView);
    }
    @Override
    public void onBackPressed() {
        Random random;
        random = new Random();
        int myCount = random.nextInt(100 - 5) + 5;
        if (AdsControl.passCondition() && myCount % 2 == 0) {
            AdsControl.mInterstitialAd.show(this);
//            Toast.makeText(this, "Ready For Show Ads!" + myCount, Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        } else {
//            Toast.makeText(this, "Something Wrong And Value is : " + Ads.mod + myCount, Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
    }//onBackPressed
}