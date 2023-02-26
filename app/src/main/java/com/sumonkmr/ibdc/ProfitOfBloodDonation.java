package com.sumonkmr.ibdc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdView;

import java.util.Random;

public class ProfitOfBloodDonation extends AppCompatActivity {

    LinearLayout bannerLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profit_of_donation);
        bannerLay = findViewById(R.id.bannerLayout);
        AdsControl ads = new AdsControl(this);
        ads.loadBannerAd(bannerLay);
    }
    @Override
    public void onBackPressed() {
        AdsControl adsControl = new AdsControl(this);
        Random random;
        random = new Random();
        int myCount = random.nextInt(100 - 5) + 5;
        if (adsControl.passCondition() && myCount % 2 == 0) {
            AdsControl.mInterstitialAd.show(this);
            super.onBackPressed();
        } else if (!adsControl.passCondition() && myCount % 2 == 0) {
            adsControl.StartIoInnit(AdsControl.isValStartIo);
            super.onBackPressed();
            Log.d("llaa", "onBackPressed: " + adsControl.passCondition() + " and " + myCount);
        } else {
            super.onBackPressed();
        }
    }//onBackPressed
}