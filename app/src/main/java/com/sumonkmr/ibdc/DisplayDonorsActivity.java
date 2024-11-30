package com.sumonkmr.ibdc;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sumonkmr.ibdc.adapters.UserAdapter;
import com.sumonkmr.ibdc.model.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class DisplayDonorsActivity extends AppCompatActivity {


    RecyclerView list;
    UserAdapter adapter;
    ArrayList<User> users, temp;
    AutoCompleteTextView DivisionFilter, districtFilter, UpazilaFilter, bloodGrpFilter,lastDonateSearch;
    User self;
    String uid;
    GoogleSignInAccount account;
    FirebaseAuth auth;
    AdView adView;
    FloatingActionButton search_donor;
    TextView marquee_text, edit_total;
    Dialog dialog;
    SwipeRefreshLayout donorReload;
    SearchView search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donor_list_activity);

        SoundManager soundManager = new SoundManager(this);
        donorReload = findViewById(R.id.donorReload);
        search_donor = findViewById(R.id.search_donor);
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        assert currentUser != null;
        uid = currentUser.getUid();
        LinearLayout bannerLay = findViewById(R.id.bannerLayout);
        AdsControl ads = new AdsControl(this); // for initialize Banner Ads
        ads.loadBannerAd(bannerLay);
        initializeComponents();
        getDonors();
        MorQueTxr();
        YoYo.with(Techniques.Tada).delay(300).duration(1000).playOn(search_donor);
        YoYo.with(Techniques.FadeInRight).duration(1000).playOn(edit_total);
        YoYo.with(Techniques.FadeInLeft).duration(1000).playOn(search);
        search_donor.setOnClickListener(v -> {
            SearchDialog();
        });
        donorReload.setOnRefreshListener(() -> {
            soundManager.reFresh.start();
            initializeComponents();
            getDonors();
            adapter.notifyDataSetChanged();
            search.onActionViewCollapsed();
            MorQueTxr();
            YoYo.with(Techniques.Tada).delay(500).duration(1000).playOn(search_donor);
            YoYo.with(Techniques.FadeInRight).duration(1000).playOn(edit_total);
            YoYo.with(Techniques.FadeInLeft).duration(1000).playOn(search);
            donorReload.setRefreshing(false);
        });


    }//onCreate

    private void MorQueTxr() {
        marquee_text.setSelected(true);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("texts").child("marqueTxt");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
//                String value = snapshot.getValue("marqueTxt");
                marquee_text.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SearchDialog() {
        SoundManager soundManager = new SoundManager(this);
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.donor_search);
        LinearLayout lay1,lay2,lay3;
        Button ok_Btn, cBtn;

        account = GoogleSignIn.getLastSignedInAccount(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_background));
        }

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog
        dialog.show();

        lay1 = dialog.findViewById(R.id.lay1);
        lay2 = dialog.findViewById(R.id.lay2);
        lay3 = dialog.findViewById(R.id.lay3);
        ok_Btn = dialog.findViewById(R.id.donorOkBtn);
        cBtn = dialog.findViewById(R.id.donorCBtn);
        YoYo.with(Techniques.FadeInDown).duration(1000).playOn(lay1);
        YoYo.with(Techniques.FadeInLeft).duration(1000).playOn(lay2);
        YoYo.with(Techniques.FadeInRight).duration(1000).playOn(lay3);
        YoYo.with(Techniques.SlideInLeft).duration(1000).playOn(ok_Btn);
        YoYo.with(Techniques.SlideInRight).duration(1000).playOn(cBtn);
        DialogInitializeComponents();
        initializeAddressFilters();
        getDonorsDialog();

        ok_Btn.setOnClickListener(v -> {
            if (!UpazilaFilter.getText().toString().isEmpty() || !bloodGrpFilter.getText().toString().isEmpty() || !lastDonateSearch.getText().toString().isEmpty()) {
                soundManager.great_sound.start();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "ঠিকানা অথবা রক্তের গ্রুপ অথবা শেষ রক্তদানের তারিখ সিলেক্ট করুন!", Toast.LENGTH_SHORT).show();
                soundManager.cbtN.start();
            }
        });
        cBtn.setOnClickListener(v -> dialog.dismiss());
    }//search Dialog


    private void initializeAddressFilters() {

        DivisionFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.states)));
        bloodGrpFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.blood_groups)));
        DivisionFilter.setOnItemClickListener((parent, view, position, id) -> {

            switch (DivisionFilter.getText().toString()) {
                case "ঢাকা":
                    districtFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.dhaka_division)));
                    break;

                case "রাজশাহী":
                    districtFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.rajshahi_division)));
                    break;

                case "চট্টগ্রাম":
                    districtFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.chittagong_division)));
                    break;

                case "খুলনা":
                    districtFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.khulna_division)));
                    break;

                case "বরিশাল":
                    districtFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.barisal_division)));
                    break;

                case "রংপুর":
                    districtFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.rangpur_division)));
                    break;

                case "সিলেট":
                    districtFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.sylet_division)));
                    break;

                case "ময়মনসিংহ":
                    districtFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.mymanshingh_division)));
                    break;

                default:
                    Toast.makeText(this, "যেকোনো একটি বিভাগ সিলেক্ট করুন!", Toast.LENGTH_SHORT).show();


            }
        });
        districtFilter.setOnItemClickListener((parent, view, position, id) -> {
            switch (districtFilter.getText().toString()) {

//                Khulna Division
                case "খুলনা":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.khulna)));
                    break;

                case "কুষ্টিয়া":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.kushtia)));
                    break;

                case "চুয়াডাঙ্গা":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.chuadanga)));
                    break;

                case "ঝিনাইদহ":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.jhinaidah)));
                    break;

                case "নড়াইল":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.narail)));
                    break;

                case "বাগেরহাট":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.bagerhat)));
                    break;

                case "মাগুরা":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.magura)));
                    break;

                case "মেহেরপুর":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.meherpur)));
                    break;

                case "যশোর":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.jossore)));
                    break;

                case "সাতক্ষীরা":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.satkhira)));
                    break;
