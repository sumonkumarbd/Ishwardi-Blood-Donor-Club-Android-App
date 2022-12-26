package com.sumonkmr.ibdc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    DuoDrawerLayout drawerLayout;
    final String regexStr = "^\\s?((\\+[1-9]{1,4}[ \\-]*)|(\\([0-9]{2,3}\\)[ \\-]*)|([0-9]{2,4})[ \\-]*)*?[0-9]{3,4}?[ \\-]*[0-9]{3,4}?\\s?";
    com.google.android.material.textfield.TextInputEditText f_name_signUp, l_name_signUp, number_signUp, village_signUp;
    de.hdodenhof.circleimageview.CircleImageView profile_image_signUp, p_image_shade_signUp;
    AutoCompleteTextView Division, District, Upazila, bloodGrpDropDown, lastDonateDate_signUp;
    DatePicker birthDate_signUp;
    de.hdodenhof.circleimageview.CircleImageView profile_image_menu, sds_image;
    TextView profile_name_menu, sds_dash, mail;
    int d, m, y;
    Dialog dialog;
    Uri filepath, profileImg_uri;
    FirebaseAuth auth;
    DatabaseReference dbReference;
    String userId, adsData;
    CheckBox lastDonate_check;
    boolean doubleBackToExitPressedOnce = false;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    GoogleSignInAccount account;
    private StorageReference storageReference;
    private FirebaseDatabase db;
    private ProgressBar progressbar;
    final Calendar calendar = Calendar.getInstance();
    MediaPlayer btn;
    MediaPlayer okkBtn;
    MediaPlayer cbtN;
    MediaPlayer great_sound;
    AdView mAdView;
    Intent targetActivity;
    // per app run-- do not show more than 3 fullscreen ad. [[Change it if your want]]
    int fullScreenAdMaxShowCount = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        mAdView = findViewById(R.id.adView);
        AdsControl ads = new AdsControl(this, mAdView); // for initialize Banner Ads

        //        Database references
        FirebaseUser currentUser = auth.getCurrentUser();
        assert currentUser != null;
        userId = currentUser.getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseDatabase.getInstance();
        dbReference = db.getReference();
        account = GoogleSignIn.getLastSignedInAccount(this);
        init();
        Sounds();
    }//onCreate finished

    private void init() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DuoDrawerLayout) findViewById(R.id.drawer);
        DuoDrawerToggle drawerToggle = new DuoDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        View contentView = drawerLayout.getContentView();
        View menuView = drawerLayout.getMenuView();


        LinearLayout ll_Home = menuView.findViewById(R.id.ll_Home);
        LinearLayout ll_Profile = menuView.findViewById(R.id.ll_Profile);
        LinearLayout ll_beDonor = menuView.findViewById(R.id.ll_beDonor);
        LinearLayout ll_Setting = menuView.findViewById(R.id.ll_Setting);
        LinearLayout ll_fb = menuView.findViewById(R.id.ll_fb);
        LinearLayout ll_Share = menuView.findViewById(R.id.ll_Share);
        LinearLayout ll_about_us = menuView.findViewById(R.id.ll_about_us);
        LinearLayout ll_privacy = menuView.findViewById(R.id.ll_privacy);
        LinearLayout ll_Logout = menuView.findViewById(R.id.ll_Logout);
        profile_name_menu = menuView.findViewById(R.id.profile_name_menu);
        profile_image_menu = menuView.findViewById(R.id.profile_image_menu);
        sds_dash = menuView.findViewById(R.id.sds_dash);
        sds_image = menuView.findViewById(R.id.sds_image);
        mail = menuView.findViewById(R.id.mail);

        if (account != null) {
            Uri profile_img_url = (account.getPhotoUrl());
            String f_name = (account.getDisplayName());
            profile_name_menu.setText(f_name);
            Glide.with(this)
                    .load(profile_img_url)
                    .centerCrop()
                    .placeholder(R.drawable.ibdc_logo)
                    .into(profile_image_menu);
        }


        ll_Home.setOnClickListener(this);
        ll_Profile.setOnClickListener(this);
        ll_beDonor.setOnClickListener(this);
        ll_Setting.setOnClickListener(this);
        ll_fb.setOnClickListener(this);
        ll_Share.setOnClickListener(this);
        ll_Logout.setOnClickListener(this);
        ll_about_us.setOnClickListener(this);
        ll_privacy.setOnClickListener(this);
        sds_dash.setOnClickListener(this);
        sds_image.setOnClickListener(this);
        mail.setOnClickListener(this);


        replace(new HomeFragment());


    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_Home:
                btn.start();
                replace(new HomeFragment(), "Home");
                break;

            case R.id.ll_Profile:
                btn.start();
                replace(new ProfileFragment(), "Profile");
                break;

            case R.id.ll_beDonor:
                btn.start();
                OpenDialog();
                break;

            case R.id.ll_Setting:
                btn.start();
                replace(new SettingFragments(), "Setting");
                break;

            case R.id.ll_fb:
                btn.start();
                gotoUrl("https://www.facebook.com/groups/ibdcbd");
                break;

            case R.id.ll_Share:
                btn.start();
                shareApp();
                break;

            case R.id.ll_about_us:
                btn.start();
                startActivity(new Intent(this, AboutUs.class));
                break;

            case R.id.ll_privacy:
                btn.start();
                gotoUrl("https://sites.google.com/view/ibdcprivacypolicy/home/");
                break;

            case R.id.ll_Logout:
                cbtN.start();
                auth.signOut();
                startActivity(new Intent(this, SplashScreen.class));
                MainActivity.this.finish();
                break;

            case R.id.mail:
                btn.start();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"sumonkmrofficial@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Im Sending Email From IBDC App");
                intent.putExtra(Intent.EXTRA_TEXT, "Please Type Your Massage Here : \n");
                intent.setType("massage/rfc822");
                startActivity(intent);
                break;

            case R.id.sds_dash:
                btn.start();
                gotoUrl("https://www.facebook.com/sumonkmr.studio/");
                break;

            case R.id.sds_image:
                btn.start();
                gotoUrl("https://play.google.com/store/apps/dev?id=6877143126125387449");
                break;


