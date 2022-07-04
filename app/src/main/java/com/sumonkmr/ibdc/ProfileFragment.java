package com.sumonkmr.ibdc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Calendar;
import java.util.Objects;

import okhttp3.internal.cache.DiskLruCache;


public class ProfileFragment extends Fragment {

    Context context;
    androidx.constraintlayout.widget.ConstraintLayout profile_tab, profile_edit_tab;
    Button edit_btn,save_btn, update_btn;
    TextView f_name, l_name, mobile_number_pro, blood_grp_pro, village_pro, tehsil_pro, district_pro, state_pro, lastDonateDate_pro,email_pro;
    com.google.android.material.textfield.TextInputEditText f_name_edit,l_name_edit,village_edit;
    de.hdodenhof.circleimageview.CircleImageView profile_image,profile_image_edit,p_image_shade_edit;
    AutoCompleteTextView Division,District,Upazila,bloodGrpDropDown,lastDonateDate_edit;
    ImageView cover_image;
    Uri filepath;
    Bitmap bitmap;
    String userId,profile_url;
    int day,month,year;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getContext();

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
        save_btn = view.findViewById(R.id.save_btn);
        update_btn = view.findViewById(R.id.update_btn);
        profile_tab = view.findViewById(R.id.profile_tab);
        profile_edit_tab = view.findViewById(R.id.profile_edit_tab);
        profile_image = view.findViewById(R.id.profile_image);
        cover_image = view.findViewById(R.id.cover_image);
        profile_image_edit = view.findViewById(R.id.profile_image_edit);
        p_image_shade_edit = view.findViewById(R.id.p_image_shade_edit);
        f_name_edit = view.findViewById(R.id.f_name_edit);
        l_name_edit = view.findViewById(R.id.l_name_edit);
        village_edit = view.findViewById(R.id.village_edit);
        Upazila = view.findViewById(R.id.upazilaDropDrown_edit);
        District = view.findViewById(R.id.districtDropDrown_edit);
        Division = view.findViewById(R.id.stateDropDrown_edit);
        bloodGrpDropDown = view.findViewById(R.id.bloodGrpDropDown_edit);
        lastDonateDate_edit = view.findViewById(R.id.lastDonateDate_edit);
        
        initializeAddressFilters();
        LastDonateDatePicker();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbReference = db.getReference().child("Donors");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        dbReference.child(userId).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SuspiciousIndentation")
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

//                    Edit tabs
                    f_name_edit.setText(Objects.requireNonNull(snapshot.child("FName").getValue()).toString());
                    l_name_edit.setText(Objects.requireNonNull(snapshot.child("LName").getValue()).toString());
                    village_edit.setText(Objects.requireNonNull(snapshot.child("Village").getValue()).toString());
                    Upazila.setText(Objects.requireNonNull(snapshot.child("Upazila").getValue()).toString());
                    District.setText(Objects.requireNonNull(snapshot.child("District").getValue()).toString());
                    Division.setText(Objects.requireNonNull(snapshot.child("State").getValue()).toString());
                    bloodGrpDropDown.setText(Objects.requireNonNull(snapshot.child("BloodGroup").getValue()).toString());
                    lastDonateDate_edit.setText(Objects.requireNonNull(snapshot.child("lastDonateDate").getValue()).toString());

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

                    Glide
                            .with(view)
                            .load(profile_url)
                            .centerCrop()
                            .placeholder(R.drawable.ibdc_logo)
                            .into((ImageView) profile_image_edit);

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
            save_btn.setVisibility(View.VISIBLE);
        });

        save_btn.setOnClickListener(v -> {
            save_btn.setVisibility(View.GONE);
            update_btn.setVisibility(View.VISIBLE);
        });

        update_btn.setOnClickListener(v -> {
            profile_edit_tab.setVisibility(View.GONE);
            profile_tab.setVisibility(View.VISIBLE);
            update_btn.setVisibility(View.GONE);
            edit_btn.setVisibility(View.VISIBLE);

        });

        p_image_shade_edit.setOnClickListener(v -> {
            Dexter.withContext(context)
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
        if (requestCode == 101 && resultCode == -1) {
            assert data != null;
            filepath = data.getData();
            try {
               InputStream inputStream = context.getContentResolver().openInputStream(filepath);
               bitmap = BitmapFactory.decodeStream(inputStream);
                profile_image_edit.setImageBitmap(bitmap);
            } catch (Exception ex) {
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void initializeAddressFilters() {
        Division.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Division.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.states)));
            }
        });

        Division.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.states)));

        Division.setOnItemClickListener((parent, view, position, id) -> {

            switch (Division.getText().toString()){
                case "ঢাকা":
                    District.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.dhaka_division)));
                    break;

                case "রাজশাহী":
                    District.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.rajshahi_division)));
                    break;

                case "চট্টগ্রাম":
                    District.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.chittagong_division)));
                    break;

                case "খুলনা":
                    District.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.khulna_division)));
                    break;

                case "বরিশাল":
                    District.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.barisal_division)));
                    break;

                case "রংপুর":
                    District.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.rangpur_division)));
                    break;

                case "সিলেট":
                    District.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.sylet_division)));
                    break;

                case "ময়মনসিংহ":
                    District.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.mymanshingh_division)));
                    break;

                default:
                    Toast.makeText(context, "যেকোনো একটি বিভাগ সিলেক্ট করুন!", Toast.LENGTH_SHORT).show();


            }
        });

        District.setOnItemClickListener((parent, view, position, id) -> {
            switch (District.getText().toString()){

//                Khulna Division
                case "খুলনা":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.khulna)));
                    break;

                case "কুষ্টিয়া":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.kushtia)));
                    break;

                case "চুয়াডাঙ্গা":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.chuadanga)));
                    break;

                case "ঝিনাইদহ":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.jhinaidah)));
                    break;

                case "নড়াইল":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.narail)));
                    break;

                case "বাগেরহাট":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.bagerhat)));
                    break;

                case "মাগুরা":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.magura)));
                    break;

                case "মেহেরপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.meherpur)));
                    break;

                case "যশোর":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.jossore)));
                    break;

                case "সাতক্ষীরা":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.satkhira)));
                    break;
