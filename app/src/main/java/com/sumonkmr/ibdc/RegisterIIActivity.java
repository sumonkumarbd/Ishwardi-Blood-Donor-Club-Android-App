package com.sumonkmr.ibdc;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class RegisterIIActivity extends AppCompatActivity {


    AutoCompleteTextView Division, District, Upazila;

    com.google.android.material.textfield.TextInputEditText Village;
    Button nextToII;
    com.airbnb.lottie.LottieAnimationView loadingAim2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_i_i);

        initializeComponents();
        initializeAddressFilters();








    }

    private void initializeAddressFilters() {

        Division.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.states)));
        Division.setOnItemClickListener((parent, view, position, id) -> {

            switch (Division.getText().toString()){
                case "ঢাকা":
                    District.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.dhaka_division)));
                    break;

                case "রাজশাহী":
                    District.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.rajshahi_division)));
                    break;

                case "চট্টগ্রাম":
                    District.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.chittagong_division)));
                    break;

                case "খুলনা":
                    District.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.khulna_division)));
                    break;

                case "বরিশাল":
                    District.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.barisal_division)));
                    break;

                case "রংপুর":
                    District.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.rangpur_division)));
                    break;

                case "সিলেট":
                    District.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.sylet_division)));
                    break;

                case "ময়মনসিংহ":
                    District.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.mymanshingh_division)));
                    break;

                default:
                    Toast.makeText(this, "যেকোনো একটি বিভাগ সিলেক্ট করুন!", Toast.LENGTH_SHORT).show();


            }
        });


        District.setOnItemClickListener((parent, view, position, id) -> {
            switch (District.getText().toString()){

//                Khulna Division
                case "খুলনা":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.khulna)));
                    break;

                case "কুষ্টিয়া":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.kushtia)));
                    break;

                case "চুয়াডাঙ্গা":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.chuadanga)));
                    break;

                case "ঝিনাইদহ":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.jhinaidah)));
                    break;

                case "নড়াইল":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.narail)));
                    break;

                case "বাগেরহাট":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.bagerhat)));
                    break;

                case "মাগুরা":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.magura)));
                    break;

                case "মেহেরপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.meherpur)));
                    break;

                case "যশোর":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.jossore)));
                    break;

                case "সাতক্ষীরা":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.satkhira)));
                    break;
//                    end khulna Division

//                Barishal Division
                case "বরিশাল":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.barishal)));
                    break;

                case "বরগুনা":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.borguna)));
                    break;

                case "ভোলা":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.bhola)));
                    break;

                case "ঝালকাঠি":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.jhalkhathi)));
                    break;

                case "পটুয়াখালী":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.potuyakhali)));
                    break;

                case "পিরোজপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.pirojpur)));
                    break;
//                    end Barishal

//                Chittagong Division
                case "চট্টগ্রাম":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.chittagong)));
                    break;

                case "বান্দরবান":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.bandarban)));
                    break;

                case "ব্রাহ্মণবাড়িয়া":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.brammanbaria)));
                    break;

                case "চাঁদপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.chadpur)));
                    break;

                case "কুমিল্লা":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.cumilla)));
                    break;

                case "কক্সবাজার":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.coxsbazar)));
                    break;

                case "ফেনী":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.feni)));
                    break;

                case "খাগড়াছড়ি":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.khagrachory)));
                    break;

                case "লক্ষ্মীপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.laxmipur)));
                    break;

                case "নোয়াখালী":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.noyakhali)));
                    break;

                case "রাঙ্গামাটি":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.rangamati)));
                    break;
//               end Chittagong Division
                case "ঢাকা":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.dhaka)));
                    break;

                case "ফরিদপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.faridpur)));
                    break;

                case "গাজীপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.gazipur)));
                    break;

                case "গোপালগঞ্জ":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.gopalgonj)));
                    break;

                case "কিশোরগঞ্জ":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.kishoregonj)));
                    break;

                case "মাদারীপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.madaripur)));
                    break;

                case "মানিকগঞ্জ":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.manikgonj)));
                    break;

                case "মুন্সীগঞ্জ":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.munsigonj)));
                    break;

                case "নারায়ণগঞ্জ":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.narayangonj)));
                    break;

                case "নরসিংদী":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.narshindhi)));
                    break;

                case "রাজবাড়ী":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.rajbari)));
                    break;

                case "শরীয়তপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.shoriyotpur)));
                    break;

                case "টাঙ্গাইল":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.tangail)));
                    break;
