package com.sumonkmr.ibdc;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.ads.AdView;

import java.util.Random;

public class AboutUs extends AppCompatActivity {

    LinearLayout sds, sadequl, rakibul, harun, mamun;
    TextView dev_name, sdsEmail, moreApps, sadequl_phn, rakibul_phn, harun_phn, mamun_phn;
    ImageView dev_img;
    private static final int Request_call = 1;
    String sadequl_num = "tel:+8801764942671";
    String rakibul_num = "tel:+8801726641227";
    String harun_num = "tel:+8801849696823";
    String mamun_num = "tel:+8801821897961";
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);
        mAdView = findViewById(R.id.adMob_about);
        Ads ads = new Ads(this, mAdView);

        Innit();
        sadequl_phn.setOnClickListener(v -> {
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
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"sumonkmrofficial@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Im Sending Email From IBDC App");
            intent.putExtra(Intent.EXTRA_TEXT, "Please Type Your Massage Here : \n");
            intent.setType("massage/rfc822");
            startActivity(intent);
        });

        moreApps.setOnClickListener(v -> {
            gotoUrl("https://play.google.com/store/apps/dev?id=6877143126125387449");
        });


//        Directors onClick
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

    }//Finished On create!

    private void Innit() {
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

    private void gotoUrl(String url) {
        Uri uri = Uri.parse(url);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }


    public void callActions(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, Request_call);
        } else {
            startActivity(intent);
        }
    }


    @Override
    public void onBackPressed() {
        Random random;
        random = new Random();
        int myCount = random.nextInt(100 - 5) + 5;
        if (Ads.passCondition() && myCount % 2 == 0) {
            Ads.mInterstitialAd.show(this);
//            Toast.makeText(this, "Ready For Show Ads!" + myCount, Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        } else {
//            Toast.makeText(this, "Something Wrong And Value is : " + Ads.mod + myCount, Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
    }//onBackPressed
}//Root