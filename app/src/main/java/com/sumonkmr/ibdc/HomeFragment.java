package com.sumonkmr.ibdc;

import static androidx.browser.customtabs.CustomTabsClient.getPackageName;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.ads.AdRequest;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class HomeFragment extends Fragment {

    RelativeLayout ins,benefit,find_donors, about_us,rate_us;
    LinearLayout first_row,sec_row,third_row;
    SwipeRefreshLayout reloadHome;
    ImageSlider image_slider;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        Init();
        ImageSlider();
        OnClicks();
        Anim();
        Reload();

        return view;

    }

    private void Init() {
        ins = view.findViewById(R.id.ins);
        benefit = view.findViewById(R.id.benefit);
        find_donors = view.findViewById(R.id.find_donors);
        about_us = view.findViewById(R.id.about_us);
        rate_us = view.findViewById(R.id.rate_us);
        image_slider = view.findViewById(R.id.image_slider);
        first_row = view.findViewById(R.id.first_row);
        sec_row = view.findViewById(R.id.sec_row);
        third_row = view.findViewById(R.id.third_row);
        reloadHome = view.findViewById(R.id.reloadHome);
    }

    private void Reload(){
        reloadHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Init();
                ImageSlider();
                Anim();
                reloadHome.setRefreshing(false);
            }
        });
    }

    private void OnClicks() {
        ins.setOnClickListener(v -> {
            startActivity(new Intent(getContext(),Instructions.class));
        });

        benefit.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ProfitOfBloodDonation.class));
        });


        find_donors.setOnClickListener(v -> {
            startActivity(new Intent(getContext(),DisplayDonorsActivity.class));
        });

        about_us.setOnClickListener(v -> {
            startActivity(new Intent(getContext(),AboutUs.class));
        });

        rate_us.setOnClickListener(v -> {
            rateUsOnGooglePlay();
        });
    }

    private void Anim() {
        YoYo.with(Techniques.SlideInDown).delay(0).repeat(0).duration(1000).playOn(first_row);
        YoYo.with(Techniques.SlideInLeft).delay(0).repeat(0).duration(1000).playOn(sec_row);
        YoYo.with(Techniques.SlideInRight).delay(0).repeat(0).duration(1000).playOn(third_row);
    }

    private void ImageSlider(){
        final List<SlideModel> donationImages = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("sliders")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            donationImages.add(new SlideModel(Objects.requireNonNull(dataSnapshot.child("url").getValue()).toString(), Objects.requireNonNull(dataSnapshot.child("title").getValue()).toString(), ScaleTypes.FIT));
                        }
                        image_slider.setImageList(donationImages,ScaleTypes.FIT);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void rateUsOnGooglePlay(){

            final String appPackageName = requireContext().getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>




}