//                    end khulna Division

//                Barishal Division
                case "বরিশাল":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.barishal)));
                    break;

                case "বরগুনা":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.borguna)));
                    break;

                case "ভোলা":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.bhola)));
                    break;

                case "ঝালকাঠি":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.jhalkhathi)));
                    break;

                case "পটুয়াখালী":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.potuyakhali)));
                    break;

                case "পিরোজপুর":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.pirojpur)));
                    break;
//                    end Barishal

//                Chittagong Division
                case "চট্টগ্রাম":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.chittagong)));
                    break;

                case "বান্দরবান":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.bandarban)));
                    break;

                case "ব্রাহ্মণবাড়িয়া":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.brammanbaria)));
                    break;

                case "চাঁদপুর":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.chadpur)));
                    break;

                case "কুমিল্লা":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.cumilla)));
                    break;

                case "কক্সবাজার":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.coxsbazar)));
                    break;

                case "ফেনী":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.feni)));
                    break;

                case "খাগড়াছড়ি":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.khagrachory)));
                    break;

                case "লক্ষ্মীপুর":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.laxmipur)));
                    break;

                case "নোয়াখালী":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.noyakhali)));
                    break;

                case "রাঙ্গামাটি":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.rangamati)));
                    break;
//               end Chittagong Division
                case "ঢাকা":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.dhaka)));
                    break;

                case "ফরিদপুর":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.faridpur)));
                    break;

                case "গাজীপুর":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.gazipur)));
                    break;

                case "গোপালগঞ্জ":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.gopalgonj)));
                    break;

                case "কিশোরগঞ্জ":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.kishoregonj)));
                    break;

                case "মাদারীপুর":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.madaripur)));
                    break;

                case "মানিকগঞ্জ":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.manikgonj)));
                    break;

                case "মুন্সীগঞ্জ":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.munsigonj)));
                    break;

                case "নারায়ণগঞ্জ":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.narayangonj)));
                    break;

                case "নরসিংদী":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.narshindhi)));
                    break;

                case "রাজবাড়ী":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.rajbari)));
                    break;

                case "শরীয়তপুর":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.shoriyotpur)));
                    break;

                case "টাঙ্গাইল":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.tangail)));
                    break;
//                End  Dhaka Division


//                 mymanshigh Division
                case "ময়মনসিংহ":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.mymanshingh)));
                    break;

                case "জামালপুর":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.jamalpur)));
                    break;

                case "নেত্রকোণা":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.netrokona)));
                    break;

                case "শেরপুর":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.sherpur)));
                    break;
//              End mymanshigh Division