//            case R.id.find_donors:
////                startActivity(new Intent(this, DisplayDonorsActivity.class));
//                Toast.makeText(this, "This is Donors", Toast.LENGTH_SHORT).show();
//                break;

        }// switch finished


        drawerLayout.closeDrawer();
    }

    //////////////////////////////////////////////////////
    //Profile Image setUp//

    private void ChooseProfilePicture() {
        p_image_shade_signUp.setOnClickListener(v -> {
            final MediaPlayer profileImg_choose = MediaPlayer.create(getApplicationContext(), R.raw.mousemp3);
            profileImg_choose.start();
            Dexter.withContext(getApplicationContext())
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 101 && resultCode == -1) {
            try {
                assert data != null;
                filepath = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filepath);
                ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
                File path = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DCIM);
                String fileName = String.format("%s.jpg", account.getEmail());
                File finalFile = new File(path, fileName);
                FileOutputStream fileOutputStream = new FileOutputStream(finalFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                profile_image_signUp.setImageURI(Uri.fromFile(finalFile));
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(finalFile));
                sendBroadcast(intent);
                profileImg_uri = intent.getData();

            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(this, "ছবি আপলোডে অসফল!", Toast.LENGTH_SHORT).show();
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void FinalUploadDatabase() {
        if (profileImg_uri != null) {
            progressbar = dialog.findViewById(R.id.progressbar_signUp);
            final StorageReference uploader = storageReference.child(String.format("profile_image/User Email : %s/profile_picture.%s", account.getEmail(), ".jpg"));
            uploader.putFile(profileImg_uri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(getApplicationContext(), R.string.Updated_img, Toast.LENGTH_SHORT).show();
                        uploader.getDownloadUrl().addOnSuccessListener(uri -> {
                            if (!profileImg_uri.toString().isEmpty()) {
                                addToDatabase(uri);
                                progressbar.setProgressDrawable(getDrawable(R.drawable.progress_bar_success));
                                great_sound.start();
                                dialog.dismiss();
                            }
                        });
                    })
                    .addOnProgressListener(snapshot -> {
                        long per = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        progressbar.setProgress((int) per);
                        progressbar.setMax(100);
                        Toast.makeText(getApplicationContext(), R.string.updating, Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), R.string.failed, Toast.LENGTH_SHORT).show();
                        }
                    });

        }

    }


