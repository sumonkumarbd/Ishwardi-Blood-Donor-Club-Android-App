package com.sumonkmr.ibdc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AboutUs extends AppCompatActivity {

    LinearLayout sds;
    TextView dev_name,sdsEmail,moreApps;
    ImageView dev_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);

        Innit();

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


    }//Finished On create!

    private void Innit(){
        sds = findViewById(R.id.sds);
        sdsEmail = findViewById(R.id.sdsEmail);
        dev_name = findViewById(R.id.dev_name);
        dev_img = findViewById(R.id.dev_img);
        moreApps = findViewById(R.id.moreApps);
    }

    private void gotoUrl(String url){
        Uri uri = Uri.parse(url);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }
}