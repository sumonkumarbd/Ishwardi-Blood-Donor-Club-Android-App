package com.sumonkmr.ibdc;

import static com.bumptech.glide.load.resource.drawable.DrawableDecoderCompat.getDrawable;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class SettingFragments extends Fragment {

    TextView change_Email, change_password, changeNumber;
    EditText reNew_pass, oldPass_resetPass, newPass_resetPass,oldMail,newMail,passForCngMail;
    Button resetBtn, cBtn;
    String emailExceptions;
    FirebaseAuth mAuth;
    FirebaseUser user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.setting_fragments, container, false);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        emailExceptions = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";

        change_Email = view.findViewById(R.id.change_Email);
        change_password = view.findViewById(R.id.change_password);
        changeNumber = view.findViewById(R.id.changeNumber);

        final String userEmail = user.getEmail();
        assert userEmail != null;



//        functions
        EmailChange();
        PassWordChange();


        return view;
    }//onCreate VIew


    private void EmailChange() {

       Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.change_email_dialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_background);
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

        oldMail = dialog.findViewById(R.id.oldMail);
        newMail = dialog.findViewById(R.id.newMail);
        passForCngMail = dialog.findViewById(R.id.passForCngMail);

        resetBtn = dialog.findViewById(R.id.resetBtn_email);
        cBtn = dialog.findViewById(R.id.cBtn_email);

        change_Email.setOnClickListener(v -> {
            dialog.show();
        });

        resetBtn.setOnClickListener(v -> {
            if (!oldMail.getText().toString().isEmpty() && oldMail.getText().toString().matches(emailExceptions)) {
                if (!newMail.getText().toString().isEmpty() && newMail.getText().toString().matches(emailExceptions)) {
                    if (!passForCngMail.getText().toString().isEmpty() && passForCngMail.getText().toString().length() >= 6) {
                        AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(oldMail.getText().toString()), passForCngMail.getText().toString());
                        if (Objects.equals(user.getEmail(), oldMail.getText().toString())) {
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            user.updateEmail(newMail.getText().toString()).addOnSuccessListener(unused -> {
                                                dialog.dismiss();
                                                oldMail.setText("");
                                                newMail.setText("");
                                                passForCngMail.setText("");
                                                Toast.makeText(getContext(), "ইমেইল পরিবর্তন সফল হয়েছে!", Toast.LENGTH_SHORT).show();
                                                UpdateEmailInDB();
                                            }).addOnFailureListener(e -> Toast.makeText(getContext(), "ইমেইল পরিবর্তন অসফল, দয়াকরে পুনরায় চেষ্টা করুন!", Toast.LENGTH_SHORT).show());
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "আপনার পাসওয়ার্ড টি ভুল! অনুগ্রহপূর্বক সঠিক পাসওয়ার্ড দিন!!", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(getContext(), "আপনি যে ইমেইল টি দিয়েছেন তা সঠিক নয়, অনুগ্রহ পূর্বক পুনরায় চেষ্টা করুন!", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getContext(), "কমপক্ষে ৬ অক্ষর/নম্বার ব্যাবহার করে পাসওয়ার্ড দিন!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getContext(), "নতুন ইমেইল টি সঠিক ভাবে লিখুন!", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(getContext(), "আপনার ইমেইল টি সঠিক নয়!", Toast.LENGTH_SHORT).show();
            }


        });

        cBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });



    }


    private void PassWordChange() {

       Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.change_password_dialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_background);
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

        reNew_pass = dialog.findViewById(R.id.reNew_pass);
        oldPass_resetPass = dialog.findViewById(R.id.oldPass_resetPass);
        newPass_resetPass = dialog.findViewById(R.id.newPass_resetPass);

        resetBtn = dialog.findViewById(R.id.resetBtn_pass);
        cBtn = dialog.findViewById(R.id.cBtn_pass);

        change_password.setOnClickListener(v -> {
            dialog.show();
        });

        resetBtn.setOnClickListener(v -> {
            if (!oldPass_resetPass.getText().toString().isEmpty() && oldPass_resetPass.getText().toString().length() >= 6) {
                if (!newPass_resetPass.getText().toString().isEmpty() && newPass_resetPass.getText().toString().length() >= 6) {
                    if (!reNew_pass.getText().toString().isEmpty() && reNew_pass.getText().toString().length() >= 6) {
                        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPass_resetPass.getText().toString());
                        if (Objects.equals(newPass_resetPass.getText().toString(), reNew_pass.getText().toString())) {
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            user.updatePassword(newPass_resetPass.getText().toString()).addOnSuccessListener(unused -> {
                                                dialog.dismiss();
                                                oldPass_resetPass.setText("");
                                                newPass_resetPass.setText("");
                                                reNew_pass.setText("");
                                                Toast.makeText(getContext(), "পাসওয়ার্ড পরিবর্তন সফল হয়েছে!", Toast.LENGTH_SHORT).show();
                                            }).addOnFailureListener(e -> Toast.makeText(getContext(), "পাসওয়ার্ড পরিবর্তন অসফল, দয়াকরে পুনরায় চেষ্টা করুন!", Toast.LENGTH_SHORT).show());
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "আপনার পাসওয়ার্ড টি ভুল! অনুগ্রহপূর্বক সঠিক পাসওয়ার্ড দিন!!", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(getContext(), "পাসওয়ার্ড মিল নয়!", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getContext(), "কমপক্ষে ৬ অক্ষর/নম্বার ব্যাবহার করে পাসওয়ার্ড দিন!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getContext(), "কমপক্ষে ৬ অক্ষর/নম্বার ব্যাবহার করে পাসওয়ার্ড দিন!", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(getContext(), "কমপক্ষে ৬ অক্ষর/নম্বার ব্যাবহার করে পাসওয়ার্ড দিন!", Toast.LENGTH_SHORT).show();
            }


        });

        cBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });


    }



    private void UpdateEmailInDB() {
        HashMap<String,Object> values = new HashMap<>();
        values.put("Email",user.getEmail());
        FirebaseDatabase.getInstance().getReference("Donors/"+user.getUid())
                .updateChildren(values)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(getContext(), "আপনার নতুন ইমেইলঃ "+user.getEmail(), Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getContext(), R.string.failed, Toast.LENGTH_SHORT).show();
                    }
                });

    }//addToDatabase
}