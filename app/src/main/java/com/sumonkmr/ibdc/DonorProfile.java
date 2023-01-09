package com.sumonkmr.ibdc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class DonorProfile extends AppCompatActivity {

    TextInputEditText commentField;
    ImageView comment_done;
    DatabaseReference userRef,commentRef;
    String postKey;
    String userId;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_profile);
        Init();
    }

    private void Init() {
        commentField = findViewById(R.id.commentField);
        comment_done = findViewById(R.id.comment_done);
        postKey = getIntent().getStringExtra("postKey");
        userRef = FirebaseDatabase.getInstance().getReference().child("Donors");
        commentRef = FirebaseDatabase.getInstance().getReference().child("Donors").child(postKey).child("comments");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        userId = currentUser.getUid();

        comment_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String fName = Objects.requireNonNull(snapshot.child("FName").getValue()).toString();
                            String lName = Objects.requireNonNull(snapshot.child("LName").getValue()).toString();
                            String bloodImg_url = Objects.requireNonNull(snapshot.child("bloodImg_url").getValue()).toString();
                            String userName = fName+" "+lName;
                            ProcessComment(userName,bloodImg_url);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

    private void ProcessComment(String userName, String bloodImg_url) {
        String userComment = Objects.requireNonNull(commentField.getText()).toString();
        String parentKey = userId+""+new Random().nextInt(1000);
        Calendar dateValue = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
        String cDate = dateFormat.format(dateValue.getTime());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String cTime = timeFormat.format(dateValue.getTime());

        HashMap cmnt = new HashMap();
        cmnt.put("uid",userId);
        cmnt.put("userName",userName);
        cmnt.put("userImg",bloodImg_url);
        cmnt.put("usermsg",userComment);
        cmnt.put("date",cDate);
        cmnt.put("time",cTime);

        commentRef.child(parentKey).updateChildren(cmnt).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    Toast.makeText(DonorProfile.this, "Comment Add Successfully!!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(DonorProfile.this, "Comment Add Unsuccessfully!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        commentField.setText("");
    }
}