//                End  Dhaka Division


//                 mymanshigh Division
                case "ময়মনসিংহ":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.mymanshingh)));
                    break;

                case "জামালপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.jamalpur)));
                    break;

                case "নেত্রকোণা":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.netrokona)));
                    break;

                case "শেরপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.sherpur)));
                    break;
//              End mymanshigh Division

//                Rajshahi Division
                case "রাজশাহী":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.rajshahi)));
                    break;

                case "পাবনা":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.pabna)));
                    break;

                case "নাটোর":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.natore)));
                    break;

                case "বগুড়া":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.bogura)));
                    break;

                case "নওগাঁ":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.nogone)));
                    break;

                case "জয়পুরহাট":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.jaypurhat)));
                    break;

                case "চাঁপাইনবাবগঞ্জ":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.Chapainababgonj)));
                    break;

                case "সিরাজগঞ্জ":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.sirajgonj)));
                    break;
//               End Rajshahi Division

//                Rangpur Division
                case "রংপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.rangpur)));
                    break;

                case "দিনাজপুর":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.dinajpur)));
                    break;

                case "গাইবান্ধা":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.gaibandha)));
                    break;

                case "কুড়িগ্রাম":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.kurigram)));
                    break;

                case "লালমনিরহাট":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.lalmonirhat)));
                    break;

                case "নীলফামারী":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.niphamari)));
                    break;

                case "পঞ্চগড়":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.ponchogarh)));
                    break;

                case "ঠাকুরগাঁও":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.thakurgao)));
                    break;
//             End Rangpur Division

//                Sylet Division
                case "সিলেট":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.sylet)));
                    break;

                case "হবিগঞ্জ":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.hobigonj)));
                    break;

                case "মৌলভীবাজার":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.molovibazar)));
                    break;

                case "সুনামগঞ্জ":
                    Upazila.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.sunamgonj)));
                    break;
//               End Sylet Division

                default:
                    Toast.makeText(this, "যেকোনো একটি জেলা সিলেক্ট করুন!", Toast.LENGTH_SHORT).show();


            }
        });

    }

    private void initializeComponents() {
        Division = findViewById(R.id.stateDropDrown);
        District = findViewById(R.id.districtDropDrown);
        Upazila = findViewById(R.id.upazilaDropDrown);
        Village = findViewById(R.id.VillageRegister);
        nextToII = findViewById(R.id.nextButtonII);
        loadingAim2 = findViewById(R.id.loadingAim2);
    }

    public void registerIII(View view) {
        loadingAim2.setVisibility(View.VISIBLE);
        String districtT, upazilaT, villageT, divisionT;
        districtT = District.getText().toString();
        upazilaT = Upazila.getText().toString();
        villageT = Objects.requireNonNull(Village.getText()).toString();
        divisionT = Division.getText().toString();

        if (!divisionT.isEmpty() && !districtT.isEmpty() && !upazilaT.isEmpty() && !villageT.isEmpty()) {
            loadingAim2.setVisibility(View.VISIBLE);
            addDataToFirebaseStorage(divisionT, districtT, upazilaT, villageT, FirebaseAuth.getInstance().getUid());
            startActivity(new Intent(RegisterIIActivity.this, RegisterIIIActivity.class));
        }else {
            loadingAim2.setVisibility(View.GONE);
            Toast.makeText(this, "অনুগ্রহপূর্বক সঠিক তথ্য দিন!", Toast.LENGTH_SHORT).show();
        }
    }

    private void addDataToFirebaseStorage(String divisionT, String districtT, String tehsilT, String villageT, String uid) {

        HashMap<String,Object> values = new HashMap<>();
        values.put("State", divisionT);
        values.put("District",districtT);
        values.put("Upazila",tehsilT);
        values.put("Village",villageT);
        values.put("Step","2");

        FirebaseDatabase.getInstance().getReference("Donors")
                .child(uid).updateChildren(values);
    }

    }

