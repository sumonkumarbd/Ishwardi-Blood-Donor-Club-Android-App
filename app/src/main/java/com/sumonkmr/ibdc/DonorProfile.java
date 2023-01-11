package com.sumonkmr.ibdc;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sumonkmr.ibdc.model.CommentModel;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class DonorProfile extends AppCompatActivity {

    private static final int Request_call = 1;
    TextInputEditText commentField;
    ImageButton comment_done;
    ImageView donorImg, d_like_btn, d_commentsBtn,d_shareBtn;
    DatabaseReference userRef, commentRef, like_ref;
    String postKey;
    String userId;
    String state, district, upazila, fname, village, bloodgroup, bloodImg_url, lastDonateDate, age, mobile;
    TextView detailFullName, detailBloodGroup, age_d, detailVillage, detailTehsil, detailDistrict, detailState, mobile_no, lastDonateDate_d, d_like_count, d_commentsTxt;
    RecyclerView cmtRecView;
    FirebaseUser currentUser;
    GoogleSignInAccount account;
    private Boolean testClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_profile);
        LinearLayout bannerLay = findViewById(R.id.bannerLayout);
        AdsControl ads = new AdsControl(this);
        ads.loadBannerAd(bannerLay);
        Init();

    }//onCreate

    private void Init() {
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
//        =========================================================
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
        cmtRecView.setLayoutManager(new LinearLayoutManager(this));

//        ==========================================================
        detailFullName.setText(fname);
        detailBloodGroup.setText(bloodgroup);
        age_d.setText(age);
        detailVillage.setText(village);
        detailTehsil.setText(upazila);
        detailDistrict.setText(district);
        detailState.setText(state);
        mobile_no.setText(mobile);
        lastDonateDate_d.setText(lastDonateDate);
        Glide.with(this).load(bloodImg_url).into(donorImg);
        mobile_no.setOnClickListener(v -> {
            callActions(mobile);
        });
        d_shareBtn.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TITLE, R.string.app_name);
            sendIntent.putExtra(Intent.EXTRA_TEXT,  "আমি বাঁচাতে চাই একটি প্রাণ, তাইতো করবো রক্তদান!\n" + "এখানে রক্তদাতা সম্পর্কে তথ্য রয়েছে :\nনাম : " + fname+ " " + "\nরক্তের গ্রুপ : " + bloodgroup + "\nঠিকানা: " + village + " ," + upazila + " ," + district + " ," + state + "\nমোবাইল নম্বর : " + mobile);
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, "রক্ত দিন জীবন বাঁচান।");
            startActivity(shareIntent);
        });
//        =============================================
        userRef = FirebaseDatabase.getInstance().getReference().child("Donors");
        like_ref = FirebaseDatabase.getInstance().getReference("likes");
        commentRef = FirebaseDatabase.getInstance().getReference().child("Donors").child(postKey).child("comments");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        userId = currentUser.getUid();//if donor
        account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
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

            }
        }));
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

                }
            });
        });

    }//init

    private void ProcessComment(String userName, String bloodImg_url) {
        String userComment = Objects.requireNonNull(commentField.getText()).toString();
        String parentKey = userId + new Random().nextInt(1000);
        Calendar dateValue = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        String cDate = dateFormat.format(dateValue.getTime());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
        String cTime = timeFormat.format(dateValue.getTime());
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

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<CommentModel> options =
                new FirebaseRecyclerOptions.Builder<CommentModel>()
                        .setQuery(commentRef, CommentModel.class)
                        .build();

        FirebaseRecyclerAdapter<CommentModel, CommentViewHolder> adapter = new FirebaseRecyclerAdapter<CommentModel, CommentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull CommentModel model) {
                holder.cmtName.setText(model.getUserName());
                holder.cmtMsg.setText(model.getUsermsg());
                holder.cmtTime.setText(model.getTime());
                holder.cmtDate.setText(model.getDate());
                Glide.with(holder.itemView.getContext()).load(model.getUserImg()).into(holder.cmtImg);
                Animation itemViewAnim = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.slide_in_left);
                holder.itemView.startAnimation(itemViewAnim);

                commentRef.child(getRef(position).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("snap", "onDataChange: "+snapshot.child("uid").getValue().equals(userId));
                        if (snapshot.child("uid").getValue().equals(userId)){
                            holder.cmt_delete.setImageResource(R.drawable.delete_icon);
                        }else{
                            holder.cmt_delete.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                holder.cmt_delete.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DonorProfile.this);
                    builder.setIcon(R.drawable.ibdc_logo);
                    builder.setTitle(R.string.app_name);
                    builder.setMessage("কমেন্ট ডিলিট করতে চান?");
                    builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                        // Do something when the user clicks the OK button
                        commentRef.child(getRef(position).getKey()).removeValue((error, ref) -> Toast.makeText(DonorProfile.this, "কমেন্ট ডিলিট সফল!", Toast.LENGTH_SHORT).show());
                    });
                    builder.setNegativeButton(R.string.no, (dialog, which) -> {
                        // Do something when the user clicks the Cancel button
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                });

            }

            @NonNull
            @Override
            public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_row, parent, false);
                return new CommentViewHolder(view);
            }
        };

        adapter.startListening();
        cmtRecView.setAdapter(adapter);
        like_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postKey).hasChild(userId)) {
                    int likeCount = (int) snapshot.child(postKey).getChildrenCount();
                    d_like_count.setText(String.valueOf(likeCount));
                    d_like_btn.setImageResource(R.drawable.like_active);
                } else {
                    int likeCount = (int) snapshot.child(postKey).getChildrenCount();
                    d_like_count.setText(String.valueOf(likeCount));
                    d_like_btn.setImageResource(R.drawable.like);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int commentCount = (int) snapshot.getChildrenCount();
                    d_commentsTxt.setText(String.valueOf(commentCount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    public void callActions(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" +number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, Request_call);
        } else {
            startActivity(intent);
        }
    }


    @Override
    public void onBackPressed() {
        AdsControl adsControl = new AdsControl(this);
        Random random;
        random = new Random();
        int myCount = random.nextInt(100 - 5) + 5;
        if (adsControl.passCondition() && myCount % 2 == 0) {
            AdsControl.mInterstitialAd.show(this);
//            Toast.makeText(this, "Ready For Show Ads!" + myCount, Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        } else {
//            Toast.makeText(this, "Something Wrong And Value is : " + Ads.mod + myCount, Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
    }//onBackPressed
}