package com.sumonkmr.ibdc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.sumonkmr.ibdc.model.User;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

import okhttp3.internal.cache.DiskLruCache;


public class ProfileFragment extends Fragment {

    androidx.constraintlayout.widget.ConstraintLayout profile_tab, profile_edit_tab;
    Button edit_btn, update_btn;
    TextView f_name, l_name, mobile_number_pro, blood_grp_pro, village_pro, tehsil_pro, district_pro, state_pro, lastDonateDate_pro,email_pro;
    de.hdodenhof.circleimageview.CircleImageView profile_image,profile_image_edit,p_image_shade_edit;
    ImageView cover_image;
    Uri filepath;
    Bitmap bitmap;
    String userId,profile_url;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //        Hooks of TextViews
        f_name = view.findViewById(R.id.f_name);
        l_name = view.findViewById(R.id.l_name);
        mobile_number_pro = view.findViewById(R.id.mobile_number_pro);
        blood_grp_pro = view.findViewById(R.id.blood_grp_pro);
        village_pro = view.findViewById(R.id.village_pro);
        tehsil_pro = view.findViewById(R.id.tehsil_pro);
        district_pro = view.findViewById(R.id.district_pro);
        state_pro = view.findViewById(R.id.state_pro);
        email_pro = view.findViewById(R.id.email_pro);
        lastDonateDate_pro = view.findViewById(R.id.lastDonateDate_pro);

//        Hooks of edit buttons
        edit_btn = view.findViewById(R.id.edit_btn);
        update_btn = view.findViewById(R.id.update_btn);
        profile_tab = view.findViewById(R.id.profile_tab);
        profile_edit_tab = view.findViewById(R.id.profile_edit_tab);
        profile_image = view.findViewById(R.id.profile_image);
        cover_image = view.findViewById(R.id.cover_image);
        profile_image_edit = view.findViewById(R.id.profile_image_edit);
        p_image_shade_edit = view.findViewById(R.id.p_image_shade_edit);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbReference = db.getReference().child("Donors");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        dbReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    f_name.setText(Objects.requireNonNull(snapshot.child("FName").getValue()).toString());
                    l_name.setText(Objects.requireNonNull(snapshot.child("LName").getValue()).toString());
                    mobile_number_pro.setText(Objects.requireNonNull(snapshot.child("Mobile").getValue()).toString());
                    blood_grp_pro.setText(Objects.requireNonNull(snapshot.child("BloodGroup").getValue()).toString());
                    village_pro.setText(Objects.requireNonNull(snapshot.child("Village").getValue()).toString());
                    tehsil_pro.setText(Objects.requireNonNull(snapshot.child("Upazila").getValue()).toString());
                    district_pro.setText(Objects.requireNonNull(snapshot.child("District").getValue()).toString());
                    state_pro.setText(Objects.requireNonNull(snapshot.child("State").getValue()).toString());
                    email_pro.setText(Objects.requireNonNull(snapshot.child("Email").getValue()).toString());
                    lastDonateDate_pro.setText(Objects.requireNonNull(snapshot.child("lastDonateDate").getValue()).toString());
                    profile_url = (Objects.requireNonNull(snapshot.child("bloodImg_url").getValue()).toString());

                        Glide
                                .with(view)
                                .load(profile_url)
                                .centerCrop()
                                .placeholder(R.drawable.ibdc_logo)
                                .into(profile_image);

                        Glide
                                .with(view)
                                .load(profile_url)
                                .centerCrop()
                                .placeholder(R.drawable.ibdc_logo)
                                .into((ImageView) cover_image);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



//        View.OnClickListener
        edit_btn.setOnClickListener(v -> {
            profile_tab.setVisibility(View.GONE);
            profile_edit_tab.setVisibility(View.VISIBLE);
            edit_btn.setVisibility(View.GONE);
            update_btn.setVisibility(View.VISIBLE);
        });

        update_btn.setOnClickListener(v -> {
            profile_edit_tab.setVisibility(View.GONE);
            profile_tab.setVisibility(View.VISIBLE);
            update_btn.setVisibility(View.GONE);
            edit_btn.setVisibility(View.VISIBLE);

        });

        p_image_shade_edit.setOnClickListener(v -> {
            Dexter.withContext(requireContext().getApplicationContext())
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            intent.setAction(intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Browse For Image"), 101);
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).check();
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 101 && resultCode == 1) {
            filepath = data.getData();
            try {
               InputStream inputStream = requireActivity().getContentResolver().openInputStream(filepath);
               bitmap = BitmapFactory.decodeStream(inputStream);
                profile_image_edit.setImageBitmap(bitmap);
            } catch (Exception ex) {
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateToFirebase(){

    }


}//base