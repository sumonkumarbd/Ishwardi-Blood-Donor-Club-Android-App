package com.sumonkmr.ibdc;

import static com.bumptech.glide.load.resource.drawable.DrawableDecoderCompat.getDrawable;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.sumonkmr.ibdc.model.User;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SettingFragments extends Fragment {

    TextView change_Email, change_password, changeNumber;
    EditText reNew_pass, oldPass_resetPass, newPass_resetPass, oldMail, newMail, passForCngMail, oldNum, newNum, pass_Num;
    Button resetBtn, cBtn, resetBtn_Num_done, resetBtn_Num, cBtn_Num;
    String emailExceptions;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String id;
    boolean isSubmit = false;



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
        NumberChange();


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


    }//email Change


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


    }//password Change

    private void NumberChange() {

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.number_change_dialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_background);
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

        oldNum = dialog.findViewById(R.id.oldNum);
        newNum = dialog.findViewById(R.id.newNum);
        pass_Num = dialog.findViewById(R.id.passForCngNum);

        resetBtn_Num = dialog.findViewById(R.id.resetBtn_Num);
        cBtn_Num = dialog.findViewById(R.id.cBtn_Num);
        resetBtn_Num_done = dialog.findViewById(R.id.resetBtn_Num_done);
        resetBtn.setVisibility(View.GONE);

        pass_Num.setHint(R.string.enter_Otp);

        changeNumber.setOnClickListener(v -> {
            dialog.show();
        });

        resetBtn_Num.setOnClickListener(v -> {
            initOpt();
        });


        resetBtn_Num_done.setOnClickListener(view -> {
            updatePhoneNum(dialog);
        });


        cBtn_Num.setOnClickListener(v -> {
            oldNum.setText("");
            newNum.setText("");
            pass_Num.setText("");
            resetBtn_Num.setVisibility(View.VISIBLE);
            resetBtn_Num_done.setVisibility(View.GONE);
            pass_Num.setHint(R.string.enter_Otp);
            dialog.dismiss();
        });


    }//Number Change


    private void UpdateEmailInDB() {
        HashMap<String, Object> values = new HashMap<>();
        values.put("Email", user.getEmail());
        FirebaseDatabase.getInstance().getReference("Donors/" + user.getUid())
                .updateChildren(values)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "আপনার নতুন ইমেইলঃ " + user.getEmail(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), R.string.failed, Toast.LENGTH_SHORT).show();
                    }
                });

    }//addToDatabase


    //need to implement blow 3 function for Change number functions...
    private void UpdatePhnInDB() {
        HashMap<String, Object> values = new HashMap<>();
        values.put("Mobile", newNum.getText().toString());
        FirebaseDatabase.getInstance().getReference("Donors/" + user.getUid())
                .updateChildren(values)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "আপনার নতুন নাম্বারঃ " + user.getPhoneNumber(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), R.string.failed, Toast.LENGTH_SHORT).show();
                    }
                });

    }//addToDatabase

    private void initOpt() {

        if (oldNum.getText().toString().isEmpty() || oldNum.getText().toString().length() != 11) {
            oldNum.setError("আপনার বর্তমান নাম্বারটি সঠিক ভাবে লিখুন!");
            resetBtn_Num.setVisibility(View.VISIBLE);
            resetBtn_Num_done.setVisibility(View.GONE);
        } else if (newNum.getText().toString().isEmpty() || newNum.getText().toString().length() != 11) {
            newNum.setError("আপনার নতুন নাম্বারটি সঠিক ভাবে লিখুন!");
            resetBtn_Num.setVisibility(View.VISIBLE);
            resetBtn_Num_done.setVisibility(View.GONE);

        } else {
            resetBtn_Num.setVisibility(View.GONE);
            resetBtn_Num_done.setVisibility(View.VISIBLE);
            pass_Num.setHint(R.string.verifying);
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber("+88" + oldNum.getText().toString())       // Phone number to verify
                            .setTimeout(30L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(requireActivity())// Activity (for callback binding)
                            .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                        pass_Num.setHint("অসমাপ্ত!");
                                    } else if (e instanceof FirebaseTooManyRequestsException) {
                                        pass_Num.setHint("কিছুক্ষন পর আবার চেষ্টা করুন...");
                                        resetBtn.setEnabled(true);
                                    }
                                }

                                @Override
                                public void onCodeSent(@NonNull String verificationId,
                                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {

                                    resetBtn_Num.setText(R.string.submit);
                                    id = verificationId;
                                    isSubmit = true;

                                }
                            })          // OnVerificationStateChangedCallbacks
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);

//            loadingAim.setVisibility(View.VISIBLE);

        }
    }//init otp

    private void updatePhoneNum(Dialog d) {
        User u = new User();
        if (pass_Num.getText().toString().length() != 6) {
            Toast.makeText(getContext(), "পাসওয়ার্ড সঠিক নয়!!", Toast.LENGTH_LONG).show();
        } else if (pass_Num.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "পাসওয়ার্ড দিন!!", Toast.LENGTH_LONG).show();
        } else {
            if (Objects.equals(u.getMobile(), oldNum.toString())) {
                if (!TextUtils.isEmpty(newNum.getText()) && newNum.getText().toString().length() == 11) {
                    AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), pass_Num.getText().toString());
                    user.reauthenticate(credential)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    d.dismiss();
                                    UpdatePhnInDB();
                                    oldNum.setText("");
                                    newNum.setText("");
                                    pass_Num.setText("");
                                    Toast.makeText(getContext(), "নাম্বার পরিবর্তন সফল হয়েছে!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "নাম্বার পরিবর্তন অসফল, দয়াকরে পুনরায় চেষ্টা করুন!", Toast.LENGTH_SHORT).show();
                                }

                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "আপনার পাসওয়ার্ড টি ভুল! অনুগ্রহপূর্বক সঠিক পাসওয়ার্ড দিন!!", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    newNum.setError("নাম্বার টি সঠিক নয়!");
                }
            } else {
                oldNum.setError("নাম্বার টি সঠিক নয়!");
            }
        }
    }//updatePhoneNum


}