package com.sumonkmr.ibdc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
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
import com.squareup.picasso.Picasso;

import java.util.Objects;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class DashBoard extends AppCompatActivity implements View.OnClickListener {

    DuoDrawerLayout drawerLayout;
    de.hdodenhof.circleimageview.CircleImageView profile_image_menu,sds_image;
    TextView profile_name_menu,sds_dash,mail;
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

                         Glide.with(getApplicationContext())
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
        switch (view.getId()){
            case R.id.ll_Home:
                replace(new HomeFragment(),"Home");
                break;

            case R.id.ll_Profile:
                replace(new ProfileFragment(),"Profile");
                break;

                case R.id.ll_Setting:
                replace(new SettingFragments(),"Setting");
                break;

            case R.id.ll_fb:
                gotoUrl("https://www.facebook.com/groups/ibdcbd");
                break;

                case R.id.ll_Share:
                shareApp();
                break;

            case R.id.ll_about_us:
                startActivity(new Intent(this, AboutUs.class));
                break;

            case R.id.ll_privacy:
                gotoUrl("https://ibdcprivacypolicy.blogspot.com/");
                break;

                case R.id.ll_Logout:
                auth.signOut();
                startActivity(new Intent(this,SplashScreen.class));
                break;

            case R.id.mail:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL,new String[]{"sumonkmrofficial@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT,"Im Sending Email From IBDC App");
                intent.putExtra(Intent.EXTRA_TEXT,"Please Type Your Massage Here : \n");
                intent.setType("massage/rfc822");
                startActivity(intent);
                break;

            case R.id.sds_dash:
                gotoUrl("https://www.facebook.com/sumonkmr.studio/");
                break;

            case R.id.sds_image:
                gotoUrl("https://play.google.com/store/apps/dev?id=6877143126125387449");
                break;


//            case R.id.find_donors:
////                startActivity(new Intent(this, DisplayDonorsActivity.class));
//                Toast.makeText(this, "This is Donors", Toast.LENGTH_SHORT).show();
//                break;

        }// switch finished



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

    private void shareApp(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String subject = "একজন রক্তযোদ্ধা সবসময় একটি জীবন বাঁচাতে সর্বদায় সদয়...";
        String shareBody = "https://play.google.com/store/apps/details?id=com.sumonkmr.ibdc";
        intent.putExtra(Intent.EXTRA_SUBJECT,subject);
        intent.putExtra(Intent.EXTRA_TEXT,shareBody);
        startActivity(Intent.createChooser(intent,"Share for Helps Others..."));
    }

    private void gotoUrl(String url){
        Uri uri = Uri.parse(url);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }

}