package com.sumonkmr.ibdc;

import static androidx.browser.customtabs.CustomTabsClient.getPackageName;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HomeFragment extends Fragment {

    RelativeLayout ins,benefit,be_donor,find_donors, about_us,rate_us;
    ImageSlider image_slider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ins = view.findViewById(R.id.ins);
        benefit = view.findViewById(R.id.benefit);
        find_donors = view.findViewById(R.id.find_donors);
        about_us = view.findViewById(R.id.about_us);
        rate_us = view.findViewById(R.id.rate_us);
        image_slider = view.findViewById(R.id.image_slider);

        ImageSlider();

        ins.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), Instructions.class));
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


        return view;

    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    private void ImageSlider(){
        final List<SlideModel> donationImages = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("donationImages")
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

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

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