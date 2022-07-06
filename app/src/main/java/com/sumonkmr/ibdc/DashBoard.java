package com.sumonkmr.ibdc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class DashBoard extends AppCompatActivity implements View.OnClickListener {

    DuoDrawerLayout drawerLayout;
    de.hdodenhof.circleimageview.CircleImageView profile_image_menu;
    TextView profile_name_menu;
    String uid;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        auth = FirebaseAuth.getInstance();





        init();
    }

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
        LinearLayout ll_Share = menuView.findViewById(R.id.ll_Share);
        LinearLayout ll_Logout = menuView.findViewById(R.id.ll_Logout);
        profile_name_menu = menuView.findViewById(R.id.profile_name_menu);
        profile_image_menu = menuView.findViewById(R.id.profile_image_menu);


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        uid = currentUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("Donors");
        reference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                   String  profile_img_url = (Objects.requireNonNull(snapshot.child("bloodImg_url").getValue()).toString());
                    String f_name = (Objects.requireNonNull(snapshot.child("FName").getValue()).toString());
                    String l_name = (Objects.requireNonNull(snapshot.child("LName").getValue()).toString());
                   profile_name_menu.setText(String.format("%s %s",f_name,l_name));
                    Glide
                            .with(DashBoard.this)
                            .load(profile_img_url)
                            .centerCrop()
                            .placeholder(R.drawable.ibdc_logo)
                            .into(profile_image_menu);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        ll_Home.setOnClickListener(this);
        ll_Profile.setOnClickListener(this);
        ll_Share.setOnClickListener(this);
        ll_Logout.setOnClickListener(this);



        replace(new HomeFragment());


    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_Home:
                replace(new HomeFragment(),"Home");
                break;

            case R.id.ll_Profile:
                replace(new ProfileFragment(),"Profile");
                break;

            case R.id.ll_Share:
                Toast.makeText(this, "Share...", Toast.LENGTH_SHORT).show();
                break;

            case R.id.ll_Logout:
                auth.signOut();
                startActivity(new Intent(this,SplashScreen.class));
                break;

//            case R.id.about_us:
////                startActivity(new Intent(this, AboutUs.class));
//                Toast.makeText(this, "This is About Us", Toast.LENGTH_SHORT).show();
//                break;
//
//            case R.id.find_donors:
////                startActivity(new Intent(this, DisplayDonorsActivity.class));
//                Toast.makeText(this, "This is Donors", Toast.LENGTH_SHORT).show();
//                break;

        }
        drawerLayout.closeDrawer();
    }

    private void replace(Fragment fragment, String s) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame,fragment);
        transaction.addToBackStack(s);
        transaction.commit();
    }

    private void replace(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame,fragment);
        transaction.commit();
    }


}