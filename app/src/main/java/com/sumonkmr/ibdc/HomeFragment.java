package com.sumonkmr.ibdc;

import static androidx.browser.customtabs.CustomTabsClient.getPackageName;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Objects;


public class HomeFragment extends Fragment {

    RelativeLayout ins,benefit,be_donor,find_donors, about_us,rate_us;
    TextView notice_board;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        notice_board = view.findViewById(R.id.notice_board);
        ins = view.findViewById(R.id.ins);
        benefit = view.findViewById(R.id.benefit);
        be_donor = view.findViewById(R.id.be_donor);
        find_donors = view.findViewById(R.id.find_donors);
        about_us = view.findViewById(R.id.about_us);
        rate_us = view.findViewById(R.id.rate_us);

        ins.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ProfitOfBloodDonation.class));
        });

        benefit.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ProfitOfBloodDonation.class));
        });

        be_donor.setOnClickListener(v -> {
//            startActivity(new Intent(getContext(),.class));
        });

        find_donors.setOnClickListener(v -> {
            startActivity(new Intent(getContext(),DisplayDonorsActivity.class));
        });

        about_us.setOnClickListener(v -> {
            startActivity(new Intent(getContext(),AboutUs.class));
        });

        rate_us.setOnClickListener(v -> {
            rateUsOnGooglePlay();
        });


        return view;

    }


    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    private void rateUsOnGooglePlay(){

            final String appPackageName = requireContext().getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
}