//                Rajshahi Division
                case "রাজশাহী":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.rajshahi)));
                    break;

                case "পাবনা":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.pabna)));
                    break;

                case "নাটোর":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.natore)));
                    break;

                case "বগুড়া":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.bogura)));
                    break;

                case "নওগাঁ":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.nogone)));
                    break;

                case "জয়পুরহাট":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.jaypurhat)));
                    break;

                case "চাঁপাইনবাবগঞ্জ":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.Chapainababgonj)));
                    break;

                case "সিরাজগঞ্জ":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.sirajgonj)));
                    break;
//               End Rajshahi Division

//                Rangpur Division
                case "রংপুর":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.rangpur)));
                    break;

                case "দিনাজপুর":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.dinajpur)));
                    break;

                case "গাইবান্ধা":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.gaibandha)));
                    break;

                case "কুড়িগ্রাম":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.kurigram)));
                    break;

                case "লালমনিরহাট":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.lalmonirhat)));
                    break;

                case "নীলফামারী":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.niphamari)));
                    break;

                case "পঞ্চগড়":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.ponchogarh)));
                    break;

                case "ঠাকুরগাঁও":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.thakurgao)));
                    break;
//             End Rangpur Division

//                Sylet Division
                case "সিলেট":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.sylet)));
                    break;

                case "হবিগঞ্জ":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.hobigonj)));
                    break;

                case "মৌলভীবাজার":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.molovibazar)));
                    break;

                case "সুনামগঞ্জ":
                    UpazilaFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.sunamgonj)));
                    break;