//    private void profileImg(Uri uri) {
//        HashMap<String, Object> values = new HashMap<>();
//        values.put("bloodImg_url", uri.toString());
//        FirebaseDatabase.getInstance().getReference("Donors/" + account.getId())
//                .updateChildren(values)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(getApplicationContext(), R.string.Updated, Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(getApplicationContext(), R.string.failed, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

    //////////////////////////////////////////////////////

    private void OpenDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.be_donor_dialog);
        Button ok_Btn, cBtn;

        account = GoogleSignIn.getLastSignedInAccount(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_background));
        }

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog
        dialog.show();


        ok_Btn = dialog.findViewById(R.id.ok_Btn);
        cBtn = dialog.findViewById(R.id.cBtn);
        f_name_signUp = dialog.findViewById(R.id.f_name_signUp);
        l_name_signUp = dialog.findViewById(R.id.l_name_signUp);
        number_signUp = dialog.findViewById(R.id.number_signUp);
        village_signUp = dialog.findViewById(R.id.village_signUp);
        profile_image_signUp = dialog.findViewById(R.id.profile_image_signUp);
        p_image_shade_signUp = dialog.findViewById(R.id.p_image_shade_signUp);
        Division = dialog.findViewById(R.id.stateDropDrown_signUp);
        District = dialog.findViewById(R.id.districtDropDrown_signUp);
        Upazila = dialog.findViewById(R.id.upazilaDropDrown_signUp);
        bloodGrpDropDown = dialog.findViewById(R.id.bloodGrpDropDown_signUp);
        lastDonateDate_signUp = dialog.findViewById(R.id.lastDonateDate_signUp);
        birthDate_signUp = dialog.findViewById(R.id.birthDate_signUp);
        lastDonate_check = dialog.findViewById(R.id.lastDonate_check_signUp);

        lastDonate_check.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                lastDonateDate_signUp.setEnabled(true);
                lastDonateDate_signUp.setText("");
            } else {
                lastDonateDate_signUp.setEnabled(false);
                lastDonateDate_signUp.setError(null);
                lastDonateDate_signUp.setText(R.string.last_donate_never);
            }
        });


