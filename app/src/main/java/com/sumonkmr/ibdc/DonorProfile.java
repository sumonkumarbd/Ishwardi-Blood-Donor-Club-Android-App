package com.sumonkmr.ibdc;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class DonorProfile extends AppCompatActivity {

    // Request code for making a phone call
    private static final int Request_call = 1;

    // UI elements for the donor profile screen
    TextInputEditText commentField;
    ImageButton comment_done;
    ImageView donorImg, d_like_btn, d_commentsBtn, d_shareBtn;
    DatabaseReference userRef, commentRef, like_ref;
    String postKey;
    String userId;
    String state, district, upazila, fname, village, bloodgroup, bloodImg_url, lastDonateDate, age, mobile;
    TextView detailFullName, detailBloodGroup, age_d, detailVillage, detailTehsil, detailDistrict, detailState, mobile_no, lastDonateDate_d, d_like_count, d_commentsTxt;
    RecyclerView cmtRecView;
    FirebaseUser currentUser;
    GoogleSignInAccount account;
    private Boolean testClick = false;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_profile);

        // Initialize ad banner
        LinearLayout bannerLay = findViewById(R.id.bannerLayout);
        AdsControl ads = new AdsControl(this);
        ads.loadBannerAd(bannerLay);

        // Initialize the UI components and set the donor data
        Init();
    }

    // Method to initialize the UI components and set donor data
    private void Init() {
        // Retrieve donor details passed via Intent
        postKey = getIntent().getStringExtra("postKey");
        fname = getIntent().getStringExtra("fName");
        bloodImg_url = getIntent().getStringExtra("pIMG");
        state = getIntent().getStringExtra("division");
        district = getIntent().getStringExtra("district");
        upazila = getIntent().getStringExtra("upazila");
        village = getIntent().getStringExtra("village");
        bloodgroup = getIntent().getStringExtra("bloodGrope");
        lastDonateDate = getIntent().getStringExtra("lastDonateDate");
        age = getIntent().getStringExtra("age");
        mobile = getIntent().getStringExtra("mobile");

        // Initialize UI components
        donorImg = findViewById(R.id.donorImg);
        detailFullName = findViewById(R.id.detailFullName);
        detailBloodGroup = findViewById(R.id.detailBloodGroup);
        age_d = findViewById(R.id.age_d);
        detailVillage = findViewById(R.id.detailVillage);
        detailTehsil = findViewById(R.id.detailTehsil);
        detailDistrict = findViewById(R.id.detailDistrict);
        detailState = findViewById(R.id.detailState);
        mobile_no = findViewById(R.id.mobile_no);
        lastDonateDate_d = findViewById(R.id.lastDonateDate_d);
        commentField = findViewById(R.id.commentField);
        comment_done = findViewById(R.id.comment_done);
        d_like_btn = findViewById(R.id.d_like_btn);
        d_like_count = findViewById(R.id.d_like_count);
        d_commentsBtn = findViewById(R.id.d_commentsBtn);
        d_commentsTxt = findViewById(R.id.d_commentsTxt);
        d_shareBtn = findViewById(R.id.d_shareBtn);
        cmtRecView = findViewById(R.id.cmtRecView);

        // Set layout manager for RecyclerView
        cmtRecView.setLayoutManager(new LinearLayoutManager(this));

        // Display donor details on UI
        detailFullName.setText(fname);
        detailBloodGroup.setText(bloodgroup);
        age_d.setText(age);
        detailVillage.setText(village);
        detailTehsil.setText(upazila);
        detailDistrict.setText(district);
        detailState.setText(state);
        mobile_no.setText(mobile);
        lastDonateDate_d.setText(lastDonateDate);

        // Load donor image using Glide
        Glide.with(this).load(bloodImg_url).into(donorImg);

        // Call action when the mobile number is clicked
        mobile_no.setOnClickListener(v -> {
            callActions(mobile);
        });

        // Share donor information via intent
        d_shareBtn.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TITLE, R.string.app_name);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "আমি বাঁচাতে চাই একটি প্রাণ, তাইতো করবো রক্তদান!\n" + "এখানে রক্তদাতা সম্পর্কে তথ্য রয়েছে :\nনাম : " + fname + " " + "\nরক্তের গ্রুপ : " + bloodgroup + "\nঠিকানা: " + village + " ," + upazila + " ," + district + " ," + state + "\nমোবাইল নম্বর : " + mobile);
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, "রক্ত দিন জীবন বাঁচান।");
            startActivity(shareIntent);
        });

        // Firebase references for users, likes, and comments
        userRef = FirebaseDatabase.getInstance().getReference().child("Donors");
        like_ref = FirebaseDatabase.getInstance().getReference("likes");
        commentRef = FirebaseDatabase.getInstance().getReference().child("Donors").child(postKey).child("comments");

        // Get current user details
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        userId = currentUser.getUid();
        account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        // Handle the submit button for comments
        comment_done.setOnClickListener(v -> userRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fName = Objects.requireNonNull(snapshot.child("FName").getValue()).toString();
                    String lName = Objects.requireNonNull(snapshot.child("LName").getValue()).toString();
                    String bloodImg_url = Objects.requireNonNull(snapshot.child("bloodImg_url").getValue()).toString();
                    String userName = fName + " " + lName;
                    ProcessComment(userName, bloodImg_url);
                } else {
                    assert account != null;
                    String bloodImg_url = String.valueOf(account.getPhotoUrl());
                    String userName = account.getDisplayName();
                    ProcessComment(userName, bloodImg_url);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle cancellation here (if needed)
            }
        }));

        // Handle the like button click
        d_like_btn.setOnClickListener(v -> {
            testClick = true;
            like_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (testClick) {
                        if (snapshot.child(postKey).hasChild(userId)) {
                            like_ref.child(postKey).child(userId).removeValue();
                            testClick = false;
                            MediaPlayer.create(getApplicationContext(), R.raw.light_switch).start();
                        } else {
                            like_ref.child(postKey).child(userId).setValue(true);
                            testClick = false;
                            MediaPlayer.create(getApplicationContext().getApplicationContext(), R.raw.ping).start();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle cancellation here (if needed)
                }
            });
        });
    }

    // Method to process and post a comment
    private void ProcessComment(String userName, String bloodImg_url) {
        String userComment = Objects.requireNonNull(commentField.getText()).toString();
        String parentKey = userId + new Random().nextInt(1000);
        Calendar dateValue = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        String cDate = dateFormat.format(dateValue.getTime());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
        String cTime = timeFormat.format(dateValue.getTime());

        // Retrieve user data and post the comment
        userRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (userId != null || userId.length() != 0) {
                    HashMap cmnt = new HashMap();
                    cmnt.put("uid", userId);
                    cmnt.put("userName", userName);
                    cmnt.put("userImg", bloodImg_url);
                    cmnt.put("usermsg", userComment);
                    cmnt.put("date", cDate);
                    cmnt.put("time", cTime);

                    // Ensure comment is not empty before submitting
                    if (commentField.getText().length() != 0) {
                        commentRef.child(parentKey).updateChildren(cmnt).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(DonorProfile.this, "কমেন্ট সম্পূর্ণ!", Toast.LENGTH_SHORT).show();
                                commentField.setText("");
                            } else {
                                Toast.makeText(DonorProfile.this, "কমেন্ট যোগ হয়নি।", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "ডোনার সম্পর্কে কিছু লিখুন।", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle cancellation here (if needed)
            }
        });
    }

    // Handle phone call action if permission granted
    private void callActions(String phoneNum) {
        if (ContextCompat.checkSelfPermission(DonorProfile.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DonorProfile.this, new String[]{Manifest.permission.CALL_PHONE}, Request_call);
        } else {
            String dial = "tel:" + phoneNum;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }

    // Handle permission request result for making a call
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Request_call) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String dial = "tel:" + mobile;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