//               End Sylet Division

                default:
                    Toast.makeText(this, "যেকোনো একটি জেলা সিলেক্ট করুন!", Toast.LENGTH_SHORT).show();


            }
        });

    }

    void DialogInitializeComponents() {
        DivisionFilter = dialog.findViewById(R.id.stateFilter);
        districtFilter = dialog.findViewById(R.id.districtFilter);
        UpazilaFilter = dialog.findViewById(R.id.upazilaFilter);
        bloodGrpFilter = dialog.findViewById(R.id.bloodGrpFilter);
        lastDonateSearch = dialog.findViewById(R.id.lastDonateSearch);
        YoYo.with(Techniques.SlideInLeft).duration(700).playOn(DivisionFilter);
        YoYo.with(Techniques.SlideInRight).duration(700).playOn(districtFilter);
        YoYo.with(Techniques.SlideInLeft).duration(700).playOn(UpazilaFilter);
        YoYo.with(Techniques.SlideInRight).duration(700).playOn(bloodGrpFilter);
        YoYo.with(Techniques.SlideInDown).duration(700).playOn(lastDonateSearch);
        self = new User();
        list = findViewById(R.id.donorsList);
        users = new ArrayList<>();
        temp = new ArrayList<>();
        adapter = new UserAdapter(this, users, position -> {
            //Handle call button event
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + temp.get(position).getMobile()));
            startActivity(intent);
        }, position -> {
            //Handle share button event
            User sent = temp.get(position);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TITLE, R.string.motive);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Hello.\n" + "Here is the Information about blood Donor:\n" + sent.getFName() + " " + sent.getLName() + "\nBlood Group : " + sent.getBloodGroup() + "\nAddress : " + sent.getVillage() + " ," + sent.getUpazila() + " ," + sent.getDistrict() + " ," + sent.getState() + "\nMobile Number : " + sent.getMobile());
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, "Be Hero, Donate Blood.");
            startActivity(shareIntent);

        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(linearLayoutManager);
        list.setAdapter(adapter);
        lastDonateSearch.setOnClickListener(v -> GetLastDonateDate());
        UpazilaFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateList(s.toString());
            }
        });
        bloodGrpFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateBloodGroupList(s.toString());
            }
        });
        lastDonateSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateLastDonateList(s.toString());
            }
        });
    }

    private void GetLastDonateDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth1) -> {
                    // Do something with the selected date
                    int actualMonth = month1+1;
                    String date = dayOfMonth1 + "/" + actualMonth + "/" + year1;
                    lastDonateSearch.setText(date);
                }, year, month, dayOfMonth);

        datePickerDialog.show();
    }

    void initializeComponents() {
        marquee_text = findViewById(R.id.marquee_text);
        edit_total = findViewById(R.id.edit_total);
        self = new User();
        list = findViewById(R.id.donorsList);
        users = new ArrayList<>();
        temp = new ArrayList<>();
        adapter = new UserAdapter(this, users, position -> {
            //Handle call button event
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + temp.get(position).getMobile()));
            startActivity(intent);
        }, position -> {
            //Handle share button event
            User sent = temp.get(position);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TITLE, R.string.app_name);
            sendIntent.putExtra(Intent.EXTRA_TEXT,  "আমি বাঁচাতে চাই একটি প্রাণ, তাইতো করবো রক্তদান!\n" + "এখানে রক্তদাতা সম্পর্কে তথ্য রয়েছে :\nনাম : " + sent.getFName()+ " " + "\nরক্তের গ্রুপ : " + sent.getBloodGroup() + "\nঠিকানা: " + sent.getVillage() + " ," + sent.getUpazila() + " ," + sent.getDistrict() + " ," + sent.getState() + "\nমোবাইল নম্বর : " + sent.getMobile());
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, "রক্ত দিন জীবন বাঁচান।");
            startActivity(shareIntent);

        });
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
        GetUpdateUserCount();
        //Searching
        SearchInt();

    }

    private Handler handler = new Handler();
    private Runnable filterRunnable;

    private void SearchInt() {
        search = findViewById(R.id.search);
        search.setOnSearchClickListener(v -> {
            YoYo.with(Techniques.SlideInRight).duration(300).playOn(search);
        });
        search.setOnCloseListener(() -> {
            YoYo.with(Techniques.SlideInLeft).duration(500).playOn(search);
            return false;
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                // Cancel any previous filtering task
                handler.removeCallbacks(filterRunnable);

                // Set a new task with a delay
                filterRunnable = new Runnable() {
                    @Override
                    public void run() {
                        updateList(newText);
                    }
                };

                // Apply delay before filtering (300ms)
                handler.postDelayed(filterRunnable, 300);
                return true;
            }
        });
    }





    private void updateList(String s) {
        System.out.println(s);
        temp.clear();
        for (User v : users) {
            if (v.getVillage().toUpperCase().contains(s.toLowerCase(Locale.ROOT).toUpperCase(Locale.ROOT))||v.getUpazila().toUpperCase().contains(s) || v.getDistrict().toUpperCase().contains(s) || v.getState().toUpperCase().contains(s) || v.getLastDonateDate().toUpperCase().contains(s)||v.getBloodGroup().toUpperCase().contains(s.toLowerCase(Locale.ROOT).toUpperCase(Locale.ROOT))||s.equalsIgnoreCase("ALL")) {
                System.out.println(v.getUpazila());
                temp.add(v);
            }
        }
        adapter.updateList(temp);
        GetUpdateUserCount();
    }

    private void updateBloodGroupList(String s) {
        System.out.println(s);
        temp.clear();
        for (User v : users) {
            if (v.getBloodGroup().toUpperCase().contains(s) || s.equalsIgnoreCase("ALL")) {
                System.out.println(v.getBloodGroup());
                temp.add(v);
            }
        }
        adapter.updateList(temp);
        GetUpdateUserCount();
    }

    private void updateLastDonateList(String s) {
        System.out.println(s);
        temp.clear();
        for (User v : users) {
            if (v.getLastDonateDate().toUpperCase().contains(s) || s.equalsIgnoreCase("ALL")) {
                System.out.println(v.getLastDonateDate());
                temp.add(v);
            }
        }
        adapter.updateList(temp);
        GetUpdateUserCount();
    }


    private void GetUpdateUserCount(){
        adapter.updateList(temp);
        long totalUser = Integer.parseInt(String.valueOf(temp.size()));
        if (temp.isEmpty()){
            edit_total.setText(String.format("মোটঃ %d জন রক্তদাতা", totalUser));
        }else {
            temp.size();
            edit_total.setText(String.format("মোটঃ %d জন রক্তদাতা", totalUser));
        }
    }


    private void getDonors() {
        FirebaseDatabase.getInstance().getReference("Donors").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                temp.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    assert user != null;
                    self = user;
                    users.add(user);
                    temp.add(user);
                    edit_total.setText(String.format("মোটঃ %d জন রক্তদাতা", snapshot.getChildrenCount()));
                }
                filterList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getDonorsDialog() {
        FirebaseDatabase.getInstance().getReference("Donors").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                temp.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    assert user != null;
                    self = user;
                    users.add(user);
                    temp.add(user);
                }
                updateList(UpazilaFilter.getText().toString());
                updateBloodGroupList(bloodGrpFilter.getText().toString());
                updateLastDonateList(lastDonateSearch.getText().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void filterList() {
        adapter.notifyDataSetChanged();
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


}