//        Functions
        initializeAddressFilters();
        FromDatePickers();
        ChooseProfilePicture();

        //        processImageUpload(profileImg_uri);

        ok_Btn.setOnClickListener(v -> {
            btn.start();//sound effect
            if (!validateInput()) {
                Toast.makeText(this, "সবগুলি তথ্য দিন!", Toast.LENGTH_SHORT).show();
            } else {
                FinalUploadDatabase();
            }
        });

        cBtn.setOnClickListener(v -> {
            cbtN.start(); //sound effect
//            auth.signOut();
//            startActivity(new Intent(this, SplashScreen.class));
            dialog.dismiss();
//            MainActivity.this.finish();
        });


    }//Finished Dialog

    private void addToDatabase(Uri uri) {
        String day, month, year, date;
        day = String.valueOf(birthDate_signUp.getDayOfMonth());
        month = String.valueOf(birthDate_signUp.getMonth());
        year = String.valueOf(birthDate_signUp.getYear());
        date = day + "/" + month + "/" + year;
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int age =currentYear - Integer.parseInt(year);
        HashMap<String, Object> values = new HashMap<>();
        values.put("uid", userId);
        values.put("bloodImg_url", uri.toString());
        values.put("FName", Objects.requireNonNull(f_name_signUp.getText()).toString());
        values.put("LName", Objects.requireNonNull(l_name_signUp.getText()).toString());
        values.put("Mobile", Objects.requireNonNull(number_signUp.getText()).toString());
        values.put("Email", account.getEmail());
        values.put("State", Division.getText().toString());
        values.put("District", District.getText().toString());
        values.put("Upazila", Upazila.getText().toString());
        values.put("Village", Objects.requireNonNull(village_signUp.getText()).toString());
        values.put("BloodGroup", bloodGrpDropDown.getText().toString());
        values.put("birthdate", date);
        values.put("age",String.valueOf(age));
        if (lastDonate_check.isChecked()) {
            values.put("lastDonateDate", "পূর্বে করিনি।");
        } else {
            values.put("lastDonateDate", lastDonateDate_signUp.getText().toString());
        }

        FirebaseDatabase.getInstance().getReference("Donors/" + FirebaseAuth.getInstance().getUid())
                .updateChildren(values)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
//                        profileImg(uri);
                        Toast.makeText(getApplicationContext(), R.string.Updated, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.failed, Toast.LENGTH_SHORT).show();
                    }
                });

    }//addToDatabase

    private void initializeAddressFilters() {
        Division.setOnClickListener(v -> Division.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.states))));

        Division.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.states)));

        Division.setOnItemClickListener((parent, view, position, id) -> {

            switch (Division.getText().toString()) {
                case "ঢাকা":
                    District.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.dhaka_division)));
                    break;

                case "রাজশাহী":
                    District.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.rajshahi_division)));
                    break;

                case "চট্টগ্রাম":
                    District.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.chittagong_division)));
                    break;

                case "খুলনা":
                    District.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.khulna_division)));
                    break;

                case "বরিশাল":
                    District.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.barisal_division)));
                    break;

                case "রংপুর":
                    District.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.rangpur_division)));
                    break;

                case "সিলেট":
                    District.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.sylet_division)));
                    break;

                case "ময়মনসিংহ":
                    District.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.mymanshingh_division)));
                    break;

                default:
                    Toast.makeText(getApplicationContext(), "যেকোনো একটি বিভাগ সিলেক্ট করুন!", Toast.LENGTH_SHORT).show();


            }
        });

        District.setOnItemClickListener((parent, view, position, id) -> {
            switch (District.getText().toString()) {

//                Khulna Division
                case "খুলনা":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.khulna)));
                    break;

                case "কুষ্টিয়া":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.kushtia)));
                    break;

                case "চুয়াডাঙ্গা":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.chuadanga)));
                    break;

                case "ঝিনাইদহ":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.jhinaidah)));
                    break;

                case "নড়াইল":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.narail)));
                    break;

                case "বাগেরহাট":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.bagerhat)));
                    break;

                case "মাগুরা":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.magura)));
                    break;

                case "মেহেরপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.meherpur)));
                    break;

                case "যশোর":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.jossore)));
                    break;

                case "সাতক্ষীরা":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.satkhira)));
                    break;
//                    end khulna Division

//                Barishal Division
                case "বরিশাল":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.barishal)));
                    break;

                case "বরগুনা":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.borguna)));
                    break;

                case "ভোলা":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.bhola)));
                    break;

                case "ঝালকাঠি":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.jhalkhathi)));
                    break;

                case "পটুয়াখালী":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.potuyakhali)));
                    break;

                case "পিরোজপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.pirojpur)));
                    break;
//                    end Barishal

//                Chittagong Division
                case "চট্টগ্রাম":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.chittagong)));
                    break;

                case "বান্দরবান":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.bandarban)));
                    break;

                case "ব্রাহ্মণবাড়িয়া":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.brammanbaria)));
                    break;

                case "চাঁদপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.chadpur)));
                    break;

                case "কুমিল্লা":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.cumilla)));
                    break;

                case "কক্সবাজার":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.coxsbazar)));
                    break;

                case "ফেনী":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.feni)));
                    break;

                case "খাগড়াছড়ি":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.khagrachory)));
                    break;

                case "লক্ষ্মীপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.laxmipur)));
                    break;

                case "নোয়াখালী":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.noyakhali)));
                    break;

                case "রাঙ্গামাটি":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.rangamati)));
                    break;
