package com.sumonkmr.ibdc;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//Programmed by SumonKmr
//On Tue, 02-05-22

public class SplashScreen extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    TextView t1;
    LottieAnimationView animation_view,animation_2;
    SwipeRefreshLayout swipeRefreshLayout;
    NetworkInfo networkInfo;
    ConnectivityManager connectivityManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        setContentView(R.layout.splash_screen);

        swipeRefreshLayout = findViewById(R.id.swipeLay);
        t1 = findViewById(R.id.t1);
        animation_view = findViewById(R.id.animation_view);
        animation_2 = findViewById(R.id.animation_2);

        IsNetwork();

        swipeRefreshLayout.setOnRefreshListener(this::refreshData);

    }


    private void refreshData() {
        // Perform the necessary actions to refresh the data
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        IsNetwork();



        // Hide the refresh indicator
        swipeRefreshLayout.setRefreshing(true);
    }

    private void IsNetwork(){
        if (networkInfo != null && networkInfo.isConnected()){
            new CountDownTimer(1000, 500) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    if(user != null){
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                        SplashScreen.this.finish();
                    }else {
                        startActivity(new Intent(SplashScreen.this,GoogleLogIn.class));
                        SplashScreen.this.finish();
                    }
                }
            }.start();
        }else {
            t1.setText("দুঃখিত ইন্টারনেট সংযোগ নেই !");
            animation_view.setVisibility(View.GONE);
            animation_2.setVisibility(View.VISIBLE);
        } // for check internet Connections
    }

//    private void getSelf() {
//        FirebaseDatabase.getInstance().getReference("Donors/"+user.getUid())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        User users = snapshot.getValue(User.class);
//                        switch (users.getStep()){
//                            case "1": startActivity(new Intent(SplashScreen.this,RegisterIIActivity.class));
//                                break;
//                            case "2": startActivity(new Intent(SplashScreen.this,RegisterIIIActivity.class));
//                                break;
//                            case "Done": startActivity(new Intent(SplashScreen.this,DashBoard.class));
//                                break;
//                        }
//                        SplashScreen.this.finish();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }

}