package com.sumonkmr.ibdc;

import static com.bumptech.glide.load.resource.drawable.DrawableDecoderCompat.getDrawable;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SettingFragments extends Fragment {

    TextView change_Email, change_password, changeNumber;
    EditText reNew_pass, oldPass_resetPass, newPass_resetPass;
    Button resetBtn, cBtn;
    String emailExceptions;
    FirebaseAuth mAuth;
    FirebaseUser Currentuser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.setting_fragments, container, false);
        mAuth = FirebaseAuth.getInstance();
        Currentuser = mAuth.getCurrentUser();

        emailExceptions = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";

        change_Email = view.findViewById(R.id.change_Email);
        change_password = view.findViewById(R.id.change_password);
        changeNumber = view.findViewById(R.id.changeNumber);

        change_Email.setVisibility(View.GONE);
        changeNumber.setVisibility(View.GONE);


        DialogSetup();


        return view;
    }//onCreate VIew


    private void DialogSetup() {

        Dialog dialog;
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.change_password_dialog);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_background);
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog


        resetBtn = dialog.findViewById(R.id.resetBtn);
        cBtn = dialog.findViewById(R.id.cBtn);


        change_password.setOnClickListener(v -> {
            dialog.show();
        });

        resetBtn.setOnClickListener(v -> {
            reNew_pass = dialog.findViewById(R.id.reNew_pass);
            oldPass_resetPass = dialog.findViewById(R.id.oldPass_resetPass);
            newPass_resetPass = dialog.findViewById(R.id.newPass_resetPass);
            final String userEmail = Currentuser.getEmail();
            assert userEmail != null;

            if (!oldPass_resetPass.getText().toString().isEmpty() && oldPass_resetPass.getText().toString().length() >= 6) {
                if (!newPass_resetPass.getText().toString().isEmpty() && newPass_resetPass.getText().toString().length() >= 6) {
                    if (!reNew_pass.getText().toString().isEmpty() && reNew_pass.getText().toString().length() >= 6) {
                        AuthCredential credential = EmailAuthProvider.getCredential(Currentuser.getEmail(), oldPass_resetPass.getText().toString());
                        if (Objects.equals(newPass_resetPass.getText().toString(), reNew_pass.getText().toString())) {
                            Currentuser.reauthenticate(credential)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Currentuser.updatePassword(newPass_resetPass.getText().toString()).addOnSuccessListener(unused -> {
                                                dialog.dismiss();
                                                oldPass_resetPass.setText("");
                                                newPass_resetPass.setText("");
                                                reNew_pass.setText("");
                                                Toast.makeText(getContext(), "পাসওয়ার্ড পরিবর্তন সফল হয়েছে!", Toast.LENGTH_SHORT).show();
                                            }).addOnFailureListener(e -> Toast.makeText(getContext(), "পাসওয়ার্ড পরিবর্তন অসফল, দয়াকরে পুনরায় চেষ্টা করুন!", Toast.LENGTH_SHORT).show());
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        oldPass_resetPass.setError("আপনার পাসওয়ার্ড টি ভুল! অনুগ্রহপূর্বক সঠিক পাসওয়ার্ড দিন!!");
                                    });
                        }else {
                            newPass_resetPass.setError("পাসওয়ার্ড মিল নয়!");
                            reNew_pass.setError("পাসওয়ার্ড মিল নয়!");
                        }

                    } else {
                        reNew_pass.setError("কমপক্ষে ৬ অক্ষর/নম্বার ব্যাবহার করে পাসওয়ার্ড দিন!");
                    }

                } else {
                    newPass_resetPass.setError("কমপক্ষে ৬ অক্ষর/নম্বার ব্যাবহার করে পাসওয়ার্ড দিন!");
                }

            } else {
                oldPass_resetPass.setError("কমপক্ষে ৬ অক্ষর/নম্বার ব্যাবহার করে পাসওয়ার্ড দিন!");
            }
        });

        cBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });


    }


}