//               end Chittagong Division
                case "ঢাকা":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.dhaka)));
                    break;

                case "ফরিদপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.faridpur)));
                    break;

                case "গাজীপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.gazipur)));
                    break;

                case "গোপালগঞ্জ":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.gopalgonj)));
                    break;

                case "কিশোরগঞ্জ":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.kishoregonj)));
                    break;

                case "মাদারীপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.madaripur)));
                    break;

                case "মানিকগঞ্জ":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.manikgonj)));
                    break;

                case "মুন্সীগঞ্জ":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.munsigonj)));
                    break;

                case "নারায়ণগঞ্জ":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.narayangonj)));
                    break;

                case "নরসিংদী":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.narshindhi)));
                    break;

                case "রাজবাড়ী":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.rajbari)));
                    break;

                case "শরীয়তপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.shoriyotpur)));
                    break;

                case "টাঙ্গাইল":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.tangail)));
                    break;
//                End  Dhaka Division


//                 mymanshigh Division
                case "ময়মনসিংহ":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.mymanshingh)));
                    break;

                case "জামালপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.jamalpur)));
                    break;

                case "নেত্রকোণা":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.netrokona)));
                    break;

                case "শেরপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.sherpur)));
                    break;
//              End mymanshigh Division

//                Rajshahi Division
                case "রাজশাহী":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.rajshahi)));
                    break;

                case "পাবনা":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.pabna)));
                    break;

                case "নাটোর":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.natore)));
                    break;

                case "বগুড়া":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.bogura)));
                    break;

                case "নওগাঁ":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.nogone)));
                    break;

                case "জয়পুরহাট":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.jaypurhat)));
                    break;

                case "চাঁপাইনবাবগঞ্জ":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.Chapainababgonj)));
                    break;

                case "সিরাজগঞ্জ":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.sirajgonj)));
                    break;
//               End Rajshahi Division

//                Rangpur Division
                case "রংপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.rangpur)));
                    break;

                case "দিনাজপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.dinajpur)));
                    break;

                case "গাইবান্ধা":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.gaibandha)));
                    break;

                case "কুড়িগ্রাম":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.kurigram)));
                    break;

                case "লালমনিরহাট":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.lalmonirhat)));
                    break;

                case "নীলফামারী":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.niphamari)));
                    break;

                case "পঞ্চগড়":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.ponchogarh)));
                    break;

                case "ঠাকুরগাঁও":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.thakurgao)));
                    break;
//             End Rangpur Division

//                Sylet Division
                case "সিলেট":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.sylet)));
                    break;

                case "হবিগঞ্জ":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.hobigonj)));
                    break;

                case "মৌলভীবাজার":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.molovibazar)));
                    break;

                case "সুনামগঞ্জ":
                    Upazila.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.sunamgonj)));
                    break;
//               End Sylet Division

                default:
                    Toast.makeText(getApplicationContext(), "যেকোনো একটি জেলা সিলেক্ট করুন!", Toast.LENGTH_SHORT).show();


            }
        });

        bloodGrpDropDown.setOnClickListener(v -> {
            String[] bloodGroups = getResources().getStringArray(R.array.blood_groups);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, bloodGroups);
            bloodGrpDropDown.setAdapter(adapter);
        });

    }//initializeAddressFilters Finished

    private void FromDatePickers() {
        lastDonateDate_signUp.setOnClickListener(v -> {
            y = calendar.get(Calendar.YEAR);
            m = calendar.get(Calendar.MONTH);
            d = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, (view, year, month, dayOfMonth) -> lastDonateDate_signUp.setText(dayOfMonth + "/" + (month + 1) + "/" + year), y, m, d);
            datePickerDialog.show();
        });

