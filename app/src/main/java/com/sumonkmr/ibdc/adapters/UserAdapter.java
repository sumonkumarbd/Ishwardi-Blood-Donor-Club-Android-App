package com.sumonkmr.ibdc.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sumonkmr.ibdc.DisplayDonorsActivity;
import com.sumonkmr.ibdc.DonorProfile;
import com.sumonkmr.ibdc.R;
import com.sumonkmr.ibdc.SoundManager;
import com.sumonkmr.ibdc.StringCaseConverter;
import com.sumonkmr.ibdc.listeners.MyOnClickListener;
import com.sumonkmr.ibdc.model.User;

import java.util.ArrayList;
import java.util.List;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {


    ArrayList<User> users;
    Context context;
    MyOnClickListener myOnClickListenerCall, myOnClickListenerShare;
    DatabaseReference likeInterface,like_ref;
    Boolean testClick = false;


    public void updateList(ArrayList<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }


    public UserAdapter(Context context, ArrayList<User> users, MyOnClickListener onClickListenerCall, MyOnClickListener onClickListenerShare) {
        this.context = context;
        this.users = users;
        this.myOnClickListenerCall = onClickListenerCall;
        this.myOnClickListenerShare = onClickListenerShare;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserHolder(LayoutInflater.from(context).inflate(R.layout.donor_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
//        like System
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        String userId = currentUser.getUid();
        String postKey = users.get(position).getUid();
        likeInterface = FirebaseDatabase.getInstance().getReference("likes");
        likeInterface.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postKey).hasChild(userId)) {
                    int likeCount = (int) snapshot.child(postKey).getChildrenCount();
                    holder.like_count.setText(String.valueOf(likeCount));
                    holder.like_btn.setImageResource(R.drawable.like_active);
                } else {
                    int likeCount = (int) snapshot.child(postKey).getChildrenCount();
                    holder.like_count.setText(String.valueOf(likeCount));
                    holder.like_btn.setImageResource(R.drawable.like);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        like_ref = FirebaseDatabase.getInstance().getReference("likes");
        holder.like_btn.setOnClickListener(v -> {
            testClick = true;
            like_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (testClick) {
                        if (snapshot.child(postKey).hasChild(userId)) {
                            like_ref.child(postKey).child(userId).removeValue();
                            testClick = false;
                            MediaPlayer.create(context.getApplicationContext(), R.raw.light_switch).start();
                        }else {
                            like_ref.child(postKey).child(userId).setValue(true);
                            testClick = false;
                            MediaPlayer.create(context.getApplicationContext(), R.raw.ping).start();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
//        Like system

//        CommentSYStem
        holder.commentsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DonorProfile.class);
            intent.putExtra("postKey",postKey);
            holder.itemView.getContext().startActivity(intent);
        });
//        CommentSYStem


        String state, district, tehsil, fname, village, bloodgroup, bloodImg_url, lastDonateDate, age;
        state = users.get(position).getState();
        district = users.get(position).getDistrict();
        tehsil = users.get(position).getUpazila();
        village = users.get(position).getVillage();
        bloodgroup = users.get(position).getBloodGroup();
        bloodImg_url = users.get(position).getBloodImg_url();
        lastDonateDate = users.get(position).getLastDonateDate();
        age = users.get(position).getAge();
        fname = String.format("%s %s", users.get(position).getFName(), users.get(position).getLName());
        String s = StringCaseConverter.convertToTitleCaseIteratingChars(fname);
//        holder.state.setText(state);
//        holder.district.setText(district);
        holder.tehsil.setText(tehsil);
        holder.village.setText(village);
        holder.fullName.setText(s);
        holder.bloodGroup.setText(bloodgroup);
        holder.lastDonateDate.setText(lastDonateDate);
//        holder.age.setText(age);
        Glide
                .with(context)
                .load(bloodImg_url)
                .centerCrop()
                .placeholder(R.drawable.ibdc_logo)
                .into(holder.bloodImg);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            holder.state.setText(state);
//            holder.district.setTooltipText(district);
            holder.tehsil.setTooltipText(tehsil);
            holder.village.setTooltipText(village);
            holder.fullName.setTooltipText(fname);
            holder.lastDonateDate.setTooltipText(lastDonateDate);
            holder.bloodGroup.setText(bloodgroup);
//            holder.age.setText(age);
            Glide
                    .with(context)
                    .load(bloodImg_url)
                    .centerCrop()
                    .placeholder(R.drawable.ibdc_logo)
                    .into(holder.bloodImg);
        }

        holder.itemView.setOnClickListener(v -> {
            //for play more with view
//            Toast.makeText(context, String.valueOf(position), Toast.LENGTH_SHORT).show();
        });

        Animation itemViewAnim = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.slide_in_left);
        holder.itemView.startAnimation(itemViewAnim);

    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        holder.share.setOnClickListener(v -> myOnClickListenerShare.getPosition(position));
        holder.call.setOnClickListener(v -> myOnClickListenerCall.getPosition(position));
        try {
            YoYo.with(Techniques.SlideInDown).delay(0).duration(1000).playOn(holder.recViewHeader);
        } catch (Exception e) {
            Log.d("Error", String.valueOf(e.getCause()));
        }

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserHolder extends RecyclerView.ViewHolder {

        TextView fullName, bloodGroup, state, district, tehsil, village, lastDonateDate, age, like_count,commentsTxt;
        ImageView share, call, bloodImg, like_btn,commentsBtn;
        ConstraintLayout recViewHeader;
        CardView dRow_parent;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.dRowFullName);
            bloodGroup = itemView.findViewById(R.id.dRow_bloodG);
//            state = itemView.findViewById(R.id.detailState);
//            district = itemView.findViewById(R.id.detailDistrict);
            tehsil = itemView.findViewById(R.id.dRow_Upa);
            village = itemView.findViewById(R.id.dRowVil);
            call = itemView.findViewById(R.id.dRaw_call);
            share = itemView.findViewById(R.id.dRow_share);
            lastDonateDate = itemView.findViewById(R.id.dRaw_lastDonateDate);
            bloodImg = itemView.findViewById(R.id.dRowImg);
//            age = itemView.findViewById(R.id.age);
            dRow_parent = itemView.findViewById(R.id.dRow_parent);
            like_btn = itemView.findViewById(R.id.like_btn);
            like_count = itemView.findViewById(R.id.like_count);
            commentsBtn = itemView.findViewById(R.id.commentsBtn);
            commentsTxt = itemView.findViewById(R.id.commentsTxt);
        }

    }

}
