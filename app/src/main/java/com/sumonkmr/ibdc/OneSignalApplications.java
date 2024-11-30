package com.sumonkmr.ibdc;

import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

public class OneSignalApplications extends Application {
    private static String ONESIGNAL_APP_ID;

    @Override
    public void onCreate() {
        super.onCreate();
        Id();

    }

    private void Id(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("admob");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ONESIGNAL_APP_ID = snapshot.child("ONESIGNAL_APP_ID").getValue(String.class);
                // Enable verbose OneSignal logging to debug issues if needed.
                OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);


                // OneSignal Initialization
                OneSignal.initWithContext(OneSignalApplications.this);
                OneSignal.setAppId(ONESIGNAL_APP_ID);
                Log.d("OneSignal DatabaseError", "ONESIGNAL_APP_ID "+ ONESIGNAL_APP_ID);


                // promptForPushNotifications will show the native Android notification permission prompt.
                // We recommend removing the following code and instead using an In-App Message to prompt for notification permission (See step 7)
                OneSignal.promptForPushNotifications();

            }//onDataChange

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("OneSignal DatabaseError", "OneSignal DatabaseError: Failed to read value.");

            }
        });

    }//Id

}//OneSignalApplications