//        birthDate_signUp.setOnClickListener(v -> {
//            y = calendar.get(Calendar.YEAR);
//            m = calendar.get(Calendar.MONTH);
//            d = calendar.get(Calendar.DAY_OF_MONTH);
//            DatePickerDialog datePickerDialog = new DatePickerDialog(DashBoard.this, (view, year, month, dayOfMonth) -> {
//                birthDate_signUp.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
//            }, y, m, d);
//            datePickerDialog.show();
//        });
    }//LastDonateDatePicker Finished


    private void replace(Fragment fragment, String s) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, fragment);
        transaction.addToBackStack(s);
        transaction.commit();
    }

    private void replace(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, fragment);
        transaction.commit();
    }

    private void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String subject = "একজন রক্তযোদ্ধা সবসময় একটি জীবন বাঁচাতে সর্বদায় সদয়...";
        String shareBody = "https://play.google.com/store/apps/details?id=com.sumonkmr.ibdc";
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(intent, "Share for Helps Others..."));
    }

    private void gotoUrl(String url) {
        Uri uri = Uri.parse(url);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    private void Sounds() {
        btn = MediaPlayer.create(getApplicationContext(), R.raw.mousemp3);
        okkBtn = MediaPlayer.create(getApplicationContext(), R.raw.positive_beeps);
        cbtN = MediaPlayer.create(getApplicationContext(), R.raw.stop);
        great_sound = MediaPlayer.create(getApplicationContext(), R.raw.decide);
    }

    //        Make Form Validation Here
    private boolean validateInput() {

        if (f_name_signUp.getText() == null || f_name_signUp.getText().toString().length() == 0) {
            f_name_signUp.setError("অনুগ্রহপূর্বক সঠিক নাম দিন!");
            return false;
        } else {
            f_name_signUp.setError(null);
        }


        if (l_name_signUp.getText() == null || l_name_signUp.getText().toString().length() == 0) {
            l_name_signUp.setError("অনুগ্রহপূর্বক সঠিক শেষের নাম দিন!");
            return false;
        } else {
            l_name_signUp.setError(null);
        }

        if (Objects.requireNonNull(number_signUp.getText()).length() != 11) {
            number_signUp.setError("অনুগ্রহপূর্বক সঠিক মোবাইল নম্বর দিন!");
            return false;
        } else {
            number_signUp.setError(null);
        }

        if (Division.getText() == null || Division.getText().toString().length() == 0) {
            Division.setError("অনুগ্রহপূর্বক সঠিক তথ্য দিন!");
            return false;
        } else {
            Division.setError(null);
        }


        if (District.getText() == null || District.getText().toString().length() == 0) {
            District.setError("অনুগ্রহপূর্বক সঠিক তথ্য দিন!");
            return false;
        } else {
            District.setError(null);
        }


        if (Upazila.getText() == null || Upazila.getText().toString().length() == 0) {
            Upazila.setError("অনুগ্রহপূর্বক সঠিক তথ্য দিন!");
            return false;
        } else {
            Upazila.setError(null);
        }


        if (village_signUp.getText() == null || village_signUp.getText().toString().length() == 0) {
            village_signUp.setError("অনুগ্রহপূর্বক সঠিক তথ্য দিন!");
            return false;
        } else {
            village_signUp.setError(null);
        }


        if (bloodGrpDropDown.getText() == null || bloodGrpDropDown.getText().toString().length() == 0) {
            bloodGrpDropDown.setError("অনুগ্রহপূর্বক সঠিক তথ্য দিন!");
            return false;
        } else {
            bloodGrpDropDown.setError(null);
        }

        if (lastDonateDate_signUp.getText() == null || lastDonateDate_signUp.getText().toString().length() == 0) {
            lastDonateDate_signUp.setError("অনুগ্রহপূর্বক সঠিক তথ্য দিন!");
            return false;
        } else {
            lastDonateDate_signUp.setError(null);
        }
        if (birthDate_signUp == null || !validateAge(birthDate_signUp.getYear())) {
            return false;
        }
        if (profileImg_uri == null) {
            Toast.makeText(this, "অনুগ্রহপূর্বক একটি ছবি সিলেক্ট করুন!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean validateAge(int year) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int isAgeValid = currentYear - year;
        if (isAgeValid < 18) {
            Toast.makeText(this, "রক্তদাতা হতে কমপক্ষে ১৮ বছর বয়স প্রয়োজনীয়।", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void ExitAppDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.exit_popup);
        Button ok_Btn, cBtn;

        account = GoogleSignIn.getLastSignedInAccount(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_background));
        }

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog
        dialog.show();


        ok_Btn = dialog.findViewById(R.id.exitOkBtn);
        cBtn = dialog.findViewById(R.id.exitCBtn);

        ok_Btn.setOnClickListener(v -> MainActivity.super.onBackPressed());
        cBtn.setOnClickListener(v -> dialog.dismiss());
    }

    @Override
    public void onBackPressed() {
        ExitAppDialog();
    }
}