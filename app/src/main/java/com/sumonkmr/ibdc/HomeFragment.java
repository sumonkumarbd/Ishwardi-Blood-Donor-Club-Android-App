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

    // UI components: Layouts, Buttons, ImageSlider, etc.
    RelativeLayout ins, benefit, find_donors, about_us, rate_us;
    LinearLayout first_row, sec_row, third_row;
    SwipeRefreshLayout reloadHome; // Swipe refresh layout for reloading content
    ImageSlider image_slider; // Image slider to display images
    View view; // Root view for the fragment

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment layout and return the root view
        view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize the UI components
        Init();

        // Set up the image slider with Firebase data
        ImageSlider();

        // Set up click listeners for different sections
        OnClicks();

        // Set up animations for UI components
        Anim();

        // Set up swipe-to-refresh functionality
        Reload();

        // Return the root view for the fragment
        return view;
    }

    /**
     * Initializes the UI components by binding them with the respective views from the layout.
     */
    private void Init() {
        // Bind UI components to their respective views in the layout
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

    /**
     * Sets up the swipe-to-refresh functionality.
     * This allows the user to refresh the content by pulling down.
     */
    private void Reload() {
        reloadHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh the data by reinitializing components and reloading images/animations
                Init();
                ImageSlider();
                Anim();

                // Stop the refresh animation
                reloadHome.setRefreshing(false);
            }
        });
    }

    /**
     * Sets up click listeners for various UI components.
     * Each click listener opens a different activity based on the user's selection.
     */
    private void OnClicks() {
        // Click listener for "Instructions" section
        ins.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), Instructions.class));
        });

        // Click listener for "Benefits of Blood Donation" section
        benefit.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ProfitOfBloodDonation.class));
        });

        // Click listener for "Find Donors" section
        find_donors.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), DisplayDonorsActivity.class));
        });

        // Click listener for "About Us" section
        about_us.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AboutUs.class));
        });

        // Click listener for "Rate Us" section
        rate_us.setOnClickListener(v -> {
            // Open the app's Google Play Store page for ratings
//            rateUsOnGooglePlay();
            rateUsOnAmazonAppstore();
        });
    }

    /**
     * Sets up animations for various UI components using the YoYo animation library.
     * Animates the appearance of rows and elements on the screen.
     */
    private void Anim() {
        // Animate the first row with a slide-in from the top
        YoYo.with(Techniques.SlideInDown).delay(0).repeat(0).duration(1000).playOn(first_row);

        // Animate the second row with a slide-in from the left
        YoYo.with(Techniques.SlideInLeft).delay(0).repeat(0).duration(1000).playOn(sec_row);

        // Animate the third row with a slide-in from the right
        YoYo.with(Techniques.SlideInRight).delay(0).repeat(0).duration(1000).playOn(third_row);
    }

    /**
     * Loads images into the image slider from Firebase Realtime Database.
     * The images are added to the slider dynamically based on the data from Firebase.
     */
    private void ImageSlider() {
        final List<SlideModel> donationImages = new ArrayList<>(); // List to hold image models for the slider

        // Fetch the images from Firebase Realtime Database
        FirebaseDatabase.getInstance().getReference().child("sliders")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Clear the existing list before adding new items
                        donationImages.clear();

                        // Iterate through the data from Firebase and add each image URL and title to the list
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            donationImages.add(new SlideModel(
                                    Objects.requireNonNull(dataSnapshot.child("url").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("title").getValue()).toString(),
                                    ScaleTypes.FIT));
                        }

                        // Set the list of images to the image slider
                        image_slider.setImageList(donationImages, ScaleTypes.FIT);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle errors here (e.g., logging the error or showing a toast)
                    }
                });
    }

    /**
     * Opens the Google Play Store page for the app, allowing users to rate it.
     * If the Play Store app is not installed, it opens the Play Store page in a web browser.
     */
    private void rateUsOnGooglePlay() {
        final String appPackageName = requireContext().getPackageName(); // Get the app's package name

        try {
            // Try to open the Play Store app to the app's page
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            // If the Play Store app is not installed, open the app's Play Store page in the browser
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    /**
     * Opens the Amazon app Store page for the app, allowing users to rate it.
     * If the Amazon app Store is not installed, it opens the Amazon app Store page in a web browser.
     */
    private void rateUsOnAmazonAppstore() {
        final String appPackageName = requireContext().getPackageName(); // Get the app's package name

        try {
            // Try to open the Amazon Appstore to the app's page
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("amzn://apps/android?p=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            // If the Amazon Appstore app is not installed, open the app's page in the browser
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.amazon.com/gp/mas/dl/android?p=" + appPackageName)));
        }
    }




    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
}
