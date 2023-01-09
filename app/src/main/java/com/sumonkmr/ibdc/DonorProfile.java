package com.sumonkmr.ibdc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.sumonkmr.ibdc.model.CommentModel;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class DonorProfile extends AppCompatActivity {

    TextInputEditText commentField;
    ImageButton comment_done;
    DatabaseReference userRef,commentRef;
    String postKey;
    String userId;
    RecyclerView cmtRecView;
    FirebaseUser currentUser;
    GoogleSignInAccount account;

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
        commentField = findViewById(R.id.commentField);
        comment_done = findViewById(R.id.comment_done);
        cmtRecView = findViewById(R.id.cmtRecView);
        cmtRecView.setLayoutManager(new LinearLayoutManager(this));
        postKey = getIntent().getStringExtra("postKey");
        userRef = FirebaseDatabase.getInstance().getReference().child("Donors");
        commentRef = FirebaseDatabase.getInstance().getReference().child("Donors").child(postKey).child("comments");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        userId = currentUser.getUid();//if donor
        account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        comment_done.setOnClickListener(v -> userRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String fName = Objects.requireNonNull(snapshot.child("FName").getValue()).toString();
                    String lName = Objects.requireNonNull(snapshot.child("LName").getValue()).toString();
                    String bloodImg_url = Objects.requireNonNull(snapshot.child("bloodImg_url").getValue()).toString();
                    String userName = fName+" "+lName;
                    ProcessComment(userName,bloodImg_url);
                }else {
                    assert account != null;
                    String bloodImg_url = String.valueOf(account.getPhotoUrl());
                    String userName = account.getDisplayName();
                    ProcessComment(userName,bloodImg_url);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }));

    }

    private void ProcessComment(String userName, String bloodImg_url) {
        String userComment = Objects.requireNonNull(commentField.getText()).toString();
        String parentKey = userId+""+new Random().nextInt(1000);
        Calendar dateValue = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        String cDate = dateFormat.format(dateValue.getTime());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
        String cTime = timeFormat.format(dateValue.getTime());

        userRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    HashMap cmnt = new HashMap();
                    cmnt.put("uid",userId);
                    cmnt.put("userName",userName);
                    cmnt.put("userImg",bloodImg_url);
                    cmnt.put("usermsg",userComment);
                    cmnt.put("date",cDate);
                    cmnt.put("time",cTime);
                    if (commentField.getText().length() != 0) {
                        commentRef.child(parentKey).updateChildren(cmnt).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(DonorProfile.this, "কমেন্ট সম্পূর্ণ!", Toast.LENGTH_SHORT).show();
                                commentField.setText("");
                            } else {
                                Toast.makeText(DonorProfile.this, "কমেন্ট যোগ হয়নি।", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        Toast.makeText(getApplicationContext(), "ডোনার সম্পর্কে কিছু লিখুন।", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    HashMap cmnt = new HashMap();
                    cmnt.put("uid",account.getId());
                    cmnt.put("userName",userName);
                    cmnt.put("userImg",bloodImg_url);
                    cmnt.put("usermsg",userComment);
                    cmnt.put("date",cDate);
                    cmnt.put("time",cTime);
                    if (commentField.getText().length() != 0) {
                        commentRef.child(parentKey).updateChildren(cmnt).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(DonorProfile.this, "কমেন্ট সম্পূর্ণ!", Toast.LENGTH_SHORT).show();
                                commentField.setText("");
                            } else {
                                Toast.makeText(DonorProfile.this, "কমেন্ট যোগ হয়নি।", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
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
                Animation itemViewAnim = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.slide_in_left);
                holder.itemView.startAnimation(itemViewAnim);

            }

            @NonNull
            @Override
            public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_row,parent,false);
                return new CommentViewHolder(view);
            }
        };

        adapter.startListening();
        cmtRecView.setAdapter(adapter);

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