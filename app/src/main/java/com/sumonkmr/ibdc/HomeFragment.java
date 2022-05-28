package com.sumonkmr.ibdc;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Releasable;


public class HomeFragment extends Fragment {

    RelativeLayout find_donors, about_us;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        find_donors = view.findViewById(R.id.find_donors);
        about_us = view.findViewById(R.id.about_us);

        find_donors.setOnClickListener(v -> {
            startActivity(new Intent(getContext(),DisplayDonorsActivity.class));
        });

        about_us.setOnClickListener(v -> {
            startActivity(new Intent(getContext(),AboutUs.class));
        });

        return view;

    }
}