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

    Button edit_btn, edit_save_btn;
    TextView f_name, l_name, mobile_number_pro, blood_grp_pro, village_pro, tehsil_pro, district_pro, state_pro, lastDonateDate_pro,email_pro;
    ImageView name_Edit, number_Edit, blood_Edit, vil_Edit, upa_Edit, dis_Edit, div_Edit, last_donate_Edit,profile_image,cover_image,email_Edit;
    de.hdodenhof.circleimageview.CircleImageView profile_edit_image;
    Uri filepath;
    URL url;
    Bitmap bitmap;
    String userId;


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
        edit_save_btn = view.findViewById(R.id.edit_save_btn);
        name_Edit = view.findViewById(R.id.name_Edit);
        number_Edit = view.findViewById(R.id.number_Edit);
        blood_Edit = view.findViewById(R.id.blood_Edit);
        vil_Edit = view.findViewById(R.id.vil_Edit);
        upa_Edit = view.findViewById(R.id.upa_Edit);
        dis_Edit = view.findViewById(R.id.dis_Edit);
        div_Edit = view.findViewById(R.id.div_Edit);
        last_donate_Edit = view.findViewById(R.id.last_donate_Edit);
        profile_edit_image = view.findViewById(R.id.profile_edit_image);
        email_Edit = view.findViewById(R.id.email_Edit);
        profile_image = view.findViewById(R.id.profile_image);
        cover_image = view.findViewById(R.id.cover_image);

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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



//        View.OnClickListener
        edit_btn.setOnClickListener(v -> {
            edit_btn.setVisibility(View.GONE);
            edit_save_btn.setVisibility(View.VISIBLE);
            name_Edit.setVisibility(View.VISIBLE);
            number_Edit.setVisibility(View.VISIBLE);
            blood_Edit.setVisibility(View.VISIBLE);
            vil_Edit.setVisibility(View.VISIBLE);
            upa_Edit.setVisibility(View.VISIBLE);
            dis_Edit.setVisibility(View.VISIBLE);
            div_Edit.setVisibility(View.VISIBLE);
            last_donate_Edit.setVisibility(View.VISIBLE);
            profile_edit_image.setVisibility(View.VISIBLE);
            email_Edit.setVisibility(View.VISIBLE);
        });

        edit_save_btn.setOnClickListener(v -> {
            edit_btn.setVisibility(View.VISIBLE);
            edit_save_btn.setVisibility(View.GONE);
            name_Edit.setVisibility(View.GONE);
            number_Edit.setVisibility(View.GONE);
            blood_Edit.setVisibility(View.GONE);
            vil_Edit.setVisibility(View.GONE);
            upa_Edit.setVisibility(View.GONE);
            dis_Edit.setVisibility(View.GONE);
            div_Edit.setVisibility(View.GONE);
            last_donate_Edit.setVisibility(View.GONE);
            profile_edit_image.setVisibility(View.GONE);
            email_Edit.setVisibility(View.GONE);
        });

        profile_edit_image.setOnClickListener(v -> {
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
               profile_image.setImageBitmap(bitmap);
                cover_image.setImageBitmap(bitmap);
                f_name.setText(String.valueOf(bitmap));
                Toast.makeText(getActivity().getApplicationContext(), String.valueOf(bitmap), Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateToFirebase(){

    }


}//base