//                    end khulna Division

//                Barishal Division
                case "বরিশাল":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.barishal)));
                    break;

                case "বরগুনা":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.borguna)));
                    break;

                case "ভোলা":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.bhola)));
                    break;

                case "ঝালকাঠি":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.jhalkhathi)));
                    break;

                case "পটুয়াখালী":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.potuyakhali)));
                    break;

                case "পিরোজপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.pirojpur)));
                    break;
//                    end Barishal

//                Chittagong Division
                case "চট্টগ্রাম":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.chittagong)));
                    break;

                case "বান্দরবান":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.bandarban)));
                    break;

                case "ব্রাহ্মণবাড়িয়া":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.brammanbaria)));
                    break;

                case "চাঁদপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.chadpur)));
                    break;

                case "কুমিল্লা":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.cumilla)));
                    break;

                case "কক্সবাজার":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.coxsbazar)));
                    break;

                case "ফেনী":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.feni)));
                    break;

                case "খাগড়াছড়ি":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.khagrachory)));
                    break;

                case "লক্ষ্মীপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.laxmipur)));
                    break;

                case "নোয়াখালী":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.noyakhali)));
                    break;

                case "রাঙ্গামাটি":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.rangamati)));
                    break;
//               end Chittagong Division
                case "ঢাকা":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.dhaka)));
                    break;

                case "ফরিদপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.faridpur)));
                    break;

                case "গাজীপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.gazipur)));
                    break;

                case "গোপালগঞ্জ":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.gopalgonj)));
                    break;

                case "কিশোরগঞ্জ":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.kishoregonj)));
                    break;

                case "মাদারীপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.madaripur)));
                    break;

                case "মানিকগঞ্জ":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.manikgonj)));
                    break;

                case "মুন্সীগঞ্জ":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.munsigonj)));
                    break;

                case "নারায়ণগঞ্জ":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.narayangonj)));
                    break;

                case "নরসিংদী":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.narshindhi)));
                    break;

                case "রাজবাড়ী":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.rajbari)));
                    break;

                case "শরীয়তপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.shoriyotpur)));
                    break;

                case "টাঙ্গাইল":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.tangail)));
                    break;
//                End  Dhaka Division


//                 mymanshigh Division
                case "ময়মনসিংহ":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.mymanshingh)));
                    break;

                case "জামালপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.jamalpur)));
                    break;

                case "নেত্রকোণা":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.netrokona)));
                    break;

                case "শেরপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.sherpur)));
                    break;
//              End mymanshigh Division

//                Rajshahi Division
                case "রাজশাহী":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.rajshahi)));
                    break;

                case "পাবনা":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.pabna)));
                    break;

                case "নাটোর":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.natore)));
                    break;

                case "বগুড়া":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.bogura)));
                    break;

                case "নওগাঁ":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.nogone)));
                    break;

                case "জয়পুরহাট":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.jaypurhat)));
                    break;

                case "চাঁপাইনবাবগঞ্জ":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.Chapainababgonj)));
                    break;

                case "সিরাজগঞ্জ":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.sirajgonj)));
                    break;
//               End Rajshahi Division

//                Rangpur Division
                case "রংপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.rangpur)));
                    break;

                case "দিনাজপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.dinajpur)));
                    break;

                case "গাইবান্ধা":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.gaibandha)));
                    break;

                case "কুড়িগ্রাম":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.kurigram)));
                    break;

                case "লালমনিরহাট":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.lalmonirhat)));
                    break;

                case "নীলফামারী":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.niphamari)));
                    break;

                case "পঞ্চগড়":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.ponchogarh)));
                    break;

                case "ঠাকুরগাঁও":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.thakurgao)));
                    break;
//             End Rangpur Division

//                Sylet Division
                case "সিলেট":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.sylet)));
                    break;

                case "হবিগঞ্জ":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.hobigonj)));
                    break;

                case "মৌলভীবাজার":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.molovibazar)));
                    break;

                case "সুনামগঞ্জ":
                    Upazila.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.sunamgonj)));
                    break;
//               End Sylet Division

                default:
                    Toast.makeText(context, "যেকোনো একটি জেলা সিলেক্ট করুন!", Toast.LENGTH_SHORT).show();


            }
        });

        String[] bloodGroups = getResources().getStringArray(R.array.blood_groups);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line,bloodGroups);
        bloodGrpDropDown.setAdapter(adapter);

    }

    private void LastDonateDatePicker(){
        final Calendar calendar = Calendar.getInstance();

        lastDonateDate_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        lastDonateDate_edit.setText(dayOfMonth+"/"+month+"/"+year);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });
    }

    private void updateToFirebase(){

    }


}//base