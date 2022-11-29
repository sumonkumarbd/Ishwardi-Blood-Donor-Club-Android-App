package com.sumonkmr.ibdc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sumonkmr.ibdc.adapters.UserAdapter;
import com.sumonkmr.ibdc.model.User;

import java.util.ArrayList;
import java.util.HashMap;

public class DisplayDonorsActivity extends AppCompatActivity {


    RecyclerView list;
    UserAdapter adapter;
    ArrayList<User> users, temp;
    AutoCompleteTextView DivisionFilter, districtFilter, UpazilaFilter, bloodGrpFilter;
    User self;
    String uid;
    GoogleSignInAccount account;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donor_list_activity);

//        account = GoogleSignIn.getLastSignedInAccount(this);
//        assert account != null;
//        uid = account.getId();
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        assert currentUser != null;
        uid = currentUser.getUid();

        initializeComponents();
        initializeAddressFilters();
        getDonors();
    }

    private void initializeAddressFilters() {

        DivisionFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.states)));
//        bloodGrpFilter.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.blood_groups)));
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

    void initializeComponents() {

        DivisionFilter = findViewById(R.id.stateFilter);
        districtFilter = findViewById(R.id.districtFilter);
        UpazilaFilter = findViewById(R.id.upazilaFilter);
//        bloodGrpFilter = findViewById(R.id.bloodGrpFilter); // if get blood filter in search then comment out this line and also xml

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
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Hello.\n" + "Here is the Information about blood Donor:\n" + sent.getFName() + " " + sent.getLName() + "\nBlood Group : " + sent.getBloodGroup() + "\nAddress : " + sent.getVillage() + " ," + sent.getUpazila() + " ," + sent.getDistrict() + " ," + sent.getDivision() + "\nMobile Number : " + sent.getMobile());
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, "Be Hero, Donate Blood.");
            startActivity(shareIntent);

        });
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
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


//        bloodGrpFilter.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                updateBloodGroupList(s.toString());
//            }
//        });  // if get blood filter in search then comment out this line and also xml
    }

    private void updateVisible(boolean b) {
        HashMap<String, Object> updateValues = new HashMap<>();

        if (b) {
            updateValues.put("Visible", "True");
        } else {
            updateValues.put("Visible", "False");
        }
        FirebaseDatabase.getInstance().getReference("Donors").child(uid).updateChildren(updateValues);
    }

    private void updateList(String s) {
        System.out.println(s);
        temp.clear();
        for (User v : users) {
            if (v.getUpazila().toUpperCase().contains(s) || s.equalsIgnoreCase("ALL")) {
                System.out.println(v.getUpazila());
                temp.add(v);
            }
        }
        adapter.updateList(temp);
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
                    if (user.getUid().equals(uid)) {
                        self = user;
                        users.add(user);
                        temp.add(user);
                    }
                }
                updateList(UpazilaFilter.getText().toString());
//                updateList(bloodGrpFilter.getText().toString());
                filterList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void filterList() {
        adapter.notifyDataSetChanged();
    }


}