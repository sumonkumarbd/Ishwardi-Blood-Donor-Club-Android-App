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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class SettingFragments extends Fragment {

    TextView change_Email,change_password,changeNumber;
    EditText resetPass;
    Button resetBtn,cBtn;
    String emailExceptions;
    FirebaseAuth mAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.setting_fragments, container, false);
        mAuth = FirebaseAuth.getInstance();
        emailExceptions = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";

        change_Email = view.findViewById(R.id.change_Email);
        change_password = view.findViewById(R.id.change_password);
        changeNumber = view.findViewById(R.id.changeNumber);

        change_Email.setVisibility(View.GONE);
        changeNumber.setVisibility(View.GONE);


        DialogSetup();



        return view;
    }//onCreate VIew


    private void DialogSetup(){

        Dialog dialog;
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.reset_pass_dialog);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawableResource(getDrawable(R.drawable.custom_dialog_background));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

        resetPass = dialog.findViewById(R.id.resetPass);
        resetBtn = dialog.findViewById(R.id.resetBtn);
        cBtn = dialog.findViewById(R.id.cBtn);

        change_password.setOnClickListener(v-> {
            dialog.show();
        });

        resetBtn.setOnClickListener(v -> {
            if (!resetPass.getText().toString().isEmpty() && resetPass.getText().toString().matches(emailExceptions)) {
                mAuth.sendPasswordResetEmail(resetPass.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(), "সম্পূর্ণ হয়েছে!,অনুগ্রহপূর্বক ইমেইল ইনবক্স/স্পাম মেইল বক্স চেক করুন!!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "ইমেইল টি ডাটাবেসে পাওয়া যাইনি!!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }else {
                Toast.makeText(getContext(), "ইমেইল টি সঠিক নয়!!", Toast.LENGTH_SHORT).show();
            }
        });

        cBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });



    }

    private int getDrawable(int custom_dialog_background) {
        return R.drawable.custom_dialog_background;
    }


}