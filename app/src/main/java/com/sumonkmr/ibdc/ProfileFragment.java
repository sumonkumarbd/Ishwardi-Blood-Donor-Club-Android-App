package com.sumonkmr.ibdc;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ProfileFragment extends Fragment {

    Button edit_btn,edit_save_btn;
    ImageView name_Edit,number_Edit,blood_Edit,vil_Edit,upa_Edit,dis_Edit,div_Edit,last_donate_Edit;
    de.hdodenhof.circleimageview.CircleImageView profile_edit_image;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

//        Hooks
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        edit_btn = view.findViewById(R.id.edit_btn);
        edit_save_btn = view.findViewById(R.id.edit_save_btn);
        name_Edit = view.findViewById(R.id.name_Edit);
        number_Edit = view.findViewById(R.id.number_Edit);
        blood_Edit = view.findViewById(R.id.blood_Edit);
        vil_Edit = view.findViewById(R.id.vil_Edit);
        upa_Edit = view.findViewById(R.id.upa_Edit);
        dis_Edit = view.findViewById(R.id.dis_Edit);
        div_Edit = view.findViewById(R.id.div_Edit);
        last_donate_Edit = view.findViewById(R.id.last_donate_Edit);
        profile_edit_image = view.findViewById(R.id.profile_edit_image);

//        View.OnClickListener
        edit_btn.setOnClickListener(v -> {
            edit_btn.setVisibility(View.GONE);
            edit_save_btn.setVisibility(View.VISIBLE);
            name_Edit.setVisibility(View.VISIBLE);
            number_Edit.setVisibility(View.VISIBLE);
            blood_Edit.setVisibility(View.VISIBLE);
            vil_Edit.setVisibility(View.VISIBLE);
            upa_Edit.setVisibility(View.VISIBLE);
            dis_Edit.setVisibility(View.VISIBLE);
            div_Edit.setVisibility(View.VISIBLE);
            last_donate_Edit.setVisibility(View.VISIBLE);
            profile_edit_image.setVisibility(View.VISIBLE);
        });

        edit_save_btn.setOnClickListener(v -> {
            edit_btn.setVisibility(View.VISIBLE);
            edit_save_btn.setVisibility(View.GONE);
            name_Edit.setVisibility(View.GONE);
            number_Edit.setVisibility(View.GONE);
            blood_Edit.setVisibility(View.GONE);
            vil_Edit.setVisibility(View.GONE);
            upa_Edit.setVisibility(View.GONE);
            dis_Edit.setVisibility(View.GONE);
            div_Edit.setVisibility(View.GONE);
            last_donate_Edit.setVisibility(View.GONE);
            profile_edit_image.setVisibility(View.GONE);
        });

        return view;
    }
}