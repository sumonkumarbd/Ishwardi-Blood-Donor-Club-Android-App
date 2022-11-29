package com.sumonkmr.ibdc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//Programmed by SumonKmr
//On Tue, 02-05-22

public class SplashScreen extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
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