package com.sumonkmr.ibdc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class DashBoard extends AppCompatActivity implements View.OnClickListener {

    com.google.android.material.navigation.NavigationView nav;
    androidx.appcompat.widget.Toolbar toolbar;
    DuoDrawerToggle toggle;
    DuoDrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

//        toolbar = (Toolbar)findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
////        nav = (NavigationView)findViewById(R.id.nav_menu);
//        drawerLayout = (DuoDrawerLayout) findViewById(R.id.drawer);
//
//        toggle = new DuoDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
//        drawerLayout.setDrawerListener(toggle);
//        toggle.syncState();
//
//
//        View contentView = drawerLayout.getContentView();
//        View menuView = drawerLayout.getMenuView();
//
//        LinearLayout ll_Home = ((View) menuView).findViewById(R.id.ll_Home);
//        LinearLayout ll_Profile = menuView.findViewById(R.id.ll_Profile);
//        LinearLayout ll_Setting = menuView.findViewById(R.id.ll_Setting);
//        LinearLayout ll_Share = menuView.findViewById(R.id.ll_Share);
//        LinearLayout ll_Logout = menuView.findViewById(R.id.ll_Logout);
//        LinearLayout ll_about = menuView.findViewById(R.id.ll_about_us);




//        ll_Home.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(),DispalyRequestsActivity.class));
//                drawerLayout.closeDrawer();
//            }
//        });
//        ll_Profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        ll_Setting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        ll_Share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        ll_about.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        ll_Logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        init();

//        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.division:
//                        Toast.makeText(getApplicationContext(), "This is Division", Toast.LENGTH_SHORT).show();
//                        drawerLayout.closeDrawer(GravityCompat.START);
//                        break;
//
//                    case R.id.district:
//                        Toast.makeText(getApplicationContext(), "This is District", Toast.LENGTH_SHORT).show();
//                        drawerLayout.closeDrawer(GravityCompat.START);
//                        break;
//
//                    case R.id.upazila:
//                        Toast.makeText(getApplicationContext(), "This is Upazila", Toast.LENGTH_SHORT).show();
//                        drawerLayout.closeDrawer(GravityCompat.START);
//                        break;
//
//                    case R.id.union:
//                        Toast.makeText(getApplicationContext(), "This is Union", Toast.LENGTH_SHORT).show();
//                        drawerLayout.closeDrawer(GravityCompat.START);
//                        break;
//
//                    case R.id.blood:
//                        Toast.makeText(getApplicationContext(), "This is Blood", Toast.LENGTH_SHORT).show();
//                        drawerLayout.closeDrawer(GravityCompat.START);
//                        break;
//                }
//                return true;
//            }
//
//            });


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
        LinearLayout ll_Share = menuView.findViewById(R.id.ll_Share);
        LinearLayout ll_Logout = menuView.findViewById(R.id.ll_Logout);


        ll_Home.setOnClickListener(this);
        ll_Profile.setOnClickListener(this);
        ll_Setting.setOnClickListener(this);
        ll_Share.setOnClickListener(this);
        ll_Logout.setOnClickListener(this);


        replace(new HomeFragment());


    }


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
                replace(new SettingFragment(),"Setting");
                break;

            case R.id.ll_Share:
                Toast.makeText(this, "Share...", Toast.LENGTH_SHORT).show();
                break;

            case R.id.ll_Logout:
                Toast.makeText(this, "Logout...", Toast.LENGTH_SHORT).show();
                break;

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