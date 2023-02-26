package com.sumonkmr.ibdc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Profile extends AppCompatActivity {

    Context context;
    Dialog dialog;
    androidx.constraintlayout.widget.ConstraintLayout profile_tab, profile_edit_tab;
    Button edit_btn,save_btn,update_back;
    TextView f_name, l_name, mobile_number_pro, blood_grp_pro, village_pro, tehsil_pro, district_pro, state_pro, lastDonateDate_pro,email_pro,birthDate;
    com.google.android.material.textfield.TextInputEditText f_name_edit,l_name_edit,village_edit,number_edit;
    de.hdodenhof.circleimageview.CircleImageView profile_image,profile_image_edit,p_image_shade_edit;
    AutoCompleteTextView Division,District,Upazila,bloodGrpDropDown,lastDonateDate_edit;
    ImageView cover_image;
    Uri filepath;
    Bitmap bitmap;
    String userId,profile_url;
    Uri profile_uri;
    int d,m,y;
    protected StorageReference storageReference;
    protected DatabaseReference dbReference;
    protected FirebaseDatabase db;
    protected ProgressBar progressbar;
    GoogleSignInAccount account;
    int REQUEST_CODE = 11;
    LinearLayout bannerLay;
    String day, month, year, date;
    int currentYear,age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        context = getApplicationContext();
        bannerLay = findViewById(R.id.bannerLayout);
        AdsControl ads = new AdsControl(this); // for initialize Banner Ads
        ads.loadBannerAd(bannerLay);
        account = GoogleSignIn.getLastSignedInAccount(this);

        //        Methods
        Init();
        initializeAddressFilters();
        LastDonateDatePicker();
//        Database references
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();
        getValueOnDatabase();

//        View.OnClickListener

        edit_btn.setOnClickListener(v -> {
            profile_tab.setVisibility(View.GONE);
            profile_edit_tab.setVisibility(View.VISIBLE);
            save_btn.setVisibility(View.VISIBLE);
        });

        save_btn.setOnClickListener(v -> {
                if (NotEmpty()){
                    addToDatabase();
                    processImageUpload();
                }else {
                    Toast.makeText(context, "ত্রুটি, অনুগ্রহপূর্বক সকল তথ্য পুনরায় চেক করুন।", Toast.LENGTH_SHORT).show();
                }

        });

        update_back.setOnClickListener(v -> {
            profile_edit_tab.setVisibility(View.GONE);
            profile_tab.setVisibility(View.VISIBLE);
        });

        p_image_shade_edit.setOnClickListener(v -> {
            Toast.makeText(context, "working", Toast.LENGTH_SHORT).show();
            Dexter.withContext(context)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Browse For Image"), REQUEST_CODE);
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


    }//onCrate
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
                profile_image_edit.setImageURI(Uri.fromFile(finalFile));
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(finalFile));
                sendBroadcast(intent);
                profile_uri = intent.getData();

            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(this, "ছবি আপলোডে অসফল!", Toast.LENGTH_SHORT).show();
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void Init(){
        //        Hooks of TextViews
        f_name = findViewById(R.id.f_name);
        l_name = findViewById(R.id.l_name);
        mobile_number_pro = findViewById(R.id.mobile_number_pro);
        blood_grp_pro = findViewById(R.id.blood_grp_pro);
        village_pro = findViewById(R.id.village_pro);
        tehsil_pro = findViewById(R.id.tehsil_pro);
        district_pro = findViewById(R.id.district_pro);
        state_pro = findViewById(R.id.state_pro);
        email_pro = findViewById(R.id.email_pro);
        birthDate = findViewById(R.id.birthDate);
        lastDonateDate_pro = findViewById(R.id.lastDonateDate_pro);
        progressbar = findViewById(R.id.progressbar);

//        Hooks of edit buttons
        edit_btn = findViewById(R.id.edit_btn);
        save_btn = findViewById(R.id.save_btn);
        update_back = findViewById(R.id.update_back);
        profile_tab = findViewById(R.id.profile_tab);
        profile_edit_tab = findViewById(R.id.profile_edit_tab);
        profile_image = findViewById(R.id.profile_image);
        cover_image = findViewById(R.id.cover_image);
        profile_image_edit = findViewById(R.id.profile_image_edit);
        p_image_shade_edit = findViewById(R.id.p_image_shade_edit);
        f_name_edit = findViewById(R.id.f_name_edit);
        l_name_edit = findViewById(R.id.l_name_edit);
        number_edit = findViewById(R.id.number_edit);
        village_edit = findViewById(R.id.village_edit);
        Upazila = findViewById(R.id.upazilaDropDrown_edit);
        District = findViewById(R.id.districtDropDrown_edit);
        Division = findViewById(R.id.stateDropDrown_edit);
        bloodGrpDropDown = findViewById(R.id.bloodGrpDropDown_edit);
        lastDonateDate_edit = findViewById(R.id.lastDonateDate_edit);
        bloodGrpDropDown.setOnClickListener(v -> bloodGrpDropDown.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.blood_groups))));
    }

    private void initializeAddressFilters() {

//        bloodGrpDropDown.setOnClickListener(v -> {
//            ArrayAdapter<String> adapter=new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line,getResources().getStringArray(R.array.blood_groups));
//            bloodGrpDropDown.setAdapter(adapter);
//        });

        Division.setOnClickListener(v -> Division.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.states))));

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

    }

    private void LastDonateDatePicker(){
        final Calendar calendar = Calendar.getInstance();
        lastDonateDate_edit.setOnClickListener(v -> {
            y = calendar.get(Calendar.YEAR);
            m = calendar.get(Calendar.MONTH);
            d = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(Profile.this, (view, year, month, dayOfMonth) -> lastDonateDate_edit.setText(dayOfMonth+ "/"+(month+1)+"/"+year),y,m,d);
            datePickerDialog.show();
        });

    }

    private void processImageUpload(){
        User user = new User();
        if (profile_uri != null) {
            final StorageReference uploader = storageReference.child(String.format("profile_image/User Email : %s/profile_picture.%s",account.getEmail(), ".jpg"));
            uploader.putFile(profile_uri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(context, R.string.Updated_img, Toast.LENGTH_SHORT).show();
                        uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            @Override
                            public void onSuccess(Uri uri) {
                                if (!filepath.toString().isEmpty()) {
                                    profileImg(uri);
                                    progressbar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_success));
                                }
                            }
                        });
                    })
                    .addOnProgressListener(snapshot -> {
                        long per = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        progressbar.setProgress((int) per);
                        progressbar.setMax(100);
                        Toast.makeText(context, R.string.updating, Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show());
        }else {
            System.out.println("profile Uri is null but program can run.");
        }
    }

    private void profileImg(Uri uri){
        HashMap<String,Object> values = new HashMap<>();
        values.put("bloodImg_url",uri.toString());
        FirebaseDatabase.getInstance().getReference("Donors/"+ FirebaseAuth.getInstance().getUid())
                .updateChildren(values)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        profile_edit_tab.setVisibility(View.GONE);
                        profile_tab.setVisibility(View.VISIBLE);
                        Toast.makeText(context, R.string.Updated, Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addToDatabase() {
        HashMap<String,Object> values = new HashMap<>();
        values.put("FName", Objects.requireNonNull(f_name_edit.getText()).toString());
        values.put("LName", Objects.requireNonNull(l_name_edit.getText()).toString());
        values.put("Mobile", Objects.requireNonNull(number_edit.getText()).toString());
        values.put("State",Division.getText().toString());
        values.put("District",District.getText().toString());
        values.put("Upazila",Upazila.getText().toString());
        values.put("Village", Objects.requireNonNull(village_edit.getText()).toString());
        values.put("BloodGroup",bloodGrpDropDown.getText().toString());
        values.put("lastDonateDate",lastDonateDate_edit.getText().toString());
        FirebaseDatabase.getInstance().getReference("Donors/"+FirebaseAuth.getInstance().getUid())
                .updateChildren(values)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        profile_edit_tab.setVisibility(View.GONE);
                        profile_tab.setVisibility(View.VISIBLE);
                        Toast.makeText(context, R.string.Updated, Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show();
                    }
                });

    }//addToDatabase

    private void getValueOnDatabase(){
        db = FirebaseDatabase.getInstance();
        dbReference = db.getReference().child("Donors");
        storageReference = FirebaseStorage.getInstance().getReference();
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
                    birthDate.setText(Objects.requireNonNull(snapshot.child("birthdate").getValue()).toString());
                    lastDonateDate_pro.setText(Objects.requireNonNull(snapshot.child("lastDonateDate").getValue()).toString());
                    profile_url = (Objects.requireNonNull(snapshot.child("bloodImg_url").getValue()).toString());

//                    Edit tabs
                    f_name_edit.setText(Objects.requireNonNull(snapshot.child("FName").getValue()).toString());
                    l_name_edit.setText(Objects.requireNonNull(snapshot.child("LName").getValue()).toString());
                    number_edit.setText(Objects.requireNonNull(snapshot.child("Mobile").getValue()).toString());
                    village_edit.setText(Objects.requireNonNull(snapshot.child("Village").getValue()).toString());
                    Upazila.setText(Objects.requireNonNull(snapshot.child("Upazila").getValue()).toString());
                    District.setText(Objects.requireNonNull(snapshot.child("District").getValue()).toString());
                    Division.setText(Objects.requireNonNull(snapshot.child("State").getValue()).toString());
                    bloodGrpDropDown.setText(Objects.requireNonNull(snapshot.child("BloodGroup").getValue()).toString());
                    lastDonateDate_edit.setText(Objects.requireNonNull(snapshot.child("lastDonateDate").getValue()).toString());

                    Glide.with(context)
                            .load(profile_url)
                            .centerCrop()
                            .placeholder(R.drawable.ibdc_logo)
                            .into(profile_image);

                    Glide.with(context)
                            .load(profile_url)
                            .centerCrop()
                            .placeholder(R.drawable.ibdc_logo)
                            .into((ImageView) cover_image);

                    Glide.with(context)
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
    }

    private boolean NotEmpty(){
        if (f_name_edit.getText() == null || f_name_edit.getText().toString().length() == 0) {
            f_name_edit.setError("অনুগ্রহপূর্বক সঠিক নাম দিন!");
            return false;
        } else {
            f_name_edit.setError(null);
        }


        if (l_name_edit.getText() == null || l_name_edit.getText().toString().length() == 0) {
            l_name_edit.setError("অনুগ্রহপূর্বক সঠিক শেষের নাম দিন!");
            return false;
        } else {
            l_name_edit.setError(null);
        }

        if (Objects.requireNonNull(number_edit.getText()).length() != 11) {
            number_edit.setError("অনুগ্রহপূর্বক সঠিক মোবাইল নম্বর দিন!");
            return false;
        } else {
            number_edit.setError(null);
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


        if (village_edit.getText() == null || village_edit.getText().toString().length() == 0) {
            village_edit.setError("অনুগ্রহপূর্বক সঠিক তথ্য দিন!");
            return false;
        } else {
            village_edit.setError(null);
        }


        if (bloodGrpDropDown.getText() == null || bloodGrpDropDown.getText().toString().length() == 0) {
            bloodGrpDropDown.setError("অনুগ্রহপূর্বক সঠিক তথ্য দিন!");
            return false;
        } else {
            bloodGrpDropDown.setError(null);
        }

        if (lastDonateDate_edit.getText() == null || lastDonateDate_edit.getText().toString().length() == 0) {
            lastDonateDate_edit.setError("অনুগ্রহপূর্বক সঠিক তথ্য দিন!");
            return false;
        } else {
            lastDonateDate_edit.setError(null);
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        AdsControl adsControl = new AdsControl(this);
        Random random;
        random = new Random();
        int myCount = random.nextInt(100 - 5) + 5;
        if (adsControl.passCondition() && myCount % 2 == 0) {
            AdsControl.mInterstitialAd.show(this);
            super.onBackPressed();
        } else if (!adsControl.passCondition() && myCount % 2 == 0) {
            adsControl.StartIoInnit(AdsControl.isValStartIo);
            super.onBackPressed();
            Log.d("llaa", "onBackPressed: " + adsControl.passCondition() + " and " + myCount);
        } else {
            super.onBackPressed();
        }
    }//onBackPressed
}//Root